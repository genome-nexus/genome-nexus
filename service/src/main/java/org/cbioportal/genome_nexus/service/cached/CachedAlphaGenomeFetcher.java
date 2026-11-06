package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.persistence.AlphaGenomeRepository;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.remote.AlphaGenomeDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.util.AlphaGenomePipelineDebugLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** One variant per call; concurrent pool backed by cache */
@Component
public class CachedAlphaGenomeFetcher extends BaseCachedExternalResourceFetcher<AlphaGenome, AlphaGenomeRepository>
{
    private static final Log LOG = LogFactory.getLog(CachedAlphaGenomeFetcher.class);

    public static final String COLLECTION = "alphagenome.annotation";

    private final ExecutorService fetchPool;
    private final ThreadLocal<Map<String, Map<String, Object>>> pendingBodies =
        new ThreadLocal<>();

    @Autowired
    public CachedAlphaGenomeFetcher(
        ExternalResourceTransformer<AlphaGenome> transformer,
        AlphaGenomeRepository repository,
        AlphaGenomeDataFetcher fetcher,
        @Value("${alphagenome.sidecar.max_page_size:1}") Integer maxPageSize,
        @Value("${alphagenome.sidecar.max_concurrency:16}") Integer maxConcurrency
    ) {
        super(COLLECTION, repository, AlphaGenome.class, fetcher, transformer, maxPageSize);
        this.fetchPool = Executors.newFixedThreadPool(
            Math.max(1, maxConcurrency),
            runnable -> {
                Thread thread = new Thread(runnable, "alphagenome-sidecar-fetch");
                thread.setDaemon(true);
                return thread;
            });
    }

    @PreDestroy
    public void shutdown() {
        if (fetchPool != null && !fetchPool.isShutdown()) {
            fetchPool.shutdown();
        }
    }

    /**
     * Fetches the given variants, using the staged sidecar request body for
     * each cache miss. The key of each entry is the Genome Nexus variant id
     * (also the MongoDB {@code _id}); the value is the {@code POST /score}
     * JSON body built from the normalized GRCh38 variant and canonical gene
     */
    public List<AlphaGenome> fetchAndCache(Map<String, Map<String, Object>> requestBodies)
        throws ResourceMappingException
    {
        pendingBodies.set(new LinkedHashMap<>(requestBodies));
        try {
            return fetchAndCache(new ArrayList<>(requestBodies.keySet()));
        }
        finally {
            pendingBodies.remove();
        }
    }

    @Override
    protected Object buildRequestBody(Set<String> ids)
    {
        Map<String, Map<String, Object>> bodies = pendingBodies.get();
        if (bodies == null) {
            LOG.warn("No staged sidecar request bodies for ids: " + ids);
            return new LinkedHashMap<String, Object>();
        }
        if (ids.size() == 1) {
            Map<String, Object> body = bodies.get(ids.iterator().next());
            if (body != null) {
                return body;
            }
        }
        LOG.warn("No staged sidecar request body for ids: " + ids);
        return new LinkedHashMap<String, Object>();
    }

    @Override
    protected String extractId(AlphaGenome instance)
    {
        return instance.getVariant();
    }

    @Override
    protected String extractId(DBObject dbObject)
    {
        Object variant = dbObject.get("variant");
        return variant == null ? (String) dbObject.get("_id") : variant.toString();
    }

    /** Variants are fetched concurrently and written sequentially */
    @Override
    protected void fetchAndCache(Set<String> needToFetch,
                                 Map<String, AlphaGenome> idToInstance,
                                 boolean saveValues) throws ResourceMappingException
    {
        List<Set<String>> chunks = new ArrayList<>(this.generateChunks(needToFetch));
        AlphaGenomePipelineDebugLogger.log("PHASE 3: CACHE CHECK",
            String.format("needToFetch uncached variants: %s (split into %d chunks)", needToFetch, chunks.size()));

        List<Future<DBObject>> futures = new ArrayList<>();
        for (Set<String> chunk : chunks) {
            final Set<String> ids = new LinkedHashSet<>(chunk);
            final Map<String, Map<String, Object>> bodies =
                pendingBodies.get() == null
                    ? new LinkedHashMap<>()
                    : new LinkedHashMap<>(pendingBodies.get());
            AlphaGenomePipelineDebugLogger.log("PHASE 3: DISPATCH TO SIDECAR",
                String.format("Submitting chunk fetch task for ids=%s, body=%s", ids, bodies.get(ids.iterator().next())));
            Callable<DBObject> task = () -> {
                pendingBodies.set(bodies);
                try {
                    return this.fetcher.fetchRawValue(this.buildRequestBody(ids));
                }
                finally {
                    pendingBodies.remove();
                }
            };
            futures.add(fetchPool.submit(task));
        }

        for (int i = 0; i < chunks.size(); i++) {
            DBObject rawValue = null;
            try {
                rawValue = futures.get(i).get();
            } catch (Exception e) {
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                    String.format("AlphaGenome sidecar fetch failed for %s: %s", chunks.get(i), e.getLocalizedMessage()));
                LOG.error("AlphaGenome sidecar fetch failed for " + chunks.get(i) + ": "
                    + e.getLocalizedMessage());
            }
            if (rawValue == null) {
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                    String.format("AlphaGenome rawValue is null for chunk %s", chunks.get(i)));
                continue;
            }
            try {
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE FROM ALPHAGENOME SIDECAR",
                    String.format("Raw response for %s: %s", chunks.get(i), rawValue.toString()));
                rawValue = this.normalizeResponse(rawValue);
                List<AlphaGenome> fetched = this.transformer.transform(rawValue, this.type);
                fetched.forEach(t -> idToInstance.put(this.extractId(t), t));
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE TRANSFORMED",
                    String.format("Transformed %d AlphaGenome instances for chunk %s", fetched.size(), chunks.get(i)));
                if (saveValues) {
                    AlphaGenomePipelineDebugLogger.log("PHASE 5: CACHE WRITE",
                        String.format("Saving %s to MongoDB collection '%s'", chunks.get(i), COLLECTION));
                    this.saveToDb(rawValue);
                }
            } catch (Exception e) {
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                    String.format("AlphaGenome response handling failed for %s: %s", chunks.get(i), e.getLocalizedMessage()));
                LOG.error("AlphaGenome sidecar response handling failed for " + chunks.get(i)
                    + ": " + e.getLocalizedMessage());
            }
        }
    }
}
