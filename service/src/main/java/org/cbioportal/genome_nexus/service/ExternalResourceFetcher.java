package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

public interface ExternalResourceFetcher<T>
{
    String fetchStringValue(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException;
    String fetchStringValue(String param) throws HttpClientErrorException, ResourceAccessException;
    String fetchStringValue(Object requestBody) throws HttpClientErrorException, ResourceAccessException;
    List<T> fetchInstances(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
    List<T> fetchInstances(String param) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
    List<T> fetchInstances(Object requestBody) throws HttpClientErrorException, ResourceAccessException, ResourceMappingException;
}
