package org.cbioportal.genome_nexus.annotation.service;

import org.cbioportal.genome_nexus.annotation.domain.IsoformOverride;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideService
{
    IsoformOverride getIsoformOverride(String source, String id);
    List<IsoformOverride> getIsoformOverrides(String source);
}
