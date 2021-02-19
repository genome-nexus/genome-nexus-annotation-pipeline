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
package org.cbioportal.annotation;

/**
 *
 * @author heinsz
 */
import org.cbioportal.annotation.pipeline.BatchConfiguration;
import org.cbioportal.annotation.pipeline.TestConfiguration;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.batch.core.*;
import org.springframework.batch.test.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BatchConfiguration.class, TestConfiguration.class})
@TestPropertySource("classpath:application.properties")
public class TestAnnotationPipeline {
    
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;
    
    @Test
    public void pipelineTest() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
            .addString("filename", classLoader.getResource("data/testmaf.txt").getPath())
            .addString("outputFilename", "target/test-outputs/output.txt")
            .addString("replace", "false")
            .addString("isoformOverride", "uniprot")
            .toJobParameters());
        AssertFile.assertFileEquals(new FileSystemResource(classLoader.getResource("data/expectedmaf.txt").getPath()), new FileSystemResource("target/test-outputs/output.txt"));
    }
    
}
