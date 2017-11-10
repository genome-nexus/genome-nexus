package org.cbioportal.genome_nexus.service.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.cbioportal.genome_nexus.service.exception.JsonMappingException;
import org.cbioportal.genome_nexus.util.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExternalResourceTransformer
{
    private final ObjectMapper externalResourceObjectMapper;

    @Autowired
    public ExternalResourceTransformer(
        @Qualifier("defaultExternalResourceObjectMapper") ObjectMapper externalResourceObjectMapper)
    {
        this.externalResourceObjectMapper = externalResourceObjectMapper;
    }

    public <T> List<T> transform(String jsonString, Class<T> type) throws JsonMappingException
    {
        return this.mapJsonToInstance(jsonString, type, this.externalResourceObjectMapper);
    }

    public <T> List<T> mapJsonToInstance(String jsonString, Class<T> type) throws JsonMappingException
    {
        return this.mapJsonToInstance(jsonString, type, null);
    }

    /**
     * Maps the given raw JSON string onto the provided class instance.
     *
     * @param jsonString    raw JSON string
     * @param type          object class
     * @param objectMapper  custom object mapper
     * @return a list of instances of the provided class
     * @throws JsonMappingException
     */
    public <T> List<T> mapJsonToInstance(String jsonString, Class<T> type, ObjectMapper objectMapper)
        throws JsonMappingException
    {
        List<T> list = new ArrayList<>();
        ObjectMapper mapper = objectMapper;

        if (mapper == null)
        {
            mapper = new ObjectMapper();
        }

        try {
            for (DBObject dbObject: Transformer.convertToDbObject(jsonString))
            {
                String toMap = JSON.serialize(dbObject);

                // map json string onto the given class type
                list.add(mapper.readValue(toMap, type));
            }
        } catch (Exception e) {
            throw new JsonMappingException(e.getMessage());
        }

        return list;
    }
}
