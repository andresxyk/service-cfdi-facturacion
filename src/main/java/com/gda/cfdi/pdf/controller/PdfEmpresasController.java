package com.gda.cfdi.pdf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.pdf.bean.InformacionPdfBean;
import com.gda.cfdi.pdf.exception.ResponseErrorDto;
import com.gda.cfdi.pdf.service.exakta.PdfExaktaService;

@RestController
@RequestMapping(value = "/gda/service-pdf-empresas")
public class PdfEmpresasController {

	private static final Logger log = LoggerFactory.getLogger(PdfEmpresasController.class);
	
	@Autowired
	private PdfExaktaService pdfService;
	
	@PostMapping("/pdf-exakta")
	public ResponseEntity<?> getXmlOrden(@RequestBody InformacionPdfBean cfdiBean){
		try {			
			return new ResponseEntity<String>(pdfService.generarPdfExaktaV40(cfdiBean), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
