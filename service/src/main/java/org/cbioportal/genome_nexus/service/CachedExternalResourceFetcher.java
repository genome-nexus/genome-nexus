package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;

public interface CachedExternalResourceFetcher<T>
{
    T fetchAndCache(String id) throws ResourceMappingException;
}
