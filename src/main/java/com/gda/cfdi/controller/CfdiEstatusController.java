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
import com.gda.cfdi.service.CfdiEstatusService;

@RestController
@RequestMapping(value = "/gda/service-cfdi")
public class CfdiEstatusController {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiController.class);
	
	@Autowired
	private CfdiEstatusService cfdiEstatus;
	
	@GetMapping("/cfdi-estatus")
	public ResponseEntity<?> cfdiCancelacion(@RequestParam("rfcEmisor") String rfcEmisor, @RequestParam("rfcReceptor") String rfcReceptor, 
			@RequestParam("total") String total, @RequestParam("uuid") String uuid){
		try {
			return new ResponseEntity<String>(cfdiEstatus.generarCfdiEstatus(rfcEmisor, rfcReceptor, total, uuid), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}

}
