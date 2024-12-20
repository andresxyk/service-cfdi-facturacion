package com.gda.cfdi.dto.empresa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrasladosConcepto {
	@JsonProperty("Traslado")
	public List<TrasladoConcepto> traslado;
}
