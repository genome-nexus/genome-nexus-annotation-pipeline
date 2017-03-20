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
import org.springframework.util.StringUtils;

/**
 *
 * @author Zachary Heins
 */
public class MutationRecord {
    protected String hugoSymbol;
    protected String entrezGeneId;
    protected String center;
    protected String ncbiBuild;
    protected String chromosome;
    protected String startPosition;
    protected String endPosition;
    protected String strand;
    protected String variantClassification;
    protected String variantType;
    protected String referenceAllele;
    protected String tumorSeqAllele1;
    protected String tumorSeqAllele2;
    protected String dbSnpRs;
    protected String dbSnpValStatus;
    protected String tumorSampleBarcode;
    protected String matchedNormSampleBarcode;
    protected String matchedNormSeqAllele1;
    protected String matchedNormSeqAllele2;
    protected String tumorValidationAllele1;
    protected String tumorValidationAllele2;
    protected String matchNormValidationAllele1;
    protected String matchNormValidationAllele2;
    protected String verificationStatus;
    protected String validationStatus;
    protected String mutationStatus;
    protected String sequencingPhase;
    protected String sequencingSource;
    protected String validationMethod;
    protected String score;
    protected String bamFile;
    protected String sequencer;
    protected String tumorSampleUUID;
    protected String matchedNormSampleUUID;
    protected String tRefCount;
    protected String tAltCount;
    protected String nRefCount;
    protected String nAltCount;
    protected Map<String, String> additionalProperties = new LinkedHashMap<>();
    protected List<String> header = new ArrayList<>();

    public MutationRecord() {
        initHeader();
    }

    public MutationRecord(String hugoSymbol, String entrezGeneId, String center, String ncbiBuild,
            String chromosome, String startPosition, String endPosition, String strand, String variantClassification,
            String variantType, String referenceAllele, String tumorSeqAllele1, String tumorSeqAllele2, String dbSnpRs,
            String dbSnpValStatus, String tumorSampleBarcode, String matchedNormSampleBarcode,
            String matchedNormSeqAllele1, String matchedNormSeqAllele2, String tumorValidationAllele1,
            String tumorValidationAllele2, String matchNormValidationAllele1, String matchNormValidationAllele2,
            String verificationStatus, String validationStatus, String mutationStatus, String sequencingPhase,
            String sequencingSource, String validationMethod, String score, String bamFile, String sequencer,
            String tumorSampleUUID, String matchedNormSampleUUID, String tRefCount, String tAltCount,
            String nRefCount, String nAltCount, Map<String, String> additionalProperties) {

        this.hugoSymbol = hugoSymbol;
        this.entrezGeneId = entrezGeneId;
        this.center = center;
        this.ncbiBuild = ncbiBuild;
        this.chromosome = chromosome;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.strand = strand;
        this.variantClassification = variantClassification;
        this.variantType = variantType;
        this.referenceAllele = referenceAllele;
        this.tumorSeqAllele1 = tumorSeqAllele1;
        this.tumorSeqAllele2 = tumorSeqAllele2;
        this.dbSnpRs = dbSnpRs;
        this.dbSnpValStatus = dbSnpValStatus;
        this.tumorSampleBarcode = tumorSampleBarcode;
        this.matchedNormSampleBarcode = matchedNormSampleBarcode;
        this.matchedNormSeqAllele1 = matchedNormSeqAllele1;
        this.matchedNormSeqAllele2 = matchedNormSeqAllele2;
        this.tumorValidationAllele1 = tumorValidationAllele1;
        this.tumorValidationAllele2 = tumorValidationAllele2;
        this.matchNormValidationAllele1 = matchNormValidationAllele1;
        this.matchNormValidationAllele2 = matchNormValidationAllele2;
        this.verificationStatus = verificationStatus;
        this.validationStatus = validationStatus;
        this.mutationStatus = mutationStatus;
        this.sequencingPhase = sequencingPhase;
        this.sequencingSource = sequencingSource;
        this.validationMethod = validationMethod;
        this.score = score;
        this.bamFile = bamFile;
        this.sequencer = sequencer;
        this.tumorSampleUUID = tumorSampleUUID;
        this.matchedNormSampleUUID = matchedNormSampleUUID;
        this.tRefCount = tRefCount;
        this.tAltCount = tAltCount;
        this.nRefCount = nRefCount;
        this.nAltCount = nAltCount;
        this.additionalProperties = additionalProperties;
        initHeader();
    }

    public String getHugo_Symbol() {
        return this.hugoSymbol == null ? "" : this.hugoSymbol;
    }

    public void setHugo_Symbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getEntrez_Gene_Id() {
        return this.entrezGeneId == null ? "" : this.entrezGeneId;
    }

    public void setEntrez_Gene_Id(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getCenter() {
        return this.center == null ? "" : this.center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getNCBI_Build() {
        return this.ncbiBuild == null ? "" : this.ncbiBuild;
    }

    public void setNCBI_Build(String ncbiBuild) {
        this.ncbiBuild = ncbiBuild;
    }

    public String getChromosome() {
        return this.chromosome == null ? "" : this.chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getStart_Position() {
        return this.startPosition == null ? "" : this.startPosition;
    }

    public void setStart_Position(String startPosition) {
        this.startPosition = startPosition;
    }

    public String getEnd_Position() {
        return this.endPosition == null ? "" : this.endPosition;
    }

    public void setEnd_Position(String endPosition) {
        this.endPosition = endPosition;
    }

    public String getStrand() {
        return this.strand == null ? "" : this.strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getVariant_Classification() {
        return this.variantClassification == null ? "" : this.variantClassification;
    }

    public void setVariant_Classification(String variantClassification) {
        this.variantClassification = variantClassification;
    }

    public String getVariant_Type() {
        return this.variantType == null ? "" : this.variantType;
    }

    public void setVariant_Type(String variantType) {
        this.variantType = variantType;
    }

    public String getReference_Allele() {
        return this.referenceAllele == null ? "" : this.referenceAllele;
    }

    public void setReference_Allele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public String getTumor_Seq_Allele1() {
        return this.tumorSeqAllele1 == null ? "" : this.tumorSeqAllele1;
    }

    public void setTumor_Seq_Allele1(String tumorSeqAllele1) {
        this.tumorSeqAllele1 = tumorSeqAllele1;
    }

    public String getTumor_Seq_Allele2() {
        return this.tumorSeqAllele2 == null ? "" : this.tumorSeqAllele2;
    }

    public void setTumor_Seq_Allele2(String tumorSeqAllele2) {
        this.tumorSeqAllele2 = tumorSeqAllele2;
    }

    public String getdbSNP_RS() {
        return this.dbSnpRs == null ? "" : this.dbSnpRs;
    }

    public void setdbSNP_RS(String dbSnpRs) {
        this.dbSnpRs = dbSnpRs;
    }

    public String getdbSNP_Val_Status() {
        return this.dbSnpValStatus == null ? "" : this.dbSnpValStatus;
    }

    public void setdbSNP_Val_Status(String dbSnpValStatus) {
        this.dbSnpValStatus = dbSnpValStatus;
    }

    public String getTumor_Sample_Barcode() {
        return this.tumorSampleBarcode == null ? "" : this.tumorSampleBarcode;
    }

    public void setTumor_Sample_Barcode(String tumorSampleBarcode) {
        this.tumorSampleBarcode = tumorSampleBarcode;
    }

    public String getMatched_Norm_Sample_Barcode() {
        return this.matchedNormSampleBarcode == null ? "" : this.matchedNormSampleBarcode;
    }

    public void setMatched_Norm_Sample_Barcode(String matchedNormSampleBarcode) {
        this.matchedNormSampleBarcode = matchedNormSampleBarcode;
    }

    public String getMatch_Norm_Seq_Allele1() {
        return this.matchNormValidationAllele1 == null ? "" : this.matchedNormSeqAllele1;
    }

    public void setMatch_Norm_Seq_Allele1(String matchedNormSeqAllele1) {
        this.matchedNormSeqAllele1 = matchedNormSeqAllele1;
    }

    public String getMatch_Norm_Seq_Allele2() {
        return this.matchNormValidationAllele2 == null ? "" : this.matchedNormSeqAllele2;
    }

    public void setMatch_Norm_Seq_Allele2(String matchedNormSeqAllele2) {
        this.matchedNormSeqAllele2 = matchedNormSeqAllele2;
    }

    public String getTumor_Validation_Allele1() {
        return this.tumorValidationAllele1 == null ? "" : this.tumorValidationAllele1;
    }

    public void setTumor_Validation_Allele1(String tumorValidationAllele1) {
        this.tumorValidationAllele1 = tumorValidationAllele1;
    }

    public String getTumor_Validation_Allele2() {
        return this.tumorValidationAllele2 == null ? "" : this.tumorValidationAllele2;
    }

     public void setTumor_Validation_Allele2(String tumorValidationAllele2) {
        this.tumorValidationAllele2 = tumorValidationAllele2;
    }

    public String getMatch_Norm_Validation_Allele1() {
        return this.matchNormValidationAllele1 == null ? "" : this.matchNormValidationAllele1;
    }

    public void setMatch_Norm_Validation_Allele1(String matchNormValidationAllele1) {
        this.matchNormValidationAllele1 = matchNormValidationAllele1;
    }

    public String getMatch_Norm_Validation_Allele2() {
        return this.matchNormValidationAllele2 == null ? "" : this.matchNormValidationAllele2;
    }

    public void setMatch_Norm_Validation_Allele2(String matchNormValidationAllele2) {
        this.matchNormValidationAllele2 = matchNormValidationAllele2;
    }

    public String getVerification_Status() {
        return this.verificationStatus == null ? "" : this.verificationStatus;
    }

    public void setVerification_Status(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getValidation_Status() {
        return this.validationStatus == null ? "" : this.validationStatus;
    }

    public void setValidation_Status(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getMutation_Status() {
        return this.mutationStatus == null ? "" : this.mutationStatus;
    }

    public void setMutation_Status(String mutationStatus) {
        this.mutationStatus = mutationStatus;
    }

    public String getSequencing_Phase() {
        return this.sequencingPhase == null ? "" : this.sequencingPhase;
    }

    public void setSequencing_Phase(String sequencingPhase) {
        this.sequencingPhase = sequencingPhase;
    }

    public String getSequence_Source() {
        return this.sequencingSource == null ? "" : this.sequencingSource;
    }

    public void setSequence_Source(String sequencingSource) {
        this.sequencingSource = sequencingSource;
    }

    public String getValidation_Method() {
        return this.validationMethod == null ? "" : validationMethod;
    }

    public void setValidation_Method(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getScore() {
        return this.score == null ? "" : this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getBAM_File() {
        return this.bamFile == null ? "" : this.bamFile;
    }

    public void setBAM_File(String bamFile) {
        this.bamFile = bamFile;
    }

    public String getSequencer() {
        return this.sequencer == null ? "" : this.sequencer;
    }

    public void setSequencer(String sequencer) {
        this.sequencer = sequencer;
    }

    public String getTumor_Sample_UUID() {
        return this.tumorSampleUUID == null ? "" : this.tumorSampleUUID;
    }

    public void setTumor_Sample_UUID(String tumorSampleUUID) {
        this.tumorSampleUUID = tumorSampleUUID;
    }

    public String getMatched_Norm_Sample_UUID() {
        return this.matchedNormSampleUUID == null ? "" : this.matchedNormSampleUUID;
    }

    public void setMatched_Norm_Sample_UUID(String matchedNormSampleUUID) {
        this.matchedNormSampleUUID = matchedNormSampleUUID;
    }

    public String gett_ref_count() {
        return this.tRefCount == null ? "" : this.tRefCount;
    }

    public void sett_ref_count(String tRefCount) {
        this.tRefCount = tRefCount;
    }

    public String gett_alt_count() {
        return this.tAltCount == null ? "" : this.tAltCount;
    }

    public void sett_alt_count(String tAltCount) {
        this.tAltCount = tAltCount;
    }

    public String getn_ref_count() {
        return this.nRefCount == null ? "" : this.nRefCount;
    }

    public void setn_ref_count(String nRefCount) {
        this.nRefCount = nRefCount;
    }

    public String getn_alt_count() {
        return this.nAltCount == null ? "" : this.nAltCount;
    }

    public void setn_alt_count(String nAltCount) {
        this.nAltCount = nAltCount;
    }

    public void addAdditionalProperty(String property, String value) {
        this.additionalProperties.put(property, value);
    }

    public Map<String, String> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<String> getHeaderWithAdditionalFields() {
        List<String> headerWithAdditionalFields = new ArrayList<>();
        headerWithAdditionalFields.addAll(header);

        for (String field : additionalProperties.keySet()) {
            if (!headerWithAdditionalFields.contains(field)) {
                headerWithAdditionalFields.add(field);
            }
        }
        return headerWithAdditionalFields;
    }

    public List<String> getHeader() {
        return  header;
    }

    public void initHeader() {
        header.add("Hugo_Symbol");
        header.add("Entrez_Gene_Id");
        header.add("Center");
        header.add("NCBI_Build");
        header.add("Chromosome");
        header.add("Start_Position");
        header.add("End_Position");
        header.add("Strand");
        header.add("Variant_Classification");
        header.add("Variant_Type");
        header.add("Reference_Allele");
        header.add("Tumor_Seq_Allele1");
        header.add("Tumor_Seq_Allele2");
        header.add("dbSNP_RS");
        header.add("dbSNP_Val_Status");
        header.add("Tumor_Sample_Barcode");
        header.add("Matched_Norm_Sample_Barcode");
        header.add("Match_Norm_Seq_Allele1");
        header.add("Match_Norm_Seq_Allele2");
        header.add("Tumor_Validation_Allele1");
        header.add("Tumor_Validation_Allele2");
        header.add("Match_Norm_Validation_Allele1");
        header.add("Match_Norm_Validation_Allele2");
        header.add("Verification_Status");
        header.add("Validation_Status");
        header.add("Mutation_Status");
        header.add("Sequencing_Phase");
        header.add("Sequence_Source");
        header.add("Validation_Method");
        header.add("Score");
        header.add("BAM_File");
        header.add("Sequencer");
        header.add("t_ref_count");
        header.add("t_alt_count");
        header.add("n_ref_count");
        header.add("n_alt_count");
    }
}
