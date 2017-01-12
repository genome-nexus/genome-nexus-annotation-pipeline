/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.database.annotator.model;

/**
 *
 * @author heinsz
 */

public class MutationEvent {

    private Integer MUTATION_EVENT_ID;
    private String CHR;
    private Integer START_POSITION;
    private Integer END_POSITION;
    private String REFERENCE_ALLELE;
    private String TUMOR_SEQ_ALLELE;
    private String PROTEIN_CHANGE;
    private String ONCOTATOR_CODON_CHANGE;
    private Integer ONCOTATOR_PROTEIN_POS_START;
    private Integer ONCOTATOR_PROTEIN_POS_END;
    private String MUTATION_TYPE;

    public MutationEvent() {}

    public MutationEvent(Integer MUTATION_EVENT_ID, String CHR, Integer START_POSITION, Integer END_POSITION, String REFERENCE_ALLELE, String TUMOR_SEQ_ALLELE, String PROTEIN_CHANGE, String MUTATION_TYPE, String ONCOTATOR_CODON_CHANGE, Integer ONCOTATOR_PROTEIN_POS_START, Integer ONCOTATOR_PROTEIN_POS_END) {
        this.MUTATION_EVENT_ID = MUTATION_EVENT_ID;
        this.CHR = CHR;
        this.START_POSITION = START_POSITION;
        this.END_POSITION = END_POSITION;
        this.REFERENCE_ALLELE = REFERENCE_ALLELE;
        this.TUMOR_SEQ_ALLELE = TUMOR_SEQ_ALLELE;
        this.PROTEIN_CHANGE = PROTEIN_CHANGE;
        this.MUTATION_TYPE = MUTATION_TYPE;
        this.ONCOTATOR_CODON_CHANGE = ONCOTATOR_CODON_CHANGE;
        this.ONCOTATOR_PROTEIN_POS_START = ONCOTATOR_PROTEIN_POS_START;
        this.ONCOTATOR_PROTEIN_POS_END = ONCOTATOR_PROTEIN_POS_END;
    }

    public Integer getMUTATION_EVENT_ID() {
        return MUTATION_EVENT_ID;
    }

    public void setMUTATION_EVENT_ID(Integer MUTATION_EVENT_ID) {
        this.MUTATION_EVENT_ID = MUTATION_EVENT_ID;
    }

    public String getCHR() {
        return CHR;
    }

    public void setCHR(String CHR) {
        this.CHR = CHR;
    }

    public Integer getSTART_POSITION() {
        return START_POSITION;
    }

    public void setSTART_POSITION(Integer START_POSITION) {
        this.START_POSITION = START_POSITION;
    }

    public Integer getEND_POSITION() {
        return END_POSITION;
    }

    public void setEND_POSITION(Integer END_POSITION) {
        this.END_POSITION = END_POSITION;
    }

    public String getREFERENCE_ALLELE() {
        return REFERENCE_ALLELE;
    }

    public void setREFERENCE_ALLELE(String REFERENCE_ALLELE) {
        this.REFERENCE_ALLELE = REFERENCE_ALLELE;
    }

    public String getTUMOR_SEQ_ALLELE() {
        return TUMOR_SEQ_ALLELE;
    }

    public void setTUMOR_SEQ_ALLELE(String TUMOR_SEQ_ALLELE) {
        this.TUMOR_SEQ_ALLELE = TUMOR_SEQ_ALLELE;
    }

    public String getPROTEIN_CHANGE() {
        return PROTEIN_CHANGE;
    }

    public void setPROTEIN_CHANGE(String PROTEIN_CHANGE) {
        this.PROTEIN_CHANGE = PROTEIN_CHANGE;
    }

    public String getONCOTATOR_CODON_CHANGE() {
        return ONCOTATOR_CODON_CHANGE;
    }

    public void setONCOTATOR_CODON_CHANGE(String ONCOTATOR_CODON_CHANGE) {
        this.ONCOTATOR_CODON_CHANGE = ONCOTATOR_CODON_CHANGE;
    }

    public Integer getONCOTATOR_PROTEIN_POS_START() {
        return ONCOTATOR_PROTEIN_POS_START;
    }

    public void setONCOTATOR_PROTEIN_POS_START(Integer ONCOTATOR_PROTEIN_POS_START) {
        this.ONCOTATOR_PROTEIN_POS_START = ONCOTATOR_PROTEIN_POS_START;
    }

    public Integer getONCOTATOR_PROTEIN_POS_END() {
        return ONCOTATOR_PROTEIN_POS_END;
    }

    public void setONCOTATOR_PROTEIN_POS_END(Integer ONCOTATOR_PROTEIN_POS_END) {
        this.ONCOTATOR_PROTEIN_POS_END = ONCOTATOR_PROTEIN_POS_END;
    }

    public String getMUTATION_TYPE() {
        return MUTATION_TYPE;
    }

    public void setMUTATION_TYPE(String MUTATION_TYPE) {
        this.MUTATION_TYPE = MUTATION_TYPE;
    }
}
