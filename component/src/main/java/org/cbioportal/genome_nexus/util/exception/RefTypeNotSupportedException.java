package org.cbioportal.genome_nexus.util.exception;

public class RefTypeNotSupportedException extends IllegalArgumentException {
    public RefTypeNotSupportedException() {
        super("coding dna, protein, and rna hgvs sequences are not supported");
    }
}