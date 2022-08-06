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
