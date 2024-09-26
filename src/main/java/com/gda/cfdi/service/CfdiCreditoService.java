package com.gda.cfdi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.DatosFiscalesDto;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.EstudioDto;
import com.gda.cfdi.dto.TDatoFiscalDto;
import com.gda.cfdi.dto.TFacturaCreditoDto;
import com.gda.cfdi.dto.TFacturaEntityDto;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfd._3.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._3.Comprobante.Conceptos;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto.Impuestos;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto.Impuestos.Retenciones;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto.Impuestos.Traslados;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd._3.Comprobante.Emisor;
import mx.gob.sat.cfd._3.Comprobante.Receptor;
import mx.gob.sat.cfd._3.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class CfdiCreditoService {

private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;
	
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	public TFacturaEntityDto generarCfdiOrden( Integer folio, Integer tipofactura, double msubtotal, double miva,  double mtotal,
			 String strnocuenta,  String strmetodopago,  String uuidSustitucion, boolean isSustitucion,  boolean isDescuento,  
			 String descuentos, String notaDescuento,  boolean isRetencion,  Integer marca, String descripcionFactura) throws Exception {
		
		String sserie = marca.equals(1)?"A":marca.equals(4)?"AZ":marca.equals(5)?"AS":marca.equals(15)?"ASL":
			marca.equals(7)?"AJP":marca.equals(8)?"AJL":marca.equals(19)?"AFN":marca.equals(20)?"AJK":marca.equals(21)?"AAS":
				marca.equals(16)?"AMO":marca.equals(22)?"APO":marca.equals(25)?"ABR":marca.equals(26)?"APR":null;
		Integer csucursal = marca.equals(1)?1003:marca.equals(4)?1012:marca.equals(5)?1013:marca.equals(15)?1017:
			marca.equals(7)?1014:marca.equals(8)?1015:marca.equals(19)?1020:marca.equals(20)?1021:
			marca.equals(21)?1022:marca.equals(16)?1026:marca.equals(22)?1023:marca.equals(25)?1024:marca.equals(26)?1025:null;
		
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosMarca(marca);
		
		TFacturaCreditoDto dtoFac = consultaService.getTFacturaCreditoDtoBySerieAndFolio(sserie, folio);
//		if(dtoFac.getUuid().length()>0){
//			throw new Exception("La factura "+sserie+"-"+folio+" ya esta facturada.");
//		}
		consultaService.updateAjusteFactura(dtoFac.getIdFactura(), dtoFac.getUserId(), new BigDecimal(msubtotal), new BigDecimal(miva), new BigDecimal(mtotal));
		consultaService.updateMetodoPago(strnocuenta.trim(), strmetodopago.trim(), dtoFac.getIdConvenio());
		
		TFacturaCreditoDto dto = consultaService.getTFacturaCreditoDtoBySerieAndFolio(sserie, folio);
		
		Comprobante comprobante = this.buildCFDI(sserie, csucursal, folio, dto, marca, isSustitucion, uuidSustitucion, tipofactura,
				descripcionFactura, isRetencion, isDescuento, descuentos, notaDescuento,datosMarcaDto);
		
		String xml = utilsService.createXmlFromComprobante(comprobante);
		comprobante.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		comprobante.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  utilsService.createXmlFromComprobante(comprobante);
		
		TFacturaEntityDto tfactura = new TFacturaEntityDto();
		tfactura.setKfactura(dtoFac.getIdFactura());
		tfactura.setMsubtotal(comprobante.getSubTotal());
		tfactura.setMiva(comprobante.getImpuestos().getTotalImpuestosTrasladados());
		tfactura.setMtotal(comprobante.getTotal());
		tfactura.setUser_id_change(dtoFac.getUserId());
		tfactura.setSxml(xmlOriginalSello);
		tfactura.setSxmlsello("");
		tfactura.setSurl("");
		tfactura.setSuddi("");
		tfactura.setScadenaoriginal(cadenaOriginal.length()>4000?cadenaOriginal.substring(0, 3999):cadenaOriginal);
		tfactura.setSsellodigital(comprobante.getSello());
		return tfactura;
	}
	
	public Comprobante buildCFDI(String sserie, Integer csucursal, Integer ufoliofactura, TFacturaCreditoDto dto, Integer marca, boolean sustitucion, 
			String uuidSustitucion, Integer tipofactura, String descripcionFactura, boolean isRetencion, boolean isDescuento,
			String descuentos, String notaDescuento, DatosMarcaDto datosMarcaDto) throws Exception {
		Comprobante comprobante = new Comprobante();
		
		comprobante.setVersion(env.getProperty("cfdi.version"));
		comprobante.setSerie(sserie);
		comprobante.setFolio(ufoliofactura.toString());
		comprobante.setTipoCambio(BigDecimal.valueOf(1));
		if(marca.equals(15)){
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(utilsService.sumarORestarMinutosAFecha(dto.getDregistro(),-60), "yyyy-MM-dd'T'HH:mm:ss"));
		}else{
			
			comprobante.setFecha(utilsService.toXmlGregorianCalendar(dto.getDregistro(), "yyyy-MM-dd'T'HH:mm:ss"));
		}
		
		if(marca.equals(1)){			
			if(dto.getIdConvenio().equals(1605) || dto.getIdConvenio().equals(226) || 
					dto.getIdConvenio().equals(1224) || dto.getIdConvenio().equals(191) ||
					dto.getIdConvenio().equals(1129) || dto.getIdConvenio().equals(1225) ||
					dto.getIdConvenio().equals(8881) || dto.getIdConvenio().equals(200) ||
					dto.getIdConvenio().equals(3760) || dto.getIdConvenio().equals(1227) ||
					dto.getIdConvenio().equals(1228) || dto.getIdConvenio().equals(1610) ||
					dto.getIdConvenio().equals(198) || dto.getIdConvenio().equals(1611) ||
					dto.getIdConvenio().equals(197) || dto.getIdConvenio().equals(3759) ||
					dto.getIdConvenio().equals(1604) || dto.getIdConvenio().equals(7922) ||
					dto.getIdConvenio().equals(1464) || dto.getIdConvenio().equals(1609) ||
					dto.getIdConvenio().equals(3080)
					|| dto.getIdConvenio().equals(1189) || dto.getIdConvenio().equals(8344)
					|| dto.getIdConvenio().equals(1971) || dto.getIdConvenio().equals(183)
					|| dto.getIdConvenio().equals(1156) 
					|| dto.getIdConvenio().equals(391) 
					|| dto.getIdConvenio().equals(364)|| dto.getIdConvenio().equals(947)
					|| dto.getIdConvenio().equals(12234)|| dto.getIdConvenio().equals(7986)
					){
				
				comprobante.setLugarExpedicion("57708");
				
			}else{				
				comprobante.setLugarExpedicion("15710");
			}
		}else if(marca.equals(5)){
			comprobante.setLugarExpedicion("64040");
		}else if(marca.equals(15)){
			comprobante.setLugarExpedicion("31203");
		}else if(marca.equals(4) || marca.equals(7) || marca.equals(8)){
			comprobante.setLugarExpedicion("15710");
		}
		
		
		
		DatosFiscalesDto fiscalesDto = consultaService.getDatosFiscalesByConvenio(dto.getIdConvenio());
		String metodoDePago = "";
		if(fiscalesDto!=null){
			metodoDePago = fiscalesDto.getStipopago().trim();
		}else{
			metodoDePago ="99";
		}

		switch (metodoDePago) {
		case "01 - Efectivo":
			comprobante.setFormaPago("01");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "1":
			comprobante.setFormaPago("01");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "02":
			comprobante.setFormaPago("02");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "02-Cheque Nominativo":
			comprobante.setFormaPago("02");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "02, 03":
			comprobante.setFormaPago("02");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "03 - Transferencia electronica de fondos":
			comprobante.setFormaPago("03");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "03":
			comprobante.setFormaPago("03");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "03 Transferencia electronica de fondos":
			comprobante.setFormaPago("03");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			break;
		case "99":
			comprobante.setFormaPago("99");
			comprobante.setMetodoPago(CMetodoPago.PPD);
			break;
		default:
			comprobante.setFormaPago("99");
			comprobante.setMetodoPago(CMetodoPago.PPD);
			break;
		}
		
		comprobante.setNoCertificado(datosMarcaDto.getNumeroCertificado());
		comprobante.setMoneda(CMoneda.MXN);
		comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
		
		/**
		 * Sustitucion
		 */
		if(sustitucion){
			CfdiRelacionados cfdiRelacionados = new ObjectFactory().createComprobanteCfdiRelacionados();
			CfdiRelacionado cfdiRelacionado = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
			cfdiRelacionado.setUUID(uuidSustitucion);
			cfdiRelacionados.setTipoRelacion("04");
			cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
			comprobante.setCfdiRelacionados(cfdiRelacionados);
		}
		
		/**
		 * Emisor
		 */
		Emisor emisor = new ObjectFactory().createComprobanteEmisor();
		emisor.setNombre(utilsService.darFormatoCFDI(datosMarcaDto.getRazonSocialMarca()));
		emisor.setRfc(datosMarcaDto.getRfcMarca());
		emisor.setRegimenFiscal("601");
		comprobante.setEmisor(emisor);
				
		/**
		 * Receptor
		 */
		Receptor receptor = new ObjectFactory().createComprobanteReceptor();
		TDatoFiscalDto datoFiscalDto = consultaService.getTDatoFiscalById(dto.getIdDatoFiscal());
		receptor.setNombre(utilsService.darFormatoCFDI(datoFiscalDto.getSrazonsocial()));
		receptor.setRfc(datoFiscalDto.getSrfc());
		List<TDatoFiscalDto> listDatos = consultaService.getListDatosFiscalesByRfc(datoFiscalDto.getSrfc());
		if(listDatos.size()>0){
			receptor.setUsoCFDI(CUsoCFDI.P_01);
		}else{
			receptor.setUsoCFDI(CUsoCFDI.G_03);
		}
		
		switch (datoFiscalDto.getSrfc()) {
		case "ISS6001015A3":
			comprobante.setFormaPago("03");
			comprobante.setMetodoPago(CMetodoPago.PPD);
			receptor.setUsoCFDI(CUsoCFDI.G_03);
			break;
		case "FCU971006FS1":
			comprobante.setFormaPago("99");
			comprobante.setMetodoPago(CMetodoPago.PPD);
			receptor.setUsoCFDI(CUsoCFDI.P_01);
		break;	
		case "WME0503291Q5":
			comprobante.setFormaPago("99");
			comprobante.setMetodoPago(CMetodoPago.PUE);
			receptor.setUsoCFDI(CUsoCFDI.P_01);
		break;	
		default:
			break;
		}
		
		comprobante.setReceptor(receptor);
		
		/**
		 * Conceptos
		 */
		Conceptos conceptos = new ObjectFactory().createComprobanteConceptos();
		BigDecimal importeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importePadre = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importeTDescuento = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importeRetencion = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		if(tipofactura.equals(1) || tipofactura.equals(3)){
			BigDecimal msubtotalGlobal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			List<EstudioDto> listEstudios = consultaService.getListEstudiosByKfactura(dto.getIdFactura());
			for (EstudioDto estudioDto : listEstudios) {
				msubtotalGlobal = msubtotalGlobal.add(estudioDto.getImporte());
			}
			Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
			concepto.setDescuento(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
			concepto.setCantidad(new BigDecimal("1").setScale(0, BigDecimal.ROUND_HALF_UP));
			concepto.setClaveProdServ("85121800");
			concepto.setNoIdentificacion("NO APLICA");
			concepto.setClaveUnidad("E48");
			concepto.setUnidad("Unidad de Servicio");
			concepto.setDescripcion(descripcionFactura);
			concepto.setValorUnitario(msubtotalGlobal);
			concepto.setImporte(msubtotalGlobal);
			
			importePadre = importePadre.add(concepto.getImporte().setScale(4));
			
			conceptos.getConcepto().add(concepto);
			
			Impuestos impuestos = new ObjectFactory().createComprobanteConceptosConceptoImpuestos();
			Traslados traslados = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTraslados();
			Traslado traslado = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
			Retenciones retenciones = new ObjectFactory().createComprobanteConceptosConceptoImpuestosRetenciones();
			Retencion retencion = new ObjectFactory().createComprobanteConceptosConceptoImpuestosRetencionesRetencion();
			
			retencion.setImporte(concepto.getImporte().multiply(new BigDecimal(env.getProperty("cfdi.retencion.monto")))
					.setScale(2, BigDecimal.ROUND_HALF_UP));
			retencion.setImpuesto("002");
			retencion.setTasaOCuota(new BigDecimal(0.06).setScale(6, RoundingMode.HALF_UP));
			retencion.setTipoFactor(CTipoFactor.TASA);
			retencion.setBase(concepto.getImporte());
			if(isRetencion){
				retenciones.getRetencion().add(retencion);
				impuestos.setRetenciones(retenciones);
				importeRetencion = importeRetencion.add(retencion.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			
//			traslado.setBase(concepto.getImporte().subtract(concepto.getDescuento()).setScale(2, 4));
			traslado.setBase(concepto.getImporte().setScale(2, 4));
			traslado.setImporte(traslado.getBase().multiply(new BigDecimal(0.16)).setScale(2, BigDecimal.ROUND_HALF_UP));
			importeTotal = importeTotal.add(traslado.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			traslado.setImpuesto("002");
			traslado.setTipoFactor(CTipoFactor.TASA);
			traslado.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));
			
			traslados.getTraslado().add(traslado);
			impuestos.setTraslados(traslados);
			concepto.setImpuestos(impuestos);
			comprobante.setConceptos(conceptos);
					
		}else{
			List<EstudioDto> listEstudios = consultaService.getListEstudiosByKfactura(dto.getIdFactura());
			for (EstudioDto estudioDto : listEstudios) {
				
				Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
								
				concepto.setDescuento(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
				if(isDescuento){
			          String [] splitdesc = descuentos.split("\\|");
			          for(int i = 0; i<splitdesc.length; i++){
			        	  String [] splitDescuento = splitdesc[i].split("\\=");
			        	  if(splitDescuento.length>0){
			        		  System.out.println("splitDescuento[0]:"+splitDescuento[0]);
			        		  log.info("==============================================");
			        		  log.info(splitDescuento[0]);
			        		  log.info(estudioDto.getCexamen().toString());
			        		  if(splitDescuento[0].equals(estudioDto.getCexamen().toString())){
			        			  concepto.setDescuento(new BigDecimal(splitDescuento[1]));
			        			  log.info(splitDescuento[1]);
//			        			  descuento = splitDescuento[1];
			        		  }
			        	  }
			          }
		          }
				
				importeTDescuento = importeTDescuento.add(concepto.getDescuento());
				
				concepto.setCantidad(new BigDecimal(estudioDto.getCantidad()).setScale(0, BigDecimal.ROUND_HALF_UP));
				concepto.setClaveProdServ("85121800");
				concepto.setNoIdentificacion("NO APLICA");
				concepto.setClaveUnidad("E48");
				concepto.setUnidad("Unidad de Servicio");
				concepto.setDescripcion(estudioDto.getSexamen());
				concepto.setValorUnitario(estudioDto.getValorUnitario());
				concepto.setImporte(estudioDto.getValorUnitario()
						.multiply(new BigDecimal(estudioDto.getCantidad())).setScale(2));
//				concepto.setImporte(concepto.getImporte().subtract(concepto.getDescuento()).setScale(2));
				concepto.setImporte(concepto.getImporte().setScale(2));
				
				importePadre = importePadre.add(concepto.getImporte().setScale(4));
				
				conceptos.getConcepto().add(concepto);
				
				Impuestos impuestos = new ObjectFactory().createComprobanteConceptosConceptoImpuestos();
				Traslados traslados = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTraslados();
				Traslado traslado = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
				Retenciones retenciones = new ObjectFactory().createComprobanteConceptosConceptoImpuestosRetenciones();
				Retencion retencion = new ObjectFactory().createComprobanteConceptosConceptoImpuestosRetencionesRetencion();
				
				retencion.setImporte((concepto.getImporte().subtract(concepto.getDescuento()).setScale(2, 4)).multiply(new BigDecimal(env.getProperty("cfdi.retencion.monto")))
						.setScale(2, BigDecimal.ROUND_HALF_UP));
				retencion.setImpuesto("002");
				retencion.setTasaOCuota(new BigDecimal(0.06).setScale(6, RoundingMode.HALF_UP));
				retencion.setTipoFactor(CTipoFactor.TASA);
				retencion.setBase(concepto.getImporte().subtract(concepto.getDescuento()).setScale(2, 4));
				if(isRetencion){
					retenciones.getRetencion().add(retencion);
					impuestos.setRetenciones(retenciones);
					importeRetencion = importeRetencion.add(retencion.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				traslado.setBase(concepto.getImporte().subtract(concepto.getDescuento()).setScale(2, 4));
//				traslado.setBase(concepto.getImporte());
				traslado.setImporte(traslado.getBase().multiply(new BigDecimal(0.16)).setScale(2, BigDecimal.ROUND_HALF_UP));
				importeTotal = importeTotal.add(traslado.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
				traslado.setImpuesto("002");
				traslado.setTipoFactor(CTipoFactor.TASA);
				traslado.setTasaOCuota(new BigDecimal(0.16).setScale(6, BigDecimal.ROUND_HALF_UP));
				
				traslados.getTraslado().add(traslado);
				impuestos.setTraslados(traslados);
				concepto.setImpuestos(impuestos);
				comprobante.setConceptos(conceptos);
				
				
				
				
			}
			
		}
		
		comprobante.setSubTotal(importePadre.setScale(2));		
		comprobante.setDescuento(importeTDescuento.setScale(2));
		
		BigDecimal total1 = comprobante.getSubTotal().subtract(comprobante.getDescuento()).setScale(2, 1);
//		BigDecimal total1 = comprobante.getSubTotal();
		BigDecimal totalt = BigDecimal.ZERO.setScale(2, 4);
		totalt = totalt.add(total1);
		totalt = totalt.add(importeTotal);
		if(isRetencion){
			comprobante.setTotal(totalt.setScale(2));
			comprobante.setTotal(comprobante.getTotal().subtract(importeRetencion).setScale(2, BigDecimal.ROUND_DOWN));
		}else{
			comprobante.setTotal(totalt.setScale(2));
		}
		
		
		consultaService.updateMontosFactura(comprobante.getTotal(), comprobante.getSubTotal(), importeTotal,comprobante.getDescuento(), dto.getIdFactura());
		
		mx.gob.sat.cfd._3.Comprobante.Impuestos impuestos = new ObjectFactory().createComprobanteImpuestos();
		mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados traslados = new ObjectFactory().createComprobanteImpuestosTraslados();
//		List<mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado> lstTrasladosTotales = new ArrayList<mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado>();
		mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new ObjectFactory().createComprobanteImpuestosTrasladosTraslado();
		
		trasladosTotales.setImporte(importeTotal.setScale(2, BigDecimal.ROUND_DOWN));
		trasladosTotales.setImpuesto("002");
		trasladosTotales.setTasaOCuota(new BigDecimal(0.16).setScale(6, RoundingMode.HALF_UP));
		trasladosTotales.setTipoFactor(CTipoFactor.TASA);
//		lstTrasladosTotales.add(trasladosTotales);
		traslados.getTraslado().add(trasladosTotales);
		impuestos.setTraslados(traslados);		
		impuestos.setTotalImpuestosTrasladados(importeTotal.setScale(2, BigDecimal.ROUND_DOWN));
		
		if(isRetencion){
			mx.gob.sat.cfd._3.Comprobante.Impuestos.Retenciones retenciones = new ObjectFactory().createComprobanteImpuestosRetenciones();
			mx.gob.sat.cfd._3.Comprobante.Impuestos.Retenciones.Retencion retencionTotales = new ObjectFactory().createComprobanteImpuestosRetencionesRetencion();
			retencionTotales.setImporte(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));
			retencionTotales.setImpuesto("002");
			retenciones.getRetencion().add(retencionTotales);
			impuestos.setRetenciones(retenciones);
			impuestos.setTotalImpuestosRetenidos(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));
		}
		comprobante.setImpuestos(impuestos);
		
		
		return comprobante;
	}
	
}
