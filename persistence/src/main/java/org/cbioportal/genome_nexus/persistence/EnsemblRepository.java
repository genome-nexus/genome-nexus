package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.persistence.internal.EnsemblRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EnsemblRepository extends MongoRepository<EnsemblTranscript, String>, EnsemblRepositoryCustom
{
    /*List<EnsemblTranscript> findAll();*/
    EnsemblTranscript findOneByTranscriptId(String transcriptId);

    List<EnsemblTranscript> findByTranscriptIdIn(List<String> transcriptIds);
    List<EnsemblTranscript> findByGeneId(String geneId);
    List<EnsemblTranscript> findByGeneIdIn(List<String> geneIds);
    List<EnsemblTranscript> findByProteinId(String proteinId);
    List<EnsemblTranscript> findByProteinIdIn(List<String> proteinIds);
    List<EnsemblTranscript> findByGeneIdAndProteinId(String geneId, String proteinId);
}
