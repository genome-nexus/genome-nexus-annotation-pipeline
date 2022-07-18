package org.cbioportal.annotation.cli;

public class NoSubcommandFoundException extends Exception {
    public NoSubcommandFoundException() {
        super("Can't find subcommand");
    }
}
