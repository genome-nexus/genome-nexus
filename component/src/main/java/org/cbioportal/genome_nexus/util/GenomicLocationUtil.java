package org.cbioportal.genome_nexus.util;

import java.util.Map;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantType;

public class GenomicLocationUtil {
    private static final Map<String, String> InversionMap = Map.of(
        "A", "T",
        "T", "A",
        "C", "G",
        "G", "C",
        "X", "X"
    );

    public static VariantType getTypeFromGenomicLocation(GenomicLocation genomicLocation) {
        String refAllele = genomicLocation.getReferenceAllele();
        String varAllele = genomicLocation.getVariantAllele();
        // Determine variant type
        if (refAllele.equals("-") && !varAllele.equals("-")) {
            // Pure insertion
            return VariantType.INSERTION;
        }

        if (!refAllele.equals("-") && varAllele.equals("-")) {
            // Pure deletion
            return VariantType.DELETION;
        }

        else if (varAllele.equals(refAllele + refAllele)) {
            // Duplication 
            return VariantType.DUPLICATION;
        }

        // Inversion check for longer strings
        else if (refAllele.length() > 1 && varAllele.length() > 1 && refAllele.length() == varAllele.length() && isInversion(refAllele, varAllele)) {
            return VariantType.INVERSION;
        }

        // Substitution check for single character
        else if (refAllele.length() == 1 && varAllele.length() == 1 && !refAllele.equals(varAllele)) {
            return VariantType.SUBSTITUTION;
        }

        else if (refAllele.length() >= 1 && varAllele.length() >= 1) {
            // Deletion-insertion if not a simple substitution
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