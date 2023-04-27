package com.gda.cfdi.service.empresa;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.service.UtilsCfdi4Service;
import com.gda.cfdi.service.UtilsService;
import com.google.gson.Gson;

import facturacion.domain.dto.AddendaDto;
import mx.gob.sat.addenda.AddendaEmpresa;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfd._4.Comprobante.Conceptos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.InformacionGlobal;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.pagos20.Pagos;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR;
import mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP.TrasladosP;
import mx.gob.sat.pagos20.Pagos.Pago.ImpuestosP.TrasladosP.TrasladoP;

@Service("empresaCfdiService")
public class EmpresaCfdiService {
	private static final Logger log = LoggerFactory.getLogger(EmpresaCfdiService .class);
	
	@Autowired
	private Environment env;
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private UtilsCfdi4Service utilsCfdi4Service;
	private Gson gson = new Gson();
	
	public String getXmlAddendaCfdi(AddendaDto addendaDto) throws Exception {
		try {
			Comprobante comprobante = utilsCfdi4Service.createComprobanteFromXml(addendaDto.getXmltimbrado());
			/**
			 * Informacion Addenda
			 */
			if(addendaDto.getComprobante().getAddenda()!=null) {
				if(addendaDto.getComprobante().getAddenda().getAny().size()>0) {
					ObjectMapper mapper = new ObjectMapper();
					AddendaEmpresa addendaItem = mapper.convertValue(addendaDto.getComprobante().getAddenda().getAny().get(0), AddendaEmpresa.class);
					AddendaEmpresa addendaEmpresa = new AddendaEmpresa();
					addendaEmpresa.setDatos(addendaItem.getDatos());
					Comprobante.Addenda addenda = new Comprobante.Addenda();
					addenda.getAny().add(addendaEmpresa);
					comprobante.setAddenda(addenda);
				}
			}
			Boolean bPago = false;		
			if(comprobante.getComplemento()!=null) {
				List<Object> listComplementos = comprobante.getComplemento().getAny();
				for (Object object : listComplementos) {
					if (object instanceof Pagos) {
						bPago = true;
					}
				}			
			}		
			String xml = !bPago ? utilsCfdi4Service.createXmlFromComprobante(comprobante) : utilsCfdi4Service.createXmlFromComplementoPago(comprobante);
			return xml;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	public String getXmlCfdi(Comprobante comprobante)  throws Exception {
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosRfcEmisor(comprobante.getEmisor().getRfc(), 4);
		Comprobante cfdi = getComprobanteToCfdi(comprobante);
		cfdi.setVersion(env.getProperty("cfdi.version.4"));
		cfdi.setNoCertificado(datosMarcaDto.getNumeroCertificado());		
		cfdi.getEmisor().setNombre(datosMarcaDto.getRazonSocialMarca());
		cfdi.getEmisor().setRfc(datosMarcaDto.getRfcMarca());
		Boolean bPago = false;		
		if(cfdi.getComplemento()!=null) {
			List<Object> listComplementos = cfdi.getComplemento().getAny();
			for (Object object : listComplementos) {
				if (object instanceof Pagos) {
					bPago = true;
				}
			}			
		}		
		String xml = !bPago ? utilsCfdi4Service.createXmlFromComprobante(cfdi) : utilsCfdi4Service.createXmlFromComplementoPago(cfdi);
		cfdi.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		cfdi.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  !bPago ? utilsCfdi4Service.createXmlFromComprobante(cfdi) : utilsCfdi4Service.createXmlFromComplementoPago(cfdi);
		return xmlOriginalSello;
	}
	
	public Comprobante getComprobanteToCfdi(Comprobante comprobante) throws DatatypeConfigurationException {
		Comprobante cfdi = new Comprobante();
		cfdi.setVersion(comprobante.getVersion());
		cfdi.setExportacion(comprobante.getExportacion());
		cfdi.setFormaPago(comprobante.getFormaPago());
		cfdi.setSerie(comprobante.getSerie());
		cfdi.setFolio(comprobante.getFolio());
		cfdi.setFecha(utilsService.toXmlGregorianCalendar(comprobante.getFecha().toGregorianCalendar().getTime(), "yyyy-MM-dd'T'HH:mm:ss"));
		cfdi.setTipoCambio(comprobante.getTipoCambio());
		cfdi.setLugarExpedicion(comprobante.getLugarExpedicion());
		cfdi.setMetodoPago(comprobante.getMetodoPago());
		cfdi.setMoneda(comprobante.getMoneda());
		cfdi.setTipoDeComprobante(comprobante.getTipoDeComprobante());
		cfdi.setTotal(comprobante.getTotal());
		cfdi.setSubTotal(comprobante.getSubTotal());
		if(comprobante.getDescuento()!=null) {
			cfdi.setDescuento(comprobante.getDescuento());			
		}
		
		if(comprobante.getCfdiRelacionados()!=null) {
			if(comprobante.getCfdiRelacionados().size()>0) {
				for (Comprobante.CfdiRelacionados relacionados : comprobante.getCfdiRelacionados()) {
					Comprobante.CfdiRelacionados cfdiRelacionadosV4 = new ObjectFactory().createComprobanteCfdiRelacionados();
					cfdiRelacionadosV4.setTipoRelacion(relacionados.getTipoRelacion());
					for (CfdiRelacionado cfdiRelacionado : relacionados.getCfdiRelacionado()) {
						CfdiRelacionado cfdiRelacionadoV4 = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
						cfdiRelacionadoV4.setUUID(cfdiRelacionado.getUUID());
						cfdiRelacionadosV4.getCfdiRelacionado().add(cfdiRelacionadoV4);
					}
					cfdi.getCfdiRelacionados().add(cfdiRelacionadosV4);
				}
			}
		}
		
		/**
		 * Informacion Global
		 */
		System.out.println(comprobante.getInformacionGlobal());
		if(comprobante.getInformacionGlobal()!=null) {
			InformacionGlobal informacionGlobal = new InformacionGlobal();
			informacionGlobal.setAno(comprobante.getInformacionGlobal().getAno());
			informacionGlobal.setMeses(comprobante.getInformacionGlobal().getMeses());
			informacionGlobal.setPeriodicidad(comprobante.getInformacionGlobal().getPeriodicidad());
			cfdi.setInformacionGlobal(informacionGlobal);
		}
		
				
		/**
		 * Emisor
		 */
		Emisor emisor = new ObjectFactory().createComprobanteEmisor();
		emisor.setNombre(comprobante.getEmisor().getNombre());
		emisor.setRfc(comprobante.getEmisor().getRfc());
		emisor.setRegimenFiscal(comprobante.getEmisor().getRegimenFiscal());
		cfdi.setEmisor(emisor);
		
		/**
		 * Receptor
		 */
		Receptor receptor = new ObjectFactory().createComprobanteReceptor();
		receptor.setNombre(comprobante.getReceptor().getNombre());
		receptor.setRfc(comprobante.getReceptor().getRfc());
		receptor.setUsoCFDI(comprobante.getReceptor().getUsoCFDI());
		receptor.setRegimenFiscalReceptor(comprobante.getReceptor().getRegimenFiscalReceptor());
		receptor.setDomicilioFiscalReceptor(comprobante.getReceptor().getDomicilioFiscalReceptor());
		cfdi.setReceptor(receptor);
		
		/**
		 * Conceptos
		 */
		Conceptos conceptos = new ObjectFactory().createComprobanteConceptos();
		if(comprobante.getConceptos()!=null) {
			if(comprobante.getConceptos().getConcepto().size()>0) {
				for (Comprobante.Conceptos.Concepto conceptoItem : comprobante.getConceptos().getConcepto()) {
					Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
					concepto.setCantidad(conceptoItem.getCantidad());
					concepto.setClaveProdServ(conceptoItem.getClaveProdServ());
					concepto.setNoIdentificacion(conceptoItem.getNoIdentificacion());
					concepto.setClaveUnidad(conceptoItem.getClaveUnidad());
					concepto.setUnidad(conceptoItem.getUnidad());
					if(conceptoItem.getDescuento()!=null) {
						concepto.setDescuento(conceptoItem.getDescuento());						
					}
					concepto.setObjetoImp(conceptoItem.getObjetoImp());
					concepto.setDescripcion(darFormatoCFDI(conceptoItem.getDescripcion()));
					concepto.setValorUnitario(conceptoItem.getValorUnitario());
					concepto.setImporte(conceptoItem.getImporte());										
					conceptos.getConcepto().add(concepto);
					if(conceptoItem.getImpuestos()!=null) {
						Impuestos impuestos = new ObjectFactory().createComprobanteConceptosConceptoImpuestos();
						Boolean isIvaCero = false;
						if(conceptoItem.getImpuestos().getTraslados()!=null) {
							Traslados traslados = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTraslados();
							if(conceptoItem.getImpuestos().getTraslados().getTraslado()!=null) {
								if(conceptoItem.getImpuestos().getTraslados().getTraslado().size()>0) {
									for (Traslado trasladoItem :conceptoItem.getImpuestos().getTraslados().getTraslado()) {
										Traslado traslado = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
										traslado.setBase(trasladoItem.getBase());
										traslado.setImporte(trasladoItem.getImporte());
										traslado.setImpuesto(trasladoItem.getImpuesto());
										traslado.setTipoFactor(trasladoItem.getTipoFactor());
										traslado.setTasaOCuota(trasladoItem.getTasaOCuota());
										traslados.getTraslado().add(traslado);
										log.info("1"+traslado.getTasaOCuota());
										log.info("1"+BigDecimal.ZERO);
									}									
								}
							}							
							impuestos.setTraslados(traslados);
						}
						log.info("isIvaCero:"+isIvaCero);
						concepto.setImpuestos(impuestos);
					}					
				}				
			}
		}
		cfdi.setConceptos(conceptos);
		
		/**
		 * Complemento Pagos
		 */
		if(comprobante.getComplemento()!=null) {
			log.info("Contiene Complemento");
			log.info(""+comprobante.getComplemento().getAny().size());
			Comprobante.Complemento complemento = new Comprobante.Complemento();
						
			
			if (comprobante.getComplemento().getAny().size()==1) {
				log.info("valorr");
				log.info("Contiene Pago");
				ObjectMapper mapper = new ObjectMapper();
				Pagos pagosItem = mapper.convertValue(comprobante.getComplemento().getAny().get(0), Pagos.class);
				Pagos pago = new Pagos();
				pago.setVersion(pagosItem.getVersion());
				if(pagosItem.getTotales()!=null) {
					Pagos.Totales totales = new Pagos.Totales();
					totales.setTotalTrasladosBaseIVA16(pagosItem.getTotales().getTotalTrasladosBaseIVA16()!=null ? pagosItem.getTotales().getTotalTrasladosBaseIVA16().setScale(2) : null);
					totales.setTotalTrasladosImpuestoIVA16(pagosItem.getTotales().getTotalTrasladosImpuestoIVA16()!=null ? pagosItem.getTotales().getTotalTrasladosImpuestoIVA16().setScale(2) : null);
					totales.setMontoTotalPagos(pagosItem.getTotales().getMontoTotalPagos()!=null ? pagosItem.getTotales().getMontoTotalPagos().setScale(2) : null);
					pago.setTotales(totales);	
				}
				if(pagosItem.getPago()!=null) {
					if(pagosItem.getPago().size()>0) {
						for (Pagos.Pago pagoItem : pagosItem.getPago()) {
							Pagos.Pago newPago = new Pagos.Pago();								
							newPago.setFechaPago(utilsService.toXmlGregorianCalendar(pagoItem.getFechaPago().toGregorianCalendar().getTime(), "yyyy-MM-dd'T'HH:mm:ss"));
							newPago.setMonedaP(pagoItem.getMonedaP());
							newPago.setTipoCambioP(pagoItem.getTipoCambioP());
							newPago.setFormaDePagoP(pagoItem.getFormaDePagoP());
							newPago.setMonto(pagoItem.getMonto() !=null ? pagoItem.getMonto().setScale(2) : null);
							
							if(pagoItem.getDoctoRelacionado()!=null) {
								if(pagoItem.getDoctoRelacionado().size()>0) {
									for (Pagos.Pago.DoctoRelacionado relacionadoItem : pagoItem.getDoctoRelacionado()) {
										Pagos.Pago.DoctoRelacionado facturaRelacionada = new Pagos.Pago.DoctoRelacionado();
										facturaRelacionada.setIdDocumento(relacionadoItem.getIdDocumento());
										facturaRelacionada.setSerie(relacionadoItem.getSerie());
										facturaRelacionada.setFolio(relacionadoItem.getFolio());
										facturaRelacionada.setObjetoImpDR(relacionadoItem.getObjetoImpDR());
										facturaRelacionada.setMonedaDR(relacionadoItem.getMonedaDR());
										facturaRelacionada.setEquivalenciaDR(relacionadoItem.getEquivalenciaDR());
										facturaRelacionada.setNumParcialidad(relacionadoItem.getNumParcialidad());
										facturaRelacionada.setImpSaldoAnt(relacionadoItem.getImpSaldoAnt() !=null ? relacionadoItem.getImpSaldoAnt().setScale(2) : null);
										facturaRelacionada.setImpSaldoInsoluto(relacionadoItem.getImpSaldoInsoluto() !=null ? relacionadoItem.getImpSaldoInsoluto().setScale(2) : null);
										facturaRelacionada.setImpPagado(relacionadoItem.getImpPagado() !=null ? relacionadoItem.getImpPagado().setScale(2) : null);
										
										if(relacionadoItem.getImpuestosDR()!=null) {
											ImpuestosDR impuestosDR = new ImpuestosDR();
											if(relacionadoItem.getImpuestosDR().getTrasladosDR()!=null) {
												TrasladosDR trasladosDR = new TrasladosDR();
												if(relacionadoItem.getImpuestosDR().getTrasladosDR().getTrasladoDR()!=null) {
													if(relacionadoItem.getImpuestosDR().getTrasladosDR().getTrasladoDR().size()>0) {
														for (TrasladoDR trasladoDRItem : relacionadoItem.getImpuestosDR().getTrasladosDR().getTrasladoDR()) {
															TrasladoDR trasladoDR = new TrasladoDR();
															trasladoDR.setBaseDR(trasladoDRItem.getBaseDR());
															trasladoDR.setImpuestoDR(trasladoDRItem.getImpuestoDR());
															trasladoDR.setTipoFactorDR(trasladoDRItem.getTipoFactorDR());
															trasladoDR.setTasaOCuotaDR(trasladoDRItem.getTasaOCuotaDR().setScale(6, BigDecimal.ROUND_HALF_UP));
															trasladoDR.setImporteDR(trasladoDRItem.getImporteDR());
															trasladosDR.getTrasladoDR().add(trasladoDR);
														}
													}
												}
												impuestosDR.setTrasladosDR(trasladosDR);	
											}
											facturaRelacionada.setImpuestosDR(impuestosDR);
										}
														
										newPago.getDoctoRelacionado().add(facturaRelacionada);
									}
								}
							}
							if(pagoItem.getImpuestosP()!=null) {
								ImpuestosP impuestosP = new ImpuestosP();
								if(pagoItem.getImpuestosP().getTrasladosP()!=null) {
									TrasladosP trasladosP = new TrasladosP();
									if(pagoItem.getImpuestosP().getTrasladosP().getTrasladoP()!=null) {
										for (TrasladoP trasladoPItem : pagoItem.getImpuestosP().getTrasladosP().getTrasladoP()) {
											TrasladoP trasladoP = new TrasladoP();
											trasladoP.setBaseP(trasladoPItem.getBaseP());												
											trasladoP.setImpuestoP(trasladoPItem.getImpuestoP());
											trasladoP.setTipoFactorP(trasladoPItem.getTipoFactorP());
											trasladoP.setTasaOCuotaP(trasladoPItem.getTasaOCuotaP().setScale(6, BigDecimal.ROUND_HALF_UP));
											trasladoP.setImporteP(trasladoPItem.getImporteP());
											trasladosP.getTrasladoP().add(trasladoP);
										}
									}
									impuestosP.setTrasladosP(trasladosP);
								}			
								newPago.setImpuestosP(impuestosP);
							}
							pago.getPago().add(newPago);
						}
					}
				}
				complemento.getAny().add(pago);
			}
			cfdi.setComplemento(complemento);
		}
		
		
		/**
		 * Impuesto
		 */		
		if(comprobante.getImpuestos()!=null) {
			Comprobante.Impuestos impuestos = new ObjectFactory().createComprobanteImpuestos();			
			impuestos.setTotalImpuestosTrasladados(comprobante.getImpuestos().getTotalImpuestosTrasladados());
			if(comprobante.getImpuestos().getTraslados()!=null) {
				Comprobante.Impuestos.Traslados traslados = new ObjectFactory().createComprobanteImpuestosTraslados();
				for (Comprobante.Impuestos.Traslados.Traslado trasladoItem : comprobante.getImpuestos().getTraslados().getTraslado()) {
					Comprobante.Impuestos.Traslados.Traslado traslado = new ObjectFactory().createComprobanteImpuestosTrasladosTraslado();
					traslado.setBase(trasladoItem.getBase());
					traslado.setImporte(trasladoItem.getImporte());
					traslado.setImpuesto(trasladoItem.getImpuesto());
					traslado.setTasaOCuota(trasladoItem.getTasaOCuota());
					traslado.setTipoFactor(trasladoItem.getTipoFactor());
					traslados.getTraslado().add(traslado);
				}
				impuestos.setTraslados(traslados);		
			}
			cfdi.setImpuestos(impuestos);			
		}
		
		return cfdi;
	}
	
	public static String darFormatoCFDI(String cadenaOrigianl){
		String cadenaNormalize = Normalizer.normalize(cadenaOrigianl, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		System.out.println("Resultado: " + cadenaSinAcentos);		
		return cadenaSinAcentos;
	}
	
}
