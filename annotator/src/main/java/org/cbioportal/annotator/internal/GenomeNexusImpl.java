/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cbioportal.annotator.internal;

import org.cbioportal.models.TranscriptConsequence;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.GenomeNexusAnnotationResponse;
import org.cbioportal.models.MutationRecord;
import org.cbioportal.models.GenomeNexusIsoformOverridesResponse;
import org.cbioportal.models.GeneXref;
import org.cbioportal.annotator.Annotator;

import java.util.*;
import java.util.regex.*;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;

/**
 *
 * @author Zachary Heins
 *
 * Use GenomeNexus to annotate mutation records
 *
 */

@Configuration
public class GenomeNexusImpl implements Annotator {

    @Value("${genomenexus.hgvs}")
    private String hgvsServiceUrl;
    @Value("${genomenexus.isoform_query_parameter}")
    private String isoformQueryParameter;
    @Value("${genomenexus.hotspot_parameter}")
    private String hotspotParameter;
    @Value("${genomenexus.xrefs}")
    private String geneXrefsServiceUrl;

    private MutationRecord mRecord;
    private GenomeNexusAnnotationResponse gnResponse;
    private TranscriptConsequence canonicalTranscript;

    private List<GenomeNexusIsoformOverridesResponse> overrides = new ArrayList<>();

    private final Logger log = Logger.getLogger(GenomeNexusImpl.class);

    private String aa3to1[][] = {
        {"Ala", "A"}, {"Arg", "R"}, {"Asn", "N"}, {"Asp", "D"}, {"Asx", "B"}, {"Cys", "C"},
        {"Glu", "E"}, {"Gln", "Q"}, {"Glx", "Z"}, {"Gly", "G"}, {"His", "H"}, {"Ile", "I"},
        {"Leu", "L"}, {"Lys", "K"}, {"Met", "M"}, {"Phe", "F"}, {"Pro", "P"}, {"Ser", "S"},
        {"Thr", "T"}, {"Trp", "W"}, {"Tyr", "Y"}, {"Val", "V"}, {"Xxx", "X"}, {"Ter", "*"}
    };
    private Set<String> spliceSiteVariants = new HashSet<String>(Arrays.asList(
            "splice_acceptor_variant", "splice_donor_variant", "splice_region_variant"));

    private static Map<String, String> variantMap = initVariantMap();
    private static List<String> hgvspNullClassifications = initNullClassifications();
    private static Map<String, Integer> effectPriority = initEffectPriority();

    private Pattern cDnaExtractor = Pattern.compile(".*[cn].-?\\*?(\\d+).*");
    private String variantType;

    @Bean
    public GenomeNexusImpl annotator() {
        return this;
    }

    @Bean
    public GenomeNexusImpl annotator(String hgvsServiceUrl) {
        this.hgvsServiceUrl = hgvsServiceUrl;
        return this;
    }

    private boolean annotationNeeded(MutationRecord record) {
        Map<String, String> additionalProperties = record.getAdditionalProperties();
        if (!additionalProperties.containsKey("HGVSp_Short")) {
            return true;
        }
        return additionalProperties.get("HGVSp_Short").isEmpty() && !hgvspNullClassifications.contains(record.getVARIANT_CLASSIFICATION());
    }

    @Override
    public AnnotatedRecord annotateRecord(MutationRecord record, boolean replace, String isoformOverridesSource, boolean reannotate) {
        this.mRecord = record;

        //check if record already is annotated
        if(!reannotate && !annotationNeeded(record)) {
            return new AnnotatedRecord(mRecord);
        }

        // make the rest call to genome nexus
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();        
        ResponseEntity<GenomeNexusAnnotationResponse[]> responseEntity = restTemplate.exchange(getUrlForRecord(record, isoformOverridesSource), HttpMethod.GET, requestEntity, GenomeNexusAnnotationResponse[].class);
        gnResponse = responseEntity.getBody()[0];

        // get the canonical trnascript
        canonicalTranscript = getCanonicalTranscript(gnResponse);

        // annotate the record
        AnnotatedRecord annotatedRecord= new AnnotatedRecord(resolveHugoSymbol(replace),
                resolveEntrezGeneId(replace),
                mRecord.getCENTER(),
                resolveAssemblyName(),
                resolveChromosome(),
                resolveStart(),
                resolveEnd(),
                resolveStrandSign(),
                resolveVariantClassification(),
                resolveVariantType(),
                resolveReferenceAllele(),
                resolveReferenceAllele(),
                resolveTumorSeqAllele(),
                mRecord.getDBSNP_RS(),
                mRecord.getDBSNP_VAL_STATUS(),
                mRecord.getTUMOR_SAMPLE_BARCODE(),
                mRecord.getMATCHED_NORM_SAMPLE_BARCODE(),
                mRecord.getMATCH_NORM_SEQ_ALLELE1(),
                mRecord.getMATCH_NORM_SEQ_ALLELE2(),
                mRecord.getTUMOR_VALIDATION_ALLELE1(),
                mRecord.getTUMOR_VALIDATION_ALLELE2(),
                mRecord.getMATCH_NORM_VALIDATION_ALLELE1(),
                mRecord.getMATCH_NORM_VALIDATION_ALLELE2(),
                mRecord.getVERIFICATION_STATUS(),
                mRecord.getVALIDATION_STATUS(),
                mRecord.getMUTATION_STATUS(),
                mRecord.getSEQUENCING_PHASE(),
                mRecord.getSEQUENCE_SOURCE(),
                mRecord.getVALIDATION_METHOD(),
                mRecord.getSCORE(),
                mRecord.getBAM_FILE(),
                mRecord.getSEQUENCER(),
                mRecord.getTUMOR_SAMPLE_UUID(),
                mRecord.getMATCHED_NORM_SAMPLE_UUID(),
                mRecord.getT_REF_COUNT(),
                mRecord.getT_ALT_COUNT(),
                mRecord.getN_REF_COUNT(),
                mRecord.getN_ALT_COUNT(),
                resolveHgvsc(),
                resolveHgvsp(),
                resolveHgvspShort(),
                resolveTranscriptId(),
                resolveRefSeq(),
                resolveProteinPosStart(),
                resolveProteinPosEnd(),
                resolveCodonChange(),
                resolveHotspot(),
                resolveConsequence(),
                mRecord.getAdditionalProperties());
        return annotatedRecord;
    }

    @Override
    public MutationRecord createRecord(Map<String, String> mafLine) throws Exception {
        MutationRecord record = new MutationRecord();
        for (String header : record.getHeader()) {
            if(mafLine.keySet().contains(header)) {
                record.getClass().getMethod("set" + header.toUpperCase(), String.class).invoke(record, mafLine.remove(header));
            }
        }
        record.setAdditionalProperties(mafLine);
        return record;
    }

     @Override
    public boolean isHgvspNullClassifications(String variantClassification) {
        return hgvspNullClassifications.contains(variantClassification);
    }

    @Override
    public String getUrlForRecord(MutationRecord record, String isoformOverridesSource) {
        String hgvsNotation = convertToHgvs(record);
        return hgvsServiceUrl + hgvsNotation + "?" + isoformQueryParameter + "=" + isoformOverridesSource + "&" +  hotspotParameter + "=summary";
    }

    private String resolveHugoSymbol(boolean replace) {
        if (replace && canonicalTranscript != null && canonicalTranscript.getGeneSymbol() != null && !canonicalTranscript.getGeneSymbol().trim().isEmpty()) {
            return canonicalTranscript.getGeneSymbol();
        }

        return mRecord.getHUGO_SYMBOL();
    }
    private String resolveChromosome() {
        if (gnResponse.getSeqRegionName() != null) {
            return gnResponse.getSeqRegionName();
        }
        return mRecord.getCHROMOSOME();
    }

    private String resolveAssemblyName() {
        return (gnResponse.getAssemblyName() == null) ? mRecord.getNCBI_BUILD() : gnResponse.getAssemblyName();
    }

    private String resolveStart() {
        try {
            if (gnResponse.getStart() != null) {
                return String.valueOf(Math.min(gnResponse.getStart(), gnResponse.getEnd()));
            }
            else {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            return mRecord.getSTART_POSITION();
        }
    }

    private String resolveEnd() {
        try {
            if(gnResponse.getEnd() != null) {
                return String.valueOf(Math.max(gnResponse.getStart(), gnResponse.getEnd()));
            }
            else {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            return mRecord.getEND_POSITION();
        }
    }

    private String resolveStrandSign() {
        String strand = String.valueOf(gnResponse.getStrand());

        if (strand != null && !strand.equals("+") && !strand.equals("-")) {
            try {
                int i = Integer.parseInt(strand);
                if (i < 0) {
                    strand = "-";
                }
                else {
                    strand = "+";
                }
            }
            catch (Exception e) {}
        }
        else {
            strand = mRecord.getSTRAND();
        }

        return strand;
    }

    private String resolveVariantClassification() {
        String variantClassification = null;
        String[] alleles = null;
        if (gnResponse.getAlleleString() != null) {
            alleles = gnResponse.getAlleleString().split("/", -1);
        }
        if(alleles != null && alleles.length == 2) {
            variantType = getVariantType(alleles[0], alleles[1]);
        }
        if (canonicalTranscript != null) {
            variantClassification = getVariantClassificationFromMap(pickHighestPriorityConsequence(canonicalTranscript.getConsequenceTerms()));
        }
        else {
            if (gnResponse.getMostSevereConsequence() != null) {
                variantClassification = getVariantClassificationFromMap(gnResponse.getMostSevereConsequence());
            }
        }
        return variantClassification != null ? variantClassification : mRecord.getVARIANT_CLASSIFICATION();
    }

    private String resolveVariantType() {
        if (variantType != null) {
            return variantType;
        }
        String[] alleles = null;
        if (gnResponse.getAlleleString() != null) {
            alleles = gnResponse.getAlleleString().split("/", -1);
        }
        if (alleles != null) {
            if (alleles.length == 2) {
                variantType = getVariantType(alleles[0], alleles[1]);
                return variantType != null ? variantType : "";
            }
        }
        return "";
    }

    private String resolveReferenceAllele() {
        if (gnResponse.getAlleleString() != null)
        {
            return gnResponse.getAlleleString().split("/", -1)[0];
        }
        return mRecord.getREFERENCE_ALLELE();
    }

    private String resolveTumorSeqAllele() {
        if (gnResponse.getAlleleString() != null)
        {
            return gnResponse.getAlleleString().split("/", -1)[1];
        }
        return getTumorSeqAllele(mRecord);
    }

    private String resolveHgvsc() {
        if (canonicalTranscript != null) {
            return canonicalTranscript.getHgvsc() != null ? canonicalTranscript.getHgvsc() : "" ;
        }
        return "";
    }

    private String resolveHgvsp() {
        if (canonicalTranscript != null) {
            String hgvsp = canonicalTranscript.getHgvsp();
            if (hgvsp != null) {
                return getProcessedHgvsp(hgvsp);
            }
        }
        return "";
    }

    private String resolveHgvspShort() {
        String hgvsp = "";
        if(canonicalTranscript != null) {
            if (canonicalTranscript.getHgvsp() != null) {
                hgvsp = getProcessedHgvsp(canonicalTranscript.getHgvsp());
                for (int i = 0; i < 24; i++) {
                    if (hgvsp.contains(aa3to1[i][0])) {
                        hgvsp = hgvsp.replaceAll(aa3to1[i][0], aa3to1[i][1]);
                    }
                }
            }
            else if (canonicalTranscript.getHgvsc() != null && spliceSiteVariants.contains(canonicalTranscript.getConsequenceTerms().get(0))) {
                Integer cPos = 0;
                Integer pPos = 0;
                Matcher m = cDnaExtractor.matcher(canonicalTranscript.getHgvsc());
                if (m.matches()) {
                    cPos = Integer.parseInt(m.group(1));
                    cPos = cPos < 1 ? 1 : cPos;
                    pPos = (cPos + cPos % 3) / 3;
                    hgvsp = "p.X" + String.valueOf(pPos) + "_splice";
                }
            }
            else {
                // try to salvage using protein_start, amino_acids, and consequence_terms
                hgvsp = resolveHgvspShortFromAAs();
            }
        }
        return hgvsp;
    }

    private String resolveHgvspShortFromAAs() {
        String hgvsp = "";
        try {
            String[] aaParts = canonicalTranscript.getAminoAcids().split("/");
            if (canonicalTranscript.getConsequenceTerms() != null && canonicalTranscript.getConsequenceTerms().get(0).equals("inframe_insertion")) {
                hgvsp = aaParts[1].substring(0,1) + canonicalTranscript.getProteinStart() + "_" + aaParts[1].substring(1, 2) + "ins" +
                        canonicalTranscript.getProteinEnd() + aaParts[1].substring(2);
            }
            else if (canonicalTranscript.getConsequenceTerms() != null && canonicalTranscript.getConsequenceTerms().get(0).equals("inframe_deletion")) {
                hgvsp = aaParts[0] + "del";
            }
            else {
                hgvsp = aaParts[0] + canonicalTranscript.getProteinStart();
                if (canonicalTranscript.getConsequenceTerms() != null && canonicalTranscript.getConsequenceTerms().get(0).equals("frameshift_variant")) {
                    hgvsp += "fs";
                }
                else {
                    hgvsp += aaParts[1];
                }
            }
        }
        catch (NullPointerException e) {
            log.debug("Failed to salvage HGVSp_Short from protein start, amino acids, and consequence terms");
        }

        return hgvsp;
    }

    private String resolveTranscriptId() {
        String transcriptId = "";
        if(canonicalTranscript != null) {
            transcriptId = canonicalTranscript.getTranscriptId();
        }

        return transcriptId != null ? transcriptId : "";
    }

    private String resolveRefSeq() {
        String refSeq = "";
        if(canonicalTranscript != null) {
            if (canonicalTranscript.getRefseqTranscriptIds() != null) {
                List<String> refseqTranscriptIds = canonicalTranscript.getRefseqTranscriptIds();
                if(refseqTranscriptIds.size() > 0) {
                    refSeq = refseqTranscriptIds.get(0);
                }
            }
        }

        return refSeq != null ? refSeq : "";
    }

    private String resolveProteinPosStart() {
        String proteinStart = "";
        if(canonicalTranscript != null) {
            proteinStart = canonicalTranscript.getProteinStart();
        }

        return proteinStart != null ? proteinStart : "";
    }

    private String resolveProteinPosEnd() {
        String proteinEnd = "";
        if(canonicalTranscript != null) {
            proteinEnd = canonicalTranscript.getProteinEnd();
        }

        return proteinEnd != null ? proteinEnd : "";
    }

    private String resolveCodonChange() {
        String codonChange = "";
        if(canonicalTranscript != null) {
            codonChange = canonicalTranscript.getCodons();
        }

        return codonChange != null ? codonChange : "";
    }

    private String resolveHotspot() {
        String hotspot = "0";
        if (canonicalTranscript != null) {
            if (canonicalTranscript.getIsHotspot() != null) {
                hotspot = canonicalTranscript.getIsHotspot().equals("true") ? "1" : "0";
            }
        }
        return hotspot;
    }

    private String getProcessedHgvsp(String hgvsp) {
        int iHgvsp = hgvsp.indexOf(":");
        if (hgvsp.contains("(p.%3D)")) {
            return "p.=";
        }
        else {
            return hgvsp.substring(iHgvsp+1);
        }
    }

    private String getVariantType(String refAllele, String varAllele) {
        int refLength = refAllele.length();
        int varLength = varAllele.length();
        refLength = refAllele.equals("-") ? 0 : refLength;
        varLength = varAllele.equals("-") ? 0 : varLength;

        if (refLength == varLength) {
            if (refLength - 1 < 0) {
                    log.info("Check " + mRecord.getTUMOR_SAMPLE_BARCODE() + " " + mRecord.getHUGO_SYMBOL());
                    return "";
            }
            String npType[] = {"SNP", "DNP", "TNP"};
            return (refLength < 3 ? npType[refLength - 1] : "ONP");
        } else {
            if (refLength < varLength) {
                return "INS";
            } else {
                return "DEL";
            }
        }
    }

    private String resolveConsequence() {
        if (canonicalTranscript == null) {
            return "";
        }
        List<String> consequenceTerms = canonicalTranscript.getConsequenceTerms();
        if (consequenceTerms != null && consequenceTerms.size() > 0) {
            return StringUtils.join(consequenceTerms, ",");
        }
        return "";
    }
    
    private String resolveEntrezGeneId(boolean replace) {
        if (!replace || canonicalTranscript == null || canonicalTranscript.getGeneId() == null || canonicalTranscript.getGeneId().trim().isEmpty()) {
            return mRecord.getENTREZ_GENE_ID();
        }
        // make the rest call to ensembl server for gene external refs
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();        
        ResponseEntity<GeneXref[]> responseEntity = restTemplate.exchange(geneXrefsServiceUrl + canonicalTranscript.getGeneId(), HttpMethod.GET, requestEntity, GeneXref[].class);
        
        String entrezGeneId = null;
        for (GeneXref xref : responseEntity.getBody()) {
            if (xref.getDbname().equals("EntrezGene")) {
                entrezGeneId = xref.getPrimaryId();
                break;
            }
        }
        return entrezGeneId != null ? entrezGeneId : mRecord.getENTREZ_GENE_ID();
    }

    private String convertToHgvs(MutationRecord record)
    {
        String chr = record.getCHROMOSOME();
        String start = record.getSTART_POSITION();
        String end = record.getEND_POSITION();
        String ref = record.getREFERENCE_ALLELE();
        String var = getTumorSeqAllele(record);

        String prefix = "";
        if(ref.equals(var)) {
            log.warn("Warning: Reference allele extracted from " + chr + ":" + start + "-" + end + " matches alt allele. Sample: " + record.getTUMOR_SAMPLE_BARCODE());
        }
        else {
            prefix = longestCommonPrefix(ref, var);
        }

        // Remove common prefix and ajust variant position accordingly
        if (prefix.length() > 0){
            ref = ref.substring(prefix.length());
            var = var.substring(prefix.length());

            int nStart = Integer.valueOf(start);
            int nEnd = Integer.valueOf(end);

            nStart += prefix.length();
            if (ref.length() == 0) {
                nStart -= 1;
            }

            record.setSTART_POSITION(Integer.toString(nStart));
            record.setEND_POSITION(Integer.toString(nEnd));
            start = Integer.toString(nStart);

            record.setREFERENCE_ALLELE(ref);
            record.setTUMOR_SEQ_ALLELE1(ref);
            record.setTUMOR_SEQ_ALLELE2(var);
        }

        String hgvs;
        /*
         Process Insertion
         Example insertion: 17 36002277 36002278 - A
         Example output: 17:g.36002277_36002278insA
         */
        if(ref.equals("-") || ref.length() == 0){
            try {
                hgvs = chr+":g."+start+"_"+String.valueOf(Integer.parseInt(start) + 1)+"ins"+var;
            }
            catch (NumberFormatException e) {
                return "";
            }
        }
        /*
         Process Deletion
         Example deletion: 1 206811015 206811016  AC -
         Example output:   1:g.206811015_206811016delAC
         */
        else if(var.equals("-") || var.length() == 0){
            hgvs = chr+":g."+start+"_"+end+"del"+ref;
        }
        /*
         Process ONP
         Example SNP   : 2 216809708 216809709 CA T
         Example output: 2:g.216809708_216809709delCAinsT
         */
        else if (ref.length() > 1) {
            hgvs = chr + ":g."+start+"_"+end+"del"+ref+"ins"+var;
        }

        /*
         Process SNV
         Example SNP   : 2 216809708 216809708 C T
         Example output: 2:g.216809708C>T
         */
        else{
            hgvs = chr+":g."+start+ref+">"+var;
        }
        return hgvs;
    }

    private String longestCommonPrefix(String str1, String str2) {
        for (int prefixLen = 0; prefixLen < str1.length(); prefixLen++) {
            char c = str1.charAt(prefixLen);
            if ( prefixLen >= str2.length() || str2.charAt(prefixLen) != c ) {
                // Mismatch found
                return str2.substring(0, prefixLen);
            }
        }
        return str1;
    }

    private HttpEntity getRequestEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<Object>(headers);
    }

    private TranscriptConsequence getCanonicalTranscript(GenomeNexusAnnotationResponse gnResponse) {
        List<TranscriptConsequence> transcripts = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        for(TranscriptConsequence transcript : gnResponse.getTranscriptConsequences()) {
            if(transcript.getTranscriptId() != null) {
                if (transcript.getCanonical().equals("1")) {
                    transcripts.add(transcript);
                }
            }
        }

        // only one transcript marked as canonical
        if (transcripts.size() == 1) {
            return transcripts.iterator().next();
        }
        else if (transcripts.size() > 1) {
            return transcriptWithMostSevereConsequence(transcripts, gnResponse.getMostSevereConsequence());
        }
        // no transcript marked as canonical (list.size() == 0), use most sever consequence to decide which one to pick among all available
        else {
            return transcriptWithMostSevereConsequence(gnResponse.getTranscriptConsequences(), gnResponse.getMostSevereConsequence());
        }
    }

    private TranscriptConsequence transcriptWithMostSevereConsequence(List<TranscriptConsequence> transcripts, String mostSevereConsequence) {
        Integer highestPriority = Integer.MAX_VALUE;
        TranscriptConsequence highestPriorityTranscript = null;
        for (TranscriptConsequence transcript : transcripts) {
            List<String> consequenceTerms = transcript.getConsequenceTerms();
            for (String consequenceTerm : consequenceTerms) {
                if (effectPriority.getOrDefault(consequenceTerm.toLowerCase(), Integer.MAX_VALUE) < highestPriority) {
                    highestPriorityTranscript = transcript;
                    highestPriority = effectPriority.getOrDefault(consequenceTerm.toLowerCase(), Integer.MAX_VALUE);
                }
                if (consequenceTerm.trim().equals(mostSevereConsequence)) {
                    return transcript;
                }
            }
        }

        // no match, pick one with the highest priority
        if (highestPriorityTranscript != null) {
            return highestPriorityTranscript;
        }
        
        // if for whatever reason that is null, just return the first one.
        if (transcripts.size() > 0) {
            return transcripts.get(0);
        }

        return null;
    }

    private String getVariantClassificationFromMap(String variant) {
        boolean inframe = Math.abs(mRecord.getREFERENCE_ALLELE().length() - getTumorSeqAllele(mRecord).length()) % 3 == 0;
        variant = variant.toLowerCase();
        String resolvedVariantType = resolveVariantType();
        if ((variant.equals("frameshift_variant") || (variant.equals("protein_altering_variant") || variant.equals("coding_sequence_variant")) && !inframe)) {
            if (resolvedVariantType.equals("DEL")) {
                return "Frame_Shift_Del";
            }
            else if (resolvedVariantType.equals("INS")) {
                return "Frame_Shift_Ins";
            }
        }
        else if ((variant.equals("protein_altering_variant") || variant.equals("coding_sequence_variant")) && inframe) {
            if (resolvedVariantType.equals("DEL")) {
                return "In_Frame_Del";
            }
            else if (resolvedVariantType.equals("INS")) {
                return "In_Frame_Ins";
            }
        }
        return variantMap.getOrDefault(variant, "Targeted_Region");
    }

    public String getHgvsServiceUrl() {
        return hgvsServiceUrl;
    }

    public void setHgvsServiceUrl(String hgvsServiceUrl) {
        this.hgvsServiceUrl = hgvsServiceUrl;
    }

    private String getTumorSeqAllele(MutationRecord record) {
        String tumorSeqAllele;
        if (record.getTUMOR_SEQ_ALLELE1().equals(record.getREFERENCE_ALLELE()) || record.getTUMOR_SEQ_ALLELE1().equals("") || record.getTUMOR_SEQ_ALLELE1().equals("NA")) {
            return tumorSeqAllele = record.getTUMOR_SEQ_ALLELE2();
        }
        else {
            return tumorSeqAllele = record.getTUMOR_SEQ_ALLELE1();
        }
    }

    private String pickHighestPriorityConsequence(List<String> consequences) {
        String highestPriorityConsequence = "";
        Integer highestPriority = Integer.MAX_VALUE;
        for (String consequence : consequences) {
            if (effectPriority.getOrDefault(consequence.toLowerCase(), Integer.MAX_VALUE) < highestPriority) {
                highestPriorityConsequence = consequence;
                highestPriority = effectPriority.getOrDefault(consequence.toLowerCase(), Integer.MAX_VALUE);
            }
        }
        return highestPriorityConsequence;
    }
    
    private static Map<String, String> initVariantMap() {
        Map<String, String> variantMap = new HashMap<>();
        variantMap.put("splice_acceptor_variant",       "Splice_Site");
        variantMap.put("splice_donor_variant",          "Splice_Site");
        variantMap.put("transcript_ablation",           "Splice_Site");
        variantMap.put("exon_loss_variant", "Splice_Site");
        variantMap.put("stop_gained",                   "Nonsense_Mutation");
        variantMap.put("frameshift_variant",            "Frame_Shift");
        variantMap.put("stop_lost",                     "Nonstop_Mutation");
        variantMap.put("initiator_codon_variant",       "Translation_Start_Site");
        variantMap.put("start_lost",                    "Translation_Start_Site");
        variantMap.put("inframe_insertion",             "In_Frame_Ins");
        variantMap.put("inframe_deletion",              "In_Frame_Del");
        variantMap.put("disruptive_inframe_insertion", "In_Frame_Ins");
        variantMap.put("disrupting_inframe_deletion", "In_Frame_Del");
        variantMap.put("missense_variant",              "Missense_Mutation");
        variantMap.put("protein_altering_variant",      "Missense_Mutation"); // Not always correct, code below to handle exceptions
        variantMap.put("coding_sequence_variant",       "Missense_Mutation"); // Not always correct, code below to handle exceptions
        variantMap.put("conservative_missense_variant", "Missense_Mutation");
        variantMap.put("rare_amino_acid_variant",       "Missense_Mutation");
        variantMap.put("transcript_amplification",      "Intron");
        variantMap.put("splice_region_variant",         "Splice_Region");
        variantMap.put("intron_variant",                "Intron");
        variantMap.put("intragenic",                    "Intron");
        variantMap.put("intragenic_variant",            "Intron");
        variantMap.put("incomplete_terminal_codon_variant", "Silent");
        variantMap.put("synonymous_variant",            "Silent");
        variantMap.put("stop_retained_variant",         "Silent");
        variantMap.put("nmd_transcript_variant",        "Silent");
        variantMap.put("mature_mirna_variant",          "RNA");
        variantMap.put("non_coding_exon_variant",       "RNA");
        variantMap.put("non_coding_transcript_exon_variant", "RNA");
        variantMap.put("non_coding_transcript_variant", "RNA");
        variantMap.put("nc_transcript_variant",         "RNA");
        variantMap.put("exon_variant", "RNA");
        variantMap.put("5_prime_utr_variant",           "5'UTR");
        variantMap.put("5_prime_utr_premature_start_codon_gain_variant", "5'UTR");
        variantMap.put("3_prime_utr_variant",           "3'UTR");
        variantMap.put("TF_binding_site_variant",       "IGR");
        variantMap.put("regulatory_region_variant",     "IGR");
        variantMap.put("regulatory_region",             "IGR");
        variantMap.put("intergenic_variant",            "IGR");
        variantMap.put("intergenic_region",             "IGR");
        variantMap.put("upstream_gene_variant",         "5'Flank");
        variantMap.put("downstream_gene_variant",       "3'Flank");
        return variantMap;
    }

    private static List<String> initNullClassifications() {
        List<String> hgvspNullClassifications = new ArrayList<>();
        hgvspNullClassifications.add("3'UTR");
        hgvspNullClassifications.add("5'UTR");
        hgvspNullClassifications.add("3'Flank");
        hgvspNullClassifications.add("5'Flank");
        hgvspNullClassifications.add("IGR");
        hgvspNullClassifications.add("Intron");
        hgvspNullClassifications.add("RNA");
        return hgvspNullClassifications;
    }

    // Prioritize Sequence Ontology terms in order of severity, as estimated by Ensembl:
    // http://useast.ensembl.org/info/genome/variation/predicted_data.html#consequences
    private static Map<String, Integer> initEffectPriority() {
        Map<String, Integer> effectPriority = new HashMap<>();
        effectPriority.put("transcript_ablation", 1); // A feature ablation whereby the deleted region includes a transcript feature
        effectPriority.put("exon_loss_variant", 1); // A sequence variant whereby an exon is lost from the transcript
        effectPriority.put("splice_donor_variant", 2); // A splice variant that changes the 2 base region at the 5' end of an intron
        effectPriority.put("splice_acceptor_variant", 2); // A splice variant that changes the 2 base region at the 3' end of an intron
        effectPriority.put("stop_gained", 3); // A sequence variant whereby at least one base of a codon is changed, resulting in a premature stop codon, leading to a shortened transcript
        effectPriority.put("frameshift_variant", 3); // A sequence variant which causes a disruption of the translational reading frame, because the number of nucleotides inserted or deleted is not a multiple of three
        effectPriority.put("stop_lost", 3); // A sequence variant where at least one base of the terminator codon (stop) is changed, resulting in an elongated transcript
        effectPriority.put("start_lost", 4); // A codon variant that changes at least one base of the canonical start codon
        effectPriority.put("initiator_codon_variant", 4); // A codon variant that changes at least one base of the first codon of a transcript
        effectPriority.put("disruptive_inframe_insertion", 5); // An inframe increase in cds length that inserts one or more codons into the coding sequence within an existing codon
        effectPriority.put("disruptive_inframe_deletion", 5); // An inframe decrease in cds length that deletes bases from the coding sequence starting within an existing codon
        effectPriority.put("inframe_insertion", 5); // An inframe non synonymous variant that inserts bases into the coding sequence
        effectPriority.put("inframe_deletion", 5); // An inframe non synonymous variant that deletes bases from the coding sequence
        effectPriority.put("missense_variant", 6); // A sequence variant, that changes one or more bases, resulting in a different amino acid sequence but where the length is preserved
        effectPriority.put("conservative_missense_variant", 6); // A sequence variant whereby at least one base of a codon is changed resulting in a codon that encodes for a different but similar amino acid. These variants may or may not be deleterious
        effectPriority.put("rare_amino_acid_variant", 6); // A sequence variant whereby at least one base of a codon encoding a rare amino acid is changed, resulting in a different encoded amino acid
        effectPriority.put("transcript_amplification", 7); // A feature amplification of a region containing a transcript
        effectPriority.put("splice_region_variant", 8); // A sequence variant in which a change has occurred within the region of the splice site, either within 1-3 bases of the exon or 3-8 bases of the intron
        effectPriority.put("stop_retained_variant", 9); // A sequence variant where at least one base in the terminator codon is changed, but the terminator remains
        effectPriority.put("synonymous_variant", 9); // A sequence variant where there is no resulting change to the encoded amino acid
        effectPriority.put("incomplete_terminal_codon_variant", 10); // A sequence variant where at least one base of the final codon of an incompletely annotated transcript is changed
        effectPriority.put("protein_altering_variant", 11); // A sequence variant which is predicted to change the protein encoded in the coding sequence
        effectPriority.put("coding_sequence_variant", 11); // A sequence variant that changes the coding sequence
        effectPriority.put("mature_miRNA_variant", 11); // A transcript variant located with the sequence of the mature miRNA
        effectPriority.put("exon_variant", 11); // A sequence variant that changes exon sequence
        effectPriority.put("5_prime_utr_variant", 12); // A UTR variant of the 5' UTR
        effectPriority.put("5_prime_utr_premature_start_codon_gain_variant", 12); // snpEff-specific effect, creating a start codon in 5' UTR
        effectPriority.put("3_prime_utr_variant", 12); // A UTR variant of the 3' UTR
        effectPriority.put("non_coding_exon_variant", 13); // A sequence variant that changes non-coding exon sequence
        effectPriority.put("non_coding_transcript_exon_variant", 13); // snpEff-specific synonym for non_coding_exon_variant
        effectPriority.put("non_coding_transcript_variant", 14); // A transcript variant of a non coding RNA gene
        effectPriority.put("nc_transcript_variant", 14); // A transcript variant of a non coding RNA gene (older alias for non_coding_transcript_variant)
        effectPriority.put("intron_variant", 14); // A transcript variant occurring within an intron
        effectPriority.put("intragenic_variant", 14); // A variant that occurs within a gene but falls outside of all transcript features. This occurs when alternate transcripts of a gene do not share overlapping sequence
        effectPriority.put("intragenic", 14); // snpEff-specific synonym of intragenic_variant
        effectPriority.put("nmd_transcript_variant", 15); // A variant in a transcript that is the target of NMD
        effectPriority.put("upstream_gene_variant", 16); // A sequence variant located 5' of a gene
        effectPriority.put("downstream_gene_variant", 16); // A sequence variant located 3' of a gene
        effectPriority.put("tfbs_ablation", 17); // A feature ablation whereby the deleted region includes a transcription factor binding site
        effectPriority.put("tfbs_amplification", 17); // A feature amplification of a region containing a transcription factor binding site
        effectPriority.put("tf_binding_site_variant", 17); // A sequence variant located within a transcription factor binding site
        effectPriority.put("regulatory_region_ablation", 17); // A feature ablation whereby the deleted region includes a regulatory region
        effectPriority.put("regulatory_region_amplification", 17); // A feature amplification of a region containing a regulatory region
        effectPriority.put("regulatory_region_variant", 17); // A sequence variant located within a regulatory region
        effectPriority.put("regulatory_region", 17); // snpEff-specific effect that should really be regulatory_region_variant
        effectPriority.put("feature_elongation", 18); // A sequence variant that causes the extension of a genomic feature, with regard to the reference sequence
        effectPriority.put("feature_truncation", 18); // A sequence variant that causes the reduction of a genomic feature, with regard to the reference sequence
        effectPriority.put("intergenic_variant", 19); // A sequence variant located in the intergenic region, between genes
        effectPriority.put("intergenic_region", 19); // snpEff-specific effect that should really be intergenic_variant
        effectPriority.put("", 20);
        return effectPriority;
    }

    public static void main(String[] args) {}
}
