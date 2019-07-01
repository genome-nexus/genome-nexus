package org.cbioportal.genome_nexus.util;
import java.util.regex.*;
import java.util.Arrays;

public class GenomicVariantUtil {

    public static GenomicVariant fromHgvs(String hgvs) {
        if (!isHgvs(hgvs))
            throw new IllegalArgumentException("hgvs is invalid");
        String chr = getPattern("^\\d+(?=:)", hgvs);
        Integer start = Integer.parseInt(getPattern("(?<=\\.)\\d+(?=[_ATGC])", hgvs));
        Integer end = getEndFromHgvs(hgvs, start);
        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]+)", hgvs);
        String ref =getRefFromHgvs(hgvs, type);
        String alt = getAltFromHgvs(hgvs, type);
        String[] types = { ">", "ins", "del", "delins" }; 
        
        if (!Arrays.asList(types).contains(type))
            throw new RuntimeException("only substitutions, insertions, deletions, and indels are supported");

        return new GenomicVariant(chr, start, end, type, ref, alt);
    }

    public static GenomicVariant fromRegion(String region) {
        if (!isRegion(region))
            throw new IllegalArgumentException("invalid region");
        String chr = getPattern("^\\d{1,2}(?=:)", region);
        Integer start = Integer.parseInt(getPattern("(?<=:)\\d+(?=-)", region));
        Integer end = Integer.parseInt(getPattern("(?<=-)\\d+(?=:)", region));
        String type = null;
        String ref = null;
        String alt = getPattern("(?<=:-?1/)[ATCG]+|-$", region);
        return new GenomicVariant(chr, start, end, type, ref, alt);
    }

    public static String toRegion(GenomicVariant variant){
        return variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd() + ":1/" + variant.getAlt();
    }

    public static boolean isHgvs(String variant) {
        return Pattern.matches("^\\d{1,2}:[gmcn]\\.\\d+[ATCG_]\\d*[a-z>]+?[ATCG]+$", variant);
    }

    public static boolean isRegion (String variant) {
        return Pattern.matches("^\\d{1,2}:\\d+-\\d+:-?1/([ATCG]+|-)$", variant);
    }

    // postcondition: returns a substring of hgvs that matched to the regex, or null if not matched
    private static String getPattern (String regex, String hgvs) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(hgvs);
        if (m.find()) 
            return hgvs.substring(m.start(), m.end());
        return null;
    }

    private static Integer getEndFromHgvs(String hgvs, Integer start) {
        try {
            return Integer.parseInt(getPattern("(?<=_)\\d+(?=[a-z]+)", hgvs));
        } catch (NumberFormatException e) {
            return start;
        }
    }

    private static String getRefFromHgvs(String hgvs, String type) {
        if (type.equals("del"))
            return getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
        if (type.equals(">"))
            return getPattern("(?<=\\d+)[ATCG]+(?=>)", hgvs);
        String ans = "";
        while (ans.length() < getPattern("(?<=[a-z+>])[ATCG]+$", hgvs).length())
            ans += "X";
        return ans;
    }

    private static String getAltFromHgvs(String hgvs, String type) {
        if (!type.equals("del"))
            return getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
        String ans = "";
        while (ans.length() < getPattern("(?<=[a-z+>])[ATCG]+$", hgvs).length())
            ans += "X";
        return ans;
    }


}