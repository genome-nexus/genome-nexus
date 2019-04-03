package org.cbioportal.genome_nexus.util;

public class GenomicVariant {
    private String chromosome;
    private Integer start;
    private Integer end;
    private String type;
    private String ref;
    private String alt;

    public GenomicVariant (String chromosome, Integer start, Integer end, String type, String ref, String alt) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.type = type;
        this.ref = ref;
        this.alt = alt;
    }

    public GenomicVariant (){
        this.chromosome = this.ref = this.alt = null;
        this.start = this.end = null;
    }

    public String getChromosome() {
        return this.chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Integer getStart() {
        return this.start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
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
