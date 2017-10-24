package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.PfamDomain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface PfamDomainRepository
{
    List<PfamDomain> findAll();
    List<PfamDomain> findByTranscriptId(String transcriptId);
    List<PfamDomain> findByProteinId(String proteinId);
    List<PfamDomain> findByGeneId(String geneId);
    List<PfamDomain> findByPfamDomainId(String pfamDomainId);
}
