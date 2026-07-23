package org.cbioportal.genome_nexus.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HotspotVersionTest
{
    @Test
    public void fromStringParsesKnownValuesCaseInsensitively()
    {
        assertEquals(HotspotVersion.V2, HotspotVersion.fromString("v2"));
        assertEquals(HotspotVersion.V2, HotspotVersion.fromString("V2"));
        assertEquals(HotspotVersion.V3, HotspotVersion.fromString("v3"));
        assertEquals(HotspotVersion.V3, HotspotVersion.fromString("V3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringRejectsUnknownValues()
    {
        HotspotVersion.fromString("all");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringRejectsBogusValues()
    {
        HotspotVersion.fromString("bogus");
    }

    @Test
    public void v2MatchesOnlyV2AndTreatsMissingVersionAsV2()
    {
        assertTrue("v2 raw value should match V2", HotspotVersion.V2.matches("v2"));
        assertFalse("v3 raw value should not match V2", HotspotVersion.V2.matches("v3"));
        assertTrue("missing/null raw version should be treated as legacy v2 data",
            HotspotVersion.V2.matches(null));
    }

    @Test
    public void v3MatchesEverythingRegardlessOfRawVersion()
    {
        assertTrue(HotspotVersion.V3.matches("v2"));
        assertTrue(HotspotVersion.V3.matches("v3"));
        assertTrue("v3 should also include legacy hotspots with no version tag at all",
            HotspotVersion.V3.matches(null));
    }
}
