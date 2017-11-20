package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.persistence.IsoformOverrideRepoFactory;
import org.cbioportal.genome_nexus.persistence.IsoformOverrideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Component
public class IsoformOverrideRepoFactoryImpl implements IsoformOverrideRepoFactory
{
    // comma separated list of isoform overrides
    private final String isoformOverrides;

    private Map<String, IsoformOverrideRepository> overrideRepositories;

    @Autowired
    public IsoformOverrideRepoFactoryImpl(@Value("${vep.isoform.overrides}") String isoformOverrides)
    {
        this.isoformOverrides = isoformOverrides;

        Map<String, String> resources = parseIsoformOverrides(isoformOverrides);
        this.overrideRepositories = new HashMap<>();

        // Create a repository for each override resource
        for (String key: resources.keySet())
        {
            this.overrideRepositories.put(key, new IsoformOverrideRepositoryImpl(resources.get(key)));
        }
    }

    /**
     * Parses the isoform overrides string.
     *
     * @param isoformOverrides  comma separated list of isoform overrides
     * @return  map
     */
    private Map<String, String> parseIsoformOverrides(String isoformOverrides)
    {
        Map<String, String> overrideResources = new HashMap<>();

        // list is comma separated
        for (String pair: isoformOverrides.split(","))
        {
            // key-value pairs are semicolon separated
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

    public String getIsoformOverrides()
    {
        return isoformOverrides;
    }
}
