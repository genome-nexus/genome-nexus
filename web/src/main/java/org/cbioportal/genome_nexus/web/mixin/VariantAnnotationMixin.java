package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantAnnotationMixin {

    @JsonProperty(required = true)
    @ApiModelProperty(value = "Variant key", required = true)
    private String variant;

    @JsonProperty(required = true)
    @ApiModelProperty(value = "Annotation data as JSON string", required = true)
    private String annotationJSON;

    @JsonProperty(value="id", required = true)
    @ApiModelProperty(value = "Variant id", required = true)
    private String variantId;

    @JsonProperty(value="assembly_name", required = true)
    @ApiModelProperty(value = "NCBI build number", required = false)
    private String assemblyName;

    @JsonProperty(value="seq_region_name", required = true)
    @ApiModelProperty(value = "Chromosome", required = false)
    private String seqRegionName;

    @JsonProperty(value="start", required = true)
    @ApiModelProperty(value = "Start position", required = false)
    public Integer getStart()
    {
        return start;
    }
    private Integer start;

    @JsonProperty(value="end", required = true)
    @ApiModelProperty(value = "End position", required = false)
    private Integer end;

    @JsonProperty(value="allele_string", required = true)
    @ApiModelProperty(value = "Allele string (e.g: A/T)", required = false)
    private String alleleString;

    @JsonProperty(value="strand", required = true)
    @ApiModelProperty(value = "Strand (negative or positive)", required = false)
    private Integer strand;

    @JsonProperty(value="most_severe_consequence", required = true)
    @ApiModelProperty(value = "Most severe consequence", required = false)
    private String mostSevereConsequence;

    @JsonProperty(value="transcript_consequences", required = true)
    @ApiModelProperty(value = "List of transcripts", required = false)
    private List<TranscriptConsequence> transcriptConsequences;

    @JsonIgnore
    private Map<String, Object> dynamicProps;

    @JsonAnySetter
    public void setDynamicProp(String key, Object value) {

    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicProps() {
        return null;
    }
}
