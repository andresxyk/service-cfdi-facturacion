package com.gda.cfdi.pdf.dto.empresa;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CfdiRelacionado {

	@JsonProperty("TipoRelacion") 
    public String tipoRelacion;
    @JsonProperty("CfdiRelacionado") 
    public List<CfdiRelacionadoUuid> cfdiRelacionado;
    
}
