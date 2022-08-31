/*
 * Copyright (c) 2016 - 2020 Memorial Sloan-Kettering Cancer Center.
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

import org.apache.commons.lang3.StringUtils;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.GenomeNexusAnnotationFailureException;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.genome_nexus.ApiClient;
import org.genome_nexus.ApiException;
import org.genome_nexus.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.cbioportal.annotator.util.AnnotationUtil.*;
import static org.cbioportal.models.Header.*;

/**
 * @author Zachary Heins
 * <p>
 * Use GenomeNexus to annotate mutation records
 */

@Configuration
public class GenomeNexusImpl implements Annotator {

    static final Comparator<GenomicLocation> GENOMIC_LOCATION_COMPARATOR = new Comparator<GenomicLocation>() {
        // GenomicLocation can't be null, so we do not need to check for null
        public int compare(GenomicLocation gl1, GenomicLocation gl2) {
            int chromCmp = gl1.getChromosome().compareTo(gl2.getChromosome());
            if (chromCmp != 0) {
                return chromCmp;
            }
            int startCmp = gl1.getStart().compareTo(gl2.getStart());
            if (startCmp != 0) {
                return startCmp;
            }
            int endCmp = gl1.getEnd().compareTo(gl2.getEnd());
            if (endCmp != 0) {
                return endCmp;
            }
            int referenceAlleleCmp = gl1.getReferenceAllele().compareTo(gl2.getReferenceAllele());
            return (referenceAlleleCmp != 0 ? referenceAlleleCmp : gl1.getVariantAllele().compareTo(gl2.getVariantAllele()));
        }
    };
    private static final String UKNOWN_GENOME_NEXUS_VERSION = "unknown";
    private static final List<String> hgvspNullClassifications = List.of("3'UTR", "5'UTR", "3'Flank", "5'Flank", "IGR", "Intron", "RNA");
    private final Logger LOG = LoggerFactory.getLogger(GenomeNexusImpl.class);
    private final Integer READ_TIMEOUT_OVERRIDE = 300000; // built-in default of 5 seconds is not enough time to read responses
    @Value("${genomenexus.base:https://www.genomenexus.org}")
    private String genomeNexusBaseUrl;
    @Value("${genomenexus.isoform_query_parameter:isofromOverrideSource}")
    private String isoformQueryParameter;
    @Value("${genomenexus.enrichment_fields:annotation_summary}")
    private String enrichmentFields;
    private AnnotationControllerApi apiClient;

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
        String token = record.get(HGVSp_Short);
        if (token == null) {
            return true;
        }
        return token.isEmpty() && !hgvspNullClassifications.contains(record.get(Variant_Classification));
    }

    @Override
    public AnnotatedRecord annotateRecord(MutationRecord mRecord, boolean replace, String isoformOverridesSource, boolean reannotate) throws GenomeNexusAnnotationFailureException {
        AnnotatedRecord annotatedRecord = new AnnotatedRecord(mRecord);
        //check if record already is annotated
        if (!reannotate && !annotationNeeded(mRecord)) {
            return annotatedRecord;
        }
        String genomicLocation = parseGenomicLocationString(mRecord);
        VariantAnnotation gnResponse = null;
        try {
            gnResponse = this.apiClient.fetchVariantAnnotationByGenomicLocationGET(genomicLocation, isoformOverridesSource, "", Arrays.asList(this.enrichmentFields.split(",")));
        } catch (ApiException e) {
            // catch case where Genome Nexus Server is down
            // not logging here because if GN is down you could write out an arbitarily large logfile of "failures"
            throw new GenomeNexusAnnotationFailureException("Server error from Genome Nexus: " + genomicLocation);
        }
        // catch case where annotation fails (server will return default "failed" variant)
        if (gnResponse == null || !gnResponse.isSuccessfullyAnnotated()) {
            // only logs cases which can't be annotated due to a problem with input
            LOG.warn("Annotation failed for variant " + gnResponse.getVariant());
            throw new GenomeNexusAnnotationFailureException("Genome Nexus failed to annotate: " + gnResponse.getVariant());
        }
        return convertResponseToAnnotatedRecord(gnResponse, mRecord, replace);
    }

    public List<AnnotatedRecord> annotateRecordsUsingGET(AnnotationSummaryStatistics summaryStatistics, List<MutationRecord> mutationRecords, String isoformOverridesSource,
                                                         Boolean replace, boolean reannotate) {
        List<AnnotatedRecord> annotatedRecordsList = new ArrayList<>();
        int totalVariantsToAnnotateCount = mutationRecords.size();
        int annotatedVariantsCount = 0;
        LOG.info(String.valueOf(totalVariantsToAnnotateCount) + " records to annotate");

        for (MutationRecord mutationRecord : mutationRecords) {
            logAnnotationProgress(++annotatedVariantsCount, totalVariantsToAnnotateCount, 2000);
            // init annotated record w/o genome nexus in case server error occurs
            // if no error then annotated record will get overwritten anyway with genome nexus response
            String serverErrorMessage = "";
            AnnotatedRecord annotatedRecord = new AnnotatedRecord(mutationRecord);
            Instant startTime = Instant.now();
            try {
                annotatedRecord = annotateRecord(mutationRecord, replace, isoformOverridesSource, reannotate);
                annotatedRecord.setIfPopulated(Annotation_Status, "SUCCESS");
            } catch (HttpServerErrorException ex) {
                serverErrorMessage = "Failed to annotate variant due to internal server error";
            } catch (HttpClientErrorException ex) {
                serverErrorMessage = "Failed to annotate variant due to client error";
            } catch (HttpMessageNotReadableException ex) {
                serverErrorMessage = "Failed to annotate variant due to message not readable error";
            } catch (GenomeNexusAnnotationFailureException ex) {
                serverErrorMessage = "Failed to annotate variant due to Genome Nexus : " + ex.getMessage();
            }
            summaryStatistics.addDuration(Duration.between(startTime, Instant.now()).getSeconds());
            annotatedRecordsList.add(annotatedRecord);

            // log server failure message if applicable
            if (!serverErrorMessage.isEmpty()) {
                annotatedRecord.setIfPopulated(Annotation_Status, "FAILED");
                summaryStatistics.addFailedAnnotatedRecordDueToServer(mutationRecord, serverErrorMessage, isoformOverridesSource);
                continue;
            }
            // dont need to do anything with output, just need to call method
            summaryStatistics.isFailedAnnotatedRecord(annotatedRecord, mutationRecord, isoformOverridesSource);
        }
        return annotatedRecordsList;
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

    private AnnotationControllerApi initApiClient() {
        AnnotationControllerApi apiClient;

        if (this.genomeNexusBaseUrl != null && this.genomeNexusBaseUrl.length() > 0) {
            ApiClient client = new ApiClient();
            client.setReadTimeout(READ_TIMEOUT_OVERRIDE);
            client.setBasePath(this.genomeNexusBaseUrl);
            apiClient = new AnnotationControllerApi(client);
        } else {
            apiClient = new AnnotationControllerApi();
        }

        return apiClient;
    }

    public AnnotatedRecord convertResponseToAnnotatedRecord(VariantAnnotation gnResponse, MutationRecord mutationRecord, boolean replace) {
        // get the canonical transcript
        TranscriptConsequenceSummary canonicalTranscript = getCanonicalTranscript(gnResponse);

        String resolvedReferenceAllele = resolveReferenceAllele(gnResponse, mutationRecord);
        String resolvedTumorSeqAllele1 = mutationRecord.get(Tumor_Seq_Allele1);
        String resolvedTumorSeqAllele2 = resolveTumorSeqAllele(gnResponse, mutationRecord);

        // Copy over changes to the reference allele or tumor_seq_allele1 if
        // they were identical in the input
        String allele1 = mutationRecord.get(Tumor_Seq_Allele1);
        if (allele1.equals(mutationRecord.get(Reference_Allele))) {
            resolvedTumorSeqAllele1 = resolvedReferenceAllele;
        } else if (allele1.equals(mutationRecord.get(Tumor_Seq_Allele2))) {
            resolvedTumorSeqAllele1 = resolvedTumorSeqAllele2;
        } else {
            // TODO: it's also possible that the position has changed after
            // resolving ref+alt. Tumor seq allele1 would have to be updated
            // then as well. Kind of a corner case, but we might want to handle
            // that in some way. Discussion point: should we even allow the core
            // variant attributes (pos,ref,alt1,alt2) to be mutable?
        }

        // annotate the record
        AnnotatedRecord annotatedRecord = new AnnotatedRecord(mutationRecord);
        annotatedRecord.setIfPopulated(Hugo_Symbol, resolveHugoSymbol(canonicalTranscript, mutationRecord, replace));
        annotatedRecord.setIfPopulated(Entrez_Gene_Id, resolveEntrezGeneId(canonicalTranscript, mutationRecord, replace));
        annotatedRecord.setIfPopulated(NCBI_Build, resolveAssemblyName(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(Chromosome, resolveChromosome(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(Start_Position, resolveStart(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(End_Position, resolveEnd(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(Strand, resolveStrandSign(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(Variant_Classification, resolveVariantClassification(canonicalTranscript, mutationRecord));
        annotatedRecord.setIfPopulated(Variant_Type, resolveVariantType(gnResponse));
        annotatedRecord.setIfPopulated(Reference_Allele, resolvedReferenceAllele);
        annotatedRecord.setIfPopulated(Tumor_Seq_Allele1, resolvedTumorSeqAllele1);
        annotatedRecord.setIfPopulated(Tumor_Seq_Allele2, resolvedTumorSeqAllele2);
        annotatedRecord.setIfPopulated(dbSNP_RS, resolveDbSnpRs(gnResponse, mutationRecord));
        annotatedRecord.setIfPopulated(HGVSc, resolveHgvsc(canonicalTranscript));
        annotatedRecord.setIfPopulated(HGVSp, resolveHgvsp(canonicalTranscript));
        annotatedRecord.setIfPopulated(HGVSp_Short, resolveHgvspShort(canonicalTranscript));
        annotatedRecord.setIfPopulated(Transcript_ID, resolveTranscriptId(canonicalTranscript));
        annotatedRecord.setIfPopulated(RefSeq, resolveRefSeq(canonicalTranscript));
        annotatedRecord.setIfPopulated(Codons, resolveCodonChange(canonicalTranscript));
        annotatedRecord.setIfPopulated(Consequence, resolveConsequence(canonicalTranscript));
        annotatedRecord.setIfPopulated(Protein_position, resolveProteinPosition(canonicalTranscript, mutationRecord));
        annotatedRecord.setIfPopulated(Exon_Number, resolveExon(canonicalTranscript));

        if (enrichmentFields.contains("my_variant_info")) {
            // get the gnomad allele frequency
            AlleleFrequency alleleFrequency = getGnomadAlleleFrequency(gnResponse);
            annotatedRecord.setIfPopulated("gnomAD_AF", resolveGnomadAlleleFrequency(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_AFR_AF", resolveGnomadAlleleFrequencyAFR(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_AMR_AF", resolveGnomadAlleleFrequencyAMR(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_ASJ_AF", resolveGnomadAlleleFrequencyASJ(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_EAS_AF", resolveGnomadAlleleFrequencyEAS(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_FIN_AF", resolveGnomadAlleleFrequencyFIN(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_NFE_AF", resolveGnomadAlleleFrequencyNFE(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_OTH_AF", resolveGnomadAlleleFrequencyOTH(alleleFrequency));
            annotatedRecord.setIfPopulated("gnomAD_SAS_AF", resolveGnomadAlleleFrequencySAS(alleleFrequency));
        }
        if (enrichmentFields.contains("polyphen")) {
            annotatedRecord.setIfPopulated("Polyphen_Prediction", resolvePolyphenPrediction(canonicalTranscript));
            annotatedRecord.setIfPopulated("Polyphen_Score", resolvePolyphenScore(canonicalTranscript));
        }
        if (enrichmentFields.contains("sift")) {
            annotatedRecord.setIfPopulated("SIFT_Prediction", resolveSiftPrediction(canonicalTranscript));
            annotatedRecord.setIfPopulated("SIFT_Score", resolveSiftScore(canonicalTranscript));
        }
        if (enrichmentFields.contains("mutation_assessor")) {
            annotatedRecord.setIfPopulated("MA:FImpact", resolveMaFunctionalImpact(gnResponse));
            annotatedRecord.setIfPopulated("MA:FIS",  resolveMaFunctionalImpactScore(gnResponse));
            annotatedRecord.setIfPopulated("MA:link.MSA", resolveMaLinkMSA(gnResponse));
            annotatedRecord.setIfPopulated("MA:link.PDB", resolveMaLinkPDB(gnResponse));
        }
        if (enrichmentFields.contains("nucleotide_context")) {
            annotatedRecord.setIfPopulated("Ref_Tri", resolveRefTri(gnResponse));
            annotatedRecord.setIfPopulated("Var_Tri", resolveVarTri(gnResponse));
        }
        return annotatedRecord;
    }

    @Override
    public boolean isHgvspNullClassifications(String variantClassification) {
        return hgvspNullClassifications.contains(variantClassification);
    }

    @Override
    public String getUrlForRecord(MutationRecord record, String isoformOverridesSource) {
        String genomicLocation = parseGenomicLocationString(record);

        // TODO this is now handled by the API client, we don't really need this (keeping for logging purposes only)
        return genomeNexusBaseUrl + "annotation/genomic/" + genomicLocation + "?" + isoformQueryParameter + "=" + isoformOverridesSource + "&fields=" + enrichmentFields;
    }

    private AlleleFrequency getGnomadAlleleFrequency(VariantAnnotation gnResponse) {
        MyVariantInfoAnnotation myVariantInfoAnnotation = gnResponse.getMyVariantInfo();
        if (myVariantInfoAnnotation != null) {
            MyVariantInfo myVariantInfo = myVariantInfoAnnotation.getAnnotation();
            if (myVariantInfo != null) {
                Gnomad gnomad = myVariantInfo.getGnomadExome();
                if (gnomad != null) {
                    AlleleFrequency alleleFrequency = gnomad.getAlleleFrequency();
                    if (alleleFrequency != null) {
                        return alleleFrequency;
                    }
                }
            }
        }
        return null;
    }

    public String parseGenomicLocationString(MutationRecord record) {
        GenomicLocation genomicLocation = record.asGenomicLocation();
        return StringUtils.join(
                new String[]{genomicLocation.getChromosome(), genomicLocation.getStart().toString(), genomicLocation.getEnd().toString(), genomicLocation.getReferenceAllele(), genomicLocation.getVariantAllele()},
                ",");
    }

    private TranscriptConsequenceSummary getCanonicalTranscript(VariantAnnotation gnResponse) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getTranscriptConsequences() != null && gnResponse.getAnnotationSummary()
                .getTranscriptConsequences().size() > 0) {
            return gnResponse.getAnnotationSummary().getTranscriptConsequences().get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<AnnotatedRecord> getAnnotatedRecordsUsingPOST(AnnotationSummaryStatistics summaryStatistics, List<MutationRecord> mutationRecords, String isoformOverridesSource,
                                                              Boolean replace, boolean reannotate) {
        // this will send everything at once
        return getAnnotatedRecordsUsingPOST(summaryStatistics, mutationRecords, isoformOverridesSource, replace, mutationRecords.size(), reannotate);
    }

    @Override
    public List<AnnotatedRecord> getAnnotatedRecordsUsingPOST(AnnotationSummaryStatistics summaryStatistics, List<MutationRecord> mutationRecords, String isoformOverridesSource,
                                                              Boolean replace, Integer postIntervalSize, boolean reannotate) {
        // construct list of genomic location objects to pass to api client
        // TODO use SortedSet (or at least Set) instead?  Do we anticipate a lot of redundancy? Probably quite a bit.
        // Maybe test which is faster with a large study
        List<GenomicLocation> genomicLocations = new ArrayList<>();
        for (MutationRecord record : mutationRecords) {
            if (reannotate || annotationNeeded(record)) {
                genomicLocations.add(record.asGenomicLocation());
            }
        }
        // sort and partition the genomic locations
        List<List<GenomicLocation>> partitionedGenomicLocationList = sortAndPartitionMutationRecordsListForPOST(genomicLocations, postIntervalSize);

        Map<String, VariantAnnotation> gnResponseVariantKeyMap = new HashMap<>();
        int totalVariantsToAnnotateCount = genomicLocations.size();
        int annotatedVariantsCount = 0;
        for (List<GenomicLocation> partitionedList : partitionedGenomicLocationList) {
            List<VariantAnnotation> gnResponseList = null;
            Instant startTime = Instant.now();
            // Get annotations from Genome Nexus and log if server error (e.g VEP is down)
            try {
                gnResponseList = apiClient.fetchVariantAnnotationByGenomicLocationPOST(partitionedList, isoformOverridesSource, "",
                        Arrays.asList(this.enrichmentFields.split(",")));
            } catch (Exception e) {
                LOG.error("Annotation failed for ALL variants in this partition. " + e.getMessage());
            }
            System.out.println("POST with size of " + postIntervalSize + " took " + Duration.between(startTime, Instant.now()).getSeconds() + " sec.");
            summaryStatistics.addDuration(Duration.between(startTime, Instant.now()).getSeconds());
            // Verify annotations coming back from Genome Nexus and log annotation failures (e.g used to be 404s)
            if (gnResponseList != null) {
                for (VariantAnnotation gnResponse : gnResponseList) {
                    logAnnotationProgress(++annotatedVariantsCount, totalVariantsToAnnotateCount, postIntervalSize);
                    if (gnResponse.isSuccessfullyAnnotated()) {
                        gnResponseVariantKeyMap.put(gnResponse.getOriginalVariantQuery(), gnResponse);
                    } else {
                        LOG.warn("Annotation failed for variant " + gnResponse.getVariant());
                    }
                }
            }
        }

        List<AnnotatedRecord> annotatedRecords = new ArrayList<>();
        // loop through the original mutationRecords (in original sort order) and
        // create annotated records by merging the responses from gn with their corresponding MAF record
        for (MutationRecord mutationRecord : mutationRecords) {
            String genomicLocation = parseGenomicLocationString(mutationRecord);
            AnnotatedRecord annotatedRecord = new AnnotatedRecord(mutationRecord);
            // if not a failed annotation then convert/merge the response from gn with the maf record
            VariantAnnotation gnResponse = gnResponseVariantKeyMap.get(genomicLocation);
            if (gnResponse == null) {
                if (reannotate || annotationNeeded(mutationRecord)) {
                    // only log if record actually attempted annotation
                    annotatedRecord.setIfPopulated(Annotation_Status, "FAILED");
                    summaryStatistics.addFailedAnnotatedRecordDueToServer(mutationRecord, "Genome Nexus failed to annotate", isoformOverridesSource);
                }
            } else {
                annotatedRecord = convertResponseToAnnotatedRecord(gnResponseVariantKeyMap.get(genomicLocation), mutationRecord, replace);
                annotatedRecord.setIfPopulated(Annotation_Status, "SUCCESS");
                // annotation status indicates if a response was returned from GN, not whether the annotation is considered valid or not
                if (summaryStatistics.isFailedAnnotatedRecord(annotatedRecord, mutationRecord, isoformOverridesSource)) {
                    // Log case where annotation comes back from Genome Nexus but still invalid (e.g null variant classification)
                    LOG.warn("Annotated record is invalid for variant " + gnResponseVariantKeyMap.get(genomicLocation).getVariant());
                }
            }
            annotatedRecords.add(annotatedRecord);
        }
        return annotatedRecords;
    }

    private List<List<GenomicLocation>> sortAndPartitionMutationRecordsListForPOST(List<GenomicLocation> genomicLocations, Integer postIntervalSize) {
        // sort
        List<GenomicLocation> sortedGenomicLocations = new ArrayList<>(genomicLocations);
        Collections.sort(sortedGenomicLocations, GENOMIC_LOCATION_COMPARATOR);
        // partition
        int start = 0;
        int end = postIntervalSize;
        List<List<GenomicLocation>> genomicLocationPartitionedLists = new ArrayList<>();
        while (end <= sortedGenomicLocations.size()) {
            genomicLocationPartitionedLists.add(sortedGenomicLocations.subList(start, end));
            start = end;
            end = start + postIntervalSize;
        }
        if (start < sortedGenomicLocations.size()) {
            genomicLocationPartitionedLists.add(sortedGenomicLocations.subList(start, sortedGenomicLocations.size()));
        }
        return genomicLocationPartitionedLists;
    }

    private void logAnnotationProgress(Integer annotatedVariantsCount, Integer totalVariantsToAnnotateCount, Integer intervalSize) {
        if (annotatedVariantsCount % intervalSize == 0 || Objects.equals(annotatedVariantsCount, totalVariantsToAnnotateCount)) {
            LOG.info("\tOn record " + String.valueOf(annotatedVariantsCount) + " out of " + String.valueOf(totalVariantsToAnnotateCount) + ", annotation " + String.valueOf(
                    (int) (((annotatedVariantsCount * 1.0) / totalVariantsToAnnotateCount) * 100)) + "% complete");
        }
    }
}
