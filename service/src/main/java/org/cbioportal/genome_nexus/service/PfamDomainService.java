package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.PfamDomain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface PfamDomainService
{
    List<PfamDomain> getAllPfamDomains();
    List<PfamDomain> getPfamDomainsByTranscriptId(String transcriptId);
    List<PfamDomain> getPfamDomainsByProteinId(String proteinId);
    List<PfamDomain> getPfamDomainsByGeneId(String geneId);
    List<PfamDomain> getPfamDomainsByPfamDomainId(String pfamDomainId);
}
