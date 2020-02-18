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

package org.cbioportal.annotation.pipeline;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.annotator.internal.AnnotationSummaryStatistics;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.GenomeNexusAnnotationFailureException;
import org.cbioportal.models.*;
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
public class MutationRecordReader implements ItemStreamReader<AnnotatedRecord> {

    @Value("#{jobParameters[filename]}")
    private String filename;

    @Value("#{jobParameters[replace]}")
    private boolean replace;

    @Value("#{jobParameters[isoformOverride]}")
    private String isoformOverride;

    @Value("#{jobParameters[errorReportLocation]}")
    private String errorReportLocation;

    @Value("#{jobParameters[postIntervalSize]}")
    private Integer postIntervalSize;

    private AnnotationSummaryStatistics summaryStatistics;
    private List<AnnotatedRecord> allAnnotatedRecords = new ArrayList<>();
    private Set<String> header = new LinkedHashSet<>();

    @Autowired
    Annotator annotator;

    private static final Log LOG = LogFactory.getLog(MutationRecordReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        this.summaryStatistics = new AnnotationSummaryStatistics(annotator);
        String genomeNexusVersion = annotator.getVersion();

        processComments(ec, genomeNexusVersion);
        List<MutationRecord> mutationRecords = loadMutationRecordsFromMaf();
        System.out.println("In MUTATIONRECORDREADER WOWZERS");
        if (postIntervalSize > 0) {
            try {
                System.out.println("Guess we're POST-ing tonight bois");
                this.allAnnotatedRecords = annotator.getAnnotatedRecordsUsingPOST(summaryStatistics, mutationRecords, isoformOverride, replace, postIntervalSize);
                // TODO this is an extra loop we previously did not have to do, can GenomeNexuxImpl do this?  do other clients need it?
                for (AnnotatedRecord ar : this.allAnnotatedRecords) {
                    header.addAll(ar.getHeaderWithAdditionalFields());
                }
                System.out.println("We now have " + this.allAnnotatedRecords.size() + " annotated records");
            } catch (Exception ex) {
                LOG.error("ERROR ANNOTATING WITH POST REQUESTS");
                throw new RuntimeException(ex);
            }
        } else {
            this.allAnnotatedRecords = annotateRecordsWithGET(ec, mutationRecords);
        }
        ec.put("mutation_header", new ArrayList(header));
        summaryStatistics.printSummaryStatistics();
        if (errorReportLocation != null) {
            summaryStatistics.saveErrorMessagesToFile(errorReportLocation);
        }
    }

    private List<MutationRecord> loadMutationRecordsFromMaf() {
        List<MutationRecord> mutationRecords = new ArrayList<>();
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
        reader.open(new ExecutionContext());
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
        LOG.info("Loaded " + String.valueOf(mutationRecords.size()) + " records from: " + filename);
        return mutationRecords;
    }

    private List<AnnotatedRecord> annotateRecordsWithGET(ExecutionContext ec, List<MutationRecord> mutationRecords) {
        List<AnnotatedRecord> annotatedRecordsList = new ArrayList<>();
        int totalVariantsToAnnotateCount = mutationRecords.size();
        int annotatedVariantsCount = 0;
        LOG.info(String.valueOf(totalVariantsToAnnotateCount) + " records to annotate");
        for (MutationRecord record : mutationRecords) {
            logAnnotationProgress(++annotatedVariantsCount, totalVariantsToAnnotateCount, 2000);
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
            annotatedRecordsList.add(annotatedRecord);
            header.addAll(annotatedRecord.getHeaderWithAdditionalFields());

            // log server failure message if applicable
            if (!serverErrorMessage.isEmpty()) {
                summaryStatistics.addFailedAnnotatedRecordDueToServer(record, serverErrorMessage, isoformOverride);
                continue;
            }
            // dont need to do anything with output, just need to call method
            summaryStatistics.isFailedAnnotatedRecord(annotatedRecord, record, isoformOverride);
        }
        return annotatedRecordsList;
    }

    private void logAnnotationProgress(Integer annotatedVariantsCount, Integer totalVariantsToAnnotateCount, Integer intervalSize) {
        if (annotatedVariantsCount % intervalSize == 0 || Objects.equals(annotatedVariantsCount, totalVariantsToAnnotateCount)) {
                LOG.info("\tOn record " + String.valueOf(annotatedVariantsCount) + " out of " + String.valueOf(totalVariantsToAnnotateCount) +
                        ", annotation " + String.valueOf((int)(((annotatedVariantsCount * 1.0)/totalVariantsToAnnotateCount) * 100)) + "% complete");
        }
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public AnnotatedRecord read() throws Exception {
        if (!allAnnotatedRecords.isEmpty()) {
            return allAnnotatedRecords.remove(0);
        }
        return null;
    }

    private void processComments(ExecutionContext ec, String genomeNexusVersion) {
        List<String> comments = new ArrayList<>();
        comments.add("#genome_nexus_version: " + genomeNexusVersion);
        comments.add("#isoform: " + isoformOverride);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    // do not duplicate comments in header for version or isoform used
                    if (!line.startsWith("#genome_nexus_version") && !line.startsWith("#isoform")) {
                        comments.add(line);
                    }
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
