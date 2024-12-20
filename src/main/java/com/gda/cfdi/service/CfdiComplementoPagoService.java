package com.gda.cfdi.service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.CUsoCfdiDto;
import com.gda.cfdi.dto.ControlFolioDto;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.FacturaSelloDto;
import com.gda.cfdi.dto.MultiPagoDto;
import com.gda.cfdi.dto.PagoDto;
import com.gda.cfdi.dto.PagoFacturaDto;
import com.gda.cfdi.dto.TFacturaCreditoDto;
import com.gda.cfdi.dto.TFacturaDto;
import com.gda.cfdi.dto.TFacturaEntityDto;
import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.exception.ResponseErrorException;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfd._3.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._3.Comprobante.Conceptos;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._3.Comprobante.Emisor;
import mx.gob.sat.cfd._3.Comprobante.Receptor;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class CfdiComplementoPagoService {
	private static final Logger log = LoggerFactory.getLogger(CfdiController.class);

	@Autowired
	private Environment env;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private UtilsService utilsService;
	
	public TFacturaEntityDto generarXML(Integer idpago) throws Exception {
		Comprobante comprobante;
		
		PagoDto pagoDto = consultaService.findPagoById(idpago);
		List<PagoFacturaDto> facturasRelacionadas= new ArrayList<>();
		for (PagoFacturaDto pagoFacturaDto : pagoDto.getFacturasRelacionadas()) {
			if (pagoFacturaDto.getXml() == null || pagoFacturaDto.getXml().replaceAll(" ", "").isEmpty()) {
				log.info("XML no encontrado en BD");
				try {
					String urlXml = utilsService.getRutaXMLByIdSucursal(pagoFacturaDto.getCsucursal()) + utilsService.getNombreFacturasByIdSucursal(pagoFacturaDto.getCsucursal(), Integer.parseInt(pagoFacturaDto.getFolio())) + ".xml";
					URL url = new URL(urlXml);
					
					StringWriter writer = new StringWriter();
					IOUtils.copy(url.openStream(), writer, "UTF-8");
					String xmlServidor = writer.toString();
					
					pagoFacturaDto.setXml(xmlServidor);
				} catch (NumberFormatException | IOException e) {
					log.error("No fue posible extraer el XML del servidor");
				}
			}	
			if (pagoFacturaDto.getXml() != null && !pagoFacturaDto.getXml().replaceAll(" ", "").isEmpty()) {
				String xml = pagoFacturaDto.getXml();
				String cleanXml = xml.replace("\n", "").replace("\r", "").replace("\t", "");
				pagoFacturaDto.setXml(cleanXml);
			}
			Comprobante c = null;
			try {
				log.info(pagoFacturaDto.getXml());
				c = utilsService.createComplementoFromXml(pagoFacturaDto.getXml());
				log.info(pagoFacturaDto.getXml());
				pagoFacturaDto.setMoneda(c.getMoneda().value());
				pagoFacturaDto.setMetodoPago(c.getMetodoPago().value());
				
				BigDecimal manticipo = pagoFacturaDto.getManticipo() == null ? BigDecimal.ZERO : pagoFacturaDto.getManticipo();
				
				pagoFacturaDto.setSaldoInsoluto(c.getTotal().subtract(manticipo).subtract(pagoFacturaDto.getMpago()));
				pagoFacturaDto.setImporteSaldoAnterior(pagoFacturaDto.getSaldoInsoluto().add(pagoFacturaDto.getMpago()));
				pagoFacturaDto.setImportePagado(pagoFacturaDto.getMpago());
				pagoFacturaDto.setEstatus(pagoFacturaDto.getCestadoregistro());
			} catch (Exception e) {
				log.error("Error al consultar pago factura");
				log.info(pagoFacturaDto.getXml());
				pagoFacturaDto.setEstatus(-1);
				facturasRelacionadas.add(pagoFacturaDto);
			} 
			facturasRelacionadas.add(pagoFacturaDto);
		}
		pagoDto.setFacturasRelacionadas(facturasRelacionadas);
		
		String RfcEmisor = getRfcEmisor(pagoDto);
		String sserie = getSSerie(pagoDto);
		boolean procesaJenner = getFechaRegistro(pagoDto);
		comprobante = buildPago(pagoDto,procesaJenner);
		Integer kfactura =saveComplementoPagos(pagoDto, comprobante);
		consultaService.updateCControlFolio(Integer.parseInt(comprobante.getFolio()), pagoDto.getIdControlFolio());
		
		log.info("RfcEmisor:"+RfcEmisor);
		log.info("procesaJenner:"+procesaJenner);
		log.info("sserie:"+sserie);
		
		String xmlOriginal = utilsService.createXmlFromComplementoPago(comprobante);
		FacturaSelloDto facturaSello = utilsService.buildSelloFactura(RfcEmisor, xmlOriginal,procesaJenner);

		comprobante.setCertificado(facturaSello.getCertificadoB64());
		comprobante.setSello(facturaSello.getSello());
		String xmlOriginalSello =  utilsService.createXmlFromComplementoPago(comprobante);
		
		TFacturaEntityDto tfactura = new TFacturaEntityDto();
		tfactura.setKfactura(kfactura);
		tfactura.setSxml(xmlOriginalSello);
		tfactura.setSxmlsello("");
		tfactura.setSurl("");
		tfactura.setSuddi("");
		tfactura.setScadenaoriginal(facturaSello.getCadenaOriginal().length()>4000?facturaSello.getCadenaOriginal().substring(0, 3999):facturaSello.getCadenaOriginal());
		tfactura.setSsellodigital(comprobante.getSello());
		return tfactura;
	}
	
	public TFacturaEntityDto generarXMLMultiPago(MultiPagoDto multiPagoDto) throws Exception {
		Comprobante comprobante;
		List<PagoDto> pagosDto = new ArrayList<PagoDto>();
		List<PagoFacturaDto> pagosFacturaDto = new ArrayList<PagoFacturaDto>();
		for (Integer idHijo : multiPagoDto.getPagos()) {
			PagoDto p = consultaService.findPagoById(idHijo);
			List<PagoFacturaDto> facturasRelacionadas= new ArrayList<>();
			for (PagoFacturaDto pagoFacturaDto : p.getFacturasRelacionadas()) {
				if (pagoFacturaDto.getXml() == null || pagoFacturaDto.getXml().replaceAll(" ", "").isEmpty()) {
					log.info("XML no encontrado en BD");
					try {
						String urlXml = utilsService.getRutaXMLByIdSucursal(pagoFacturaDto.getCsucursal()) + utilsService.getNombreFacturasByIdSucursal(pagoFacturaDto.getCsucursal(), Integer.parseInt(pagoFacturaDto.getFolio())) + ".xml";
						URL url = new URL(urlXml);
						
						StringWriter writer = new StringWriter();
						IOUtils.copy(url.openStream(), writer, "UTF-8");
						String xmlServidor = writer.toString();
						
						pagoFacturaDto.setXml(xmlServidor);
					} catch (NumberFormatException | IOException e) {
						log.error("No fue posible extraer el XML del servidor");
					}
				}	
				if (pagoFacturaDto.getXml() != null && !pagoFacturaDto.getXml().replaceAll(" ", "").isEmpty()) {
					String xml = pagoFacturaDto.getXml();
					String cleanXml = xml.replace("\n", "").replace("\r", "").replace("\t", "");
					pagoFacturaDto.setXml(cleanXml);
				}
				Comprobante c = null;
				try {
					log.info(pagoFacturaDto.getXml());
					c = utilsService.createComplementoFromXml(pagoFacturaDto.getXml());
					log.info(pagoFacturaDto.getXml());
					pagoFacturaDto.setMoneda(c.getMoneda().value());
					pagoFacturaDto.setMetodoPago(c.getMetodoPago().value());
					
					BigDecimal manticipo = pagoFacturaDto.getManticipo() == null ? BigDecimal.ZERO : pagoFacturaDto.getManticipo();
					
					pagoFacturaDto.setSaldoInsoluto(c.getTotal().subtract(manticipo).subtract(pagoFacturaDto.getMpago()));
					pagoFacturaDto.setImporteSaldoAnterior(pagoFacturaDto.getSaldoInsoluto().add(pagoFacturaDto.getMpago()));
					pagoFacturaDto.setImportePagado(pagoFacturaDto.getMpago());
					pagoFacturaDto.setEstatus(pagoFacturaDto.getCestadoregistro());
				} catch (Exception e) {
					log.error("Error al consultar pago factura");
					log.info(pagoFacturaDto.getXml());
					pagoFacturaDto.setEstatus(-1);
					facturasRelacionadas.add(pagoFacturaDto);
				} 
				facturasRelacionadas.add(pagoFacturaDto);
			}
			p.setFacturasRelacionadas(facturasRelacionadas);
			pagosDto.add(p);
			pagosFacturaDto.addAll(p.getFacturasRelacionadas());
		}
		
		multiPagoDto.getPago().setFacturasRelacionadas(pagosFacturaDto);
		String RfcEmisor = getRfcEmiorMultiPago(pagosDto);
		String sserie = getSSerieMultiPago(pagosDto);
		boolean procesaJenner = getFechaFacturaMultiPago(pagosDto);
		comprobante = buildPago(multiPagoDto.getPago(), pagosDto,procesaJenner);
		
		Integer kfactura =saveComplementoPagos(multiPagoDto.getPago(), comprobante);
		consultaService.updateCControlFolio(Integer.parseInt(comprobante.getFolio()), multiPagoDto.getPago().getIdControlFolio());
		
		log.info("RfcEmisor:"+RfcEmisor);
		log.info("procesaJenner:"+procesaJenner);
		log.info("sserie:"+sserie);
		
		String xmlOriginal = utilsService.createXmlFromComplementoPago(comprobante);
		FacturaSelloDto facturaSello = utilsService.buildSelloFactura(RfcEmisor, xmlOriginal,procesaJenner);
		
		comprobante.setCertificado(facturaSello.getCertificadoB64());
		comprobante.setSello(facturaSello.getSello());
		String xmlOriginalSello =  utilsService.createXmlFromComplementoPago(comprobante);
		
		TFacturaEntityDto tfactura = new TFacturaEntityDto();
		tfactura.setKfactura(kfactura);
		tfactura.setSxml(xmlOriginalSello);
		tfactura.setSxmlsello("");
		tfactura.setSurl("");
		tfactura.setSuddi("");
		tfactura.setScadenaoriginal(facturaSello.getCadenaOriginal().length()>4000?facturaSello.getCadenaOriginal().substring(0, 3999):facturaSello.getCadenaOriginal());
		tfactura.setSsellodigital(comprobante.getSello());
		return tfactura;		
	}
	
	
	
	
	
	
	
	public Comprobante buildPago(PagoDto pagoPadre, List<PagoDto> pagos, boolean procesaJenner) throws Exception {
		Comprobante comprobante = new Comprobante();

		String facturaPagadaXml = pagos.get(0).getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		
		if(pagoPadre.getSuddi()!=null && pagoPadre.getSuddi() != ""){			
			CfdiRelacionado cfdiRelacionado = new CfdiRelacionado();
			cfdiRelacionado.setUUID(pagoPadre.getSuddi());
			CfdiRelacionados cfdiRelacionados = new CfdiRelacionados();
			cfdiRelacionados.setTipoRelacion("04");
			cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
			comprobante.setCfdiRelacionados(cfdiRelacionados);
		}
		
		/**
		 * Receptor
		 */
		Receptor receptor = facturaBase.getReceptor();
		receptor.setUsoCFDI(CUsoCFDI.P_01);
		comprobante.setReceptor(receptor);

		/**
		 * Emisor
		 */
		Emisor emisor = facturaBase.getEmisor();
//		if(emisor.getRfc().equals("LCP061017PA9") || emisor.getRfc().equals("LCL050622DD9")){
//			if(!procesaJenner){
//				emisor.setRfc("LQC920131M20");
//				emisor.setNombre("LABORATORIO QUIMICO CLINICO AZTECA SAPI DE CV");				
//			}
//		}		
		comprobante.setEmisor(emisor);

		/**
		 * Fecha
		 */
//		comprobante.setFecha(DateUtil.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

		/**
		 * Serie y Folio
		 */
		ControlFolioDto controlFolio = this.getNextFolio(pagos.get(0).getIdControlFolio());
		comprobante.setFolio(controlFolio.getFolio().toString());
		comprobante.setSerie(controlFolio.getSerie());

		/**
		 * Información general
		 */
		comprobante.setVersion(env.getProperty("cfdi.version"));
		comprobante.setTipoDeComprobante(CTipoDeComprobante.P);
		comprobante.setMoneda(CMoneda.XXX);
		comprobante.setSubTotal(BigDecimal.ZERO);
		comprobante.setTotal(BigDecimal.ZERO);
		comprobante.setLugarExpedicion(facturaBase.getLugarExpedicion());
		
		/**
		 * Fecha
		 */
		if(facturaBase.getLugarExpedicion().equals("31203")){
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(utilsService.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss"));
		}else{
			
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
		}
		
		
		
		
		comprobante.setNoCertificado(facturaBase.getNoCertificado());
		
		if(facturaBase.getNoCertificado().equals("00001000000404009726")){
			comprobante.setNoCertificado("00001000000505145362");
		}
		if(facturaBase.getNoCertificado().equals("00001000000406347874")){
			comprobante.setNoCertificado("00001000000507423256");
		}
		
//		if(facturaBase.getNoCertificado().equals("00001000000402926302") || facturaBase.getNoCertificado().equals("00001000000504077717")){
//			if(!procesaJenner){
//				comprobante.setNoCertificado("00001000000502350617");				
//			}else{
//				comprobante.setNoCertificado(facturaBase.getNoCertificado().equals("00001000000400940461") 
//						? "00001000000502350617":facturaBase.getNoCertificado());
//			}
//		}else{			
//			comprobante.setNoCertificado(facturaBase.getNoCertificado().equals("00001000000400940461") 
//					? "00001000000502350617":facturaBase.getNoCertificado());
//		}
		

		/**
		 * Concepto
		 */
		Concepto concepto = new Concepto();
		concepto.setClaveProdServ(env.getProperty("complemento.pagos.xml.default.cveprod"));
		concepto.setCantidad(BigDecimal.ONE);
		concepto.setClaveUnidad(env.getProperty("complemento.pagos.xml.default.cveunidad"));
		concepto.setDescripcion(env.getProperty("complemento.pagos.xml.default.pago"));
		concepto.setValorUnitario(BigDecimal.ZERO);
		concepto.setImporte(BigDecimal.ZERO);

		Conceptos conceptos = new Conceptos();
		conceptos.getConcepto().add(concepto);
		comprobante.setConceptos(conceptos);

		/**
		 * Complemento Pagos
		 */
		Pagos pago = new Pagos();
		pago.setVersion(env.getProperty("complemento.pagos.xml.default.version"));
		Pagos.Pago newPago = new Pagos.Pago();

		newPago.setFechaPago(utilsService.toXmlGregorianCalendar(pagoPadre.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"));
		newPago.setMonedaP(CMoneda.MXN);
		newPago.setFormaDePagoP(pagoPadre.getFormaPago());
		newPago.setMonto(pagoPadre.getMonto());
		newPago.setCtaOrdenante(pagoPadre.getNumeroCuenta() == null ? null : pagoPadre.getNumeroCuenta().trim());
		newPago.setRfcEmisorCtaOrd(pagoPadre.getRfcBanco() == null ? null : pagoPadre.getRfcBanco().trim());
		newPago.setNomBancoOrdExt(pagoPadre.getNombreBanco());
		newPago.setNumOperacion(pagoPadre.getNumeroOperacion()==null ? null : pagoPadre.getNumeroOperacion().trim());

		for (PagoFacturaDto facturaPago : pagoPadre.getFacturasRelacionadas()) {
			log.info(facturaPago.toString());
			Pagos.Pago.DoctoRelacionado facturaRelacionada = new Pagos.Pago.DoctoRelacionado();
			facturaRelacionada.setIdDocumento(facturaPago.getIdDocumento());
			facturaRelacionada.setSerie(facturaPago.getSerie());
			facturaRelacionada.setFolio(facturaPago.getFolio());
			facturaRelacionada.setMetodoDePagoDR(CMetodoPago.fromValue(facturaPago.getMetodoPago()));
			facturaRelacionada.setMonedaDR(CMoneda.MXN);
			facturaRelacionada.setNumParcialidad(BigInteger.valueOf(facturaPago.getNumParcialidad().intValue()));
			facturaRelacionada.setImpSaldoAnt(facturaPago.getImporteSaldoAnterior());
			facturaRelacionada.setImpSaldoInsoluto(facturaPago.getSaldoInsoluto());
			facturaRelacionada.setImpPagado(facturaPago.getImportePagado());
			newPago.getDoctoRelacionado().add(facturaRelacionada);
			if (facturaRelacionada.getImpSaldoInsoluto().compareTo(BigDecimal.ZERO) < 0) {
				ResponseErrorDto responseTimbradoDto = new ResponseErrorDto();
				responseTimbradoDto.setCodigo("");
				responseTimbradoDto.setDescripcion(
						"Verifique las cantidades, no es posible especificar valores menores a 0.");
				responseTimbradoDto.setDetalle("Serie: " + facturaRelacionada.getSerie() + ", Folio: "
						+ facturaRelacionada.getFolio() + ", Saldo Anterior: " + facturaRelacionada.getImpSaldoAnt()
						+ ", Pago: " + facturaRelacionada.getImpPagado() + ", Saldo Insoluto: "
						+ facturaRelacionada.getImpSaldoInsoluto());
				throw new ResponseErrorException(responseTimbradoDto);
			}
		}
		pago.getPago().add(newPago);

		Comprobante.Complemento complemento = new Comprobante.Complemento();
		complemento.getAny().add(pago);
		comprobante.getComplemento().add(complemento);

		return comprobante;
	}
	
	
	public Comprobante buildPago(PagoDto pagoDto, boolean procesaJenner) throws Exception {
		Comprobante comprobante = new Comprobante();

		String facturaPagadaXml = pagoDto.getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		
		if(pagoDto.getSuddi()!=null && pagoDto.getSuddi() != ""){			
			CfdiRelacionado cfdiRelacionado = new CfdiRelacionado();
			cfdiRelacionado.setUUID(pagoDto.getSuddi());
			CfdiRelacionados cfdiRelacionados = new CfdiRelacionados();
			cfdiRelacionados.setTipoRelacion("04");
			cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
			comprobante.setCfdiRelacionados(cfdiRelacionados);
		}
		
		/**
		 * Receptor
		 */
		Receptor receptor = facturaBase.getReceptor();
		receptor.setUsoCFDI(CUsoCFDI.P_01);
		comprobante.setReceptor(receptor);

		/**
		 * Emisor
		 */
		Emisor emisor = facturaBase.getEmisor();
//		if(emisor.getRfc().equals("LCP061017PA9") || emisor.getRfc().equals("LCL050622DD9")){
//			if(!procesaJenner){
//				emisor.setNombre("LABORATORIO QUIMICO CLINICO AZTECA SAPI DE CV");
//				emisor.setRfc("LQC920131M20");				
//			}
//		}		
		comprobante.setEmisor(emisor);

		/**
		 * Fecha
		 */
		comprobante.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

		/**
		 * Serie y Folio
		 */
		ControlFolioDto controlFolio = this.getNextFolio(pagoDto.getIdControlFolio());
		comprobante.setFolio(controlFolio.getFolio().toString());
		comprobante.setSerie(controlFolio.getSerie());

		/**
		 * Información general
		 */
		comprobante.setVersion(env.getProperty("cfdi.version"));
		comprobante.setTipoDeComprobante(CTipoDeComprobante.P);
		comprobante.setMoneda(CMoneda.XXX);
		comprobante.setSubTotal(BigDecimal.ZERO);
		comprobante.setTotal(BigDecimal.ZERO);
		comprobante.setLugarExpedicion(facturaBase.getLugarExpedicion());
		
		
		/**
		 * Fecha
		 */
//		comprobante.setFecha(DateUtil.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

		
		if(facturaBase.getLugarExpedicion().equals("31203")){
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(utilsService.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss"));
		}else{
			
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
		}
		
		
		comprobante.setNoCertificado(facturaBase.getNoCertificado());
		if(facturaBase.getNoCertificado().equals("00001000000404009726")){
			comprobante.setNoCertificado("00001000000505145362");
		}
		if(facturaBase.getNoCertificado().equals("00001000000406347874")){
			comprobante.setNoCertificado("00001000000507423256");
		}
		
		
//		if(facturaBase.getNoCertificado().equals("00001000000402926302") || facturaBase.getNoCertificado().equals("00001000000504077717")){
//			if(!procesaJenner){
//				comprobante.setNoCertificado("00001000000502350617");				
//			}else{
//				comprobante.setNoCertificado(facturaBase.getNoCertificado().equals("00001000000400940461") 
//						? "00001000000502350617":facturaBase.getNoCertificado());
//			}
//		}else{			
//			comprobante.setNoCertificado(facturaBase.getNoCertificado().equals("00001000000400940461") 
//					? "00001000000502350617":facturaBase.getNoCertificado());
//		}

		/**
		 * Concepto
		 */
		Concepto concepto = new Concepto();
		concepto.setClaveProdServ(env.getProperty("complemento.pagos.xml.default.cveprod"));
		concepto.setCantidad(BigDecimal.ONE);
		concepto.setClaveUnidad(env.getProperty("complemento.pagos.xml.default.cveunidad"));
		concepto.setDescripcion(env.getProperty("complemento.pagos.xml.default.pago"));
		concepto.setValorUnitario(BigDecimal.ZERO);
		concepto.setImporte(BigDecimal.ZERO);

		Conceptos conceptos = new Conceptos();
		conceptos.getConcepto().add(concepto);
		comprobante.setConceptos(conceptos);

		/**
		 * Complemento Pagos
		 */
		Pagos pago = new Pagos();
		pago.setVersion(env.getProperty("complemento.pagos.xml.default.version"));
		Pagos.Pago newPago = new Pagos.Pago();

		newPago.setFechaPago(utilsService.toXmlGregorianCalendar(pagoDto.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"));
		newPago.setMonedaP(CMoneda.MXN);
		newPago.setFormaDePagoP(pagoDto.getFormaPago());
		newPago.setMonto(pagoDto.getMonto());
		newPago.setCtaOrdenante(pagoDto.getNumeroCuenta() == null ? null : pagoDto.getNumeroCuenta().trim());
		newPago.setRfcEmisorCtaOrd(pagoDto.getRfcBanco() == null ? null : pagoDto.getRfcBanco().trim());
		newPago.setNomBancoOrdExt(pagoDto.getNombreBanco());
		newPago.setNumOperacion(pagoDto.getNumeroOperacion()==null?null:pagoDto.getNumeroOperacion().trim());

		for (PagoFacturaDto facturaPago : pagoDto.getFacturasRelacionadas()) {
			log.info(facturaPago.toString());
			Pagos.Pago.DoctoRelacionado facturaRelacionada = new Pagos.Pago.DoctoRelacionado();
			facturaRelacionada.setIdDocumento(facturaPago.getIdDocumento());
			facturaRelacionada.setSerie(facturaPago.getSerie());
			facturaRelacionada.setFolio(facturaPago.getFolio());
			facturaRelacionada.setMetodoDePagoDR(CMetodoPago.fromValue(facturaPago.getMetodoPago()));
			facturaRelacionada.setMonedaDR(CMoneda.MXN);
			facturaRelacionada.setNumParcialidad(BigInteger.valueOf(facturaPago.getNumParcialidad().intValue()));
			facturaRelacionada.setImpSaldoAnt(facturaPago.getImporteSaldoAnterior());
			facturaRelacionada.setImpSaldoInsoluto(facturaPago.getSaldoInsoluto());
			facturaRelacionada.setImpPagado(facturaPago.getImportePagado());
			newPago.getDoctoRelacionado().add(facturaRelacionada);
			if (facturaRelacionada.getImpSaldoInsoluto().compareTo(BigDecimal.ZERO) < 0) {
				ResponseErrorDto responseTimbradoDto = new ResponseErrorDto();
				responseTimbradoDto.setCodigo("");
				responseTimbradoDto.setDescripcion(
						"Verifique las cantidades, no es posible especificar valores menores a 0.");
				responseTimbradoDto.setDetalle("Serie: " + facturaRelacionada.getSerie() + ", Folio: "
						+ facturaRelacionada.getFolio() + ", Saldo Anterior: " + facturaRelacionada.getImpSaldoAnt()
						+ ", Pago: " + facturaRelacionada.getImpPagado() + ", Saldo Insoluto: "
						+ facturaRelacionada.getImpSaldoInsoluto());
				throw new ResponseErrorException(responseTimbradoDto);
			}
		}
		pago.getPago().add(newPago);

		Comprobante.Complemento complemento = new Comprobante.Complemento();
		complemento.getAny().add(pago);
		comprobante.getComplemento().add(complemento);

		
		return comprobante;
	}
	
	public ControlFolioDto getNextFolio(Integer idControlFolio) {
		ControlFolioDto controlFolio;
		
		controlFolio = consultaService.findControlFolioById(idControlFolio);
		controlFolio.setFolio(controlFolio.getFolio() + 1);
		
		return controlFolio;
	}

	public String getRfcEmisor(PagoDto pagoDto)throws Exception{
		String facturaPagadaXml = pagoDto.getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		
		Emisor emisor = facturaBase.getEmisor();
		
		return emisor.getRfc();
	}
	
	public String getRfcEmiorMultiPago(List<PagoDto> pagos) throws Exception {
		String facturaPagadaXml = pagos.get(0).getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		Emisor emisor = facturaBase.getEmisor();
		return emisor.getRfc();
	}
	
	public String getSSerie(PagoDto pagoDto)throws Exception{
		String facturaPagadaXml = pagoDto.getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
//		facturaBase.getSerie();
//		
//		
//		Emisor emisor = facturaBase.getEmisor();
		
		return facturaBase.getSerie();
	}
	
	public String getSSerieMultiPago(List<PagoDto> pagos) throws Exception {
		String facturaPagadaXml = pagos.get(0).getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
//		Emisor emisor = facturaBase.getEmisor();
		return facturaBase.getSerie();
	}
	
	public boolean getFechaRegistro(PagoDto pagoDto)throws Exception{
		boolean statusJenner = false;
		String facturaPagadaXml = pagoDto.getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		XMLGregorianCalendar fecEmision = facturaBase.getFecha();
		
		Date dateFac = fecEmision.toGregorianCalendar().getTime();
		log.info("dateFac:"+dateFac);
		Date dateOct = null;
		
		dateOct = sdf.parse("01-10-2019");
		log.info("dateOct:"+dateOct);
		
		
		if(dateFac.before(dateOct)){
			statusJenner = true;
		}else{
			statusJenner = false;
		}
		
		return statusJenner;
	}
	
	public boolean getFechaFacturaMultiPago(List<PagoDto> pagos) throws Exception {
		boolean statusJenner = false;
	
		String facturaPagadaXml = pagos.get(0).getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		XMLGregorianCalendar fecEmision = facturaBase.getFecha();
		
		Date dateFac = fecEmision.toGregorianCalendar().getTime();
		
		Date dateOct = null;
		
		dateOct = sdf.parse("01-10-2019");
		
		if(dateFac.before(dateOct)){
			statusJenner = true;
		}else{
			statusJenner = false;
		}
		
		return statusJenner;
		
	}
	
	public Integer saveComplementoPagos(PagoDto pagoDto, Comprobante comprobante) throws IOException, JAXBException {
		TFacturaDto tFacturaCPagos = new TFacturaDto();
		/**
		 * Generales
		 */
		ControlFolioDto ctrlFolioDto =  this.getNextFolio(pagoDto.getIdControlFolio());
		tFacturaCPagos.setCveSucursal(ctrlFolioDto.getCveScursal());
		tFacturaCPagos.setFolio(Integer.parseInt(comprobante.getFolio()));
		tFacturaCPagos.setSerie(comprobante.getSerie());
		tFacturaCPagos.setSubtotal(comprobante.getSubTotal());
		tFacturaCPagos.setTotal(comprobante.getTotal());
		tFacturaCPagos.setIdSucursal(ctrlFolioDto.getIdSucursal());
		tFacturaCPagos.setCentidadlegal(utilsService.getCEntidadLegalByRFC(comprobante.getEmisor().getRfc()));
		/**
		 * Datos Cliente
		 */
		String facturaPagadaXml = pagoDto.getFacturasRelacionadas().get(0).getXml();
		Comprobante facturaBase = utilsService.createComplementoPagoFromXml(facturaPagadaXml);
		TFacturaCreditoDto facturaBaseDto = consultaService.getTFacturaCreditoDtoBySerieAndFolio(facturaBase.getSerie(),
				Integer.parseInt(facturaBase.getFolio()));
		tFacturaCPagos.setIdCliente(facturaBaseDto.getIdCliente());
		tFacturaCPagos.setIdDatoFiscal(facturaBaseDto.getIdDatoFiscal());
		
		/**
		 * Catálogos
		 */

		Optional<CUsoCfdiDto> usoCfdi = this.selectAllCUsoCfdi().stream()
				.filter(usoCfdiI -> usoCfdiI.getSclaveusocfdi().equals(comprobante.getReceptor().getUsoCFDI().value()))
				.findFirst();
		tFacturaCPagos.setIdUsoCfdi(usoCfdi.get().getCusocfdi());

		return consultaService.saveComplementoPago(tFacturaCPagos);

	}
	
	
	public List<CUsoCfdiDto> selectAllCUsoCfdi() {
		List<CUsoCfdiDto> usosValidos = consultaService.getListUsoCFDI(3);
//		
//		usosValidos.removeIf(x -> x.getCusocfdi().equals(0));
//		
		return usosValidos;
	}
	
	
}
