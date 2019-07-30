package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.persistence.internal.VariantAnnotationRepositoryImpl;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.util.NaturalOrderComparator;
import org.cbioportal.genome_nexus.service.remote.VEPDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CachedVariantAnnotationFetcher extends BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository>
{
    public static final String FIXED_ENTRY = "AGT:c.803T>C";

    @Autowired
    public CachedVariantAnnotationFetcher(ExternalResourceTransformer<VariantAnnotation> transformer,
                                          VariantAnnotationRepository repository,
                                          VEPDataFetcher fetcher,
                                          @Value("${vep.max_page_size:200}") Integer maxPageSize)
    {
        super(VariantAnnotationRepositoryImpl.COLLECTION,
            repository,
            VariantAnnotation.class,
            fetcher,
            transformer,
            maxPageSize);
    }

    @Override
    protected Boolean isValidId(String id) {
        return !id.contains("N") && !id.contains("-") && !id.contains("undefined") && !id.contains("g.0") && id.contains(":") && id.split(":")[1].matches(".*\\d+.*");
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

    @Override
    public List<VariantAnnotation> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        List<VariantAnnotation> annotations = super.fetchAndCache(ids);

        // post process to remove the fixed entry (or duplicate fix entries depending on the number of total pages)
        // filter out the fixed entry, so that it won't be included in the final list
        List<VariantAnnotation> postProcessedAnnotations = annotations.stream().filter(
            v -> !v.getVariant().equals(FIXED_ENTRY)).collect(Collectors.toList());

        // very unlikely but in case the fixed entry is already in the original list we want to add it back
        if (ids.contains(FIXED_ENTRY))
        {
            Optional<VariantAnnotation> first = annotations.stream().filter(
                v -> v.getVariant().equals(FIXED_ENTRY)).findFirst();

            // TODO add it back to its correct index not just at the end of the list: doable but not straightforward due to possible missing annotation(s)
            first.ifPresent(postProcessedAnnotations::add);
        }

        return postProcessedAnnotations;
    }

    /**
     * Overriding the base method in order to add a fixed first entry to the query (due to a bug in VEP web service):
     * Basically, the query fails if the first entry in the list is invalid.
     * See https://github.com/Ensembl/ensembl-vep/issues/156 for details.
     */
    @Override
    protected List<Set<String>> generateChunks(Set<String> needToFetch)
    {
        List<Set<String>> chunks = new ArrayList<>();
        List<String> list = new ArrayList<>(needToFetch);

        // -1, because we add fixed entry to the beginning of each and every chunk
        int chunkSize = this.maxPageSize - 1;

        for (int i = 0; i < list.size(); i += chunkSize)
        {
            Set<String> chunk = new LinkedHashSet<>();

            // add the fixed entry to the beginning of the chunk
            chunk.add(FIXED_ENTRY);

            // append the next slice
            // VEP requires ids to be sorted
            List<String> sortedChunk = list.subList(i, Math.min(list.size(), i + chunkSize));
            Collections.sort(sortedChunk, new NaturalOrderComparator());

            chunk.addAll(sortedChunk);

            chunks.add(chunk);
        }

        return chunks;
    }

    @Override
    protected Object buildRequestBody(Set<String> ids)
    {
        HashMap<String, Object> requestBody = new HashMap<>();

        requestBody.put("hgvs_notations", ids);

        return requestBody;
    }

    protected void saveToDb(DBObject value)
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
