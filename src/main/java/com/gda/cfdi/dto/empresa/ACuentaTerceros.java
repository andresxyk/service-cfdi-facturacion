package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ACuentaTerceros {

	@JsonProperty("RfcACuentaTerceros") 
    public String rfcACuentaTerceros;
    @JsonProperty("NombreACuentaTerceros") 
    public String nombreACuentaTerceros;
    @JsonProperty("RegimenFiscalACuentaTerceros") 
    public String regimenFiscalACuentaTerceros;
    @JsonProperty("DomicilioFiscalACuentaTerceros") 
    public String domicilioFiscalACuentaTerceros;
}
