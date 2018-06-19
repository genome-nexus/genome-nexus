package org.cbioportal.genome_nexus.web.mock;

import org.cbioportal.genome_nexus.model.Hotspot;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class JsonToObjectMapper
{
    private final TestResourceObjectMapper objectMapper;

    public JsonToObjectMapper()
    {
        this.objectMapper = new TestResourceObjectMapper();
    }

    public List<Hotspot> readHotspots(String resourceName) throws IOException
    {
        return this.objectMapper.readValue(
            new ClassPathResource("hotspot/" + resourceName).getInputStream(),
            this.objectMapper.getTypeFactory().constructCollectionType(List.class, Hotspot.class)
        );
    }
}
