package org.cbioportal.genome_nexus.util;
import java.util.regex.*;
import java.util.Arrays;

public class GenomicVariantUtil {
    public GenomicVariant variant = new GenomicVariant();

    public static GenomicVariant fromHgvs(String hgvs) {
        if (Pattern.matches("^d{1,2}:[cg]\\.\\d+_?\\d+?[ATCGa-z>]+?[ATCG]+$", hgvs))
            throw new IllegalArgumentException("hgvs is invalid");
        String chr = getPattern("^\\d+(?=:)", hgvs);
        Integer start = Integer.parseInt(getPattern("(?<=\\.)\\d+(?=[_ATGC])", hgvs));
        Integer end;

        try {
            end = Integer.parseInt(getPattern("(?<=_)\\d+(?=[a-z]+)", hgvs)); 
        } catch (NumberFormatException e) {
            end = start;
        }

        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]+)", hgvs);
        String ref = getPattern("(?<=\\d+)[ATCG]+(?=>)", hgvs);
        String alt = getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);

        if (ref.equals("")) ref = "XXX";
        String[] types = { ">", "ins", "del", "delins" }; //{ ">", "del", "dup", "inv", "ins", "con", "delins" };
        if (!Arrays.asList(types).contains(type))
            throw new RuntimeException("only substitutions, insertions, deletions, and indels are supported");

        return new GenomicVariant(chr, start, end, type, ref, alt);
    }

    public GenomicVariant getVariant() {
        return variant;
    }

    // postcondition: returns a substring of hgvs that matched to the regex, or null if not matched
    private static String getPattern (String regex, String hgvs) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(hgvs);
        if (m.find()) 
            return hgvs.substring(m.start(), m.end());
        return "";
    }
}