package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantAnnotationSummaryMixin
{
    @JsonProperty(required = true)
    @ApiModelProperty(value = "Variant key", required = true)
    private String variant;

    @ApiModelProperty(value = "Genomic location")
    private GenomicLocation genomicLocation;

    @ApiModelProperty(value = "Strand (- or +)")
    private String strandSign;

    @ApiModelProperty(value = "Variant type")
    private String variantType;

    @ApiModelProperty(value = "Assembly name")
    private String assemblyName;

    @ApiModelProperty(value = "Canonical transcript id")
    private String canonicalTranscriptId;

    @ApiModelProperty(value = "List of transcript consequence summaries", required = true)
    private List<TranscriptConsequenceSummary> transcriptConsequences;
}
