package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.IntegerRange;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.annotation.ProteinPositionResolver;
import org.cbioportal.genome_nexus.service.annotation.VariantClassificationResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HotspotFilterTest
{
    @InjectMocks
    private HotspotFilter hotspotFilter;

    @Mock
    private VariantClassificationResolver variantClassificationResolver;

    @Mock
    private ProteinPositionResolver proteinPositionResolver;

    @Test
    public void filterHotspotsByPositionAndMutationType()
    {
        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setVariantId("7:g.140453136A>T");

        Hotspot singleResidueHotspot = new Hotspot();
        singleResidueHotspot.setResidue("V600");
        singleResidueHotspot.setAminoAcidPosition(new IntegerRange(600, 600));
        singleResidueHotspot.setType("single residue");

        Hotspot indelHotspot = new Hotspot();
        indelHotspot.setResidue("594-602");
        indelHotspot.setAminoAcidPosition(new IntegerRange(594, 602));
        indelHotspot.setType("in-frame indel");

        // same mutation as the single residue one, except the hotspot type
        Hotspot hotspot3d = new Hotspot();
        hotspot3d.setResidue("V600");
        hotspot3d.setAminoAcidPosition(new IntegerRange(600, 600));
        hotspot3d.setType("3d");

        TranscriptConsequence singleResidueTranscript = new TranscriptConsequence();
        singleResidueTranscript.setProteinStart(600);
        singleResidueTranscript.setProteinEnd(600);

        TranscriptConsequence indelTranscript = new TranscriptConsequence();
        indelTranscript.setProteinStart(599);
        indelTranscript.setProteinEnd(613);

        this.mockVariantClassificationResolverMethod(annotation, singleResidueTranscript, "Missense_Mutation");
        this.mockVariantClassificationResolverMethod(annotation, indelTranscript, "In_Frame_Insertion");

        this.mockProteinChangeResolverMethod(annotation, singleResidueTranscript);
        this.mockProteinChangeResolverMethod(annotation, indelTranscript);

        assertTrue("Single residue missense hotspot should be selected for single residue transcript",
            this.hotspotFilter.filter(singleResidueHotspot, singleResidueTranscript, annotation));
        assertFalse("Single residue missense hotspot should be filtered out for indel transcript",
            this.hotspotFilter.filter(singleResidueHotspot, indelTranscript, annotation));

        assertTrue("Indel hotspot should be selected for indel transcript",
            this.hotspotFilter.filter(indelHotspot, indelTranscript, annotation));
        assertFalse("Indel hotspot should be filtered out for single residue transcript",
            this.hotspotFilter.filter(indelHotspot, singleResidueTranscript, annotation));

        assertTrue("3D hotspot should be selected for single residue transcript",
            this.hotspotFilter.filter(hotspot3d, singleResidueTranscript, annotation));
        assertFalse("3D hotspot should be filtered out for indel transcript",
            this.hotspotFilter.filter(hotspot3d, indelTranscript, annotation));
    }

    private void mockVariantClassificationResolverMethod(VariantAnnotation variantAnnotation,
                                                         TranscriptConsequence transcriptConsequence,
                                                         String variantClassification)
    {
        Set<String> returnValue = new HashSet<>();
        returnValue.add(variantClassification);

        Mockito.when(this.variantClassificationResolver.resolveAll(
            variantAnnotation, transcriptConsequence)).thenReturn(returnValue);
    }

    private void mockProteinChangeResolverMethod(VariantAnnotation variantAnnotation,
                                                 TranscriptConsequence transcriptConsequence)
    {
        IntegerRange returnValue = new IntegerRange(
            transcriptConsequence.getProteinStart(), transcriptConsequence.getProteinEnd());

        Mockito.when(this.proteinPositionResolver.resolve(
            variantAnnotation, transcriptConsequence)).thenReturn(returnValue);
    }
}
