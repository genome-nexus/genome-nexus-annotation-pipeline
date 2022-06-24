package org.cbioportal.annotation.pipeline;

import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultLineCallbackHandler implements LineCallbackHandler {
    private static final String[] requiredNames = {"Chromosome", "Start_Position", "End_Position", "Reference_Allele", "Tumor_Seq_Allele1"};
    private final DelimitedLineTokenizer tokenizer;

    public DefaultLineCallbackHandler(DelimitedLineTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public void handleLine(String line) {
        String[] names = line.split("\t");
        Set<String> nameSet = new HashSet<>();
        nameSet.addAll(Arrays.asList(names));
        for (String requiredName : requiredNames) {
            if (!nameSet.contains(requiredName)) {
                String errorMessage = "Input file does not contain all the necessary fields: " + Arrays.toString(requiredNames);
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }
        tokenizer.setNames(names); // do not use sorted names here
    }
}
