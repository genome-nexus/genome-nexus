package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
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

import java.util.*;

public abstract class BaseCachedExternalResourceFetcher<T, R extends MongoRepository<T, String> & GenericMongoRepository>
    implements CachedExternalResourceFetcher<T>
{
    private static final Log LOG = LogFactory.getLog(BaseCachedExternalResourceFetcher.class);

    protected String collection;
    protected R repository;
    protected Class<T> type;
    protected ExternalResourceFetcher<T> fetcher;
    protected ResourceTransformer<T> transformer;
    protected Integer maxPageSize;

    public BaseCachedExternalResourceFetcher(String collection,
                                             R repository,
                                             Class<T> type,
                                             ExternalResourceFetcher<T> fetcher,
                                             ResourceTransformer<T> transformer)
    {
        this(collection, repository, type, fetcher, transformer, Integer.MAX_VALUE);
    }

    public BaseCachedExternalResourceFetcher(String collection,
                                             R repository,
                                             Class<T> type,
                                             ExternalResourceFetcher<T> fetcher,
                                             ResourceTransformer<T> transformer,
                                             Integer maxPageSize)
    {
        this.collection = collection;
        this.repository = repository;
        this.type = type;
        this.fetcher = fetcher;
        this.transformer = transformer;
        this.maxPageSize = maxPageSize;
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

    public List<T> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        boolean saveValues = true;
        Set<String> uniqueIds = new LinkedHashSet<>(ids);
        Map<String, T> idToInstance = initIdToInstanceMap(uniqueIds);
        Set<String> alreadyCached = new LinkedHashSet<>();

        try {
            // add everything already cached into the map
            for (T instance: this.repository.findAll(uniqueIds))
            {
                String id = this.extractId(instance);
                idToInstance.put(id, instance);
                alreadyCached.add(id);
            }
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on the external web service. " +
                "Will not attempt to store variant in Mongo database.");
            saveValues = false;
        }

        Set<String> needToFetch = new LinkedHashSet<>(uniqueIds);

        // remove already cached ids from the set, so that we don't query again
        needToFetch.removeAll(alreadyCached);

        // fetch missing instances
        if (needToFetch.size() > 0) {
            // get the annotation from the web service and save it to the DB
            this.fetchAndCache(needToFetch, idToInstance, saveValues);
        }

        Collection<T> values = idToInstance.values();
        values.removeIf(Objects::isNull);

        return new ArrayList<>(values);
    }

    private void fetchAndCache(Set<String> needToFetch,
                               Map<String, T> idToInstance,
                               boolean saveValues) throws ResourceMappingException
    {
        List<String> list = new ArrayList<>(needToFetch);

        try {
            // send up to maxPageSize entities per request
            for (int i = 0; i < list.size(); i += this.maxPageSize)
            {
                Set<String> subSet = new LinkedHashSet<>(list.subList(i, Math.min(list.size(), i + this.maxPageSize)));

                // get the raw annotation string from the web service
                String stringValue = this.fetcher.fetchStringValue(this.buildRequestBody(subSet));

                // fetch instances to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                List<T> fetched = this.transformer.transform(stringValue, this.type);
                fetched.forEach(t -> idToInstance.put(this.extractId(t), t));

                // save everything to the cache as a properly parsed JSON
                if (saveValues) {
                    this.saveToDb(stringValue);
                }
            }
        }
        catch (DataIntegrityViolationException e) {
            // in case of data integrity violation exception, do not bloat the logs
            // this is thrown when the annotationJSON can't be stored by mongo
            // due to the variant annotation key being too large to index
            LOG.info(e.getLocalizedMessage());
        }
    }

    protected void saveToDb(String value)
    {
        List<DBObject> dbObjects = this.transformer.transform(value);

        for (DBObject dbObject: dbObjects) {
            dbObject.put("_id", this.extractId(dbObject));
        }

        this.repository.saveDBObjects(this.collection, dbObjects);
    }

    private Map<String, T> initIdToInstanceMap(Set<String> ids)
    {
        Map<String, T> map = new LinkedHashMap<>();

        // this will ensure the final map has the same order of the input ids
        ids.forEach(id -> map.put(id, null));

        return map;
    }

    // Needs to be overridden by child classes to support multi fetch and cache!
    protected String extractId(T instance)
    {
        return null;
    }

    // Needs to be overridden by child classes to support multi fetch and cache!
    protected String extractId(DBObject dbObject)
    {
        return null;
    }

    protected Object buildRequestBody(Set<String> ids)
    {
        return ids;
    }
}
