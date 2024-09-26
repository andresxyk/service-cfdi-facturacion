package com.gda.cfdi.dto.empresa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Retenciones {

	@JsonProperty("Retencion") 
    public List<Retencion>  retencion;
}
