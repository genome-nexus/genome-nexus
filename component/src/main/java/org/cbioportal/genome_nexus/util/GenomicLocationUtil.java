package org.cbioportal.genome_nexus.util;

import java.util.Map;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantType;
import org.jetbrains.annotations.NotNull;

public class GenomicLocationUtil {
    private static final Map<String, String> InversionMap = Map.of(
        "A", "T",
        "T", "A",
        "C", "G",
        "G", "C",
        "X", "X"
    );

        // TODO factor out to a utility class as a static method if needed
    @NotNull
    public static String longestCommonPrefix(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return "";
        }
        for (int prefixLen = 0; prefixLen < str1.length(); prefixLen++) {
            char c = str1.charAt(prefixLen);
            if (prefixLen >= str2.length() || str2.charAt(prefixLen) != c) {
                // mismatch found
                return str2.substring(0, prefixLen);
            }
        }
        return str1;
    }

    public static VariantType getTypeFromGenomicLocation(GenomicLocation genomicLocation) {
        String refAllele = genomicLocation.getReferenceAllele();
        String varAllele = genomicLocation.getVariantAllele();
        String prefix = "";
        if (!refAllele.equals(varAllele) && !refAllele.matches("X+")) { 
            prefix = longestCommonPrefix(refAllele, varAllele);
        }
        if (prefix.length() > 0) {
            refAllele = refAllele.substring(prefix.length());
            varAllele = varAllele.substring(prefix.length());
        }
        // Determine variant type
        if ((refAllele.equals("-") || refAllele.equals("")) && !varAllele.equals("-")) {
            return VariantType.INSERTION;
        }

        if (!refAllele.equals("-") && (varAllele.equals("-") || varAllele.equals(""))) {
            return VariantType.DELETION;
        }

        else if (varAllele.equals(refAllele + refAllele)) {
            return VariantType.DUPLICATION;
        }

        else if (refAllele.length() > 1 && varAllele.length() > 1 && refAllele.length() == varAllele.length() && isInversion(refAllele, varAllele)) {
            return VariantType.INVERSION;
        }

        else if (refAllele.length() == 1 && varAllele.length() == 1 && !refAllele.equals(varAllele)) {
            return VariantType.SUBSTITUTION;
        }

        else if (refAllele.length() >= 1 && varAllele.length() >= 1) {
            return VariantType.INDEL;
        }

        return VariantType.UNKNOWN;
    }

    private static boolean isInversion(String ref, String var) {
        // Check if the variant is the complementary base of the reference
        for (int i = 0; i < ref.length(); i++) {
            if (!InversionMap.getOrDefault(String.valueOf(ref.charAt(i)), "")
                .equals(String.valueOf(var.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}