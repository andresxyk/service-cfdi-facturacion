package com.gda.cfdi.pdf.dto.empresa;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InformacionEmisor {

	public String direccion;
    public String pais;
    public String estado;
    public String ciudad;
    public String codigopostal;
}
