package org.cbioportal.genome_nexus.util;
import org.cbioportal.genome_nexus.model.GenomicLocation;

public class GenomicLocationUtil {
    public static String buildGenomicLocationStringFromGenomicLocation (GenomicLocation genomicLocation) {
        return (
            genomicLocation.getChromosome() + "," +
            genomicLocation.getStart() + "," +
            genomicLocation.getEnd() + "," +
            genomicLocation.getReferenceAllele() + "," +
            genomicLocation.getVariantAllele()
        );
    }
}
