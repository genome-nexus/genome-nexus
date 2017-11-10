package org.cbioportal.genome_nexus.service;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExternalResourceFetcher<T>
{
    String fetchJsonString(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException;
    String fetchJsonString(String param) throws HttpClientErrorException, ResourceAccessException;
    List<T> fetchInstances(Map<String, String> queryParams) throws HttpClientErrorException, ResourceAccessException, IOException;
    List<T> fetchInstances(String param) throws HttpClientErrorException, ResourceAccessException, IOException;
}
