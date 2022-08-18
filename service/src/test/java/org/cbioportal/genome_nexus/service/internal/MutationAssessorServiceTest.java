package org.cbioportal.genome_nexus.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.MutationAssessorRepository;
import org.cbioportal.genome_nexus.service.cached.CachedMutationAssessorFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
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
    private CachedMutationAssessorFetcher fetcher;

    @Mock
    MutationAssessorRepository mutationAssessorRepository;

    private MutationAssessorMockData mutationAssessorMockData = new MutationAssessorMockData();
    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void getMutationAssessorByMutationAssessorVariant()
        throws ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        Map<String, MutationAssessor> mockData = this.mutationAssessorMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(mutationAssessorRepository.findById("17,7578479,G,A")).thenReturn(Optional.of(mockData.get("17,7578479,G,A")));
        Mockito.when(mutationAssessorRepository.findById("17,7578416,C,A")).thenReturn(Optional.of(mockData.get("17,7578416,C,A")));
        Mockito.when(mutationAssessorRepository.findById("INVALID")).thenReturn(Optional.empty());

        MutationAssessor mutationAssessor1 = service.getMutationAssessorByMutationAssessorVariant("17,7578479,G,A");
        assertEquals(mutationAssessor1.getHugoSymbol(), mockData.get("17,7578479,G,A").getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessorByMutationAssessorVariant("17,7578416,C,A");
        assertEquals(mutationAssessor2.getHugoSymbol(), mockData.get("17,7578416,C,A").getHugoSymbol());
        
		Exception exception = assertThrows(Exception.class, () -> {
            service.getMutationAssessorByMutationAssessorVariant("INVALID");
        });
        assertEquals("Mutation Assessor annotation not found for variant: INVALID", exception.getMessage());        
    }

    @Test
    public void getMutationAssessorByVariantAnnotation()
        throws ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException,
        IOException
    {
        Map<String, MutationAssessor> maMockData = this.mutationAssessorMockData.generateData();
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        doReturn(maMockData.get("7,140453136,A,T")).when(service).getMutationAssessorByMutationAssessorVariant("7,140453136,A,T");
        doReturn(maMockData.get("12,25398285,C,A")).when(service).getMutationAssessorByMutationAssessorVariant("12,25398285,C,A");

        MutationAssessor mutationAssessor1 = service.getMutationAssessor(variantMockData.get("7:g.140453136A>T"));
        assertEquals(mutationAssessor1.getHugoSymbol(), maMockData.get("7,140453136,A,T").getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessor(variantMockData.get("12:g.25398285C>A"));
        assertEquals(mutationAssessor2.getHugoSymbol(), maMockData.get("12,25398285,C,A").getHugoSymbol());
    }
}
