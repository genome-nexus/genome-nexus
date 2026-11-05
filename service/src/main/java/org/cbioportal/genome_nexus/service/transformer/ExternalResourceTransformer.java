package org.cbioportal.genome_nexus.service.transformer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.util.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExternalResourceTransformer<T> implements ResourceTransformer<T>
{
    private static final Log LOG = LogFactory.getLog(ExternalResourceTransformer.class);

    private final ObjectMapper externalResourceObjectMapper;

    @Autowired
    public ExternalResourceTransformer(
        @Qualifier("defaultExternalResourceObjectMapper") ObjectMapper externalResourceObjectMapper)
    {
        this.externalResourceObjectMapper = externalResourceObjectMapper;
    }

    @Override
    public List<T> transform(DBObject json, Class<T> type) throws ResourceMappingException
    {
        return this.mapJsonToInstance(json, type, this.externalResourceObjectMapper);
    }

    @Override
    public List<DBObject> transform(DBObject rawJson)
    {
        return Transformer.convertToDbObjectList(rawJson);
    }

    /**
     * Maps the given raw JSON DBObject onto the provided class instances.
     *
     * A single entity that cannot be mapped (for example because the external
     * service returned an unexpected shape for one variant) is skipped instead
     * of discarding the entire batch. Only a payload that cannot be split into
     * entities at all is reported as a mapping failure.
     *
     * @param rawJson       raw JSON value
     * @param type          object class
     * @param objectMapper  custom object mapper
     * @return a list of instances of the provided class
     * @throws ResourceMappingException
     */
    private List<T> mapJsonToInstance(DBObject rawJson, Class<T> type, ObjectMapper objectMapper)
        throws ResourceMappingException
    {
        List<T> list = new ArrayList<>();
        ObjectMapper mapper = objectMapper;
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<DBObject> dbObjects;

        try {
            dbObjects = Transformer.convertToDbObjectList(rawJson);
        } catch (Exception e) {
            throw new ResourceMappingException(e.getMessage());
        }

        for (DBObject dbObject: dbObjects)
        {
            try {
                // convert DBObject to a proper instance of the given class type
                list.add(mapper.convertValue(dbObject, type));
            } catch (Exception e) {
                // skip only the entity we cannot map, the rest of the batch is still valid
                LOG.warn("Failed to map " + type.getSimpleName() + " entity, skipping it: " +
                    e.getLocalizedMessage());
            }
        }

        return list;
    }
}
