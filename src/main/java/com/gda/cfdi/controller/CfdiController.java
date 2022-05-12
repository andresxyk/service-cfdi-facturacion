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
public class CfdiController {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private CfdiService cfdiService;
	
	@Autowired
	private Cfdi4Service cfdi4Service;	
	
	@Autowired
	private CfdiCreditoService cfdiCreditoService;
	
	@Autowired
	private CfdiSerieAService cfdiSerieAService;
	
	@Autowired
	private CfdiSerieBConvenioService cfdiSerieBConvenioService;
	
	@Autowired
	private CfdiSerieBRfcService cfdiSerieBRfcService;
	
	@Autowired
	private CfdiNotaCreditoService cfdiNotaCreditoService;
	
	@Autowired
	private CfdiComplementoPagoService cfdiComplementoPagoService;
	
	@GetMapping("/generar-cfdi")
	public ResponseEntity<?> getXmlOrden(@RequestParam("kordensucursal") Integer kordensucursal,
			@RequestParam("version") Integer version, @RequestParam("cusocfdi") Integer cusocfdi, 
			@RequestParam("kdatofiscal") Integer kdatofiscal){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiService.generarCfdi(kordensucursal, cusocfdi, kdatofiscal);	
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				cfdiDto = null;
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
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
	
	
	@GetMapping("/cfdi-orden-credito")
	public ResponseEntity<?> getXmlOrdenCredito(@RequestParam Integer folio,@RequestParam Integer tipofactura,
			@RequestParam double msubtotal,@RequestParam double miva, @RequestParam double mtotal,
			@RequestParam String strnocuenta, @RequestParam String strmetodopago, @RequestParam String uuidSustitucion,
			@RequestParam boolean sustitucion, @RequestParam boolean descuento, @RequestParam String descuentos,
			@RequestParam String notaDescuento, @RequestParam boolean retencion, @RequestParam Integer marca, 
			@RequestParam String descripcionFactura, @RequestParam("version") Integer version){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiCreditoService.generarCfdiOrden(folio, tipofactura, msubtotal, miva, mtotal, strnocuenta, strmetodopago, uuidSustitucion, sustitucion, descuento, descuentos, notaDescuento, retencion, marca, descripcionFactura);	
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	
	@PostMapping("/cfdi-serie-a")
	public ResponseEntity<?> getXmlSerieA(@RequestParam("version") Integer version,
			@RequestBody AnticipadaSerieADto anticipadaSerieADto){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiSerieAService.generarCfdiSerieA(anticipadaSerieADto);
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	
	@PostMapping("/cfdi-serie-b-convenio")
	public ResponseEntity<?> getXmlSerieBConvenio(@RequestParam("version") Integer version,
			@RequestBody SerieBConvenioDto serieBConvenioDto){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiSerieBConvenioService.generarCfdiSerieB(serieBConvenioDto);
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	
	@PostMapping("/cfdi-serie-b-rfc")
	public ResponseEntity<?> getXmlSerieBRfc(@RequestParam("version") Integer version,
			@RequestBody SerieBRfcDto serieBRfcDto){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiSerieBRfcService.generarCfdiSerieB(serieBRfcDto);
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	
	@PostMapping("/cfdi-nota-credito")
	public ResponseEntity<?> getXmlNotaCredito(@RequestParam("version") Integer version,
			@RequestBody NotaCreditoDto notaCreditoDto ){
		try {
			TNotaCreditoEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiNotaCreditoService.generarCfdiOrden(notaCreditoDto);
				return new ResponseEntity<TNotaCreditoEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	@GetMapping("/cfdi-complemento-pago")
	public ResponseEntity<?> getXmlComplementoPago(@RequestParam Integer idpago, @RequestParam("version") Integer version){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiComplementoPagoService.generarXML(idpago);	
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
	
	@PostMapping("/cfdi-complemento-pago-multi-pago")
	public ResponseEntity<?> getXmlComplementoPagoMultiPago(@RequestBody MultiPagoDto multiPagoDto, @RequestParam("version") Integer version){
		try {
			TFacturaEntityDto cfdiDto = null;
			if(version.equals(3)) {
				cfdiDto = cfdiComplementoPagoService.generarXMLMultiPago(multiPagoDto);
				return new ResponseEntity<TFacturaEntityDto>(cfdiDto, HttpStatus.OK);
			}else if(version.equals(4)){
//				cfdiDto = cfdi4Service.generarCfdi(kordensucursal);
				return new ResponseEntity<TFacturaEntityDto>(new TFacturaEntityDto(), HttpStatus.OK);
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
