package org.cbioportal.genome_nexus.util;

public class GenomicVariantUtil {
    public GenomicVariant variant = new GenomicVariant();

    public static GenomicVariant fromHgvs(String hgvs) {
        int chrToType = hgvs.indexOf(":");
        int typeToStart = hgvs.indexOf(".");
        int startToEnd = hgvs.indexOf("_");
        String ref = getRef(hgvs);
        String type = getHgvsType(hgvs);
        int typeIndex = hgvs.indexOf(type);
        String alt = getAlt(hgvs);

        return new GenomicVariant(hgvs.substring(0, chrToType),
                Integer.valueOf(hgvs.substring(typeToStart + 1, startToEnd)),
                Integer.valueOf(hgvs.substring(startToEnd + 1, typeIndex)),
                type, ref, alt);
    }

    public GenomicVariant getVariant() {
        return variant;
    }

    private static String getHgvsType(String hgvs) {
        String[] keys = { ">", "del", "dup", "inv", "ins", "con", "delins" };
        String ans = "";
        for (String key : keys) {
            if (hgvs.contains(key))
                ans = key;
        }
        return ans;
    }

    private static String getRef(String hgvs) {
        return "XXX";
    }

    private static String getAlt(String hgvs){
        return "";
    }
}