package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.genome_nexus.client.MyVariantInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyVariantInfoAnnotationMixin {
    @JsonProperty("annotation")
    private MyVariantInfo annotation;
}
