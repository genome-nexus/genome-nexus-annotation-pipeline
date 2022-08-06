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

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mete Ozguz
 */
public class SuccessfulMutationRecordWriter implements ItemStreamWriter<String> {

    @Value("#{jobParameters[successfulOutputFilename]}")
    private String successfulOutputFilename;

    @Value("#{stepExecutionContext['commentLines']}")
    private List<String> commentLines;

    @Value("#{stepExecutionContext['mutation_header']}")
    private List<String> header;

    @Value("#{stepExecutionContext['successfulRecordsCount']}")
    private Integer successfulRecordsCount;
    private FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<>();

    // Set up the writer and print the json from CVR to a file
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        if (successfulOutputFilename != null && !successfulOutputFilename.isEmpty() && successfulRecordsCount > 0) {
            Path stagingFile = Paths.get(successfulOutputFilename);
            PassThroughLineAggregator aggr = new PassThroughLineAggregator();
            flatFileItemWriter.setLineAggregator(aggr);
            flatFileItemWriter.setResource(new FileSystemResource(stagingFile.toString()));
            flatFileItemWriter.setHeaderCallback(new DefaultFlatFileHeaderCallback(header, commentLines));
            flatFileItemWriter.open(ec);
        }
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        if (successfulOutputFilename != null && !successfulOutputFilename.isEmpty() && successfulRecordsCount > 0) {
            flatFileItemWriter.close();
        }
    }

    @Override
    public void write(List<? extends String> items) throws Exception {
        if (successfulOutputFilename != null && !successfulOutputFilename.isEmpty() && successfulRecordsCount > 0) {
            List<String> successItems = new ArrayList<>();
            for (String item : items) {
                if (item.contains("\tSUCCESS")) {
                    successItems.add(item);
                }
            }
            flatFileItemWriter.write(successItems);
        }
    }
}
