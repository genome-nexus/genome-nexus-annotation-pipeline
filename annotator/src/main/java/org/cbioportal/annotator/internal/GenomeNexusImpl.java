/*
 * Copyright (c) 2016 - 2019 Memorial Sloan-Kettering Cancer Center.
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

import com.google.common.base.Strings;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.GenomeNexusAnnotationFailureException;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.genome_nexus.ApiClient;
import org.genome_nexus.ApiException;
import org.genome_nexus.StringUtil;
import org.genome_nexus.client.AnnotationControllerApi;
import org.genome_nexus.client.InfoControllerApi;
import org.genome_nexus.client.GenomicLocation;
import org.genome_nexus.client.TranscriptConsequenceSummary;
import org.genome_nexus.client.VariantAnnotation;
import org.genome_nexus.client.Version;
import org.mskcc.cbio.maf.MafUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Zachary Heins
 *
 * Use GenomeNexus to annotate mutation records
 *
 */

@Configuration
public class GenomeNexusImpl implements Annotator {

    @Value("${genomenexus.base:https://www.genomenexus.org}")
    private String genomeNexusBaseUrl;
    @Value("${genomenexus.isoform_query_parameter:isofromOverrideSource}")
    private String isoformQueryParameter;
    @Value("${genomenexus.enrichment_fields:annotation_summary}")
    private String enrichmentFields;

    private AnnotationControllerApi apiClient;
    private static final String UKNOWN_GENOME_NEXUS_VERSION = "unknown";
    private final Log LOG = LogFactory.getLog(GenomeNexusImpl.class);

    private final Pattern PROTEIN_POSITTION_REGEX = Pattern.compile("p.[A-Za-z]([0-9]*).*$");
    private static List<String> hgvspNullClassifications = initNullClassifications();

    @Bean
    public GenomeNexusImpl annotator(String genomeNexusBaseUrl) {
        this.genomeNexusBaseUrl = genomeNexusBaseUrl;
        return annotator();
    }

    @Bean
    public GenomeNexusImpl annotator() {
        this.apiClient = initApiClient();
        return this;
    }

    public String getGenomeNexusBaseUrl() {
        return genomeNexusBaseUrl;
    }

    public void setGenomeNexusBaseUrl(String genomeNexusBaseUrl) {
        this.genomeNexusBaseUrl = genomeNexusBaseUrl;
    }

    private boolean annotationNeeded(MutationRecord record) {
        Map<String, String> additionalProperties = record.getAdditionalProperties();
        if (!additionalProperties.containsKey("HGVSp_Short")) {
            return true;
        }
        return additionalProperties.get("HGVSp_Short").isEmpty() && !hgvspNullClassifications.contains(record.getVARIANT_CLASSIFICATION());
    }

    @Override
    public AnnotatedRecord annotateRecord(MutationRecord mRecord, boolean replace, String isoformOverridesSource, boolean reannotate)
            throws GenomeNexusAnnotationFailureException
    {

        //check if record already is annotated
        if(!reannotate && !annotationNeeded(mRecord)) {
            return new AnnotatedRecord(mRecord);
        }

        String genomicLocation = this.extractGenomicLocationAsString(mRecord);
        VariantAnnotation gnResponse = null;
        try {
            gnResponse = this.apiClient.fetchVariantAnnotationByGenomicLocationGET(genomicLocation,
                    isoformOverridesSource,
                    Arrays.asList(this.enrichmentFields.split(",")));
        } catch (ApiException e) {
            throw new GenomeNexusAnnotationFailureException("Empty Response From Genome Nexus: " + genomicLocation);
        }

        return convertResponseToAnnotatedRecord(gnResponse, mRecord, replace);
    }

    @Override
    public String getVersion() {
        InfoControllerApi infoApiClient = new InfoControllerApi();
        try {
            Version result = infoApiClient.fetchVersionGET();
            return result.getVersion();
        } catch (ApiException e) {
            LOG.error("Exception when calling InfoControllerApi#fetchVersionGET, genome nexus version is unknown", e);
        }
        return UKNOWN_GENOME_NEXUS_VERSION;
    }

    private AnnotationControllerApi initApiClient()
    {
        AnnotationControllerApi apiClient;

        if (this.genomeNexusBaseUrl != null && this.genomeNexusBaseUrl.length() > 0)
        {
            ApiClient client = new ApiClient();
            client.setBasePath(this.genomeNexusBaseUrl);
            apiClient = new AnnotationControllerApi(client);
        }
        else {
            apiClient = new AnnotationControllerApi();
        }

        return apiClient;
    }

    public AnnotatedRecord convertResponseToAnnotatedRecord(VariantAnnotation gnResponse, MutationRecord mRecord, boolean replace) {
        // get the canonical transcript
        TranscriptConsequenceSummary canonicalTranscript = getCanonicalTranscript(gnResponse);

        // annotate the record
        AnnotatedRecord annotatedRecord= new AnnotatedRecord(resolveHugoSymbol(canonicalTranscript, mRecord, replace),
                resolveEntrezGeneId(canonicalTranscript, mRecord, replace),
                mRecord.getCENTER(),
                resolveAssemblyName(gnResponse, mRecord),
                resolveChromosome(gnResponse, mRecord),
                resolveStart(gnResponse, mRecord),
                resolveEnd(gnResponse, mRecord),
                resolveStrandSign(gnResponse, mRecord),
                resolveVariantClassification(canonicalTranscript, mRecord),
                resolveVariantType(gnResponse),
                resolveReferenceAllele(gnResponse, mRecord),
                mRecord.getTUMOR_SEQ_ALLELE1(),
                resolveTumorSeqAllele(gnResponse, mRecord),
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
                resolveHgvsc(canonicalTranscript),
                resolveHgvsp(canonicalTranscript),
                resolveHgvspShort(canonicalTranscript),
                resolveTranscriptId(canonicalTranscript),
                resolveRefSeq(canonicalTranscript),
                resolveProteinPosStart(canonicalTranscript),
                resolveProteinPosEnd(canonicalTranscript),
                resolveCodonChange(canonicalTranscript),
                resolveHotspot(),
                resolveConsequence(canonicalTranscript),
                resolveProteinPosition(canonicalTranscript, mRecord),
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
        String genomicLocation = extractGenomicLocationAsString(record);

        // TODO this is now handled by the API client, we don't really need this (keeping for logging purposes only)
        return genomeNexusBaseUrl + "annotation/genomic/" + genomicLocation + "?" +
                isoformQueryParameter + "=" + isoformOverridesSource + "&fields=" + enrichmentFields;
    }

    private String resolveHugoSymbol(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (replace && canonicalTranscript != null && canonicalTranscript.getHugoGeneSymbol() != null) {
            return canonicalTranscript.getHugoGeneSymbol();
        }
        else {
            return mRecord.getHUGO_SYMBOL();
        }
    }

    private String resolveChromosome(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome() != null)
        {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome();
        }
        else {
            return mRecord.getCHROMOSOME();
        }
    }

    private String resolveAssemblyName(VariantAnnotation gnResponse, MutationRecord mRecord) {
        return (gnResponse.getAssemblyName() == null) ? mRecord.getNCBI_BUILD() : gnResponse.getAssemblyName();
    }

    private String resolveStart(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getGenomicLocation().getStart() != null)
        {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getStart().toString();
        }
        else {
            return mRecord.getSTART_POSITION();
        }
    }

    private String resolveEnd(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getGenomicLocation().getEnd() != null)
        {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getEnd().toString();
        }
        else {
            return mRecord.getEND_POSITION();
        }
    }

    private String resolveStrandSign(VariantAnnotation gnResponse, MutationRecord mRecord) {
        String strand = String.valueOf(gnResponse.getStrand());

        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getStrandSign() != null)
        {
            strand = gnResponse.getAnnotationSummary().getStrandSign();
        }
        else {
            strand = mRecord.getSTRAND();
        }

        return strand;
    }

    private String resolveVariantClassification(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord) {
        String variantClassification = null;

        if (canonicalTranscript != null) {
            variantClassification = canonicalTranscript.getVariantClassification();
        }

        return variantClassification != null ? variantClassification : mRecord.getVARIANT_CLASSIFICATION();
    }

    private String resolveVariantType(VariantAnnotation gnResponse) {
        String variantType = "";

        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getVariantType() != null)
        {
            variantType = gnResponse.getAnnotationSummary().getVariantType();
        }

        return variantType;
    }

    private String resolveReferenceAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele() != null)
        {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele();
        }
        return mRecord.getREFERENCE_ALLELE();
    }

    private String resolveTumorSeqAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null &&
                gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele() != null)
        {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele();
        }
        return MafUtil.resolveTumorSeqAllele(mRecord.getREFERENCE_ALLELE(),
                mRecord.getTUMOR_SEQ_ALLELE1(),
                mRecord.getTUMOR_SEQ_ALLELE2());
    }

    private String resolveHgvsc(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsc = "";

        if (canonicalTranscript != null &&
            canonicalTranscript.getHgvsc() != null)
        {
            hgvsc = canonicalTranscript.getHgvsc();
        }

        return hgvsc;
    }

    private String resolveHgvsp(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";

        if (canonicalTranscript != null &&
            canonicalTranscript.getHgvsp() != null)
        {
            hgvsp = canonicalTranscript.getHgvsp();
        }

        return hgvsp;
    }

    private String resolveHgvspShort(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";

        if (canonicalTranscript != null &&
            canonicalTranscript.getHgvspShort() != null)
        {
            hgvsp = canonicalTranscript.getHgvspShort();
        }

        return hgvsp;
    }

    private String resolveTranscriptId(TranscriptConsequenceSummary canonicalTranscript) {
        String transcriptId = "";

        if(canonicalTranscript != null) {
            transcriptId = canonicalTranscript.getTranscriptId();
        }

        return transcriptId != null ? transcriptId : "";
    }

    private String resolveRefSeq(TranscriptConsequenceSummary canonicalTranscript) {
        String refSeq = "";

        if(canonicalTranscript != null) {
            refSeq = canonicalTranscript.getRefSeq();
        }

        return refSeq != null ? refSeq : "";
    }

    private String resolveProteinPosStart(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinStart = null;

        if(canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinStart = canonicalTranscript.getProteinPosition().getStart();
        }

        return proteinStart != null ? proteinStart.toString() : "";
    }

    private String resolveProteinPosEnd(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinEnd = null;

        if(canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinEnd = canonicalTranscript.getProteinPosition().getEnd();
        }

        return proteinEnd != null ? proteinEnd.toString() : "";
    }

    private String resolveCodonChange(TranscriptConsequenceSummary canonicalTranscript) {
        String codonChange = "";
        if(canonicalTranscript != null) {
            codonChange = canonicalTranscript.getCodonChange();
        }

        return codonChange != null ? codonChange : "";
    }

    private String resolveHotspot() {
        String hotspot = "0";
        // TODO this hotspot field is not valid anymore:
        // we need to redo this part if we want to include hotspot information
//        if (canonicalTranscript != null) {
//            if (canonicalTranscript.getIsHotspot() != null) {
//                hotspot = canonicalTranscript.getIsHotspot().equals("true") ? "1" : "0";
//            }
//        }
        return hotspot;
    }

    private String resolveConsequence(TranscriptConsequenceSummary canonicalTranscript) {
        if (canonicalTranscript == null || canonicalTranscript.getConsequenceTerms() == null) {
            return "";
        }
        else {
            return canonicalTranscript.getConsequenceTerms();
        }
    }

    private String resolveEntrezGeneId(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (!replace || canonicalTranscript == null || canonicalTranscript.getEntrezGeneId() == null) {
            return mRecord.getENTREZ_GENE_ID();
        }
        else {
            return canonicalTranscript.getEntrezGeneId();
        }
    }

    private String resolveProteinPosition(TranscriptConsequenceSummary canonicalTranscript, MutationRecord record) {
        String proteinPosition = null;
        if (canonicalTranscript != null) {
            String proteinPosStart = resolveProteinPosStart(canonicalTranscript);
            String hgvspShort = resolveHgvspShort(canonicalTranscript);
            if (!Strings.isNullOrEmpty(proteinPosStart)) {
                proteinPosition = proteinPosStart;
            } else if (!Strings.isNullOrEmpty(hgvspShort)) {
                // try extracting from hgvspShort if proteinPosStart null/empty
                Matcher matcher = PROTEIN_POSITTION_REGEX.matcher(hgvspShort);
                if (matcher.find()) {
                    proteinPosition = matcher.group(1);
                }
            }
        }
        return !Strings.isNullOrEmpty(proteinPosition) ? proteinPosition : record.getAdditionalProperties().getOrDefault("Protein_position", "");
    }
    public String extractGenomicLocationAsString(GenomicLocation genomicLocation) {
        return StringUtil.join(
                new String[]{genomicLocation.getChromosome(),
                    genomicLocation.getStart().toString(),
                    genomicLocation.getEnd().toString(),
                    genomicLocation.getReferenceAllele(),
                    genomicLocation.getVariantAllele()},
                ",");
    }

    public String extractGenomicLocationAsString(MutationRecord record) {
        GenomicLocation genomicLocation = extractGenomicLocation(record);
        if(genomicLocation.getReferenceAllele().equals(genomicLocation.getVariantAllele())) {
            LOG.warn("Warning: Reference allele extracted from " + genomicLocation.getChromosome() + ":" + genomicLocation.getStart() +
                    "-" + genomicLocation.getEnd() + " matches alt allele. Sample: " + record.getTUMOR_SAMPLE_BARCODE());
        }
        return extractGenomicLocationAsString(genomicLocation);
    }

    public GenomicLocation extractGenomicLocation(MutationRecord record) {
        GenomicLocation genomicLocation = new GenomicLocation();
        genomicLocation.setChromosome(record.getCHROMOSOME());
        genomicLocation.setStart(Integer.valueOf(record.getSTART_POSITION()));
        genomicLocation.setEnd(Integer.valueOf(record.getEND_POSITION()));
        genomicLocation.setReferenceAllele(record.getREFERENCE_ALLELE());
        genomicLocation.setVariantAllele(MafUtil.resolveTumorSeqAllele(record.getREFERENCE_ALLELE(),
                record.getTUMOR_SEQ_ALLELE1(),
                record.getTUMOR_SEQ_ALLELE2()));
        return genomicLocation;
    }

    private TranscriptConsequenceSummary getCanonicalTranscript(VariantAnnotation gnResponse) {
        if (gnResponse.getAnnotationSummary() != null &&
            gnResponse.getAnnotationSummary().getTranscriptConsequences() != null &&
            gnResponse.getAnnotationSummary().getTranscriptConsequences().size() > 0)
        {
            return gnResponse.getAnnotationSummary().getTranscriptConsequences().get(0);
        }
        else {
            return null;
        }
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

    @Override
    public List<AnnotatedRecord> getAnnotatedRecordsUsingPOST(AnnotationSummaryStatistics summaryStatistics, List<MutationRecord> mutationRecords, String isoformOverridesSource, Boolean replace) throws Exception {
        // construct list of genomic location objects to pass to api client
        List<GenomicLocation> genomicLocations = new ArrayList<>();
        for (MutationRecord record : mutationRecords) {
            genomicLocations.add(extractGenomicLocation(record));
        }
        List<VariantAnnotation> gnResponseList = apiClient.fetchVariantAnnotationByGenomicLocationPOST(genomicLocations,
                isoformOverridesSource, Arrays.asList(this.enrichmentFields.split(",")));
        Map<String, VariantAnnotation> gnResponseVariantKeyMap = new HashMap<>();
        for (VariantAnnotation gnResponse : gnResponseList) {
            if (gnResponse.isSuccessfullyAnnotated()) {
                gnResponseVariantKeyMap.put(extractGenomicLocationAsString(gnResponse.getAnnotationSummary().getGenomicLocation()), gnResponse);
            } else {
                LOG.warn("Annotation failed for variant " + gnResponse.getVariant());
            }
        }
        // create annotated records by merging the responses from gn with their corresponding MAF record
        List<AnnotatedRecord> annotatedRecords = new ArrayList<>();
        for (MutationRecord record : mutationRecords) {
            String genomicLocationKey = extractGenomicLocationAsString(record);
            AnnotatedRecord annotatedRecord = new AnnotatedRecord(record);
            // if not a failed annotation then convert/merge the response from gn with the maf record
            VariantAnnotation gnResponse = gnResponseVariantKeyMap.get(genomicLocationKey);
            if (gnResponse == null) {
                summaryStatistics.addFailedAnnotatedRecordDueToServer(record, "Annotation failed due to server error", isoformOverridesSource);
            } else if (!summaryStatistics.isFailedAnnotatedRecord(annotatedRecord, record, isoformOverridesSource)) {
                annotatedRecord = convertResponseToAnnotatedRecord(gnResponseVariantKeyMap.get(genomicLocationKey), record, replace);
            }
            annotatedRecords.add(annotatedRecord);
        }
        return annotatedRecords;
    }
}
