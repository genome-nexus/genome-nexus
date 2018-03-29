package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.persistence.IsoformOverrideRepoFactory;
import org.cbioportal.genome_nexus.persistence.IsoformOverrideRepository;
import org.cbioportal.genome_nexus.service.IsoformOverrideService;
import org.cbioportal.genome_nexus.service.exception.IsoformOverrideNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class IsoformOverrideServiceImpl implements IsoformOverrideService
{
    private final IsoformOverrideRepoFactory repoFactory;

    @Autowired
    public IsoformOverrideServiceImpl(IsoformOverrideRepoFactory repoFactory)
    {
        this.repoFactory = repoFactory;
    }

    public IsoformOverride getIsoformOverride(String source, String id) throws IsoformOverrideNotFoundException
    {
        IsoformOverrideRepository repository = this.repoFactory.getRepository(source);
        IsoformOverride override = null;

        if (repository != null)
        {
            override = repository.findIsoformOverride(id);
        }

        if (override != null)
        {
            return override;
        }
        else
        {
            throw new IsoformOverrideNotFoundException(source, id);
        }
    }

    public List<IsoformOverride> getIsoformOverrides(String source) throws IsoformOverrideNotFoundException
    {
        IsoformOverrideRepository repository = this.repoFactory.getRepository(source);

        if (repository != null)
        {
            return repository.findAllAsList();
        }
        else
        {
            throw new IsoformOverrideNotFoundException(source);
        }
    }

    public List<IsoformOverride> getIsoformOverrides(String source, List<String> transcriptIds)
    {
        List<IsoformOverride> isoformOverrides = new ArrayList<>();

        for (String id: transcriptIds)
        {
            try {
                isoformOverrides.add(getIsoformOverride(source, id));
            } catch (IsoformOverrideNotFoundException e) {
                // fail silently for this override source
            }
        }

        return isoformOverrides;
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
