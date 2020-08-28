package org.cbioportal.genome_nexus.web.mixin.uniprot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.uniprot.DbReferenceObject;
import org.cbioportal.genome_nexus.model.uniprot.Evidence;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureMixin {
    @ApiModelProperty(value = "Type", required = true)
    private String type;

    @ApiModelProperty(value = "['MOLECULE_PROCESSING', 'STRUCTURAL', 'DOMAINS_AND_SITES', 'MUTAGENESIS', 'PTM', 'SEQUENCE_INFORMATION', 'TOPOLOGY', 'VARIANTS']", required = true)
    private String category;

    @ApiModelProperty(value = "Cv ID", required = false)
    private String cvId;
    
    @ApiModelProperty(value = "Ft ID", required = false)
    private String ftId;

    @ApiModelProperty(value = "Description", required = false)
    private String description;

    @ApiModelProperty(value = "Alternative sequence", required = false)
    private String alternativeSequence;

    @ApiModelProperty(value = "Begin", required = true)
    private String begin;

    @ApiModelProperty(value = "End", required = true)
    private String end;

    @ApiModelProperty(value = "Molecule", required = false)
    private String molecule;

    @ApiModelProperty(value = "Xrefs", required = false)
    private List<DbReferenceObject> xrefs;

    @ApiModelProperty(value = "Evidences", required = true)
    private List<Evidence> evidences;
}