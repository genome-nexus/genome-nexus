package org.cbioportal.genome_nexus.component.annotation;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.util.AlphaGenomePipelineDebugLogger;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalizes a Genome Nexus {@code VariantAnnotation} into GRCh38,
 * AlphaGenome-ready coordinates.
 * All coordinates come from {@code seqRegionName}, {@code start}, {@code end}, and
 * {@code alleleString}.
 *
 * <p>Step 1 (pre-filter) and Step 2 ({@code GRCh37 -> GRCh38} liftover) run
 * first. Rejected variants return {@code Optional.empty()}
 * Step 3 ports the {@code alphagenome} SDK
 * {@code normalize_variant} left-alignment (Tan et al. 2015) to convert MAF
 * {@code -} alleles into VCF anchor-base form, and Step 4 verifies
 * {@code REF} against GRCh38 and extracts the canonical gene.
 */
@Component
public class AlphaGenomeVariantNormalizer
{
    public static final String CHAIN_RESOURCE = "liftover/hg19ToHg38.over.chain.gz";

    private static final Pattern ALLELE_PATTERN = Pattern.compile("^[ACGT-]+$");

    private static final Set<String> VALID_CHROMOSOMES = new HashSet<>(Arrays.asList(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "X", "Y"
    ));

    private final LiftOver liftOver;
    private final Grch38ReferenceSequenceResolver referenceResolver;
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public AlphaGenomeVariantNormalizer(CanonicalTranscriptResolver canonicalTranscriptResolver,
                                        @Nullable Grch38ReferenceSequenceResolver referenceResolver)
    {
        this(loadDefaultLiftOver(), referenceResolver, canonicalTranscriptResolver);
    }

    AlphaGenomeVariantNormalizer(
        LiftOver liftOver,
        Grch38ReferenceSequenceResolver referenceResolver,
        CanonicalTranscriptResolver canonicalTranscriptResolver
    ) {
        if (liftOver == null) {
            throw new IllegalArgumentException("liftOver must not be null");
        }
        this.liftOver = liftOver;
        this.referenceResolver = referenceResolver;
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    /**
     * Applies pre-filtering and liftover
     */
    public Optional<NormalizedAlphaGenomeVariant> normalize(VariantAnnotation annotation)
    {
        // ---- Step 1: pre-filter ----
        if (annotation == null) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER", "annotation is null -> REJECTED");
            return Optional.empty();
        }

        String varId = annotation.getVariant() != null ? annotation.getVariant() : annotation.getOriginalVariantQuery();
        AlphaGenomePipelineDebugLogger.log("PHASE 1: BEFORE LIFTOVER",
            String.format("Input variant='%s', query='%s', assembly='%s', chrom='%s', start=%s, end=%s, alleles='%s', strand=%s, successfullyAnnotated=%s, errorMessage='%s'",
                varId,
                annotation.getOriginalVariantQuery(),
                annotation.getAssemblyName(),
                annotation.getSeqRegionName(),
                annotation.getStart(),
                annotation.getEnd(),
                annotation.getAlleleString(),
                annotation.getStrand(),
                annotation.isSuccessfullyAnnotated(),
                annotation.getErrorMessage()
            )
        );

        if (!Boolean.TRUE.equals(annotation.isSuccessfullyAnnotated()) || annotation.getErrorMessage() != null) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER REJECTED",
                String.format("Variant '%s' rejected: isSuccessfullyAnnotated=%s, errorMessage='%s'",
                    varId, annotation.isSuccessfullyAnnotated(), annotation.getErrorMessage()));
            return Optional.empty();
        }
        if (annotation.getSeqRegionName() == null
            || annotation.getStart() == null
            || annotation.getEnd() == null
            || annotation.getAlleleString() == null
            || annotation.getAlleleString().isEmpty()) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER REJECTED",
                String.format("Variant '%s' rejected: null or empty coordinate field", varId));
            return Optional.empty();
        }

        String chrom = normalizeChromosome(annotation.getSeqRegionName());
        if (chrom == null) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER REJECTED",
                String.format("Variant '%s' rejected: invalid or unmapped chromosome '%s'",
                    varId, annotation.getSeqRegionName()));
            return Optional.empty();
        }

        String[] alleles = annotation.getAlleleString().toUpperCase().split("/", -1);
        if (alleles.length != 2) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER REJECTED",
                String.format("Variant '%s' rejected: alleleString '%s' does not have 2 parts",
                    varId, annotation.getAlleleString()));
            return Optional.empty();
        }
        String ref = alleles[0].trim();
        String alt = alleles[1].trim();
        if (ref.isEmpty()
            || alt.isEmpty()
            || ref.equals(alt)
            || !ALLELE_PATTERN.matcher(ref).matches()
            || !ALLELE_PATTERN.matcher(alt).matches()) {
            AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER REJECTED",
                String.format("Variant '%s' rejected: invalid ref='%s' or alt='%s'",
                    varId, ref, alt));
            return Optional.empty();
        }

        if (annotation.getStrand() != null && annotation.getStrand() == -1) {
            String origRef = ref;
            String origAlt = alt;
            if (!ref.equals("-")) {
                ref = GenomicLocationUtil.getReverseStrandAllele(ref);
            }
            if (!alt.equals("-")) {
                alt = GenomicLocationUtil.getReverseStrandAllele(alt);
            }
            AlphaGenomePipelineDebugLogger.log("PHASE 1: BEFORE LIFTOVER",
                String.format("Variant '%s' strand is -1: complemented ref '%s'->'%s', alt '%s'->'%s'",
                    varId, origRef, ref, origAlt, alt));
        }

        AlphaGenomePipelineDebugLogger.log("PHASE 1: PRE-FILTER PASSED",
            String.format("Variant '%s' passed pre-filter: contig='chr%s', ref='%s', alt='%s'",
                varId, chrom, ref, alt));

        // ---- Step 2: GRCh37 -> GRCh38 liftover ----
        String assembly = annotation.getAssemblyName();
        boolean isGrch37 = assembly == null || assembly.equalsIgnoreCase("GRCh37");
        boolean isGrch38 = assembly != null && assembly.equalsIgnoreCase("GRCh38");
        AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER",
            String.format("Assembly detected: '%s' (isGrch37=%s, isGrch38=%s)", assembly, isGrch37, isGrch38));

        if (!isGrch37 && !isGrch38) {
            AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER REJECTED",
                String.format("Variant '%s' rejected: unsupported assembly '%s'", varId, assembly));
            return Optional.empty();
        }

        String contig = "chr" + chrom;
        int liftedStart;
        int liftedEnd;
        if (isGrch37) {
            // Insertions may be stored with start/end swapped (end = start - 1);
            // lift the covered span consistently.
            int low = Math.min(annotation.getStart(), annotation.getEnd());
            int high = Math.max(annotation.getStart(), annotation.getEnd());
            AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER",
                String.format("Attempting htsjdk LiftOver for %s:%d-%d", contig, low, high));
            if (low < 1) {
                AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER REJECTED",
                    String.format("Variant '%s' rejected: low coordinate < 1 (%d)", varId, low));
                return Optional.empty();
            }
            Interval lifted = liftOver.liftOver(new Interval(contig, low, high));
            if (lifted == null || !contig.equals(lifted.getContig())) {
                AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER REJECTED",
                    String.format("Variant '%s' rejected: liftOver returned %s (contig=%s)",
                        varId,
                        lifted == null ? "null" : lifted.toString(),
                        lifted == null ? "null" : lifted.getContig()));
                return Optional.empty();
            }
            if (lifted.length() != (high - low + 1)) {
                AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER REJECTED",
                    String.format("Variant '%s' rejected: span length mismatch (%d != %d)",
                        varId, lifted.length(), high - low + 1));
                return Optional.empty();
            }
            liftedStart = lifted.getStart();
            liftedEnd = lifted.getEnd();
            if (lifted.isNegativeStrand()) {
                String origRef = ref;
                String origAlt = alt;
                if (!ref.equals("-")) {
                    ref = GenomicLocationUtil.getReverseStrandAllele(ref);
                }
                if (!alt.equals("-")) {
                    alt = GenomicLocationUtil.getReverseStrandAllele(alt);
                }
                AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER",
                    String.format("Variant '%s' mapped to negative strand by LiftOver: complemented ref '%s'->'%s', alt '%s'->'%s'",
                        varId, origRef, ref, origAlt, alt));
            }
            AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER SUCCESS",
                String.format("Liftover %s:%d-%d -> %s:%d-%d (negativeStrand=%s)",
                    contig, low, high, contig, liftedStart, liftedEnd, lifted.isNegativeStrand()));
        }
        else {
            liftedStart = annotation.getStart();
            liftedEnd = annotation.getEnd();
            AlphaGenomePipelineDebugLogger.log("PHASE 2: LIFTOVER",
                String.format("GRCh38 passthrough without liftover: %s:%d-%d", contig, liftedStart, liftedEnd));
        }

        if (referenceResolver == null) {
            AlphaGenomePipelineDebugLogger.log("PHASE 2: GRCH38 REF CHECK",
                "Grch38ReferenceSequenceResolver is null! Throwing IllegalStateException.");
            throw new IllegalStateException(
                "Grch38ReferenceSequenceResolver is not configured");
        }

        // ---- Step 3: VCF indel anchoring & left-alignment ----
        // Port of alphagenome genome.normalize_variant (Tan et al.
        // 2015): MAF "-" becomes the empty allele, SNVs pass through, and
        // every other allele runs the suffix-trim / left-extend / prefix-trim
        // loop against GRCh38 so indels gain a 1-bp anchor base.
        int pos;
        String vcfRef;
        String vcfAlt;
        if (!ref.equals("-") && !alt.equals("-") && ref.length() == 1 && alt.length() == 1) {
            pos = liftedStart;
            vcfRef = ref;
            vcfAlt = alt;
            AlphaGenomePipelineDebugLogger.log("PHASE 2: VCF NORMALIZATION",
                String.format("SNV detected (no indel anchoring needed): pos=%d, vcfRef='%s', vcfAlt='%s'",
                    pos, vcfRef, vcfAlt));
        }
        else {
            String seedRef = ref.equals("-") ? "" : ref;
            String seedAlt = alt.equals("-") ? "" : alt;
            // Deletions seed at the first deleted base. Insertions seed at the
            // right-flank coordinate (max of start/end): the SDK loop opens by
            // extending left (position -= 1) whenever an allele is empty, so a
            // right-flank seed lands the first extension exactly on the true
            // anchor base. Seeding the left flank would shift one base too far
            int seedPos = ref.equals("-")
                ? Math.max(liftedStart, liftedEnd)
                : liftedStart;
            AlphaGenomePipelineDebugLogger.log("PHASE 2: VCF NORMALIZATION",
                String.format("Indel detected: seedPos=%d, seedRef='%s', seedAlt='%s' (ref='%s', alt='%s', liftedStart=%d, liftedEnd=%d)",
                    seedPos, seedRef, seedAlt, ref, alt, liftedStart, liftedEnd));
            String[] normalizedAlleles = normalizeAlleles(contig, seedPos, seedRef, seedAlt);
            pos = Integer.parseInt(normalizedAlleles[0]);
            vcfRef = normalizedAlleles[1];
            vcfAlt = normalizedAlleles[2];
            AlphaGenomePipelineDebugLogger.log("PHASE 2: VCF NORMALIZATION",
                String.format("Indel left-aligned & anchored: pos=%d, vcfRef='%s', vcfAlt='%s'",
                    pos, vcfRef, vcfAlt));
        }

        // ---- Step 4: GRCh38 REF check & canonical transcript extraction ---- Reject variants whose REF mismatches GRCh38
        AlphaGenomePipelineDebugLogger.log("PHASE 2: GRCH38 REF CHECK",
            String.format("Resolver class: %s. Extracting reference sequence for %s:%d-%d (length=%d)",
                referenceResolver.getClass().getName(), contig, pos - 1, pos - 1 + vcfRef.length(), vcfRef.length()));
        String expectedRef = referenceResolver.extract(contig, pos - 1, pos - 1 + vcfRef.length());
        AlphaGenomePipelineDebugLogger.log("PHASE 2: GRCH38 REF CHECK",
            String.format("Reference comparison at %s:%d -> Expected REF in Reference='%s' vs Variant VCF REF='%s'",
                contig, pos, expectedRef, vcfRef));
        if (!expectedRef.equalsIgnoreCase(vcfRef)) {
            AlphaGenomePipelineDebugLogger.log("PHASE 2: GRCH38 REF CHECK REJECTED",
                String.format("Variant '%s' REJECTED: REF check mismatch! Expected '%s' != Got '%s' at %s:%d",
                    varId, expectedRef, vcfRef, contig, pos));
            return Optional.empty();
        }
        AlphaGenomePipelineDebugLogger.log("PHASE 2: GRCH38 REF CHECK PASSED",
            String.format("Variant '%s' PASSED REF check ('%s' matches reference '%s')",
                varId, vcfRef, expectedRef));

        String geneId = null;
        String geneSymbol = null;
        if (canonicalTranscriptResolver != null) {
            TranscriptConsequence canonical = canonicalTranscriptResolver.resolve(annotation);
            if (canonical != null) {
                geneId = stripGeneVersion(canonical.getGeneId());
                geneSymbol = canonical.getGeneSymbol();
                AlphaGenomePipelineDebugLogger.log("PHASE 2: CANONICAL TRANSCRIPT",
                    String.format("Canonical transcript found: transcriptId='%s', geneId='%s' (raw='%s'), geneSymbol='%s'",
                        canonical.getTranscriptId(), geneId, canonical.getGeneId(), geneSymbol));
            } else {
                AlphaGenomePipelineDebugLogger.log("PHASE 2: CANONICAL TRANSCRIPT",
                    String.format("No canonical transcript resolved for variant '%s'", varId));
            }
        } else {
            AlphaGenomePipelineDebugLogger.log("PHASE 2: CANONICAL TRANSCRIPT",
                "canonicalTranscriptResolver is null");
        }

        NormalizedAlphaGenomeVariant result = new NormalizedAlphaGenomeVariant(contig, pos, vcfRef, vcfAlt, geneId, geneSymbol);
        AlphaGenomePipelineDebugLogger.log("PHASE 2: NORMALIZATION COMPLETE",
            String.format("Variant '%s' successfully normalized to: %s (geneId='%s', geneSymbol='%s')",
                varId, result.getVariantStr(), result.getGeneId(), result.getGeneSymbol()));
        return Optional.of(result);
    }

    /**
     * Ports {@code alphagenome.data.genome.normalize_variant}. Returns a
     * 3-element array of {@code [position, referenceBases, alternateBases]}.
     * The seed follows MAF conventions ({@code ""} for {@code -}); the result
     * is VCF-style with at least 1 bp of anchor context.
     */
    private String[] normalizeAlleles(String chrom, int position, String ref, String alt)
    {
        int window = ref.length();
        String genomeSequence = referenceResolver.extract(chrom, position - 1, position - 1 + window);
        String[] alleles = new String[]{genomeSequence, ref, alt};

        boolean finished = false;
        while (!finished) {
            int suffixLength = commonPrefixLength(reverse(alleles[0]), reverse(alleles[1]), reverse(alleles[2]));
            if (suffixLength > 0) {
                alleles[0] = alleles[0].substring(0, alleles[0].length() - suffixLength);
                alleles[1] = alleles[1].substring(0, alleles[1].length() - suffixLength);
                alleles[2] = alleles[2].substring(0, alleles[2].length() - suffixLength);
            }

            if (alleles[0].isEmpty() || alleles[1].isEmpty() || alleles[2].isEmpty()) {
                position -= 1;
                String base = referenceResolver.extract(chrom, position - 1, position);
                alleles[0] = base + alleles[0];
                alleles[1] = base + alleles[1];
                alleles[2] = base + alleles[2];
            }
            else {
                finished = suffixLength == 0;
            }
        }

        int shortest = Math.min(alleles[0].length(), Math.min(alleles[1].length(), alleles[2].length()));
        int maxPrefixLength = shortest - 1;
        int prefixLength = commonPrefixLength(alleles[0], alleles[1], alleles[2]);
        int trim = Math.min(maxPrefixLength, prefixLength);

        return new String[]{
            Integer.toString(position + trim),
            alleles[1].substring(trim),
            alleles[2].substring(trim)
        };
    }

    /** Mirrors the SDK {@code _prefix_length} helper (stops at shortest input) */
    private static int commonPrefixLength(String first, String second, String third)
    {
        String[] inputs = new String[]{first, second, third};
        int shortest = Math.min(first.length(), Math.min(second.length(), third.length()));
        int length = 0;
        while (length < shortest
            && inputs[0].charAt(length) == inputs[1].charAt(length)
            && inputs[0].charAt(length) == inputs[2].charAt(length)) {
            length++;
        }
        return length;
    }

    private static String reverse(String value)
    {
        return new StringBuilder(value).reverse().toString();
    }

    /** Drops Ensembl version suffix from gene IDs */
    private static String stripGeneVersion(String geneId)
    {
        if (geneId == null) {
            return null;
        }
        int dot = geneId.indexOf('.');
        return dot == -1 ? geneId : geneId.substring(0, dot);
    }

    /** Normalizes contigs to 1-22/X/Y; rejects others */
    private static String normalizeChromosome(String seqRegionName)
    {
        String chrom = seqRegionName.trim();
        if (chrom.length() >= 3 && chrom.regionMatches(true, 0, "chr", 0, 3)) {
            chrom = chrom.substring(3);
        }
        chrom = chrom.toUpperCase();
        if (chrom.equals("23")) {
            chrom = "X";
        }
        else if (chrom.equals("24")) {
            chrom = "Y";
        }
        if (chrom.equals("M") || chrom.equals("MT")) {
            return null;
        }
        if (!VALID_CHROMOSOMES.contains(chrom)) {
            return null;
        }
        return chrom;
    }

    /**
     * Loads the bundled chain file
     */
    static LiftOver loadDefaultLiftOver()
    {
        InputStream chainStream = AlphaGenomeVariantNormalizer.class
            .getClassLoader()
            .getResourceAsStream(CHAIN_RESOURCE);
        if (chainStream == null) {
            throw new IllegalStateException(
                "Liftover chain file not found on classpath: " + CHAIN_RESOURCE);
        }
        try {
            File stagedChain = File.createTempFile("hg19ToHg38", ".over.chain.gz");
            stagedChain.deleteOnExit();
            Files.copy(chainStream, stagedChain.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new LiftOver(stagedChain);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not stage liftover chain file: " + CHAIN_RESOURCE, e);
        }
    }
}
