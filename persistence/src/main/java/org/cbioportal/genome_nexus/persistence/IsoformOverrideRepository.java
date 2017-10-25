package org.cbioportal.genome_nexus.persistence;

import org.cbioportal.genome_nexus.model.IsoformOverride;

import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideRepository
{
	/**
     * Retrieves one IsoformOverride instance matching the given id.
     *
     * @param id transcript id of the isoform
     * @return  matching IsoformOverride instance
     */
    IsoformOverride findIsoformOverride(String id);

	/**
	 * Returns all available IsoformOverrides as a list.
     * @return List of IsoformOverride instances
     */
    List<IsoformOverride> findAllAsList();

	/**
	 * Returns all available IsoformOverrides as a map keyed on transcript id.
     * @return Map of IsoformOverrides keyed on transcript id
     */
    Map<String, IsoformOverride> findAllAsMap();
}
