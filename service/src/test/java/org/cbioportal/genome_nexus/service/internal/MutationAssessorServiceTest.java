package org.cbioportal.genome_nexus.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.cbioportal.genome_nexus.component.annotation.ProteinChangeResolver;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.MutationAssessorRepository;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.mock.MutationAssessorMockData;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MutationAssessorServiceTest
{
    @Spy
    @InjectMocks
    private MutationAssessorServiceImpl service;
    @Mock
    private ProteinChangeResolver proteinChangeResolver;
    @Mock
    private EnsemblService ensemblService;
    @Mock
    MutationAssessorRepository mutationAssessorRepository;

    private MutationAssessorMockData mutationAssessorMockData = new MutationAssessorMockData();
    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void getMutationAssessorByMutationAssessorVariant()
        throws ResourceMappingException, MutationAssessorNotFoundException
    {
        Map<String, MutationAssessor> mockData = this.mutationAssessorMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(mutationAssessorRepository.findById("P15056,p.V600E")).thenReturn(Optional.of(mockData.get("P15056,p.V600E")));
        Mockito.when(mutationAssessorRepository.findById("P01116,p.G12C")).thenReturn(Optional.of(mockData.get("P01116,p.G12C")));
        Mockito.when(mutationAssessorRepository.findById("INVALID")).thenReturn(Optional.empty());

        VariantAnnotation variantAnnotation1 = new VariantAnnotation();
        variantAnnotation1.setVariant("7:g.140453136A>T");
        MutationAssessor mutationAssessor1 = service.getMutationAssessorByMutationAssessorVariant("P15056,p.V600E", variantAnnotation1);
        assertEquals(mutationAssessor1.getFunctionalImpactPrediction(), mockData.get("P15056,p.V600E").getFunctionalImpactPrediction());

        VariantAnnotation variantAnnotation2 = new VariantAnnotation();
        variantAnnotation2.setVariant("12:g.25398285C>A");
        MutationAssessor mutationAssessor2 = service.getMutationAssessorByMutationAssessorVariant("P01116,p.G12C", variantAnnotation2);
        assertEquals(mutationAssessor2.getFunctionalImpactPrediction(), mockData.get("P01116,p.G12C").getFunctionalImpactPrediction());
        
        VariantAnnotation invalidVariantAnnotation = new VariantAnnotation();
        invalidVariantAnnotation.setVariant("INVALID");
        
        MutationAssessorNotFoundException exception = assertThrows(MutationAssessorNotFoundException.class, () -> {
            service.getMutationAssessorByMutationAssessorVariant("INVALID", invalidVariantAnnotation);
        });
        assertEquals("Mutation Assessor not found for variant: INVALID", exception.getMessage());     
    }

    @Test
    public void getMutationAssessorByVariantAnnotation()
        throws ResourceMappingException, MutationAssessorNotFoundException,
        IOException
    {
        Map<String, MutationAssessor> maMockData = this.mutationAssessorMockData.generateData();
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(proteinChangeResolver.resolveHgvspShort(variantMockData.get("7:g.140453136A>T"))).thenReturn("p.V600E");
        Mockito.when(ensemblService.getUniprotId("ENST00000288602")).thenReturn("P15056");
        Mockito.when(mutationAssessorRepository.findById("P15056,p.V600E")).thenReturn(Optional.of(maMockData.get("P15056,p.V600E")));
        MutationAssessor mutationAssessor1 = service.getMutationAssessor(variantMockData.get("7:g.140453136A>T"));
        assertEquals(mutationAssessor1.getFunctionalImpactScore(), maMockData.get("P15056,p.V600E").getFunctionalImpactScore());

        Mockito.when(proteinChangeResolver.resolveHgvspShort(variantMockData.get("12:g.25398285C>A"))).thenReturn("p.G12C");
        Mockito.when(ensemblService.getUniprotId("ENST00000256078")).thenReturn("P01116");
        Mockito.when(mutationAssessorRepository.findById("P01116,p.G12C")).thenReturn(Optional.of(maMockData.get("P01116,p.G12C")));
        MutationAssessor mutationAssessor2 = service.getMutationAssessor(variantMockData.get("12:g.25398285C>A"));
        assertEquals(mutationAssessor2.getFunctionalImpactScore(), maMockData.get("P01116,p.G12C").getFunctionalImpactScore());
    }
}