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
public class RefSeqResolverTest
{
    @InjectMocks
    private RefSeqResolver refSeqResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveRefSeqForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "NM_002227.2",
            this.refSeqResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertNull(
            this.refSeqResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        assertEquals(
            "NM_152536.3",
            this.refSeqResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "NM_001164342.1",
            this.refSeqResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        assertEquals(
            "NM_000798.4",
            this.refSeqResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "NM_020859.3",
            this.refSeqResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "NM_000416.2",
            this.refSeqResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        assertEquals(
            "NM_000416.2",
            this.refSeqResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        assertEquals(
            "NM_004333.4",
            this.refSeqResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "NM_032777.9",
            this.refSeqResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "NM_001162426.1",
            this.refSeqResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        assertEquals(
            "NM_001278.3",
            this.refSeqResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        assertEquals(
            "NM_198335.3",
            this.refSeqResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        assertEquals(
            "NM_033360.2",
            this.refSeqResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "NM_004119.2",
            this.refSeqResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        assertEquals(
            "NM_003470.2",
            this.refSeqResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "NM_001193268.1",
            this.refSeqResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        assertEquals(
            "NM_001005735.1",
            this.refSeqResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        assertEquals(
            "NM_002473.4",
            this.refSeqResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }

    @Test
    public void resolveRefSeq() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertEquals(
            "NM_018310.3",
            this.refSeqResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "NM_012200.3",
            this.refSeqResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delGGinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.refSeqResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(1)
            )
        );
    }
}
