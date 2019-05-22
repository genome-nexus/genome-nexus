package org.cbioportal.genome_nexus.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Numerical utils.
 *
 * @author Selcuk Onur Sumer
 */
public class VariantIdTest
{
    @Test
    public void buildVariantId()
    {
        String[] variants = {
            "17:g.41276045_41276046delCT",
            "7:g.140453136A>T",
            "17:g.41242962_41242963insGA",
            "13:g.28611219_28611221delTGTinsGC",
            "chr1:g.182712A>C"
        };

        assertEquals("chr17:g.41276045_41276046del", VariantId.buildVariantId(variants[0]));
        assertEquals("chr7:g.140453136A>T", VariantId.buildVariantId(variants[1]));
        assertEquals("chr17:g.41242962_41242963insGA", VariantId.buildVariantId(variants[2]));
        assertEquals("chr13:g.28611219_28611221delinsGC", VariantId.buildVariantId(variants[3]));
        assertEquals("chr1:g.182712A>C", VariantId.buildVariantId(variants[4]));
    }

}
