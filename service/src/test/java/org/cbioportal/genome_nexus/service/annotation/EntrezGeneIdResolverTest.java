package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForHugoSymbolException;
import org.cbioportal.genome_nexus.service.mock.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EntrezGeneIdResolverTest
{
    @InjectMocks
    private EntrezGeneIdResolver entrezGeneIdResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Mock
    private GeneXrefService geneXrefService;

    @Mock
    private EnsemblService ensemblService;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final GeneXrefMockData geneXrefMockData = new GeneXrefMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();
    private final GeneXrefServiceMocker geneXrefServiceMocker = new GeneXrefServiceMocker();

    private void mockGetCanonicalEnsemblGeneIdByHugoSymbol(String hugoSymbol, String entrezGeneId) {
        EnsemblGene eg;
        try {
            eg = new EnsemblGene();
            eg.setEntrezGeneId(entrezGeneId);
            Mockito.when(ensemblService.getCanonicalEnsemblGeneIdByHugoSymbol(hugoSymbol)).thenReturn(eg);
        } catch (NoEnsemblGeneIdForHugoSymbolException e) {
            // silently ignore error
        }
    }

    private void mockEnsemblService() {
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("JAK1", "3716");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("FGD5", "152273");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("ZBTB20", "26137");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("DRD5", "1816");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("SHROOM3", "57619");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("IFNGR1", "3459");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("BRAF", "673");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("GPR124", "25960");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("TSC1", "7248");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("CHUK", "1147");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("GANAB", "23193");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("KRAS", "3845");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("FLT3", "2322");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("USP7", "7874");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("EML2", "24139");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("CHEK2", "11200");
        mockGetCanonicalEnsemblGeneIdByHugoSymbol("MYH9", "4627");
    }

    @Test
    public void resolveEntrezGeneIdForCanonical() throws IOException, EnsemblWebServiceException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, List<GeneXref>> geneXrefMockData = this.geneXrefMockData.generateData();

        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);
        this.geneXrefServiceMocker.mockMethods(geneXrefMockData, this.geneXrefService);
        this.mockEnsemblService();

        assertEquals(
            "3716",
            this.entrezGeneIdResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertNull(
            "No EntrezGene info exists for the canonical transcript of 3:g.14106026_14106037delCCAGCAGTAGCT",
            this.entrezGeneIdResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        assertEquals(
            "152273",
            this.entrezGeneIdResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "26137",
            this.entrezGeneIdResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        assertEquals(
            "1816",
            this.entrezGeneIdResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "57619",
            this.entrezGeneIdResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "3459",
            this.entrezGeneIdResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        assertEquals(
            "3459",
            this.entrezGeneIdResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        assertEquals(
            "673",
            this.entrezGeneIdResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "25960",
            this.entrezGeneIdResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "7248",
            this.entrezGeneIdResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        assertEquals(
            "1147",
            this.entrezGeneIdResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        assertEquals(
            "23193",
            this.entrezGeneIdResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        assertEquals(
            "3845",
            this.entrezGeneIdResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "2322",
            this.entrezGeneIdResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        assertEquals(
            "7874",
            this.entrezGeneIdResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "24139",
            this.entrezGeneIdResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        assertEquals(
            "11200",
            this.entrezGeneIdResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        assertEquals(
            "4627",
            this.entrezGeneIdResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }
}
