/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cbioportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "consequence_terms",
    "gene_id",
    "gene_symbol",
    "hgnc_id",
    "protein_id",
    "variant_allele",
    "amino_acids",
    "canonical",
    "codons",
    "hgvsc",
    "hgvsp",
    "protein_end",
    "protein_start",
    "refseq_transcript_ids"
})
public class TranscriptConsequence {

    @JsonProperty("consequence_terms")
    private List<String> consequenceTerms = new ArrayList<String>();
    @JsonProperty("gene_id")
    private String geneId;
    @JsonProperty("gene_symbol")
    private String geneSymbol;
    @JsonProperty("hgnc_id")
    private String hgncId;
    @JsonProperty("protein_id")
    private String proteinId;
    @JsonProperty("transcript_id")
    private String transcriptId;
    @JsonProperty("variant_allele")
    private String variantAllele;
    @JsonProperty("amino_acids")
    private String aminoAcids;
    @JsonProperty("canonical")
    private String canonical;
    @JsonProperty("codons")
    private String codons;
    @JsonProperty("hgvsc")
    private String hgvsc;
    @JsonProperty("hgvsp")
    private String hgvsp;
    @JsonProperty("protein_end")
    private String proteinEnd;
    @JsonProperty("protein_start")
    private String proteinStart;
    @JsonProperty("refseq_transcript_ids")
    private List<String> refseqTranscriptIds = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
    * No args constructor for use in serialization
    * 
    */
    public TranscriptConsequence() {}

    /**
    * 
    * @param geneSymbol
    * @param aminoAcids
    * @param refseqTranscriptIds
    * @param hgvsp
    * @param transcriptId
    * @param proteinStart
    * @param consequenceTerms
    * @param variantAllele
    * @param codons
    * @param geneId
    * @param proteinId
    * @param hgvsc
    * @param proteinEnd
    * @param hgncId
    * @param canonical
    */
    public TranscriptConsequence(List<String> consequenceTerms, String geneId, String geneSymbol, String hgncId, String proteinId, String transcriptId, String variantAllele, String aminoAcids, String canonical, String codons, String hgvsc, String hgvsp, String proteinEnd, String proteinStart, List<String> refseqTranscriptIds) {
        this.consequenceTerms = consequenceTerms;
        this.geneId = geneId;
        this.geneSymbol = geneSymbol;
        this.hgncId = hgncId;
        this.proteinId = proteinId;
        this.transcriptId = transcriptId;
        this.variantAllele = variantAllele;
        this.aminoAcids = aminoAcids;
        this.canonical = canonical;
        this.codons = codons;
        this.hgvsc = hgvsc;
        this.hgvsp = hgvsp;
        this.proteinEnd = proteinEnd;
        this.proteinStart = proteinStart;
        this.refseqTranscriptIds = refseqTranscriptIds;
    }

    /**
    * 
    * @return
    * The consequenceTerms
    */
    @JsonProperty("consequence_terms")
    public List<String> getConsequenceTerms() {
        return consequenceTerms;
    }

    /**
    * 
    * @param consequenceTerms
    * The consequence_terms
    */
    @JsonProperty("consequence_terms")
    public void setConsequenceTerms(List<String> consequenceTerms) {
        this.consequenceTerms = consequenceTerms;
    }

    /**
    * 
    * @return
    * The geneId
    */
    @JsonProperty("gene_id")
    public String getGeneId() {
        return geneId;
    }

    /**
    * 
    * @param geneId
    * The gene_id
    */
    @JsonProperty("gene_id")
    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    /**
    * 
    * @return
    * The geneSymbol
    */
    @JsonProperty("gene_symbol")
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
    * 
    * @param geneSymbol
    * The gene_symbol
    */
    @JsonProperty("gene_symbol")
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
    * 
    * @return
    * The hgncId
    */
    @JsonProperty("hgnc_id")
    public String getHgncId() {
        return hgncId;
    }

    /**
    * 
    * @param hgncId
    * The hgnc_id
    */
    @JsonProperty("hgnc_id")
    public void setHgncId(String hgncId) {
        this.hgncId = hgncId;
    }

    /**
    * 
    * @return
    * The proteinId
    */
    @JsonProperty("protein_id")
    public String getProteinId() {
        return proteinId;
    }

    /**
    * 
    * @param proteinId
    * The protein_id
    */
    @JsonProperty("protein_id")
    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    /**
    * 
    * @return
    * The transcriptId
    */
    @JsonProperty("transcript_id")
    public String getTranscriptId() {
        return transcriptId;
    }

    /**
    * 
    * @param transcriptId
    * The transcript_id
    */
    @JsonProperty("transcript_id")
    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    /**
    * 
    * @return
    * The variantAllele
    */
    @JsonProperty("variant_allele")
    public String getVariantAllele() {
        return variantAllele;
    }

    /**
    * 
    * @param variantAllele
    * The variant_allele
    */
    @JsonProperty("variant_allele")
    public void setVariantAllele(String variantAllele) {
        this.variantAllele = variantAllele;
    }

    /**
    * 
    * @return
    * The aminoAcids
    */
    @JsonProperty("amino_acids")
    public String getAminoAcids() {
        return aminoAcids;
    }

    /**
    * 
    * @param aminoAcids
    * The amino_acids
    */
    @JsonProperty("amino_acids")
    public void setAminoAcids(String aminoAcids) {
        this.aminoAcids = aminoAcids;
    }

    /**
    * 
    * @return
    * The canonical
    */
    @JsonProperty("canonical")
    public String getCanonical() {
        return canonical;
    }

    /**
    * 
    * @param canonical
    * The canonical
    */
    @JsonProperty("canonical")
    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    /**
    * 
    * @return
    * The codons
    */
    @JsonProperty("codons")
    public String getCodons() {
        return codons;
    }

    /**
    * 
    * @param codons
    * The codons
    */
    @JsonProperty("codons")
    public void setCodons(String codons) {
        this.codons = codons;
    }

    /**
    * 
    * @return
    * The hgvsc
    */
    @JsonProperty("hgvsc")
    public String getHgvsc() {
        return hgvsc;
    }

    /**
    * 
    * @param hgvsc
    * The hgvsc
    */
    @JsonProperty("hgvsc")
    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    /**
    * 
    * @return
    * The hgvsp
    */
    @JsonProperty("hgvsp")
    public String getHgvsp() {
        return hgvsp;
    }

    /**
    * 
    * @param hgvsp
    * The hgvsp
    */
    @JsonProperty("hgvsp")
    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    /**
    * 
    * @return
    * The proteinEnd
    */
    @JsonProperty("protein_end")
    public String getProteinEnd() {
        return proteinEnd;
    }

    /**
    * 
    * @param proteinEnd
    * The protein_end
    */
    @JsonProperty("protein_end")
    public void setProteinEnd(String proteinEnd) {
        this.proteinEnd = proteinEnd;
    }

    /**
    * 
    * @return
    * The proteinStart
    */
    @JsonProperty("protein_start")
    public String getProteinStart() {
        return proteinStart;
    }

    /**
    * 
    * @param proteinStart
    * The protein_start
    */
    @JsonProperty("protein_start")
    public void setProteinStart(String proteinStart) {
        this.proteinStart = proteinStart;
    }

    /**
    * 
    * @return
    * The refseqTranscriptIds
    */
    @JsonProperty("refseq_transcript_ids")
    public List<String> getRefseqTranscriptIds() {
        return refseqTranscriptIds;
    }

    /**
    * 
    * @param refseqTranscriptIds
    * The refseq_transcript_ids
    */
    @JsonProperty("refseq_transcript_ids")
    public void setRefseqTranscriptIds(List<String> refseqTranscriptIds) {
        this.refseqTranscriptIds = refseqTranscriptIds;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}