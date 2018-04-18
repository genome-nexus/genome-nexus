package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.PdbHeaderTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

@Component
public class PdbHeaderDataFetcher extends BaseExternalResourceFetcher<PdbHeader>
{
    private static final String MAIN_QUERY_PARAM = "pdbId";
    private static final String PLACEHOLDER = "PDB_ID";

    private final PdbHeaderTransformer transformer;

    @Autowired
    public PdbHeaderDataFetcher(PdbHeaderTransformer transformer,
                                @Value("${pdb.header_service_url}") String headerServiceUrl)
    {
        //http://www.rcsb.org/pdb/files/PDB_ID.pdb?headerOnly=YES
        //http://files.rcsb.org/header/PDB_ID.pdb
        super(headerServiceUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    @Override
    public List<PdbHeader> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        String rawValue = this.fetchStringValue(queryParams);
        //String pdbId = queryParams.get(MAIN_QUERY_PARAM);

        return this.transformer.transform(rawValue, PdbHeader.class);
    }

    @Override
    public List<PdbHeader> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchStringValue(requestBody), PdbHeader.class);
    }

    public PdbHeaderTransformer getTransformer() {
        return transformer;
    }
}
