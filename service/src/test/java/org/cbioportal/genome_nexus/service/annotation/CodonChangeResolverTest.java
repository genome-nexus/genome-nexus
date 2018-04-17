package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CodonChangeResolverTest
{
    @InjectMocks
    private CodonChangeResolver codonChangeResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveCodonChangeForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "ccg/ccCg",
            this.codonChangeResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "cCCAGCAGTAGCTcc/ccc",
            this.codonChangeResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        assertEquals(
            "cat/cCATat",
            this.codonChangeResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "cCt/ct",
            this.codonChangeResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        assertEquals(
            "ggt/gAGAgt",
            this.codonChangeResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "gcc/gCcc",
            this.codonChangeResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "AGt/t",
            this.codonChangeResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        assertEquals(
            "AGt/Tt",
            this.codonChangeResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        assertEquals(
            "gTg/gAg",
            this.codonChangeResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "gtg/gtGg",
            this.codonChangeResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "atG/atAT",
            this.codonChangeResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        assertEquals(
            "Agg/gg",
            this.codonChangeResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        assertEquals(
            "ctCCag/ctTTag",
            this.codonChangeResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        assertEquals(
            "Ggt/Tgt",
            this.codonChangeResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "tTCAGAGAATATGAATATGat/tCCCCCCCACCCCat",
            this.codonChangeResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        assertEquals(
            "cag/caCAGg",
            this.codonChangeResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertNull(
            this.codonChangeResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        assertEquals(
            "tcCAag/tcTGag",
            this.codonChangeResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        assertEquals(
            "gAGGcc/gcc",
            this.codonChangeResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }

    @Test
    public void resolveCodonChange() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delGGinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.codonChangeResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(1)
            )
        );
    }
}
