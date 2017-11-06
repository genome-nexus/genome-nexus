package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.PdbHeader;

/**
 * @author Selcuk Onur Sumer
 */
public interface PdbDataService
{
    PdbHeader getPdbHeader(String pdbId);
}
