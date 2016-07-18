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

package org.cbioportal.genomeNexus.annotation;

import org.cbioportal.genomeNexus.annotation.pipeline.BatchConfiguration;

import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.*;

/**
 * @author Zachary Heins
 */
@SpringBootApplication
public class GenomeNexusAnnotationPipeline
{

    private static Options getOptions(String[] args)
    {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
            .addOption("f", "filename", true, "Mutation filename")
            .addOption("d", "directory", true, "Output directory");

        return gnuOptions;
    }

    private static void help(Options gnuOptions, int exitStatus)
    {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("GenomeNexusAnnotationPipeline", gnuOptions);
        System.exit(exitStatus);
    }

    private static void launchJob(String[] args, String filename, String directory) throws Exception
    {
        SpringApplication app = new SpringApplication(GenomeNexusAnnotationPipeline.class);      
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);

        Job annotationJob = ctx.getBean(BatchConfiguration.ANNOTATION_JOB, Job.class);       
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("filename", filename)
            .addString("directory", directory)
    		.toJobParameters();  
        JobExecution jobExecution = jobLauncher.run(annotationJob, jobParameters);
    }
    
    public static void main(String[] args) throws Exception
    {        
        Options gnuOptions = GenomeNexusAnnotationPipeline.getOptions(args);
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(gnuOptions, args);
        if (commandLine.hasOption("h") ||
            !commandLine.hasOption("filename") ||
            !commandLine.hasOption("directory")) {
            help(gnuOptions, 0);
        }
        launchJob(args, commandLine.getOptionValue("filename"), commandLine.getOptionValue("directory"));            
    }
}
