package org.cbioportal.genome_nexus.web.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsemblGeneMixin {
    @ApiModelProperty(value = "Ensembl gene id", position=1, required = true)
    private String geneId;

    @ApiModelProperty(value = "Hugo symbol synonyms", position=2, required = false)
    private String[] synonyms;

    @ApiModelProperty(value = "Hugo symbol synonyms", position=3, required = false)
    private String[] previousSymbols;
}