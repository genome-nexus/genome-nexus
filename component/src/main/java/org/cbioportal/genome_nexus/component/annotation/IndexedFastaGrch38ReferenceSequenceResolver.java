package org.cbioportal.genome_nexus.component.annotation;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/** Local indexed-FASTA GRCh38 resolver */
public class IndexedFastaGrch38ReferenceSequenceResolver implements Grch38ReferenceSequenceResolver
{
    private final IndexedFastaSequenceFile fasta;

    public IndexedFastaGrch38ReferenceSequenceResolver(Path fastaPath) throws FileNotFoundException
    {
        this(fastaPath.toFile());
    }

    public IndexedFastaGrch38ReferenceSequenceResolver(File fastaFile) throws FileNotFoundException
    {
        this.fasta = new IndexedFastaSequenceFile(fastaFile);
    }

    @Override
    public String extract(String chrom, int zeroBasedStart, int zeroBasedEndExclusive)
    {
        if (zeroBasedEndExclusive <= zeroBasedStart) {
            return "";
        }
        // Mirror the SDK FastaExtractor: pad leading out-of-range bases with N
        String prefix = "";
        if (zeroBasedStart < 0) {
            StringBuilder pad = new StringBuilder();
            for (int i = zeroBasedStart; i < 0; i++) {
                pad.append('N');
            }
            prefix = pad.toString();
            zeroBasedStart = 0;
        }
        // htsjdk takes 1-based inclusive coordinates
        return prefix
            + fasta.getSubsequenceAt(chrom, zeroBasedStart + 1, zeroBasedEndExclusive)
                .getBaseString()
                .toUpperCase();
    }
}
