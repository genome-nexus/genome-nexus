package org.cbioportal.genome_nexus.annotation.domain;

import java.util.List;

public interface EnsemblRepository
{
    List<EnsemblTranscript> findAll();
    List<EnsemblTranscript> findByTranscriptId(String transcriptId);
    List<EnsemblTranscript> findByGeneId(String geneId);
    List<EnsemblTranscript> findByHugoSymbol(String hugoSymbol, String isoformOverrideSource);
    List<EnsemblTranscript> findByProteinId(String proteinId);
}
