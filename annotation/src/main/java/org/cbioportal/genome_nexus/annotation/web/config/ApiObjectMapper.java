package org.cbioportal.genome_nexus.annotation.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.annotation.domain.*;
import org.cbioportal.genome_nexus.annotation.web.mixin.*;

import java.util.HashMap;
import java.util.Map;


public class ApiObjectMapper extends ObjectMapper
{
    public ApiObjectMapper()
    {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();

        mixinMap.put(GeneXref.class, GeneXrefMixin.class);
        mixinMap.put(Hotspot.class, HotspotMixin.class);
        mixinMap.put(IsoformOverride.class, IsoformOverrideMixin.class);
        mixinMap.put(MutationAssessor.class, MutationAssessorMixin.class);
        mixinMap.put(PfamDomain.class, PfamDomainMixin.class);
        mixinMap.put(TranscriptConsequence.class, TranscriptConsequenceMixin.class);
        mixinMap.put(VariantAnnotation.class, VariantAnnotationMixin.class);

        super.setMixIns(mixinMap);
    }
}
