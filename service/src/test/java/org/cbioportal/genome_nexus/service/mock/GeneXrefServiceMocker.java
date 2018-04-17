package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

public class GeneXrefServiceMocker
{
    public void mockMethods(Map<String, List<GeneXref>> geneXrefMockData,
                            GeneXrefService geneXrefService)
        throws EnsemblWebServiceException
    {
        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000020181")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000020181")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000027697")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000027697")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000089597")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000089597")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000100345")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000100345")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000122025")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000122025")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000125746")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000125746")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000133703")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000133703")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000138771")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000138771")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000154783")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000154783")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000157764")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000157764")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000162434")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000162434")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000165699")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000165699")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000169676")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000169676")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000180438")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000180438")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000181722")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000181722")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000183765")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000183765")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000187555")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000187555")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000213341")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000213341")
        );
    }
}
