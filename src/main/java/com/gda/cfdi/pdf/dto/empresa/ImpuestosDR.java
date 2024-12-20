package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ImpuestosDR {

	@JsonProperty("TrasladosDR") 
    public TrasladosDR trasladosDR;
	
}
