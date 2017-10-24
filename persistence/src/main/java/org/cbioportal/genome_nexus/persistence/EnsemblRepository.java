package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.persistence.internal.EnsemblRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EnsemblRepository extends MongoRepository<EnsemblTranscript, String>, EnsemblRepositoryCustom
{
    /*List<EnsemblTranscript> findAll();*/
    List<EnsemblTranscript> findByTranscriptId(String transcriptId);
    List<EnsemblTranscript> findByGeneId(String geneId);
    List<EnsemblTranscript> findByProteinId(String proteinId);
}
