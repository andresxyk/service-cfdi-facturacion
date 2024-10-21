package com.gda.cfdi.pdf.dto.empresa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Retencion {
	public String base;
    public String impuesto;
    public String tipoFactor;
    public String tasaOCuota;
    public String importe;
    public String ordenador;
}
