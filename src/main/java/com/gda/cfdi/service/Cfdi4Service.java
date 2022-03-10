package com.gda.cfdi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.encoding.Base64;
import org.apache.commons.ssl.PKCS8Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dao.inte.IConsultaDao;
import com.gda.cfdi.dto.CClaveProductoServicioSatDto;
import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.CfdiDto;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.SelloDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TFacturaEntityDto;
import com.gda.cfdi.dto.TOrdenExamenSucursalDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;
import com.gda.cfdi.dto.TSociedadCivilDto;

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._4.Comprobante.Conceptos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.ACuentaTerceros;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CRegimenFiscal;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class Cfdi4Service {

	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;
	
//	@Autowired
//	private IConsultaDao consultaDao;
	
//	public TFacturaEntityDto generarCfdi(Integer kordensucursal) throws Exception {	
//		try {
//			Integer cusoCfdi = 3;
//			Boolean bandAzteca;
//			List<TPagoPacienteDto> list =  consultaDao.getTPagoPacienteDto(kordensucursal);
//			List<TOrdenSucursalDto> listTos = consultaDao.getListTOrdenSucursalByKordensucursal(kordensucursal);
//			Boolean contieneSaldo = list.get(0).getMsaldo().intValue() > 0 ? true :false;
//			Integer csucursal = listTos.get(0).getCsucursal();
//			Integer cmarca = listTos.get(0).getCmarca();
//			Integer cconvenio = listTos.get(0).getCconvenio();
//			TPagoPacienteDto pacienteDto = this.obtenerPago(list);			
//			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");			
//			Date fechaInicio = null;
//			Date fechaFin = null;			
//			fechaInicio = sdf.parse("01-01-2019");
//			fechaFin = sdf.parse("30-09-2019");			
//			if((pacienteDto.getDregistro().equals(fechaInicio)|| pacienteDto.getDregistro().after(fechaInicio)) && 
//					(pacienteDto.getDregistro().equals(fechaFin) || pacienteDto.getDregistro().before(fechaFin)) ){
//				bandAzteca = false;
//			}else{
//				bandAzteca = true;
//			}
//			String tipoPago = "";
//			if (contieneSaldo) {
//				tipoPago = "PPD";
//			} else {
//				tipoPago = "PUE";
//			}
//			DatosMarcaDto datosMarcaDto = obtenerDatosMarca(cmarca, csucursal, bandAzteca);
//			
//			String FORMATER = "yyyy-MM-dd'T'HH:mm:ss";
//			DateFormat format = new SimpleDateFormat(FORMATER);
//			XMLGregorianCalendar value = null;
//			
//			if(cmarca.equals(15)){
//				value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss");
//			}else{
//				if(csucursal == 250 
//						|| csucursal == 238 
//						|| csucursal == 240 
//						|| csucursal.equals(250)
//						|| csucursal.equals(238)
//						|| csucursal.equals(240)){
//					value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss");
//				}else{
//					value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-2), "yyyy-MM-dd'T'HH:mm:ss");				
//				}
//			}
//			
//			ObjectFactory of = new ObjectFactory();
//			Comprobante cfdi = of.createComprobante();
//			cfdi.setVersion(env.getProperty("cfdi.version.4"));
//			CControlFolioDto controlFolioDto = consultaDao.getControlFolioDto(csucursal);
//			Integer ufolio = obtenerFolio(controlFolioDto); 
//			
//			cfdi.setFolio(String.valueOf(ufolio));
//			cfdi.setSerie(controlFolioDto.getSserie());
//			cfdi.setTipoCambio(BigDecimal.valueOf(1));
//			cfdi.setFecha(value);
//			cfdi.setNoCertificado(datosMarcaDto.getNumeroCertificado());
//			
//			cfdi.setCertificado(obtenerCertificadocer(datosMarcaDto));
//			cfdi.setMoneda(CMoneda.MXN);
//			cfdi.setTipoDeComprobante(CTipoDeComprobante.I);
//			
//			cfdi.setLugarExpedicion(consultaDao.getCPostalByCsucursal(csucursal));
//			
//			CTipoPagoDto tipoPagoDto = consultaDao.getCTipoPagoById(pacienteDto.getCtipopago());
//			
//			log.info("tip pago infogda-->>  "+tipoPagoDto.getCtipopago());
//			CFormaPagoCfdiDto cFormaPagoCfdiDto = consultaDao.getCFormaPagoCfdiById(tipoPagoDto.getCformapagocfdi());
//			log.info("Forma Pago cfdi"+cFormaPagoCfdiDto.getSclaveformapagocfdi()
//				+" form "+cFormaPagoCfdiDto.getSformapagocfdi());
//			cfdi.setFormaPago(cFormaPagoCfdiDto.getSclaveformapagocfdi());
//			cfdi.setMetodoPago(contieneSaldo ? CMetodoPago.PPD : CMetodoPago.PUE);
//			
//			List<TFacturaCanceladaDto> lstFacturasCanceladas = consultaDao.getFacturasCanceladasByKorden(kordensucursal);
//			if(lstFacturasCanceladas.size() > 0){
//				log.info("UDDI::   "+lstFacturasCanceladas.get(0).getFactura_id());
//				if(!lstFacturasCanceladas.get(0).getFactura_id().equals("File no found") && lstFacturasCanceladas.get(0).getFactura_id().trim().length()>5 ){
//					CfdiRelacionados cfdiRelacionados = of.createComprobanteCfdiRelacionados();
//					CfdiRelacionado cfdiRelacionado = of.createComprobanteCfdiRelacionadosCfdiRelacionado();
//					log.info("UDDID:::--->>>>   "+lstFacturasCanceladas.get(0).getFactura_id());
//					cfdiRelacionado.setUUID(lstFacturasCanceladas.get(0).getFactura_id());
//					cfdiRelacionados.setTipoRelacion("04");
//					cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
//					cfdi.getCfdiRelacionados().add(cfdiRelacionados);
//				}
//			}
//			
//			
//			// Emisor
//			Emisor emisor = of.createComprobanteEmisor();
//			emisor.setNombre(darFormatoCFDI( datosMarcaDto.getRazonSocialMarca().toUpperCase()  ));  
//			emisor.setRfc(datosMarcaDto.getRfcMarca().toUpperCase()); 
//			emisor.setRegimenFiscal(CRegimenFiscal.valueOf("C" + "601").getValue());
//			cfdi.setEmisor(emisor);
//			
//			// Receptor
//			Receptor receptor = of.createComprobanteReceptor();
//			receptor.setNombre("PUBLICO EN GENERAL");  
//			log.info("Nombre:::-----    "+receptor.getNombre());
//			receptor.setRfc("XAXX010101000");
//			log.info("cusoCfdi--->>>   " + cusoCfdi);
//			switch (cusoCfdi) {
//			case 3:
//				receptor.setUsoCFDI(CUsoCFDI.G_03);
//				break;
//			case 12:
//				receptor.setUsoCFDI(CUsoCFDI.D_01);
//				break;
//			case 13:
//				receptor.setUsoCFDI(CUsoCFDI.D_02);
//				break;
//			case 22:
////									case 44:
//				receptor.setUsoCFDI(CUsoCFDI.P_01);
//				break;
//			default:
//				break;
//			}
//			cfdi.setReceptor(receptor);
//			
//			
//			List<TOrdenExamenSucursalDto> lstTOrdenExamenSucursal = consultaDao.getTOrdenExamenSucursalByKOrdenSucursal(kordensucursal);
//			Conceptos conceptos = of.createComprobanteConceptos();
//			BigDecimal importeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
//			BigDecimal importePadre = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
//			BigDecimal importeTerceros = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
//			
//			BigDecimal dbTotalOrden = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
//			
//			Integer count = 0;
//			
//			Boolean contTerceros = false;
//			List<TSociedadCivilDto> listSociedadCivil = consultaDao.getTSociedadCivilByKOrdenSucursal(kordensucursal);
//			
//			if(listSociedadCivil.size()>0){
//				contTerceros = true;
//			}
//			
//			BigDecimal bDSubtotal = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
//			BigDecimal bDTotalImpuestos = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
//
//			for (TOrdenExamenSucursalDto tOrdenExamen : lstTOrdenExamenSucursal) {				
//				BigDecimal bDimporteConcepto = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
//				BigDecimal bDimporteTraslado = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);				
//				log.info("tOrdenExamen.getKordenexamensucursal:"+tOrdenExamen.getKordenexamensucursal());
//				TSociedadCivilDto civilEntity = null;
//				if(contTerceros){
//					for(TSociedadCivilDto entity : listSociedadCivil){
//						if(entity.getKordenexamensucursal().equals(tOrdenExamen.getKordenexamensucursal())){
//							civilEntity = entity;
//						}
//					}
//				}				
//				count++;
//				log.info("Monto total--->>>   "+tOrdenExamen.getMtotal());
//				log.info("comparar---->>>   "+tOrdenExamen.getMtotal().compareTo(BigDecimal.valueOf(0.00)));
//				
//				log.info("Monto pago paciente--->>>   "+tOrdenExamen.getMpagopaciente());
//				log.info("comparar---->>>   "+tOrdenExamen.getMpagopaciente().compareTo(BigDecimal.valueOf(0.00)));
//				
//				if (tOrdenExamen.getMpagopaciente().compareTo(BigDecimal.valueOf(0.00)) != 0 ) {
//					Concepto concepto1 = of.createComprobanteConceptosConcepto();
//					concepto1.setCantidad(new BigDecimal(tOrdenExamen.getUvolumenexamen()).setScale(0));
//					log.info("tOrdenExamen.getMtotal()--->>  "+tOrdenExamen.getMtotal());
//					log.info("tOrdenExamen.getMpagopaciente()--->>  "+tOrdenExamen.getMpagopaciente());
//					CClaveProductoServicioSatDto claveProductoServicioSatDto = consultaDao.findCClaveProductoServicioSatById(tOrdenExamen.getCexamen());
//					concepto1.setClaveProdServ(claveProductoServicioSatDto != null 
//							&& !claveProductoServicioSatDto
//									.getSclaveproductoserviciosat().isEmpty()
//									? String.valueOf(claveProductoServicioSatDto
//											.getSclaveproductoserviciosat())
//									: "85121800"); // Debe ser catalogo
//					concepto1.setNoIdentificacion(String.valueOf(tOrdenExamen.getCexamen()));
//					
//					concepto1.setClaveUnidad("E48"); 
//					concepto1.setUnidad("Examen");
//					concepto1.setDescripcion(darFormatoCFDI(tOrdenExamen.getSexamen())); // "Prueba concepto1");
//					
//					
//					BigDecimal importeConcepto = null;
//					
//					if(civilEntity == null){
//						Integer tipoConvenio = consultaDao.getCTipoConvenioByConvenio(tOrdenExamen.getCconvenio());
//						log.info("TIPO Convenio:::"+tipoConvenio);
//						if(tipoConvenio == 21){
//							importeConcepto = (tOrdenExamen.getMpagopaciente().divide(BigDecimal.valueOf(1.16), 6, BigDecimal.ROUND_HALF_UP))
//									.multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen())).setScale(6, BigDecimal.ROUND_HALF_UP);							
//							concepto1.setValorUnitario(tOrdenExamen.getMpagopaciente().divide(BigDecimal.valueOf(1.16), 2));
//							log.info("concepto1.getValorUnitario()-->>  "+concepto1.getValorUnitario());
//							concepto1.setImporte(tOrdenExamen.getMpagopaciente().divide(BigDecimal.valueOf(1.16), 2));
//							log.info("concepto1.getImporte()-->  "+concepto1.getImporte());
//							concepto1.setImporte(concepto1.getImporte().multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen())).setScale(2));     ///concepto1.getCantidad()).setScale(2));
//							log.info("concepto1.getImporte()2-->  "+concepto1.getImporte());
////							concepto1.setImporte(validarLimiteInferiorSuperiorUnitario(concepto1.getCantidad(),concepto1.getValorUnitario(),concepto1.getImporte(),false));	
//						}else{
//							dbTotalOrden = dbTotalOrden.add(tOrdenExamen.getMpagopaciente());
//							BigDecimal bdValorUnitario = tOrdenExamen.getMpagopaciente().divide(new BigDecimal("1.16"), 2, BigDecimal.ROUND_HALF_UP);
//							BigDecimal bdImporteIva = tOrdenExamen.getMpagopaciente().multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen()));
//							BigDecimal bdImporte = bdImporteIva.divide(BigDecimal.valueOf(1.16), 2);
//							BigDecimal bdIva = bdImporteIva.subtract(bdImporte).setScale(2);
//							concepto1.setValorUnitario(bdValorUnitario); 
//							log.info("concepto1.getValorUnitario()-->>  "+concepto1.getValorUnitario());
//							concepto1.setImporte(bdValorUnitario.multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen())).setScale(2, BigDecimal.ROUND_HALF_UP));
//							log.info("concepto1.getImporte()-->  "+concepto1.getImporte());
//							
//						}
//					}else{
//						log.info("civilEntity.getMoperacion()::"+civilEntity.getMoperacion());
//						log.info("civilEntity.getMoperacion()::"+civilEntity.getMoperacion().divide(BigDecimal.valueOf(1.16), 2, BigDecimal.ROUND_UP));
//						dbTotalOrden = dbTotalOrden.add(civilEntity.getMoperacion());
//						
//						concepto1.setValorUnitario(civilEntity.getMoperacion().divide(BigDecimal.valueOf(1.16), 2));
//						concepto1.setImporte(civilEntity.getMoperacion().divide(BigDecimal.valueOf(1.16), 2));
//						concepto1.setImporte(concepto1.getImporte().multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen())).setScale(2));
//						concepto1.setImporte(validarLimiteInferiorSuperiorUnitario(concepto1.getCantidad(),concepto1.getValorUnitario(),concepto1.getImporte(),false));
//					}
//					importePadre = importePadre.add(concepto1.getImporte().setScale(2));					
//					bDSubtotal = bDSubtotal.add(bDimporteConcepto.setScale(2));					
//					conceptos.getConcepto().add(concepto1);
//					// Impuestos
//					Impuestos impuestos = of.createComprobanteConceptosConceptoImpuestos();
//					Traslados traslados = of.createComprobanteConceptosConceptoImpuestosTraslados();
//					Traslado traslado = of.createComprobanteConceptosConceptoImpuestosTrasladosTraslado();					
//					traslado.setBase(concepto1.getImporte());
//					log.info("traslado.getBase()--->>>  "+traslado.getBase());
//					if(civilEntity == null){
//						if(consultaDao.getCTipoConvenioByConvenio(tOrdenExamen.getCconvenio())  == 21){
//							log.info("getCtipoConvenio=21");
//							traslado.setImporte(importeConcepto.multiply(new BigDecimal(.16)).setScale(2,BigDecimal.ROUND_HALF_UP));
//							log.info("traslado.getImporte()--->>>  "+traslado.getImporte());
////							traslado.setImporte(validarLimiteInferiorSuperiorUnitario(concepto1.getCantidad(),traslado.getBase(),traslado.getImporte(),true));
//							log.info("traslado.getImporte()2--->>>  "+traslado.getImporte());
//						}else{							
//							BigDecimal bdImporteIva = tOrdenExamen.getMpagopaciente().multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen()));
//							BigDecimal bdImporte = bdImporteIva.divide(BigDecimal.valueOf(1.16), 2);
//							BigDecimal bdIva = bdImporteIva.subtract(bdImporte).setScale(2);							
//							log.info("IMPORTE::::::::: "+traslado.getBase().multiply(new BigDecimal("0.16")).setScale(2,BigDecimal.ROUND_HALF_UP));
//							BigDecimal importeIva = bdImporteIva.subtract(traslado.getBase()).setScale(2);
//							traslado.setImporte(importeIva);
//							log.info("traslado.getImporte()--->>>  "+traslado.getImporte());
//						}
//					}else{
//						traslado.setImporte(civilEntity.getMoperacion().subtract(concepto1.getImporte()).setScale(2));
//						traslado.setImporte(validarLimiteInferiorSuperiorUnitario(concepto1.getCantidad(),traslado.getBase(),traslado.getImporte(),true));
//					}
//					
//					importeTotal = importeTotal.add(traslado.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
//					bDTotalImpuestos = bDTotalImpuestos.add(bDimporteTraslado.setScale(2, BigDecimal.ROUND_HALF_UP));
//					traslado.setImpuesto("002");
//					traslado.setTipoFactor(CTipoFactor.TASA);
//					traslado.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));
//					traslados.getTraslado().add(traslado);						
//					impuestos.setTraslados(traslados);
//					concepto1.setImpuestos(impuestos);
//					cfdi.setConceptos(conceptos);
//				}else{
//					System.out.println("****************+ contiene valores en 0 "+tOrdenExamen.getMtotal());
//				}
//
//			}			
//			if(contTerceros){				
//				for (TOrdenExamenSucursalDto tOrdenExamen : lstTOrdenExamenSucursal) {					
//					for(TSociedadCivilDto entity : listSociedadCivil){
//						if(entity.getKordenexamensucursal().equals(tOrdenExamen.getKordenexamensucursal())){
//							BigDecimal importeXVolumen = entity.getMinterpretacion().multiply(new BigDecimal(tOrdenExamen.getUvolumenexamen()));
//							importeTerceros = importeTerceros.add(importeXVolumen.setScale(2));
//						}
//					}					
//				}
//				Concepto conceptoTerceros = of.createComprobanteConceptosConcepto();				
//				conceptoTerceros.setCantidad(new BigDecimal("1.00"));
//				conceptoTerceros.setClaveProdServ("85121600"); 				
//				conceptoTerceros.setClaveUnidad("ACT");
//				conceptoTerceros.setDescripcion("Honorario Medico");				
//				conceptoTerceros.setValorUnitario(importeTerceros.setScale(2, BigDecimal.ROUND_DOWN));
//				conceptoTerceros.setImporte(importeTerceros.setScale(2, BigDecimal.ROUND_DOWN));				
//				conceptos.getConcepto().add(conceptoTerceros);				
//				Impuestos impuestosTerceros = of.createComprobanteConceptosConceptoImpuestos();
//				Traslados trasladosTerceros = of.createComprobanteConceptosConceptoImpuestosTraslados();
//				Traslado trasladoTerceros = of.createComprobanteConceptosConceptoImpuestosTrasladosTraslado();								
//				trasladoTerceros.setBase(importeTerceros.setScale(2, BigDecimal.ROUND_DOWN));
//				trasladoTerceros.setImpuesto("002");
//				trasladoTerceros.setTipoFactor(CTipoFactor.EXENTO);				
//				trasladosTerceros.getTraslado().add(trasladoTerceros);						
//				impuestosTerceros.setTraslados(trasladosTerceros);
//				conceptoTerceros.setImpuestos(impuestosTerceros);
//				
//				ACuentaTerceros cuentaTerceros = of.createComprobanteConceptosConceptoACuentaTerceros();
//				cuentaTerceros.setRfcACuentaTerceros("SAE190815RA5");
//				cuentaTerceros.setNombreACuentaTerceros("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC");
//				cuentaTerceros.setRegimenFiscalACuentaTerceros("PENDIENTE");
//				cuentaTerceros.setDomicilioFiscalACuentaTerceros("PENDIENTE");
//				
//				conceptoTerceros.setACuentaTerceros(cuentaTerceros);
//				
////				ComplementoConcepto complementoConcepto = of.createComprobanteConceptosConceptoComplementoConcepto();				
////				PorCuentadeTerceros porCuentadeTerceros = new PorCuentadeTerceros();				
////				porCuentadeTerceros.setVersion("1.1");
////				porCuentadeTerceros.setRfc("SAE190815RA5");
////				porCuentadeTerceros.setNombre("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC");
////				mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Parte parte = new mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Parte();
////				parte.setCantidad(new BigDecimal("1.00"));
////				parte.setDescripcion("Honorario Medico");
////				parte.setImporte(importeTerceros.setScale(2, BigDecimal.ROUND_DOWN));
////				parte.setNoIdentificacion("001");
////				parte.setUnidad("ACT");
////				parte.setValorUnitario(importeTerceros.setScale(2, BigDecimal.ROUND_DOWN));				
////				porCuentadeTerceros.getParte().add(parte);				
////				mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos impuestos = new mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos();
////				mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos.Traslados traslados = new mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos.Traslados();
////				mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos.Traslados.Traslado traslado = new mx.gob.sat.cfd._4.terceros.PorCuentadeTerceros.Impuestos.Traslados.Traslado();
////				traslado.setImporte(new BigDecimal("0.000000").setScale(6));
////				traslado.setTasa(new BigDecimal("0.000").setScale(3));
////				traslado.setImpuesto("IVA");				
////				traslados.getTraslado().add(traslado);	
////				impuestos.setTraslados(traslados);				
////				porCuentadeTerceros.setImpuestos(impuestos);				
////				complementoConcepto.getAny().add(porCuentadeTerceros);				
////				conceptoTerceros.setComplementoConcepto(complementoConcepto);				
//				cfdi.setConceptos(conceptos);				
//				importePadre = importePadre.add(importeTerceros);
//			}
//			cfdi.setTotal(pacienteDto.getMpagopacientetotal().setScale(2));
//			cfdi.setSubTotal(cfdi.getTotal().subtract(importeTotal).setScale(2, BigDecimal.ROUND_HALF_UP));			
//			if(!dbTotalOrden.equals(cfdi.getTotal())) {
//				cfdi.setSubTotal(importePadre.setScale(2, BigDecimal.ROUND_HALF_UP));
//				cfdi.setTotal(cfdi.getSubTotal().add(importeTotal).setScale(2, BigDecimal.ROUND_HALF_UP));
//			}			
//			System.out.println("importeTotal-->>>  " + importeTotal);
//			log.info("Tota::::::   " + cfdi.getTotal());		
//			mx.gob.sat.cfd._4.Comprobante.Impuestos impuestos = new mx.gob.sat.cfd._4.Comprobante.Impuestos();
//			mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados traslados = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados();
//			mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado();
//			trasladosTotales.setImporte(importeTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
//			System.out.println("trasladosTotales.getImporte()-->>  " + trasladosTotales.getImporte());
//			trasladosTotales.setImpuesto("002");
//			trasladosTotales.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));
//			trasladosTotales.setTipoFactor(CTipoFactor.TASA);
//			traslados.getTraslado().add(trasladosTotales);
//			impuestos.setTraslados(traslados);
//			impuestos.setTotalImpuestosTrasladados(importeTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
//			cfdi.setImpuestos(impuestos);
//			String xmlOriginal = createXmlFromComprobante(cfdi);
//			
//			SelloDto selloDto = this.obtenerSello(datosMarcaDto, xmlOriginal);
//
//			cfdi.setSello(selloDto.getSelloCFDI());
//			
//			String xmlOriginalSello = createXmlFromComprobante(cfdi);
//			TFacturaEntityDto cfdiDto = new TFacturaEntityDto();
//			return cfdiDto;
//		} catch (Exception e) {
//			throw e;
//		}
//	}
	
	public SelloDto obtenerSello(DatosMarcaDto datosMarcaDto, String xml) {
		String selloCFDI = "";
		String cadenaOriginal = "";
		log.info("rutaCadenaOriginal--->>>   "+datosMarcaDto.getRutaCadenaOriginal());
		File xslt = new File(datosMarcaDto.getRutaCadenaOriginal());
		StreamSource sourceXSL = new StreamSource(xslt);		
		StreamSource sourceXML = new StreamSource(new StringReader(xml));
		System.out.println("cargo xml::  " + sourceXML);
		byte[] output1 = null;
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(sourceXSL);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			transformer.transform(sourceXML, new StreamResult(output));
			output1 = output.toString().getBytes("UTF-8");
			cadenaOriginal = new String(output1);
		} catch (Exception e) {
			System.out.println("Error de codificacion: " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Obtener sellos ");
		selloCFDI = generarSello(datosMarcaDto, new String(output1));
//		selloCFDI = generarSello("12345678a", new String(output1));
		
		SelloDto selloDto = new SelloDto(selloCFDI, cadenaOriginal);
		return selloDto;
	}
	
	public String generarSello(DatosMarcaDto datosMarcaDto, String cadenaOriginal) {
		FileInputStream fileInputStream;
		String firma = "";
		try {
			fileInputStream = new FileInputStream(datosMarcaDto.getRutaKey());
			byte[] fileBytes = new byte[fileInputStream.available()];
			fileInputStream.read(fileBytes);
			System.out.println("fileBytes-->>>    " + fileBytes);
			PKCS8Key pkcs8 = new PKCS8Key(fileBytes, datosMarcaDto.getPasword().toCharArray());
			KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec pkcs8Encoded = new PKCS8EncodedKeySpec(pkcs8.getDecryptedBytes());
			PrivateKey privateKey = privateKeyFactory.generatePrivate(pkcs8Encoded);
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			byte[] cadenaOriginalByte = cadenaOriginal.getBytes();
			signature.update(cadenaOriginalByte);
			// fileInputStream.close();
			firma = new String(Base64.encode(signature.sign()));
			System.out.println("firma::::    " + firma);
		} catch (SignatureException e) {
			System.out.println("SignatureException-->> " + e);
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			System.out.println("GeneralSecurityException-->> " + e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("Firma digital del CFDI:" + firma);
		return firma;
	}	
	
	public String createXmlFromComprobante(Comprobante comprobante) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		if(comprobante.getComplemento()!=null) {
			comprobante.getComplemento().getAny().forEach(object -> {
				if (object instanceof TimbreFiscalDigital) {
					classesMarshall.add(TimbreFiscalDigital.class);
				}
			});			
		}
		classesMarshall.add(Comprobante.class);
//		classesMarshall.add(PorCuentadeTerceros.class);
//		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/terceros http://www.sat.gob.mx/sitio_internet/cfd/terceros/terceros11.xsd");
		StringWriter sw = new StringWriter();
		marshaller.marshal(comprobante, sw);
		xml = sw.toString();
		return xml;
	}
	
	public BigDecimal validarLimiteInferiorSuperiorUnitario(BigDecimal cantidad, BigDecimal valorUnitario,BigDecimal importeCalculado,boolean base) {
		log.info("cantidad:::   "+cantidad+" valorUnitario :: "+valorUnitario);
		BigDecimal importeLimites = null;
		BigDecimal limiteInferior=null;
		BigDecimal limiteSuperior=null;		
		if(base){
			log.info("Calcular Importe del translado ****");
			limiteInferior = ((valorUnitario.subtract(new BigDecimal("0.005"))).multiply(new BigDecimal("0.160000"))).setScale(2,BigDecimal.ROUND_DOWN) ;
			limiteSuperior = ((valorUnitario.add(new BigDecimal("0.004999999999"))).multiply((new BigDecimal("0.160000")))).setScale(2,BigDecimal.ROUND_UP);
			log.info("limiteInferior-->>   "+limiteInferior+" limiteSuperior--->>>  "+limiteSuperior);
			//es igual al limite inferior
			if(((importeCalculado.compareTo(limiteInferior) == 0) || (importeCalculado.compareTo(limiteInferior) == 1)) 
					&& ((importeCalculado.compareTo(limiteSuperior) == -1) || (importeCalculado.compareTo(limiteSuperior) == 0))){			
				importeLimites = importeCalculado;
				log.info("ESTA EN EL LIMITE CORRECTO***  "+importeLimites);
			}else{
				importeLimites = limiteSuperior;
				log.info("AGARRAR EL LIMITE SUOERIOR** "+importeLimites);
			}			
		}else{
			log.info("Calcular Importe Concepto****");
			limiteInferior = ((cantidad.subtract(new BigDecimal("0.005"))).multiply( (valorUnitario).subtract(new BigDecimal("0.005")))).setScale(2,BigDecimal.ROUND_DOWN) ;
			limiteSuperior = ((cantidad.add(new BigDecimal("0.004999999999"))).multiply( (valorUnitario).add(new BigDecimal("0.004999999999")))).setScale(2,BigDecimal.ROUND_UP) ;
			log.info("limiteInferior-->>   "+limiteInferior+" limiteSuperior--->>>  "+limiteSuperior);
			//es igual al limite inferior
			if(((importeCalculado.compareTo(limiteInferior) == 0) || (importeCalculado.compareTo(limiteInferior) == 1)) 
					&& ((importeCalculado.compareTo(limiteSuperior) == -1) || (importeCalculado.compareTo(limiteSuperior) == 0))){			
				importeLimites = importeCalculado;
				log.info("ESTA EN EL LIMITE CORRECTO***  "+importeLimites);
			}else{
				importeLimites = limiteSuperior;
				log.info("AGARRAR EL LIMITE SUOERIOR** "+importeLimites);
			}
		}
		return importeLimites;
	}
	
	public static String darFormatoCFDI(String cadenaOrigianl){
		String cadenaNormalize = Normalizer.normalize(cadenaOrigianl, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		System.out.println("Resultado: " + cadenaSinAcentos);		
		return cadenaSinAcentos;
	}
	
	private  XMLGregorianCalendar toXmlGregorianCalendar(Date date, String format) throws DatatypeConfigurationException {
	    return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat(format).format(date));
	}
	
	private Date sumarORestarMinutosAFecha(Date fecha, int minutos){	
        Calendar calendar = Calendar.getInstance();	
        calendar.setTime(fecha); 	
        calendar.add(Calendar.MINUTE, minutos);  	
        return calendar.getTime(); 	
    }
	
	private Integer obtenerFolio(CControlFolioDto ccontrol) {
		log.info("ENTRY::: insertarFacturaCancelada:::  " + ccontrol.getCcontrolfolio() + " folioActual "
				+ ccontrol.getUfolioactual());
		Integer actualizo = ccontrol.getUfolioactual() + 1;
		ccontrol.setUfolioactual(actualizo);
		log.info("ufolio + 1 *********   "+actualizo);
//		consultaDao.updateCControlFolio(ccontrol.getCcontrolfolio(), actualizo);
		return actualizo;
	}
	
	private TPagoPacienteDto obtenerPago(List<TPagoPacienteDto> list) {
		Integer mayor = list.get(0).getMpagopacienteparcial().intValue();
		int posicion = 0;
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).getMpagopacienteparcial().intValue() > mayor) {
				mayor = list.get(i).getMpagopacienteparcial().intValue();
				posicion = i;
			}
		}
		TPagoPacienteDto pagoPacienteDto = list.get(posicion);
		return pagoPacienteDto;
	}
	
	
	private DatosMarcaDto obtenerDatosMarca(Integer marca, Integer csucursal, boolean bandAzteca){
		String sucursalesPrado = env.getProperty("list.sucursal.jenner.prado");
		String sucursalesLean = env.getProperty("list.sucursal.jenner.lean");
		List<String> listPrado = new ArrayList<String>(Arrays.asList(sucursalesPrado.split(",")));
		List<String> listLean = new ArrayList<String>(Arrays.asList(sucursalesLean.split(",")));
		
		String rutaCadenaOriginal = env.getProperty("path.file.cadena.original.4");
		String pasword = "";
		String rfcMarca = "";
		String razonSocialMarca = "";
		String numeroCertificado = "";
		String rutaKey = "";
		String rutaCer = "";
		switch (marca) {
		case 1:
			log.info("**** OLAB *****");
			rutaKey = env.getProperty("path.file.key.olab");
			rutaCer = env.getProperty("path.file.cer.olab");		
			pasword = env.getProperty("password.cer.olab");	
			rfcMarca = env.getProperty("rfc.marca.olab");	
			razonSocialMarca = env.getProperty("razon.social.olab");
			numeroCertificado = env.getProperty("numero.certificado.olab");
			break;
		case 4:
			log.info("**** AZTECA *****");
			rutaKey = env.getProperty("path.file.key.azteca");
			rutaCer = env.getProperty("path.file.cer.azteca");		
			pasword = env.getProperty("password.cer.azteca");	
			rfcMarca = env.getProperty("rfc.marca.azteca");	
			razonSocialMarca = env.getProperty("razon.social.azteca");
			numeroCertificado = env.getProperty("numero.certificado.azteca");
			break;
		case 7:
			log.info("**** JENNER *****");
			if(bandAzteca){
				log.info("**** JENNER Timbrado con Azteca *****");
				rutaKey = env.getProperty("path.file.key.azteca");
				rutaCer = env.getProperty("path.file.cer.azteca");		
				pasword = env.getProperty("password.cer.azteca");	
				rfcMarca = env.getProperty("rfc.marca.azteca");	
				razonSocialMarca = env.getProperty("razon.social.azteca");
				numeroCertificado = env.getProperty("numero.certificado.azteca");			
			}else{				
				if(listPrado.contains(csucursal.toString())){
					log.info("**** PRADO *****");
					rutaKey = env.getProperty("path.file.key.prado");
					rutaCer = env.getProperty("path.file.cer.prado");		
					pasword = env.getProperty("password.cer.prado");	
					rfcMarca = env.getProperty("rfc.marca.prado");	
					razonSocialMarca = env.getProperty("razon.social.prado");
					numeroCertificado = env.getProperty("numero.certificado.prado");	
				}else if(listLean.contains(csucursal.toString())){
					log.info("**** LEAN *****");
					rutaKey = env.getProperty("path.file.key.lean");
					rutaCer = env.getProperty("path.file.cer.lean");		
					pasword = env.getProperty("password.cer.lean");	
					rfcMarca = env.getProperty("rfc.marca.lean");	
					razonSocialMarca = env.getProperty("razon.social.lean");
					numeroCertificado = env.getProperty("numero.certificado.lean");
				}
			}
			break;
		case 5:
			log.info("**** SWISSLAB *****");
			rutaKey = env.getProperty("path.file.key.swisslab");
			rutaCer = env.getProperty("path.file.cer.swisslab");		
			pasword = env.getProperty("password.cer.swisslab");	
			rfcMarca = env.getProperty("rfc.marca.swisslab");	
			razonSocialMarca = env.getProperty("razon.social.swisslab");
			numeroCertificado = env.getProperty("numero.certificado.swisslab");
			break;			
		case 15:
			log.info("**** LIACSA *****");
			rutaKey = env.getProperty("path.file.key.swisslab");
			rutaCer = env.getProperty("path.file.cer.swisslab");		
			pasword = env.getProperty("password.cer.swisslab");	
			rfcMarca = env.getProperty("rfc.marca.swisslab");	
			razonSocialMarca = env.getProperty("razon.social.swisslab");
			numeroCertificado = env.getProperty("numero.certificado.swisslab");
			break;		
		default:
			break;
		}
		
		DatosMarcaDto datosMarcaDto= new DatosMarcaDto(rutaCadenaOriginal, pasword, rfcMarca, 
				razonSocialMarca, numeroCertificado, rutaKey, rutaCer);
		return datosMarcaDto;
	}
	
	public String obtenerCertificadocer(DatosMarcaDto datosMarcaDto) throws Exception{
		log.info("Obtener certificado*******");
		String encodedFile = "";
		try {
			Base64 base64 = new Base64();
			log.info("rutaCer--->>>   "+datosMarcaDto.getRutaCer());
			File file = new File(datosMarcaDto.getRutaCer());
			byte[] fileArray = new byte[(int) file.length()];
			InputStream inputStream;
			inputStream = new FileInputStream(file);
			inputStream.read(fileArray);
			encodedFile = new String(base64.encode(fileArray));
		} catch (Exception e) {
			throw e;
		}
		return encodedFile;
	}
	
}
