package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.IsoformOverride;
import org.cbioportal.genome_nexus.annotation.service.IsoformOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class VEPIsoformOverrideService implements IsoformOverrideService
{
    private String overrideURIs;

    private Map<String, IsoformOverrideResourceManager> overrideResources;

    @Autowired
    public VEPIsoformOverrideService(@Value("${vep.overrides_uris}") String overrideURIs)
    {
        this.overrideURIs = overrideURIs;

        Map<String, String> resources = parseOverrideURIsString(overrideURIs);
        this.overrideResources = new HashMap<>();

        for (String key: resources.keySet())
        {
            this.overrideResources.put(key, new IsoformOverrideResourceManager(resources.get(key)));
        }
    }

    private Map<String, String> parseOverrideURIsString(String overrideURIs)
    {
        Map<String, String> overrideResources = new HashMap<>();

        for (String pair: overrideURIs.split(","))
        {
            String parts[] = pair.split(":");

            if (parts.length > 1)
            {
                String key = parts[0];
                String value = parts[1];

                overrideResources.put(key, value);
            }
        }

        return overrideResources;
    }

    public IsoformOverride getIsoformOverride(String source, String id)
    {
        IsoformOverrideResourceManager manager = this.overrideResources.get(source);

        if (manager != null)
        {
            return manager.getOverride(id);
        }
        else
        {
            return null;
        }
    }

    public String getOverrideURIs()
    {
        return overrideURIs;
    }

    public Map<String, IsoformOverrideResourceManager> getOverrideResources()
    {
        return overrideResources;
    }
}
