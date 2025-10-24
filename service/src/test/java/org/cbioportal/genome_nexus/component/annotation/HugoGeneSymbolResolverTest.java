package org.cbioportal.genome_nexus.component.annotation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.internal.EnsemblRepositoryCustom;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class HugoGeneSymbolResolverTest
{
    @InjectMocks
    private HugoGeneSymbolResolver hugoGeneSymbolResolver;

    @Mock
    private EnsemblRepositoryCustom ensemblRepository;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData =
        new VariantAnnotationMockData();

    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker =
        new CanonicalTranscriptResolverMocker();

    private void mockEnsemblRepositoryCustomMethods()
    {
        // Map of previousSymbol -> (hgncId -> officialSymbol)
        Map<String, Map<String, String>> geneNameMap = new HashMap<>();
        geneNameMap.put("FAM58A", Map.of("28434", "CCNQ"));

        Mockito.lenient()
            .when(ensemblRepository.getOfficialHugoSymbol(Mockito.anyString()))
            .thenAnswer(invocation -> {
                String symbol = invocation.getArgument(0, String.class);
                return symbol;
            });

        Mockito.lenient()
            .when(ensemblRepository.getOfficialHugoSymbol(
                Mockito.anyString(),
                Mockito.anyString()
            ))
            .thenAnswer(invocation -> {
                String prevSymbol = invocation.getArgument(0, String.class);
                String hgncId     = invocation.getArgument(1, String.class);

                Map<String, String> byHgnc = geneNameMap.get(prevSymbol);
                if (byHgnc != null) {
                    String mapped = byHgnc.get(hgncId);
                    if (mapped != null) {
                        return mapped;
                    }
                }

                // default: no rename
                return prevSymbol;
            });
    }

    @Test
    public void resolveHugoGeneSymbolForCanonical() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);
        this.mockEnsemblRepositoryCustomMethods();

        assertEquals(
            "JAK1",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            "TPRXL",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "FGD5",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "ZBTB20",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "DRD5",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "SHROOM3",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "IFNGR1",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "IFNGR1",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "BRAF",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "GPR124",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "TSC1",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "CHUK",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "GANAB",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "KRAS",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "FLT3",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "USP7",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "EML2",
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "CHEK2",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "MYH9",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    @Test
    public void resolveHugoGeneSymbol() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.mockEnsemblRepositoryCustomMethods();

        assertEquals(
            "BRF2",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData
                    .get("8:g.37696499_37696500insG")
                    .getTranscriptConsequences()
                    .get(0)
            )
        );

        assertEquals(
            "B3GAT3",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData
                    .get("11:g.62393546_62393547delinsAA")
                    .getTranscriptConsequences()
                    .get(0)
            )
        );

        assertEquals(
            "MIR330",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData
                    .get("19:g.46141892_46141893delinsAA")
                    .getTranscriptConsequences()
                    .get(1)
            )
        );

        // Historical symbol case:
        // If the mock data for this transcript has geneSymbol="FAM58A" and hgncId="28434",
        // resolver will call getOfficialHugoSymbol("FAM58A","28434"),
        // and our mock will return "CCNQ".
        assertEquals(
            "CCNQ",
            this.hugoGeneSymbolResolver.resolve(
                variantMockData
                    .get("X:g.152859948_152860038del")
                    .getTranscriptConsequences()
                    .get(0)
            )
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation)
    {
        TranscriptConsequence canonicalTc =
            this.canonicalTranscriptResolver.resolve(variantAnnotation);

        return this.hugoGeneSymbolResolver.resolve(canonicalTc);
    }
}
