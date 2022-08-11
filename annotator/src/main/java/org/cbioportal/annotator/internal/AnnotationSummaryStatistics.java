/*
 * Copyright (c) 2019 Memorial Sloan-Kettering Cancer Center.
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

import org.cbioportal.annotator.Annotator;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.mskcc.cbio.maf.MafUtil;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ochoaa
 */
public class AnnotationSummaryStatistics {
private final List<String> ERROR_FILE_HEADER = Arrays.asList("SAMPLE_ID", "CHR", "START",
        "END", "REF", "ALT", "VARIANT_CLASSIFICATION",
        "FAILURE_REASON", "URL");
    private final String AMBIGUOUS_ALLELE_ERROR_MESSAGE = "Record contains ambiguous SNP and INDEL allele change - SNP allele will be used";
    private final String NULL_VAR_CLASSIFICATION_ERROR_MESSGAE = "Record contains null HGVSp variant classification";
    private final String UNKNOWN_ANNOTATION_ERROR_MESSAGE = "Failed to annotate variant";
    private final Instant initTime = Instant.now();

    private Annotator annotator;
    private Integer totalFailedAnnotatedRecords;
    private Integer ambiguousTumorSeqAlleleRecords;
    private Integer nullVariantClassificationRecords;
    private Integer otherFailedAnnotatedRecords;
    private List<MutationRecord> failedAnnotatedRecords;
    private List<String> failedAnnotatedRecordsErrorMessages;
    private final List<Long> durations = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationSummaryStatistics.class);

    public AnnotationSummaryStatistics(Annotator annotator) {
        this.annotator = annotator;
        this.totalFailedAnnotatedRecords = 0;
        this.ambiguousTumorSeqAlleleRecords = 0;
        this.nullVariantClassificationRecords = 0;
        this.otherFailedAnnotatedRecords = 0;
        this.failedAnnotatedRecords = new ArrayList<>();
        this.failedAnnotatedRecordsErrorMessages = new ArrayList<>();
    }

    /**
     * POST and GET are not used together... if this changes, you might want to change this method too
     * @param duration Duration of the POST/GET operation in seconds
     */
    public void addDuration(Long duration) {
        durations.add(duration);
    }

    /**
     *
     * @return The average response time with 3 digits precision
     */
    public String averageResponseTime() {
        if(durations.size() == 0) {
            return "0.000";
        }
        return String.format("%.3f", durations.stream().mapToLong(Long::longValue).average().getAsDouble());
    }

    /**
     *
     * @return The total response time
     */
    public String totalResponseTime() {
        if(durations.size() == 0) {
            return "0";
        }
        return String.valueOf(durations.stream().mapToLong(Long::longValue).sum());
    }

    /**
     * This class used with its object and its initialization is fairly at an early stage soo...
     * Its initialization used as the start of the runtime.
     * @return The total run time
     */
    public String totalRunTime() {
        return String.valueOf(Duration.between(initTime, Instant.now()).getSeconds());
    }

    public void addFailedAnnotatedRecordDueToServer(MutationRecord record, String serverErrorMessage, String isoformOverride) {
        failedAnnotatedRecords.add(record);
        failedAnnotatedRecordsErrorMessages.add(constructErrorMessageFromRecord(record,
                record.getVARIANT_CLASSIFICATION(),
                serverErrorMessage,
                annotator.getUrlForRecord(record, isoformOverride))
        );
        this.totalFailedAnnotatedRecords++;
        this.otherFailedAnnotatedRecords++;
    }

    public Boolean isFailedAnnotatedRecord(AnnotatedRecord annotatedRecord, MutationRecord record, String isoformOverride) {
        Boolean failedAnnotation = Boolean.FALSE;
        if (MafUtil.variantContainsAmbiguousTumorSeqAllele(record.getREFERENCE_ALLELE(),
                record.getTUMOR_SEQ_ALLELE1(), record.getTUMOR_SEQ_ALLELE2())) {
            this.ambiguousTumorSeqAlleleRecords++;
            this.failedAnnotatedRecordsErrorMessages.add(
                    constructErrorMessageFromRecord(record,
                            annotatedRecord.getVARIANT_CLASSIFICATION(),
                            AMBIGUOUS_ALLELE_ERROR_MESSAGE,
                            annotator.getUrlForRecord(record, isoformOverride))
            );
            failedAnnotation = Boolean.TRUE;

        }
        if (annotatedRecord.getHGVSC().isEmpty() && annotatedRecord.getHGVSP().isEmpty()) {
            if (annotator.isHgvspNullClassifications(annotatedRecord.getVARIANT_CLASSIFICATION())) {
                this.nullVariantClassificationRecords++;
                this.failedAnnotatedRecordsErrorMessages.add(
                        constructErrorMessageFromRecord(record,
                                annotatedRecord.getVARIANT_CLASSIFICATION(),
                                NULL_VAR_CLASSIFICATION_ERROR_MESSGAE,
                                annotator.getUrlForRecord(record, isoformOverride))
                );
                failedAnnotation = Boolean.TRUE;
            } else {
                this.otherFailedAnnotatedRecords++;
                this.failedAnnotatedRecordsErrorMessages.add(
                        constructErrorMessageFromRecord(record,
                                record.getVARIANT_CLASSIFICATION(),
                                UNKNOWN_ANNOTATION_ERROR_MESSAGE,
                                annotator.getUrlForRecord(record, isoformOverride))
                );
                failedAnnotation = Boolean.TRUE;
            }
        }
        if (failedAnnotation) {
            this.failedAnnotatedRecords.add(record);
            this.totalFailedAnnotatedRecords++;
        }
        return failedAnnotation;
    }

    public void printSummaryStatistics() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nAnnotation Summary:")
                .append("\n\tRecords with ambiguous SNP and INDEL allele changes:  ").append(ambiguousTumorSeqAlleleRecords);
        if (totalFailedAnnotatedRecords > 0) {
                builder.append("\n\n\tFailed annotations summary:  ").append(totalFailedAnnotatedRecords).append(" total failed annotations")
                .append("\n\t\tRecords with HGVSp null variant classification:  ").append(nullVariantClassificationRecords)
                .append("\n\t\tRecords that failed due to other unknown reason: ").append(otherFailedAnnotatedRecords);
        } else {
            builder.append("\n\tAll variants annotated successfully without failures!");
        }
        builder.append("\n\n\tAverage Response Time:  ").append(averageResponseTime()).append(" sec.");
        builder.append("\n\t  Total Response Time:  ").append(totalResponseTime()).append(" sec.");
        builder.append("\n\t       Total Run Time:  ").append(totalRunTime()).append(" sec.");
        builder.append("\n\n");
        System.out.print(builder);
    }

    private String constructErrorMessageFromRecord(MutationRecord record, String variantClassification, String errorMessage, String url) {
        List<String> msg = Arrays.asList(record.getTUMOR_SAMPLE_BARCODE(), record.getCHROMOSOME(),
                record.getSTART_POSITION(), record.getEND_POSITION(), record.getREFERENCE_ALLELE(),
                record.getTUMOR_SEQ_ALLELE1(), record.getTUMOR_SEQ_ALLELE2(), variantClassification,
                errorMessage, url);
        return StringUtils.join(msg, "\t");
    }

    public void saveErrorMessagesToFile(String filename) {
        if(filename == null || filename.isEmpty()) return;
        if (this.failedAnnotatedRecordsErrorMessages.isEmpty()) {
            LOG.info("No errors to write - error report will not be generated.");
        }
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(StringUtils.join(ERROR_FILE_HEADER, "\t") + "\n");
            writer.write(StringUtils.join(this.failedAnnotatedRecordsErrorMessages, "\n"));
            writer.close();
        } catch (IOException e) {
            LOG.error("Unable to save error messages to file!");
            e.printStackTrace();
        }
    }
}
