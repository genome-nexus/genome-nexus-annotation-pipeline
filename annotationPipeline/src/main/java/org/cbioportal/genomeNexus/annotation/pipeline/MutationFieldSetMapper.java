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

package org.cbioportal.genomeNexus.annotation.pipeline;

import java.util.List;
import org.cbioportal.genomeNexus.models.MutationRecord;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 *
 * @author heinsz
 */
public class MutationFieldSetMapper implements  FieldSetMapper<MutationRecord> {

    @Override
    public MutationRecord mapFieldSet(FieldSet fs) throws BindException {
        MutationRecord record = new MutationRecord();
        List<String> fields = MutationRecord.getFieldNames();
        
        for (int i = 0; i < fs.getFieldCount(); i++)
        {            
            try {
                String field = fields.get(i);
                record.getClass().getMethod("set" + field, String.class).invoke(record, fs.readString(i));                
            }
            catch(Exception e) {
                if (e.getClass().equals(NoSuchMethodException.class) || e.getClass().equals(IndexOutOfBoundsException.class)) {
                    record.addAdditionalProperty(fs.getNames()[i], fs.readString(i));
                }                
            }
         }       
        
        return record;
    }
}