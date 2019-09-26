package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.InsightMutation;
import org.cbioportal.genome_nexus.persistence.InsightMutationRepository;
import org.cbioportal.genome_nexus.service.InsightMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsightMutationServiceImpl implements InsightMutationService
{
    private final InsightMutationRepository insightMutationRepository;

    @Autowired
    public InsightMutationServiceImpl(InsightMutationRepository insightMutationRepository) {
        this.insightMutationRepository = insightMutationRepository;
    }

    @Override
    public List<InsightMutation> getInsightMutations() {
        return this.insightMutationRepository.findAll();
    }

    @Override
    public List<InsightMutation> getInsightMutations(String hugoGeneSymbol) {
        return this.insightMutationRepository.findByHugoGeneSymbol(hugoGeneSymbol);
    }

    @Override
    public List<InsightMutation> getInsightMutations(List<String> hugoGeneSymbols) {
        return this.insightMutationRepository.findByHugoGeneSymbolIn(hugoGeneSymbols);
    }
}
