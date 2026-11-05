package org.cbioportal.genome_nexus.service.transformer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.config.ExternalResourceObjectMapper;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.junit.Test;

public class ExternalResourceTransformerTest
{
    private final ExternalResourceTransformer<MyVariantInfo> transformer =
        new ExternalResourceTransformer<>(new ExternalResourceObjectMapper());

    /**
     * An entity that cannot be mapped onto the model must not discard the rest of the batch.
     * MyVariantInfo returns clinvar.hgvs.protein as a bare string when a variant has a single
     * protein change, which does not fit the List<String> model field. Before this was handled
     * per entity, a single such variant wiped out my_variant_info for the entire request.
     */
    @Test
    public void unmappableEntityDoesNotDiscardTheBatch() throws ResourceMappingException
    {
        BasicDBList rawJson = new BasicDBList();
        rawJson.addAll(Arrays.asList(
            validEntity("chr1:g.71873144A>T"),
            entityWithSingleValuedClinvarProtein("chr1:g.71873145T>G"),
            validEntity("chr1:g.71873156G>T")
        ));

        List<MyVariantInfo> mapped = transformer.transform(rawJson, MyVariantInfo.class);

        assertEquals(2, mapped.size());
        assertEquals("chr1:g.71873144A>T", mapped.get(0).getVariant());
        assertEquals("chr1:g.71873156G>T", mapped.get(1).getVariant());
    }

    @Test
    public void mapsEveryEntityWhenAllAreValid() throws ResourceMappingException
    {
        BasicDBList rawJson = new BasicDBList();
        rawJson.addAll(Arrays.asList(validEntity("chr1:g.1A>T"), validEntity("chr1:g.2A>T")));

        assertEquals(2, transformer.transform(rawJson, MyVariantInfo.class).size());
    }

    private BasicDBObject validEntity(String id)
    {
        BasicDBObject hgvs = new BasicDBObject();
        hgvs.put("protein", Arrays.asList("NP_776169.2:p.Asn350Thr"));

        BasicDBObject clinvar = new BasicDBObject();
        clinvar.put("hgvs", hgvs);

        BasicDBObject entity = new BasicDBObject();
        entity.put("_id", id);
        entity.put("clinvar", clinvar);

        return entity;
    }

    private BasicDBObject entityWithSingleValuedClinvarProtein(String id)
    {
        BasicDBObject entity = validEntity(id);
        ((BasicDBObject) ((BasicDBObject) entity.get("clinvar")).get("hgvs"))
            .put("protein", "NP_776169.2:p.Asn350Thr");

        return entity;
    }
}
