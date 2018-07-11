package org.cbioportal.genome_nexus.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.mixin.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("defaultExternalResourceObjectMapper")
public class ExternalResourceObjectMapper extends ObjectMapper
{
    public ExternalResourceObjectMapper()
    {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();

        mixinMap.put(GeneXref.class, GeneXrefMixin.class);
        mixinMap.put(MutationAssessor.class, MutationAssessorMixin.class);
        mixinMap.put(TranscriptConsequence.class, TranscriptConsequenceMixin.class);
        mixinMap.put(VariantAnnotation.class, VariantAnnotationMixin.class);
        mixinMap.put(MyVariantInfo.class, MyVariantInfoMixin.class);
        mixinMap.put(Snpeff.class, SnpeffMixin.class);
        mixinMap.put(Ann.class, AnnMixin.class);
        mixinMap.put(Vcf.class, VcfMixin.class);
        

        super.setMixIns(mixinMap);
    }
}
