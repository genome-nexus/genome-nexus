package org.cbioportal.genome_nexus.annotation.domain;

import org.cbioportal.genome_nexus.annotation.domain.internal.EnsemblRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EnsemblRepository extends MongoRepository<EnsemblTranscript, String>, EnsemblRepositoryCustom
{
    /*List<EnsemblTranscript> findAll();*/
    List<EnsemblTranscript> findByTranscriptId(String transcriptId);
    List<EnsemblTranscript> findByGeneId(String geneId);
    List<EnsemblTranscript> findByProteinId(String proteinId);
}
