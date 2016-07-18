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
package org.cbioportal.genomeNexus.models;

import org.cbioportal.genomeNexus.annotator.Annotator;

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
    protected static Map<String, String> additionalProperties = new LinkedHashMap<String, String>();

    public AnnotatedRecord() {}
    
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
        Map additionalProperties
    ) {        
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
        this.tumorSeqAllele2 =  tumorSeqAllele2;
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
        this.hgvsc = hgvsc;
        this.hgvsp = hgvsp;
        this.hgvspShort = hgvspShort;
        this.transcriptId = transcriptId;
        this.refSeq = refSeq;
        this.proteinPosStart = proteinPosStart;
        this.proteinPosEnd = proteinPosEnd;
        this.codonChange = codonChange;
        this.additionalProperties = additionalProperties;
    }  

    public String getHgvsc() {
        return this.hgvsc;
    }
    
    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }   
    
    public String getHgvsp() {
        return this.hgvsp;
    }
    
    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    } 

    public String getHgvsp_Short() {
        return this.hgvspShort;
    }
    
    public void setHgvsp_Short(String hgvspShort) {
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
    
    public String getProtein_Position() {
        return this.proteinPosStart;
    }
    
    public void setProtein_Position(String proteinPosStart) {
        this.proteinPosStart = proteinPosStart;
    }
    
    public String getCodons() {
        return this.codonChange;
    }
    
    public void setCodons(String codonChange) {
        this.codonChange = codonChange;
    }        
    
    public static List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        
        fieldNames.add("Hugo_Symbol");
        fieldNames.add("Entrez_Gene_Id");
        fieldNames.add("Center");
        fieldNames.add("NCBI_Build");
        fieldNames.add("Chromosome");
        fieldNames.add("Start_Position");
        fieldNames.add("End_Position");
        fieldNames.add("Strand");
        fieldNames.add("Variant_Classification");
        fieldNames.add("Variant_Type");
        fieldNames.add("Reference_Allele");
        fieldNames.add("Tumor_Seq_Allele1");
        fieldNames.add("Tumor_Seq_Allele2");
        fieldNames.add("dbSNP_RS");
        fieldNames.add("dbSNP_Val_Status");
        fieldNames.add("Tumor_Sample_Barcode");
        fieldNames.add("Matched_Norm_Sample_Barcode");
        fieldNames.add("Match_Norm_Seq_Allele1");
        fieldNames.add("Match_Norm_Seq_Allele2");
        fieldNames.add("Tumor_Validation_Allele1");
        fieldNames.add("Tumor_Validation_Allele2");
        fieldNames.add("Match_Norm_Validation_Allele1");
        fieldNames.add("Match_Norm_Validation_Allele2");
        fieldNames.add("Verification_Status");
        fieldNames.add("Validation_Status");
        fieldNames.add("Mutation_Status");
        fieldNames.add("Sequencing_Phase");
        fieldNames.add("Sequence_Source");
        fieldNames.add("Validation_Method");
        fieldNames.add("Score");
        fieldNames.add("BAM_File");
        fieldNames.add("Sequencer");
        fieldNames.add("t_ref_count");
        fieldNames.add("t_alt_count");
        fieldNames.add("n_ref_count");
        fieldNames.add("n_alt_count");
        fieldNames.add("Hgvsc");
        fieldNames.add("Hgvsp");
        fieldNames.add("Hgvsp_Short");
        fieldNames.add("Transcript_ID");
        fieldNames.add("RefSeq");
        fieldNames.add("Protein_Position");
        fieldNames.add("Codons");
                            
        return fieldNames;
    }
    
    public static List<String> getHeader() {
        List<String> fieldNames = getFieldNames();
        for (String field : additionalProperties.keySet()) {
            fieldNames.add(field);
        }
        return fieldNames;
    }
    
}
