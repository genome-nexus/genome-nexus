package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;

import java.util.List;

public interface EnsemblService
{
    List<EnsemblTranscript> getAllEnsemblTranscripts();
    List<EnsemblTranscript> getEnsemblTranscriptsByTranscriptId(String transcriptId);
    List<EnsemblTranscript> getEnsemblTranscriptsByGeneId(String geneId);
    EnsemblTranscript getEnsemblTranscriptsByHugoSymbol(String hugoSymbol, String isoformOverrideSource);
    List<EnsemblTranscript> getEnsemblTranscriptsByProteinId(String proteinId);
}
