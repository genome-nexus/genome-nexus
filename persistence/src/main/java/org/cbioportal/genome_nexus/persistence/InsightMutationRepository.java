package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.InsightMutation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InsightMutationRepository extends MongoRepository<InsightMutation, String>
{
    List<InsightMutation> findByHugoGeneSymbol(String hugoGeneSymbol);
    List<InsightMutation> findByHugoGeneSymbolIn(List<String> hugoGeneSymbol);
}
