package org.cbioportal.annotation.cli;

public class AnnotationFailedException extends Exception {
    public AnnotationFailedException(String message) {
        super(message);
    }

    public AnnotationFailedException(Exception e) {
        super(e);
    }
}
