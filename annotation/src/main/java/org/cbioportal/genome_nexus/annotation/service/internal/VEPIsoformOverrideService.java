package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.IsoformOverride;
import org.cbioportal.genome_nexus.annotation.domain.IsoformOverrideRepoFactory;
import org.cbioportal.genome_nexus.annotation.domain.IsoformOverrideRepository;
import org.cbioportal.genome_nexus.annotation.service.IsoformOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class VEPIsoformOverrideService implements IsoformOverrideService
{
    private final IsoformOverrideRepoFactory repoFactory;

    @Autowired
    public VEPIsoformOverrideService(
        @Qualifier("defaultIsoformOverrideRepoFactory") IsoformOverrideRepoFactory repoFactory)
    {
        this.repoFactory = repoFactory;
    }

    public IsoformOverride getIsoformOverride(String source, String id)
    {
        IsoformOverrideRepository repository = this.repoFactory.getRepository(source);

        if (repository != null)
        {
            return repository.findIsoformOverride(id);
        }
        else
        {
            return null;
        }
    }

    public List<IsoformOverride> getIsoformOverrides(String source)
    {
        IsoformOverrideRepository repository = this.repoFactory.getRepository(source);

        if (repository != null)
        {
            return repository.findAllAsList();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public List<String> getOverrideSources()
    {
        return repoFactory.getOverrideSources();
    }

    @Override
    public Boolean hasData(String source)
    {
        return this.repoFactory.getRepository(source) != null;
    }
}
