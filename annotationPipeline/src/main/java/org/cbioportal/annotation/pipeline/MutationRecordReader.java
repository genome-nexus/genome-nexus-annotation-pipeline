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

package org.cbioportal.annotation.pipeline;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.models.*;
import org.cbioportal.annotator.Annotator;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;
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

    private int failedAnnotations;
    private int failedNullHgvspAnnotations;
    private int failedMitochondrialAnnotations;

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

        for(MutationRecord record : mutationRecords) {
            // save variant details for logging
            String variantDetails = "(sampleId,chr,start,end,ref,alt,url)= (" + record.getTUMOR_SAMPLE_BARCODE() + "," +  record.getCHROMOSOME() + "," + record.getSTART_POSITION() + ","
                    + record.getEND_POSITION() + "," + record.getREFERENCE_ALLELE() + "," + record.getTUMOR_SEQ_ALLELE2() + "," + annotator.getUrlForRecord(record, isoformOverride) + ")";

            // init annotated record w/o genome nexus in case server error occurs
            // if no error then annotated record will get overwritten anyway with genome nexus response
            AnnotatedRecord annotatedRecord = new AnnotatedRecord(record);
            try {
                annotatedRecord = annotator.annotateRecord(record, replace, isoformOverride, true);
            }
            catch (HttpServerErrorException ex) {
                String reasonFailed = "Failed to annotate variant due to internal server error";
                LOG.warn(reasonFailed + ": " + variantDetails);
                updateErrorMessages(record, record.getVARIANT_CLASSIFICATION(), annotator.getUrlForRecord(record, isoformOverride), reasonFailed);
            }
            if (annotatedRecord.getHGVSC().isEmpty() && annotatedRecord.getHGVSP().isEmpty()) {
                String reasonFailed = "";
                if (annotator.isHgvspNullClassifications(annotatedRecord.getVARIANT_CLASSIFICATION())) {
                    failedNullHgvspAnnotations++;
                    reasonFailed = "Ignoring record with HGVSp null classification '" + annotatedRecord.getVARIANT_CLASSIFICATION() + "'";
                }
                else if (annotatedRecord.getCHROMOSOME().equals("M")) {
                    failedMitochondrialAnnotations++;
                    reasonFailed = "Mitochondrial variants are not supported at this time for annotation - skipping variant";
                }
                else {
                    reasonFailed = "Failed to annotate variant";                    
                }
                failedAnnotations++;
                LOG.info(reasonFailed + ": " + variantDetails);                
                updateErrorMessages(record, annotatedRecord.getVARIANT_CLASSIFICATION(), annotator.getUrlForRecord(record, isoformOverride), reasonFailed);
            }
            annotatedRecords.add(annotatedRecord);
            header.addAll(annotatedRecord.getHeaderWithAdditionalFields());
        }
        // log number of records that failed annotations
        LOG.info("Total records that failed annotation: " + failedAnnotations);
        LOG.info("# records with HGVSp null variant classification: " + failedNullHgvspAnnotations);
        LOG.info("# records with Mitochondrial variants: " + failedMitochondrialAnnotations);
        errorMessages.add("Total records that failed annotation: " + failedAnnotations);
        errorMessages.add("# records with HGVSp null variant classification: " + failedNullHgvspAnnotations);
        errorMessages.add("# records with Mitochondrial variants: " + failedMitochondrialAnnotations);
        saveErrorMessagesToFile(errorMessages);

        List<String> full_header = new ArrayList(header);
        ec.put("mutation_header", full_header);
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
    
    private void updateErrorMessages(MutationRecord record, String variantClassification, String url, String reasonFailed) {
        List<String> msg = Arrays.asList(new String[]{record.getTUMOR_SAMPLE_BARCODE(), record.getCHROMOSOME(),
                                record.getSTART_POSITION(), record.getEND_POSITION(), record.getREFERENCE_ALLELE(),
                                record.getTUMOR_SEQ_ALLELE2(), record.getVARIANT_CLASSIFICATION(), reasonFailed, url});
        errorMessages.add(StringUtils.join(msg, "\t"));
    }

    private void saveErrorMessagesToFile(List<String> errorMessages) {
        if (errorReportLocation == null) return;
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
