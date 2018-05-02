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
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ConsequenceTermsResolverTest
{
    @InjectMocks
    private ConsequenceTermsResolver consequenceTermsResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveConsequenceTermsForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "inframe_deletion",
            this.consequenceTermsResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        assertEquals(
            "protein_altering_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        assertEquals(
            "protein_altering_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        assertEquals(
            "missense_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        assertEquals(
            "frameshift_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        assertEquals(
            "stop_gained",
            this.consequenceTermsResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        assertEquals(
            "missense_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "protein_altering_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        assertEquals(
            "protein_altering_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "splice_acceptor_variant,coding_sequence_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        assertEquals(
            "missense_variant",
            this.consequenceTermsResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        assertEquals(
            "inframe_deletion",
            this.consequenceTermsResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }

    @Test
    public void resolveConsequenceTerms() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertEquals(
            "downstream_gene_variant",
            this.consequenceTermsResolver.resolve(
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "upstream_gene_variant",
            this.consequenceTermsResolver.resolve(
                variantMockData.get("11:g.62393546_62393547delGGinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "downstream_gene_variant",
            this.consequenceTermsResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(1)
            )
        );

        assertEquals(
            "splice_acceptor_variant,coding_sequence_variant",
            this.consequenceTermsResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(0))
        );

        assertEquals(
            "splice_acceptor_variant,coding_sequence_variant,NMD_transcript_variant",
            this.consequenceTermsResolver.resolve(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(2))
        );
    }

    @Test
    public void resolveAllConsequenceTerms() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        String[] consequenceTerms1 = {"splice_acceptor_variant", "coding_sequence_variant"};

        assertEquals(
            Arrays.asList(consequenceTerms1),
            this.consequenceTermsResolver.resolveAll(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(0))
        );

        String[] consequenceTerms2 = {"splice_acceptor_variant", "coding_sequence_variant", "NMD_transcript_variant"};

        assertEquals(
            Arrays.asList(consequenceTerms2),
            this.consequenceTermsResolver.resolveAll(
                variantMockData.get("19:g.46141892_46141893delTCinsAA").getTranscriptConsequences().get(2))
        );
    }
}
