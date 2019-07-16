package org.cbioportal.genome_nexus.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class GenomicVariantTest {
    @Test    
    public void testHgvsSubstitutionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("7:g.140453136A>T");

        assertEquals("7", variant.getChromosome());
        assertEquals(RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 140453136, variant.getStart());
        assertEquals((Integer) 140453136,variant.getEnd());
        assertEquals("A", variant.getRef());
        assertEquals(Type.SUBSTITUTION, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testHgvsInsertionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("10:g.32867861_32867862insT");

        assertEquals("10", variant.getChromosome());
        assertEquals(RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 32867861, variant.getStart());
        assertEquals((Integer) 32867862, variant.getEnd());
        assertEquals("X", variant.getRef());
        assertEquals(Type.INSERTION, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testHgvsDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("1:g.4849848_4849857del");

        assertEquals("1", variant.getChromosome());
        assertEquals(RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 4849848, variant.getStart());
        assertEquals((Integer) 4849857, variant.getEnd());
        assertEquals("", variant.getRef());
        assertEquals(Type.DELETION, variant.getType());
        assertEquals("", variant.getAlt());
    }

    @Test
    public void testHgvsIndelToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromHgvs("23:g.88778_88784delinsTAGATAG");

        assertEquals("23", variant.getChromosome());
        assertEquals(RefType.GENOMIC, variant.getRefType());
        assertEquals((Integer) 88778, variant.getStart());
        assertEquals((Integer) 88784,variant.getEnd());
        assertEquals("XXXXXXX", variant.getRef());
        assertEquals(Type.INDEL, variant.getType());
        assertEquals("TAGATAG", variant.getAlt());
    }

    @Test
    public void testRegionSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("5:140532-140532:1/C");

        assertEquals("5", variant.getChromosome());
        assertEquals(null, variant.getRefType());
        assertEquals((Integer) 140532, variant.getStart());
        assertEquals((Integer) 140532, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("C", variant.getAlt());
    }

    @Test
    public void testRegionReverseSnpToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("14:19584687-19584687:-1/T");

        assertEquals("14", variant.getChromosome());
        assertEquals(null, variant.getRefType());
        assertEquals((Integer) 19584687, variant.getStart());
        assertEquals((Integer) 19584687, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("T", variant.getAlt());
    }

    @Test
    public void testRegionInsertionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("1:881907-881906:1/C");

        assertEquals("1", variant.getChromosome());
        assertEquals(null, variant.getRefType());
        assertEquals((Integer) 881907, variant.getStart());
        assertEquals((Integer) 881906, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("C", variant.getAlt());
    }

    @Test
    public void testRegionDeletionToGenomicVariant() {
        GenomicVariant variant = GenomicVariantUtil.fromRegion("2:946507-946511:1/-");

        assertEquals("2", variant.getChromosome());
        assertEquals((Integer) 946507, variant.getStart());
        assertEquals((Integer) 946511, variant.getEnd());
        assertEquals(null, variant.getRef());
        assertEquals(null, variant.getType());
        assertEquals("-", variant.getAlt());
    }

    @Test
    public void testGenomicVariantToRegion() {
        GenomicVariant variant = new GenomicVariant("5", null, 140532, 140532, null, null, "C");

        assertEquals("5:140532-140532:1/C", GenomicVariantUtil.toRegion(variant));
    }

    @Test
    public void testHgvsToGenomicVariantToRegion() {
        String region = GenomicVariantUtil.toRegion(GenomicVariantUtil.fromHgvs("12:g.25398285C>A"));

        assertEquals("12:25398285-25398285:1/A", region);

    }

    @Test
    public void testMafToGenomicVariant() {
        ArrayList<GenomicVariant> list = new ArrayList<GenomicVariant>();
        ArrayList<String> mafs = GenomicVariantUtil.getMafs("src/test/java/org/cbioportal/genome_nexus/util/minimal_example.in.maf");
        String[] key = mafs.remove(0).split("\\s");
        for (String maf : mafs) {
            list.add(GenomicVariantUtil.fromMaf(maf, key));
        }
        //3	178916927	178916939	TAGGCAACCGTGA	G
        //7	55220240	55220240	G	T

        GenomicVariant one = list.get(0);
        GenomicVariant two = list.get(1);

        assertEquals("3", one.getChromosome());
        assertEquals(null, one.getRefType());
        assertEquals((Integer) 178916927, one.getStart());
        assertEquals((Integer) 178916939, one.getEnd());
        assertEquals(null, one.getType());
        assertEquals("TAGGCAACCGTGA", one.getRef());
        assertEquals("G", one.getAlt());

        assertEquals("7", two.getChromosome());
        assertEquals(null, two.getRefType());
        assertEquals((Integer) 55220240, two.getStart());
        assertEquals((Integer) 55220240, two.getEnd());
        assertEquals(null, two.getType());
        assertEquals("G", two.getRef());
        assertEquals("T", two.getAlt());
    }
} 