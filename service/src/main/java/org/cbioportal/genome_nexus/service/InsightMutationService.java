package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.InsightMutation;

import java.util.List;

public interface InsightMutationService
{
    List<InsightMutation> getInsightMutations();
    List<InsightMutation> getInsightMutations(String hugoGeneSymbol);
    List<InsightMutation> getInsightMutations(List<String> hugoGeneSymbols);
}
