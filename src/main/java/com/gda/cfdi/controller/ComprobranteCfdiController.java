package com.gda.cfdi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.service.ComprobanteCfdiService;

import mx.gob.sat.cfd._3.Comprobante;

@RestController
@RequestMapping(value = "/gda/service-cfdi")
public class ComprobranteCfdiController {
	private static final Logger log = LoggerFactory.getLogger(ComprobranteCfdiController.class);
	
	@Autowired
	private ComprobanteCfdiService comprobanteCfdiService;

	@GetMapping("/comprobante-cfdi")
	public ResponseEntity<?> getXmlOrden(@RequestParam("kfactura") Integer kfactura,
			@RequestParam("version") Integer version){
		try {
			if(version.equals(3)) {
				Comprobante comprobante = comprobanteCfdiService.comprobanteCfdi3OfXml(kfactura);	
				return new ResponseEntity<Comprobante>(comprobante, HttpStatus.OK);
			}else if(version.equals(4)){
				mx.gob.sat.cfd._4.Comprobante comprobante = comprobanteCfdiService.comprobanteCfdi4OfXml(kfactura);
				return new ResponseEntity<mx.gob.sat.cfd._4.Comprobante>(comprobante, HttpStatus.OK);
			}else {
				ResponseErrorDto dto = new ResponseErrorDto();
				dto.setCodigo("error");
				dto.setDescripcion("La versión es incorrecta.");
				return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
}
