package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.annotation.VariantTypeResolver;
import org.mockito.Mockito;

import java.util.Map;

public class VariantTypeResolverMocker
{
    public void mockMethods(Map<String, VariantAnnotation> variantMockData,
                            VariantTypeResolver variantTypeResolver)
    {
        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        ).thenReturn(
            "SNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        ).thenReturn(
            "SNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        ).thenReturn(
            "DEL"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        ).thenReturn(
            "INS"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        ).thenReturn(
            "DNP"
        );

        Mockito.when(
            variantTypeResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        ).thenReturn(
            "DEL"
        );
    }
}
