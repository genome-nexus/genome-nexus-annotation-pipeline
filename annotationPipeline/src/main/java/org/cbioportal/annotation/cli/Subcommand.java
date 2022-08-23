package org.cbioportal.annotation.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * @author Mete Ozguz
 */
public interface Subcommand {

    public static void help(String cmdLineSyntax, String header, Options gnuOptions) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(100, cmdLineSyntax, header,gnuOptions,"Visit https://github.com/genome-nexus/genome-nexus-annotation-pipeline/blob/master/CMD_HELP.md for more.\n ");
    }

    void printHelp();

    boolean hasOption(String opt);

    String getOptionValue(String opt);

    String getOptionValue(String opt, String defaultValue);
}
