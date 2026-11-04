package org.cbioportal.genome_nexus.service.cached;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.config.ExternalResourceObjectMapper;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.util.Transformer;
import org.junit.Test;

public class CachedMyVariantInfoFetcherTest
{
    private final CachedMyVariantInfoFetcher fetcher =
        new CachedMyVariantInfoFetcher(null, null, null, 200);

    private final ExternalResourceTransformer<MyVariantInfo> transformer =
        new ExternalResourceTransformer<>(new ExternalResourceObjectMapper());

    /**
     * Everything normalizeResponse keeps is also written to the cache, so anything it keeps
     * has to be mappable by the transformer. If the check in normalizeResponse is more
     * permissive than the transformer, entities land in mongo that we cannot read back.
     */
    @Test
    public void everyKeptEntityCanBeMappedByTheTransformer() throws ResourceMappingException
    {
        BasicDBList rawJson = new BasicDBList();
        rawJson.addAll(Arrays.asList(
            entityWithClinvarProtein("chr1:g.71873145T>G", "NP_776169.2:p.Asn350Thr"),
            entityWithClinvarProtein("chr1:g.71873156G>T", Arrays.asList("NP_000000.1:p.Ala1Gly")),
            unmappableEntity("chr1:g.71873160A>C")
        ));

        DBObject curated = fetcher.normalizeResponse(rawJson);
        List<DBObject> kept = Transformer.convertToDbObjectList(curated);

        // the single valued clinvar.hgvs.protein is normalized and kept, the entity whose
        // shape we cannot normalize is dropped before it reaches the cache
        assertEquals(2, kept.size());
        assertEquals(kept.size(), transformer.transform(curated, MyVariantInfo.class).size());
    }

    @Test
    public void normalizesSingleValuedClinvarHgvsFields() throws ResourceMappingException
    {
        BasicDBList rawJson = new BasicDBList();
        rawJson.add(entityWithClinvarProtein("chr1:g.71873145T>G", "NP_776169.2:p.Asn350Thr"));

        List<MyVariantInfo> mapped =
            transformer.transform(fetcher.normalizeResponse(rawJson), MyVariantInfo.class);

        assertEquals(1, mapped.size());
        assertEquals(Arrays.asList("NP_776169.2:p.Asn350Thr"),
            mapped.get(0).getClinVar().getHgvs().getProtein());
    }

    /**
     * The check in normalizeResponse decides what gets cached, the transformer decides what
     * we can read back. ACCEPT_SINGLE_VALUE_AS_ARRAY cannot be enabled on the shared mapper
     * (see ExternalResourceObjectMapper), so enabling it only on the check would let entities
     * through that the transformer then has to skip.
     */
    @Test
    public void checkIsNotMorePermissiveThanTheTransformer() throws Exception
    {
        Field field = CachedMyVariantInfoFetcher.class.getDeclaredField("objectMapper");
        field.setAccessible(true);
        ObjectMapper checkMapper = (ObjectMapper) field.get(fetcher);

        assertFalse(checkMapper.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        assertFalse(new ExternalResourceObjectMapper()
            .isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
    }

    private BasicDBObject entityWithClinvarProtein(String id, Object protein)
    {
        BasicDBObject hgvs = new BasicDBObject();
        hgvs.put("protein", protein);

        BasicDBObject clinvar = new BasicDBObject();
        clinvar.put("hgvs", hgvs);

        BasicDBObject entity = new BasicDBObject();
        entity.put("_id", id);
        entity.put("clinvar", clinvar);

        return entity;
    }

    /** clinvar.allele_id is an Integer on the model, a nested object can never map onto it. */
    private BasicDBObject unmappableEntity(String id)
    {
        BasicDBObject entity = entityWithClinvarProtein(id, Arrays.asList("NP_000000.1:p.Ala1Gly"));
        ((BasicDBObject) entity.get("clinvar")).put("allele_id", new BasicDBObject("nested", 1));

        return entity;
    }
}
