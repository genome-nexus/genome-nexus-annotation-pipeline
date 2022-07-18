package org.cbioportal.annotation.annotationTools;

import org.cbioportal.annotation.cli.MergeFailedException;
import org.cbioportal.annotation.pipeline.DefaultLineCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Mete Ozguz
 */
public class MafMerger {

    private static final String SPLITTER = "\t";

    /**
     * Generates a merged MAF given a list of input MAFs.
     *
     * @return
     */
    public static Path mergeInputMafs(List<String> inputMafs, String outputMafFilename, boolean skipInvalidInput) throws IOException, MergeFailedException {
        List<String> validInputMafs = new ArrayList<>();
        Set<String> mergedHeaders = mergeHeaders(inputMafs, skipInvalidInput, validInputMafs);
        if (validInputMafs.size() == 0 || validInputMafs.size() == 1) {
            throw new MergeFailedException("There is nothing to merge!");
        }
        Map<String, List<String>> data = mergeData(mergedHeaders, validInputMafs);
        return dumpTo(mergedHeaders, data, outputMafFilename);
    }

    private static Map<String, List<String>> mergeData(Set<String> mergedHeaders, List<String> validInputMafs) throws IOException {
        Map<String, List<String>> data = new HashMap<>();
        for (String header : mergedHeaders) {
            data.put(header, new ArrayList<>());
        }
        for (String inputMaf : validInputMafs) {
            boolean headerPassed = false;
            Set<String> missingHeaders = new LinkedHashSet<>();
            missingHeaders.addAll(mergedHeaders);
            List<String> headers = new ArrayList<>();
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(inputMaf))) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    } else {
                        if (!headerPassed) {
                            headerPassed = true;
                            headers.addAll(Arrays.asList(line.split(SPLITTER)));
                            missingHeaders.removeAll(headers);
                        } else {
                            String[] tokens = line.split(SPLITTER);
                            for (int i = 0; i < tokens.length; i++) {
                                data.get(headers.get(i)).add(tokens[i]);
                            }
                            for (String missingHeader : missingHeaders) {
                                data.get(missingHeader).add("");
                            }
                        }
                    }
                }
            }
        }
        return data;
    }

    private static Path dumpTo(Set<String> headers, Map<String, List<String>> data, String outputMafFilename) throws IOException {
        Path path = Paths.get(outputMafFilename);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            // write header
            StringBuilder line = new StringBuilder();
            for (String header : headers) {
                line.append(header).append(SPLITTER);
            }
            line.deleteCharAt(line.length() - 1);
            bufferedWriter.write(line.toString());
            bufferedWriter.newLine();
            int rowCount = data.get(data.keySet().iterator().next()).size();
            // write data
            for (int i = 0; i < rowCount; i++) {
                line.setLength(0);
                for (String header : headers) {
                    line.append(data.get(header).get(i)).append(SPLITTER);
                }
                line.deleteCharAt(line.length() - 1);
                bufferedWriter.write(line.toString());
                bufferedWriter.newLine();
            }
        }
        return path.toAbsolutePath();
    }

    /**
     * Generates a merged header from all input files.
     * Also ensures that "Hugo_Symbol" and "Entrez_Gene_Id"
     * are the first 2 columns in the merged MAF header.
     *
     * @param inputMafs
     * @param skipInvalidInput
     * @param validInputMafs
     * @return Order preserving set of merged headers
     * @throws IOException
     */
    public static Set<String> mergeHeaders(List<String> inputMafs, boolean skipInvalidInput, List<String> validInputMafs) throws IOException {
        Set<String> headers = new LinkedHashSet<>();
        headers.add("Hugo_Symbol");
        headers.add("Entrez_Gene_Id");
        for (String inputMaf : inputMafs) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(inputMaf))) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    break;
                }
                if (line != null) {
                    if (skipInvalidInput) {
                        try {
                            DefaultLineCallbackHandler.checkHeader(line, null);
                        } catch (Exception e) {
                            continue; // invalid input found
                        }
                    }
                    validInputMafs.add(inputMaf);
                    headers.addAll(Arrays.asList(line.split(SPLITTER)));
                }
            }
        }
        return headers;
    }
}
