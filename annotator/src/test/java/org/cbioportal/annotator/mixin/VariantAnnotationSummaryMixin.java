
package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import org.genome_nexus.client.GenomicLocation;
import org.genome_nexus.client.TranscriptConsequenceSummary;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariantAnnotationSummaryMixin {
    @JsonProperty("variant")
    private String variant;

    @JsonProperty("genomicLocation")
    private GenomicLocation genomicLocation;

    @JsonProperty("strandSign")
    private String strandSign;

    @JsonProperty("variantType")
    private String variantType;

    @JsonProperty("assemblyName")
    private String assemblyName;

    @JsonProperty("canonicalTranscriptId")
    private String canonicalTranscriptId;

    @JsonProperty("transcriptConsequenceSummaries")
    private List<TranscriptConsequenceSummary> transcriptConsequenceSummaries;

    @JsonProperty("transcriptConsequenceSummary")
    private TranscriptConsequenceSummary transcriptConsequenceSummary;
}
