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
    
    static {                       
        HEADER.add("HGVSc");
        HEADER.add("HGVSp");
        HEADER.add("HGVSp_Short");
        HEADER.add("Transcript_ID");
        HEADER.add("RefSeq");
        HEADER.add("Protein_position");
        HEADER.add("Codons");        
    }       

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
}
