package com.gda.cfdi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.service.empresa.EmpresaCfdiService;
import com.gda.cfdi.service.exakta.ExaktaCfdiService;
//import com.gda.cfdi.service.referencia.ReferenciaCfdiService;
import com.gda.cfdi.service.referencia.ReferenciaCfdiService;

import facturacion.domain.dto.AddendaDto;
import facturacion.domain.dto.referencia.cfdi.GenerarCFDI40;
import mx.gob.sat.cfd._4.Comprobante;

@RestController
@RequestMapping(value = "/gda/service-cfdi-empresas")
public class CfdiEmpresasController {

	private static final Logger log = LoggerFactory.getLogger(CfdiEmpresasController .class);
	
	@Autowired
	private ReferenciaCfdiService referenciaCfdiService;
	@Autowired
	private ExaktaCfdiService exaktaCfdiService;
	@Autowired
	private EmpresaCfdiService empresaCfdiService;
	
	@RequestMapping(path = "/cfdi-biomedica", method = RequestMethod.POST)
	public ResponseEntity<?> getCfdiBiomedica(@RequestBody GenerarCFDI40 generarCFDI40){
		try {
			return new ResponseEntity<String>(referenciaCfdiService.generarCfdi(generarCFDI40), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(path = "/cfdi-exakta", method = RequestMethod.POST)
	public ResponseEntity<?> getCfdiExakta(@RequestBody String cfdiPlano){
		try {
			return new ResponseEntity<String>(exaktaCfdiService.generarCfdi(cfdiPlano), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(path = "/xml-cfdi", method = RequestMethod.POST)
	public ResponseEntity<?> getXmlCfdi(@RequestBody Comprobante comprobante){
		try {
			return new ResponseEntity<String>(empresaCfdiService.getXmlCfdi(comprobante), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(path = "/xml-cfdi-add-addenda", method = RequestMethod.POST)
	public ResponseEntity<?> getXmlCfdi(@RequestBody AddendaDto addendaDto){
		try {
			return new ResponseEntity<String>(empresaCfdiService.getXmlAddendaCfdi(addendaDto), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
}
