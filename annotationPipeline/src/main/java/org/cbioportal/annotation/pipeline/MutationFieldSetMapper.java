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

import org.cbioportal.models.MutationRecord;
import java.util.*;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import org.springframework.cglib.beans.BeanMap;
import com.google.common.collect.Sets;
import com.google.common.base.Strings;
import java.util.stream.Collectors;

/**
 *
 * @author heinsz
 */
public class MutationFieldSetMapper implements  FieldSetMapper<MutationRecord> {
    @Override
    public MutationRecord mapFieldSet(FieldSet fs) throws BindException {
        MutationRecord mr = new MutationRecord();
        BeanMap recordBeanMap = BeanMap.create(new MutationRecord());
        Set<String> fieldSetNames = new HashSet(Arrays.asList(fs.getNames()));
        Set<String> mutationRecordFields = new HashSet(mr.getHeader());
        
        // We need to intersect these two sets ignoring case, and then use this
        // intersection to filter the original set for data accession
        Set<String> intersection = Sets.intersection(
            mutationRecordFields.stream()
                .map(field -> field.toUpperCase())
                .collect(Collectors.toSet()),
            fieldSetNames.stream()
                .map(field -> field.toUpperCase())
                .collect(Collectors.toSet()));
        
        // Here we make sure that the field is in our intersection
        Set<String> fields = fieldSetNames.stream()
                .filter(line -> intersection.contains(line.toUpperCase()))
                .collect(Collectors.toSet());
        
        // In this we actually set the value in our bean map
        fields.stream().forEach(column -> 
            {
                recordBeanMap.put(column.toUpperCase(),Strings.isNullOrEmpty(fs.readRawString(column)) ? "" : fs.readRawString(column));
            });
        
        // Now access the bean from the map and set the additional propertes 
        // (the difference of the field set and mutation record header)
        MutationRecord record = (MutationRecord) recordBeanMap.getBean();
        Set<String> additionalColumns = Sets.difference(
            fieldSetNames.stream()
                .map(field -> field.toUpperCase())
                .collect(Collectors.toSet()),
            mutationRecordFields.stream()
                .map(field -> field.toUpperCase())
                .collect(Collectors.toSet()));
        Set<String> additionalColumnsWithCase = fieldSetNames.stream()
                .filter(field -> additionalColumns.contains(field.toUpperCase()))
                .collect(Collectors.toSet());
        
        // Actually set the values
        additionalColumnsWithCase.stream().forEach((additionalField) -> {
            record.addAdditionalProperty(additionalField, fs.readRawString(additionalField));
        });
        return record;
    }
}
