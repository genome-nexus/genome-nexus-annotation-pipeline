/*
 * Copyright (c) 2016 - 2024 Memorial Sloan-Kettering Cancer Center.
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

import org.cbioportal.annotator.util.AnnotationUtil;
import org.cbioportal.models.AnnotatedRecord;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Zachary Heins
 */
@Configuration
@ComponentScan(basePackages="org.cbioportal.annotator")
public class BatchConfiguration
{
    public static final String ANNOTATION_JOB = "annotationJob";

    @Value("${chunk:1000000}")
    private String chunkSize;

    @Bean
    public Job annotationJob(JobRepository jobRepository, Step step)
    {
        return new JobBuilder(ANNOTATION_JOB, jobRepository)
            .start(step)
            .build();
    }

    @Bean
    public AnnotationUtil annotationUtil() {
        return new AnnotationUtil();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager)
    {
        return new StepBuilder("step", jobRepository)
            .<AnnotatedRecord, String> chunk(Integer.parseInt(chunkSize), transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @Bean
    @StepScope
    public ItemStreamReader<AnnotatedRecord> reader()
    {
        return new MutationRecordReader();
    }

    @Bean
    @StepScope
    public MutationRecordProcessor processor()
    {
        return new MutationRecordProcessor();
    }

    @Bean
    @StepScope
    public ItemStreamWriter<String> writer()
    {
        return new MutationRecordWriter();
    }

}
