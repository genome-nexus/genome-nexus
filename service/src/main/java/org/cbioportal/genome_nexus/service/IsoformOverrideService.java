package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.service.exception.IsoformOverrideNotFoundException;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideService
{
    IsoformOverride getIsoformOverride(String source, String id) throws IsoformOverrideNotFoundException;
    List<IsoformOverride> getIsoformOverrides(String source) throws IsoformOverrideNotFoundException;
    List<IsoformOverride> getIsoformOverrides(String source, List<String> transcriptIds);
    List<String> getOverrideSources();
    Boolean hasData(String source);
}
