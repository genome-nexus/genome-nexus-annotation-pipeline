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

package org.cbioportal.models;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

/**
 *
 * @author ochoaa
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"display_id",
"primary_id",
"version",
"description",
"dbname",
"synonyms",
"info_text",
"info_type",
"db_display_name",
})
public class GeneXref {
    
    @JsonProperty("display_id")
    private String displayId;

    @JsonProperty("primary_id")
    private String primaryId;

    @JsonProperty("version")
    private String version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dbname")
    private String dbname;

    @JsonProperty("synonyms")
    private List<String> synonyms = new ArrayList<>();

    @JsonProperty("info_text")
    private String infoText;

    @JsonProperty("info_type")
    private String infoType;

    @JsonProperty("db_display_name")
    private String dbDisplayName;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    
    /**
    * No args constructor for use in serialization
    * 
    */
    public GeneXref(){}
    
    /**
     * 
     * @param displayId
     * @param primaryId
     * @param version
     * @param description
     * @param dbname
     * @param synonyms
     * @param infoText
     * @param infoType
     * @param dbDisplayName 
     */
    public GeneXref(String displayId, String primaryId, String version, String description, String dbname, List<String> synonyms, String infoText, String infoType, String dbDisplayName) {
        this.displayId = displayId;
        this.primaryId = primaryId;
        this.version = version;
        this.description = description;
        this.dbname = dbname;
        this.synonyms = synonyms;
        this.infoText = infoText;
        this.infoType = infoType;
        this.dbDisplayName = dbDisplayName;
    }
    
    @JsonProperty("display_id")
    public String getDisplayId() {
        return displayId;
    }

    @JsonProperty("display_id")
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    @JsonProperty("primary_id")
    public String getPrimaryId() {
        return primaryId;
    }

    @JsonProperty("primary_id")
    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("dbname")
    public String getDbname() {
        return dbname;
    }

    @JsonProperty("dbname")
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    @JsonProperty("synonyms")
    public List<String> getSynonyms() {
        return synonyms;
    }

    @JsonProperty("synonyms")
    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    @JsonProperty("info_text")
    public String getInfoText() {
        return infoText;
    }

    @JsonProperty("info_text")
    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    @JsonProperty("info_type")
    public String getInfoType() {
        return infoType;
    }

    @JsonProperty("info_type")
    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    @JsonProperty("db_display_name")
    public String getDbDisplayName() {
        return dbDisplayName;
    }

    @JsonProperty("db_display_name")
    public void setDbDisplayName(String dbDisplayName) {
        this.dbDisplayName = dbDisplayName;
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
