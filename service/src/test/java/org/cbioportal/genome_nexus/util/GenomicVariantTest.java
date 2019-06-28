package org.cbioportal.genome_nexus.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GenomicVariantTest {
    @Test    
    public void testHgvsSubstitutionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("1:c.123C>T");

        assertEquals(variant.getChromosome(), "1");
        assertEquals(variant.getStart(), (Integer) 123);
        assertEquals(variant.getEnd(), (Integer) 123);
        assertEquals(variant.getRef(), "C");
        assertEquals(variant.getType(), ">");
        assertEquals(variant.getAlt(), "T");
    }

    @Test
    public void testHgvsAdditionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("10:g.32867861_32867862insT");

        assertEquals(variant.getChromosome(), "10");
        assertEquals(variant.getStart(), (Integer) 32867861);
        assertEquals(variant.getEnd(), (Integer) 32867862);
        assertEquals(variant.getRef(), "XXX");
        assertEquals(variant.getType(), "ins");
        assertEquals(variant.getAlt(), "T");
    }

    @Test

    public void testHgvsDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("5:c.189_197delAAATGGAGC");

        assertEquals(variant.getChromosome(), "5");
        assertEquals(variant.getStart(), (Integer) 189);
        assertEquals(variant.getEnd(), (Integer) 197);
        assertEquals(variant.getRef(), "XXX");
        assertEquals(variant.getType(), "del");
        assertEquals(variant.getAlt(), "AAATGGAGC");
    }

    @Test
    public void testHgvsDelinsToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("23:g.88778_88784delinsTAGATAG");

        assertEquals(variant.getChromosome(), "23");
        assertEquals(variant.getStart(), (Integer) 88778);
        assertEquals(variant.getEnd(), (Integer) 88784);
        assertEquals(variant.getRef(), "XXX");
        assertEquals(variant.getType(), "delins");
        assertEquals(variant.getAlt(), "TAGATAG");
    }
    
} 