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
    protected String proteinPosition;

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
        String proteinPosition,
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
        this.proteinPosition = proteinPosition;
        addAnnotatedFieldsToHeader();
    }
    
    public AnnotatedRecord(MutationRecord mRecord) {
        Map<String, String> additionalProperties = mRecord.getAdditionalProperties();
        this.hugoSymbol = mRecord.getHUGO_SYMBOL();
        this.entrezGeneId = mRecord.getENTREZ_GENE_ID();
        this.center = mRecord.getCENTER();
        this.ncbiBuild = mRecord.getNCBI_BUILD();
        this.chromosome = mRecord.getCHROMOSOME();
        this.startPosition = mRecord.getSTART_POSITION();
        this.endPosition = mRecord.getEND_POSITION();
        this.strand = mRecord.getSTRAND();
        this.variantClassification = mRecord.getVARIANT_CLASSIFICATION();
        this.variantType = mRecord.getVARIANT_TYPE();
        this.referenceAllele = mRecord.getREFERENCE_ALLELE();
        this.tumorSeqAllele1 = mRecord.getTUMOR_SEQ_ALLELE1();
        this.tumorSeqAllele2 = mRecord.getTUMOR_SEQ_ALLELE2();
        this.dbSnpRs = mRecord.getDBSNP_RS();
        this.dbSnpValStatus = mRecord.getDBSNP_VAL_STATUS();
        this.tumorSampleBarcode = mRecord.getTUMOR_SAMPLE_BARCODE();
        this.matchedNormSampleBarcode = mRecord.getMATCHED_NORM_SAMPLE_BARCODE();
        this.matchedNormSeqAllele1 = mRecord.getMATCH_NORM_SEQ_ALLELE1();
        this.matchedNormSeqAllele2 = mRecord.getMATCH_NORM_SEQ_ALLELE2();
        this.tumorValidationAllele1 = mRecord.getTUMOR_VALIDATION_ALLELE1();
        this.tumorValidationAllele2 = mRecord.getTUMOR_VALIDATION_ALLELE2();
        this.matchNormValidationAllele1 = mRecord.getMATCH_NORM_VALIDATION_ALLELE1();
        this.matchNormValidationAllele2 = mRecord.getMATCH_NORM_VALIDATION_ALLELE2();
        this.verificationStatus = mRecord.getVERIFICATION_STATUS();
        this.validationStatus = mRecord.getVALIDATION_STATUS();
        this.mutationStatus = mRecord.getMUTATION_STATUS();
        this.sequencingPhase = mRecord.getSEQUENCING_PHASE();
        this.sequencingSource = mRecord.getSEQUENCE_SOURCE();
        this.validationMethod = mRecord.getVALIDATION_METHOD();
        this.score = mRecord.getSCORE();
        this.bamFile = mRecord.getBAM_FILE();
        this.sequencer = mRecord.getSEQUENCER();
        this.tumorSampleUUID = mRecord.getTUMOR_SAMPLE_UUID();
        this.matchedNormSampleUUID = mRecord.getMATCHED_NORM_SAMPLE_UUID();
        this.tRefCount = mRecord.getT_REF_COUNT();
        this.tAltCount = mRecord.getT_ALT_COUNT();
        this.nRefCount = mRecord.getN_REF_COUNT();
        this.nAltCount = mRecord.getN_ALT_COUNT();
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
        this.proteinPosition = additionalProperties.get("Protein_position") != null ? additionalProperties.get("Protein_position") : "";
        this.additionalProperties = additionalProperties;
    }

    public String getHGVSC() {
        return this.hgvsc;
    }

    public void setHGVSC(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public String getHGVSP() {
        return this.hgvsp;
    }

    public void setHGVSP(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public String getHGVSP_SHORT() {
        return this.hgvspShort;
    }

    public void setHGVSP_SHORT(String hgvspShort) {
        this.hgvspShort = hgvspShort;
    }

    public String getTRANSCRIPT_ID() {
        return this.transcriptId;
    }

    public void setTRANSCRIPT_ID(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getREFSEQ() {
        return this.refSeq;
    }

    public void setREFSEQ(String refSeq) {
        this.refSeq = refSeq;
    }

    public String getPROTEIN_POSITION() {
        return this.proteinPosition;
    }

    public void setPROTEIN_POSITION(String proteinPosition) {
        this.proteinPosition = proteinPosition;
    }

    public String getCODONS() {
        return this.codonChange;
    }

    public void setCODONS(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getHOTSPOT() {
        return this.hotspot;
    }

    public void setHOTSPOT(String hotspot) {
        this.hotspot = hotspot;
    }

    public String getCONSEQUENCE() {
        return this.consequence;
    }

    public void setCONSEQUENCE(String consequence) {
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
