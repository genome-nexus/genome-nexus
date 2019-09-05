package org.cbioportal.genome_nexus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cbioportal.genome_nexus.util.exception.InvalidHgvsException;
import org.cbioportal.genome_nexus.util.exception.RefTypeNotSupportedException;
import org.cbioportal.genome_nexus.util.exception.TypeNotSupportedException;

public class GenomicVariantUtil {

    public static GenomicVariant fromHgvs(String hgvs) {
        if (!isHgvs(hgvs)) {
            throw new InvalidHgvsException();
        }
        String chr = getPattern("^.+(?=:)", hgvs);
        RefType ref_type = getRefTypeFromHgvs(hgvs);
        Integer start = Integer.parseInt(getPattern("(?<=\\.)\\d+(?=[_ATGC])", hgvs));
        Integer end = getEndFromHgvs(hgvs, start);
        Type type = getTypeFromHgvs(hgvs);
        String ref = getRefFromHgvs(hgvs, type);
        String alt = getAltFromHgvs(hgvs, type);
        return new GenomicVariant(chr, ref_type, start, end, type, ref, alt);
    }

    public static GenomicVariant fromRegion(String region) {
        if (!isRegion(region)) {
            throw new IllegalArgumentException("region is invalid");
        }
        String chr = getPattern("^\\d{1,2}(?=:)", region);
        RefType ref_type = null;
        Integer start = Integer.parseInt(getPattern("(?<=:)\\d+(?=-)", region));
        Integer end = Integer.parseInt(getPattern("(?<=-)\\d+(?=:)", region));
        Type type = null;
        String ref = null;
        String alt = getPattern("(?<=:-?1/)[ATCG]+|-$", region);
        return new GenomicVariant(chr, ref_type, start, end, type, ref, alt);
    }

    public static String toRegion(GenomicVariant variant) {
        return variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd() + ":1/" + variant.getAlt();
    }

    public static ArrayList<String> getMafs(String maf_file) {
        if (!isMafFile(maf_file)) {
            throw new RuntimeException("maf file not found");
        }
        ArrayList<String> list = null;
        try {
            list = new ArrayList<String>();
            File file = new File(maf_file);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.startsWith("#")) {
                    list.add(line);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static GenomicVariant fromMaf(String maf, String[] key) {
        String[] arr = maf.split("\\s");
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
        // TODO this pattern does not include MT, just check whether g is included
        // return Pattern.matches("^[1-9][0-9]?:[cgmrp]\\.\\d+[ATCG_]\\d*[a-z>]*[ATCG]*$", variant);
        return variant.contains("g.");
    }

    public static boolean isRegion(String variant) {
        // TODO this pattern does not include MT, just check whether / is included
        // return Pattern.matches("^\\d{1,2}:\\d+-\\d+:-?1/([ATCG]+|-)$", variant);
        return variant.contains("/");
    }

    public static boolean isMafFile(String file) {
        return Pattern.matches("^[\\w\\-\\./]+(\\.maf)|^[\\w\\-\\./]+(\\.tsv)|^[\\w\\-\\./]+(\\.txt)$$", file);
    }

    // postcondition: returns a substring of hgvs that matched to the regex, or null if not matched
    private static String getPattern(String regex, String hgvs) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(hgvs);
        if (m.find()) { 
            return hgvs.substring(m.start(), m.end());
        }
        return null;
    }

    private static Integer getEndFromHgvs(String hgvs, Integer start) {
        try {
            return Integer.parseInt(getPattern("(?<=_)\\d+(?=[a-z]+)", hgvs));
        } catch (NumberFormatException e) {
            return start;
        }
    }

    private static String getRefFromHgvs(String hgvs, Type type) {
        if (type.equals(Type.DELETION)) {
            String ref = getPattern("(?<=[a-z+>])[ATCG]*$", hgvs);
            if (ref.equals(null)) {
                return "";
            }
            return ref;
        } else if (type.equals(Type.SUBSTITUTION)) {
            return getPattern("(?<=\\d+)[ATCG]+(?=>)", hgvs);
        }
        String ans = "";
        while (ans.length() < getPattern("(?<=[a-z+>])[ATCG]+$", hgvs).length()) {
            ans += "X";
        }
        return ans;
    }

    private static String getAltFromHgvs(String hgvs, Type type) {
        if (!type.equals(Type.DELETION)) {
            return getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
        }
        String alt = getPattern("(?<=[a-z+>])[ATCG]*$", hgvs);
        if (alt.equals(null)) {
            return "";
        }
        String ans = "";
        while (ans.length() < alt.length()) {
            ans += "X";
        }
        return ans;
    }

    private static RefType getRefTypeFromHgvs (String hgvs) {
        String ref_type = getPattern("(?<=:)[cgmrp](?=.)", hgvs);
        switch(ref_type) {
            case "g":
                return RefType.GENOMIC;
            case "m":
                return RefType.MITOCHONDRIAL;
        }
        throw new RefTypeNotSupportedException();
    }

    private static Type getTypeFromHgvs(String hgvs) {
        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]*)", hgvs);
        switch(type) {
            case ">":
                return Type.SUBSTITUTION;
            case "ins":
                return Type.INSERTION;
            case "del":
                return Type.DELETION;
            case "delins":
                return Type.INDEL;
        }
        throw new TypeNotSupportedException();
    }
}