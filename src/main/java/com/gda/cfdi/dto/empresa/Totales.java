package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Totales {

	@JsonProperty("TotalTrasladosBaseIVA16") 
    public String totalTrasladosBaseIVA16;
    @JsonProperty("TotalTrasladosImpuestoIVA16") 
    public String totalTrasladosImpuestoIVA16;
    @JsonProperty("MontoTotalPagos") 
    public String montoTotalPagos;
}
