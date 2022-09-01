package org.cbioportal.annotation.pipeline;

import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.util.*;

/**
 *
 * @author Mete Ozguz
 */
public class DefaultLineCallbackHandler implements LineCallbackHandler {

    private static final String[] requiredNames = {"Chromosome", "Start_Position", "End_Position", "Reference_Allele"};
    private final DelimitedLineTokenizer tokenizer;
    private static List<String> inputFileHeaders;

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
     * Header line should include following names: "Chromosome", "Start_Position", "End_Position", "Reference_Allele"
     * Header line should either include "Tumor_Seq_Allele1" or "Tumor_Seq_Allele2"
     *
     * @param line The line which contains tab separated headers. It shouldn't be null.
     * @param tokenizer It can be null if it won't be used for spring batch file operations later on.
     */
    public static void checkHeader(String line, DelimitedLineTokenizer tokenizer) {
        String[] names = line.split("\t");
        Set<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        for (String requiredName : requiredNames) {
            if (!nameSet.contains(requiredName)) {
                String error = "Input file does not contain all the necessary fields. Missing field: " + requiredName;
                System.err.println(error);
                throw new RuntimeException(error);
            }
        }
        // Presence of Tumor_Seq_Allele1 or Tumor_Seq_Allele2
        if (!nameSet.contains("Tumor_Seq_Allele1") && !nameSet.contains("Tumor_Seq_Allele2")) {
            String error = "Header line should either include \"Tumor_Seq_Allele1\" or \"Tumor_Seq_Allele2\"";
            System.err.println(error);
            throw new RuntimeException(error);
        }
        if (inputFileHeaders != null) {
            Collections.addAll(inputFileHeaders, names);
        }
        if (tokenizer != null) {
            tokenizer.setNames(names); // do not use sorted names here, this will mess the places where tokenizer is used later on.
        }
    }

    @Override
    public void handleLine(String line) {
        checkHeader(line, tokenizer);
    }
}
