package org.cbioportal.genome_nexus.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GenomicVariantTest {
    @Test    
    public void testHgvsSubstitutionToGenomicVariant() {
        GenomicVariant variant = GenomicVariant.fromHgvs("17:g.41242962_41242963insGA");
        GenomicVariantUtil util = variant.getUtil();

        assertEquals(util.getChromosome(), "17");
        assertEquals(util.getStart(), (Integer) 41242962);
        assertEquals(util.getEnd(), (Integer) 41242963);
        assertEquals(util.getRef(), "ins");
        assertEquals(util.getAlt(), "GA");
    }

    @Test
    public void testHgvsAdditionToGenomicVariant() {
        GenomicVariant variant = GenomicVariant.fromHgvs("10:g.32867861_32867862insT");
        GenomicVariantUtil util = variant.getUtil();

        assertEquals(util.getChromosome(), "10");
        assertEquals(util.getStart(), (Integer) 32867861);
        assertEquals(util.getEnd(), (Integer) 32867862);
        assertEquals(util.getRef(), "ins");
        assertEquals(util.getAlt(), "T");

    }

    @Test

    public void testHgvsDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariant.fromHgvs("1:g.19_21del");
        GenomicVariantUtil util = variant.getUtil();

        assertEquals(util.getChromosome(), "1");
        assertEquals(util.getStart(), (Integer) 19);
        assertEquals(util.getEnd(), (Integer) 21);
        assertEquals(util.getRef(), "del");
        assertEquals(util.getAlt(), "");
    }

    @Test
    public void testHgvsDelinsToGenomicVariant() {
        GenomicVariant variant = GenomicVariant.fromHgvs("g.123_127delinsAG");
        GenomicVariantUtil util = variant.getUtil();

        assertEquals(util.getChromosome(), "");
        assertEquals(util.getStart(), (Integer) 123);
        assertEquals(util.getEnd(), (Integer) 127);
        assertEquals(util.getRef(), "delins");
        assertEquals(util.getAlt(), "AG");
    }
    
} 