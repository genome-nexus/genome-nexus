package org.cbioportal.genome_nexus.service.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.cbioportal.genome_nexus.model.*;

import org.cbioportal.genome_nexus.model.my_variant_info_model.*;

import org.cbioportal.genome_nexus.service.mixin.*;

import org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin.*;

import org.springframework.stereotype.Component;

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
        mixinMap.put(Dbsnp.class, DbsnpMixin.class);
        mixinMap.put(Gene.class, GeneMixin.class);
        mixinMap.put(Alleles.class, AllelesMixin.class);
        mixinMap.put(Hg19.class, Hg19Mixin.class);
        mixinMap.put(Cosmic.class, CosmicMixin.class);
        mixinMap.put(ClinVar.class, ClinVarMixin.class);
        mixinMap.put(Hg38.class, Hg38Mixin.class);
        mixinMap.put(Mutdb.class, MutdbMixin.class);
        super.setMixIns(mixinMap);
    }
}
