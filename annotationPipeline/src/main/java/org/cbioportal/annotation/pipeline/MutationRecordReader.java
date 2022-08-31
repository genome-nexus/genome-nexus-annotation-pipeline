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

import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.internal.AnnotationSummaryStatistics;
import org.cbioportal.format.ExtendedMafFormat;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * <pre>
 * Reader for <a href="https://docs.gdc.cancer.gov/Data/File_Formats/MAF_Format/">GDC MAF Format v.1.0.0</a>
 *
 * Following columns are required:
 *  Chromosome, Start_Position, End_Position, Reference_Allele, Tumor_Seq_Allele1
 * </pre>
 *
 * @author Mete Ozguz
 * @author Zachary Heins
 */
public class MutationRecordReader implements ItemStreamReader<AnnotatedRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(MutationRecordReader.class);
    @Autowired
    Annotator annotator;
    private List<String> inputHeaders = new ArrayList<>();
    @Value("#{jobParameters[filename]}")
    private String filename;
    @Value("#{jobParameters[replace]}")
    private boolean replace;
    @Value("#{jobParameters[isoformOverride]}")
    private String isoformOverride;
    @Value("#{jobParameters[errorReportLocation] ?: ''}")
    private String errorReportLocation;
    @Value("#{jobParameters[postIntervalSize] ?: '100'}")
    private Integer postIntervalSize;
    @Value("#{jobParameters[outputFormat]}")
    private String outputFormat;
    private AnnotationSummaryStatistics summaryStatistics;
    private List<AnnotatedRecord> allAnnotatedRecords = new ArrayList<>();

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        summaryStatistics = new AnnotationSummaryStatistics(annotator);
        String genomeNexusVersion = annotator.getVersion();
        Set<String> outputHeaders = new LinkedHashSet<>();
        processComments(ec, genomeNexusVersion);
        Instant start = Instant.now();
        List<MutationRecord> mutationRecords = loadMutationRecordsFromMaf();
        System.out.println("loadMutationRecordsFromMaf: " + Duration.between(start, Instant.now()).getSeconds() + "sec");
        if (!mutationRecords.isEmpty()) {
            if (postIntervalSize > 1) {
                allAnnotatedRecords = annotator.getAnnotatedRecordsUsingPOST(summaryStatistics, mutationRecords, isoformOverride, replace, postIntervalSize, true);
            } else {
                allAnnotatedRecords = annotator.annotateRecordsUsingGET(summaryStatistics, mutationRecords, isoformOverride, replace, true);
            }
            // if output-format option is supplied, we only need to convert its data into header
            if (outputFormat != null) {
                if ("extended".equals(outputFormat)) {
                    outputHeaders.addAll(ExtendedMafFormat.headers);
                } else if ("minimal".equals(outputFormat)) {
                    outputHeaders.addAll(inputHeaders);
                } else {
                    outputHeaders.addAll(Arrays.asList(outputFormat.split(",")));
                }
                // extra headers should go in the back alphabetically for these options
                if ("extended".equals(outputFormat) || "minimal".equals(outputFormat)) {
                    Set<String> sortedAllHeaders = new TreeSet<>();
                    sortedAllHeaders.addAll(AnnotatedRecord.CLASS_WIDE_HEADERS);
                    outputHeaders.addAll(sortedAllHeaders);
                }
            } else {
                outputHeaders.addAll(AnnotatedRecord.CLASS_WIDE_HEADERS);
            }
            // we want to put Annotation_Status to the end
            outputHeaders.remove("Annotation_Status");
            outputHeaders.add("Annotation_Status");
            ec.put("mutation_header", new ArrayList(outputHeaders));
            summaryStatistics.printSummaryStatistics();
            summaryStatistics.saveErrorMessagesToFile(errorReportLocation);
        } else {
            System.out.println("It seems that the input mutation file does not contain any mutation records. Exiting without writing an output file.");
            LOG.warn("Did not extract any records from the MAF, nothing to process - ending annotation job...");
        }
        // always add size of "allAnnotatedRecords" to execution context
        // this is used to determine whether an output file should be generated or not
        // to prevent writing a file without any annotated records
        ec.put("records_to_write_count", allAnnotatedRecords.size());
    }

    private List<MutationRecord> loadMutationRecordsFromMaf() {
        List<MutationRecord> mutationRecords = new ArrayList<>();
        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filename));
        PassThroughLineMapper mapper = new PassThroughLineMapper();
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.setSkippedLinesCallback(new DefaultLineCallbackHandler(inputHeaders));
        reader.open(new ExecutionContext());
        LOG.info("Loading records from: " + filename);
        MutationRecord.init(inputHeaders); // inputFileHeaders gets populated when DefaultLineCallbackHandler invoked
        String line;
        try {
            while ((line = reader.read()) != null) {
                mutationRecords.add(new MutationRecord(line));
            }
        } catch (Exception e) {
            throw new ItemStreamException(e);
        }
        reader.close();
        LOG.info("Loaded " + mutationRecords.size() + " records from: " + filename);
        return mutationRecords;
    }


    private void logAnnotationProgress(Integer annotatedVariantsCount, Integer totalVariantsToAnnotateCount, Integer intervalSize) {
        if (annotatedVariantsCount % intervalSize == 0 || Objects.equals(annotatedVariantsCount, totalVariantsToAnnotateCount)) {
            LOG.info("\tOn record " + String.valueOf(annotatedVariantsCount) + " out of " + String.valueOf(totalVariantsToAnnotateCount) +
                    ", annotation " + String.valueOf((int) (((annotatedVariantsCount * 1.0) / totalVariantsToAnnotateCount) * 100)) + "% complete");
        }
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

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
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    // do not duplicate comments in header for version or isoform used
                    if (!line.startsWith("#genome_nexus_version") && !line.startsWith("#isoform")) {
                        comments.add(line);
                    }
                } else {
                    // no more comments, go on processing
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new ItemStreamException(e);
        }

        // Add comments to the config for the writer to access later
        ec.put("commentLines", comments);
    }

}
