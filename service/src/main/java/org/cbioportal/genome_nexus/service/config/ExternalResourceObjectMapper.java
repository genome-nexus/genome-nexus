package org.cbioportal.genome_nexus.service.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.cbioportal.genome_nexus.model.Alleles;
import org.cbioportal.genome_nexus.model.Ann;
import org.cbioportal.genome_nexus.model.ClinVar;
import org.cbioportal.genome_nexus.model.Cosmic;
import org.cbioportal.genome_nexus.model.Dbsnp;
import org.cbioportal.genome_nexus.model.Gene;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.Hg19;
import org.cbioportal.genome_nexus.model.Hg38;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.Snpeff;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.model.Vcf;
import org.cbioportal.genome_nexus.service.mixin.AllelesMixin;
import org.cbioportal.genome_nexus.service.mixin.AnnMixin;
import org.cbioportal.genome_nexus.service.mixin.ClinVarMixin;
import org.cbioportal.genome_nexus.service.mixin.CosmicMixin;
import org.cbioportal.genome_nexus.service.mixin.DbsnpMixin;
import org.cbioportal.genome_nexus.service.mixin.GeneMixin;
import org.cbioportal.genome_nexus.service.mixin.GeneXrefMixin;
import org.cbioportal.genome_nexus.service.mixin.Hg19Mixin;
import org.cbioportal.genome_nexus.service.mixin.Hg38Mixin;
import org.cbioportal.genome_nexus.service.mixin.MutationAssessorMixin;
import org.cbioportal.genome_nexus.service.mixin.MyVariantInfoMixin;
import org.cbioportal.genome_nexus.service.mixin.SnpeffMixin;
import org.cbioportal.genome_nexus.service.mixin.TranscriptConsequenceMixin;
import org.cbioportal.genome_nexus.service.mixin.VariantAnnotationMixin;
import org.cbioportal.genome_nexus.service.mixin.VcfMixin;
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
        super.setMixIns(mixinMap);
    }
}
