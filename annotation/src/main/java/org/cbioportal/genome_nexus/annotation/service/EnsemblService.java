package org.cbioportal.genome_nexus.annotation.service;

import org.cbioportal.genome_nexus.annotation.domain.EnsemblTranscript;

import java.util.List;

public interface EnsemblService
{
    List<EnsemblTranscript> getAllEnsemblTranscripts();
    List<EnsemblTranscript> getEnsemblTranscriptsByTranscriptId(String transcriptId);
    List<EnsemblTranscript> getEnsemblTranscriptsByGeneId(String geneId);
    List<EnsemblTranscript> getEnsemblTranscriptsByHugoSymbol(String hugoSymbol, String isoformOverrideSource);
    List<EnsemblTranscript> getEnsemblTranscriptsByProteinId(String proteinId);
}
