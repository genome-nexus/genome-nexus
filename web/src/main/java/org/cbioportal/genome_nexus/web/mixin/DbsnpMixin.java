package org.cbioportal.genome_nexus.web.mixin;
import java.util.List;

import org.cbioportal.genome_nexus.model.Alleles;
import org.cbioportal.genome_nexus.model.Dbsnp;
import org.cbioportal.genome_nexus.model.Gene;
import org.cbioportal.genome_nexus.model.Hg19;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbsnpMixin
{
@ApiModelProperty(value = "_license", required = false)
private String _license;
@ApiModelProperty(value = "allele_origin", required = false)
private String allele_origin;
@ApiModelProperty(value = "alleles", required = false)
private Alleles alleles;
@ApiModelProperty(value = "alt", required = false)
private String alt;
@ApiModelProperty(value = "chrom", required = false)
private String chrom;
@ApiModelProperty(value = "class", required = false)
private String _class;
@ApiModelProperty(value = "dbsnp_build", required = false)
private Integer dbsnp_build;
// @ApiModelProperty(value = "flags", required = false)
// private Flags flags;
@ApiModelProperty(value = "gene", required = false)
private Gene gene;
@ApiModelProperty(value = "hg19", required = false)
private Hg19 hg19;
@ApiModelProperty(value = "ref", required = false)
private String ref;
@ApiModelProperty(value = "rsid", required = false)
private String rsid;
@ApiModelProperty(value = "validated", required = false)
private Boolean validated;
@ApiModelProperty(value = "var_subtype", required = false)
private String var_subtype;
@ApiModelProperty(value = "vartype", required = false)
private String vartype;}