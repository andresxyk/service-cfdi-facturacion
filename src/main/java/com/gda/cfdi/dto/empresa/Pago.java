package com.gda.cfdi.dto.empresa;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Pago {
	@JsonProperty("FechaPago") 
    public Date fechaPago;
    @JsonProperty("FormaDePagoP") 
    public String formaDePagoP;
    @JsonProperty("MonedaP") 
    public String monedaP;
    @JsonProperty("TipoCambioP") 
    public String tipoCambioP;
    @JsonProperty("Monto") 
    public String monto;
    @JsonProperty("DoctoRelacionado") 
    public DoctoRelacionado doctoRelacionado;
    @JsonProperty("ImpuestosP") 
    public ImpuestosP impuestosP;
}
