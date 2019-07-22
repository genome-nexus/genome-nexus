package org.cbioportal.genome_nexus.util.exception;

public class InvalidHgvsException extends IllegalArgumentException {
    public InvalidHgvsException() {
        super("hgvs is invalid");
    }
}