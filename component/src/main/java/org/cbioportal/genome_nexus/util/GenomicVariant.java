package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.model.VariantType;

public class GenomicVariant {

    static enum RefType {
        GENOMIC,
        MITOCHONDRIAL // for future development (not yet implemented)
    }

    private String chromosome;
    private RefType refType;
    private Integer start;
    private Integer end;
    private VariantType type;
    private String ref;
    private String alt;

    public GenomicVariant() {
        this.chromosome = null;
        this.refType = null;
        this.start = null;
        this.end = null;
        this.type = null;
        this.ref = null;
        this.alt = null;
    }

    public GenomicVariant (String chromosome, RefType refType, Integer start, Integer end, VariantType type, String ref, String alt) {
        this.chromosome = chromosome;
        this.refType = refType;
        this.start = start;
        this.end = end;
        this.type = type;
        this.ref = ref;
        this.alt = alt;
    }

    public String getChromosome() {
        return this.chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public RefType getRefType() {
        return this.refType;
    }

    public void setRefType(RefType refType) {
        this.refType = refType;
    }

    public Integer getStart() {
        return this.start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public VariantType getType() {
        return this.type;
    }

    public void setType(VariantType type) {
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
