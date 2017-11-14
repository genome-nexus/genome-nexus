package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.exception.JsonMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MutationAssessorServiceTest
{
    @InjectMocks
    private MutationAssessorServiceImpl service;

    @Mock
    private MutationAssessorDataFetcher fetcher;

    @Test
    public void getMutationAssessor()
        throws JsonMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        Map<String, List<MutationAssessor>> mockData = this.generateMockData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(fetcher.fetchInstances("7,140453136,A,T")).thenReturn(mockData.get("7,140453136,A,T"));
        Mockito.when(fetcher.fetchInstances("12,25398285,C,A")).thenReturn(mockData.get("12,25398285,C,A"));
        Mockito.when(fetcher.fetchInstances("INVALID")).thenReturn(mockData.get("INVALID"));

        // TODO add more assertions once the mock data is updated
        MutationAssessor mutationAssessor1 = service.getMutationAssessor("7,140453136,A,T", "7:g.140453136A>T");
        assertEquals(mutationAssessor1.getHugoSymbol(), mockData.get("7,140453136,A,T").get(0).getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessor("12,25398285,C,A", "12:g.25398285C>A");
        assertEquals(mutationAssessor2.getHugoSymbol(), mockData.get("12,25398285,C,A").get(0).getHugoSymbol());

        MutationAssessor mutationAssessor3 = service.getMutationAssessor("INVALID", "INVALID");
        assertEquals(mutationAssessor3.getHugoSymbol(), mockData.get("INVALID").get(0).getHugoSymbol());
    }

    // TODO define a better mock data if needed
    private Map<String, List<MutationAssessor>> generateMockData()
    {
        Map<String, List<MutationAssessor>> mockData = new HashMap<>();

        List<MutationAssessor> list;
        MutationAssessor mutationAssessor;

        // mock data for variant: 7,140453136,A,T
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("GENE1");

        list = new ArrayList<>();
        list.add(mutationAssessor);

        mockData.put("7,140453136,A,T", list);

        // mock data for variant: 12,25398285,C,A
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol("GENE2");

        list = new ArrayList<>();
        list.add(mutationAssessor);

        mockData.put("12,25398285,C,A", list);

        // mock data for variant: INVALID
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHugoSymbol(null);

        list = new ArrayList<>();
        list.add(mutationAssessor);

        mockData.put("INVALID", list);

        return mockData;
    }
}
