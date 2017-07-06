package org.cbioportal.genmone_nexus.annotation.util;

import org.cbioportal.genome_nexus.annotation.util.HGVS;
import org.junit.Test;

import java.util.logging.Logger;
import java.util.List;


import static org.junit.Assert.*;

public class HGVSTest {

    // for debugging
    private static Logger log = Logger.getLogger(String.valueOf(MutationAssessorTest.class));


    @Test
    public void mutationAssessor() {

        assertEquals("1,115256530,G,T", HGVS.getMutationAssessorString("1:g.115256530G>T"));
        assertEquals("4,55152093,A,T", HGVS.getMutationAssessorString("4:g.55152093A>T"));
        assertEquals("2,29416326,G,A", HGVS.getMutationAssessorString("2:g.29416326G>A"));
        assertEquals("13,28626706,A,G", HGVS.getMutationAssessorString("13:g.28626706A>G"));

    }

    @Test
    public void parser() {

        List<String> variant = HGVS.parseInput("AGT:c.803T>C");

        assertTrue(variant.size() == 6);

        assertTrue(variant.get(0).equals("AGT"));
        assertTrue(variant.get(1).equals("c"));
        assertTrue(variant.get(2).equals("803"));
        assertTrue(variant.get(3).equals("T"));
        assertTrue(variant.get(4).equals(">"));
        assertTrue(variant.get(5).equals("C"));

        List<String> variant2 = HGVS.parseInput("ENST00000003084:c.1431_1433A>T");

        assertTrue(variant.size() == 6);

        assertTrue(variant2.get(0).equals("ENST00000003084"));
        assertTrue(variant2.get(1).equals("c"));
        assertTrue(variant2.get(2).equals("1431_1433"));
        assertTrue(variant2.get(3).equals("A"));
        assertTrue(variant2.get(4).equals(">"));
        assertTrue(variant2.get(5).equals("T"));

    }

}
