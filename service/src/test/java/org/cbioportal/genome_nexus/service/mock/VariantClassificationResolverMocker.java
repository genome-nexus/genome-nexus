package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.annotation.VariantClassificationResolver;
import org.mockito.Mockito;

import java.util.Map;

public class VariantClassificationResolverMocker
{
    public void mockMethods(Map<String, VariantAnnotation> variantMockData,
                            VariantClassificationResolver variantClassificationResolver)
    {
        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        ).thenReturn(
            "Frame_Shift_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("3:g.14106026_14106037delCCAGCAGTAGCT"))
        ).thenReturn(
            "In_Frame_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        ).thenReturn(
            "In_Frame_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("3:g.114058003_114058003delG"))
        ).thenReturn(
            "Frame_Shift_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        ).thenReturn(
            "In_Frame_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        ).thenReturn(
            "Frame_Shift_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("6:g.137519505_137519506delCT"))
        ).thenReturn(
            "Frame_Shift_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("6:g.137519505_137519506delCTinsA"))
        ).thenReturn(
            "Frame_Shift_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        ).thenReturn(
            "Missense_Mutation"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        ).thenReturn(
            "Frame_Shift_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("9:g.135797242_135797242delCinsAT"))
        ).thenReturn(
            "Frame_Shift_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("10:g.101953779_101953779delT"))
        ).thenReturn(
            "Frame_Shift_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("11:g.62393546_62393547delGGinsAA"))
        ).thenReturn(
            "Nonsense_Mutation"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        ).thenReturn(
            "Missense_Mutation"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("13:g.28608258_28608275delCATATTCATATTCTCTGAinsGGGGTGGGGGGG"))
        ).thenReturn(
            "In_Frame_Del"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        ).thenReturn(
            "In_Frame_Ins"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("19:g.46141892_46141893delTCinsAA"))
        ).thenReturn(
            "Splice_Site"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("22:g.29091840_29091841delTGinsCA"))
        ).thenReturn(
            "Missense_Mutation"
        );

        Mockito.when(
            variantClassificationResolver.resolve(variantMockData.get("22:g.36689419_36689421delCCT"))
        ).thenReturn(
            "In_Frame_Del"
        );
    }
}
