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
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "variant",
    "id",    
    "assembly_name",
    "seq_region_name",
    "start",
    "end",
    "allele_string",
    "strand",
    "most_severe_consequence",
    "transcript_consequences"
})
public class GenomeNexusAnnotationResponse {

    @JsonProperty("allele_string")
    private String alleleString;
    @JsonProperty("assembly_name")
    private String assemblyName;
    @JsonProperty("end")
    private Integer end;
    @JsonProperty("id")
    private String id;
    @JsonProperty("most_severe_consequence")
    private String mostSevereConsequence;
    @JsonProperty("seq_region_name")
    private String seqRegionName;
    @JsonProperty("start")
    private Integer start;
    @JsonProperty("strand")
    private Integer strand;
    @JsonProperty("transcript_consequences")
    private List<TranscriptConsequence> transcriptConsequences = new ArrayList<TranscriptConsequence>();
    @JsonProperty("variant")
    private String variant;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
    * No args constructor for use in serialization
    * 
    */
    public GenomeNexusAnnotationResponse() {}

    /**
    * 
    * @param id
    * @param start
    * @param transcriptConsequences
    * @param strand
    * @param seqRegionName
    * @param variant
    * @param mostSevereConsequence
    * @param end
    * @param assemblyName
    * @param alleleString
    */
    public GenomeNexusAnnotationResponse(String alleleString, String assemblyName, int end, String id, String mostSevereConsequence, String seqRegionName, int start, int strand, List<TranscriptConsequence> transcriptConsequences, String variant) {
        this.alleleString = alleleString;
        this.assemblyName = assemblyName;
        this.end = end;
        this.id = id;
        this.mostSevereConsequence = mostSevereConsequence;
        this.seqRegionName = seqRegionName;
        this.start = start;
        this.strand = strand;
        this.transcriptConsequences = transcriptConsequences;
        this.variant = variant;
    }

    /**
    * 
    * @return
    * The alleleString
    */
    @JsonProperty("allele_string")
    public String getAlleleString() {
        return alleleString;
    }

    /**
    * 
    * @param alleleString
    * The allele_string
    */
    @JsonProperty("allele_string")
    public void setAlleleString(String alleleString) {
        this.alleleString = alleleString;
    }

    /**
    * 
    * @return
    * The assemblyName
    */
    @JsonProperty("assembly_name")
    public String getAssemblyName() {
        return assemblyName;
    }

    /**
    * 
    * @param assemblyName
    * The assembly_name
    */
    @JsonProperty("assembly_name")
    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    /**
    * 
    * @return
    * The end
    */
    @JsonProperty("end")
    public Integer getEnd() {
        return end;
    }

    /**
    * 
    * @param end
    * The end
    */
    @JsonProperty("end")
    public void setEnd(int end) {
        this.end = end;
    }

    /**
    * 
    * @return
    * The id
    */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
    * 
    * @param id
    * The id
    */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
    * 
    * @return
    * The mostSevereConsequence
    */
    @JsonProperty("most_severe_consequence")
    public String getMostSevereConsequence() {
        return mostSevereConsequence;
    }

    /**
    * 
    * @param mostSevereConsequence
    * The most_severe_consequence
    */
    @JsonProperty("most_severe_consequence")
    public void setMostSevereConsequence(String mostSevereConsequence) {
        this.mostSevereConsequence = mostSevereConsequence;
    }

    /**
    * 
    * @return
    * The seqRegionName
    */
    @JsonProperty("seq_region_name")
    public String getSeqRegionName() {
        return seqRegionName;
    }

    /**
    * 
    * @param seqRegionName
    * The seq_region_name
    */
    @JsonProperty("seq_region_name")
    public void setSeqRegionName(String seqRegionName) {
        this.seqRegionName = seqRegionName;
    }

    /**
    * 
    * @return
    * The start
    */
    @JsonProperty("start")
    public Integer getStart() {
        return start;
    }

    /**
    * 
    * @param start
    * The start
    */
    @JsonProperty("start")
    public void setStart(int start) {
        this.start = start;
    }

    /**
    * 
    * @return
    * The strand
    */
    @JsonProperty("strand")
    public Integer getStrand() {
        return strand;
    }

    /**
    * 
    * @param strand
    * The strand
    */
    @JsonProperty("strand")
    public void setStrand(int strand) {
        this.strand = strand;
    }

    /**
    * 
    * @return
    * The transcriptConsequences
    */
    @JsonProperty("transcript_consequences")
    public List<TranscriptConsequence> getTranscriptConsequences() {
        return transcriptConsequences;
    }

    /**
    * 
    * @param transcriptConsequences
    * The transcript_consequences
    */
    @JsonProperty("transcript_consequences")
    public void setTranscriptConsequences(List<TranscriptConsequence> transcriptConsequences) {
        this.transcriptConsequences = transcriptConsequences;
    }

    /**
    * 
    * @return
    * The variant
    */
    @JsonProperty("variant")
    public String getVariant() {
        return variant;
    }

    /**
    * 
    * @param variant
    * The variant
    */
    @JsonProperty("variant")
    public void setVariant(String variant) {
        this.variant = variant;
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
