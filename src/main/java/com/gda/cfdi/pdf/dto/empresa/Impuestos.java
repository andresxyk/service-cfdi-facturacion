package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Impuestos {

	@JsonProperty("Traslados")
	public Traslados traslados;
	@JsonProperty("Retenciones")
	public Retenciones retenciones;
	@JsonProperty("TotalImpuestosTrasladados")
	public String totalImpuestosTrasladados;
	@JsonProperty("TotalImpuestosRetenidos")
	public String totalImpuestosRetenidos;
}
