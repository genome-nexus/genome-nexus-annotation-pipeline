package org.cbioportal.annotation.cli;

import org.apache.commons.cli.*;

/**
 * @author Mete Ozguz
 */
public class Subcommands {
    public static Subcommand find(String[] args) throws ParseException, NoSubcommandFoundException {
        for (String arg : args) {
            if ("annotate".equals(arg)) {
                return new AnnotateSubcommand(args);
            } else if ("merge".equals(arg)) {
                return new MergeSubcommand(args);
            } else if("version".equals(arg)) {
                return new VersionSubcommand(args);
            }
        }
        throw new NoSubcommandFoundException();
    }

    public static CommandLine getCommandLine(String[] args, Options options) throws ParseException {
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options, args);
        return commandLine;
    }
}
