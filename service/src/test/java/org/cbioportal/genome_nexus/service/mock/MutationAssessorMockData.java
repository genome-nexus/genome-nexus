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

        // mock data for variant: P15056,p.V600E
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHgvspShort("p.V600E");
        mutationAssessor.setUniprotId("P15056");
        mutationAssessor.setFunctionalImpactScore(7.110925419276407);
        mutationAssessor.setFunctionalImpactPrediction("high");
        mutationAssessor.setMsa("P15056.4_full_b0.6_rg0.3");
        mutationAssessor.setMav(4);
        mutationAssessor.setSv(4);
        mockData.put("P15056,p.V600E", mutationAssessor);

        // mock data for variant: P01116,p.G12C
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHgvspShort("p.G12C");
        mutationAssessor.setUniprotId("P01116");
        mutationAssessor.setFunctionalImpactScore(5.8627536904542215);
        mutationAssessor.setFunctionalImpactPrediction("medium");
        mutationAssessor.setMsa("P01116.1_full_b1.0_rg0.3");
        mutationAssessor.setMav(4);
        mutationAssessor.setSv(1);
        mockData.put("P01116,p.G12C", mutationAssessor);

        // mock data for variant: p.S601F, P00519
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHgvspShort("p.L25V");
        mutationAssessor.setUniprotId("P00519");
        mutationAssessor.setFunctionalImpactScore(2.554887124363546);
        mutationAssessor.setFunctionalImpactPrediction("neutral");
        mutationAssessor.setMsa("P00519.4_full_b0.6_rg0.3");
        mutationAssessor.setMav(4);
        mutationAssessor.setSv(4);
        mockData.put("P00519,p.L25V", mutationAssessor);

        // mock data for variant: INVALID
        mutationAssessor = new MutationAssessor();
        mutationAssessor.setHgvspShort(null);
        mutationAssessor.setUniprotId(null);
        mutationAssessor.setFunctionalImpactScore(null);
        mutationAssessor.setFunctionalImpactPrediction(null);
        mutationAssessor.setMsa(null);
        mutationAssessor.setMav(null);
        mutationAssessor.setSv(null);
        mockData.put("INVALID,INVALID", mutationAssessor);
        return mockData;
    }
}
