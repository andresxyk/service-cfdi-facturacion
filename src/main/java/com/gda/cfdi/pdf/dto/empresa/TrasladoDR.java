package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TrasladoDR {
	@JsonProperty("BaseDR") 
    public String baseDR;
    @JsonProperty("ImpuestoDR") 
    public String impuestoDR;
    @JsonProperty("TipoFactorDR") 
    public String tipoFactorDR;
    @JsonProperty("TasaOCuotaDR") 
    public String tasaOCuotaDR;
    @JsonProperty("ImporteDR") 
    public String importeDR;
}
