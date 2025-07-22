package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.GenomicLocation;

public class GenomicLocationUtil {
    public enum MutationType {
        INS,
        DEL,
        SNP,
        INDEL
    }

    public static MutationType getTypeFromGenomicLocation(String genomicLocationString) {
        NotationConverter notationConverter = new NotationConverter();
        GenomicLocation genomicLocation = notationConverter.parseGenomicLocation(genomicLocationString);
        if (genomicLocation.getReferenceAllele().equals("-")) {
            return MutationType.INS;
        }
        else if (genomicLocation.getVariantAllele().equals("-")) {
            return MutationType.DEL;
        }
        else if (genomicLocation.getReferenceAllele().length() == 1 && genomicLocation.getVariantAllele().length() == 1) {
            return MutationType.SNP;
        }
        else {
            return MutationType.INDEL;
        }

    }

    public static String getReverseStrandAllele(String allele) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allele.length(); i++) {
            char nucleotide = allele.charAt(i);
            if (nucleotide == 'A') {
                sb.append('T');
            } else if (nucleotide == 'T') {
                sb.append('A');
            } else if (nucleotide == 'C') {
                sb.append('G');
            } else if (nucleotide == 'G') {
                sb.append('C');
            } else {
                sb.append(nucleotide);
            } 
        }
        return sb.toString();
    }
}