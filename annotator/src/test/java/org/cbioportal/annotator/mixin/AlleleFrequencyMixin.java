package org.cbioportal.annotator.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleFrequencyMixin {
    @JsonProperty("af")
    private Double af;

    @JsonProperty("af_afr")
    private Double afAfr;

    @JsonProperty("af_amr")
    private Double afAmr;

    @JsonProperty("af_asj")
    private Double afAsj;

    @JsonProperty("af_eas")
    private Double afEas;

    @JsonProperty("af_fin")
    private Double afFin;
    
    @JsonProperty("af_nfe")
    private Double afNfe;

    @JsonProperty("af_oth")
    private Double afOth;

    @JsonProperty("af_sas")
    private Double afSas;
}
