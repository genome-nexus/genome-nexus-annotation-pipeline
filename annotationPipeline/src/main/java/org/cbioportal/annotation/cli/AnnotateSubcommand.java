package org.cbioportal.annotation.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Mete Ozguz
 */
public class AnnotateSubcommand implements Subcommand {
    private static Options options;

    static {
        options = getOptions();
    }

    private CommandLine commandLine;

    private AnnotateSubcommand() {
    }

    public AnnotateSubcommand(String[] args) throws ParseException {
        commandLine = Subcommands.getCommandLine(args, options);
    }

    public static void help() {
        Subcommand.help("GenomeNexusAnnotationPipeline annotate", "annotate is the default behavior when subcommand is omitted.\nannotate subcommand options:", options);
    }

    private static Options getOptions() {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
                .addOption("f", "filename", true, "Mutation filename")
                .addOption("o", "output-filename", true, "Output filename (including path)")
                .addOption("t", "output-format", true, "tcga, minimal or a file path which includes output format (FORMAT EXAMPLE: Chromosome,Hugo_Symbol,Entrez_Gene_Id,Center,NCBI_Build)")
                .addOption("i", "isoform-override", true, "Isoform Overrides (mskcc or uniprot)")
                .addOption("e", "error-report-location", true, "Error report filename (including path)")
                .addOption("r", "replace-symbol-entrez", false, "Replace gene symbols and entrez id with what is provided by annotator")
                .addOption("p", "post-interval-size", true, "Number of records to make POST requests to Genome Nexus with at a time");
        return gnuOptions;
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
