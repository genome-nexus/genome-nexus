package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.IsoformOverride;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideService
{
    IsoformOverride getIsoformOverride(String source, String id);
    List<IsoformOverride> getIsoformOverrides(String source);
    List<String> getOverrideSources();
    Boolean hasData(String source);
}
