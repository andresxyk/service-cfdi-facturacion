package com.gda.cfdi.pdf.dto.empresa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GDAMenssage {

	public String codeHttp;
    public String mensaje;
    public String descripcion;
    public String acuse;
}
