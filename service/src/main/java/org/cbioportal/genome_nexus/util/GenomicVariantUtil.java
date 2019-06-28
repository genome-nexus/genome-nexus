package org.cbioportal.genome_nexus.util;
import java.util.regex.*;
import java.util.Arrays;

public class GenomicVariantUtil {
    public GenomicVariant variant = new GenomicVariant();

    public static GenomicVariant fromHgvs(String hgvs) {
        if (Pattern.matches("^d{1,2}:[gmcn]\\.\\d+_?\\d+?[ATCGa-z>]+?[ATCG]+$", hgvs))
            throw new IllegalArgumentException("hgvs is invalid");
        String chr = getPattern("^\\d+(?=:)", hgvs);
        Integer start = Integer.parseInt(getPattern("(?<=\\.)\\d+(?=[_ATGC])", hgvs));
        Integer end = getEnd(hgvs, start);
        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]+)", hgvs);
        String ref =getRef(hgvs, type);
        String alt = getAlt(hgvs, type);
        String[] types = { ">", "ins", "del", "delins" }; 
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
        return null;
    }

    private static Integer getEnd(String hgvs, Integer start) {
        try {
            return Integer.parseInt(getPattern("(?<=_)\\d+(?=[a-z]+)", hgvs));
        } catch (NumberFormatException e) {
            return start;
        }
    }

    private static String getRef(String hgvs, String type) {
        if (type.equals("del"))
            return getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
        if (type.equals(">"))
            return getPattern("(?<=\\d+)[ATCG]+(?=>)", hgvs);
        String ans = "";
        while (ans.length() < getPattern("(?<=[a-z+>])[ATCG]+$", hgvs).length())
            ans += "X";
        return ans;
    }

    private static String getAlt(String hgvs, String type) {
        if (!type.equals("del"))
            return getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
        String ans = "";
        while (ans.length() < getPattern("(?<=[a-z+>])[ATCG]+$", hgvs).length())
            ans += "X";
        return ans;
    }
}