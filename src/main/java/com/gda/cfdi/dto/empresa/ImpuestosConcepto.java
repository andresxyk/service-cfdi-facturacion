package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ImpuestosConcepto {

	@JsonProperty("Traslados")
	public TrasladosConcepto traslados;
	@JsonProperty("Retenciones")
	public RetencionesConcepto retenciones;
	
}
