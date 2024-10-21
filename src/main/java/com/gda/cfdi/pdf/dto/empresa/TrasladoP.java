package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TrasladoP {
	@JsonProperty("BaseP")
	public String baseP;
	@JsonProperty("ImpuestoP")
	public String impuestoP;
	@JsonProperty("TipoFactorP")
	public String tipoFactorP;
	@JsonProperty("TasaOCuotaP")
	public String tasaOCuotaP;
	@JsonProperty("ImporteP")
	public String importeP;
}
