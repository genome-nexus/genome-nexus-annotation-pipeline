package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.genome_nexus.client.Gnomad;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyVariantInfoMixin {
    @JsonProperty("gnomadExome")
    private Gnomad gnomadExome;
}
