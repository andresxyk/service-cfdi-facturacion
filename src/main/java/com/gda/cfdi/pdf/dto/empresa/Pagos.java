package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Pagos {

	@JsonProperty("Version") 
    public String version;
    @JsonProperty("Totales") 
    public Totales totales;
    @JsonProperty("Pago") 
    public Pago pago;
}
