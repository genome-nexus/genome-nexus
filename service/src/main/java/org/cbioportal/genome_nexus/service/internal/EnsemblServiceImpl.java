package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.persistence.EnsemblRepository;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnsemblServiceImpl implements EnsemblService
{
    private final EnsemblRepository ensemblRepository;

    @Override
    public List<EnsemblTranscript> getAllEnsemblTranscripts() {
        return this.ensemblRepository.findAll();
    }

    @Override
    public List<EnsemblTranscript> getEnsemblTranscriptsByTranscriptId(String transcriptId) {
        return this.ensemblRepository.findByTranscriptId(transcriptId);
    }

    @Override
    public List<EnsemblTranscript> getEnsemblTranscriptsByGeneId(String geneId) {
        return this.ensemblRepository.findByGeneId(geneId);
    }

    @Override
    public List<EnsemblTranscript> getEnsemblTranscriptsByProteinId(String proteinId) {
        return this.ensemblRepository.findByProteinId(proteinId);
    }

    @Override
    public EnsemblTranscript getCanonicalEnsemblTranscriptsByHugoSymbol(String hugoSymbol, String isoformOverrideSource) {
        return this.ensemblRepository.findByHugoSymbol(hugoSymbol, isoformOverrideSource);
    }

    @Autowired
    public EnsemblServiceImpl(EnsemblRepository ensemblRepository)
    {
        this.ensemblRepository = ensemblRepository;
    }
}
