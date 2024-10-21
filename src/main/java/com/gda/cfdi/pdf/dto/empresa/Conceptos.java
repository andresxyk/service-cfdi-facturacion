package com.gda.cfdi.pdf.dto.empresa;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Conceptos {

	@JsonProperty("Concepto")
	public ArrayList<Concepto> concepto;
}
