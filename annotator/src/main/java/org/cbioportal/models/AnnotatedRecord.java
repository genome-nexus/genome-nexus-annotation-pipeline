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

import java.util.*;

/**
 *
 * @author Zachary Heins
 */

public class AnnotatedRecord extends MutationRecord{
    protected String hgvsc;
    protected String hgvsp;
    protected String hgvspShort;
    protected String transcriptId;
    protected String refSeq;
    protected String proteinPosStart;
    protected String proteinPosEnd;
    protected String codonChange;
    protected String hotspot;
    protected String consequence;

    public AnnotatedRecord() {
        addAnnotatedFieldsToHeader();
    }

    public AnnotatedRecord(String hugoSymbol,
        String entrezGeneId,
        String center,
        String ncbiBuild,
        String chromosome,
        String startPosition,
        String endPosition,
        String strand,
        String variantClassification,
        String variantType,
        String referenceAllele,
        String tumorSeqAllele1,
        String tumorSeqAllele2,
        String dbSnpRs,
        String dbSnpValStatus,
        String tumorSampleBarcode,
        String matchedNormSampleBarcode,
        String matchedNormSeqAllele1,
        String matchedNormSeqAllele2,
        String tumorValidationAllele1,
        String tumorValidationAllele2,
        String matchNormValidationAllele1,
        String matchNormValidationAllele2,
        String verificationStatus,
        String validationStatus,
        String mutationStatus,
        String sequencingPhase,
        String sequencingSource,
        String validationMethod,
        String score,
        String bamFile,
        String sequencer,
        String tumorSampleUUID,
        String matchedNormSampleUUID,
        String tRefCount,
        String tAltCount,
        String nRefCount,
        String nAltCount,
        String hgvsc,
        String hgvsp,
        String hgvspShort,
        String transcriptId,
        String refSeq,
        String proteinPosStart,
        String proteinPosEnd,
        String codonChange,
        String hotspot,
        String consequence,
        Map<String, String> additionalProperties
    ) {
        super(hugoSymbol,
            entrezGeneId,
            center,
            ncbiBuild,
            chromosome,
            startPosition,
            endPosition,
            strand,
            variantClassification,
            variantType,
            referenceAllele,
            tumorSeqAllele1,
            tumorSeqAllele2,
            dbSnpRs,
            dbSnpValStatus,
            tumorSampleBarcode,
            matchedNormSampleBarcode,
            matchedNormSeqAllele1,
            matchedNormSeqAllele2,
            tumorValidationAllele1,
            tumorValidationAllele2,
            matchNormValidationAllele1,
            matchNormValidationAllele2,
            verificationStatus,
            validationStatus,
            mutationStatus,
            sequencingPhase,
            sequencingSource,
            validationMethod,
            score,
            bamFile,
            sequencer,
            tumorSampleUUID,
            matchedNormSampleUUID,
            tRefCount,
            tAltCount,
            nRefCount,
            nAltCount,
            additionalProperties);
        this.hgvsc = hgvsc;
        this.hgvsp = hgvsp;
        this.hgvspShort = hgvspShort;
        this.transcriptId = transcriptId;
        this.refSeq = refSeq;
        this.proteinPosStart = proteinPosStart;
        this.proteinPosEnd = proteinPosEnd;
        this.codonChange = codonChange;
        this.hotspot = hotspot;
        this.consequence = consequence;
        addAnnotatedFieldsToHeader();
    }
    
    public AnnotatedRecord(MutationRecord mRecord) {
        Map<String, String> additionalProperties = mRecord.getAdditionalProperties();
        this.hugoSymbol = mRecord.getHugo_Symbol();
        this.entrezGeneId = mRecord.getEntrez_Gene_Id();
        this.center = mRecord.getCenter();
        this.ncbiBuild = mRecord.getNCBI_Build();
        this.chromosome = mRecord.getChromosome();
        this.startPosition = mRecord.getStart_Position();
        this.endPosition = mRecord.getEnd_Position();
        this.strand = mRecord.getStrand();
        this.variantClassification = mRecord.getVariant_Classification();
        this.variantType = mRecord.getVariant_Type();
        this.referenceAllele = mRecord.getReference_Allele();
        this.tumorSeqAllele1 = mRecord.getTumor_Seq_Allele1();
        this.tumorSeqAllele2 = mRecord.getTumor_Seq_Allele2();
        this.dbSnpRs = mRecord.getdbSNP_RS();
        this.dbSnpValStatus = mRecord.getdbSNP_Val_Status();
        this.tumorSampleBarcode = mRecord.getTumor_Sample_Barcode();
        this.matchedNormSampleBarcode = mRecord.getMatched_Norm_Sample_Barcode();
        this.matchedNormSeqAllele1 = mRecord.getMatch_Norm_Seq_Allele1();
        this.matchedNormSeqAllele2 = mRecord.getMatch_Norm_Seq_Allele2();
        this.tumorValidationAllele1 = mRecord.getTumor_Validation_Allele1();
        this.tumorValidationAllele2 = mRecord.getTumor_Validation_Allele2();
        this.matchNormValidationAllele1 = mRecord.getMatch_Norm_Validation_Allele1();
        this.matchNormValidationAllele2 = mRecord.getMatch_Norm_Validation_Allele2();
        this.verificationStatus = mRecord.getVerification_Status();
        this.validationStatus = mRecord.getValidation_Status();
        this.mutationStatus = mRecord.getMutation_Status();
        this.sequencingPhase = mRecord.getSequencing_Phase();
        this.sequencingSource = mRecord.getSequence_Source();
        this.validationMethod = mRecord.getValidation_Method();
        this.score = mRecord.getScore();
        this.bamFile = mRecord.getBAM_File();
        this.sequencer = mRecord.getSequencer();
        this.tumorSampleUUID = mRecord.getTumor_Sample_UUID();
        this.matchedNormSampleUUID = mRecord.getMatched_Norm_Sample_UUID();
        this.tRefCount = mRecord.gett_ref_count();
        this.tAltCount = mRecord.gett_alt_count();
        this.nRefCount = mRecord.getn_ref_count();
        this.nAltCount = mRecord.getn_alt_count();
        this.hgvsc = additionalProperties.get("HGVSc") != null ? additionalProperties.get("HGVSc") : "";
        this.hgvsp = additionalProperties.get("HGVSp") != null ? additionalProperties.get("HGVSp") : "";
        this.hgvspShort = additionalProperties.get("HGVSp_Short");
        this.transcriptId = additionalProperties.get("Transcript_ID") != null ? additionalProperties.get("Transcript_ID") : "";
        this.refSeq = additionalProperties.get("RefSeq") != null ? additionalProperties.get("RefSeq") : "";
        this.proteinPosStart = additionalProperties.get("Protein_Position") != null ? additionalProperties.get("Protein_Position") : "";
        this.proteinPosEnd = additionalProperties.get("Protein_Position") != null ? additionalProperties.get("Protein_Position") : "";
        this.codonChange = additionalProperties.get("Codons") != null ? additionalProperties.get("Codons") : "";
        this.hotspot = additionalProperties.get("Hotspot") != null ? additionalProperties.get("Hotspot") : "";
        this.consequence = additionalProperties.get("Consequence") != null ? additionalProperties.get("Consequence") : "";
        this.additionalProperties = additionalProperties;
    }

    public String getHGVSc() {
        return this.hgvsc;
    }

    public void setHGVSc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public String getHGVSp() {
        return this.hgvsp;
    }

    public void setHGVSp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public String getHGVSp_Short() {
        return this.hgvspShort;
    }

    public void setHGVSp_Short(String hgvspShort) {
        this.hgvspShort = hgvspShort;
    }

    public String getTranscript_ID() {
        return this.transcriptId;
    }

    public void setTranscript_ID(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getRefSeq() {
        return this.refSeq;
    }

    public void setRefSeq(String refSeq) {
        this.refSeq = refSeq;
    }

    public String getProtein_position() {
        return this.proteinPosStart;
    }

    public void setProtein_position(String proteinPosStart) {
        this.proteinPosStart = proteinPosStart;
    }

    public String getCodons() {
        return this.codonChange;
    }

    public void setCodons(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getHotspot() {
        return this.hotspot;
    }

    public void setHotspot(String hotspot) {
        this.hotspot = hotspot;
    }

    public String getConsequence() {
        return this.consequence;
    }

    public void setConsquence(String consequence) {
        this.consequence = consequence;
    }

    private void addAnnotatedFieldsToHeader() {
        header.add("HGVSc");
        header.add("HGVSp");
        header.add("HGVSp_Short");
        header.add("Transcript_ID");
        header.add("RefSeq");
        header.add("Protein_position");
        header.add("Codons");
        header.add("Hotspot");
        header.add(header.indexOf("Variant_Classification"), "Consequence");
    }
}
