package org.cbioportal.annotation.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class VersionSubcommand implements Subcommand {
    private static Options options;

    static {
        options = getOptions();
    }

    private CommandLine commandLine;

    private VersionSubcommand() {
    }

    public VersionSubcommand(String[] args) throws ParseException {
        commandLine = Subcommands.getCommandLine(args, options);
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
