package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.service.exception.EnsemblTranscriptNotFoundException;

import java.util.List;

public interface EnsemblService
{
    EnsemblTranscript getEnsemblTranscriptsByTranscriptId(String transcriptId)
        throws EnsemblTranscriptNotFoundException;

    EnsemblTranscript getCanonicalEnsemblTranscriptByHugoSymbol(String hugoSymbol, String isoformOverrideSource)
        throws EnsemblTranscriptNotFoundException;
    List<EnsemblTranscript> getCanonicalEnsemblTranscriptsByHugoSymbols(List<String> hugoSymbol, String isoformOverrideSource);

    List<EnsemblTranscript> getEnsemblTranscripts(String geneId, String proteinId);
    List<EnsemblTranscript> getEnsemblTranscripts(List<String> transcriptIds, List<String> geneIds, List<String> proteinIds);
}
