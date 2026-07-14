package org.cbioportal.genome_nexus.component.annotation;

import java.io.IOException;
import java.util.Map;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.cbioportal.genome_nexus.service.mock.VariantClassificationResolverMocker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProteinChangeResolverTest
{
    @InjectMocks
    private ProteinChangeResolver proteinChangeResolver;

    @Mock
    private VariantClassificationResolver variantClassificationResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();
    private final VariantClassificationResolverMocker variantClassificationResolverMocker = new VariantClassificationResolverMocker();

    @Test
    public void resolveHgvspShortForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);
        this.variantClassificationResolverMocker.mockMethods(variantMockData, this.variantClassificationResolver);

        assertEquals(
            "p.L431Vfs*22",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "p.S124_S127del",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "p.H1034delinsPY",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "p.P692Lfs*43",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "p.G432delinsES",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "p.D1450*",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "p.S378Ffs*6",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "p.S378Ffs*5",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "p.V600E",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "p.A765Rfs*98",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "p.M209Ifs*2",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "p.R646Gfs*22",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "p.Q928*",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "p.G12C",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "p.F594_D600delinsSPPPH",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "p.Q10delinsHR",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "p.X218_splice",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "p.K416E",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "p.E1350del",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("22:g.36689419_36689421del"))
        );

        assertEquals(
            "p.Q575_G592dup",
            this.proteinChangeResolver.resolveHgvspShort(variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT"))
        );
    }

    @Test
    public void resolveHgvspShort() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.variantClassificationResolverMocker.mockMethods(variantMockData, this.variantClassificationResolver);

        assertNull(
            this.proteinChangeResolver.resolveHgvspShort(
                variantMockData.get("8:g.37696499_37696500insG"),
                variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(0)
            )
        );

        assertNull(
            this.proteinChangeResolver.resolveHgvspShort(
                variantMockData.get("11:g.62393546_62393547delinsAA"),
                variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "p.X17_splice",
            this.proteinChangeResolver.resolveHgvspShort(
                variantMockData.get("19:g.46141892_46141893delinsAA"),
                variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "p.K373E",
            this.proteinChangeResolver.resolveHgvspShort(
                variantMockData.get("22:g.29091840_29091841delinsCA"),
                variantMockData.get("22:g.29091840_29091841delinsCA").getTranscriptConsequences().get(0)
            )
        );

        assertEquals(
            "p.L210=",
            this.proteinChangeResolver.resolveHgvspShort(
                variantMockData.get("7:g.55220240G>T"),
                variantMockData.get("7:g.55220240G>T").getTranscriptConsequences().get(0)
            )
        );
    }

    @Test
    public void resolveHgvsp() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.variantClassificationResolverMocker.mockMethods(variantMockData, this.variantClassificationResolver);

        // Missense variant: BRAF V600E - should normalize hgvsp (strip transcript prefix)
        TranscriptConsequence brafCanonical = variantMockData.get("7:g.140453136A>T").getTranscriptConsequences().get(0);
        assertEquals(
            "p.Val600Glu",
            this.proteinChangeResolver.resolveHgvsp(brafCanonical)
        );

        // Missense variant: KRAS G12C
        TranscriptConsequence krasCanonical = variantMockData.get("12:g.25398285C>A").getTranscriptConsequences().get(0);
        assertEquals(
            "p.Gly12Cys",
            this.proteinChangeResolver.resolveHgvsp(krasCanonical)
        );

        // Inframe deletion: should have valid hgvsp
        TranscriptConsequence delCanonical = variantMockData.get("22:g.36689419_36689421del").getTranscriptConsequences().get(0);
        assertEquals(
            "p.Glu1350del",
            this.proteinChangeResolver.resolveHgvsp(delCanonical)
        );

        // Protein altering variant (delins): FLT3
        TranscriptConsequence flt3Canonical = variantMockData.get("13:g.28608258_28608275del").getTranscriptConsequences().get(0);
        assertEquals(
            "p.Phe594_Asp600delinsSerProProProHis",
            this.proteinChangeResolver.resolveHgvsp(flt3Canonical)
        );

        // Frameshift variant: should have valid hgvsp
        TranscriptConsequence fsCanonical = variantMockData.get("6:g.137519505_137519506delinsA").getTranscriptConsequences().get(0);
        assertEquals(
            "p.Ser378PhefsTer5",
            this.proteinChangeResolver.resolveHgvsp(fsCanonical)
        );

        // Splice acceptor variant: hgvsp should be null (splice site exclusion)
        TranscriptConsequence spliceCanonical = variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(0);
        assertNull(
            this.proteinChangeResolver.resolveHgvsp(spliceCanonical)
        );

        // Null transcript consequence: should return null
        assertNull(
            this.proteinChangeResolver.resolveHgvsp(null)
        );
    }

}
