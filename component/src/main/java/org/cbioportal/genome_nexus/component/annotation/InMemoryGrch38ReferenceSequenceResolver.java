package org.cbioportal.genome_nexus.component.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * W1: deterministic, in-memory {@link Grch38ReferenceSequenceResolver} for
 * unit tests. Holds whole contig sequences (or test slices) keyed by
 * {@code chr}-prefixed contig name.
 */
public class InMemoryGrch38ReferenceSequenceResolver implements Grch38ReferenceSequenceResolver
{
    private final Map<String, String> sequences = new HashMap<>();

    public InMemoryGrch38ReferenceSequenceResolver(Map<String, String> sequences)
    {
        if (sequences == null) {
            throw new IllegalArgumentException("sequences must not be null");
        }
        for (Map.Entry<String, String> entry : sequences.entrySet()) {
            this.sequences.put(entry.getKey(), entry.getValue().toUpperCase());
        }
    }

    @Override
    public String extract(String chrom, int zeroBasedStart, int zeroBasedEndExclusive)
    {
        String sequence = sequences.get(chrom);
        if (sequence == null) {
            throw new IllegalArgumentException("Unknown contig: " + chrom);
        }
        StringBuilder out = new StringBuilder();
        for (int i = zeroBasedStart; i < zeroBasedEndExclusive; i++) {
            if (i < 0 || i >= sequence.length()) {
                out.append('N');
            }
            else {
                out.append(sequence.charAt(i));
            }
        }
        return out.toString();
    }
}
