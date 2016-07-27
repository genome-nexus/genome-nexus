package org.cbioportal.genome_nexus.annotation.domain;

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
}
