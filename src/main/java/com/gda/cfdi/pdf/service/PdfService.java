package com.gda.cfdi.pdf.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.itextpdf.text.DocumentException;

@Service
public class PdfService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private ConsultaService consultaService;	
	
	@Autowired
	private PdfContadoService contadoService;
	
		
	public String generarPdf(Integer kfactura, Integer cmarca, Integer csucursal) throws DocumentException, IOException, Exception{	
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = contadoService.generarPdfContadoV40(facturaDto, cmarca, csucursal);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = contadoService.generarPdfContadoV33(facturaDto, cmarca, csucursal);
		}		
		return base64;
	}
	
	
	
	

}
