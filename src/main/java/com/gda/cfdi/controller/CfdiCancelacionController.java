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
import com.gda.cfdi.service.CfdiCancenlacionService;

@RestController
@RequestMapping(value = "/gda/service-cfdi")
public class CfdiCancelacionController {

	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private CfdiCancenlacionService cancelacionService;
	
	@GetMapping("/cfdi-cancelacion")
	public ResponseEntity<?> cfdiCancelacion(@RequestParam("uuid") String uuid, @RequestParam("rfcEmisor") String rfcEmisor, 
			@RequestParam("uuidSustitucion") String uuidSustitucion, @RequestParam("motivo") String motivo,
			 @RequestParam("cmarca") Integer cmarca, @RequestParam("csucursal") Integer csucursal){
		try {
			return new ResponseEntity<String>(cancelacionService.generarCfdiCancelacion(uuid, rfcEmisor, uuidSustitucion, motivo, cmarca, csucursal), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
}
