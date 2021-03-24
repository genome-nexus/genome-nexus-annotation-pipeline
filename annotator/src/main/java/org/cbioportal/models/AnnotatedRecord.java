/*
 * Copyright (c) 2016 - 2020 Memorial Sloan-Kettering Cancer Center.
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

public class AnnotatedRecord extends MutationRecord {

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
    protected String exon;
    protected String gnomadAlleleFrequency;
    protected String gnomadAlleleFrequencyAFR;
    protected String gnomadAlleleFrequencyAMR;
    protected String gnomadAlleleFrequencyASJ;
    protected String gnomadAlleleFrequencyEAS;
    protected String gnomadAlleleFrequencyFIN;
    protected String gnomadAlleleFrequencyNFE;
    protected String gnomadAlleleFrequencyOTH;
    protected String gnomadAlleleFrequencySAS;
    protected String annotationStatus;

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
        String IGNORE_Genome_Nexus_Original_Chromosome,
        String IGNORE_Genome_Nexus_Original_Start_Position,
        String IGNORE_Genome_Nexus_Original_End_Position,
        String IGNORE_Genome_Nexus_Original_Reference_Allele,
        String IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele1,
        String IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele2,
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
        String exon,
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
            IGNORE_Genome_Nexus_Original_Chromosome,
            IGNORE_Genome_Nexus_Original_Start_Position,
            IGNORE_Genome_Nexus_Original_End_Position,
            IGNORE_Genome_Nexus_Original_Reference_Allele,
            IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele1,
            IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele2,
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
        this.exon = exon;
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
        this.IGNORE_Genome_Nexus_Original_Chromosome = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_CHROMOSOME();
        this.IGNORE_Genome_Nexus_Original_Start_Position = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_START_POSITION();
        this.IGNORE_Genome_Nexus_Original_End_Position = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_END_POSITION();
        this.IGNORE_Genome_Nexus_Original_Reference_Allele = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_REFERENCE_ALLELE();
        this.IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele1 = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_TUMOR_SEQ_ALLELE1();
        this.IGNORE_Genome_Nexus_Original_Tumor_Seq_Allele2 = mRecord.getIGNORE_GENOME_NEXUS_ORIGINAL_TUMOR_SEQ_ALLELE2();
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
        this.exon = additionalProperties.get("exon") != null ? additionalProperties.get("exon") : "";
        this.additionalProperties = additionalProperties;
    }

    public void setGnomadFields(String gnomadAlleleFrequency,
        String gnomadAlleleFrequencyAFR,
        String gnomadAlleleFrequencyAMR,
        String gnomadAlleleFrequencyASJ,
        String gnomadAlleleFrequencyEAS,
        String gnomadAlleleFrequencyFIN,
        String gnomadAlleleFrequencyNFE,
        String gnomadAlleleFrequencyOTH,
        String gnomadAlleleFrequencySAS) {
        header.add("gnomAD_AF");
        header.add("gnomAD_AFR_AF");
        header.add("gnomAD_AMR_AF");
        header.add("gnomAD_ASJ_AF");
        header.add("gnomAD_EAS_AF");
        header.add("gnomAD_FIN_AF");
        header.add("gnomAD_NFE_AF");
        header.add("gnomAD_OTH_AF");
        header.add("gnomAD_SAS_AF");
        this.gnomadAlleleFrequency = gnomadAlleleFrequency;
        this.gnomadAlleleFrequencyAFR = gnomadAlleleFrequencyAFR;
        this.gnomadAlleleFrequencyAMR = gnomadAlleleFrequencyAMR;
        this.gnomadAlleleFrequencyASJ = gnomadAlleleFrequencyASJ;
        this.gnomadAlleleFrequencyEAS = gnomadAlleleFrequencyEAS;
        this.gnomadAlleleFrequencyFIN = gnomadAlleleFrequencyFIN;
        this.gnomadAlleleFrequencyNFE = gnomadAlleleFrequencyNFE;
        this.gnomadAlleleFrequencyOTH = gnomadAlleleFrequencyOTH;
        this.gnomadAlleleFrequencySAS = gnomadAlleleFrequencySAS;
    }

    public void setPolyphenFields(String polyphenPrediction,
            String polyphenScore) {
        addAdditionalProperty("Polyphen_Prediction", polyphenPrediction);
        addAdditionalProperty("Polyphen_Score", polyphenScore);
    }

    public void setSiftFields(String siftPrediction,
            String siftScore) {
        addAdditionalProperty("SIFT_Prediction", siftPrediction);
        addAdditionalProperty("SIFT_Score", siftScore);
    }
    
    public void setMutationAssessorFields(String maFunctionalImpact,
            String maFunctionalImpactScore,
            String maLinkMSA,
            String maLinkPDB) {
        addAdditionalProperty("MA:FImpact", maFunctionalImpact);
        addAdditionalProperty("MA:FIS", maFunctionalImpactScore);
        addAdditionalProperty("MA:link.MSA", maLinkMSA);
        addAdditionalProperty("MA:link.PDB", maLinkPDB);
    }

    public void setNucleotideContextFields(String refTri, String varTri) {
        addAdditionalProperty("Ref_Tri", refTri);
        addAdditionalProperty("Var_Tri", varTri);
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

    public String getEXON_NUMBER() {
        return this.exon;
    }

    public void setEXON_NUMBER(String exon) {
        this.exon = exon;
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

    public String getGNOMAD_AF() {
        return this.gnomadAlleleFrequency;
    }

    public void setGNOMAD_AF(String gnomadAlleleFrequency) {
        this.gnomadAlleleFrequency = gnomadAlleleFrequency;
    }

    public String getGNOMAD_AFR_AF() {
        return this.gnomadAlleleFrequencyAFR;
    }

    public void setGNOMAD_AFR_AF(String gnomadAlleleFrequencyAFR) {
        this.gnomadAlleleFrequencyAFR = gnomadAlleleFrequencyAFR;
    }

    public String getGNOMAD_AMR_AF() {
        return this.gnomadAlleleFrequencyAMR;
    }

    public void setGNOMAD_AMR_AF(String gnomadAlleleFrequencyAMR) {
        this.gnomadAlleleFrequencyAMR = gnomadAlleleFrequencyAMR;
    }

    public String getGNOMAD_ASJ_AF() {
        return this.gnomadAlleleFrequencyASJ;
    }

    public void setGNOMAD_ASJ_AF(String gnomadAlleleFrequencyASJ) {
        this.gnomadAlleleFrequencyASJ = gnomadAlleleFrequencyASJ;
    }

    public String getGNOMAD_EAS_AF() {
        return this.gnomadAlleleFrequencyEAS;
    }

    public void setGNOMAD_EAS_AF(String gnomadAlleleFrequencyEAS) {
        this.gnomadAlleleFrequencyEAS = gnomadAlleleFrequencyEAS;
    }

    public String getGNOMAD_FIN_AF() {
        return this.gnomadAlleleFrequencyFIN;
    }

    public void setGNOMAD_FIN_AF(String gnomadAlleleFrequencyFIN) {
        this.gnomadAlleleFrequencyFIN = gnomadAlleleFrequencyFIN;
    }

    public String getGNOMAD_NFE_AF() {
        return this.gnomadAlleleFrequencyNFE;
    }

    public void setGNOMAD_NFE_AF(String gnomadAlleleFrequencyNFE) {
        this.gnomadAlleleFrequencyNFE = gnomadAlleleFrequencyNFE;
    }

    public String getGNOMAD_OTH_AF() {
        return this.gnomadAlleleFrequencyOTH;
    }

    public void setGNOMAD_OTH_AF(String gnomadAlleleFrequencyOTH) {
        this.gnomadAlleleFrequencyOTH = gnomadAlleleFrequencyOTH;
    }

    public String getGNOMAD_SAS_AF() {
        return this.gnomadAlleleFrequencySAS;
    }

    public void setGNOMAD_SAS_AF(String gnomadAlleleFrequencySAS) {
        this.gnomadAlleleFrequencySAS = gnomadAlleleFrequencySAS;
    }

    public String getANNOTATION_STATUS() {
        return this.annotationStatus != null ? this.annotationStatus : "NOT_ATTEMPTED";
    }

    public void setANNOTATION_STATUS(String annotationStatus) {
        this.annotationStatus = annotationStatus;
    }

    private void addAnnotatedFieldsToHeader() {
        header.add("HGVSc");
        header.add("HGVSp");
        header.add("HGVSp_Short");
        header.add("Transcript_ID");
        header.add("RefSeq");
        header.add("Protein_position");
        header.add("Codons");
        header.add("Exon_Number");
        header.add(header.indexOf("Variant_Classification"), "Consequence");
    }
}
