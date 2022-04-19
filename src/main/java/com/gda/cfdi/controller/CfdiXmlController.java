package com.gda.cfdi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gda.cfdi.dto.AnticipadaSerieADto;
import com.gda.cfdi.dto.MultiPagoDto;
import com.gda.cfdi.dto.NotaCreditoDto;
import com.gda.cfdi.dto.SerieBConvenioDto;
import com.gda.cfdi.dto.SerieBRfcDto;
import com.gda.cfdi.dto.TFacturaEntityDto;
import com.gda.cfdi.dto.TNotaCreditoEntityDto;
import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.service.Cfdi4Service;
import com.gda.cfdi.service.CfdiComplementoPagoService;
import com.gda.cfdi.service.CfdiCreditoService;
import com.gda.cfdi.service.CfdiNotaCreditoService;
import com.gda.cfdi.service.CfdiSerieAService;
import com.gda.cfdi.service.CfdiSerieBConvenioService;
import com.gda.cfdi.service.CfdiSerieBRfcService;
import com.gda.cfdi.service.CfdiService;

@RestController
@RequestMapping(value = "/gda/service-cfdi")
public class CfdiXmlController {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiXmlController .class);
	
	@Autowired
	private CfdiService cfdiService;
		
	@GetMapping("/xml-cfdi-factura")
	public ResponseEntity<?> getXmlOrden(@RequestParam("kfactura") Integer kfactura){
		try {
			return new ResponseEntity<String>(cfdiService.getXmlFactura(kfactura), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			ResponseErrorDto dto = new ResponseErrorDto();
			dto.setCodigo("error");
			dto.setDescripcion(e.getMessage());
			return new ResponseEntity<ResponseErrorDto>(dto, HttpStatus.BAD_REQUEST);
		}
	}
	
	
		
}
