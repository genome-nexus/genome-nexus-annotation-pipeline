package org.cbioportal.annotation.cli;

public class MergeFailedException extends Exception {
    public MergeFailedException(String message) {
        super(message);
    }

    public MergeFailedException(Exception e) {
        super(e);
    }
}
