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
        assertEquals(variant.getRef(), "X");
        assertEquals(variant.getType(), "ins");
        assertEquals(variant.getAlt(), "T");
    }

    @Test

    public void testHgvsDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("5:c.189_197delAAATGGAGC");

        assertEquals(variant.getChromosome(), "5");
        assertEquals(variant.getStart(), (Integer) 189);
        assertEquals(variant.getEnd(), (Integer) 197);
        assertEquals(variant.getRef(), "AAATGGAGC");
        assertEquals(variant.getType(), "del");
        assertEquals(variant.getAlt(), "XXXXXXXXX");
    }

    @Test
    public void testHgvsIndelToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("23:g.88778_88784delinsTAGATAG");

        assertEquals(variant.getChromosome(), "23");
        assertEquals(variant.getStart(), (Integer) 88778);
        assertEquals(variant.getEnd(), (Integer) 88784);
        assertEquals(variant.getRef(), "XXXXXXX");
        assertEquals(variant.getType(), "delins");
        assertEquals(variant.getAlt(), "TAGATAG");
    }

    @Test
    public void testRegionSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("5:140532-140532:1/C");

        assertEquals(variant.getChromosome(), "5");
        assertEquals(variant.getStart(), (Integer) 140532);
        assertEquals(variant.getEnd(), (Integer) 140532);
        assertEquals(variant.getRef(), null);
        assertEquals(variant.getType(), null);
        assertEquals(variant.getAlt(), "C");
    }

    @Test
    public void testRegionReverseSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("14:19584687-19584687:-1/T");

        assertEquals(variant.getChromosome(), "14");
        assertEquals(variant.getStart(), (Integer) 19584687);
        assertEquals(variant.getEnd(),(Integer) 19584687);
        assertEquals(variant.getRef(), null);
        assertEquals(variant.getType(), null);
        assertEquals(variant.getAlt(), "T");
    }

    @Test
    public void testRegionInsertionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("1:881907-881906:1/C");

        assertEquals(variant.getChromosome(), "1");
        assertEquals(variant.getStart(), (Integer) 881907);
        assertEquals(variant.getEnd(), (Integer) 881906);
        assertEquals(variant.getRef(), null);
        assertEquals(variant.getType(), null);
        assertEquals(variant.getAlt(), "C");
    }

    @Test
    public void testRegionDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("2:946507-946511:1/-");

        assertEquals(variant.getChromosome(), "2");
        assertEquals(variant.getStart(), (Integer) 946507);
        assertEquals(variant.getEnd(), (Integer) 946511);
        assertEquals(variant.getRef(), null);
        assertEquals(variant.getType(), null);
        assertEquals(variant.getAlt(), "-");
    }

    @Test
    public void testGenomicVariantToHgvs() {
        GenomicVariant variant = new GenomicVariant("17", "g", 41242962, 41242963, "ins", null, "GA");

        assertEquals(GenomicVariantUtil.toHgvs(variant), "17:g.41242962_41242963insGA");
    }

    @Test
    public void testGenomicVariantToRegion() {
        GenomicVariant variant = new GenomicVariant("5", null, 140532, 140532, null, null, "C");
    }
} 