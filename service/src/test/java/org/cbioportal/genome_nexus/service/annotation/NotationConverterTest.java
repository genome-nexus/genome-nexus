package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class NotationConverterTest
{
    private final NotationConverter notationConverter = new NotationConverter();

    @Test
    public void parseGenomicLocation()
    {
        assertNull("Null genomic location input string -> Null output",
            this.notationConverter.parseGenomicLocation(null));

        assertNull("Invalid genomic location input string -> Null output",
            this.notationConverter.parseGenomicLocation("6,6,6"));

        GenomicLocation location = this.notationConverter.parseGenomicLocation("4,9784947,9784948,-,AGA");

        assertEquals("4", location.getChromosome());
        assertEquals((Integer)9784947, location.getStart());
        assertEquals((Integer)9784948, location.getEnd());
        assertEquals("-", location.getReferenceAllele());
        assertEquals("AGA", location.getVariantAllele());
    }

    @Test
    public void genomicToHgvs()
    {
        assertNull("Null genomic location input string -> Null output",
            this.notationConverter.genomicToHgvs((String)null));

        assertNull("Invalid genomic location input string -> Null output",
            this.notationConverter.genomicToHgvs("6,6,6"));

        assertEquals("4:g.9784947_9784948insAGA",
            this.notationConverter.genomicToHgvs("4,9784947,9784948,-,AGA"));

        assertEquals("3:g.14940279_14940280insCAT",
            this.notationConverter.genomicToHgvs("3,14940279,14940280,-,CAT"));

        assertEquals("16:g.9057113_9057114insCTG",
            this.notationConverter.genomicToHgvs("16,9057113,9057114,-,CTG"));

        assertEquals("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG",
            this.notationConverter.genomicToHgvs("13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG"));

        assertEquals("22:g.36689419_36689421delCCT",
            this.notationConverter.genomicToHgvs("22,36689419,36689421,CCT,-"));

        assertEquals("3:g.14106026_14106037delCCAGCAGTAGCT",
            this.notationConverter.genomicToHgvs("3,14106026,14106037,CCAGCAGTAGCT,-"));

        assertEquals("22:g.29091840_29091841delTGinsCA",
            this.notationConverter.genomicToHgvs("22,29091840,29091841,TG,CA"));

        assertEquals("19:g.46141892_46141893delTCinsAA",
            this.notationConverter.genomicToHgvs("19,46141892,46141893,TC,AA"));

        assertEquals("11:g.62393546_62393547delGGinsAA",
            this.notationConverter.genomicToHgvs("11,62393546,62393547,GG,AA"));

        assertEquals("1:g.65325832_65325833insG",
            this.notationConverter.genomicToHgvs("1,65325832,65325833,-,G"));

        assertEquals("4:g.77675978_77675979insC",
            this.notationConverter.genomicToHgvs("4,77675978,77675979,-,C"));

        assertEquals("8:g.37696499_37696500insG",
            this.notationConverter.genomicToHgvs("8,37696499,37696500,-,G"));

        assertEquals("10:g.101953779_101953779delT",
            this.notationConverter.genomicToHgvs("10,101953779,101953779,T,-"));

        assertEquals("6:g.137519505_137519506delCT",
            this.notationConverter.genomicToHgvs("6,137519505,137519506,CT,-"));

        assertEquals("3:g.114058003_114058003delG",
            this.notationConverter.genomicToHgvs("3,114058003,114058003,G,-"));

        assertEquals("9:g.135797242_135797242delCinsAT",
            this.notationConverter.genomicToHgvs("9,135797242,135797242,C,AT"));

        assertEquals("6:g.137519505_137519506delCTinsA",
            this.notationConverter.genomicToHgvs("6,137519505,137519506,CT,A"));

        assertEquals("7:g.140453136A>T",
            this.notationConverter.genomicToHgvs("7,140453136,140453136,A,T"));

        assertEquals("12:g.25398285C>A",
            this.notationConverter.genomicToHgvs("12,25398285,25398285,C,A"));
    }
}
