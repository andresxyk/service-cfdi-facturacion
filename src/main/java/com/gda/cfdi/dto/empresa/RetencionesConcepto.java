package com.gda.cfdi.dto.empresa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RetencionesConcepto {

	@JsonProperty("Retencion") 
    public List<RetencionConcepto> retencion;
}
