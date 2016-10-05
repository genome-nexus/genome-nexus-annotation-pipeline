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

/**
 *
 * @author Zachary Heins
 */

import java.util.HashMap;
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
    "transcriptId",
    "geneSymbol",
    "refseqId",
    "ccdsId"
})
public class GenomeNexusIsoformOverridesResponse {

    @JsonProperty("transcriptId")
    private String transcriptId;
    @JsonProperty("geneSymbol")
    private String geneSymbol;
    @JsonProperty("refseqId")
    private String refseqId;
    @JsonProperty("ccdsId")
    private String ccdsId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
    * No args constructor for use in serialization
    * 
    */
    public GenomeNexusIsoformOverridesResponse() {}

    /**
    * 
    * @param geneSymbol
    * @param ccdsId
    * @param transcriptId
    * @param refseqId
    */
    public GenomeNexusIsoformOverridesResponse(String transcriptId, String geneSymbol, String refseqId, String ccdsId) {
        this.transcriptId = transcriptId;
        this.geneSymbol = geneSymbol;
        this.refseqId = refseqId;
        this.ccdsId = ccdsId;
    }

    /**
    * 
    * @return
    * The transcriptId
    */
    @JsonProperty("transcriptId")
    public String getTranscriptId() {
        return transcriptId;
    }

    /**
    * 
    * @param transcriptId
    * The transcriptId
    */
    @JsonProperty("transcriptId")
    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    /**
    * 
    * @return
    * The geneSymbol
    */
    @JsonProperty("geneSymbol")
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
    * 
    * @param geneSymbol
    * The geneSymbol
    */
    @JsonProperty("geneSymbol")
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
    * 
    * @return
    * The refseqId
    */
    @JsonProperty("refseqId")
    public String getRefseqId() {
        return refseqId;
    }

    /**
    * 
    * @param refseqId
    * The refseqId
    */
    @JsonProperty("refseqId")
    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    /**
    * 
    * @return
    * The ccdsId
    */
    @JsonProperty("ccdsId")
    public String getCcdsId() {
        return ccdsId;
    }

    /**
    * 
    * @param ccdsId
    * The ccdsId
    */
    @JsonProperty("ccdsId")
    public void setCcdsId(String ccdsId) {
        this.ccdsId = ccdsId;
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
