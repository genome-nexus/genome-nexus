package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.persistence.internal.VariantAnnotationRepositoryImpl;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.service.remote.VEPDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CachedVariantAnnotationFetcher extends BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository>
{
    public static final String FIXED_ENTRY = "AGT:c.803T>C";

    @Autowired
    public CachedVariantAnnotationFetcher(ExternalResourceTransformer<VariantAnnotation> transformer,
                                          VariantAnnotationRepository repository,
                                          VEPDataFetcher fetcher,
                                          @Value("${vep.max_page_size:300}") Integer maxPageSize)
    {
        super(VariantAnnotationRepositoryImpl.COLLECTION,
            repository,
            VariantAnnotation.class,
            fetcher,
            transformer,
            maxPageSize);
    }

    @Override
    protected String extractId(VariantAnnotation instance)
    {
        return instance.getVariantId();
    }

    @Override
    protected String extractId(DBObject dbObject)
    {
        return (String)dbObject.get("input");
    }

    public List<VariantAnnotation> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        ArrayList<String> variantsWithFixedFirstEntry = new ArrayList<>();

        // we need to add a fixed first entry due to a bug in VEP web service,
        // basically, the query fails if the first entry in the list is invalid:
        // https://github.com/Ensembl/ensembl-vep/issues/156
        variantsWithFixedFirstEntry.add(FIXED_ENTRY);
        variantsWithFixedFirstEntry.addAll(ids);

        List<VariantAnnotation> annotations = super.fetchAndCache(variantsWithFixedFirstEntry);

        // very unlikely but in case the fixed entry is already in the original list we don't want to remove it
        if (!ids.contains(FIXED_ENTRY)) {
            // filter out the fixed entry, so that it won't be included in the final list
            annotations = annotations.stream().filter(
                v -> !v.getVariant().equals(FIXED_ENTRY)).collect(Collectors.toList());
        }

        return annotations;
    }

    @Override
    protected Object buildRequestBody(Set<String> ids)
    {
        HashMap<String, Object> requestBody = new HashMap<>();

        requestBody.put("hgvs_notations", ids);

        return requestBody;
    }

    protected void saveToDb(String value)
    {
        List<DBObject> dbObjects = this.transformer.transform(value);

        for (DBObject dbObject: dbObjects) {
            dbObject.put("_id", this.extractId(dbObject));
        }

        // never cache the fixed entry: filter out corresponding object
        dbObjects = dbObjects.stream().filter(
            o -> !this.extractId(o).equals(FIXED_ENTRY)).collect(Collectors.toList());

        this.repository.saveDBObjects(this.collection, dbObjects);
    }
}
