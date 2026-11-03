package org.cbioportal.genome_nexus.component.annotation;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Normalized AlphaGenome-ready view of a {@code VariantAnnotation};
 *
 * <p>Holds GRCh38 coordinates with a {@code chr} prefix and VCF-style alleles
 * ({@code pos}, {@code ref}, {@code alt} where indels carry a 1-bp anchor base
 * and are left-aligned). {@code variantStr} renders as
 * {@code chr:pos:REF>ALT} (e.g. {@code chr5:177513010:C>T}).
 *
 * <p>{@code chrom}, {@code pos}, {@code ref}, and {@code alt} come from
 * pre-filtering plus {@code GRCh37 -> GRCh38} liftover followed by SDK-style
 * indel anchoring / left-alignment; {@code REF} is verified against GRCh38,
 * so every instance carries a reference allele that matches the reference
 * genome. {@code geneId} / {@code geneSymbol} come from the canonical
 * transcript ({@code null} only when no resolver or no canonical transcript
 * was available).
 */
public class NormalizedAlphaGenomeVariant
{
    private final String chrom;
    private final int pos;
    private final String ref;
    private final String alt;
    private final String geneId;
    private final String geneSymbol;
    private final String variantStr;

    public NormalizedAlphaGenomeVariant(
        String chrom,
        int pos,
        String ref,
        String alt,
        @Nullable String geneId,
        @Nullable String geneSymbol
    ) {
        this.chrom = chrom;
        this.pos = pos;
        this.ref = ref;
        this.alt = alt;
        this.geneId = geneId;
        this.geneSymbol = geneSymbol;
        this.variantStr = String.format("%s:%d:%s>%s", chrom, pos, ref, alt);
    }

    /** GRCh38 contig with {@code chr} prefix (e.g. {@code chr5}) */
    public String getChrom()
    {
        return chrom;
    }

    /** GRCh38 1-based position (anchor-base position for indels) */
    public int getPos()
    {
        return pos;
    }

    /** VCF-style reference allele (includes anchor base for indels) */
    public String getRef()
    {
        return ref;
    }

    /** VCF-style alternate allele (includes anchor base for indels) */
    public String getAlt()
    {
        return alt;
    }

    /** Canonical gene ID, null if unavailable */
    @Nullable
    public String getGeneId()
    {
        return geneId;
    }

    /** Canonical gene symbol, null if unavailable */
    @Nullable
    public String getGeneSymbol()
    {
        return geneSymbol;
    }

    /** Rendered {@code chr:pos:REF>ALT} string (e.g. {@code chr5:177513010:C>T}) */
    public String getVariantStr()
    {
        return variantStr;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NormalizedAlphaGenomeVariant)) {
            return false;
        }
        NormalizedAlphaGenomeVariant that = (NormalizedAlphaGenomeVariant) other;
        return pos == that.pos
            && Objects.equals(chrom, that.chrom)
            && Objects.equals(ref, that.ref)
            && Objects.equals(alt, that.alt)
            && Objects.equals(geneId, that.geneId)
            && Objects.equals(geneSymbol, that.geneSymbol);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(chrom, pos, ref, alt, geneId, geneSymbol);
    }

    @Override
    public String toString()
    {
        return variantStr;
    }
}
