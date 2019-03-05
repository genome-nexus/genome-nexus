package org.cbioportal.genome_nexus.util;
public class GenomicVariant {

    private static String chromosome;
    private static Integer start;
    private static Integer end;
    private static String ref;
    private static String alt;

    public GenomicVariant(String chromosome, Integer start, Integer end, String ref, String alt) {
        GenomicVariant.chromosome = chromosome;
        GenomicVariant.start = start;
        GenomicVariant.end = end;
        GenomicVariant.ref = ref;
        GenomicVariant.alt = alt;
    }

    public static GenomicVariant fromHgvs(String hgvs) {
        int chrToStart = hgvs.indexOf(":");
        int startToEnd = hgvs.indexOf("_");
        String ref = getRef(hgvs);
        int refIndex = hgvs.indexOf(ref);

        GenomicVariant.chromosome = hgvs.substring(0, chrToStart);
        GenomicVariant.start = Integer.valueOf(hgvs.substring(chrToStart + 1, startToEnd));
        GenomicVariant.end = Integer.valueOf(hgvs.substring(startToEnd + 1, refIndex));
        GenomicVariant.ref = hgvs.substring(refIndex, refIndex + ref.length());
        GenomicVariant.alt = hgvs.substring(refIndex + ref.length());

        return new GenomicVariant(chromosome, Integer.valueOf(start), Integer.valueOf(end), GenomicVariant.ref, alt);
    }

    private static String getRef(String hgvs){
        String[] refs = {">", "del", "dup", "inv", "ins", "con", "delins"};
        //
        String ans = "";
        for (String ref: refs){ 
            if (hgvs.contains(ref))
                ans = ref;
        }
        return ans;
    }
}