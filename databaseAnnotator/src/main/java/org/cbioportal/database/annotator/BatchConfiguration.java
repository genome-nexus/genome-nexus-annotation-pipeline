/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.database.annotator;

import org.cbioportal.database.annotator.model.*;
import org.springframework.batch.core.*;
import org.springframework.batch.item.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author heinsz
 */

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages="org.cbioportal.annotator")
public class BatchConfiguration {

    public static final String DATABASE_ANNOTATOR_JOB = "databaseAnnotatorJob";
    public static final String MUTATION_EVENT_TABLE = "mutation_event";
    public static final String MUTATION_TABLE = "mutation";
    public static final String SAMPLE_TABLE =  "sample";
    public static final String PATIENT_TABLE = "patient";
    public static final String CANCER_STUDY_TABLE = "cancer_study";

    @Value("${databaseannotator.chunk_size}")
    private Integer chunkSize;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job databaseAnnotatorJob(){
        return jobBuilderFactory.get(DATABASE_ANNOTATOR_JOB)
                .start(AnnotateRecordsStep())
                .build();
    }

    @Bean
    public Step AnnotateRecordsStep(){
        return stepBuilderFactory.get("AnnotateRecordsStep")
                .<MutationEvent, MutationEvent> chunk(chunkSize)
                .reader(annotateRecordsReader())
                .processor(annotateRecordsProcessor())
                .writer(annotateRecordsWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemStreamReader<MutationEvent> annotateRecordsReader(){
        return new AnnotateRecordsReader();
    }

    @Bean
    @StepScope
    public AnnotateRecordsProcessor annotateRecordsProcessor()
    {
        return new AnnotateRecordsProcessor();
    }

    @Bean
    @StepScope
    public ItemStreamWriter<MutationEvent> annotateRecordsWriter()
    {
        return new AnnotateRecordsWriter();
    }
}
