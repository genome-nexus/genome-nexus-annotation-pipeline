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
package org.cbioportal.models;

import org.genome_nexus.client.GenomicLocation;
import org.mskcc.cbio.maf.MafUtil;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cbioportal.models.Header.*;

/**
 * It holds a single line of the data
 * In other words, a single line of a maf file
 * All the data should be "immutable" here!
 * This class shouldn't be a place for input validation!
 *
 * @author Mete Ozguz
 */
public class MutationRecord {
    // value is the header index
    private static final Map<String, Integer> headers = new HashMap<>();
    private static final String separator = "\t";
    private final int[] beginIndices;
    private final int[] endIndices;
    private final String line;

    public MutationRecord(String line) {
        Assert.notNull(headers, "Init headers by using its method before creating any object!");
        Assert.notNull(line, "You can't create the object with a null line!");
        this.line = line;
        beginIndices = new int[headers.size()];
        endIndices = new int[headers.size()];
        initSeparatorIndices();
    }

    public static void init(List<String> inputHeaders) {
        int i = 0;
        for (String inputHeader : inputHeaders) {
            headers.put(inputHeader, i);
            i++;
        }
    }

    private void initSeparatorIndices() {
        Matcher matcher = Pattern.compile(separator).matcher(line);
        endIndices[endIndices.length - 1] = line.length();
        int i = 1;
        while (matcher.find()) {
            beginIndices[i] = matcher.start() + 1;
            endIndices[i - 1] = matcher.start();
            i++;
        }
    }

    public String get(String header) {
        Integer index = headers.get(header);
        if (index == null) {
            return "";
        }
        return line.substring(beginIndices[index], endIndices[index]);
    }

    public GenomicLocation asGenomicLocation() {
        GenomicLocation genomicLocation = new GenomicLocation();
        genomicLocation.setChromosome(get(Chromosome));
        genomicLocation.setStart(Integer.valueOf(get(Start_Position)));
        genomicLocation.setEnd(Integer.valueOf(get(End_Position)));
        String referenceAllele = get(Reference_Allele);
        genomicLocation.setReferenceAllele(referenceAllele);
        genomicLocation.setVariantAllele(MafUtil.resolveTumorSeqAllele(referenceAllele, get(Tumor_Seq_Allele1), get(Tumor_Seq_Allele2)));
        return genomicLocation;
    }

    public Collection<String> getHeaders() {
        return headers.keySet();
    }

}
