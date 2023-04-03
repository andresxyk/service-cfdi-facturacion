package com.gda.cfdi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.CClaveProductoServicioSatDto;
import com.gda.cfdi.dto.ConceptoDto;
import com.gda.cfdi.dto.DatosFiscales;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.NotaCreditoDto;
import com.gda.cfdi.dto.TDatoFiscalDto;
import com.gda.cfdi.dto.TNotaCreditoEntityDto;

import facturacion.domain.dto.DatosCfdiDto;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._4.Comprobante.Conceptos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Retenciones;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class CfdiNotaCreditoV4Service {

private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;	
	@Autowired
	private ConsultaService consultaService;	
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private UtilsCfdi4Service utilsCfdi4Service;
	
	public TNotaCreditoEntityDto generarCfdiOrden(NotaCreditoDto creditoDto) throws Exception {
//		timbrado.cadena.notacredito=1|@uuid@|@numNotaCredito@|@numFactura@|@cveFormaPago@|@cveMetodoPago@|@conceptos@						
//		timbrado.cadena.notacredito.concepto=@codigoConcepto@|@cantidad@|@cveUnidad@|@descripcion@|@iva@|
		try {			
			String uuid = creditoDto.getUuid();
			TNotaCreditoEntityDto creditoEntityDto = consultaService.obtenerNotaCredito(creditoDto.getNumNotaCredito());
			Integer razon = creditoEntityDto.getCentidalegal();
			String formaPago = creditoDto.getFormaPago();
			String metodoPago = creditoDto.getMetodoPago();
			
			String codigoNota = "";
			Integer cantidadNota = 0;
			String claveNota = "";
			String conceptoNota = "";
			String iva ="";
			for (ConceptoDto concepto : creditoDto.getConceptos()) {
				if (concepto.getCodigoOlabAzteca() == null || concepto.getCodigoOlabAzteca().isEmpty()) {
					codigoNota = "84111506";
				} else {
					codigoNota = concepto.getCodigoOlabAzteca();
				}
				cantidadNota = concepto.getCantidad();
				if (concepto.getClaveUnidad() == null || concepto.getClaveUnidad().isEmpty()) {
					claveNota = "NO APLICA";
				} else {
					claveNota = concepto.getClaveUnidad();
				}
				conceptoNota = concepto.getNombreConcepto();
				iva = concepto.getIva();			
			}
			
			Integer cmarca = consultaService.obtenerMarcaConvenio(creditoEntityDto.getCconvenio());
			Integer razonSocialJen = creditoEntityDto.getCentidalegal();
			DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosMarcaAnticipada(cmarca, razonSocialJen, 4);
			
			Comprobante cfdi40 = new Comprobante();
			cfdi40.setVersion("4.0");
			cfdi40.setExportacion("01");
			cfdi40.setFolio(String.valueOf(creditoEntityDto.getUfoliofactura()));
			cfdi40.setSerie(creditoEntityDto.getSserie());
			cfdi40.setTipoCambio(BigDecimal.valueOf(1));
			if (cmarca.equals(15)) {
				cfdi40.setFecha(utilsService.toXmlGregorianCalendar(utilsService.sumarORestarMinutosAFecha(new Date(), -60),
						"yyyy-MM-dd'T'HH:mm:ss"));
			} else {
				
				cfdi40.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
			}
			cfdi40.setNoCertificado(datosMarcaDto.getNumeroCertificado());
			cfdi40.setMoneda(CMoneda.MXN);
			cfdi40.setTipoDeComprobante(CTipoDeComprobante.E);
			cfdi40.setTotal(creditoEntityDto.getMtotal().setScale(2, BigDecimal.ROUND_HALF_UP));
			if (cmarca==1 || cmarca==4 || cmarca==7){	
				
				if(creditoEntityDto.getCconvenio().equals(1605) || creditoEntityDto.getCconvenio().equals(226) || 
						creditoEntityDto.getCconvenio().equals(1224) || creditoEntityDto.getCconvenio().equals(191) ||
						creditoEntityDto.getCconvenio().equals(1129) || creditoEntityDto.getCconvenio().equals(1225) ||
						creditoEntityDto.getCconvenio().equals(8881) || creditoEntityDto.getCconvenio().equals(200) ||
						creditoEntityDto.getCconvenio().equals(3760) || creditoEntityDto.getCconvenio().equals(1227) ||
						creditoEntityDto.getCconvenio().equals(1228) || creditoEntityDto.getCconvenio().equals(1610) ||
						creditoEntityDto.getCconvenio().equals(198) || creditoEntityDto.getCconvenio().equals(1611) ||
						creditoEntityDto.getCconvenio().equals(197) || creditoEntityDto.getCconvenio().equals(3759) ||
						creditoEntityDto.getCconvenio().equals(1604) || creditoEntityDto.getCconvenio().equals(7922) ||
						creditoEntityDto.getCconvenio().equals(1464) || creditoEntityDto.getCconvenio().equals(1609) ||
						creditoEntityDto.getCconvenio().equals(3080)
						|| creditoEntityDto.getCconvenio().equals(1189) || creditoEntityDto.getCconvenio().equals(8344)	
						|| creditoEntityDto.getCconvenio().equals(1971) || creditoEntityDto.getCconvenio().equals(183)	
						|| creditoEntityDto.getCconvenio().equals(1156)
						|| creditoEntityDto.getCconvenio().equals(391)
						|| creditoEntityDto.getCconvenio().equals(364)|| creditoEntityDto.getCconvenio().equals(947)
						|| creditoEntityDto.getCconvenio().equals(12234)|| creditoEntityDto.getCconvenio().equals(7986)
						){
					
					cfdi40.setLugarExpedicion("57708");
					
				}else{				
					cfdi40.setLugarExpedicion("15710");
				}			
				
//				cfdi40.setLugarExpedicion("11560");// siempre es el mismo CP ya que siempre se expide en OMEGA
			}else if (cmarca==5){
				cfdi40.setLugarExpedicion("64040");
			}else if ( cmarca==15){
				cfdi40.setLugarExpedicion("31203");	
			}
			cfdi40.setFormaPago(formaPago);
			
			if(metodoPago.equals("PPD")){
				cfdi40.setMetodoPago(CMetodoPago.PPD);				
			}else{
				cfdi40.setMetodoPago(CMetodoPago.PUE);
			}
			
			Emisor emisor = new Emisor();
			log.info("razonSocialMarca--->>>  " + datosMarcaDto.getRazonSocialMarca());
			emisor.setNombre(utilsService.darFormatoCFDI(datosMarcaDto.getRazonSocialMarca())); 
			log.info("rfcMarca--->>>  " + datosMarcaDto.getRfcMarca());
			emisor.setRfc(datosMarcaDto.getRfcMarca()); 
			emisor.setRegimenFiscal("601");
			cfdi40.setEmisor(emisor);

			// Receptor
			Receptor receptor = new Receptor();
			boolean convenioKdato = false;
			DatosFiscales datoFiscal = consultaService
					.obtenerDatosFiscalesByCConvenioAndBconvenio(creditoEntityDto.getKdatofiscal(), convenioKdato);
			TDatoFiscalDto datoFiscalDto = consultaService.getTDatoFiscalById(datoFiscal.getKdatofiscal());
			DatosCfdiDto datosCfdiDto = consultaService.getDatosCfdiByConvenio(creditoEntityDto.getCconvenio());
			log.info("datoFiscal.getSrazonsocial()::::   " + datoFiscal.getSrazonsocial());
			receptor.setNombre(utilsService.darFormatoCFDI(datoFiscal.getSrazonsocial()));
			receptor.setRfc(datoFiscal.getSrfc());
			receptor.setUsoCFDI(CUsoCFDI.G_02);
			receptor.setRegimenFiscalReceptor(datoFiscalDto.getSclaveregimenfiscal());
			receptor.setDomicilioFiscalReceptor(datoFiscalDto.getCpostalcliente());
			
			cfdi40.setReceptor(receptor);
			
			if(creditoDto.isSustitucion()){
				Comprobante.CfdiRelacionados cfdiRelacionados = new ObjectFactory().createComprobanteCfdiRelacionados();
				CfdiRelacionado cfdiRelacionado = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
				cfdiRelacionado.setUUID(uuid);
				cfdiRelacionados.setTipoRelacion("04");
				cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
				cfdi40.getCfdiRelacionados().add(cfdiRelacionados);
			}else{
				Comprobante.CfdiRelacionados cfdiRelacionados = new ObjectFactory().createComprobanteCfdiRelacionados();
				CfdiRelacionado cfdiRelacionado = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
				cfdiRelacionado.setUUID(uuid);
				cfdiRelacionados.setTipoRelacion("01");
				cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
				cfdi40.getCfdiRelacionados().add(cfdiRelacionados);
			}
			
			// conceptos
			Conceptos conceptos = new Conceptos();
			Concepto concepto1 = new Concepto();
			BigDecimal importeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal importeBaseTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal importePadre = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal importeRetencion = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

			concepto1.setCantidad(new BigDecimal(cantidadNota).setScale(2));
			log.info("codigo::::   " + codigoNota);
			CClaveProductoServicioSatDto claveProductoServicioSat = null;
			try {
				Integer.parseInt(codigoNota);
				claveProductoServicioSat = consultaService
						.findCClaveProductoServicioSatById(Integer.valueOf(codigoNota));
			} catch (NumberFormatException e) {
				claveProductoServicioSat = null;
			}
			
			log.info("lstCClaveProductoServicioSat--->>>>>>>    " + claveProductoServicioSat);
			concepto1.setClaveProdServ(claveProductoServicioSat!=null ? claveProductoServicioSat.getSclaveproductoserviciosat() : "84111506");
//			concepto1.setNoIdentificacion(String.valueOf(codigoNota));
			concepto1.setNoIdentificacion("NO APLICA");
			concepto1.setClaveUnidad("ACT");
			concepto1.setUnidad("Actividad");
			concepto1.setDescripcion(utilsService.darFormatoCFDI(conceptoNota));// (2,
			concepto1.setObjetoImp("02");														// BigDecimal.ROUND_HALF_UP)
			concepto1.setValorUnitario(creditoEntityDto.getMtotal().subtract(creditoEntityDto.getMiva()).setScale(2,
					BigDecimal.ROUND_HALF_UP)); // tOrdenExamen.getMtotal().divide(BigDecimal.valueOf(1.16),
			// 2,RoundingMode.CEILING));
			log.info("concepto1.getValorUnitario()-->>  " + concepto1.getValorUnitario());
			concepto1.setImporte(creditoEntityDto.getMtotal().subtract(creditoEntityDto.getMiva()).setScale(2,
					BigDecimal.ROUND_HALF_UP)); //// tOrdenExamen.getMtotal().divide(BigDecimal.valueOf(1.16),
			//// 2, RoundingMode.CEILING));
			log.info("concepto1.getImporte()-->  " + concepto1.getImporte());
			concepto1.setImporte(
					concepto1.getImporte().multiply(concepto1.getCantidad()).setScale(2, BigDecimal.ROUND_HALF_UP)); /// concepto1.getCantidad()).setScale(2));
			log.info("concepto1.getImporte()2-->  " + concepto1.getImporte());

			importePadre = importePadre.add(concepto1.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));

			conceptos.getConcepto().add(concepto1);
			
			// Impuestos
			Impuestos impuestos = new Impuestos();

			Traslados traslados = new Traslados();

			Traslado traslado = new Traslado();
			
			
			Retenciones retenciones = new Retenciones();
			Retencion retencion = new Retencion();
			
			retencion.setImporte(concepto1.getImporte().multiply(new BigDecimal(".06")).setScale(2, BigDecimal.ROUND_HALF_UP));
			retencion.setImpuesto("002");
			retencion.setTasaOCuota(new BigDecimal(0.06).setScale(6, RoundingMode.HALF_UP));
			retencion.setTipoFactor(CTipoFactor.TASA);
			retencion.setBase(concepto1.getImporte());
			
			if(creditoDto.isRetencion()){
				retenciones.getRetencion().add(retencion);
				impuestos.setRetenciones(retenciones);		
				importeRetencion = importeRetencion.add(retencion.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
			// importe examen
			traslado.setImporte(creditoEntityDto.getMiva().setScale(2, BigDecimal.ROUND_HALF_UP)); // tOrdenExamen.getMsubtotal().subtract(tOrdenExamen.getMiva()).setScale(2));
			// //tOrdenExamen.getMiva().setScale(2,BigDecimal.ROUND_HALF_UP));
			log.info("traslado.getImporte()--->>>  " + traslado.getImporte());

			traslado.setBase(
					creditoEntityDto.getMtotal().subtract(traslado.getImporte()).setScale(2, BigDecimal.ROUND_HALF_UP)); // tOrdenExamen.getMtotal().divide(BigDecimal.valueOf(1.16),
			// 2,RoundingMode.CEILING));
			log.info("traslado.getBase()--->>>  " + traslado.getBase());
			traslado.setBase(traslado.getBase().multiply(concepto1.getCantidad()).setScale(2, BigDecimal.ROUND_HALF_UP)); // concepto1.getCantidad()).setScale(2));
			log.info("traslado.getBase*********--->>>  " + traslado.getBase());
			
			if (iva.equals("0.16")) {
				traslado.setImporte(traslado.getImporte().multiply(concepto1.getCantidad()).setScale(2, BigDecimal.ROUND_HALF_UP)); // concepto1.getCantidad()).setScale(2));
			}else{
				traslado.setImporte(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP)); // concepto1.getCantidad()).setScale(2));
			}
			importeBaseTotal = importeBaseTotal.add(traslado.getBase().setScale(2, BigDecimal.ROUND_HALF_UP));
			log.info("traslado.getImporte()2222--->>>  " + traslado.getImporte());
			importeTotal = importeTotal.add(traslado.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			traslado.setImpuesto("002");
			traslado.setTipoFactor(CTipoFactor.TASA);
			
			if (iva.equals("0.16")) {
				traslado.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));			
			}else{
				traslado.setTasaOCuota(new BigDecimal(0.00).setScale(6, BigDecimal.ROUND_HALF_UP));
			}
			
			traslados.getTraslado().add(traslado);
			impuestos.setTraslados(traslados);
			concepto1.setImpuestos(impuestos);
			cfdi40.setConceptos(conceptos);
			cfdi40.setSubTotal(importePadre);
			
			if(creditoDto.isRetencion()){
				cfdi40.setTotal(cfdi40.getTotal().subtract(importeRetencion).setScale(2, BigDecimal.ROUND_DOWN));
			}

			log.info("importeTotal-->>>  " + importeTotal);

			mx.gob.sat.cfd._4.Comprobante.Impuestos impuestos2 = new mx.gob.sat.cfd._4.Comprobante.Impuestos();
			mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados traslados2 = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados();
			List<mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado> lstTrasladosTotales = new ArrayList<mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado>();
			mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Traslados.Traslado();
			if (iva.equals("0.16")) {
				trasladosTotales.setImporte(importeTotal.setScale(2));
			}else{
				trasladosTotales.setImporte(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			trasladosTotales.setBase(importeBaseTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
			
			log.info("trasladosTotales.getImporte()-->>  " + trasladosTotales.getImporte());
			trasladosTotales.setImpuesto("002");
			if (iva.equals("0.16")) {
				trasladosTotales.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));
			}else{
				trasladosTotales.setTasaOCuota(new BigDecimal(0.00).setScale(6, BigDecimal.ROUND_HALF_UP));
			}
			trasladosTotales.setTipoFactor(CTipoFactor.TASA);
//			lstTrasladosTotales.add(trasladosTotales);
			traslados2.getTraslado().add(trasladosTotales);
			impuestos2.setTraslados(traslados2);
			if (iva.equals("0.16")) {
				impuestos2.setTotalImpuestosTrasladados(importeTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
			}else{
				impuestos2.setTotalImpuestosTrasladados(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			//se omite 13-04-2018
			//impuestos2.setTotalImpuestosRetenidos(BigDecimal.ZERO.setScale(2));
			
			if(creditoDto.isRetencion()){
				mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones retenciones2 = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones();
				List<mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones.Retencion> lstRetencionesTotales = new ArrayList<mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones.Retencion>();
				mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones.Retencion retencionTotales = new mx.gob.sat.cfd._4.Comprobante.Impuestos.Retenciones.Retencion();
				retencionTotales.setImporte(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));
				retencionTotales.setImpuesto("002");
				lstRetencionesTotales.add(retencionTotales);
				retenciones2.getRetencion().add(retencionTotales);
				impuestos2.setRetenciones(retenciones2);
				impuestos2.setTotalImpuestosRetenidos(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));	
			}
			
			cfdi40.setImpuestos(impuestos2);
			
			String xml = utilsCfdi4Service.createXmlFromComprobante(cfdi40);
			cfdi40.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
			String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
			cfdi40.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
			
			String xmlOriginalSello =  utilsCfdi4Service.createXmlFromComprobante(cfdi40);
			
			creditoEntityDto.setScadenaoriginal(cadenaOriginal);
			creditoEntityDto.setSxml(xmlOriginalSello);
			
			return creditoEntityDto;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
	}
	
}
