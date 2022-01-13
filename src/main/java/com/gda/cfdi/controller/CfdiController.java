package com.gda.cfdi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.service.Cfdi4Service;
import com.gda.cfdi.service.CfdiService;

@RestController
@RequestMapping(value = "/gda/service-cfdi")
public class CfdiController {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private CfdiService cfdiService;
	
	@Autowired
	private Cfdi4Service cfdi4Service;
	
	@GetMapping("/generar-cfdi")
	public ResponseEntity<?> getXmlOrden(@RequestParam("kordensucursal") Integer kordensucursal,
			@RequestParam("version") Integer version){
		try {
			String cfdi = "";
			if(version.equals(3)) {
				cfdi = cfdiService.generarCfdi(kordensucursal);	
				return new ResponseEntity<String>(cfdi, HttpStatus.OK);
			}else if(version.equals(4)){
				cfdi = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<String>(cfdi, HttpStatus.OK);
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
