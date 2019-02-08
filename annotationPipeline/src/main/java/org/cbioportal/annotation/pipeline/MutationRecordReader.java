/*
 * Copyright (c) 2016 - 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.annotation.pipeline;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.GenomeNexusAnnotationFailureException;
import org.cbioportal.models.*;
import org.mskcc.cbio.maf.MafUtil;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author Zachary Heins
 */
public class MutationRecordReader  implements ItemStreamReader<AnnotatedRecord>{
    @Value("#{jobParameters[filename]}")
    private String filename;

    @Value("#{jobParameters[replace]}")
    private boolean replace;

    @Value("#{jobParameters[isoformOverride]}")
    private String isoformOverride;

    @Value("#{jobParameters[errorReportLocation]}")
    private String errorReportLocation;
    
    @Value("#{jobParameters[verbose]}")
    private boolean verbose;

    private int failedAnnotations;
    private int failedServerAnnotations;
    private int failedNullHgvspAnnotations;
    private int snpAndIndelVariants;

    private List<MutationRecord> mutationRecords = new ArrayList<>();
    private List<AnnotatedRecord> annotatedRecords = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();
    private Set<String> header = new LinkedHashSet<>();
    private List<String> errorHeader = Arrays.asList(new String[]{"SAMPLE_ID", "CHR", "START",
                                            "END", "REF", "ALT", "VARIANT_CLASSIFICATION", 
                                            "FAILURE_REASON", "URL"});

    @Autowired
    Annotator annotator;

    private static final Log LOG = LogFactory.getLog(MutationRecordReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {

        processComments(ec);

        FlatFileItemReader<MutationRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filename));
        DefaultLineMapper<MutationRecord> mapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("\t");
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new MutationFieldSetMapper());
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.setSkippedLinesCallback(new LineCallbackHandler() {
                @Override
                public void handleLine(String line) {
                    tokenizer.setNames(line.split("\t"));
                }
        });
        reader.open(ec);

        LOG.info("Loading records from: " + filename);
        MutationRecord mutationRecord;
        try {
            while((mutationRecord = reader.read()) != null) {
                mutationRecords.add(mutationRecord);
            }
        }
        catch(Exception e) {
            throw new ItemStreamException(e);
        }
        reader.close();

        int variantsToAnnotateCount = mutationRecords.size();
        int annotatedVariantsCount = 0;
        LOG.info(String.valueOf(variantsToAnnotateCount) + " records to annotate");
        for (MutationRecord record : mutationRecords) {
            annotatedVariantsCount++;
            if (annotatedVariantsCount % 2000 == 0) {
                LOG.info("\tOn record " + String.valueOf(annotatedVariantsCount) + " out of " + String.valueOf(variantsToAnnotateCount) + ", annotation " + String.valueOf((int)(((annotatedVariantsCount * 1.0)/variantsToAnnotateCount) * 100)) + "% complete");
            }
            // save variant details for logging
            String variantDetails = "(sampleId,chr,start,end,ref,alt,url)= (" + record.getTUMOR_SAMPLE_BARCODE() + "," +  record.getCHROMOSOME() + "," + record.getSTART_POSITION() + ","
                    + record.getEND_POSITION() + "," + record.getREFERENCE_ALLELE() + "," + record.getTUMOR_SEQ_ALLELE2() + "," + annotator.getUrlForRecord(record, isoformOverride) + ")";

            // init annotated record w/o genome nexus in case server error occurs
            // if no error then annotated record will get overwritten anyway with genome nexus response
            String serverErrorMessage = "";
            AnnotatedRecord annotatedRecord = new AnnotatedRecord(record);
            try {
                annotatedRecord = annotator.annotateRecord(record, replace, isoformOverride, true);
            }
            catch (HttpServerErrorException ex) {
                serverErrorMessage = "Failed to annotate variant due to internal server error";
            }
            catch (HttpClientErrorException ex) {
                serverErrorMessage = "Failed to annotate variant due to client error";
            }
            catch (HttpMessageNotReadableException ex) {
                serverErrorMessage = "Failed to annotate variant due to message not readable error";
            }
            catch (GenomeNexusAnnotationFailureException ex) {
                serverErrorMessage = "Failed to annotate variant due to Genome Nexus : " + ex.getMessage();
            }
            annotatedRecords.add(annotatedRecord);
            header.addAll(annotatedRecord.getHeaderWithAdditionalFields());
            
            // log server failure message if applicable
            if (!serverErrorMessage.isEmpty()) {
                LOG.warn(serverErrorMessage);
                failedAnnotations++;
                failedServerAnnotations++;
                if (errorReportLocation != null) updateErrorMessages(record, record.getVARIANT_CLASSIFICATION(), annotator.getUrlForRecord(record, isoformOverride), serverErrorMessage);
                continue;
            }
            String annotationErrorMessage = "";
            if (MafUtil.variantContainsAmbiguousTumorSeqAllele(record.getREFERENCE_ALLELE(), record.getTUMOR_SEQ_ALLELE1(), record.getTUMOR_SEQ_ALLELE2())) {
                snpAndIndelVariants++;
                annotationErrorMessage = "Record contains ambiguous SNP and INDEL allele change - SNP allele will be used";
            }
            if (annotatedRecord.getHGVSC().isEmpty() && annotatedRecord.getHGVSP().isEmpty()) {
                if (annotator.isHgvspNullClassifications(annotatedRecord.getVARIANT_CLASSIFICATION())) {
                    failedNullHgvspAnnotations++;
                    annotationErrorMessage = "Ignoring record with HGVSp null classification '" + annotatedRecord.getVARIANT_CLASSIFICATION() + "'";
                }
                else {
                    annotationErrorMessage = "Failed to annotate variant";                    
                }
                failedAnnotations++;
            }
            if (!annotationErrorMessage.isEmpty()) {
                if (verbose) LOG.info(annotationErrorMessage + ": " + variantDetails);
                if (errorReportLocation != null) updateErrorMessages(record, annotatedRecord.getVARIANT_CLASSIFICATION(), annotator.getUrlForRecord(record, isoformOverride), annotationErrorMessage);                
            }
        }
        // print summary statistics and save error messages to file if applicable
        printSummaryStatistics(failedAnnotations, failedNullHgvspAnnotations, snpAndIndelVariants, failedServerAnnotations);
        if (errorReportLocation != null) {
            saveErrorMessagesToFile(errorMessages);
        }
        ec.put("mutation_header", new ArrayList(header));
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public AnnotatedRecord read() throws Exception {
        if (!annotatedRecords.isEmpty()) {
            return annotatedRecords.remove(0);
        }
        return null;
    }
    
    private void printSummaryStatistics(Integer failedAnnotations, Integer failedNullHgvspAnnotations, Integer snpAndIndelVariants, Integer failedServerAnnotations) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nAnnotation Summary:")
                .append("\n\tRecords with ambiguous SNP and INDEL allele changes:  ").append(snpAndIndelVariants);
        if (failedAnnotations > 0) {
                builder.append("\n\n\tFailed annotations summary:  ").append(failedAnnotations).append(" total failed annotations")
                .append("\n\t\tRecords with HGVSp null variant classification:  ").append(failedNullHgvspAnnotations)
                .append("\n\t\tRecords that failed due to server issue: ").append(failedServerAnnotations);
        }
        else {
            builder.append("\n\tAll variants annotated successfully without failures!");
        }
        builder.append("\n\n");
        System.out.print(builder.toString());
    }
    
    private void updateErrorMessages(MutationRecord record, String variantClassification, String url, String errorMessage) {
        List<String> msg = Arrays.asList(new String[]{record.getTUMOR_SAMPLE_BARCODE(), record.getCHROMOSOME(),
                                record.getSTART_POSITION(), record.getEND_POSITION(), record.getREFERENCE_ALLELE(),
                                record.getTUMOR_SEQ_ALLELE1(), record.getTUMOR_SEQ_ALLELE2(), variantClassification, 
                                errorMessage, url});
        errorMessages.add(StringUtils.join(msg, "\t"));
    }

    private void saveErrorMessagesToFile(List<String> errorMessages) {
        if (errorMessages.isEmpty()) {
            LOG.info("No errors to write - error report will not be generated.");
            return;
        }
        try {
            FileWriter writer = new FileWriter(errorReportLocation);
            writer.write(StringUtils.join(errorHeader, "\t") + "\n");
            writer.write(StringUtils.join(errorMessages, "\n"));
            writer.close();
        }
        catch (IOException e) {
            LOG.error("Unable to save error messages to file!");
            e.printStackTrace();
        }
    }

    private void processComments(ExecutionContext ec) {
        List<String> comments = new ArrayList<>();
        String sequencedSamples = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    comments.add(line);
                }
                else {
                    // no more comments, go on processing
                    break;
                }
            }
            reader.close();
        }
        catch (Exception e) {
            throw new ItemStreamException(e);
        }

        // Add comments to the config for the writer to access later
        ec.put("commentLines", comments);
    }

}
