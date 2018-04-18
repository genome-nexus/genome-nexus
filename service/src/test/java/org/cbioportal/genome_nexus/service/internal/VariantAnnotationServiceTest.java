package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.IsoformOverrideService;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.cached.CachedVariantAnnotationFetcher;
import org.cbioportal.genome_nexus.service.exception.*;
import org.cbioportal.genome_nexus.service.mock.CancerHotspotMockData;
import org.cbioportal.genome_nexus.service.mock.IsoformOverrideMockData;
import org.cbioportal.genome_nexus.service.mock.MutationAssessorMockData;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VariantAnnotationServiceTest
{
    @InjectMocks
    private VariantAnnotationServiceImpl variantAnnotationService;

    @Mock
    private CachedVariantAnnotationFetcher fetcher;

    @Mock
    private MutationAssessorService mutationAssessorService;

    @Mock
    private IsoformOverrideService isoformOverrideService;

    @Mock
    private CancerHotspotServiceImpl cancerHotspotService;

    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private MutationAssessorMockData mutationAssessorMockData = new MutationAssessorMockData();
    private CancerHotspotMockData cancerHotspotMockData = new CancerHotspotMockData();
    private IsoformOverrideMockData isoformOverrideMockData = new IsoformOverrideMockData();

    @Test
    public void getAnnotationByVariantString()
        throws ResourceMappingException, VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.mockVariantFetcherMethods(variantMockData);

        VariantAnnotation annotation1 = variantAnnotationService.getAnnotation("7:g.140453136A>T");
        assertEquals(variantMockData.get("7:g.140453136A>T").getStart(), annotation1.getStart());
        assertEquals(variantMockData.get("7:g.140453136A>T").getVariant(), annotation1.getVariant());

        VariantAnnotation annotation2 = variantAnnotationService.getAnnotation("12:g.25398285C>A");
        assertEquals(variantMockData.get("12:g.25398285C>A").getStart(), annotation2.getStart());
        assertEquals(variantMockData.get("12:g.25398285C>A").getVariant(), annotation2.getVariant());
    }

    @Test
    public void getAnnotationsByVariantString()
        throws ResourceMappingException, VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        this.mockVariantFetcherMethods(variantMockData);

        List<String> variants = new ArrayList<>(2);
        variants.add("7:g.140453136A>T");
        variants.add("12:g.25398285C>A");

        List<VariantAnnotation> annotations = variantAnnotationService.getAnnotations(variants);
        assertEquals(variantMockData.get("7:g.140453136A>T").getStart(), annotations.get(0).getStart());
        assertEquals(variantMockData.get("7:g.140453136A>T").getVariant(), annotations.get(0).getVariant());
        assertEquals(variantMockData.get("12:g.25398285C>A").getStart(), annotations.get(1).getStart());
        assertEquals(variantMockData.get("12:g.25398285C>A").getVariant(), annotations.get(1).getVariant());
    }

    @Test
    public void getMutationAssessorEnrichedAnnotationByVariantString()
        throws ResourceMappingException, VariantAnnotationWebServiceException, VariantAnnotationNotFoundException,
        MutationAssessorWebServiceException, MutationAssessorNotFoundException, IsoformOverrideNotFoundException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, MutationAssessor> maMockData = this.mutationAssessorMockData.generateData();
        Map<String, IsoformOverride> isoformOverrideMockData = this.isoformOverrideMockData.generateData();

        this.mockVariantFetcherMethods(variantMockData);
        this.mockMutationAssessorServiceMethods(variantMockData, maMockData);
        this.mockIsoformOverrideServiceMethods(isoformOverrideMockData);

        List<String> fields = new ArrayList<>(1);
        fields.add("mutation_assessor");

        VariantAnnotation annotation1 = variantAnnotationService.getAnnotation(
            "7:g.140453136A>T", null, fields);

        assertEquals(maMockData.get("7,140453136,A,T"),
            annotation1.getMutationAssessorAnnotation().getAnnotation());

        VariantAnnotation annotation2 = variantAnnotationService.getAnnotation(
            "12:g.25398285C>A", null, fields);

        assertEquals(maMockData.get("12,25398285,C,A"),
            annotation2.getMutationAssessorAnnotation().getAnnotation());
    }

    @Test
    public void getHotspotEnrichedAnnotationByVariantString()
        throws ResourceMappingException, VariantAnnotationWebServiceException, VariantAnnotationNotFoundException,
        CancerHotspotsWebServiceException, IsoformOverrideNotFoundException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, List<Hotspot>> hotspotMockData = this.cancerHotspotMockData.generateData();
        Map<String, IsoformOverride> isoformOverrideMockData = this.isoformOverrideMockData.generateData();

        this.mockVariantFetcherMethods(variantMockData);
        this.mockHotspotServiceMethods(hotspotMockData);
        this.mockIsoformOverrideServiceMethods(isoformOverrideMockData);

        List<String> fields = new ArrayList<>(1);
        fields.add("hotspots");

        VariantAnnotation annotation1 = variantAnnotationService.getAnnotation(
            "7:g.140453136A>T", null, fields);

        assertEquals(hotspotMockData.get("ENST00000288602"),
            annotation1.getHotspotAnnotation().getAnnotation().get(0));

        VariantAnnotation annotation2 = variantAnnotationService.getAnnotation(
            "12:g.25398285C>A", null, fields);

        assertEquals(hotspotMockData.get("ENST00000256078"),
            annotation2.getHotspotAnnotation().getAnnotation().get(0));
    }

    @Test
    public void getIsorformOverrideEnrichedAnnotationByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, ResourceMappingException,
        IsoformOverrideNotFoundException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, IsoformOverride> isoformOverrideMockData = this.isoformOverrideMockData.generateData();

        this.mockVariantFetcherMethods(variantMockData);
        this.mockIsoformOverrideServiceMethods(isoformOverrideMockData);

        VariantAnnotation annotation1 = variantAnnotationService.getAnnotation(
            "7:g.140453136A>T", "mskcc", null);

        // first transcript of this annotation should be marked as canonical, the second one should NOT be marked
        assertEquals("1", annotation1.getTranscriptConsequences().get(0).getCanonical());
        assertEquals(null, annotation1.getTranscriptConsequences().get(1).getCanonical());

        VariantAnnotation annotation2 = variantAnnotationService.getAnnotation(
            "7:g.140453136A>T", "uniprot", null);

        // second transcript of this annotation should be marked as canonical, the first one should NOT be marked
        assertEquals(null, annotation2.getTranscriptConsequences().get(0).getCanonical());
        assertEquals("1", annotation2.getTranscriptConsequences().get(1).getCanonical());
    }

    private void mockVariantFetcherMethods(Map<String, VariantAnnotation> variantMockData)
        throws ResourceMappingException, VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        // mock methods in order to prevent hitting the live VEP web API
        Mockito.when(this.fetcher.fetchAndCache("7:g.140453136A>T")).thenReturn(variantMockData.get("7:g.140453136A>T"));
        Mockito.when(this.fetcher.fetchAndCache("12:g.25398285C>A")).thenReturn(variantMockData.get("12:g.25398285C>A"));

        List<String> variants = new ArrayList<>(2);
        variants.add("7:g.140453136A>T");
        variants.add("12:g.25398285C>A");

        List<VariantAnnotation> returnValue = new ArrayList<>(2);
        returnValue.add(variantMockData.get("7:g.140453136A>T"));
        returnValue.add(variantMockData.get("12:g.25398285C>A"));

        Mockito.when(this.fetcher.fetchAndCache(variants)).thenReturn(returnValue);
    }

    private void mockMutationAssessorServiceMethods(Map<String, VariantAnnotation> variantMockData,
                                                    Map<String, MutationAssessor> maMockData)
        throws MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        Mockito.when(this.mutationAssessorService.getMutationAssessor(
            variantMockData.get("7:g.140453136A>T"))).thenReturn(maMockData.get("7,140453136,A,T"));
        Mockito.when(this.mutationAssessorService.getMutationAssessor(
            variantMockData.get("12:g.25398285C>A"))).thenReturn(maMockData.get("12,25398285,C,A"));
    }

    private void mockHotspotServiceMethods(Map<String, List<Hotspot>> hotspotMockData)
        throws CancerHotspotsWebServiceException
    {
        // call the real getHotspots(TranscriptConsequence transcript) method when called with a transcript
        // it is the one calling the getHotspots(String transcriptId)
        Mockito.when(this.cancerHotspotService.getHotspots(
            Mockito.any(TranscriptConsequence.class), Mockito.any(VariantAnnotation.class))).thenCallRealMethod();

        Mockito.when(this.cancerHotspotService.filterHotspot(Mockito.any(Hotspot.class),
            Mockito.any(TranscriptConsequence.class),
            Mockito.any(VariantAnnotation.class))).thenReturn(true);

        Mockito.when(this.cancerHotspotService.getHotspots(
            "ENST00000288602")).thenReturn(hotspotMockData.get("ENST00000288602"));
        Mockito.when(this.cancerHotspotService.getHotspots(
            "ENST00000256078")).thenReturn(hotspotMockData.get("ENST00000256078"));
    }

    private void mockIsoformOverrideServiceMethods(Map<String, IsoformOverride> isoformOverrideMockData)
        throws IsoformOverrideNotFoundException
    {
        // false for null values
        Mockito.when(this.isoformOverrideService.hasData(null)).thenReturn(false);

        // true for mskcc and uniprot
        Mockito.when(this.isoformOverrideService.hasData("mskcc")).thenReturn(true);
        Mockito.when(this.isoformOverrideService.hasData("uniprot")).thenReturn(true);

        // when called for "mskcc" override, set first transcript "ENST00000288602" as canonical
        Mockito.when(this.isoformOverrideService.getIsoformOverride(
            "mskcc", "ENST00000288602")).thenReturn(isoformOverrideMockData.get("ENST00000288602"));
        Mockito.when(this.isoformOverrideService.getIsoformOverride(
            "uniprot", "ENST00000288602")).thenReturn(null);

        // when called for "uniprot" override, set second transcript "ENST00000479537" as canonical
        Mockito.when(this.isoformOverrideService.getIsoformOverride(
            "uniprot", "ENST00000479537")).thenReturn(isoformOverrideMockData.get("ENST00000479537"));
        Mockito.when(this.isoformOverrideService.getIsoformOverride(
            "mskcc", "ENST00000479537")).thenReturn(null);
    }
}
