package org.cbioportal.genome_nexus.util;

enum Type {
    SUBSTITUTION, INSERTION, DELETION, INDEL
}

enum RefType {
    GENOMIC, MITOCHONDRIAL
}
public class GenomicVariant {
    private String chromosome;
    private RefType ref_type;
    private Integer start;
    private Integer end;
    private Type type;
    private String ref;
    private String alt;

    public GenomicVariant (String chromosome, RefType ref_type, Integer start, Integer end, Type type, String ref, String alt) {
        this.chromosome = chromosome;
        this.ref_type = ref_type;
        this.start = start;
        this.end = end;
        this.type = type;
        this.ref = ref;
        this.alt = alt;
    }

    public GenomicVariant() {
        this.chromosome = this.ref = this.alt = null;
        this.start = this.end = null;
    }

    public String getChromosome() {
        return this.chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public RefType getRefType() {
        return this.ref_type;
    }

    public void setRefType(RefType ref_type) {
        this.ref_type = ref_type;
    }

    public Integer getStart() {
        return this.start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getEnd() {
        return this.end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
