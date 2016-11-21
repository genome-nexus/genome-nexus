package org.cbioportal.genome_nexus.annotation.domain.internal;

import org.cbioportal.genome_nexus.annotation.domain.IsoformOverrideRepoFactory;
import org.cbioportal.genome_nexus.annotation.domain.IsoformOverrideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Component("defaultIsoformOverrideRepoFactory")
public class IsoformOverrideRepoFactoryImpl implements IsoformOverrideRepoFactory
{
    private final String overrideURIs;

    private Map<String, IsoformOverrideRepository> overrideRepositories;

    @Autowired
    public IsoformOverrideRepoFactoryImpl(@Value("${vep.overrides_uris}") String overrideURIs)
    {
        this.overrideURIs = overrideURIs;

        Map<String, String> resources = parseOverrideURIsString(overrideURIs);
        this.overrideRepositories = new HashMap<>();

        // Create a repository for each resource URI
        for (String key: resources.keySet())
        {
            this.overrideRepositories.put(key, new IsoformOverrideRepositoryImpl(resources.get(key)));
        }
    }

    /**
     * Parses the isoform override URIs string.
     *
     * @param overrideURIs  override URI list as a string
     * @return  map
     */
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

    @Override
    public IsoformOverrideRepository getRepository(String id)
    {
        return getOverrideRepositories().get(id);
    }

    @Override
    public List<String> getOverrideSources()
    {
        if (overrideRepositories != null)
        {
            return new ArrayList<>(overrideRepositories.keySet());
        }

        return Collections.emptyList();
    }

    public Map<String, IsoformOverrideRepository> getOverrideRepositories()
    {
        return overrideRepositories;
    }

    public String getOverrideURIs()
    {
        return overrideURIs;
    }
}
