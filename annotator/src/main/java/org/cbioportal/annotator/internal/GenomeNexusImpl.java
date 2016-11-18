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
    
    @Value("${genomenexus.isoform_override}")
    private String overrideServiceUrl;   
    
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
    
    private Map<String, String> variantMap = new HashMap<String, String>();
    
    private Pattern cDnaExtractor = Pattern.compile(".*c.(\\d+).*");
    private String variantType;
    
    @Bean
    public GenomeNexusImpl annotator() {
        return this;
    }
    
    @Bean
    public GenomeNexusImpl annotator(String hgvsServiceUrl, String overrideServiceUrl) {
        this.hgvsServiceUrl = hgvsServiceUrl;
        this.overrideServiceUrl = overrideServiceUrl;
        return this;
    }
    
    @Override
    public AnnotatedRecord annotateRecord(MutationRecord record, boolean replaceHugo, String isoformOverridesSource, boolean reannotate) {
        this.mRecord = record;
        
        //check if record already is annotated
        Map<String, String> additionalProperties = mRecord.getAdditionalProperties();
        if(additionalProperties.containsKey("HGVSp_Short") && !reannotate && !additionalProperties.get("HGVSp_Short").isEmpty()) {
            return new AnnotatedRecord(mRecord.getHugo_Symbol(),
                mRecord.getEntrez_Gene_Id(),
                mRecord.getCenter(),
                mRecord.getNCBI_Build(),
                mRecord.getChromosome(),
                mRecord.getStart_Position(),
                mRecord.getEnd_Position(),
                mRecord.getStrand(),
                mRecord.getVariant_Classification(),
                mRecord.getVariant_Type(),
                mRecord.getReference_Allele(),
                mRecord.getTumor_Seq_Allele1(),
                mRecord.getTumor_Seq_Allele2(),
                mRecord.getdbSNP_RS(),
                mRecord.getdbSNP_Val_Status(),
                mRecord.getTumor_Sample_Barcode(),
                mRecord.getMatched_Norm_Sample_Barcode(),
                mRecord.getMatch_Norm_Seq_Allele1(),
                mRecord.getMatch_Norm_Seq_Allele2(),
                mRecord.getTumor_Validation_Allele1(),
                mRecord.getTumor_Validation_Allele2(),
                mRecord.getMatch_Norm_Validation_Allele1(),
                mRecord.getMatch_Norm_Validation_Allele2(),
                mRecord.getVerification_Status(),
                mRecord.getValidation_Status(),
                mRecord.getMutation_Status(),
                mRecord.getSequencing_Phase(),
                mRecord.getSequence_Source(),
                mRecord.getValidation_Method(),
                mRecord.getScore(),
                mRecord.getBAM_File(),
                mRecord.getSequencer(),
                mRecord.getTumor_Sample_UUID(),
                mRecord.getMatched_Norm_Sample_UUID(),
                mRecord.gett_ref_count(),
                mRecord.gett_alt_count(),
                mRecord.getn_ref_count(),
                mRecord.getn_alt_count(),
                additionalProperties.get("HGVSc") != null ? additionalProperties.get("HGVSc") : "",
                additionalProperties.get("HGVSp") != null ? additionalProperties.get("HGVSp") : "",
                additionalProperties.get("HGVSp_Short"),
                additionalProperties.get("Transcript_ID") != null ? additionalProperties.get("Transcript_ID") : "",
                additionalProperties.get("RefSeq") != null ? additionalProperties.get("RefSeq") : "",
                additionalProperties.get("Protein_Position") != null ? additionalProperties.get("Protein_Position") : "",
                additionalProperties.get("Protein_Position") != null ? additionalProperties.get("Protein_Position") : "",
                additionalProperties.get("Codons") != null ? additionalProperties.get("Codons") : "",
                additionalProperties);                                   
        }
        
        // first, get the mutation in the right notation
        String hgvsNotation = convertToHgvs(record);
        
        // make the rest call to genome nexus
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();

        log.debug("Annotating: " + hgvsNotation + " from sample " + record.getTumor_Sample_Barcode());
        
        ResponseEntity<GenomeNexusAnnotationResponse[]> responseEntity = restTemplate.exchange(hgvsServiceUrl + hgvsNotation, HttpMethod.GET, requestEntity, GenomeNexusAnnotationResponse[].class);
        gnResponse = responseEntity.getBody()[0];
        
        // get the canonical trnascript
        canonicalTranscript = getCanonicalTranscript(gnResponse, isoformOverridesSource);
        
        // annotate the record
        AnnotatedRecord annotatedRecord= new AnnotatedRecord(resolveHugoSymbol(replaceHugo),
                mRecord.getEntrez_Gene_Id(),
                mRecord.getCenter(),
                gnResponse.getAssemblyName(),               
                resolveChromosome(),
                resolveStart(),
                resolveEnd(),
                resolveStrandSign(),
                resolveVariantClassification(),
                resolveVariantType(),
                resolveReferenceAllele(),
                resolveReferenceAllele(),
                resolveTumorSeqAllele(),
                mRecord.getdbSNP_RS(),
                mRecord.getdbSNP_Val_Status(),
                mRecord.getTumor_Sample_Barcode(),
                mRecord.getMatched_Norm_Sample_Barcode(),
                mRecord.getMatch_Norm_Seq_Allele1(),
                mRecord.getMatch_Norm_Seq_Allele2(),
                mRecord.getTumor_Validation_Allele1(),
                mRecord.getTumor_Validation_Allele2(),
                mRecord.getMatch_Norm_Validation_Allele1(),
                mRecord.getMatch_Norm_Validation_Allele2(),
                mRecord.getVerification_Status(),
                mRecord.getValidation_Status(),
                mRecord.getMutation_Status(),
                mRecord.getSequencing_Phase(),
                mRecord.getSequence_Source(),
                mRecord.getValidation_Method(),
                mRecord.getScore(),
                mRecord.getBAM_File(),
                mRecord.getSequencer(),
                mRecord.getTumor_Sample_UUID(),
                mRecord.getMatched_Norm_Sample_UUID(),
                mRecord.gett_ref_count(),
                mRecord.gett_alt_count(),
                mRecord.getn_ref_count(),
                mRecord.getn_alt_count(),
                resolveHgvsc(),
                resolveHgvsp(),
                resolveHgvspShort(),
                resolveTranscriptId(),
                resolveRefSeq(),
                resolveProteinPosStart(),
                resolveProteinPosEnd(),
                resolveCodonChange(),
                mRecord.getAdditionalProperties());
                
        return annotatedRecord;
    }
    
    @Override
    public MutationRecord createRecord(Map<String, String> mafLine) throws Exception {
        MutationRecord record = new MutationRecord();
        for (String header : new MutationRecord().getHeader()) {
            if(mafLine.keySet().contains(header)) {
                record.getClass().getMethod("set" + header, String.class).invoke(record, mafLine.remove(header));
            }
        }
        record.setAdditionalProperties(mafLine);
        return record;
    }
    
    private String resolveHugoSymbol(boolean replaceHugo) {
        if (replaceHugo && canonicalTranscript.getGeneSymbol() != null && canonicalTranscript.getGeneSymbol().trim().length() > 0) {
            return canonicalTranscript.getGeneSymbol();
        }
        
        return mRecord.getHugo_Symbol();
    }    
    private String resolveChromosome() {
        if (gnResponse.getSeqRegionName() != null) {
            return gnResponse.getSeqRegionName();
        }
        return mRecord.getChromosome();        
    }
    
    private String resolveStart() {
        try {
            if (gnResponse.getStart() != null) {
                return String.valueOf(Math.min(Integer.parseInt(gnResponse.getStart()), Integer.parseInt(gnResponse.getEnd())));
            }
            else {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            return mRecord.getStart_Position();
        }
    }
    
    private String resolveEnd() {
        try {
            if(gnResponse.getEnd() != null) {
                return String.valueOf(Math.max(Integer.parseInt(gnResponse.getStart()), Integer.parseInt(gnResponse.getEnd())));
            }
            else {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            return mRecord.getEnd_Position();
        }
    }    
    
    private String resolveStrandSign() {
        String strand = gnResponse.getStrand();        
        
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
            strand = mRecord.getStrand();
        }
        
        return strand;
    }
    
    private String resolveVariantClassification() {
        String variantClassification = null;
        String[] alleles = null;
        if (gnResponse.getAlleleString() != null) {
            alleles = gnResponse.getAlleleString().split("/", -1);
        }
        if(alleles != null) {
            if (alleles.length == 2) {
                variantType = getVariantType(alleles[0], alleles[1]);                
            }
        }        
        if (canonicalTranscript != null) {
            if (canonicalTranscript.getConsequenceTerms().size() > 0) {
                if (canonicalTranscript.getConsequenceTerms().size() > 0)
                {
                    variantClassification = getVariantClassificationFromMap(canonicalTranscript.getConsequenceTerms().get(0));
                }                
                if (variantClassification != null && variantClassification.equals("Frame_Shift")) {
                    if (variantType != null && variantType.equals("INS")) {
                        variantClassification += "_Ins";
                    }
                    else if (variantType != null && variantType.equals("DEL")) {
                        variantClassification += "_Del";
                    }
                }
            }            
        }
        return variantClassification != null ? variantClassification : "";
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
        return "";
    }
    
    private String resolveTumorSeqAllele() {
        if (gnResponse.getAlleleString() != null)
        {
            return gnResponse.getAlleleString().split("/", -1)[1];
        }
        return "";
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
            else if (canonicalTranscript.getHgvsc() != null && (canonicalTranscript.getConsequenceTerms().get(0).equals("splice_acceptor_variant") || canonicalTranscript.getConsequenceTerms().get(0).equals("splice_donor_variant"))) {
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
            if(canonicalTranscript.getRefseqTranscriptIds().size() > 0) {
                refSeq = canonicalTranscript.getRefseqTranscriptIds().get(0);
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
                    log.info("Check " + mRecord.getTumor_Sample_Barcode() + " " + mRecord.getHugo_Symbol());
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
    
    private String convertToHgvs(MutationRecord record)
    {        
        String chr = record.getChromosome();
        String start = record.getStart_Position();
        String end = record.getEnd_Position();
        String ref = record.getReference_Allele();
        String var;
        if (record.getTumor_Seq_Allele1().equals(ref) || record.getTumor_Seq_Allele1().equals("") || record.getTumor_Seq_Allele1().equals("NA")) {
            var = record.getTumor_Seq_Allele2();
        }
        else {
            var = record.getTumor_Seq_Allele1();
        }

        if (ref.equals(var)){
            log.info("Warning: Reference allele extracted from " + chr + ":" + start + "-" + end + " matches alt allele. Sample: " + record.getTumor_Sample_Barcode());
            return null;
        }

        // Remove common prefix and ajust variant position accordingly
        String prefix = longestCommonPrefix(ref, var);
        if (prefix.length() > 0){
            ref = ref.substring(prefix.length());
            var = var.substring(prefix.length());

            int nStart = Integer.valueOf(start);
            int nEnd = Integer.valueOf(end);
            
            nStart += prefix.length();
            
            record.setStart_Position(Integer.toString(nStart));
            record.setEnd_Position(Integer.toString(nEnd));
            start = Integer.toString(nStart);

            record.setReference_Allele(record.getReference_Allele().substring(prefix.length()));
            record.setTumor_Seq_Allele1(record.getTumor_Seq_Allele1().substring(prefix.length()));
        }

        String hgvs;
        /*
         Process Insertion
         Example insertion: 17 36002277 36002278 - A
         Example output: 17:g.36002277_36002278insA
         */
        if(ref.equals("-") || ref.length() == 0){
            hgvs = chr+":g."+start+"_"+String.valueOf(Integer.parseInt(start) + 1)+"ins"+var;
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
    
    private TranscriptConsequence getCanonicalTranscript(GenomeNexusAnnotationResponse gnResponse, String isoformOverridesSource) {
        List<TranscriptConsequence> transcripts = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        Map<String, TranscriptConsequence> map = new HashMap<>();
        
        // construct a list of transcripts to be queried
        for(TranscriptConsequence transcript : gnResponse.getTranscriptConsequences()) {
            if(transcript.getTranscriptId() != null) {
                ids.add(transcript.getTranscriptId());
                map.put(transcript.getTranscriptId(), transcript);
            }
        }
        
        // get isoform override from genome nexus
        if (overrides.isEmpty()) {
            overrides = getIsoformOverrides(ids, isoformOverridesSource);
        }
        
        
        for (GenomeNexusIsoformOverridesResponse override : overrides) {
            TranscriptConsequence transcript = map.get(override.getTranscriptId());
            if (transcript != null) {
                transcripts.add(transcript);
            }
        }
        
        // If no canonical transcript is found, use information provided by the annotation service
        if (transcripts.size() == 0) {
            for (TranscriptConsequence transcript : gnResponse.getTranscriptConsequences()) {
                if (transcript.getCanonical() != null)
                {
                    if (transcript.getCanonical().equals("1")) {
                        transcripts.add(transcript);
                    }
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
        for (TranscriptConsequence transcript : transcripts) {
            List<String> consequenceTerms = transcript.getConsequenceTerms();
            for (String consequenceTerm : consequenceTerms) {
                if (consequenceTerm.trim().equals(mostSevereConsequence)) {
                    return transcript;
                }
            }
        }
        
        // no match
        return null;
    }
    
    private List<GenomeNexusIsoformOverridesResponse> getIsoformOverrides(List<String> ids, String isoformOverridesSource) {
        List<GenomeNexusIsoformOverridesResponse> overrides = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
        ResponseEntity<GenomeNexusIsoformOverridesResponse[]> responseEntity = restTemplate.exchange(overrideServiceUrl + isoformOverridesSource + "/" + StringUtils.join(ids, ","), HttpMethod.GET, requestEntity, GenomeNexusIsoformOverridesResponse[].class);
        if(responseEntity.getBody().length > 0) {
            overrides = Arrays.asList(responseEntity.getBody());
        }                    
        return overrides;
    }   
    
    private String getVariantClassificationFromMap(String variant) {
        if(variantMap.isEmpty()) {
            // TODO we need to identify all possible variant classifications!
            variantMap.put("splice_acceptor_variant",       "Splice_Site");
            variantMap.put("splice_donor_variant",          "Splice_Site");
            variantMap.put("transcript_ablation",           "Splice_Site");
            variantMap.put("stop_gained",                   "Nonsense_Mutation");    
            variantMap.put("frameshift_variant",            "Frame_Shift");
            variantMap.put("stop_lost",                     "Nonstop_Mutation");
            variantMap.put("initiator_codon_variant",       "Translation_Start_Site");
            variantMap.put("start_lost",                    "Translation_Start_Site");
            variantMap.put("inframe_insertion",             "In_Frame_Ins");
            variantMap.put("inframe_deletion",              "In_Frame_Del");    
            variantMap.put("missense_variant",              "Missense_Mutation");
            variantMap.put("protein_altering_variant",      "Missense_Mutation"); // Not sure if this is correct
            variantMap.put("coding_sequence_variant",       "Missense_Mutation");
            variantMap.put("conservative_missense_variant", "Missense_Mutation");
            variantMap.put("rare_amino_acid_variant",       "Missense_Mutation");
            variantMap.put("transcript_amplification",      "Intron");
            variantMap.put("splice_region_variant",         "Intron");
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
            variantMap.put("tfbs_ablation",                 "Targeted_Region"); 
            variantMap.put("tfbs_amplification",            "Targeted_Region"); 
            variantMap.put("regulatory_region_ablation",    "Targeted_Region"); 
            variantMap.put("regulatory_region_amplification", "Targeted_Region"); 
            variantMap.put("feature_elongation",            "Targeted_Region"); 
            variantMap.put("feature_truncation",            "Targeted_Region"); 
        }
        
        return variantMap.get(variant.toLowerCase());
    }
    
    public String getHgvsServiceUrl() {
        return hgvsServiceUrl;
    }
    
    public void setHgvsServiceUrl(String hgvsServiceUrl) {
        this.hgvsServiceUrl = hgvsServiceUrl;
    }
    
    public String getOverrideServiceUrl() {
        return overrideServiceUrl;
    }
    
    public void setOverrideServiceUrl(String overrideServiceUrl) {
        this.overrideServiceUrl = overrideServiceUrl;
    }    
    
    public static void main(String[] args) {}
}
