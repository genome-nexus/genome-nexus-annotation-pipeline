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

import org.apache.commons.cli.ParseException;
import org.cbioportal.annotation.annotationTools.MafMerger;
import org.cbioportal.annotation.cli.*;
import org.cbioportal.annotation.pipeline.BatchConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Mete Ozguz
 * @author Zachary Heins
 */
@SpringBootApplication
public class AnnotationPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationPipeline.class);

    private static void annotateJob(String[] args, String filename, String outputFilename, String outputFormat, String isoformOverride,
                                    String errorReportLocation, boolean replace, String postIntervalSize, String stripMatchingBases, Boolean ignoreOriginalGenomicLocation, Boolean addOriginalGenomicLocation) throws Exception {
        SpringApplication app = new SpringApplication(AnnotationPipeline.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setAllowBeanDefinitionOverriding(Boolean.TRUE);
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);

        Job annotationJob = ctx.getBean(BatchConfiguration.ANNOTATION_JOB, Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("filename", filename)
            .addString("outputFilename", outputFilename)
            .addString("outputFormat", outputFormat)
            .addString("replace", String.valueOf(replace))
            .addString("isoformOverride", isoformOverride)
            .addString("errorReportLocation", errorReportLocation)
            .addString("postIntervalSize", postIntervalSize)
            .addString("stripMatchingBases", stripMatchingBases)
            .addString("ignoreOriginalGenomicLocation", String.valueOf(ignoreOriginalGenomicLocation))
            .addString("addOriginalGenomicLocation", String.valueOf(addOriginalGenomicLocation))
            .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(annotationJob, jobParameters);
        if (!jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            System.exit(2);
        }
    }

    public static void subMain(String[] args) throws NoSubcommandFoundException, ParseException, MergeFailedException, AnnotationFailedException {
        boolean help = false;
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                help = true;
                break;
            }
        }
        Subcommand subcommand = null;
        try {
            subcommand = Subcommands.find(args);
        } catch (ParseException | NoSubcommandFoundException e) {
            if (help || args.length == 0) {
                AnnotateSubcommand.help();
                MergeSubcommand.help();
                throw e;
            }
        }
        if (subcommand == null) {
            subcommand = new AnnotateSubcommand(args);
        }
        if (subcommand instanceof AnnotateSubcommand) {
            Instant start = Instant.now();
            annotate(subcommand, args);
            System.out.println(" RUNTIME: " + Duration.between(start, Instant.now()).getSeconds() + " secs.");
        } else if (subcommand instanceof MergeSubcommand) {
            merge(subcommand);
        } else if (subcommand instanceof VersionSubcommand) {
            version((VersionSubcommand) subcommand);
        }
    }

    public static void main(String[] args) {
        try {
            subMain(args);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static String getAppVersion() {
        java.io.InputStream is = AnnotationPipeline.class.getClassLoader().getResourceAsStream("maven.properties");
        java.util.Properties p = new Properties();
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String appVersion = p.getProperty("app.version");

        return appVersion;
    }

    private static void version(VersionSubcommand subcommand) {
        if (subcommand.hasOption("h")) {
            subcommand.printHelp();
            return;
        }
        System.out.println("Client: " + getAppVersion());
        System.out.println(subcommand.getformattedServerVersion());
    }

    private static void merge(Subcommand subcommand) throws MergeFailedException {
        if (subcommand.hasOption("h")) {
            subcommand.printHelp();
            return;
        }
        if (!subcommand.hasOption("output-maf")) {
            subcommand.printHelp();
            throw new MergeFailedException("required option: output-maf");
        }
        if (subcommand.hasOption("input-mafs-list") && subcommand.hasOption("input-mafs-directory")) {
            String error = "Please choose only one of the following options when running script: --input-mafs-list | --input-mafs-directory";
            LOG.error(error);
            subcommand.printHelp();
            throw new MergeFailedException(error);
        }
        List<String> inputMafs = new ArrayList<>();
        if (subcommand.hasOption("input-mafs-list")) {
            String[] files = subcommand.getOptionValue("input-mafs-list").split(",");
            for (String file : files) {
                inputMafs.add(file);
            }
        } else if (subcommand.hasOption("input-mafs-directory")) {
            File inputDirectory = new File(subcommand.getOptionValue("input-mafs-directory"));
            if (inputDirectory.exists() && inputDirectory.isDirectory()) {
                File[] files = inputDirectory.listFiles();
                for (File file : files) {
                    inputMafs.add(file.getAbsolutePath());
                }
            } else {
                String error = "Supplied input mafs directory is not a directory or it does not exist!";
                throw new MergeFailedException(error);
            }
        }
        if (inputMafs.size() == 0 || inputMafs.size() == 1) {
            String error = "There is nothing to merge! Count of input files: " + inputMafs.size();
            LOG.error(error);
            throw new MergeFailedException(error);
        }
        boolean skipInvalidInput = false;
        if (subcommand.hasOption("skip-invalid-input")) {
            skipInvalidInput = true;
        }
        try {
            MafMerger.mergeInputMafs(inputMafs, subcommand.getOptionValue("output-maf"), skipInvalidInput);
        } catch (IOException e) {
            throw new MergeFailedException(e);
        }
    }

    private static void annotate(Subcommand subcommand, String[] args) throws AnnotationFailedException {
        if (subcommand.hasOption("h")) {
            subcommand.printHelp();
            return;
        }
        if (!subcommand.hasOption("filename")) {
            subcommand.printHelp();
            throw new AnnotationFailedException("required option: filename");
        }
        if (!subcommand.hasOption("output-filename")) {
            subcommand.printHelp();
            throw new AnnotationFailedException("required option: output-filename");
        }
        String outputFormat = null;
        if (subcommand.hasOption("output-format")) {
            String outputFormatFile = subcommand.getOptionValue("output-format");
            if ("extended".equals(outputFormatFile)) {
                outputFormat = "extended";
            } else if ("minimal".equals(outputFormatFile)) {
                outputFormat = "minimal";
            } else if (!Files.exists(Paths.get(outputFormatFile))) {
                String error = "Either file is not exist or outputFormat is not 'minimal' or 'extended'. Supplied outputFormat value: " + outputFormatFile;
                System.err.println(error);
                throw new AnnotationFailedException(error);
            } else {
                // user supplied a format file instead of pre-defined formats
                try (BufferedReader br = new BufferedReader(new FileReader(outputFormatFile))) {
                    outputFormat = br.readLine();
                    if (outputFormat == null || !outputFormat.contains(",")) {
                        String error = "Unexpected formatting found inside of " + outputFormatFile;
                        System.err.println(error);
                        throw new AnnotationFailedException(error);
                    }
                } catch (IOException e) {
                    throw new AnnotationFailedException("Error while reading output-format file: " + outputFormatFile);
                }
            }
        }
        if (subcommand.hasOption("isoform-override")) {
            String isoformOverride = subcommand.getOptionValue("isoform-override");
            if (!(isoformOverride.equals("mskcc") || isoformOverride.equals("uniprot"))) {
                throw new AnnotationFailedException("Isoform override not valid. Options: 'mskcc' or 'uniprot'.");
            }
        }
        if (subcommand.hasOption("strip-matching-bases")) {
            String stripMatchingBases = subcommand.getOptionValue("strip-matching-bases");
            if (!(stripMatchingBases.equals("first") || stripMatchingBases.equals("none") || stripMatchingBases.equals("all"))) {
                throw new AnnotationFailedException("Strip matching bases not valid. Options: 'first', 'all' or 'none'.");
            }
        }
        try {
            annotateJob(args, subcommand.getOptionValue("filename"), subcommand.getOptionValue("output-filename"), outputFormat, subcommand.getOptionValue("isoform-override"),
                    subcommand.getOptionValue("error-report-location", ""),
                    true, subcommand.getOptionValue("post-interval-size", "100"), subcommand.getOptionValue("strip-matching-bases", "all"), subcommand.hasOption("ignore-original-genomic-location"), subcommand.hasOption("add-original-genomic-location"));
            // When you change the default value of post-interval-size, do not forget to update MutationRecordReader.postIntervalSize accordingly
            // "replace-symbol-entrez" is true by default
        } catch (Exception e) {
            throw new AnnotationFailedException(e);
        }
    }
}
