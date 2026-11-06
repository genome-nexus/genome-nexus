package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AlphaGenomeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AlphaGenomeAnnotationEnricherTest
{
    @Mock
    private AlphaGenomeService alphaGenomeService;

    @Test
    public void enrichSingleAnnotation() throws Exception
    {
        AlphaGenome alphaGenome = alphaGenome("7:g.140453136A>T");
        when(alphaGenomeService.getAlphaGenomeByAnnotation(any(VariantAnnotation.class)))
            .thenReturn(alphaGenome);

        VariantAnnotation annotation = new VariantAnnotation("7:g.140453136A>T");
        new AlphaGenomeAnnotationEnricher("alpha_genome", alphaGenomeService).enrich(annotation);

        assertEquals(alphaGenome, annotation.getAlphaGenomeAnnotation().getAnnotation());
        assertEquals(AlphaGenomeAnnotationEnricher.LICENSE,
            annotation.getAlphaGenomeAnnotation().getLicense());
    }

    @Test
    public void enrichSkipsMissingAnnotation() throws Exception
    {
        when(alphaGenomeService.getAlphaGenomeByAnnotation(any(VariantAnnotation.class)))
            .thenReturn(null);

        VariantAnnotation annotation = new VariantAnnotation("7:g.140453136A>T");
        new AlphaGenomeAnnotationEnricher("alpha_genome", alphaGenomeService).enrich(annotation);

        assertNull(annotation.getAlphaGenomeAnnotation());
    }

    @Test
    public void enrichListMatchesByVariant() throws Exception
    {
        AlphaGenome first = alphaGenome("7:g.140453136A>T");
        AlphaGenome second = alphaGenome("5:g.176940011C>T");
        when(alphaGenomeService.getAlphaGenomeByAnnotation(any(java.util.List.class)))
            .thenReturn(Arrays.asList(first, second));

        VariantAnnotation firstAnnotation = new VariantAnnotation("7:g.140453136A>T");
        VariantAnnotation secondAnnotation = new VariantAnnotation("5:g.176940011C>T");
        new AlphaGenomeAnnotationEnricher("alpha_genome", alphaGenomeService).enrich(
            Arrays.asList(firstAnnotation, secondAnnotation));

        assertEquals(first, firstAnnotation.getAlphaGenomeAnnotation().getAnnotation());
        assertEquals(second, secondAnnotation.getAlphaGenomeAnnotation().getAnnotation());
    }

    @Test
    public void enrichListSkipsUnmatchedAnnotations() throws Exception
    {
        when(alphaGenomeService.getAlphaGenomeByAnnotation(any(java.util.List.class)))
            .thenReturn(Collections.emptyList());

        VariantAnnotation annotation = new VariantAnnotation("7:g.140453136A>T");
        new AlphaGenomeAnnotationEnricher("alpha_genome", alphaGenomeService).enrich(
            Collections.singletonList(annotation));

        assertNull(annotation.getAlphaGenomeAnnotation());
    }

    private static AlphaGenome alphaGenome(String variant)
    {
        AlphaGenome alphaGenome = new AlphaGenome();
        alphaGenome.setVariant(variant);
        alphaGenome.setHg38Variant("chr7:140753336:A>T");
        alphaGenome.setCanonicalGene("BRAF");
        return alphaGenome;
    }
}
