package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.HashMap;
import java.util.Map;

public class VariantAnnotationMockData implements MockData<VariantAnnotation>
{
    @Override
    public Map<String, VariantAnnotation> generateData()
    {
        Map<String, VariantAnnotation> mockData = new HashMap<>();

        VariantAnnotation variantAnnotation;

        // mock data for variant: 7:g.140453136A>T
        variantAnnotation = new VariantAnnotation();

        variantAnnotation.setVariant("7:g.140453136A>T");
        variantAnnotation.setSeqRegionName("7");
        variantAnnotation.setStart(140453136);
        variantAnnotation.setEnd(140453136);
        variantAnnotation.setAlleleString("A/T");

        mockData.put("7:g.140453136A>T", variantAnnotation);

        // mock data for variant: 12:g.25398285C>A
        variantAnnotation = new VariantAnnotation();
        variantAnnotation.setVariant("12:g.25398285C>A");
        variantAnnotation.setSeqRegionName("12");
        variantAnnotation.setStart(25398285);
        variantAnnotation.setEnd(25398285);
        variantAnnotation.setAlleleString("C/A");

        mockData.put("12:g.25398285C>A", variantAnnotation);

        return mockData;
    }
}
