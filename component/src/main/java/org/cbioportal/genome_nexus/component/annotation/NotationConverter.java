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
public class NotationConverter {
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
        gl.setOriginalInput(hgvsg);
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
    public GenomicLocation parseGenomicLocation(String genomicLocation) {
        return parseGenomicLocation(genomicLocation, DEFAULT_DELIMITER);
    }

    @Nullable
    public GenomicLocation parseGenomicLocation(String genomicLocation, String delimiter) {
        if (genomicLocation == null) {
            return null;
        }
        String[] parts = genomicLocation.split(delimiter);
        GenomicLocation location = null;
        if (parts.length >= 5) {
            // trim all parts
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            location = new GenomicLocation();
            location.setChromosome(chromosomeNormalizer(parts[0]));
            location.setStart(parts[1].length() > 0 ? Integer.parseInt(parts[1]) : null);
            location.setEnd(parts[2].length() > 0 ? Integer.parseInt(parts[2]) : null);
            location.setReferenceAllele(parts[3]);
            location.setVariantAllele(parts[4]);
            location.setOriginalInput(genomicLocation);
        }

        return location;
    }

    @Nullable
    public String genomicToHgvs(String genomicLocation) {
        return genomicToHgvs(parseGenomicLocation(genomicLocation));
    }

    @Nullable
    public String genomicToEnsemblRestRegion(String genomicLocation) {
        return genomicToEnsemblRestRegion(parseGenomicLocation(genomicLocation));
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
        // if original input is set in the incoming genomic location object then use the same value
        // for the normalized genomic location object returned, otherwise set it to the
        // string representation of the incoming genomic location object
        if (genomicLocation.getOriginalInput() != null && !genomicLocation.getOriginalInput().isEmpty()) {
            normalizedGenomicLocation.setOriginalInput(genomicLocation.getOriginalInput());
        } else {
            normalizedGenomicLocation.setOriginalInput(genomicLocation.toString());
        }

        // normalize chromosome name
        String chr = chromosomeNormalizer(genomicLocation.getChromosome().trim());
        normalizedGenomicLocation.setChromosome(chr);

        // convert vcf style start,end,ref,alt to MAF style
        Integer start = genomicLocation.getStart();
        Integer end = genomicLocation.getEnd();
        String ref = genomicLocation.getReferenceAllele().trim();
        String var = genomicLocation.getVariantAllele().trim();

        String prefix = "";

        if (!ref.equals(var)) {
            prefix = longestCommonPrefix(ref, var);
        }

        // Remove common prefix and adjust variant position accordingly
        if (prefix.length() > 0) {
            ref = ref.substring(prefix.length());
            var = var.substring(prefix.length());
            int nStart = start + prefix.length();
            if (ref.length() == 0) {
                nStart -= 1;
            }
            start = nStart;
        }
        end = harmonizeGenomicLocationCoordinate(start, end, ref);
        normalizedGenomicLocation.setStart(start);
        normalizedGenomicLocation.setEnd(end);
        normalizedGenomicLocation.setReferenceAllele(ref);
        normalizedGenomicLocation.setVariantAllele(var);
        return normalizedGenomicLocation;
    }

    public Integer harmonizeGenomicLocationCoordinate(Integer start, Integer end, String ref) {
        if (end == null) {
            // if end position is missing, give a default value
            end = start;
        }
        if (ref.equals("-") || ref.length() == 0 || ref.equals("NA") || ref.contains("--")) {
            // insertion variants: end = start + 1
            if (end != start + 1) {
                end = start + 1;
            }
        }
        else {
            // all deletion, delins, and SNV
            // for single alleledel, delins and SNV, ref.length() = 1, so end = start
            if (end != start + ref.length() - 1) {
                end = start + ref.length() - 1;
            }
        }
        return end;
    }

    @Nullable
    public String genomicToHgvs(GenomicLocation genomicLocation) {
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
        if (start < 1) {
            // cannot convert invalid locations
            // Ensembl uses a one-based coordinate system
            hgvs = null;
        } else if (ref.equals("-") || ref.length() == 0 || ref.equals("NA") || ref.contains("--")) {
            /*
            Process Insertion
            Example insertion: 17 36002277 36002278 - A
            Example output: 17:g.36002277_36002278insA
            */
            try {
                hgvs = chr + ":g." + start + "_" + String.valueOf(start + 1) + "ins" + var;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (var.equals("-") || var.length() == 0 || var.equals("NA") || var.contains("--")) {
            if (ref.length() == 1) {
                /*
                Process Deletion (single positon)
                Example deletion: 13 32914438 32914438 T -
                Example output:   13:g.32914438del
                */
                hgvs = chr + ":g." + start + "del";
            }
            else {
                /*
                Process Deletion (multiple postion)
                Example deletion: 1 206811015 206811016  AC -
                Example output:   1:g.206811015_206811016del
                */
                hgvs = chr + ":g." + start + "_" + end + "del";
            } 
        } else if (ref.length() > 1 && var.length() >= 1) {
            /*
            Process ONP (multiple deletion insertion)
            Example INDEL   : 2 216809708 216809709 CA T
            Example output: 2:g.216809708_216809709delinsT
            */
            hgvs = chr + ":g." + start + "_" + end + "delins" + var;
        } else if (ref.length() == 1 && var.length() > 1) {
            /*
            Process ONP (single deletion insertion)
            Example INDEL   : 17 7579363 7579363 A TTT
            Example output: 17:g.7579363delinsTTT
            */
            hgvs = chr + ":g." + start + "delins" + var;
        } else {
            /*
            Process SNV
            Example SNP   : 2 216809708 216809708 C T
            Example output: 2:g.216809708C>T
            */
            hgvs = chr + ":g." + start + ref + ">" + var;
        }
        return hgvs;
    }

    @Nullable
    public String genomicToEnsemblRestRegion(GenomicLocation genomicLocation) {
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
        // If the var allele is "-", then it is a deletion
        // A ref allele of "-" could be an insertion or deletion, so this check should come first.
        } else if (var.equals("-") || var.length() == 0) {
            /*
            Process Deletion
            Example deletion: 1 206811015 206811016  AC -
            Example output:   1:206811015-206811016:1/-

            Example deletion: 11 2133018 2133018 - -
            Example output: 11:2133018-2133018:1/-
            */
            region = chr + ":" + start + "-" + end + ":1/-";
        } else if (ref.equals("-") || ref.length() == 0 || ref.equals("NA") || ref.contains("--")) {
            /*
            Process Insertion
            Example insertion: 17 36002277 36002278 - A
            Example output: 17:36002278-36002277:1/A
            */
            try {
                // We follow the rule for insertions described here: https://useast.ensembl.org/info/docs/tools/vep/vep_formats.html#default
                // The VEP differentiates between ins and delins by swapping the start and end positions for insertions.
                region = chr + ":" + String.valueOf(start + 1) + "-" + start  + ":1/" + var;
            }
            catch (NumberFormatException e) {
                return null;
            }
        } else if (ref.length() > 1 || var.length() > 1) {
            /*
            Process ONP
            Example SNP   : 2 216809708 216809709 CA T
            Example output: 2:216809708-216809709:1/T
            */
            region = chr + ":" + start + "-" + end + ":1/" + var;
        } else {
            /*
            Process SNV
            Example SNP   : 2 216809708 216809708 C T
            Example output: 2:216809708-216809708:1/T
            */
            region = chr + ":" + start + "-" + start + ":1/" + var;
        }
        return region;
    }

    @NotNull
    public Map<String, GenomicLocation> genomicToHgvsMap(List<GenomicLocation> genomicLocations) {
        Map<String, GenomicLocation> variantToGenomicLocation = new LinkedHashMap<>();
        // convert genomic location to hgvs notation (there is always 1-1 mapping)
        for (GenomicLocation location : genomicLocations) {
            String hgvs = genomicToHgvs(location);
            if (hgvs != null) {
                variantToGenomicLocation.put(hgvs, location);
            }
        }
        return variantToGenomicLocation;
    }

    public List<String> genomicToHgvs(List<GenomicLocation> genomicLocations) {
        List<String> hgvsList = new ArrayList<>();
        for (GenomicLocation location : genomicLocations) {
            String hgvs = genomicToHgvs(location);
            if (hgvs != null) {
                hgvsList.add(hgvs);
            }
        }
        return hgvsList;
    }

    @NotNull
    public Map<String, GenomicLocation> genomicToEnsemblRestRegionMap(List<GenomicLocation> genomicLocations) {
        Map<String, GenomicLocation> variantToGenomicLocation = new LinkedHashMap<>();
        // convert genomic location to ensembl rest region notation (there is always 1-1 mapping)
        for (GenomicLocation location : genomicLocations) {
            String ensemblRestRegion = genomicToEnsemblRestRegion(location);
            if (ensemblRestRegion != null) {
                variantToGenomicLocation.put(ensemblRestRegion, location);
            }
        }
        return variantToGenomicLocation;
    }

    public List<String> genomicToEnsemblRestRegion(List<GenomicLocation> genomicLocations) {
        List<String> ensemblRestRegionsList = new ArrayList<>();
        for (GenomicLocation location : genomicLocations) {
            String ensemblRestRegion = genomicToEnsemblRestRegion(location);
            if (ensemblRestRegion != null) {
                ensemblRestRegionsList.add(ensemblRestRegion);
            }
        }
        return ensemblRestRegionsList;
    }

    // TODO factor out to a utility class as a static method if needed
    @NotNull
    public String longestCommonPrefix(String str1, String str2) {
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

    @Nullable
    public String getGenomicLocationExplanation (GenomicLocation genomicLocation) {
        if (genomicLocation == null) {
            return null;
        }

        StringBuilder explanation = new StringBuilder(); 
        GenomicLocation normalizedGenomicLocation = normalizeGenomicLocation(genomicLocation);

        Integer start = genomicLocation.getStart();
        Integer end = genomicLocation.getEnd();
        String ref = genomicLocation.getReferenceAllele().trim();
        String var = genomicLocation.getVariantAllele().trim();
        String commonBases = longestCommonPrefix(ref, var);
        Integer normalizedStart = normalizedGenomicLocation.getStart();
        Integer normalizedEnd = normalizedGenomicLocation.getEnd();
        String normalizedRef = normalizedGenomicLocation.getReferenceAllele().trim();
        String normalizedVar = normalizedGenomicLocation.getVariantAllele().trim();

        // start
        if (!start.equals(normalizedStart)) {
            explanation.append(String.format("Start position changes from %d to %d is attributed to the presence of common bases %s. ", start, normalizedStart, commonBases));
        }

        // end
        if (end == null) {
            explanation.append(String.format("End position is missing, end position should be %d. ", normalizedEnd));
        }
        else {
            if (!end.equals(normalizedEnd)) {
                if (normalizedRef.equals("-") || normalizedRef.length() == 0 || normalizedRef.equals("NA") || normalizedRef.contains("--")) {
                    /*
                    Process Insertion end position
                    Example insertion: 17 36002277 36002278 - A
                    */
                    explanation.append(String.format("End position changes from %d to %d, end position should equal to (start + 1) to indicate the location of insertion. ", end, normalizedEnd));
                } else if (normalizedVar.equals("-") || normalizedVar.length() == 0 || normalizedVar.equals("NA") || normalizedVar.contains("--")) {
                    if (normalizedRef.length() == 1) {
                        /*
                        Process Deletion (single positon) end position
                        Example deletion: 13 32914438 32914438 T -
                        */
                        explanation.append(String.format("End position changes from %d to %d, end position should equal to start position for single nucleotide deletion variants. ", end, normalizedEnd));
                    }
                    else {
                        /*
                        Process Deletion (multiple postion) end position
                        Example deletion: 1 206811015 206811016  AC -
                        */
                        explanation.append(String.format("End position changes from %d to %d, end position should be the position of last deleted nucleotide. ", end, normalizedEnd));
                    } 
                } else if (normalizedRef.length() > 1 && normalizedVar.length() >= 1) {
                    /*
                    Process ONP (multiple deletion insertion) end position
                    Example INDEL   : 2 216809708 216809709 CA T
                    */
                    explanation.append(String.format("End position changes from %d to %d, end position should be the position of last deleted nucleotide. ", end, normalizedEnd));
                } else if (normalizedRef.length() == 1 && normalizedVar.length() > 1) {
                    /*
                    Process ONP (single deletion insertion) end position
                    Example INDEL   : 17 7579363 7579363 A TTT
                    */
                    explanation.append(String.format("End position changes from %d to %d, end position should be the position of last deleted nucleotide. ", end, normalizedEnd));
                } else {
                    /*
                    Process SNV end position
                    Example SNP   : 2 216809708 216809708 C T
                    */
                    explanation.append(String.format("End position changes from %d to %d, end position should equal to start position for SNV variants", end, normalizedEnd));
                }
            }
        }
        
        // ref
        if (!ref.equals(normalizedRef)) {
            explanation.append(String.format("Reference allele changes from %s to %s is attributed to the presence of common bases %s. ", ref, normalizedRef.length() > 0 ? normalizedRef : "-", commonBases));
        }

         // var
         if (!var.equals(normalizedVar)) {
            explanation.append(String.format("Variant allele changes from %s to %s is attributed to the presence of common bases %s. ", var, normalizedVar.length() > 0 ? normalizedVar : "-", commonBases));
        }

        return explanation.length() > 0 ? explanation.toString().trim() : null;
    }

    @Nullable
    public String getGenomicLocationExplanation (String genomicLocation) {
        return this.getGenomicLocationExplanation(this.parseGenomicLocation(genomicLocation));
    }
}
