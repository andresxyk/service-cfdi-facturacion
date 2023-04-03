package com.gda.cfdi.service.exakta;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.service.UtilsCfdi4Service;
import com.gda.cfdi.service.UtilsService;

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
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class ExaktaCfdiService {
	private static final Logger log = LoggerFactory.getLogger(ExaktaCfdiService .class);
	
	@Autowired
	private Environment env;
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private UtilsCfdi4Service utilsCfdi4Service;

	public String generarCfdi(String cfdiPlano) throws Exception {
		return generarCfdiFacNC(cfdiPlano, 1);	
	}
	
	public String generarCfdiFacNC(String cfdiPlano, Integer tipoCfdi) throws Exception {
		log.info("Iniiiiiiiiicuia");
		DatosMarcaDto datosMarcaDto = null;
		Comprobante cfdi = new Comprobante();
		Emisor emisor = new ObjectFactory().createComprobanteEmisor();
		Receptor receptor = new ObjectFactory().createComprobanteReceptor();
		Boolean bgenerico = false;
		Conceptos conceptos = new ObjectFactory().createComprobanteConceptos();
		String noIdentificacion = "";
		BigDecimal montoBase = BigDecimal.ZERO;
		BigDecimal montoImp = BigDecimal.ZERO;
		
		Reader inputString = new StringReader(cfdiPlano);
		BufferedReader br = new BufferedReader(inputString);		
		String line;
		while ((line = br.readLine()) != null) {
		    if(line.startsWith("XXXINICIO")) {
	        	log.info("Inicia creacion CFDI");
	        	cfdi.setVersion(env.getProperty("cfdi.version.4"));
	        }
		    if(line.startsWith("================ IdDoc")) {
	    		log.info(line);
	    		while ((line = br.readLine()) != null) {
	    			System.out.println("1 "+line);
	    			if(line.startsWith("NumeroInterno")) {
	    				noIdentificacion = line.substring(17,line.length());
	    			}
	    			if(line.startsWith("Tipo")) {
	    				String tipoFac = line.substring(17,line.length());
	    				System.out.println("++++++++++++++++++++++++"+tipoFac);
	    				if(tipoFac.equals("33")) {
	    					cfdi.setTipoDeComprobante(CTipoDeComprobante.I);
	    				}else if(tipoFac.equals("61")) {
	    					cfdi.setTipoDeComprobante(CTipoDeComprobante.E);
	    				}
	    			}
	    			if(line.startsWith("Serie")) {
	    				cfdi.setSerie(line.substring(17,line.length()));
	    			}
	    			if(line.startsWith("Folio")) {
	    				cfdi.setFolio(line.substring(17,line.length()));
	    			}
	    			if(line.startsWith("FechaEmis")) {
	    				cfdi.setFecha(utilsService.toXmlGregorianCalendar(
	    						utilsService.stringFormatDate(line.substring(17,line.length()), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd'T'HH:mm:ss"));
	    			}
	    			if(line.startsWith("FormaPago")) {
	    				cfdi.setMetodoPago(CMetodoPago.fromValue(line.substring(17,line.length())));
	    			}
	    			if(line.startsWith("MedioPago")) {
	    				cfdi.setFormaPago(line.substring(17,line.length()));		    			
	    			}
	    			if(line.startsWith("Exportacion")) {
	    				cfdi.setExportacion(line.substring(17,line.length()));		    			
	    			}
	    			if(line.startsWith("========== Documentos Relacionados")) {
	    				break;
	    			}
	    		}
	        }		
		    
		    if(line.startsWith("========== Documentos Relacionados")) {
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("2- "+line);
		    		if(line.startsWith("TipoRelacion")) {
		    			while ((line = br.readLine()) != null) {
		    				if(line.length()>0 && !line.startsWith("XXXFINRELACIONADOS")) {
		    					System.out.println("2- "+line);
		    					Comprobante.CfdiRelacionados cfdiRelacionadosV4 = new ObjectFactory().createComprobanteCfdiRelacionados();
		    					cfdiRelacionadosV4.setTipoRelacion(line.substring(0, 17).trim());
		    					CfdiRelacionado cfdiRelacionadoV4 = new ObjectFactory().createComprobanteCfdiRelacionadosCfdiRelacionado();
								cfdiRelacionadoV4.setUUID(line.substring(17,53));
								cfdiRelacionadosV4.getCfdiRelacionado().add(cfdiRelacionadoV4);
								cfdi.getCfdiRelacionados().add(cfdiRelacionadosV4);		
		    				}
		    				if(line.startsWith("XXXFINRELACIONADOS")) {
				    			break;
				    		}
		    			}
		    		}
		    		if(line.startsWith("XXXFINRELACIONADOS")) {
		    			break;
		    		}		    		
		    	}
		    }
		    
		    if(line.startsWith("================ ExEmisor")) {
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("2 "+line);
		    		if(line.startsWith("RFCEmisor")) {
		    			datosMarcaDto = utilsService.obtenerDatosRfcEmisor(line.substring(17,line.length()), 4);
		    			emisor.setRfc(datosMarcaDto.getRfcMarca());
		    			cfdi.setNoCertificado(datosMarcaDto.getNumeroCertificado());
		    		}
		    		if(line.startsWith("NmbEmisor")) {
		    			emisor.setNombre(datosMarcaDto.getRazonSocialMarca());
		    		}
		    		if(line.startsWith("================ ExReceptor")) {
		    			cfdi.setEmisor(emisor);
		    			break;
		    		}	
		    	}
		    }	
		    
		    if(line.startsWith("================ ExReceptor")) {		    	
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("3 "+line);
		    		if(line.startsWith("RFCRecep")) {
		    			receptor.setRfc(line.substring(17,line.length()));
		    			if(line.substring(17,line.length()).equals("XAXX010101000")) {
		    				bgenerico = true;
		    			}
		    		}
		    		if(line.startsWith("NmbRecep")) {
		    			if(bgenerico) {
		    				receptor.setNombre("PUBLICO EN GENERAL");
		    			}else {		    				
		    				receptor.setNombre(line.substring(17,line.length()));
		    			}
		    		}
		    		if(line.startsWith("NmbRecep")) {
		    			if(bgenerico) {
		    				receptor.setNombre("PUBLICO EN GENERAL");
		    			}else {		    				
		    				receptor.setNombre(line.substring(17,line.length()));
		    			}
		    		}
		    		if(line.startsWith("RegimenFisc")) {
		    			receptor.setRegimenFiscalReceptor(line.substring(17,line.length()));		    			
		    		}	
		    		if(line.startsWith("CodigoPostal")) {
		    			receptor.setDomicilioFiscalReceptor(line.substring(17,line.length()));		    			
		    		}
		    		if(line.startsWith("================ Detalle")) {
		    			cfdi.setReceptor(receptor);
		    			break;
		    		}	
		    	}
		    }
		    
		    
		    if(line.startsWith("================ Detalle")) {		    	
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("4 "+line);
		    		if(line.startsWith("DetT")) {
		    			while ((line = br.readLine()) != null) {
		    				if(line.length()>0 && !line.startsWith("XXXFINDETA")) {
		    					System.out.println("5 "+line);
		    					Concepto concepto = new ObjectFactory().createComprobanteConceptosConcepto();
		    					concepto.setDescripcion(line.substring(120, 271).trim());
		    					concepto.setCantidad(new BigDecimal(line.substring(271, 288).trim()));
		    					concepto.setUnidad(line.substring(288, 321).trim());
		    					concepto.setValorUnitario(new BigDecimal(line.substring(505, 522).trim()));
		    					concepto.setImporte(new BigDecimal(line.substring(539,556).trim()));									
		    					concepto.setClaveProdServ(line.substring(658, 681).trim());
		    					concepto.setClaveUnidad(line.substring(681, 702).trim());		    					
		    					concepto.setNoIdentificacion(noIdentificacion);
		    					concepto.setObjetoImp(line.substring(862, 868).trim());
		    					conceptos.getConcepto().add(concepto);
		    					Impuestos impuestos = new ObjectFactory().createComprobanteConceptosConceptoImpuestos();
		    					Traslados traslados = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTraslados();
		    					Traslado traslado = new ObjectFactory().createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
								traslado.setBase(new BigDecimal(line.substring(722, 743).trim()));
								traslado.setImpuesto(line.substring(743, 764).trim());
								traslado.setTipoFactor(CTipoFactor.fromValue(line.substring(764, 789).trim()));
								traslado.setTasaOCuota(new BigDecimal(line.substring(789, 812).trim()));								
								traslado.setImporte(new BigDecimal(line.substring(430, 447).trim()));
								traslados.getTraslado().add(traslado);
								impuestos.setTraslados(traslados);
								concepto.setImpuestos(impuestos);
		    					
		    				}
		    				if(line.startsWith("XXXFINDETA")) {
				    			break;
				    		}
		    			}
		    		}		    		
		    		if(line.startsWith("XXXFINDETA")) {
		    			cfdi.setConceptos(conceptos);
		    			break;
		    		}	
		    	}
		    	
		    }
		    if(line.startsWith("================ Totales")) {		    	
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("6 "+line);
		    		if(line.startsWith("Moneda")) {
		    			cfdi.setMoneda(CMoneda.fromValue(line.substring(17,line.length())));
		    			if(CMoneda.fromValue(line.substring(17,line.length())) == CMoneda.MXN) {
		    				cfdi.setTipoCambio(new BigDecimal(1));
		    			}
		    		}
		    		if(line.startsWith("SubTotal")) {
		    			cfdi.setSubTotal(new BigDecimal(line.substring(17,line.length())));	    			
		    		}
		    		if(line.startsWith("VlrPagar")) {
		    			cfdi.setTotal(new BigDecimal(line.substring(17,line.length())));		
		    		}
		    		if(line.startsWith("MntBase")) {
		    			montoBase = new BigDecimal(line.substring(17,line.length()));		
		    		}
		    		if(line.startsWith("MntImp")) {
		    			montoImp = new BigDecimal(line.substring(17,line.length()));		
		    		}
		    		if(line.startsWith("================ ExImpuestos")) {
		    			break;
		    		}	
		    	}
		    }
		    if(line.startsWith("================ ExImpuestos")) {		    	
		    	log.info(line);
				Comprobante.Impuestos impuestos = new ObjectFactory().createComprobanteImpuestos();
				Comprobante.Impuestos.Traslados traslados = new ObjectFactory().createComprobanteImpuestosTraslados();
				Comprobante.Impuestos.Traslados.Traslado trasladosTotales = new ObjectFactory().createComprobanteImpuestosTrasladosTraslado();												
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("7 "+line);
		    		if(line.startsWith("TipoImp1")) {
		    			trasladosTotales.setImpuesto(line.substring(17,line.length()));	    			
		    		}
		    		if(line.startsWith("TasaImp1")) {
		    			trasladosTotales.setTasaOCuota(new BigDecimal(line.substring(17,line.length())));
		    		}
		    		if(line.startsWith("MontoImp1")) {
		    			trasladosTotales.setImporte(new BigDecimal(line.substring(17,line.length())));
		    			trasladosTotales.setBase(montoBase);
		    		}
		    		if(line.startsWith("TIPOFACT1")) {
		    			trasladosTotales.setTipoFactor(CTipoFactor.fromValue(line.substring(17,line.length())));
		    		}
		    		if(line.startsWith("MntBase1")) {
		    			trasladosTotales.setBase(new BigDecimal(line.substring(17,line.length())));
		    		}
		    		if(line.startsWith("================ ExRetenciones")) {
		    			traslados.getTraslado().add(trasladosTotales);
						impuestos.setTraslados(traslados);	
		    			impuestos.setTotalImpuestosTrasladados(montoImp);
						cfdi.setImpuestos(impuestos);
		    			break;
		    		}	
		    	}
		    }
		    
		    if(line.startsWith("================NUEVOSCAMPOS")) {		    	
		    	log.info(line);
		    	while ((line = br.readLine()) != null) {
		    		System.out.println("8 "+line);
		    		if(line.startsWith("Regimen")) {
		    			emisor.setRegimenFiscal(line.substring(17,line.length()));    			
		    		}
		    		if(line.startsWith("LugarDeExp")) {
		    			cfdi.setLugarExpedicion(line.substring(17,line.length()));			
		    		}
		    		if(line.startsWith("UsoCFDI")) {
		    			receptor.setUsoCFDI(CUsoCFDI.fromValue(line.substring(17,line.length())));			
		    		}
		    		if(line.startsWith("================DATOS LIVERPOOL")) {
		    			break;
		    		}	
		    	}
		    }
		    
		    
		    
		    
		    System.out.println("0 "+line); 
		}
		String xml = utilsCfdi4Service.createXmlFromComprobante(cfdi);
		log.info(xml);
		cfdi.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		cfdi.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  utilsCfdi4Service.createXmlFromComprobante(cfdi);
		return xmlOriginalSello;


	}
	
}
