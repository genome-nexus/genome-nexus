package org.cbioportal.genome_nexus.component.annotation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class IndexedFastaGrch38ReferenceSequenceResolverTest
{
    private File tempFasta;
    private File tempFai;
    private IndexedFastaGrch38ReferenceSequenceResolver resolver;

    @Before
    public void setUp() throws IOException
    {
        tempFasta = File.createTempFile("test_grch38_", ".fa");
        tempFai = new File(tempFasta.getAbsolutePath() + ".fai");

        // Sequence: "ACGTACGTNN" (10 bases)
        // >chr1\n (6 bytes)
        // ACGTACGTNN\n (11 bytes)
        String fastaContent = ">chr1\nACGTACGTNN\n";
        Files.write(tempFasta.toPath(), fastaContent.getBytes());

        // fai: chr1 \t 10 \t 6 \t 10 \t 11
        String faiContent = "chr1\t10\t6\t10\t11\n";
        Files.write(tempFai.toPath(), faiContent.getBytes());

        resolver = new IndexedFastaGrch38ReferenceSequenceResolver(tempFasta);
    }

    @After
    public void tearDown()
    {
        if (tempFasta != null && tempFasta.exists()) {
            tempFasta.delete();
        }
        if (tempFai != null && tempFai.exists()) {
            tempFai.delete();
        }
    }

    @Test
    public void extractsSubsequenceCorrectly()
    {
        assertEquals("ACGT", resolver.extract("chr1", 0, 4));
        assertEquals("TACG", resolver.extract("chr1", 3, 7));
        assertEquals("G", resolver.extract("chr1", 2, 3));
    }

    @Test
    public void padsLeadingNegativeStartWithN()
    {
        assertEquals("NNAC", resolver.extract("chr1", -2, 2));
    }

    @Test
    public void returnsEmptyStringWhenEndBeforeStart()
    {
        assertEquals("", resolver.extract("chr1", 5, 2));
        assertEquals("", resolver.extract("chr1", 2, 2));
    }
}
