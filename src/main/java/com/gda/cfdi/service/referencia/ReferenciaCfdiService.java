package com.gda.cfdi.service.referencia;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.PagoFacturaDto;
import com.gda.cfdi.exception.ResponseErrorDto;
import com.gda.cfdi.exception.ResponseErrorException;
import com.gda.cfdi.service.UtilsCfdi4Service;
import com.gda.cfdi.service.UtilsService;

import facturacion.domain.dto.referencia.cfdi.Conceptos.Concepto40R;
import facturacion.domain.dto.referencia.cfdi.GenerarCFDI40;
import facturacion.domain.dto.referencia.cfdi.Pagos.Pago.PagosPago20R.DoctoRelacionado.PagosPagoDoctoRelacionado20R;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._4.Comprobante.Conceptos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.pagos20.Pagos;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP.TrasladosP;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP.TrasladosP.TrasladoP;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service("referenciaCfdiService")
public class ReferenciaCfdiService {
	private static final Logger log = LoggerFactory.getLogger(ReferenciaCfdiService .class);
	
	@Autowired
	private Environment env;
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private UtilsCfdi4Service utilsCfdi4Service;

	public String generarCfdi(GenerarCFDI40 generarCFDI40) throws Exception {
		if(generarCFDI40.getCfdi().getClaveCFDI().equals("FAC") || generarCFDI40.getCfdi().getClaveCFDI().equals("CRE")) {
			return generarCfdiFacNC(generarCFDI40);
		}else if(generarCFDI40.getCfdi().getClaveCFDI().equals("CPA") ) {
			return generarCfdiPago(generarCFDI40);
		}
		return "";
	}
	
	public String generarCfdiFacNC(GenerarCFDI40 generarCFDI40) throws Exception {
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosRfcEmisor(generarCFDI40.getCredenciales().getCuenta(), 4);		
		Comprobante cfdi = new Comprobante();
		cfdi.setVersion(env.getProperty("cfdi.version.4"));
		cfdi.setExportacion(generarCFDI40.getCfdi().getExportacion());
		cfdi.setFormaPago(String.valueOf(generarCFDI40.getCfdi().getFormaPago()));
		cfdi.setSerie(generarCFDI40.getCfdi().getSerie());
		cfdi.setFolio(generarCFDI40.getCfdi().getFolio());
		cfdi.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
		cfdi.setTipoCambio(new BigDecimal(generarCFDI40.getCfdi().getTipoCambio()));
		cfdi.setLugarExpedicion(generarCFDI40.getCfdi().getLugarExpedicion());
		cfdi.setMetodoPago(CMetodoPago.fromValue(generarCFDI40.getCfdi().getMetodoPago()));
		cfdi.setNoCertificado(datosMarcaDto.getNumeroCertificado());
		cfdi.setMoneda(CMoneda.fromValue(generarCFDI40.getCfdi().getMoneda()));
		if(generarCFDI40.getCfdi().getClaveCFDI().equals("FAC")) {
			cfdi.setTipoDeComprobante(CTipoDeComprobante.I);
		}else if( generarCFDI40.getCfdi().getClaveCFDI().equals("CRE")) {
			cfdi.setTipoDeComprobante(CTipoDeComprobante.E);
		}
		cfdi.setTotal(generarCFDI40.getCfdi().getTotal());
		cfdi.setSubTotal(generarCFDI40.getCfdi().getSubTotal());
		if(generarCFDI40.getCfdi().getCfdiRelacionados()!=null) {
			if(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R() !=null) {
				if(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getCfdiRelacionado().size()>0) {
					Comprobante.CfdiRelacionados cfdiRelacionadosV4 = new ObjectFactory().createComprobanteCfdiRelacionados();
					cfdiRelacionadosV4.setTipoRelacion(String.valueOf(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getTipoRelacion()) );
					for (facturacion.domain.dto.referencia.cfdi.CfdiRelacionados.CfdiRelacionados40R.CfdiRelacionado cfdiRelacionado : generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getCfdiRelacionado()) {
						if(cfdiRelacionado.getCfdiRelacionado40R()!=null) {
							CfdiRelacionado cfdiRelacionadoV4 = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
							cfdiRelacionadoV4.setUUID(cfdiRelacionado.getCfdiRelacionado40R().getUUID());
							cfdiRelacionadosV4.getCfdiRelacionado().add(cfdiRelacionadoV4);
						}
					}
					cfdi.getCfdiRelacionados().add(cfdiRelacionadosV4);
				}				
			}
			
		}
		
		/**
		 * Emisor
		 */
		Emisor emisor = new ObjectFactory().createComprobanteEmisor();
		emisor.setNombre(datosMarcaDto.getRazonSocialMarca());
		emisor.setRfc(datosMarcaDto.getRfcMarca());
		emisor.setRegimenFiscal(String.valueOf(generarCFDI40.getCfdi().getEmisor().getRegimenFiscal()) );
		cfdi.setEmisor(emisor);
		
		/**
		 * Receptor
		 */
		Receptor receptor = new ObjectFactory().createComprobanteReceptor();
		receptor.setNombre(generarCFDI40.getCfdi().getReceptor().getNombre());
		receptor.setRfc(generarCFDI40.getCfdi().getReceptor().getRfc());
		receptor.setUsoCFDI(CUsoCFDI.fromValue(generarCFDI40.getCfdi().getReceptor().getUsoCFDI()));
		receptor.setRegimenFiscalReceptor(String.valueOf(generarCFDI40.getCfdi().getReceptor().getRegimenFiscalReceptor()));
		receptor.setDomicilioFiscalReceptor(generarCFDI40.getCfdi().getReceptor().getDomicilioFiscalReceptor());
		cfdi.setReceptor(receptor);
		
		/**
		 * Conceptos
		 */
		BigDecimal importeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importeBaseTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		Conceptos conceptos = new ObjectFactory().createComprobanteConceptos();
		if(generarCFDI40.getCfdi().getConceptos()!=null) {
			if(generarCFDI40.getCfdi().getConceptos().getConcepto40R().size()>0) {
				for (Concepto40R concepto40r : generarCFDI40.getCfdi().getConceptos().getConcepto40R()) {
					Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
					concepto.setCantidad(concepto40r.getCantidad());
					concepto.setClaveProdServ(concepto40r.getClaveProdServ());
					concepto.setNoIdentificacion(concepto40r.getNoIdentificacion());
					concepto.setClaveUnidad(concepto40r.getClaveUnidad());
					concepto.setUnidad(concepto40r.getUnidad());
					concepto.setObjetoImp(concepto40r.getObjetoImp());
					concepto.setDescripcion(concepto40r.getDescripcion());
					concepto.setValorUnitario(concepto40r.getValorUnitario());
					concepto.setImporte(concepto40r.getImporte());										
					conceptos.getConcepto().add(concepto);
					if(concepto40r.getImpuestos()!=null) {
						Impuestos impuestos = new ObjectFactory().createComprobanteConceptosConceptoImpuestos();
						if(concepto40r.getImpuestos().getTraslados()!=null) {
							Traslados traslados = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTraslados();
							if(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R()!=null) {								
								Traslado traslado = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
								traslado.setBase(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R().getBase());
								traslado.setImporte(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R().getImporte());
								traslado.setImpuesto(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R().getImpuesto());
								traslado.setTipoFactor(CTipoFactor.fromValue(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R().getTipoFactor()));
								traslado.setTasaOCuota(concepto40r.getImpuestos().getTraslados().getTrasladoConcepto40R().getTasaOCuota());
								traslados.getTraslado().add(traslado);
								importeTotal = importeTotal.add(traslado.getImporte());
								importeBaseTotal = importeBaseTotal.add(traslado.getBase());
							}							
							impuestos.setTraslados(traslados);
						}
						concepto.setImpuestos(impuestos);
					}
					
					
				}
			}
		}
		cfdi.setConceptos(conceptos);
		
		Comprobante.Impuestos impuestos = new ObjectFactory().createComprobanteImpuestos();
		Comprobante.Impuestos.Traslados traslados = new ObjectFactory().createComprobanteImpuestosTraslados();
		Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new ObjectFactory().createComprobanteImpuestosTrasladosTraslado();
		trasladosTotales.setBase(importeBaseTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
		trasladosTotales.setImporte(importeTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
		trasladosTotales.setImpuesto("002");
		trasladosTotales.setTasaOCuota(new BigDecimal(0.16).setScale(6, RoundingMode.HALF_UP));
		trasladosTotales.setTipoFactor(CTipoFactor.TASA);
		traslados.getTraslado().add(trasladosTotales);
		impuestos.setTraslados(traslados);		
		impuestos.setTotalImpuestosTrasladados(importeTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
		cfdi.setImpuestos(impuestos);
		String xml = utilsCfdi4Service.createXmlFromComprobante(cfdi);
		cfdi.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		cfdi.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  utilsCfdi4Service.createXmlFromComprobante(cfdi);
		return xmlOriginalSello;
	}
	
	
	public String generarCfdiPago(GenerarCFDI40 generarCFDI40) throws Exception {
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosRfcEmisor(generarCFDI40.getCredenciales().getCuenta(), 4);		
		Comprobante comprobante = new Comprobante();
		
		/**
		 * Fecha
		 */
		comprobante.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

		/**
		 * Serie y Folio
		 */
		comprobante.setSerie(generarCFDI40.getCfdi().getSerie());
		comprobante.setFolio(generarCFDI40.getCfdi().getFolio());

		/**
		 * Información general
		 */
		comprobante.setVersion(env.getProperty("cfdi.version.4"));
		comprobante.setTipoDeComprobante(CTipoDeComprobante.P);
		comprobante.setMoneda(CMoneda.fromValue(generarCFDI40.getCfdi().getMoneda()));
		comprobante.setTotal(generarCFDI40.getCfdi().getTotal());
		comprobante.setSubTotal(generarCFDI40.getCfdi().getSubTotal());
		comprobante.setLugarExpedicion(generarCFDI40.getCfdi().getLugarExpedicion());
		comprobante.setExportacion(generarCFDI40.getCfdi().getExportacion());
		comprobante.setNoCertificado(datosMarcaDto.getNumeroCertificado());
		
		
		if(generarCFDI40.getCfdi().getCfdiRelacionados()!=null) {
			if(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R() !=null) {
				if(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getCfdiRelacionado().size()>0) {
					Comprobante.CfdiRelacionados cfdiRelacionadosV4 = new ObjectFactory().createComprobanteCfdiRelacionados();
					cfdiRelacionadosV4.setTipoRelacion(generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getTipoRelacion());
					for (facturacion.domain.dto.referencia.cfdi.CfdiRelacionados.CfdiRelacionados40R.CfdiRelacionado cfdiRelacionado : generarCFDI40.getCfdi().getCfdiRelacionados().getCfdiRelacionados40R().getCfdiRelacionado()) {
						if(cfdiRelacionado.getCfdiRelacionado40R()!=null) {
							CfdiRelacionado cfdiRelacionadoV4 = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
							cfdiRelacionadoV4.setUUID(cfdiRelacionado.getCfdiRelacionado40R().getUUID());
							cfdiRelacionadosV4.getCfdiRelacionado().add(cfdiRelacionadoV4);
						}
					}
					comprobante.getCfdiRelacionados().add(cfdiRelacionadosV4);
				}				
			}
			
		}
		
		/**
		 * Emisor
		 */
		Emisor emisor = new ObjectFactory().createComprobanteEmisor();
		emisor.setNombre(datosMarcaDto.getRazonSocialMarca());
		emisor.setRfc(datosMarcaDto.getRfcMarca());
		emisor.setRegimenFiscal(String.valueOf(generarCFDI40.getCfdi().getEmisor().getRegimenFiscal()) );
		comprobante.setEmisor(emisor);
		
		/**
		 * Receptor
		 */
		Receptor receptor = new ObjectFactory().createComprobanteReceptor();
		receptor.setNombre(generarCFDI40.getCfdi().getReceptor().getNombre());
		receptor.setRfc(generarCFDI40.getCfdi().getReceptor().getRfc());
		receptor.setUsoCFDI(CUsoCFDI.fromValue(generarCFDI40.getCfdi().getReceptor().getUsoCFDI()));
		receptor.setRegimenFiscalReceptor(String.valueOf(generarCFDI40.getCfdi().getReceptor().getRegimenFiscalReceptor()));
		receptor.setDomicilioFiscalReceptor(generarCFDI40.getCfdi().getReceptor().getDomicilioFiscalReceptor());
		comprobante.setReceptor(receptor);

		
		/**
		 * Concepto
		 */		
		Conceptos conceptos = new ObjectFactory().createComprobanteConceptos();
		if(generarCFDI40.getCfdi().getConceptos()!=null) {
			if(generarCFDI40.getCfdi().getConceptos().getConcepto40R().size()>0) {
				for (Concepto40R concepto40r : generarCFDI40.getCfdi().getConceptos().getConcepto40R()) {
					Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
					concepto.setCantidad(concepto40r.getCantidad());
					concepto.setClaveProdServ(concepto40r.getClaveProdServ());
					concepto.setNoIdentificacion(concepto40r.getNoIdentificacion());
					concepto.setClaveUnidad(concepto40r.getClaveUnidad());
					concepto.setUnidad(concepto40r.getUnidad());
					concepto.setObjetoImp(concepto40r.getObjetoImp());
					concepto.setDescripcion(concepto40r.getDescripcion());
					concepto.setValorUnitario(concepto40r.getValorUnitario());
					concepto.setImporte(concepto40r.getImporte());										
					conceptos.getConcepto().add(concepto);					
				}
			}
		}
		comprobante.setConceptos(conceptos);
		
		/**
		 * Complemento Pagos
		 */
		Comprobante.Complemento complemento = new Comprobante.Complemento();
		if(generarCFDI40.getCfdi().getPagos()!=null) {
			Pagos pago = new Pagos();
			pago.setVersion(env.getProperty("complemento.pagos.xml.default.version2"));
			if(generarCFDI40.getCfdi().getPagos().getTotales()!=null) {
				Pagos.Totales totales = new Pagos.Totales();
				totales.setMontoTotalPagos(generarCFDI40.getCfdi().getPagos().getTotales().getMontoTotalPagos());
				pago.setTotales(totales);				
			}
			if(generarCFDI40.getCfdi().getPagos().getPago()!=null) {				
				Pagos.Pago newPago = new Pagos.Pago();				
				newPago.setFechaPago(utilsService.toXmlGregorianCalendar(generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getFechaPago().toGregorianCalendar().getTime(), "yyyy-MM-dd'T'HH:mm:ss"));
				newPago.setMonedaP(CMoneda.fromValue(generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getMonedaP()));
				newPago.setTipoCambioP(generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getTipoCambioP());
				newPago.setFormaDePagoP(generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getFormaDePagoP());
				newPago.setMonto(generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getMonto());
//				newPago.setCtaOrdenante(pagoDto.getNumeroCuenta() == null ? null : pagoDto.getNumeroCuenta().trim());
//				newPago.setRfcEmisorCtaOrd(pagoDto.getRfcBanco() == null ? null : pagoDto.getRfcBanco().trim());
//				newPago.setNomBancoOrdExt(pagoDto.getNombreBanco());
//				newPago.setNumOperacion(pagoDto.getNumeroOperacion()==null?null:pagoDto.getNumeroOperacion().trim());
				PagosPagoDoctoRelacionado20R relacionado20r = generarCFDI40.getCfdi().getPagos().getPago().getPagosPago20R().getDoctoRelacionado().getPagosPagoDoctoRelacionado20R();

				Pagos.Pago.DoctoRelacionado facturaRelacionada = new Pagos.Pago.DoctoRelacionado();
				facturaRelacionada.setIdDocumento(relacionado20r.getIdDocumento());
				facturaRelacionada.setSerie(relacionado20r.getSerie());
				facturaRelacionada.setFolio(relacionado20r.getFolio());
				facturaRelacionada.setObjetoImpDR(relacionado20r.getObjetoImpDR());
				facturaRelacionada.setMonedaDR(CMoneda.fromValue(relacionado20r.getMonedaDR()));
				facturaRelacionada.setEquivalenciaDR(relacionado20r.getEquivalenciaDR());
				facturaRelacionada.setNumParcialidad(relacionado20r.getNumParcialidad());
				facturaRelacionada.setImpSaldoAnt(relacionado20r.getImpSaldoAnt());
				facturaRelacionada.setImpSaldoInsoluto(relacionado20r.getImpSaldoInsoluto());
				facturaRelacionada.setImpPagado(relacionado20r.getImpPagado());
								
				newPago.getDoctoRelacionado().add(facturaRelacionada);
									
				pago.getPago().add(newPago);
			}
			
			complemento.getAny().add(pago);
		}
		comprobante.setComplemento(complemento);
		
		String xml = utilsCfdi4Service.createXmlFromComprobante(comprobante);
		log.info(xml);
		comprobante.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		comprobante.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  utilsCfdi4Service.createXmlFromComprobante(comprobante);
		return xmlOriginalSello;
	}
	
}
