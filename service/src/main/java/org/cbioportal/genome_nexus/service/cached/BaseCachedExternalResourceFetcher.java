package org.cbioportal.genome_nexus.service.cached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.persistence.GenericMongoRepository;
import org.cbioportal.genome_nexus.service.CachedExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public abstract class BaseCachedExternalResourceFetcher<T, R extends MongoRepository<T, String> & GenericMongoRepository>
    implements CachedExternalResourceFetcher<T>
{
    private static final Log LOG = LogFactory.getLog(BaseCachedExternalResourceFetcher.class);

    protected String collection;
    protected R repository;
    protected Class<T> type;
    protected ExternalResourceFetcher<T> fetcher;
    protected ResourceTransformer<T> transformer;

    public BaseCachedExternalResourceFetcher(String collection,
                                             R repository,
                                             Class<T> type,
                                             ExternalResourceFetcher<T> fetcher,
                                             ResourceTransformer<T> transformer)
    {
        this.collection = collection;
        this.repository = repository;
        this.type = type;
        this.fetcher = fetcher;
        this.transformer = transformer;
    }

    public T fetchAndCache(String id) throws ResourceMappingException
    {
        boolean saveStringValue = true;
        T instance = null;

        try {
            instance = this.repository.findOne(id);
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the external web service. " +
                "Will not attempt to store variant in Mongo database.");
            saveStringValue = false;
        }

        if (instance == null)
        {
            // get the annotation from the web service and save it to the DB
            try {
                // get the raw annotation string from the web service
                String stringValue = this.fetcher.fetchStringValue(id);

                // construct an instance to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                List<T> list = this.transformer.transform(stringValue, this.type);

                if (list.size() > 0) {
                    instance = list.get(0);
                }

                // save everything to the cache as a properly parsed JSON
                if (saveStringValue) {
                    this.repository.saveStringValue(this.collection, id, stringValue);
                }
            }
            catch (DataIntegrityViolationException e) {
                // in case of data integrity violation exception, do not bloat the logs
                // this is thrown when the annotationJSON can't be stored by mongo
                // due to the variant annotation key being too large to index
                LOG.info(e.getLocalizedMessage());
            }
        }

        return instance;
    }
}
