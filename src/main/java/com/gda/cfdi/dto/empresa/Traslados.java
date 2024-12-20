package com.gda.cfdi.dto.empresa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Traslados {
	@JsonProperty("Traslado")
	public List<Traslado>  traslado;
}
