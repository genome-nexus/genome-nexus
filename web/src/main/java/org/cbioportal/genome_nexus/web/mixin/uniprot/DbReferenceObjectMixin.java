package org.cbioportal.genome_nexus.web.mixin.uniprot;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbReferenceObjectMixin {
    @ApiModelProperty(value = "Name", required = true)
    private String name;

    @ApiModelProperty(value = "ID", required = true)
    private String id;

    @ApiModelProperty(value = "URL", required = true)
    private String url;

    @ApiModelProperty(value = "Alternative URL", required = true)
    private String alternativeUrl;

    @ApiModelProperty(value = "Reviewed", required = true)
    private Boolean reviewed;
}