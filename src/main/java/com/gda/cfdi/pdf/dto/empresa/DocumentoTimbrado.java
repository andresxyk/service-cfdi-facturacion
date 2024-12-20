package com.gda.cfdi.pdf.dto.empresa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DocumentoTimbrado {

	public String xmlsintimbrar;
	public String xml;
    public String pdf;
    
}
