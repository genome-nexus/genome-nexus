package org.cbioportal.genome_nexus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileNotFoundException;

public class GenomicVariantUtil {

    public static GenomicVariant fromHgvs(String hgvs) {
        if (!isHgvs(hgvs))
            throw new IllegalArgumentException("hgvs is invalid");
        String chr = getPattern("^\\d+(?=:)", hgvs);
        String ref_type = getPattern("(?<=:)[cgmrp](?=.)", hgvs);
        Integer start = Integer.parseInt(getPattern("(?<=\\.)\\d+(?=[_ATGC])", hgvs));
        Integer end = getEndFromHgvs(hgvs, start);
        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]+)", hgvs);
        String ref =getRefFromHgvs(hgvs, type);
        String alt = getAltFromHgvs(hgvs, type);
        String[] types = { ">", "ins", "del", "delins" }; // supported variant types
        String[] ref_types = {"g", "c", "m"}; // supported reference genome types 

        if (!Arrays.asList(ref_types).contains(ref_type))
            throw new RuntimeException("protein and rna hgvs forms are not supported");
        
        if (!Arrays.asList(types).contains(type))
            throw new RuntimeException("only substitutions, insertions, deletions, and indels are supported");

        return new GenomicVariant(chr, ref_type, start, end, type, ref, alt);
    }

    public static GenomicVariant fromRegion(String region) {
        if (!isRegion(region))
            throw new IllegalArgumentException("region is invalid");
        String chr = getPattern("^\\d{1,2}(?=:)", region);
        String ref_type = null;
        Integer start = Integer.parseInt(getPattern("(?<=:)\\d+(?=-)", region));
        Integer end = Integer.parseInt(getPattern("(?<=-)\\d+(?=:)", region));
        String type = null;
        String ref = null;
        String alt = getPattern("(?<=:-?1/)[ATCG]+|-$", region);
        return new GenomicVariant(chr, ref_type, start, end, type, ref, alt);
    }

    public static String toRegion(GenomicVariant variant){
        return variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd() + ":1/" + variant.getAlt();
    }

    public static String toHgvs(GenomicVariant variant) {
        if (variant.getType().equals(">"))
            return variant.getChromosome() + ":" + variant.getRefType() + "." + variant.getStart() + variant.getRef()
                + ">" + variant.getAlt();
        if (variant.getType().equals("del"))
            return variant.getChromosome() + ":" + variant.getRefType() + "." + variant.getStart() + "_" + variant.getEnd()
                + variant.getType() + variant.getRef();
        return variant.getChromosome() + ":" + variant.getRefType() + "." + variant.getStart() + "_" + variant.getEnd()
                + variant.getType() + variant.getAlt();
    }

    public static ArrayList<String> getMafs(String maf_file) {
        if (!isMafFile(maf_file))
            throw new IllegalArgumentException("maf file not found");
        File file = new File(maf_file);
        Scanner sc;
        try {
            sc = new Scanner(file);
            ArrayList<String> list = new ArrayList<String>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.startsWith("#"))
                    list.add(line);
            }
            sc.close();
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GenomicVariant fromMaf(String maf, String[] key) {
        String[] arr = maf.split("\\t");
        // [Chromosome, Start_Position, End_Position, Reference_Allele,
        // Tumor_Seq_Allele]
        int chr_index = 0;
        int start_index = 0;
        int end_index = 0;
        int ref_index = 0;
        int alt_index = 0;
        for (int i = 0; i < key.length; i++) {
            switch (key[i]) {
            case "Chromosome":
                chr_index = i;
                break;
            case "Start_Position":
                start_index = i;
                break;
            case "End_Position":
                end_index = i;
                break;
            case "Reference_Allele":
                ref_index = i;
                break;
            case "Tumor_Seq_Allele":
                alt_index = i;
                break;
            }
        }

        return new GenomicVariant(arr[chr_index], null, Integer.parseInt(arr[start_index]),
                Integer.parseInt(arr[end_index]), null, arr[ref_index], arr[alt_index]);
    }

    public static boolean isHgvs(String variant) {
        return Pattern.matches("^\\d{1,2}:[cgmrp]\\.\\d+[ATCG_]\\d*[a-z>]+?[ATCG]+$", variant);
    }

    public static boolean isRegion(String variant) {
        return Pattern.matches("^\\d{1,2}:\\d+-\\d+:-?1/([ATCG]+|-)$", variant);
    }

    public static boolean isMafFile(String file) {
        return Pattern.matches("^[\\w\\-\\.]+.maf$", file);
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