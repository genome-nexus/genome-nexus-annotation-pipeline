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

import java.net.MalformedURLException;
import javax.sql.DataSource;
import org.cbioportal.annotator.util.AnnotationUtil;
import org.cbioportal.models.AnnotatedRecord;

import org.springframework.batch.core.*;
import org.springframework.batch.item.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Zachary Heins
 */
@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages="org.cbioportal.annotator")
public class BatchConfiguration
{
    public static final String ANNOTATION_JOB = "annotationJob";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${chunk:100000}")
    private String chunk;

    @Bean
    public Job annotationJob()
    {
        return jobBuilderFactory.get(ANNOTATION_JOB)
            .start(step())
            .build();
    }

    @Bean
    public AnnotationUtil annotationUtil() {
        return new AnnotationUtil();
    }

    @Bean
    public Step step()
    {
        return stepBuilderFactory.get("step")
            .<AnnotatedRecord, String> chunk(Integer.parseInt(chunk))
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

    // general spring batch configuration
    @Value("org/springframework/batch/core/schema-drop-sqlite.sql")
    private Resource dropRepositoryTables;

    @Value("org/springframework/batch/core/schema-sqlite.sql")
    private Resource dataRepositorySchema;

    /**
     * Spring Batch datasource.
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:repository.sqlite");
        return dataSource;
    }

    /**
     * Spring Batch datasource initializer.
     * @param dataSource
     * @return DataSourceInitializer
     * @throws MalformedURLException
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws MalformedURLException {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(dropRepositoryTables);
        databasePopulator.addScript(dataRepositorySchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        return initializer;
    }

    /**
     * Spring Batch job repository.
     * @return JobRepository
     * @throws Exception
     */
    private JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }

    /**
     * Spring Batch transaction manager.
     * @return PlatformTransactionManager
     */
    private PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * Spring Batch job launcher.
     * @return JobLauncher
     * @throws Exception
     */
    public JobLauncher getJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
