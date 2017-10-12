package org.cbioportal.genome_nexus.annotation.service.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    public <T> List<T> transform(String jsonString, Class<T> type) throws IOException
    {
        return Transformer.mapJsonToInstance(jsonString, type, this.externalResourceObjectMapper);
    }
}
