package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.genome_nexus.client.TranscriptConsequence;
import org.genome_nexus.client.VariantAnnotationSummary;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariantAnnotationMixin
{
    @JsonProperty("allele_string")
    private String alleleString;
    @JsonProperty("assembly_name")
    private String assemblyName;
    @JsonProperty("end")
    private Integer end;
    @JsonProperty("id")
    private String id;
    @JsonProperty("most_severe_consequence")
    private String mostSevereConsequence;
    @JsonProperty("seq_region_name")
    private String seqRegionName;
    @JsonProperty("start")
    private Integer start;
    @JsonProperty("strand")
    private Integer strand;
    @JsonProperty("transcript_consequences")
    private List<TranscriptConsequence> transcriptConsequences;
    @JsonProperty("variant")
    private String variant;
    @JsonProperty("annotation_summary")
    private VariantAnnotationSummary annotationSummary;
}
