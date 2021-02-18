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
import org.apache.log4j.Logger;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.internal.AnnotationSummaryStatistics;
import org.cbioportal.models.*;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;

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

    private static final Logger LOG = Logger.getLogger(MutationRecordReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        this.summaryStatistics = new AnnotationSummaryStatistics(annotator);
        String genomeNexusVersion = annotator.getVersion();

        processComments(ec, genomeNexusVersion);
        List<MutationRecord> mutationRecords = loadMutationRecordsFromMaf();
        if (!mutationRecords.isEmpty()) {
            if (postIntervalSize > 0) {
                this.allAnnotatedRecords = annotator.getAnnotatedRecordsUsingPOST(summaryStatistics, mutationRecords, isoformOverride, replace, postIntervalSize, true);
            } else {
                this.allAnnotatedRecords = annotator.annotateRecordsUsingGET(summaryStatistics, mutationRecords, isoformOverride, replace, true);
            }
            for (AnnotatedRecord ar : this.allAnnotatedRecords) {
                header.addAll(ar.getHeaderWithAdditionalFields());
            }
            // add 'Annotation_Status' to header if not already present
            if (!header.contains("Annotation_Status")) {
                header.add("Annotation_Status");
            }
            ec.put("mutation_header", new ArrayList(header));
            summaryStatistics.printSummaryStatistics();
            if (errorReportLocation != null) {
                summaryStatistics.saveErrorMessagesToFile(errorReportLocation);
            }
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
