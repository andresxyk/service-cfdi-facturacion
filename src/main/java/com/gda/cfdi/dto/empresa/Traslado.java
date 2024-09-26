package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Traslado {
	@JsonProperty("Base") 
    public String base;
    @JsonProperty("Impuesto") 
    public String impuesto;
    @JsonProperty("TipoFactor") 
    public String tipoFactor;
    @JsonProperty("TasaOCuota") 
    public String tasaOCuota;
    @JsonProperty("Importe") 
    public String importe;
}
