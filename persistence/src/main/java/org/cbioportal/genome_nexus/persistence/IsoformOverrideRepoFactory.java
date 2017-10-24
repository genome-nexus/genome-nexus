package org.cbioportal.genome_nexus.persistence;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface IsoformOverrideRepoFactory
{
	/**
     * Get the IsoformOverrideRepository instance corresponding to the given source id.
     *
     * @param id    source id for the repository
     * @return an IsoformOverrideRepository instance
     */
    IsoformOverrideRepository getRepository(String id);

	/**
     * Returns the list of all available isoform override data source names.
     *
     * @return list of available override sources
     */
    List<String> getOverrideSources();
}
