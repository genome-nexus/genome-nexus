package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.PfamDomain;
import org.cbioportal.genome_nexus.persistence.PfamDomainRepository;
import org.cbioportal.genome_nexus.service.PfamDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class PfamDomainServiceImpl implements PfamDomainService
{
    private final PfamDomainRepository pfamDomainRepository;

    @Autowired
    public PfamDomainServiceImpl(
        @Qualifier("defaultPfamDomainRepository") PfamDomainRepository pfamDomainRepository)
    {
        this.pfamDomainRepository = pfamDomainRepository;
    }

    @Override
    public List<PfamDomain> getAllPfamDomains() {
        return this.pfamDomainRepository.findAll();
    }

    @Override
    public List<PfamDomain> getPfamDomainsByTranscriptId(String transcriptId)
    {
        return this.pfamDomainRepository.findByTranscriptId(transcriptId);
    }

    @Override
    public List<PfamDomain> getPfamDomainsByProteinId(String proteinId)
    {
        return this.pfamDomainRepository.findByProteinId(proteinId);
    }

    @Override
    public List<PfamDomain> getPfamDomainsByGeneId(String geneId)
    {
        return this.pfamDomainRepository.findByGeneId(geneId);
    }

    @Override
    public List<PfamDomain> getPfamDomainsByPfamDomainId(String pfamDomainId)
    {
        return this.pfamDomainRepository.findByPfamDomainId(pfamDomainId);
    }
}
