package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;

import java.util.List;

public interface ResourceTransformer<T>
{
    List<T> transform(String value, Class<T> type) throws ResourceMappingException;
}
