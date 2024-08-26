
package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.genome_nexus.client.IntegerRange;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptConsequenceSummaryMixin {
    @JsonProperty("transcriptId")
    private String transcriptId;

    @JsonProperty("codonChange")
    private String codonChange;

    @JsonProperty("entrezGeneId")
    private String entrezGeneId;

    @JsonProperty("consequenceTerms")
    private String consequenceTerms;

    @JsonProperty("hugoGeneSymbol")
    private String hugoGeneSymbol;

    @JsonProperty("hgvspShort")
    private String hgvspShort;

    @JsonProperty("hgvsp")
    private String hgvsp;

    @JsonProperty("hgvsc")
    private String hgvsc;

    @JsonProperty("proteinPosition")
    private IntegerRange proteinPosition;

    @JsonProperty("refSeq")
    private String refSeq;

    @JsonProperty("variantClassification")
    private String variantClassification;

    @JsonProperty("exon")
    private String exon;

    @JsonProperty("pathogencity")
    private String pathogencity;

    @JsonProperty("score")
    private String pathogencityScore;;
}