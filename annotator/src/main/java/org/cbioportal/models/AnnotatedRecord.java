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
package org.cbioportal.models;


import java.util.*;

/**
 * It holds newest & additional data on top of a mutation record.
 * Basically it adds "mutability" to a "immutable" MutationRecord
 *
 * @author Mete Ozguz
 */

public class AnnotatedRecord {
    public static final Set<String> CLASS_WIDE_HEADERS = new HashSet<>();
    private final MutationRecord mutationRecord;
    private final Map<String, String> headersValues = new HashMap<>();

    public AnnotatedRecord(MutationRecord mutationRecord) {
        this.mutationRecord = mutationRecord;
    }

    public void setIfPopulated(String header, String value) {
        if (value != null && !value.isEmpty()) {
            headersValues.put(header, value);
            CLASS_WIDE_HEADERS.add(header);
        }
    }

    public String get(String header) {
        String value = headersValues.get(header);
        if (value == null) {
            return mutationRecord.get(header);
        }
        return value;
    }

    public String toLine(List<String> headers) {
        StringBuilder result = new StringBuilder("");
        for (String header : headers) {
            String candidate = get(header);
            result.append(candidate).append("\t");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    public Collection<String> getHeaders() {
        return headersValues.keySet();
    }
}
