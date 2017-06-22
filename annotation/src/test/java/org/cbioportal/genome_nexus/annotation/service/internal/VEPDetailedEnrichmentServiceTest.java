package org.cbioportal.genome_nexus.annotation.service.internal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Hongxin on 6/22/17.
 */
public class VEPDetailedEnrichmentServiceTest {
    @Test
    public void resolveHgvspShort() throws Exception {
        VEPDetailedEnrichmentService service = new VEPDetailedEnrichmentService();
        assertEquals("p.L729I", service.resolveHgvspShort("p.Leu729Ile"));
        assertEquals("p.L729I", service.resolveHgvspShort("ENSP00000363822.3:p.Leu729Ile"));
    }

    @Test
    public void resolveRefSeq() throws Exception {
        VEPDetailedEnrichmentService service = new VEPDetailedEnrichmentService();
        assertEquals("NM_000044.3", service.resolveRefSeq("NM_000044.3,NM_000044.4"));
        assertEquals("NM_000044.3", service.resolveRefSeq("NM_000044.3 , NM_000044.4"));
        assertEquals("", service.resolveRefSeq(""));
        assertEquals("", service.resolveRefSeq(null));
    }

    @Test
    public void resolveCodonChange() throws Exception {
        VEPDetailedEnrichmentService service = new VEPDetailedEnrichmentService();
        assertEquals("Tta/Ata", service.resolveCodonChange("Tta/Ata"));
        assertEquals("", service.resolveCodonChange(""));
        assertEquals("", service.resolveCodonChange(null));
    }

    @Test
    public void resolveConsequence() throws Exception {
        VEPDetailedEnrichmentService service = new VEPDetailedEnrichmentService();
        List<String> consequences = new ArrayList<>();
        assertEquals("", service.resolveConsequence(consequences));
        consequences.add("missense_variant");
        assertEquals("missense_variant", service.resolveConsequence(consequences));
        consequences.add("inframe_insertion");
        assertEquals("missense_variant,inframe_insertion", service.resolveConsequence(consequences));
    }

}
