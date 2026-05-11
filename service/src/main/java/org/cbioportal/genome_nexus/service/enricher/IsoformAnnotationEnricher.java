package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.OncokbService;
import org.cbioportal.genome_nexus.util.IsoformOverrideSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IsoformAnnotationEnricher extends BaseAnnotationEnricher
{
    // Biotype priority table ported from vcf2maf/vcf2maf.pl GetBiotypePriority().
    // Lower number = higher priority. Unrecognized biotypes get priority 10.
    // Full biotype definitions: https://www.gencodegenes.org/pages/biotypes.html
    private static final Map<String, Integer> BIOTYPE_PRIORITY = new HashMap<>();
    static {
        BIOTYPE_PRIORITY.put("protein_coding", 1);
        BIOTYPE_PRIORITY.put("LRG_gene", 2);
        BIOTYPE_PRIORITY.put("IG_C_gene", 2);
        BIOTYPE_PRIORITY.put("IG_D_gene", 2);
        BIOTYPE_PRIORITY.put("IG_J_gene", 2);
        BIOTYPE_PRIORITY.put("IG_LV_gene", 2);
        BIOTYPE_PRIORITY.put("IG_V_gene", 2);
        BIOTYPE_PRIORITY.put("TR_C_gene", 2);
        BIOTYPE_PRIORITY.put("TR_D_gene", 2);
        BIOTYPE_PRIORITY.put("TR_J_gene", 2);
        BIOTYPE_PRIORITY.put("TR_V_gene", 2);
        BIOTYPE_PRIORITY.put("miRNA", 3);
        BIOTYPE_PRIORITY.put("snRNA", 3);
        BIOTYPE_PRIORITY.put("snoRNA", 3);
        BIOTYPE_PRIORITY.put("ribozyme", 3);
        BIOTYPE_PRIORITY.put("tRNA", 3);
        BIOTYPE_PRIORITY.put("sRNA", 3);
        BIOTYPE_PRIORITY.put("scaRNA", 3);
        BIOTYPE_PRIORITY.put("rRNA", 3);
        BIOTYPE_PRIORITY.put("scRNA", 3);
        BIOTYPE_PRIORITY.put("lincRNA", 3);
        BIOTYPE_PRIORITY.put("lncRNA", 3);
        BIOTYPE_PRIORITY.put("bidirectional_promoter_lncrna", 3);
        BIOTYPE_PRIORITY.put("bidirectional_promoter_lncRNA", 3);
        BIOTYPE_PRIORITY.put("known_ncrna", 4);
        BIOTYPE_PRIORITY.put("vaultRNA", 4);
        BIOTYPE_PRIORITY.put("vault_RNA", 4);
        BIOTYPE_PRIORITY.put("macro_lncRNA", 4);
        BIOTYPE_PRIORITY.put("Mt_tRNA", 4);
        BIOTYPE_PRIORITY.put("Mt_rRNA", 4);
        BIOTYPE_PRIORITY.put("antisense", 5);
        BIOTYPE_PRIORITY.put("antisense_RNA", 5);
        BIOTYPE_PRIORITY.put("sense_intronic", 5);
        BIOTYPE_PRIORITY.put("sense_overlapping", 5);
        BIOTYPE_PRIORITY.put("3prime_overlapping_ncrna", 5);
        BIOTYPE_PRIORITY.put("3prime_overlapping_ncRNA", 5);
        BIOTYPE_PRIORITY.put("misc_RNA", 5);
        BIOTYPE_PRIORITY.put("non_coding", 5);
        BIOTYPE_PRIORITY.put("regulatory_region", 6);
        BIOTYPE_PRIORITY.put("disrupted_domain", 6);
        BIOTYPE_PRIORITY.put("processed_transcript", 6);
        BIOTYPE_PRIORITY.put("protein_coding_CDS_not_defined", 6);
        BIOTYPE_PRIORITY.put("TEC", 6);
        BIOTYPE_PRIORITY.put("TF_binding_site", 7);
        BIOTYPE_PRIORITY.put("CTCF_binding_site", 7);
        BIOTYPE_PRIORITY.put("promoter_flanking_region", 7);
        BIOTYPE_PRIORITY.put("enhancer", 7);
        BIOTYPE_PRIORITY.put("promoter", 7);
        BIOTYPE_PRIORITY.put("open_chromatin_region", 7);
        BIOTYPE_PRIORITY.put("retained_intron", 7);
        BIOTYPE_PRIORITY.put("nonsense_mediated_decay", 7);
        BIOTYPE_PRIORITY.put("non_stop_decay", 7);
        BIOTYPE_PRIORITY.put("ambiguous_orf", 7);
        BIOTYPE_PRIORITY.put("pseudogene", 8);
        BIOTYPE_PRIORITY.put("processed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("polymorphic_pseudogene", 8);
        BIOTYPE_PRIORITY.put("protein_coding_LoF", 8);
        BIOTYPE_PRIORITY.put("retrotransposed", 8);
        BIOTYPE_PRIORITY.put("translated_processed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("translated_unprocessed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("transcribed_processed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("transcribed_unprocessed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("transcribed_unitary_pseudogene", 8);
        BIOTYPE_PRIORITY.put("unitary_pseudogene", 8);
        BIOTYPE_PRIORITY.put("unprocessed_pseudogene", 8);
        BIOTYPE_PRIORITY.put("Mt_tRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("tRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("snoRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("snRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("scRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("rRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("misc_RNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("miRNA_pseudogene", 8);
        BIOTYPE_PRIORITY.put("IG_pseudogene", 8);
        BIOTYPE_PRIORITY.put("IG_C_pseudogene", 8);
        BIOTYPE_PRIORITY.put("IG_D_pseudogene", 8);
        BIOTYPE_PRIORITY.put("IG_J_pseudogene", 8);
        BIOTYPE_PRIORITY.put("IG_V_pseudogene", 8);
        BIOTYPE_PRIORITY.put("TR_J_pseudogene", 8);
        BIOTYPE_PRIORITY.put("TR_V_pseudogene", 8);
        BIOTYPE_PRIORITY.put("artifact", 9);
    }

    String source;
    EnsemblService ensemblService;
    OncokbService  oncokbService;
    private final String prioritizeOncokbGeneTranscriptsConfig;

    public IsoformAnnotationEnricher(
        String id,
        String source,
        EnsemblService ensemblService,
        OncokbService  oncokbService,
        String prioritizeOncokbGeneTranscriptsConfig
    ) {
        super(id);
        this.source = source;
        this.ensemblService = ensemblService;
        this.oncokbService = oncokbService;
        this.prioritizeOncokbGeneTranscriptsConfig = prioritizeOncokbGeneTranscriptsConfig;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        // no transcripts to enrich, abort.
        if (annotation.getTranscriptConsequences() == null) {
            return;
        }

        // get transcript overrides for the given isoform override source
        // if no isoform override source is provided, then the default is mskcc
        Set<String> predefinedCanonicalTranscriptIds = this.ensemblService.getCanonicalTranscriptIdsBySource(
            IsoformOverrideSource.getOrDefault(this.source)
        );

        // If no override source is configured, rely on VEP's canonical flags as-is.
        if (predefinedCanonicalTranscriptIds == null || predefinedCanonicalTranscriptIds.isEmpty()) {
            return;
        }

        // Override source is configured: take full control of canonical flags.
        // Reset all VEP canonical flags; we will re-derive them below.
        for (TranscriptConsequence transcript : annotation.getTranscriptConsequences()) {
            transcript.setCanonical(null);
        }

        // Step 1 — biotype (always first): keep only transcripts with the best biotype priority.
        // This ensures protein_coding always beats antisense/pseudogene/etc.
        List<TranscriptConsequence> candidates = filterByBestBiotype(annotation.getTranscriptConsequences());

        // Step 2 — isoform override (tiebreaker): among the best-biotype transcripts, prefer
        // those listed in the configured override source. If none of the best-biotype transcripts
        // match the override list (e.g. the override transcript was not returned by VEP for this
        // position), keep all best-biotype transcripts.
        List<TranscriptConsequence> overrideCandidates = this.findCanonicalTranscriptCandidates(
            candidates, predefinedCanonicalTranscriptIds
        );
        if (overrideCandidates.size() > 0) {
            candidates = overrideCandidates;
        }

        // Step 3 — oncokb (tiebreaker): among remaining candidates, prefer oncokb gene transcripts.
        if (candidates.size() > 1 && Boolean.parseBoolean(prioritizeOncokbGeneTranscriptsConfig)) {
            List<TranscriptConsequence> oncokbFiltered = candidates
                .stream()
                .filter(t -> oncokbService.getOncokbGeneSymbolList().contains(t.getGeneSymbol()))
                .collect(Collectors.toList());
            if (oncokbFiltered.size() > 0) {
                candidates = oncokbFiltered;
            }
        }

        // Mark all remaining candidates as canonical.
        for (TranscriptConsequence transcript : candidates) {
            transcript.setCanonical("1");
        }
    }

    private static int getBiotypePriority(String biotype) {
        return BIOTYPE_PRIORITY.getOrDefault(biotype != null ? biotype : "", 10);
    }

    // Keep only the candidates that share the best (lowest) biotype priority score.
    private static List<TranscriptConsequence> filterByBestBiotype(List<TranscriptConsequence> candidates) {
        int bestPriority = candidates.stream()
            .mapToInt(t -> getBiotypePriority(t.getBiotype()))
            .min()
            .orElse(10);
        return candidates.stream()
            .filter(t -> getBiotypePriority(t.getBiotype()) == bestPriority)
            .collect(Collectors.toList());
    }

    /**
     * Tries to find a matching transcript consequence by using
     * the given predefined canonical assignment.
     */
    private List<TranscriptConsequence> findCanonicalTranscriptCandidates(
        List<TranscriptConsequence> transcriptConsequences,
        Set<String> predefinedCanonicalTranscriptIds
    ) {
        List<TranscriptConsequence> transcripts = Collections.emptyList();

        if (predefinedCanonicalTranscriptIds != null) {
            transcripts = transcriptConsequences
                .stream()
                .filter(t -> predefinedCanonicalTranscriptIds.contains(t.getTranscriptId()))
                .collect(Collectors.toList());
        }

        return transcripts;
    }
}
