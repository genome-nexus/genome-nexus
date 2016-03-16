package org.cbioportal.genome_nexus.annotation.service;

import org.cbioportal.genome_nexus.annotation.domain.IsoformOverride;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideService
{
    IsoformOverride getIsoformOverride(String source, String id);
}
