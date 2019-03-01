
package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptConsequenceMixin {
  @JsonProperty("amino_acids")
  private String aminoAcids;

  @JsonProperty("canonical")
  private String canonical;

  @JsonProperty("codons")
  private String codons;

  @JsonProperty("consequence_terms")
  private List<String> consequenceTerms;

  @JsonProperty("gene_id")
  private String geneId;

  @JsonProperty("gene_symbol")
  private String geneSymbol;

  @JsonProperty("hgnc_id")
  private Integer hgncId;

  @JsonProperty("hgvsc")
  private String hgvsc;

  @JsonProperty("hgvsp")
  private String hgvsp;

  @JsonProperty("polyphen_prediction")
  private String polyphenPrediction;

  @JsonProperty("polyphen_score")
  private Double polyphenScore;

  @JsonProperty("protein_end")
  private Integer proteinEnd;

  @JsonProperty("protein_id")
  private String proteinId;

  @JsonProperty("protein_start")
  private Integer proteinStart;

  @JsonProperty("refseq_transcript_ids")
  private List<String> refseqTranscriptIds;

  @JsonProperty("sift_prediction")
  private String siftPrediction;

  @JsonProperty("sift_score")
  private Double siftScore;

  @JsonProperty("transcript_id")
  private String transcriptId;

  @JsonProperty("variant_allele")
  private String variantAllele;

  @JsonProperty("colocatedVariants_gnomad_afr_maf")
  private String colocatedVariantsGnomadAfrMaf;

  @JsonProperty("colocatedVariants_gnomad_eas_maf")
  private String colocatedVariantsGnomadEasMaf;

  @JsonProperty("colocatedVariants_gnomad_nfe_maf")
  private String colocatedVariantsGnomadNfeMaf;
}

