package com.gda.cfdi.dto.empresa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Header {

	public String lineanegocio;
    public String dregistro;
    public String marca;
    public String smarca;
    public String id;
    public String tipoDocumento;
    public String token;
}
