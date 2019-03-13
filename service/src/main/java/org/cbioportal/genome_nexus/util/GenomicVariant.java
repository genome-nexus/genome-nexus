package org.cbioportal.genome_nexus.util;
public class GenomicVariant {
    public static GenomicVariantUtil util = new GenomicVariantUtil();

    public GenomicVariant (String chromosome, Integer start, Integer end, String ref, String alt) {
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

        return new GenomicVariant(hgvs.substring(0, chrToStart), 
                                 Integer.valueOf(hgvs.substring(chrToStart + 1, startToEnd)), 
                                 Integer.valueOf(hgvs.substring(startToEnd + 1, refIndex)), 
                                 hgvs.substring(refIndex, refIndex + ref.length()), 
                                 hgvs.substring(refIndex + ref.length()));
    }

    public GenomicVariantUtil getUtil(){
        return util;
    }
}