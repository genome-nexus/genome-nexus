package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.cbioportal.genome_nexus.persistence.SimpleCacheRepository;
import org.cbioportal.genome_nexus.service.PdbDataService;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class PdbDataServiceImpl implements PdbDataService
{
    private String headerServiceURL;
    @Value("${pdb.header_service_url}")
    public void setHeaderServiceURL(String headerServiceURL)
    {
        this.headerServiceURL = headerServiceURL;
    }

    @Autowired
    private SimpleCacheRepository cacheRepository;

    @Autowired
    private PdbFileParser pdbParser;

    @Override
    public PdbHeader getPdbHeader(String pdbId) throws PdbHeaderNotFoundException
    {
        PdbHeader info = null;

        if (pdbId != null &&
            pdbId.length() > 0)
        {
            String rawData = this.getRawInfo(pdbId);

            if (rawData != null)
            {
                Map<String, String> content = this.pdbParser.parsePdbFile(rawData);

                info = new PdbHeader();

                info.setPdbId(pdbId);
                info.setTitle(this.pdbParser.parseTitle(content.get("title")));
                info.setCompound(this.pdbParser.parseCompound(content.get("compnd")));
                info.setSource(this.pdbParser.parseCompound(content.get("source")));
            }
        }

        if (info == null) {
            throw new PdbHeaderNotFoundException(pdbId);
        }

        return info;
    }

    @Override
    public List<PdbHeader> getPdbHeaders(List<String> pdbIds)
    {
        List<PdbHeader> pdbHeaderList = new LinkedList<>();

        // remove duplicates
        Set<String> pdbIdSet = new LinkedHashSet<>(pdbIds);

        for (String pdbId : pdbIdSet)
        {
            try {
                PdbHeader header = this.getPdbHeader(pdbId);
                pdbHeaderList.add(header);
            } catch (PdbHeaderNotFoundException e) {
                // fail silently for this pdb id
            }
        }

        return pdbHeaderList;
    }

    private String getRawInfo(String pdbId)
    {
        // try to get the data from database first
        SimpleCacheEntity entity = cacheRepository.findOne(pdbId);

        // we have the information in the cache already!
        if (entity != null)
        {
            return entity.getValue();
        }

        //http://www.rcsb.org/pdb/files/PDB_ID.pdb?headerOnly=YES
        //http://files.rcsb.org/header/PDB_ID.pdb
        String uri = headerServiceURL.replace("PDB_ID", pdbId.toUpperCase());
        RestTemplate restTemplate = new RestTemplate();

        try {
            String value = restTemplate.getForObject(uri, String.class);
            // cache the retrieved value
            if (value != null && value.length() > 0)
            {
                // TODO sanitize value before caching?
                cacheRepository.save(new SimpleCacheEntity(pdbId, value));
            }
            return value;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
}
