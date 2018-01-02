package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.service.MockData;

import java.util.HashMap;
import java.util.Map;

public class IsoformOverrideMockData implements MockData<IsoformOverride>
{
    @Override
    public Map<String, IsoformOverride> generateData()
    {
        Map<String, IsoformOverride> mockData = new HashMap<>();

        IsoformOverride isoformOverride;

        // mock data for transcript: ENST00000288602
        isoformOverride = new IsoformOverride();
        isoformOverride.setTranscriptId("ENST00000288602");
        isoformOverride.setGeneSymbol("BRAF");

        mockData.put("ENST00000288602", isoformOverride);

        // mock data for transcript: ENST00000479537
        isoformOverride = new IsoformOverride();
        isoformOverride.setTranscriptId("ENST00000479537");
        isoformOverride.setGeneSymbol("BRAF");

        mockData.put("ENST00000479537", isoformOverride);

        return mockData;
    }
}
