package org.cbioportal.genome_nexus.web.mixin.uniprot;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.uniprot.DbReferenceObject;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceMixin {
    @ApiModelProperty(value = "Code", required = true)
    private String code;

    @ApiModelProperty(value = "Label", required = false)
    private String label;

    @ApiModelProperty(value = "Source", required = true)
    private DbReferenceObject source;
}