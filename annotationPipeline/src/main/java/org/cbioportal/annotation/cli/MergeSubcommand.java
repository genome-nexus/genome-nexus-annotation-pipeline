package org.cbioportal.annotation.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Mete Ozguz
 */
public class MergeSubcommand implements Subcommand {
    private static Options options;

    static {
        options = getOptions();
    }

    private CommandLine commandLine;

    private MergeSubcommand() {
    }

    public MergeSubcommand(String[] args) throws ParseException {
        commandLine = Subcommands.getCommandLine(args, options);
    }

    private static Options getOptions() {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
                .addOption("i", "input-mafs-list", true, "comma-delimited list of MAFs to merge")
                .addOption("d", "input-mafs-directory", true, "directory containing all MAFs to merge")
                .addOption("o", "output-maf", true, "output filename for merged MAF [REQUIRED]")
                .addOption("s", "skip-invalid-input", false,
                        "skips invalid input file. Input file must include following headers:Chromosome, Start_Position, End_Position, Reference_Allele. " +
                                "Input file should either include Tumor_Seq_Allele1 or Tumor_Seq_Allele2");
        return gnuOptions;
    }

    public static void help() {
        Subcommand.help("GenomeNexusAnnotationPipeline merge", "merge subcommand options:", options);
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
