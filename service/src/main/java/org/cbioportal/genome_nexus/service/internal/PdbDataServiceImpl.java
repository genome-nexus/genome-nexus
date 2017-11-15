package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.cbioportal.genome_nexus.persistence.SimpleCacheRepository;
import org.cbioportal.genome_nexus.service.PdbDataService;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderNotFoundException;
import org.cbioportal.genome_nexus.service.remote.PdbHeaderDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class PdbDataServiceImpl implements PdbDataService
{
    private static final Log LOG = LogFactory.getLog(PdbDataServiceImpl.class);

    @Autowired
    private SimpleCacheRepository cacheRepository;

    @Autowired
    private PdbHeaderDataFetcher externalResourceFetcher;

    @Override
    public PdbHeader getPdbHeader(String pdbId) throws PdbHeaderNotFoundException, PdbHeaderWebServiceException {
        boolean saveRawValue = true;
        PdbHeader pdbHeader = null;
        String rawValue = null;

        // try to get the data from database first
        try {
            SimpleCacheEntity entity = cacheRepository.findOne(pdbId);

            if (entity != null)
            {
                // we have the information in the cache already!
                rawValue = entity.getValue();
            }
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the PDB data server. " +
                "Will not attempt to store the value in Mongo database.");
            saveRawValue = false;
        }

        if (rawValue == null)
        {
            // get the annotation from the web service and save it to the DB
            try {
                // get the raw annotation string from the web service
                rawValue = this.externalResourceFetcher.fetchStringValue(pdbId);

                // save everything to the cache as a properly parsed JSON
                if (saveRawValue && rawValue != null && rawValue.length() > 0)
                {
                    // TODO sanitize value before caching?
                    cacheRepository.save(new SimpleCacheEntity(pdbId, rawValue));
                }
            }
            catch (HttpClientErrorException e) {
                // in case of web service error, throw an exception to indicate that there is a problem with the service.
                throw new PdbHeaderWebServiceException(pdbId, e.getResponseBodyAsString(), e.getStatusCode());
            }
            catch (DataIntegrityViolationException e) {
                // in case of data integrity violation exception, do not bloat the logs
                // this is thrown when the rawValue can't be stored by mongo
                // due to the it is too large to index
                LOG.info(e.getLocalizedMessage());
            }
            catch (ResourceAccessException e) {
                throw new PdbHeaderWebServiceException(pdbId, e.getMessage());
            }
        }

        try {
            // construct a PdbHeader instance to return:
            // this does not contain all the information obtained from the web service
            // only the fields mapped to the PdbHeader model will be returned
            pdbHeader = this.externalResourceFetcher.getParser().mapToInstance(pdbId, rawValue);
        } catch (ResourceMappingException e) {
            // TODO this only indicates that web service returns an incompatible response, but
            // this does not always mean that annotation is not found
            throw new PdbHeaderNotFoundException(pdbId);
        }

        if (pdbHeader == null) {
            throw new PdbHeaderNotFoundException(pdbId);
        }

        return pdbHeader;
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
            } catch (PdbHeaderWebServiceException e) {
                e.printStackTrace();
            } catch (PdbHeaderNotFoundException e) {
                // fail silently for this pdb id
            }
        }

        return pdbHeaderList;
    }
}
