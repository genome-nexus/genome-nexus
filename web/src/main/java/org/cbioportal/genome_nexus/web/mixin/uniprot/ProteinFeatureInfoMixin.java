package org.cbioportal.genome_nexus.web.mixin.uniprot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.uniprot.Feature;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProteinFeatureInfoMixin {
    @ApiModelProperty(value = "Version", required = true)
    private String version;

    @ApiModelProperty(value = "Accession", required = true)
    private String accession;

    @ApiModelProperty(value = "Entry name", required = true)
    private String entryName;

    @ApiModelProperty(value = "Protein name", required = false)
    private String proteinName;

    @ApiModelProperty(value = "Gene name", required = false)
    private String geneName;

    @ApiModelProperty(value = "Organism name", required = false)
    private String organismName;

    @ApiModelProperty(value = "Protein existence", required = false)
    private String proteinExistence;

    @ApiModelProperty(value = "Sequence", required = true)
    private String sequence;

    @ApiModelProperty(value = "Sequence checksum", required = false)
    private String sequenceChecksum;

    @ApiModelProperty(value = "Sequence version", required = false)
    private Integer sequenceVersion;

    @ApiModelProperty(value = "Ensembl Gene ID", required = false)
    private String geteGeneId;

    @ApiModelProperty(value = "Gete protein ID", required = false)
    private String geteProteinId;

    @ApiModelProperty(value = "UniProt taxonomy ID", required = false)
    private Integer taxid;

    @ApiModelProperty(value = "Features", required = true)
    private List<Feature> features;
}