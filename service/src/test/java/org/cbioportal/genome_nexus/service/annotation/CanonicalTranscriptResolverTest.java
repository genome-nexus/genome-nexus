package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CanonicalTranscriptResolverTest
{
    @InjectMocks
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Spy
    private TranscriptConsequencePrioritizer consequencePrioritizer;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void resolveCanonicalTranscript() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertEquals(
            variantMockData.get("1:g.65325832_65325833insG").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT").getTranscriptConsequences().get(1),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        assertEquals(
            variantMockData.get("3:g.14940279_14940280insCAT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            variantMockData.get("3:g.114058003_114058003delG").getTranscriptConsequences().get(5),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        assertEquals(
            variantMockData.get("4:g.9784947_9784948insAGA").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            variantMockData.get("4:g.77675978_77675979insC").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            variantMockData.get("6:g.137519505_137519506delCT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        assertEquals(
            variantMockData.get("6:g.137519505_137519506delCTinsA").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        assertEquals(
            variantMockData.get("7:g.140453136A>T").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(2),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            variantMockData.get("9:g.135797242_135797242delCinsAT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        assertEquals(
            variantMockData.get("10:g.101953779_101953779delT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        assertEquals(
            variantMockData.get("11:g.62393546_62393547delGGinsAA").getTranscriptConsequences().get(1),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        assertEquals(
            variantMockData.get("12:g.25398285C>A").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        assertEquals(
            variantMockData.get("16:g.9057113_9057114insCTG").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(8),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        assertEquals(
            variantMockData.get("22:g.29091840_29091841delTGinsCA").getTranscriptConsequences().get(5),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        assertEquals(
            variantMockData.get("22:g.36689419_36689421delCCT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }
}
