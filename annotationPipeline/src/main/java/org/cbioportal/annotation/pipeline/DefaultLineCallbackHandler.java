package org.cbioportal.annotation.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.util.*;

/**
 *
 * @author Mete Ozguz
 */
public class DefaultLineCallbackHandler implements LineCallbackHandler {

    private static final String[] requiredNames = {"Chromosome", "Start_Position", "End_Position", "Reference_Allele"};
    private final Logger LOG = LoggerFactory.getLogger(DefaultLineCallbackHandler.class);
    private final DelimitedLineTokenizer tokenizer;
    private final List<String> inputFileHeaders;

    /**
     *
     * @param tokenizer Reference for the DefaultLineMapper's LineTokenizer. Non null.
     * @param inputFileHeaders Reference for the header names which will be used for 'minimal' file format. Non null.
     */
    public DefaultLineCallbackHandler(DelimitedLineTokenizer tokenizer, List<String> inputFileHeaders) {
        this.tokenizer = tokenizer;
        this.inputFileHeaders = inputFileHeaders;
    }

    /**
     * Parser and validator of tab separated header names
     * Should be invoked only for the header line!
     *
     * @param line
     */
    @Override
    public void handleLine(String line) {
        String[] names = line.split("\t");
        Set<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        for (String requiredName : requiredNames) {
            if (!nameSet.contains(requiredName)) {
                String errorMessage = "Input file does not contain all the necessary fields. Missing field: " + requiredName;
                LOG.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }
        // Presence of Tumor_Seq_Allele1 or Tumor_Seq_Allele2
        if (!nameSet.contains("Tumor_Seq_Allele1") && !nameSet.contains("Tumor_Seq_Allele2")) {
            String errorMessage = "Input file needs contain at least one of these fields: Tumor_Seq_Allele1, Tumor_Seq_Allele2";
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        Collections.addAll(inputFileHeaders, names);
        tokenizer.setNames(names); // do not use sorted names here
    }
}
