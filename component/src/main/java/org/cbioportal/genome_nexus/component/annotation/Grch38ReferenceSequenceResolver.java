package org.cbioportal.genome_nexus.component.annotation;

/**
 * Abstraction over the GRCh38 reference sequence used for indel
 * anchoring / left-alignment and {@code REF} check.
 *
 * <p>Coordinates are 0-based, half-open ({@code [start, end)}), matching the
 * {@code alphagenome} SDK {@code Interval} / {@code FastaExtractor.extract}
 * convention that {@code AlphaGenomeVariantNormalizer} ports. Contigs carry
 * the {@code chr} prefix (e.g. {@code chr5})
 */
public interface Grch38ReferenceSequenceResolver
{
    /**
     * Returns the uppercase GRCh38 bases for {@code [zeroBasedStart,
     * zeroBasedEnd)}. Out-of-range positions are {@code N}-padded, mirroring
     * the SDK {@code FastaExtractor} behavior.
     */
    String extract(String chrom, int zeroBasedStart, int zeroBasedEndExclusive);
}
