package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.persistence.EnsemblRepository;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.exception.EnsemblTranscriptNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnsemblServiceImpl implements EnsemblService
{
    private final EnsemblRepository ensemblRepository;

    @Autowired
    public EnsemblServiceImpl(EnsemblRepository ensemblRepository)
    {
        this.ensemblRepository = ensemblRepository;
    }

    @Override
    public EnsemblTranscript getEnsemblTranscriptsByTranscriptId(String transcriptId) {
        return this.ensemblRepository.findOneByTranscriptId(transcriptId);
    }

    @Override
    public EnsemblTranscript getCanonicalEnsemblTranscriptByHugoSymbol(String hugoSymbol, String isoformOverrideSource)
        throws EnsemblTranscriptNotFoundException
    {
        EnsemblTranscript transcript = this.ensemblRepository.findOneByHugoSymbol(hugoSymbol, isoformOverrideSource);

        if (transcript == null) {
            throw new EnsemblTranscriptNotFoundException(hugoSymbol, isoformOverrideSource);
        }

        return transcript;
    }

    @Override
    public List<EnsemblTranscript> getCanonicalEnsemblTranscriptsByHugoSymbols(List<String> hugoSymbols, String isoformOverrideSource) {
        List<EnsemblTranscript> transcripts = new ArrayList<>();

        for (String hugoSymbol : hugoSymbols)
        {
            try {
                transcripts.add(this.getCanonicalEnsemblTranscriptByHugoSymbol(hugoSymbol, isoformOverrideSource));
            } catch (EnsemblTranscriptNotFoundException e) {
                // ignore the exception for this transcript
            }
        }

        return transcripts;
    }

    @Override
    public List<EnsemblTranscript> getEnsemblTranscripts(String geneId, String proteinId)
    {
        if (geneId == null && proteinId == null)
        {
            return this.ensemblRepository.findAll();
        }
        else if (geneId != null && proteinId != null)
        {
            return this.ensemblRepository.findByGeneIdAndProteinId(geneId, proteinId);
        }
        else if (geneId != null)
        {
            return this.ensemblRepository.findByGeneId(geneId);
        }
        else
        {
            return this.ensemblRepository.findByProteinId(proteinId);
        }
    }

    @Override
    public List<EnsemblTranscript> getEnsemblTranscripts(List<String> transcriptIds, List<String> geneIds, List<String> proteinIds) {
        if (transcriptIds != null) {
            return this.ensemblRepository.findByTranscriptIdIn(transcriptIds);
        }
        else if (geneIds != null) {
            return this.ensemblRepository.findByGeneIdIn(geneIds);
        }
        else if (proteinIds != null) {
            return this.ensemblRepository.findByProteinIdIn(proteinIds);
        }
        else {
            return this.ensemblRepository.findAll();
        }
    }
}
