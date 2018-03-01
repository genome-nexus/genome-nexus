package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariantAnnotationMockData implements MockData<VariantAnnotation>
{
    @Override
    public Map<String, VariantAnnotation> generateData()
    {
        Map<String, VariantAnnotation> mockData = new HashMap<>();

        VariantAnnotation variantAnnotation;
        List<TranscriptConsequence> transcriptConsequences;
        List<String> consequenceTerms;
        TranscriptConsequence consequence;

        // mock data for variant: 7:g.140453136A>T
        variantAnnotation = new VariantAnnotation();

        variantAnnotation.setVariant("7:g.140453136A>T");
        variantAnnotation.setSeqRegionName("7");
        variantAnnotation.setStart(140453136);
        variantAnnotation.setEnd(140453136);
        variantAnnotation.setAlleleString("A/T");
        variantAnnotation.setMostSevereConsequence("missense_variant");

        transcriptConsequences = new ArrayList<>();

        consequence = new TranscriptConsequence();
        consequenceTerms = new ArrayList<>();
        consequenceTerms.add("missense_variant");
        consequence.setConsequenceTerms(consequenceTerms);
        consequence.setTranscriptId("ENST00000288602");
        consequence.setGeneSymbol("BRAF");
        consequence.setProteinStart(600);
        consequence.setProteinEnd(600);

        transcriptConsequences.add(consequence);

        consequence = new TranscriptConsequence();
        consequenceTerms = new ArrayList<>();
        consequenceTerms.add("missense_variant");
        consequenceTerms.add("NMD_transcript_variant");
        consequence.setConsequenceTerms(consequenceTerms);
        consequence.setGeneSymbol("BRAF");
        consequence.setTranscriptId("ENST00000479537");
        consequence.setProteinStart(28);
        consequence.setProteinEnd(28);

        transcriptConsequences.add(consequence);
        variantAnnotation.setTranscriptConsequences(transcriptConsequences);

        mockData.put("7:g.140453136A>T", variantAnnotation);

        // mock data for variant: 12:g.25398285C>A
        variantAnnotation = new VariantAnnotation();
        variantAnnotation.setVariant("12:g.25398285C>A");
        variantAnnotation.setSeqRegionName("12");
        variantAnnotation.setStart(25398285);
        variantAnnotation.setEnd(25398285);
        variantAnnotation.setAlleleString("C/A");

        transcriptConsequences = new ArrayList<>();

        consequence = new TranscriptConsequence();
        consequence.setTranscriptId("ENST00000256078");
        consequence.setProteinStart(12);
        consequence.setProteinEnd(12);

        transcriptConsequences.add(consequence);
        variantAnnotation.setTranscriptConsequences(transcriptConsequences);

        mockData.put("12:g.25398285C>A", variantAnnotation);

        return mockData;
    }
}
