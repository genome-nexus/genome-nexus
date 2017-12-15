package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.cached.CachedMutationAssessorFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MutationAssessorServiceTest
{
    @InjectMocks
    private MutationAssessorServiceImpl service;

    @Mock
    private CachedMutationAssessorFetcher fetcher;

    @Test
    public void getMutationAssessor()
        throws ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        Map<String, MutationAssessor> mockData = this.generateMockData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(fetcher.fetchAndCache("7,140453136,A,T")).thenReturn(mockData.get("7,140453136,A,T"));
        Mockito.when(fetcher.fetchAndCache("12,25398285,C,A")).thenReturn(mockData.get("12,25398285,C,A"));
        Mockito.when(fetcher.fetchAndCache("INVALID")).thenReturn(mockData.get("INVALID"));

        // TODO add more assertions once the mock data is updated
        MutationAssessor mutationAssessor1 = service.getMutationAssessorByMutationAssessorVariant("7,140453136,A,T");
        assertEquals(mutationAssessor1.getHugoSymbol(), mockData.get("7,140453136,A,T").getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessorByMutationAssessorVariant("12,25398285,C,A");
        assertEquals(mutationAssessor2.getHugoSymbol(), mockData.get("12,25398285,C,A").getHugoSymbol());

        MutationAssessor mutationAssessor3 = service.getMutationAssessorByMutationAssessorVariant("INVALID");
        assertEquals(mutationAssessor3.getHugoSymbol(), mockData.get("INVALID").getHugoSymbol());
    }

    // TODO define a better mock data if needed
    private Map<String, MutationAssessor> generateMockData()
    {
        Map<String, MutationAssessor> mockData = new HashMap<>();

        MutationAssessor mutationAssessor;

        // mock data for variant: 7,140453136,A,T
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("GENE1");
        mutationAssessor.setHgvs("7:g.140453136A>T");

        mockData.put("7,140453136,A,T", mutationAssessor);

        // mock data for variant: 12,25398285,C,A
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("GENE2");
        mutationAssessor.setHgvs("12:g.25398285C>A");

        mockData.put("12,25398285,C,A", mutationAssessor);

        // mock data for variant: INVALID
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol(null);
        mutationAssessor.setHgvs(null);

        mockData.put("INVALID", mutationAssessor);

        return mockData;
    }
}
