package org.cbioportal.genome_nexus.util;

public class GenomicVariantUtil {
    private String chromosome;
    private Integer start;
    private Integer end;
    private String ref;
    private String alt;

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

    public String getRef(String hgvs) {
        String[] refs = { ">", "del", "dup", "inv", "ins", "con", "delins"};
        //
        String ans = "";
        for (String ref : refs) {
            if (hgvs.contains(ref))
                ans = ref;
        }
        return ans;
    }
}