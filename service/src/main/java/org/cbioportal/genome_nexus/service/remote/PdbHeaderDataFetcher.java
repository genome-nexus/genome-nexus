package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.internal.PdbFileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PdbHeaderDataFetcher extends BaseExternalResourceFetcher<PdbHeader>
{
    private static final String MAIN_QUERY_PARAM = "pdbId";
    private static final String PLACEHOLDER = "PDB_ID";

    private final PdbFileParser parser;

    @Autowired
    public PdbHeaderDataFetcher(PdbFileParser parser,
                                @Value("${pdb.header_service_url}") String headerServiceUrl)
    {
        //http://www.rcsb.org/pdb/files/PDB_ID.pdb?headerOnly=YES
        //http://files.rcsb.org/header/PDB_ID.pdb
        super(headerServiceUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.parser = parser;
    }

    @Override
    public List<PdbHeader> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        List<PdbHeader> instances = null;
        String rawValue = this.fetchStringValue(queryParams);
        String pdbId = queryParams.get(MAIN_QUERY_PARAM);
        PdbHeader pdbHeader = this.parser.mapToInstance(pdbId, rawValue);

        if (pdbHeader != null) {
            instances = new ArrayList<>(1);
            instances.add(pdbHeader);
        }

        return instances;
    }

    public PdbFileParser getParser() {
        return parser;
    }
}
