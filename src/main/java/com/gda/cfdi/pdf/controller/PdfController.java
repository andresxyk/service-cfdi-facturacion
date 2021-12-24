package com.gda.cfdi.pdf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.pdf.exception.ResponseErrorDto;



@RestController
@RequestMapping(value = "/gda/service-pdf")
public class PdfController {

	private static final Logger log = LoggerFactory.getLogger(PdfController.class);
	
	
	@GetMapping("/generar-pdf")
	public ResponseEntity<?> getXmlOrden(@RequestParam("kfactura") Integer kfactura){
		try {
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
