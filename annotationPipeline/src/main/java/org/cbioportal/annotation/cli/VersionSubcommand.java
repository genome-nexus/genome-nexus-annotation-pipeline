package org.cbioportal.annotation.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.genome_nexus.ApiException;
import org.genome_nexus.client.AggregateSourceInfo;
import org.genome_nexus.client.InfoControllerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionSubcommand implements Subcommand {
    private static Options options;
    private static final String UKNOWN_GENOME_NEXUS_VERSION = "unknown";
    private final Logger LOG = LoggerFactory.getLogger(VersionSubcommand.class);

    static {
        options = getOptions();
    }

    private CommandLine commandLine;

    private VersionSubcommand() {
    }

    public VersionSubcommand(String[] args) throws ParseException {
        commandLine = Subcommands.getCommandLine(args, options);
    }

    public String getServerVersion() {
        InfoControllerApi infoApiClient = new InfoControllerApi();
        try {
            AggregateSourceInfo result = infoApiClient.fetchVersionGET();
            return result.getGenomeNexus().getServer().getVersion();
        } catch (ApiException e) {
            LOG.error("Exception when calling InfoControllerApi#fetchVersionGET, genome nexus version is unknown", e);
        }
        return UKNOWN_GENOME_NEXUS_VERSION;
    }

    private static Options getOptions() {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.");
        return gnuOptions;
    }

    public static void help() {
        Subcommand.help("GenomeNexusAnnotationPipeline version", "version subcommand options:", options);
    }

    @Override
    public void printHelp() {
        help();
    }

    @Override
    public boolean hasOption(String opt) {
        return commandLine.hasOption(opt);
    }

    @Override
    public String getOptionValue(String opt) {
        return commandLine.getOptionValue(opt);
    }

    @Override
    public String getOptionValue(String opt, String defaultValue) {
        return commandLine.getOptionValue(opt, defaultValue);
    }
}
