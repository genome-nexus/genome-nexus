package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColocatedVariantMixin 
{
    @JsonProperty(value="gnomad_nfe_allele", required = true)
    @ApiModelProperty(value = "GnomAD Non-Finnish European Allele", position=1, required = true)
    private String gnomadNfeAllele;

    @JsonProperty(value="gnomad_nfe_maf", required = true)
    @ApiModelProperty(value = "GnomAD Non-Finnish European MAF", position=2, required = true)
    private String gnomadNfeMaf;

    @JsonProperty(value="gnomad_afr_allele", required = true)
    @ApiModelProperty(value = "GnomAD African/African American Allele", position=3, required = true)
    private String gnomadAfrAllele;

    @JsonProperty(value="gnomad_afr_maf", required = true)
    @ApiModelProperty(value = "GnomAD African/African American MAF", position=4, required = true)
    private String gnomadAfrMaf;

    @JsonProperty(value="gnomad_eas_allele", required = true)
    @ApiModelProperty(value = "GnomAD East Asian Allele", position=5, required = true)
    private String gnomadEasAllele;

    @JsonProperty(value="gnomad_eas_maf", required = true)
    @ApiModelProperty(value = "GnomAD East Asian MAF", position=6, required = true)
    private String gnomadEasMaf;

}
