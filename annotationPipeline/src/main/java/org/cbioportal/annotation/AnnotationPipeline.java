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

import org.cbioportal.annotation.pipeline.BatchConfiguration;

import org.apache.commons.cli.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * @author Zachary Heins
 */
@SpringBootApplication
public class AnnotationPipeline
{

    private static Options getOptions(String[] args)
    {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
            .addOption("f", "filename", true, "Mutation filename")
            .addOption("o", "output-filename", true, "Output filename (including path)")
            .addOption("i", "isoform-override", true, "Isoform Overrides (mskcc or uniprot)")
            .addOption("e", "error-report-location", true, "Error report filename (including path)")
            .addOption("r", "replace-symbol-entrez", false, "Replace gene symbols and entrez id with what is provided by annotator" )
            .addOption("p", "post-interval-size", true, "Number of records to make POST requests to Genome Nexus with at a time");

        return gnuOptions;
    }

    private static void help(Options gnuOptions, int exitStatus)
    {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("GenomeNexusAnnotationPipeline", gnuOptions);
        System.exit(exitStatus);
    }

    private static void launchJob(String[] args, String filename, String outputFilename, String isoformOverride,
            String errorReportLocation, boolean replace, Integer postIntervalSize) throws Exception
    {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(AnnotationPipeline.class).web(false).run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);

        Job annotationJob = ctx.getBean(BatchConfiguration.ANNOTATION_JOB, Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("filename", filename)
            .addString("outputFilename", outputFilename)
            .addString("replace", String.valueOf(replace))
            .addString("isoformOverride", isoformOverride)
            .addString("errorReportLocation", errorReportLocation)
            .addString("postIntervalSize", String.valueOf(postIntervalSize))
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(annotationJob, jobParameters);
        if (!jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            System.exit(2);
        }
    }

    public static void main(String[] args) throws Exception
    {
        Options gnuOptions = AnnotationPipeline.getOptions(args);
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(gnuOptions, args);
        if (commandLine.hasOption("h") ||
            !commandLine.hasOption("filename") ||
            !commandLine.hasOption("output-filename")) {
            help(gnuOptions, 0);
        }
        launchJob(args, commandLine.getOptionValue("filename"), commandLine.getOptionValue("output-filename"),commandLine.getOptionValue("isoform-override"),
                commandLine.hasOption("error-report-location") ? commandLine.getOptionValue("error-report-location") : null,
                commandLine.hasOption("replace-symbol-entrez"), commandLine.hasOption("post-interval-size") ? Integer.valueOf(commandLine.getOptionValue("post-interval-size")) : -1);
    }
}
