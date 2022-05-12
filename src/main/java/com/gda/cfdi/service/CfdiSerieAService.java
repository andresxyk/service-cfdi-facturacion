package com.gda.cfdi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.AnticipadaSerieADto;
import com.gda.cfdi.dto.CUsoCfdiDto;
import com.gda.cfdi.dto.ConceptoDto;
import com.gda.cfdi.dto.ConceptosExamenes;
import com.gda.cfdi.dto.DatosFiscales;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.TFacturaEntity;
import com.gda.cfdi.dto.TFacturaEntityDto;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.ObjectFactory;
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
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class CfdiSerieAService {
	private static final Logger log = LoggerFactory.getLogger(CfdiController.class);

	@Autowired
	private Environment env;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private UtilsService utilsService;

	public TFacturaEntityDto generarCfdiSerieA(AnticipadaSerieADto serieADto) throws Exception {
		try {
			Boolean isRFC = false;
			String convenioRfc = serieADto.getIdConvenio().toString();
			DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenio(serieADto.getIdConvenio());
			List<CUsoCfdiDto> listUsoCFDI = null;
			String usoCFDI = serieADto.getIdUsoCfdi();
			Integer marca = serieADto.getIdMarca();
			String subtotal = serieADto.getSubTotal();
			String formaPago = serieADto.getFormaPago();
			String metodoPago = serieADto.getMetodoPago();

			if (!utilsService.validarRFC(datosFiscales.getSrfc().trim())) {
				throw new Exception("El RFC del Emisor es invalido.");
			}
			if (datosFiscales.getSrfc().trim().length() == 13) {
				listUsoCFDI = consultaService.getListUsoCFDI(1);
			} else {
				listUsoCFDI = consultaService.getListUsoCFDI(2);
			}
			Boolean busoCfdi = false;
			if (usoCFDI.equals("G01") || usoCFDI.equals("G02") || usoCFDI.equals("G03") || usoCFDI.equals("I01")
					|| usoCFDI.equals("I02") || usoCFDI.equals("I03") || usoCFDI.equals("I04") || usoCFDI.equals("I05")
					|| usoCFDI.equals("I06") || usoCFDI.equals("I07") || usoCFDI.equals("I08") || usoCFDI.equals("D01")
					|| usoCFDI.equals("D02") || usoCFDI.equals("D03") || usoCFDI.equals("D04") || usoCFDI.equals("D05")
					|| usoCFDI.equals("D06") || usoCFDI.equals("D07") || usoCFDI.equals("D08") || usoCFDI.equals("D09")
					|| usoCFDI.equals("D10") || usoCFDI.equals("P01")) {
				busoCfdi = true;
			}
			if (!busoCfdi) {
				throw new Exception("El UsoCfdi es invalido.");
			}

			Integer cmarca = consultaService.obtenerMarcaConvenio(serieADto.getIdConvenio());

			String query = getConsultaInsertSeriA(new BigDecimal(subtotal.replaceAll(",", "")),
					serieADto.getIdConvenio(), cmarca, Integer.valueOf(marca));

			TFacturaEntity tFacturaEntity = consultaService.getTFacturaEntityQuery(query);

			List<ConceptosExamenes> lstConcepto = new ArrayList<ConceptosExamenes>();
			for (ConceptoDto conceptoDto : serieADto.getConceptos()) {
				ConceptosExamenes conceptoExamen = new ConceptosExamenes();

				log.info("setCodigo: " + "85121800");
				conceptoExamen.setCodigo("85121800");
				String clave = "";
				if (conceptoDto.getCodigoOlabAzteca() == null || conceptoDto.getCodigoOlabAzteca().isEmpty()) {
					clave = "NO APLICA";
				} else {
					clave = conceptoDto.getCodigoOlabAzteca();
				}
				log.info("setClave: " + String.valueOf(clave));
				conceptoExamen.setClave(String.valueOf(clave));
				log.info("setCantidad: " + Integer.valueOf(conceptoDto.getCantidad()));
				conceptoExamen.setCantidad(Integer.valueOf(conceptoDto.getCantidad()));
				String unidadMedida = "";
				if (conceptoDto.getClaveUnidad() == null || conceptoDto.getClaveUnidad().isEmpty()) {
					unidadMedida = "NO APLICA";
				} else {
					unidadMedida = conceptoDto.getClaveUnidad();
				}
				log.info("setUnidadMedida: " + String.valueOf(unidadMedida));
				conceptoExamen.setUnidadMedida(String.valueOf(unidadMedida));
				log.info("setConcepto: " + conceptoDto.getNombreConcepto().trim().toUpperCase());
				conceptoExamen.setConcepto(conceptoDto.getNombreConcepto().trim().toUpperCase());
				log.info("setPrecioUnitario: " + new BigDecimal(conceptoDto.getPrecioUnitatorio()).setScale(4));
				conceptoExamen.setPrecioUnitario(new BigDecimal(conceptoDto.getPrecioUnitatorio()).setScale(4));
				log.info("setIva: " + new BigDecimal(conceptoDto.getIva()).setScale(4));
				conceptoExamen.setIva(new BigDecimal(conceptoDto.getIva()).setScale(4));
				String unidad = "";
				if (conceptoDto.getUnidad() == null || conceptoDto.getUnidad().isEmpty()) {
					unidad = "NO APLICA";
				} else {
					unidad = conceptoDto.getUnidad();
				}
				log.info("setUnidad: " + unidad.trim().toUpperCase());
				conceptoExamen.setUnidad(unidad.trim().toUpperCase());
				conceptoExamen.setImporte(BigDecimal.ZERO);
				lstConcepto.add(conceptoExamen);
			}
			DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosMarcaAnticipada(cmarca, marca);
			Comprobante comprobante = this.buildCFDI(cmarca, marca, tFacturaEntity, formaPago, 
					metodoPago, serieADto.getSustitucion(), serieADto.getUuid(), serieADto.getRetencion(), usoCFDI, 
					lstConcepto,datosMarcaDto);
			
			
			String xml = utilsService.createXmlFromComprobante(comprobante);
			comprobante.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
			String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
			comprobante.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
			
			String xmlOriginalSello =  utilsService.createXmlFromComprobante(comprobante);
			
			TFacturaEntityDto tfactura = new TFacturaEntityDto();
			tfactura.setUfoliofactura(tFacturaEntity.getUfoliofactura());
			tfactura.setSserie(tFacturaEntity.getSserie());
			tfactura.setKfactura(tFacturaEntity.getKfactura());
			tfactura.setMsubtotal(comprobante.getSubTotal());
			tfactura.setMiva(comprobante.getImpuestos().getTotalImpuestosTrasladados());
			tfactura.setMtotal(comprobante.getTotal());
			tfactura.setUser_id_change(tFacturaEntity.getUser_id().intValue());
			tfactura.setSxml(xmlOriginalSello);
			tfactura.setSxmlsello("");
			tfactura.setSurl("");
			tfactura.setSuddi("");
			tfactura.setScadenaoriginal(cadenaOriginal.length()>4000?cadenaOriginal.substring(0, 3999):cadenaOriginal);
			tfactura.setSsellodigital(comprobante.getSello());
			return tfactura;
			

		} catch (Exception e) {
			throw e;
		}
	}

	public Comprobante buildCFDI(Integer cmarca, Integer razonJenner, TFacturaEntity tFacturaEntity, String formaPago,
			String metodoPago, boolean sustitucion, String uuid, boolean bRetencion, String usoCFDI,
			List<ConceptosExamenes> lstConcepto,DatosMarcaDto datosMarcaDto ) throws DatatypeConfigurationException {
		log.info("ufolio----->>>>>   " + tFacturaEntity.getUfoliofactura());

		Comprobante cfdi33 = new Comprobante();
		cfdi33.setVersion("3.3");
		cfdi33.setFolio(String.valueOf(tFacturaEntity.getUfoliofactura()));
		cfdi33.setSerie(tFacturaEntity.getSserie());
		cfdi33.setTipoCambio(BigDecimal.valueOf(1));
		if (cmarca.equals(15)) {
			cfdi33.setFecha(utilsService.toXmlGregorianCalendar(utilsService.sumarORestarMinutosAFecha(new Date(), -60),
					"yyyy-MM-dd'T'HH:mm:ss"));
		} else {

			cfdi33.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
		}
		cfdi33.setNoCertificado(datosMarcaDto.getNumeroCertificado());
		cfdi33.setMoneda(CMoneda.MXN);
		cfdi33.setTipoDeComprobante(CTipoDeComprobante.I);

		if (cmarca == 1 || cmarca == 4 || cmarca == 7) {

			if (tFacturaEntity.getCconvenio().equals(1605) || tFacturaEntity.getCconvenio().equals(226)
					|| tFacturaEntity.getCconvenio().equals(1224) || tFacturaEntity.getCconvenio().equals(191)
					|| tFacturaEntity.getCconvenio().equals(1129) || tFacturaEntity.getCconvenio().equals(1225)
					|| tFacturaEntity.getCconvenio().equals(8881) || tFacturaEntity.getCconvenio().equals(200)
					|| tFacturaEntity.getCconvenio().equals(3760) || tFacturaEntity.getCconvenio().equals(1227)
					|| tFacturaEntity.getCconvenio().equals(1228) || tFacturaEntity.getCconvenio().equals(1610)
					|| tFacturaEntity.getCconvenio().equals(198) || tFacturaEntity.getCconvenio().equals(1611)
					|| tFacturaEntity.getCconvenio().equals(197) || tFacturaEntity.getCconvenio().equals(3759)
					|| tFacturaEntity.getCconvenio().equals(1604) || tFacturaEntity.getCconvenio().equals(7922)
					|| tFacturaEntity.getCconvenio().equals(1464) || tFacturaEntity.getCconvenio().equals(1609)
					|| tFacturaEntity.getCconvenio().equals(3080) || tFacturaEntity.getCconvenio().equals(1189)
					|| tFacturaEntity.getCconvenio().equals(8344) || tFacturaEntity.getCconvenio().equals(1971)
					|| tFacturaEntity.getCconvenio().equals(183) || tFacturaEntity.getCconvenio().equals(1156)
					|| tFacturaEntity.getCconvenio().equals(391) || tFacturaEntity.getCconvenio().equals(364)
					|| tFacturaEntity.getCconvenio().equals(947) || tFacturaEntity.getCconvenio().equals(12234)
					|| tFacturaEntity.getCconvenio().equals(7986)) {

				cfdi33.setLugarExpedicion("57708");

			} else {
				cfdi33.setLugarExpedicion("11560");
			}

//			cfdi33.setLugarExpedicion("11560");// siempre es el mismo CP ya que siempre se expide en OMEGA
		} else if (cmarca == 5) {
			cfdi33.setLugarExpedicion("64040");
		} else if (cmarca == 15) {
			cfdi33.setLugarExpedicion("31203");

		}
		cfdi33.setFormaPago(formaPago);
		if (metodoPago.equals("PPD")) {
			cfdi33.setMetodoPago(CMetodoPago.PPD);
		} else if (metodoPago.equals("PUE")) {
			cfdi33.setMetodoPago(CMetodoPago.PUE);
		}
		if (sustitucion) {
			CfdiRelacionados cfdiRelacionados = new CfdiRelacionados();
			CfdiRelacionado cfdiRelacionado = new CfdiRelacionado();
			cfdiRelacionado.setUUID(uuid);
			cfdiRelacionados.setTipoRelacion("04");
			cfdiRelacionados.getCfdiRelacionado().add(cfdiRelacionado);
			cfdi33.setCfdiRelacionados(cfdiRelacionados);
		}
		// Emisor
		Emisor emisor = new Emisor();
		log.info("razonSocialMarca--->>>  " + datosMarcaDto.getRazonSocialMarca());
		emisor.setNombre(utilsService.darFormatoCFDI(datosMarcaDto.getRazonSocialMarca()));
		log.info("rfcMarca--->>>  " + datosMarcaDto.getRfcMarca());
		emisor.setRfc(datosMarcaDto.getRfcMarca());
		emisor.setRegimenFiscal("601");
		cfdi33.setEmisor(emisor);

		// Receptor
		Receptor receptor = new Receptor();
		boolean convenioKdato = true;
		DatosFiscales datoFiscal = consultaService
				.obtenerDatosFiscalesByCConvenioAndBconvenio(tFacturaEntity.getCconvenio(), convenioKdato);
		if (datoFiscal.getSrfc() != null) {
			receptor.setNombre(datoFiscal.getSrazonsocial());
			receptor.setRfc(datoFiscal.getSrfc());
		} else {
			convenioKdato = false;
			datoFiscal = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(tFacturaEntity.getKdatofiscal(),
					convenioKdato);
			receptor.setNombre(datoFiscal.getSrazonsocial());
			receptor.setRfc(datoFiscal.getSrfc());
		}
		log.info("cusoCfdi--->>>   " + usoCFDI);
		switch (usoCFDI) {
		case "G01":
			receptor.setUsoCFDI(CUsoCFDI.G_01);
			break;
		case "G02":
			receptor.setUsoCFDI(CUsoCFDI.G_02);
			break;
		case "G03":
			receptor.setUsoCFDI(CUsoCFDI.G_03);
			break;
		case "I01":
			receptor.setUsoCFDI(CUsoCFDI.I_01);
			break;
		case "I02":
			receptor.setUsoCFDI(CUsoCFDI.I_02);
			break;
		case "I03":
			receptor.setUsoCFDI(CUsoCFDI.I_03);
			break;
		case "I04":
			receptor.setUsoCFDI(CUsoCFDI.I_04);
			break;
		case "I05":
			receptor.setUsoCFDI(CUsoCFDI.I_05);
			break;
		case "I06":
			receptor.setUsoCFDI(CUsoCFDI.I_06);
			break;
		case "I07":
			receptor.setUsoCFDI(CUsoCFDI.I_07);
			break;
		case "I08":
			receptor.setUsoCFDI(CUsoCFDI.I_08);
			break;
		case "D01":
			receptor.setUsoCFDI(CUsoCFDI.D_01);
			break;
		case "D02":
			receptor.setUsoCFDI(CUsoCFDI.D_02);
			break;
		case "D03":
			receptor.setUsoCFDI(CUsoCFDI.D_03);
			break;
		case "D04":
			receptor.setUsoCFDI(CUsoCFDI.D_04);
			break;
		case "D05":
			receptor.setUsoCFDI(CUsoCFDI.D_05);
			break;
		case "D06":
			receptor.setUsoCFDI(CUsoCFDI.D_06);
			break;
		case "D07":
			receptor.setUsoCFDI(CUsoCFDI.D_07);
			break;
		case "D08":
			receptor.setUsoCFDI(CUsoCFDI.D_08);
			break;
		case "D09":
			receptor.setUsoCFDI(CUsoCFDI.D_09);
			break;
		case "D10":
			receptor.setUsoCFDI(CUsoCFDI.D_10);
			break;
		case "P01":
			// case 44:
			receptor.setUsoCFDI(CUsoCFDI.P_01);
			break;

		default:
			break;
		}

		cfdi33.setReceptor(receptor);

		// Conceptos
		Conceptos conceptos = new Conceptos();
		BigDecimal importeTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importePadre = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal importeRetencion = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		for (ConceptosExamenes tOrdenExamen : lstConcepto) {
			log.info("Monto total--->>>   " + tOrdenExamen.getPrecioUnitario());
			log.info("comparar---->>>   " + tOrdenExamen.getPrecioUnitario().compareTo(BigDecimal.valueOf(0.00)));
			log.info("**********Importe:::   " + tOrdenExamen.getImporte());
			log.info("CONCEPTOS INGRESADOS POR EL USUARIO");
			Concepto concepto1 = new Concepto();
			concepto1.setCantidad(new BigDecimal(tOrdenExamen.getCantidad()).setScale(0, BigDecimal.ROUND_HALF_UP));
			log.info("tOrdenExamen.getMtotal()====  " + tOrdenExamen.getPrecioUnitario());
			String claveProductoServicioSat = "";

			log.info("tOrdenExamen.getCodigo()====  " + tOrdenExamen.getCodigo());

			claveProductoServicioSat = tOrdenExamen.getCodigo();
			log.info("lstCClaveProductoServicioSat--->>>>>>>    " + claveProductoServicioSat);

			if (claveProductoServicioSat.equals(null) || claveProductoServicioSat.isEmpty()
					|| claveProductoServicioSat.equals("NO APLICA")) {
				concepto1.setClaveProdServ("85121800");
			} else {
				concepto1.setClaveProdServ(claveProductoServicioSat);
			}
			concepto1.setNoIdentificacion(String.valueOf(tOrdenExamen.getClave()));

			concepto1.setClaveUnidad((tOrdenExamen.getUnidadMedida() == null || tOrdenExamen.getUnidadMedida().isEmpty()
					|| tOrdenExamen.getUnidadMedida().equals("NO APLICA")) ? "E48" : tOrdenExamen.getUnidadMedida()); // tOrdenExamen.getCexamen().getCclaveunidad().getSclaveunidad());
			
			concepto1.setUnidad((tOrdenExamen.getUnidad() == null || tOrdenExamen.getUnidad().isEmpty()
					|| tOrdenExamen.getUnidad().equals("NO APLICA")) ? "Examen" : tOrdenExamen.getUnidad());
			concepto1.setDescripcion(utilsService.darFormatoCFDI(tOrdenExamen.getConcepto())); 
			concepto1.setValorUnitario(tOrdenExamen.getPrecioUnitario()); //
			log.info("concepto1.getValorUnitario()-->>  " + concepto1.getValorUnitario());
			concepto1.setImporte(tOrdenExamen.getPrecioUnitario()); 
			log.info("concepto1.getImporte()-->  " + concepto1.getImporte());
			concepto1.setImporte(
					concepto1.getImporte().multiply(new BigDecimal(tOrdenExamen.getCantidad())).setScale(2)); /// concepto1.getCantidad()).setScale(2));
			log.info("concepto1.getImporte()2-->  " + concepto1.getImporte());

			importePadre = importePadre.add(concepto1.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));

			conceptos.getConcepto().add(concepto1);

			// Impuestos
			Impuestos impuestos = new Impuestos();

			Traslados traslados = new Traslados();

			Retenciones retenciones = new Retenciones();

			Traslado traslado = new Traslado();
			Retencion retencion = new Retencion();

			retencion.setImporte(
					concepto1.getImporte().multiply(new BigDecimal(".06")).setScale(2, BigDecimal.ROUND_HALF_UP));
			retencion.setImpuesto("002");
			retencion.setTasaOCuota(new BigDecimal(0.06).setScale(6, RoundingMode.HALF_UP));
			retencion.setTipoFactor(CTipoFactor.TASA);
			retencion.setBase(concepto1.getImporte());

			if (bRetencion) {
				retenciones.getRetencion().add(retencion);
				impuestos.setRetenciones(retenciones);
				importeRetencion = importeRetencion.add(retencion.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			}

			traslado.setBase(concepto1.getImporte()); 

			log.info("traslado.getBase()--->>>  " + traslado.getBase());

			traslado.setImporte(concepto1.getImporte().multiply(tOrdenExamen.getIva()).setScale(2, BigDecimal.ROUND_HALF_UP)); 

			log.info("traslado.getImporte()--->>>  " + traslado.getImporte());

			importeTotal = importeTotal.add(traslado.getImporte().setScale(2, BigDecimal.ROUND_HALF_UP));
			traslado.setImpuesto("002");
			traslado.setTipoFactor(CTipoFactor.TASA);
			traslado.setTasaOCuota(tOrdenExamen.getIva().setScale(6, BigDecimal.ROUND_HALF_UP));
			traslados.getTraslado().add(traslado);
			impuestos.setTraslados(traslados);
			concepto1.setImpuestos(impuestos);
			cfdi33.setConceptos(conceptos);

		}
		
		log.info("importePadre:::::  " + importePadre.setScale(2));
		cfdi33.setSubTotal(importePadre.setScale(2));
		log.info("TotalFacrura:::  " + tFacturaEntity.getMtotal());
		if(bRetencion){
			cfdi33.setTotal(importePadre.add(importeTotal).setScale(2, BigDecimal.ROUND_DOWN));
			cfdi33.setTotal(cfdi33.getTotal().subtract(importeRetencion).setScale(2, BigDecimal.ROUND_DOWN));
		}else{
			cfdi33.setTotal(importePadre.add(importeTotal).setScale(2, BigDecimal.ROUND_DOWN)); 		
		}
		consultaService.updateMontosFactura(cfdi33.getTotal(), cfdi33.getSubTotal(), importeTotal, BigDecimal.ZERO, tFacturaEntity.getKfactura());
		
		log.info("TotalCalculado--->   " + cfdi33.getTotal());
		// log.info("importeTotal-->>> " + importeTotal);
		mx.gob.sat.cfd._3.Comprobante.Impuestos impuestos = new mx.gob.sat.cfd._3.Comprobante.Impuestos();
		mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados traslados = new mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados();
		List<mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado> lstTrasladosTotales = new ArrayList<mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado>();
		mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new mx.gob.sat.cfd._3.Comprobante.Impuestos.Traslados.Traslado();
		
		trasladosTotales.setImporte(importeTotal.setScale(2, BigDecimal.ROUND_DOWN));

		log.info("trasladosTotales.getImporte()-->>  " + trasladosTotales.getImporte());
		trasladosTotales.setImpuesto("002");
		trasladosTotales.setTasaOCuota(new BigDecimal(0.16).setScale(6, RoundingMode.HALF_UP));

		trasladosTotales.setTipoFactor(CTipoFactor.TASA);
//		lstTrasladosTotales.add(trasladosTotales);
		traslados.getTraslado().add(trasladosTotales);
		impuestos.setTraslados(traslados);		
		impuestos.setTotalImpuestosTrasladados(importeTotal.setScale(2, BigDecimal.ROUND_DOWN));
		
		if(bRetencion){
			mx.gob.sat.cfd._3.Comprobante.Impuestos.Retenciones retenciones = new ObjectFactory().createComprobanteImpuestosRetenciones();
			mx.gob.sat.cfd._3.Comprobante.Impuestos.Retenciones.Retencion retencionTotales = new ObjectFactory().createComprobanteImpuestosRetencionesRetencion();
			retencionTotales.setImporte(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));
			retencionTotales.setImpuesto("002");
			retenciones.getRetencion().add(retencionTotales);
			impuestos.setRetenciones(retenciones);
			impuestos.setTotalImpuestosRetenidos(importeRetencion.setScale(2, BigDecimal.ROUND_DOWN));			
		}		
		
		cfdi33.setImpuestos(impuestos);
		
		return cfdi33;
	}

	public String getConsultaInsertSeriA(BigDecimal subTotal, Integer convenio, Integer cmarca, Integer razonSocial) {
		log.info("================");
		log.info(subTotal.toString());
		log.info(convenio.toString());
		log.info(cmarca.toString());
		log.info(razonSocial.toString());
		String ssucursal = "";
		String csucursal = "";
		String sserie = "";
		String centidadlegal = "";
		if (cmarca == 1) {
			ssucursal = "EMPRESAS";
			csucursal = "1003";
			sserie = "A";
			centidadlegal = "1";
		} else if (cmarca == 4) {
			ssucursal = "EMPRESAS AZTECA";
			csucursal = "1012";
			sserie = "AZ";
			centidadlegal = "5";
		} else if (cmarca == 5) {
			ssucursal = "EMPRESAS SWISSLAB";
			csucursal = "1013";
			sserie = "AS";
			centidadlegal = "6";
		} else if (cmarca == 15) {
			ssucursal = "EMPRESAS LIACSA";
			csucursal = "1017";
			sserie = "ASL";
			centidadlegal = "6";
		} else if (cmarca == 7) {
			if (razonSocial == 7) {
				ssucursal = "EMPRESAS JENNER PRADO";
				csucursal = "1014";
				sserie = "AJP";
				centidadlegal = "7";
//				ssucursal="EMPRESAS AZTECA";
//				csucursal="1012";
//				sserie="AZ";
//				centidadlegal="5";
			} else if (razonSocial == 8) {
				ssucursal = "EMPRESAS JENNER LEAN";
				csucursal = "1015";
				sserie = "AJL";
				centidadlegal = "8";
//				ssucursal="EMPRESAS AZTECA";
//				csucursal="1012";
//				sserie="AZ";
//				centidadlegal="5";
			} else if (razonSocial == 9) {
				ssucursal = "EMPRESAS AZTECA";
				csucursal = "1012";
				sserie = "AZ";
				centidadlegal = "5";
			}
		}
		log.info("getConsultaInsertSeriA::   " + subTotal + " :::convenio   " + convenio);
		String query = "SELECT \r\n"
				+ "'INSERT INTO t_factura VALUES(t_factura_sequence.nextval,'||(SELECT kdatofiscal FROM c_convenio_dato_fiscal WHERE cconvenio = "
				+ convenio + " limit 1)||\r\n" + "',''" + ssucursal
				+ "'','||(SELECT SUM(ufolioactual+1) FROM c_control_folio WHERE csucursal=" + csucursal
				+ " AND cestadoregistro=31)||','||\r\n" + "(SELECT ccliente FROM c_convenio WHERE cconvenio=" + convenio
				+ ")||'," + csucursal + ",1,'\r\n" + "||SUM (" + subTotal + "  )||',0.00,0.00,'||\r\n" + "SUM ("
				+ subTotal + " *.16)||','||\r\n" + "SUM (" + subTotal + " *1.16)||',1,'||" + convenio
				+ "||','' '','' ''," + centidadlegal + ",sysdate,33,sysdate,'||1||','||1||', '' '','' '','' '', ''"
				+ sserie + "'') RETURNING kfactura;' AS query1,\r\n"
				+ "'UPDATE c_control_folio SET ufolioactual='||(SELECT SUM(ufolioactual+1) FROM c_control_folio WHERE csucursal="
				+ csucursal + " AND cestadoregistro=31)||\r\n" + "' WHERE csucursal=" + csucursal
				+ " AND cestadoregistro=31;' AS query2";
		log.info("query-->>>   " + query);
		return query;
	}

}
