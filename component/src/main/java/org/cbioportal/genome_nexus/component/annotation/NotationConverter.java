package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.util.GenomicVariant;
import org.cbioportal.genome_nexus.util.GenomicVariantUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class NotationConverter
{
    public static final String DEFAULT_DELIMITER = ",";

    public String hgvsNormalizer(String hgvs) {
        return hgvs.replace("chr","").replace("23:g.","X:g.").replace("24:g.","Y:g.");
    }

    public String chromosomeNormalizer(String chromosome) {
        return chromosome.trim().replace("chr","").replace("23","X").replace("24","Y");
    }

    public GenomicLocation hgvsgToGenomicLocation(String hgvsg) {
        GenomicLocation gl = new GenomicLocation();
        GenomicVariant gv = GenomicVariantUtil.fromHgvs(hgvsg);
        gl.setChromosome(gv.getChromosome());
        gl.setStart(gv.getStart());
        gl.setEnd(gv.getEnd());
        gl.setReferenceAllele(gv.getRef());
        gl.setVariantAllele(gv.getAlt());
        return gl;
    }

    public List<GenomicLocation> hgvsgToGenomicLocations(List<String> hgvsgs) {
        List<GenomicLocation> genomicLocations = new ArrayList();
        for (String hgvsg : hgvsgs) {
            genomicLocations.add(hgvsgToGenomicLocation(hgvsg));
        }
        return genomicLocations;
    }

    @Nullable
    public GenomicLocation parseGenomicLocation(String genomicLocation)
    {
        return this.parseGenomicLocation(genomicLocation, DEFAULT_DELIMITER);
    }

    @Nullable
    public GenomicLocation parseGenomicLocation(String genomicLocation, String delimiter)
    {
        if (genomicLocation == null) {
            return null;
        }

        String[] parts = genomicLocation.split(delimiter);
        GenomicLocation location = null;

        if (parts.length >= 5)
        {
            // trim all parts
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            location = new GenomicLocation();

            location.setChromosome(this.chromosomeNormalizer(parts[0]));
            location.setStart(Integer.parseInt(parts[1]));
            location.setEnd(Integer.parseInt(parts[2]));
            location.setReferenceAllele(parts[3]);
            location.setVariantAllele(parts[4]);
        }

        return location;
    }

    @Nullable
    public String genomicToHgvs(String genomicLocation)
    {
        return this.genomicToHgvs(this.parseGenomicLocation(genomicLocation));
    }

    @Nullable
    public String genomicToEnsemblRestRegion(String genomicLocation)
    {
        return this.genomicToEnsemblRestRegion(this.parseGenomicLocation(genomicLocation));
    }

    /*
     * Normalize genomic location:
     *
     * 1. Convert VCF style start,end,ref,alt to MAF by looking for common
     * prefix. (TODO: not sure if this is always a good idea)
     * 2. Normalize chromsome names.
     */
    public GenomicLocation normalizeGenomicLocation(GenomicLocation genomicLocation) {
        GenomicLocation normalizedGenomicLocation = new GenomicLocation();

        // normalize chromosome name
        String chr = this.chromosomeNormalizer(genomicLocation.getChromosome().trim());
        normalizedGenomicLocation.setChromosome(chr);

        // convert vcf style start,end,ref,alt to MAF style
        Integer start = genomicLocation.getStart();
        Integer end = genomicLocation.getEnd();
        String ref = genomicLocation.getReferenceAllele().trim();
        String var = genomicLocation.getVariantAllele().trim();

        String prefix = "";

        if(!ref.equals(var)) {
            prefix = longestCommonPrefix(ref, var);
        }

        if(!ref.equals(var)) {
            prefix = longestCommonPrefix(ref, var);
        }
//        else {
//            log.warn("Warning: Reference allele extracted from " + chr + ":" + start + "-" + end + " matches alt allele.");
//        }

        // Remove common prefix and adjust variant position accordingly
        if (prefix.length() > 0)
        {
            ref = ref.substring(prefix.length());
            var = var.substring(prefix.length());

            int nStart = start;

            nStart += prefix.length();

            if (ref.length() == 0) {
                nStart -= 1;
            }

            start = nStart;
        }

        normalizedGenomicLocation.setStart(start);
        normalizedGenomicLocation.setEnd(end);
        normalizedGenomicLocation.setReferenceAllele(ref);
        normalizedGenomicLocation.setVariantAllele(var);

        return normalizedGenomicLocation;
    }

    @Nullable
    public String genomicToHgvs(GenomicLocation genomicLocation)
    {
        if (genomicLocation == null) {
            return null;
        }

        GenomicLocation normalizedGenomicLocation = normalizeGenomicLocation(genomicLocation);
        String chr = normalizedGenomicLocation.getChromosome();
        Integer start = normalizedGenomicLocation.getStart();
        Integer end = normalizedGenomicLocation.getEnd();
        String ref = normalizedGenomicLocation.getReferenceAllele().trim();
        String var = normalizedGenomicLocation.getVariantAllele().trim();

        String hgvs;

        // cannot convert invalid locations
        // Ensembl uses a one-based coordinate system
        if (start < 1) {
            hgvs = null;
        }
        /*
         Process Insertion
         Example insertion: 17 36002277 36002278 - A
         Example output: 17:g.36002277_36002278insA
         */
        else if (ref.equals("-") || ref.length() == 0 || ref.equals("NA") || ref.contains("--"))
        {
            try {
                hgvs = chr + ":g." + start + "_" + String.valueOf(start + 1) + "ins" + var;
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        /*
         Process Deletion
         Example deletion: 1 206811015 206811016  AC -
         Example output:   1:g.206811015_206811016delAC
         */
        else if(var.equals("-") || var.length() == 0) {
            hgvs = chr + ":g." + start + "_" + end + "del" + ref;
        }
        /*
         Process ONP
         Example SNP   : 2 216809708 216809709 CA T
         Example output: 2:g.216809708_216809709delCAinsT
         */
        else if (ref.length() > 1 || var.length() > 1) {
            hgvs = chr + ":g." + start + "_" + end + "del" + ref + "ins" + var;
        }
        /*
         Process SNV
         Example SNP   : 2 216809708 216809708 C T
         Example output: 2:g.216809708C>T
         */
        else {
            hgvs = chr + ":g." + start + ref + ">" + var;
        }

        return hgvs;
    }

    @Nullable
    public String genomicToEnsemblRestRegion(GenomicLocation genomicLocation)
    {
        if (genomicLocation == null) {
            return null;
        }

        GenomicLocation normalizedGenomicLocation = normalizeGenomicLocation(genomicLocation);
        String chr = normalizedGenomicLocation.getChromosome();
        Integer start = normalizedGenomicLocation.getStart();
        Integer end = normalizedGenomicLocation.getEnd();
        String ref = normalizedGenomicLocation.getReferenceAllele().trim();
        String var = normalizedGenomicLocation.getVariantAllele().trim();

        String region;

        // cannot convert invalid locations
        // Ensembl uses a one-based coordinate system
        if (start < 1) {
            region = null;
        }
        /*
         Process Insertion
         Example insertion: 17 36002277 36002278 - A
         Example output: 17:36002278-36002277:1/A
         */
        else if (ref.equals("-") || ref.length() == 0 || ref.equals("NA") || ref.contains("--"))
        {
            try {
                region = chr + ":" + String.valueOf(start + 1) + "-" + start  + ":1/" + var;
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        /*
         Process Deletion
         Example deletion: 1 206811015 206811016  AC -
         Example output:   1:206811015-206811016:1/-
         */
        else if(var.equals("-") || var.length() == 0) {
            region = chr + ":" + start + "-" + end + ":1/-";
        }
        /*
         Process ONP
         Example SNP   : 2 216809708 216809709 CA T
         Example output: 2:216809708-216809709:1/T
         */
        else if (ref.length() > 1 || var.length() > 1) {
            region = chr + ":" + start + "-" + end + ":1/" + var;
        }
        /*
         Process SNV
         Example SNP   : 2 216809708 216809708 C T
         Example output: 2:216809708-216809708:1/T
         */
        else {
            region = chr + ":" + start + "-" + start + ":1/" + var;
        }

        return region;
    }

    @NotNull
    public Map<String, GenomicLocation> genomicToHgvsMap(List<GenomicLocation> genomicLocations)
    {
        Map<String, GenomicLocation> variantToGenomicLocation = new LinkedHashMap<>();

        // convert genomic location to hgvs notation (there is always 1-1 mapping)
        for (GenomicLocation location : genomicLocations) {
            String hgvs = this.genomicToHgvs(location);

            if (hgvs != null) {
                variantToGenomicLocation.put(hgvs, location);
            }
        }

        return variantToGenomicLocation;
    }

    public List<String> genomicToHgvs(List<GenomicLocation> genomicLocations)
    {
        List<String> hgvsList = new ArrayList<>();
        
        for (GenomicLocation location : genomicLocations) { 
            String hgvs = this.genomicToHgvs(location);
            if (hgvs != null) {
                hgvsList.add(hgvs);
            }
        }
        
        return hgvsList;
    }

    @NotNull
    public Map<String, GenomicLocation> genomicToEnsemblRestRegionMap(List<GenomicLocation> genomicLocations)
    {
        Map<String, GenomicLocation> variantToGenomicLocation = new LinkedHashMap<>();

        // convert genomic location to ensembl rest region notation (there is always 1-1 mapping)
        for (GenomicLocation location : genomicLocations) {
            String ensemblRestRegion = this.genomicToEnsemblRestRegion(location);

            if (ensemblRestRegion != null) {
                variantToGenomicLocation.put(ensemblRestRegion, location);
            }
        }

        return variantToGenomicLocation;
    }

    public List<String> genomicToEnsemblRestRegion(List<GenomicLocation> genomicLocations)
    {
        List<String> ensemblRestRegionsList = new ArrayList<>();
        for (GenomicLocation location : genomicLocations) { 
            String ensemblRestRegion = this.genomicToEnsemblRestRegion(location);
            if (ensemblRestRegion != null) {
                ensemblRestRegionsList.add(ensemblRestRegion);
            }
        }

        return ensemblRestRegionsList;
    }

    // TODO factor out to a utility class as a static method if needed
    @NotNull
    public String longestCommonPrefix(String str1, String str2)
    {
        if (str1 == null || str2 == null) {
            return "";
        }

        for (int prefixLen = 0; prefixLen < str1.length(); prefixLen++)
        {
            char c = str1.charAt(prefixLen);

            if (prefixLen >= str2.length() ||
                str2.charAt(prefixLen) != c)
            {
                // mismatch found
                return str2.substring(0, prefixLen);
            }
        }

        return str1;
    }
}
