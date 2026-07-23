package org.cbioportal.genome_nexus.model;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HotspotTest
{
    private Hotspot buildHotspot(String hugoSymbol, String residue, String type, Integer tumorCount, String version)
    {
        Hotspot hotspot = new Hotspot();
        hotspot.setHugoSymbol(hugoSymbol);
        hotspot.setResidue(residue);
        hotspot.setType(type);
        hotspot.setTumorCount(tumorCount);
        hotspot.setVersion(version);
        return hotspot;
    }

    @Test
    public void hotspotsWithDifferentVersionAreNotEqual()
    {
        Hotspot v2 = buildHotspot("BRAF", "V600", "single residue", 100, "v2");
        Hotspot v3 = buildHotspot("BRAF", "V600", "single residue", 100, "v3");

        assertFalse("hotspots identical except for version should not be equal", v2.equals(v3));
        assertFalse(v2.hashCode() == v3.hashCode());
    }

    @Test
    public void hotspotsWithSameVersionAreEqual()
    {
        Hotspot a = buildHotspot("BRAF", "V600", "single residue", 100, "v2");
        Hotspot b = buildHotspot("BRAF", "V600", "single residue", 100, "v2");

        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hotspotWithNullVersionDoesNotThrowAndIsSelfEqual()
    {
        Hotspot legacy = buildHotspot("KRAS", "E63", "3d", 2, null);

        // must not throw despite version being null (simulates a pre-migration
        // Mongo document that has no "version" field at all)
        int hashCode = legacy.hashCode();

        assertTrue(legacy.equals(legacy));

        Set<Hotspot> set = new LinkedHashSet<>();
        set.add(legacy);
        set.add(legacy);
        assertEquals("a null-version hotspot should still dedupe against itself in a Set", 1, set.size());
    }

    @Test
    public void nullVersionAndExplicitV2AreNotEqual()
    {
        // a legacy hotspot (no version tag) is NOT the same equals()/hashCode()
        // identity as an explicitly-tagged v2 hotspot, even though HotspotVersion.V2
        // treats them the same for *filtering* purposes -- equals()/hashCode() is a
        // separate, stricter concern (dedup identity, not filter semantics)
        Hotspot legacy = buildHotspot("KRAS", "E63", "3d", 2, null);
        Hotspot explicitV2 = buildHotspot("KRAS", "E63", "3d", 2, "v2");

        assertFalse(legacy.equals(explicitV2));
    }
}
