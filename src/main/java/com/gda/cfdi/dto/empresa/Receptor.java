package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Receptor {
	@JsonProperty("Rfc") 
    public String rfc;
    @JsonProperty("Nombre") 
    public String nombre;
    @JsonProperty("DomicilioFiscalReceptor") 
    public String domicilioFiscalReceptor;
    @JsonProperty("RegimenFiscalReceptor") 
    public String regimenFiscalReceptor;
    @JsonProperty("UsoCFDI") 
    public String usoCFDI;
}
