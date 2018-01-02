package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.HashMap;
import java.util.Map;

public class MutationAssessorMockData implements MockData<MutationAssessor>
{
    @Override
    public Map<String, MutationAssessor> generateData()
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
