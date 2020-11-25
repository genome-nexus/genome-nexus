package org.cbioportal.genome_nexus.web.mixin;

import org.cbioportal.genome_nexus.model.HrdScore;
import io.swagger.annotations.ApiModelProperty;

public class StatsByTumorTypeMixin {
    @ApiModelProperty("Tumor Type")
    private String tumorType;

    @ApiModelProperty("Number Of Cancer Type Count")
    private Integer nCancerTypeCount;

    @ApiModelProperty("Frequency Of Cancer Type Count")
    private Double fCancerTypeCount;

    @ApiModelProperty("Frequency Of Biallelic")
    private Double fBiallelic;

    @ApiModelProperty("Median Age at Dx")
    private Integer ageAtDx;

    @ApiModelProperty("Median TMB")
    private Double tmb;

    @ApiModelProperty("Msi Score")
    private Double msiScore;

    @ApiModelProperty("Number With Signature")
    private Integer nWithSig;

    @ApiModelProperty("Hrd Score")
    private HrdScore hrdScore;
}
