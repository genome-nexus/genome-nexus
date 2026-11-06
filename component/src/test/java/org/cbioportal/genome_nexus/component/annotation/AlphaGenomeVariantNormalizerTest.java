package org.cbioportal.genome_nexus.component.annotation;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AlphaGenomeVariantNormalizerTest
{
    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Mock
    private Grch38ReferenceSequenceResolver referenceResolver;

    private LiftOver liftOver;

    @Before
    public void setUp()
    {
        // Exercises the exact production chain-loading path.
        this.liftOver = AlphaGenomeVariantNormalizer.loadDefaultLiftOver();
    }

    @Test
    public void ddx41Grch37SnvLiftsToChr5()
    {
        when(referenceResolver.extract("chr5", 177513009, 177513010)).thenReturn("C");
        stubCanonicalGene("ENSG00000183258.5", "DDX41");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 176940011, 176940011, "C/T", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chr5:177513010:C>T", result.get().getVariantStr());
        assertEquals("chr5", result.get().getChrom());
        assertEquals(177513010, result.get().getPos());
        assertEquals("C", result.get().getRef());
        assertEquals("T", result.get().getAlt());
        assertEquals("ENSG00000183258", result.get().getGeneId());
        assertEquals("DDX41", result.get().getGeneSymbol());
    }

    @Test
    public void brafV600eGrch37SnvLiftsToChr7()
    {
        when(referenceResolver.extract("chr7", 140753335, 140753336)).thenReturn("A");
        stubCanonicalGene("ENSG00000157764", "BRAF");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "7", 140453136, 140453136, "A/T", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chr7:140753336:A>T", result.get().getVariantStr());
        assertEquals("ENSG00000157764", result.get().getGeneId());
        assertEquals("BRAF", result.get().getGeneSymbol());
    }

    @Test
    public void smad4DeletionNormalizesToGaaGreaterThanG()
    {
        // Real liftover maps GRCh37 18:48604697-48604698 to chr18:51078327-51078328;
        // GRCh38 carries G at 51078326 followed by AA, so the VCF form is GAA>G.
        when(referenceResolver.extract("chr18", 51078326, 51078328)).thenReturn("AA");
        when(referenceResolver.extract("chr18", 51078325, 51078326)).thenReturn("G");
        when(referenceResolver.extract("chr18", 51078325, 51078328)).thenReturn("GAA");
        stubCanonicalGene("ENSG00000141646", "SMAD4");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "18", 48604697, 48604698, "AA/-", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals(51078326, result.get().getPos());
        assertEquals("GAA", result.get().getRef());
        assertEquals("G", result.get().getAlt());
        assertEquals("chr18:51078326:GAA>G", result.get().getVariantStr());
        assertEquals("ENSG00000141646", result.get().getGeneId());
        assertEquals("SMAD4", result.get().getGeneSymbol());
    }

    @Test
    public void insertionAnchorsWithPrecedingBase()
    {
        LiftOver shiftingLiftOver = mock(LiftOver.class);
        when(shiftingLiftOver.liftOver(any(Interval.class))).thenAnswer(invocation -> {
            Interval input = invocation.getArgument(0);
            return new Interval("chr1", input.getStart() + 900, input.getEnd() + 900);
        });
        Map<String, String> sequences = new HashMap<>();
        sequences.put("chr1", repeat('N', 999) + "GACGT");
        Grch38ReferenceSequenceResolver inMemory = new InMemoryGrch38ReferenceSequenceResolver(sequences);
        AlphaGenomeVariantNormalizer normalizer = new AlphaGenomeVariantNormalizer(
            shiftingLiftOver, inMemory, canonicalTranscriptResolver);
        stubCanonicalGene("ENSG00000100000.1", "TEST1");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer.normalize(annotation(
            "1", 100, 101, "-/T", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chr1", result.get().getChrom());
        assertEquals(1000, result.get().getPos());
        assertEquals("G", result.get().getRef());
        assertEquals("GT", result.get().getAlt());
        assertEquals("chr1:1000:G>GT", result.get().getVariantStr());
        assertEquals("ENSG00000100000", result.get().getGeneId());
    }

    @Test
    public void refMismatchAgainstGrch38IsRejected()
    {
        // GRCh38 carries C at chr5:177513010, so a query REF of G must be rejected.
        when(referenceResolver.extract("chr5", 177513009, 177513010)).thenReturn("C");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 176940011, 176940011, "G/T", "GRCh37", 1, true, null));

        assertFalse(result.isPresent());
    }

    @Test
    public void mitochondrialContigIsRejected()
    {
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "MT", 100, 100, "A/T", "GRCh37", 1, true, null));

        assertFalse(result.isPresent());
    }

    @Test
    public void invalidAlleleCharactersAreRejected()
    {
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 176940011, 176940011, "A/N", "GRCh37", 1, true, null));

        assertFalse(result.isPresent());
    }

    @Test
    public void failedAnnotationIsRejected()
    {
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 176940011, 176940011, "C/T", "GRCh37", 1, false, null));

        assertFalse(result.isPresent());
    }

    @Test
    public void errorMessageIsRejected()
    {
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 176940011, 176940011, "C/T", "GRCh37", 1, true, "bad"));

        assertFalse(result.isPresent());
    }

    @Test
    public void chromosomeXAndYHappyPath()
    {
        when(referenceResolver.extract("chrX", 1000000, 1000001)).thenReturn("A");
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "23", 1000001, 1000001, "A/T", "GRCh38", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chrX", result.get().getChrom());
        assertEquals(1000001, result.get().getPos());
        assertEquals("chrX:1000001:A>T", result.get().getVariantStr());
    }

    @Test
    public void negativeStrandVariantIsReverseComplementedAndAccepted()
    {
        // Query strand -1: C/A -> on strand +1 becomes G/T.
        when(referenceResolver.extract("chr5", 177513009, 177513010)).thenReturn("G");
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "5", 177513010, 177513010, "C/A", "GRCh38", -1, true, null));

        assertTrue(result.isPresent());
        assertEquals("G", result.get().getRef());
        assertEquals("T", result.get().getAlt());
        assertEquals("chr5:177513010:G>T", result.get().getVariantStr());
    }

    @Test
    public void grch38NativeAssemblyPassesThroughWithoutLiftover()
    {
        when(referenceResolver.extract("chr7", 140753335, 140753336)).thenReturn("A");
        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "7", 140753336, 140753336, "A/T", "GRCh38", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals(140753336, result.get().getPos());
        assertEquals("chr7:140753336:A>T", result.get().getVariantStr());
    }

    @Test
    public void missingReferenceResolverFailsFast()
    {
        AlphaGenomeVariantNormalizer unconfigured =
            new AlphaGenomeVariantNormalizer(liftOver, null, canonicalTranscriptResolver);

        try {
            unconfigured.normalize(annotation("5", 176940011, 176940011, "C/T", "GRCh37", 1, true, null));
            fail("Expected IllegalStateException when no reference resolver is configured");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("Grch38ReferenceSequenceResolver"));
        }
    }

    @Test
    public void negativeStrandChainLiftoverSnvIsReverseComplemented()
    {
        // Real hg19ToHg38 chain maps chr1:206319192 to negative strand chr1:206022176.
        // Alleles C/A on GRCh37 + strand must be reverse-complemented to G/T on GRCh38.
        when(referenceResolver.extract("chr1", 206022175, 206022176)).thenReturn("G");
        stubCanonicalGene("ENSG00000143811", "KIF21B");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "1", 206319192, 206319192, "C/A", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chr1", result.get().getChrom());
        assertEquals(206022176, result.get().getPos());
        assertEquals("G", result.get().getRef());
        assertEquals("T", result.get().getAlt());
        assertEquals("chr1:206022176:G>T", result.get().getVariantStr());
        assertEquals("ENSG00000143811", result.get().getGeneId());
        assertEquals("KIF21B", result.get().getGeneSymbol());
    }

    @Test
    public void secondNegativeStrandChainLiftoverSnvIsReverseComplemented()
    {
        // Real hg19ToHg38 chain maps chr1:120930255 to negative strand chr1:143880475.
        // Alleles C/T on GRCh37 + strand must be reverse-complemented to G/A on GRCh38.
        when(referenceResolver.extract("chr1", 143880474, 143880475)).thenReturn("G");
        stubCanonicalGene("ENSG00000213088", "NOTCH2NL");

        Optional<NormalizedAlphaGenomeVariant> result = normalizer().normalize(annotation(
            "1", 120930255, 120930255, "C/T", "GRCh37", 1, true, null));

        assertTrue(result.isPresent());
        assertEquals("chr1", result.get().getChrom());
        assertEquals(143880475, result.get().getPos());
        assertEquals("G", result.get().getRef());
        assertEquals("A", result.get().getAlt());
        assertEquals("chr1:143880475:G>A", result.get().getVariantStr());
    }

    @Test
    public void spanLengthMismatchDuringLiftoverIsRejected()
    {
        LiftOver mismatchedLiftOver = mock(LiftOver.class);
        // Query span is 3 bp (100 to 102), but lifted span is 4 bp (200 to 203)
        when(mismatchedLiftOver.liftOver(any(Interval.class))).thenReturn(
            new Interval("chr1", 200, 203));

        AlphaGenomeVariantNormalizer normalizer = new AlphaGenomeVariantNormalizer(
            mismatchedLiftOver, referenceResolver, canonicalTranscriptResolver);

        Optional<NormalizedAlphaGenomeVariant> result = normalizer.normalize(annotation(
            "1", 100, 102, "CAG/-", "GRCh37", 1, true, null));

        assertFalse(result.isPresent());
    }

    private AlphaGenomeVariantNormalizer normalizer()
    {
        return new AlphaGenomeVariantNormalizer(liftOver, referenceResolver, canonicalTranscriptResolver);
    }

    private void stubCanonicalGene(String geneId, String geneSymbol)
    {
        TranscriptConsequence canonical = new TranscriptConsequence();
        canonical.setGeneId(geneId);
        canonical.setGeneSymbol(geneSymbol);
        when(canonicalTranscriptResolver.resolve(any(VariantAnnotation.class))).thenReturn(canonical);
    }

    private static VariantAnnotation annotation(
        String seqRegionName,
        int start,
        int end,
        String alleleString,
        String assemblyName,
        Integer strand,
        boolean successfullyAnnotated,
        String errorMessage
    ) {
        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setSeqRegionName(seqRegionName);
        annotation.setStart(start);
        annotation.setEnd(end);
        annotation.setAlleleString(alleleString);
        annotation.setAssemblyName(assemblyName);
        annotation.setStrand(strand);
        annotation.setSuccessfullyAnnotated(successfullyAnnotated);
        annotation.setErrorMessage(errorMessage);
        return annotation;
    }

    private static String repeat(char value, int count)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < count; i++) {
            out.append(value);
        }
        return out.toString();
    }
}
