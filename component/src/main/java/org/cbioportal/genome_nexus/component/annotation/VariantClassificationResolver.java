package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VariantClassificationResolver
{
    public static Map<String, String> VARIANT_MAP = initVariantMap();

    private final TranscriptConsequencePrioritizer consequencePrioritizer;
    private final VariantTypeResolver variantTypeResolver;
    private final GenomicLocationResolver genomicLocationResolver;

    @Autowired
    public VariantClassificationResolver(
        VariantTypeResolver variantTypeResolver,
        TranscriptConsequencePrioritizer consequencePrioritizer,
        GenomicLocationResolver genomicLocationResolver
    ) {
        this.consequencePrioritizer = consequencePrioritizer;
        this.variantTypeResolver = variantTypeResolver;
        this.genomicLocationResolver = genomicLocationResolver;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation, TranscriptConsequence transcriptConsequence)
    {
        String variantClassification = null;
        String variantType = this.variantTypeResolver.resolve(variantAnnotation);
        Boolean isInframe = this.isInframe(variantAnnotation);

        // if not null, try to resolve the variant classification for the provided transcript
        if (transcriptConsequence != null)
        {
            variantClassification = this.resolveVariantClassification(
                this.consequencePrioritizer.pickHighestPriorityConsequence(transcriptConsequence.getConsequenceTerms()),
                    variantType, isInframe);
        }
        // if variant has no transcript and has intergenicConsequences instead, try to resolve the variant classification from intergenicConsequences
        else if (variantAnnotation != null && variantAnnotation.getIntergenicConsequences() != null && !variantAnnotation.getIntergenicConsequences().isEmpty()) {
            // pick the highest priority consequence from the intergenic consequences
            // get every getConsequenceTerms() from each intergenic consequence and put in a list
            variantClassification = this.resolveVariantClassification(
                this.consequencePrioritizer.pickHighestPriorityConsequence(
                    variantAnnotation.getIntergenicConsequences()
                    .stream()
                    .flatMap(consequence -> consequence.getConsequenceTerms()
                    .stream())
                    .collect(Collectors.toList())),
                    variantType,
                    isInframe
            );
        }
        // use the most severe consequence to resolve the variant classification
        else if (variantAnnotation != null)
        {
            variantClassification = resolveVariantClassification(
                variantAnnotation.getMostSevereConsequence(), variantType, isInframe);
        }

        return variantClassification;
    }

    @NotNull
    public Set<String> resolveAll(VariantAnnotation variantAnnotation, TranscriptConsequence transcriptConsequence)
    {
        Set<String> variantClassifications = new HashSet<>();
        String variantType = this.variantTypeResolver.resolve(variantAnnotation);
        Boolean isInframe = this.isInframe(variantAnnotation);

        // try to resolve all the variant classifications for the provided transcript
        for (String consequenceTerm : transcriptConsequence.getConsequenceTerms())
        {
            variantClassifications.add(this.resolveVariantClassification(consequenceTerm, variantType, isInframe));
        }

        return variantClassifications;
    }

    @NotNull
    public Boolean isInframe(VariantAnnotation variantAnnotation)
    {
        Boolean inframe = false;

        String referenceAllele = this.genomicLocationResolver.resolveReferenceAllele(variantAnnotation);
        String variantAllele = this.genomicLocationResolver.resolveVariantAllele(variantAnnotation);

        if (referenceAllele != null &&
            variantAllele != null)
        {
            inframe = Math.abs(this.calcAlleleLength(referenceAllele) - this.calcAlleleLength(variantAllele)) % 3 == 0;
        }

        return inframe;
    }

    @NotNull
    private Integer calcAlleleLength(String allele)
    {
        return allele == null || allele.equals("-") ? 0 : allele.length();
    }

    @NotNull
    private String resolveVariantClassification(String variant, String variantType, Boolean isInframe)
    {
        String defaultValue = "Targeted_Region";

        if (variant == null || (variantType == null && !variant.contains("splice"))) {
            return defaultValue;
        }
        else {
            variant = variant.toLowerCase();
        }

        if ((variant.equals("frameshift_variant") || variant.equals("protein_altering_variant") ||
            variant.equals("coding_sequence_variant")) &&
            !isInframe)
        {
            if (variantType.equals("DEL")) {
                return "Frame_Shift_Del";
            }
            else if (variantType.equals("INS")) {
                return "Frame_Shift_Ins";
            }
        }
        else if ((variant.equals("protein_altering_variant") || variant.equals("coding_sequence_variant")) &&
            isInframe)
        {
            if (variantType.equals("DEL")) {
                return "In_Frame_Del";
            }
            else if (variantType.equals("INS")) {
                return "In_Frame_Ins";
            }
        }

        return VARIANT_MAP.getOrDefault(variant, defaultValue);
    }

    public String resolveRevueVariantClassification(String revueVariantClassification) {
        if (revueVariantClassification == null) {
            return null;
        }
        switch (revueVariantClassification) {
            // Spilice exon skip:
            case "Splice_Exon_Skip_In_Frame":
                return "In_Frame_Del";
            case "Splice_Exon_Skip_Out_Of_Frame":
                return "Frame_Shift_Del";
            case "Splice_Exon_Skip_Non_Start":
                return "In_Frame_Del";
            // Splice exon extension:
            case "Splice_Exon_Extension_In_Frame":
                return "In_Frame_Ins";
            case "Splice_Exon_Extension_Out_Of_Frame":
                return "Frame_Shift_Ins";
            case "Splice_Exon_Extension_Nonsense":
                return "In_Frame_Ins";
            // Splice exon shortening:
            case "Splice_Exon_Shortening_In_Frame":
                return "In_Frame_Del";
            case "Splice_Exon_Shortening_Out_Of_Frame":
                return "Frame_Shift_Del";
            // Splice intron retention:
            case "Splice_Intron_Retention_In_Frame":
                return "In_Frame_Ins";
            case "Splice_Intron_Retention_Out_Of_Frame":
                return "Frame_Shift_Ins";
            default:
                return null;
        }
    }

    private static Map<String, String> initVariantMap()
    {
        Map<String, String> variantMap = new HashMap<>();

        variantMap.put("splice_acceptor_variant",       "Splice_Site");
        variantMap.put("splice_donor_variant",          "Splice_Site");
        variantMap.put("transcript_ablation",           "Splice_Site");
        variantMap.put("exon_loss_variant", "Splice_Site");
        variantMap.put("stop_gained",                   "Nonsense_Mutation");
        variantMap.put("frameshift_variant",            "Frame_Shift");
        variantMap.put("stop_lost",                     "Nonstop_Mutation");
        variantMap.put("initiator_codon_variant",       "Translation_Start_Site");
        variantMap.put("start_lost",                    "Translation_Start_Site");
        variantMap.put("inframe_insertion",             "In_Frame_Ins");
        variantMap.put("inframe_deletion",              "In_Frame_Del");
        variantMap.put("disruptive_inframe_insertion", "In_Frame_Ins");
        variantMap.put("disrupting_inframe_deletion", "In_Frame_Del");
        variantMap.put("missense_variant",              "Missense_Mutation");
        variantMap.put("protein_altering_variant",      "Missense_Mutation"); // Not always correct, resolveVariantClassification handles the exceptions
        variantMap.put("coding_sequence_variant",       "Missense_Mutation"); // Not always correct, resolveVariantClassification handles the exceptions
        variantMap.put("coding_transcript_variant",       "Missense_Mutation"); // Not always correct, resolveVariantClassification handles the exceptions
        variantMap.put("conservative_missense_variant", "Missense_Mutation");
        variantMap.put("rare_amino_acid_variant",       "Missense_Mutation");
        variantMap.put("transcript_amplification",      "Intron");
        variantMap.put("feature_elongation",      "Feature_Elongation");
        variantMap.put("feature_truncation",      "Feature_Elongation");
        variantMap.put("sequence_variant",      "Sequence_Variant");
        variantMap.put("splice_region_variant",         "Splice_Region");
        variantMap.put("splice_donor_region_variant",         "Splice_Region");
        variantMap.put("splice_polypyrimidine_tract_variant",         "Splice_Region");
        variantMap.put("splice_donor_5th_base_variant",         "Splice_Region");
        variantMap.put("intron_variant",                "Intron");
        variantMap.put("intragenic",                    "Intron");
        variantMap.put("intragenic_variant",            "Intron");
        variantMap.put("incomplete_terminal_codon_variant", "Silent");
        variantMap.put("synonymous_variant",            "Silent");
        variantMap.put("start_retained_variant",        "Silent");
        variantMap.put("stop_retained_variant",         "Silent");
        variantMap.put("nmd_transcript_variant",        "Silent");
        variantMap.put("mature_mirna_variant",          "RNA");
        variantMap.put("non_coding_exon_variant",       "RNA");
        variantMap.put("non_coding_transcript_exon_variant", "RNA");
        variantMap.put("non_coding_transcript_variant", "RNA");
        variantMap.put("nc_transcript_variant",         "RNA");
        variantMap.put("exon_variant", "RNA");
        variantMap.put("5_prime_utr_variant",           "5'UTR");
        variantMap.put("5_prime_utr_premature_start_codon_gain_variant", "5'UTR");
        variantMap.put("3_prime_utr_variant",           "3'UTR");
        variantMap.put("tf_binding_site_variant",       "IGR");
        variantMap.put("tfbs_ablation",       "IGR");
        variantMap.put("tfbs_amplification",       "IGR");
        variantMap.put("regulatory_region_variant",     "IGR");
        variantMap.put("regulatory_region_amplification",     "IGR");
        variantMap.put("regulatory_region",             "IGR");
        variantMap.put("regulatory_region_ablation",             "IGR");
        variantMap.put("intergenic_variant",            "IGR");
        variantMap.put("intergenic_region",             "IGR");
        variantMap.put("upstream_gene_variant",         "5'Flank");
        variantMap.put("downstream_gene_variant",       "3'Flank");
        variantMap.put("splicing",                      "Splice_Region");
        variantMap.put("nonframeshift_insertion",       "In_Frame_Ins");
        variantMap.put("frameshift_insertion",          "Frame_Shift_Ins");
        variantMap.put("stopgain_SNV",                  "Nonsense_Mutation");
        variantMap.put("frameshift_deletion",           "Frame_Shift_Del");
        variantMap.put("stoploss_SNV",                  "Nonstop_Mutation");
        variantMap.put("upstream",                      "5'Flank");
        variantMap.put("nonframeshift_deletion",        "In_Frame_Del");
        variantMap.put("nonsynonymous_SNV",             "Missense_Mutation");


        return variantMap;
    }
}
