package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class StrandSignResolverTest
{
    private final StrandSignResolver strandSignResolver = new StrandSignResolver();

    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void resolveStrandSign() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertNull(
            "Null annotation input -> Null output",
            this.strandSignResolver.resolve(null)
        );

        assertNull(
            "Variant annotation with null strand -> Null output",
            this.strandSignResolver.resolve(new VariantAnnotation())
        );

        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setStrand(-1);

        assertEquals(
            "Variant annotation with strand < 0 -> Minus (-)",
            "-",
            this.strandSignResolver.resolve(annotation)
        );

        annotation = new VariantAnnotation();
        annotation.setStrand(0);

        assertEquals(
            "Variant annotation with strand >= 0 -> Plus (+)",
            "+",
            this.strandSignResolver.resolve(annotation)
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        );

        // strand: 1
        assertEquals(
            "+",
            this.strandSignResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        );
    }
}
