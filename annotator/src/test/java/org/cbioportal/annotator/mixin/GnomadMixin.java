package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.genome_nexus.client.AlleleFrequency;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GnomadMixin {
    @JsonProperty("alleleFrequency")
    private AlleleFrequency alleleFrequency;
}
