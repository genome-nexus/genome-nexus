package org.cbioportal.genome_nexus.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.web.mixin.*;
import org.cbioportal.genome_nexus.model.*;

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
        mixinMap.put(PdbHeader.class, PdbHeaderMixin.class);
        mixinMap.put(TranscriptConsequence.class, TranscriptConsequenceMixin.class);
        mixinMap.put(ColocatedVariant.class, ColocatedVariantMixin.class);
        mixinMap.put(VariantAnnotation.class, VariantAnnotationMixin.class);
        mixinMap.put(EnsemblTranscript.class, EnsemblTranscriptMixin.class);
        mixinMap.put(EnsemblGene.class, EnsemblGeneMixin.class);
        mixinMap.put(PfamDomainRange.class, PfamDomainRangeMixin.class);
        mixinMap.put(ExonRange.class, ExonRangeMixin.class);
        mixinMap.put(GenomicLocation.class, GenomicLocationMixin.class);
        mixinMap.put(AggregatedHotspots.class, AggregatedHotspotsMixin.class);

        super.setMixIns(mixinMap);
    }
}
