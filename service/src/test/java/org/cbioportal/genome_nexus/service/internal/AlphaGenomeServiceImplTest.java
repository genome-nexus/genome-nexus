package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.component.annotation.AlphaGenomeVariantNormalizer;
import org.cbioportal.genome_nexus.component.annotation.NormalizedAlphaGenomeVariant;
import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.cached.CachedAlphaGenomeFetcher;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AlphaGenomeServiceImplTest
{
    @Mock
    private CachedAlphaGenomeFetcher fetcher;

    @Mock
    private AlphaGenomeVariantNormalizer normalizer;

    @Test
    @SuppressWarnings("unchecked")
    public void normalizedVariantBuildsSidecarBodyWithOriginalId() throws Exception
    {
        NormalizedAlphaGenomeVariant normalized = new NormalizedAlphaGenomeVariant(
            "chr7", 140753336, "A", "T", "ENSG00000157764", "BRAF");
        when(normalizer.normalize(any(VariantAnnotation.class)))
            .thenReturn(Optional.of(normalized));

        AlphaGenome alphaGenome = new AlphaGenome();
        alphaGenome.setVariant("7:g.140453136A>T");
        when(fetcher.fetchAndCache(any(Map.class)))
            .thenReturn(Collections.singletonList(alphaGenome));

        VariantAnnotation annotation = new VariantAnnotation("7:g.140453136A>T");
        AlphaGenome result =
            new AlphaGenomeServiceImpl(fetcher, normalizer).getAlphaGenomeByAnnotation(annotation);

        assertEquals(alphaGenome, result);
        ArgumentCaptor<Map> bodies = ArgumentCaptor.forClass(Map.class);
        verify(fetcher).fetchAndCache(bodies.capture());
        assertTrue(bodies.getValue().containsKey("7:g.140453136A>T"));
        Map<String, Object> body =
            (Map<String, Object>) bodies.getValue().get("7:g.140453136A>T");
        assertEquals("7:g.140453136A>T", body.get("variant"));
        assertEquals("chr7:140753336:A>T",
            body.get("chromosome") + ":" + body.get("position") + ":"
                + body.get("reference_allele") + ">" + body.get("alternate_allele"));
        assertEquals("BRAF", body.get("canonical_gene"));
    }

    @Test
    public void rejectedVariantIsNeverSentToSidecar() throws Exception
    {
        when(normalizer.normalize(any(VariantAnnotation.class)))
            .thenReturn(Optional.empty());

        VariantAnnotation annotation = new VariantAnnotation("MT:g.1A>T");
        AlphaGenome result =
            new AlphaGenomeServiceImpl(fetcher, normalizer).getAlphaGenomeByAnnotation(annotation);

        assertNull(result);
        verify(fetcher, never()).fetchAndCache(any(Map.class));
        verify(fetcher, never()).fetchAndCache(any(java.util.List.class));
    }

    @Test(expected = AlphaGenomeWebServiceException.class)
    public void mappingFailureSurfacesAsWebServiceException() throws Exception
    {
        NormalizedAlphaGenomeVariant normalized = new NormalizedAlphaGenomeVariant(
            "chr7", 140753336, "A", "T", "ENSG00000157764", "BRAF");
        when(normalizer.normalize(any(VariantAnnotation.class)))
            .thenReturn(Optional.of(normalized));
        when(fetcher.fetchAndCache(any(Map.class)))
            .thenThrow(new ResourceMappingException("bad json"));

        new AlphaGenomeServiceImpl(fetcher, normalizer).getAlphaGenomeByAnnotation(
            new VariantAnnotation("7:g.140453136A>T"));
    }
}
