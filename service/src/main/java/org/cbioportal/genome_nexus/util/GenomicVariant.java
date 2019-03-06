package org.cbioportal.genome_nexus.util;
public class GenomicVariant {
    private static GenomicVariantUtil util;

    public GenomicVariant(String chromosome, Integer start, Integer end, String ref, String alt) {
        util.setChromosome(chromosome);
        util.setStart(start);
        util.setEnd(end);
        util.setRef(ref);
        util.setAlt(alt);
    }

    public static GenomicVariant fromHgvs(String hgvs) {
        int chrToStart = hgvs.indexOf(":");
        int startToEnd = hgvs.indexOf("_");
        String ref = util.getRef(hgvs);
        int refIndex = hgvs.indexOf(ref);

        util.setChromosome(hgvs.substring(0, chrToStart));
        util.setStart(Integer.valueOf(hgvs.substring(chrToStart + 1, startToEnd)));
        util.setEnd(Integer.valueOf(hgvs.substring(startToEnd + 1, refIndex)));
        util.setRef(hgvs.substring(refIndex, refIndex + ref.length()));
        util.setAlt(hgvs.substring(refIndex + ref.length()));

        return new GenomicVariant(util.getChromosome(), util.getStart(), 
                                  util.getEnd(), util.getRef(), util.getAlt());
    }
}