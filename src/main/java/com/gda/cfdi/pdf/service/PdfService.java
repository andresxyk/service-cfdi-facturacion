package com.gda.cfdi.pdf.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.Comprobante.Conceptos;
import mx.gob.sat.cfd._3.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfd._3.Comprobante.Emisor;
import mx.gob.sat.cfd._3.Comprobante.Receptor;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CRegimenFiscal;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class PdfService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	public String generarPdf(Integer kfactura, Integer cmarca, Integer csucursal){
		
		ArrayList<Integer> arrPrado = new ArrayList<Integer>(Arrays.asList(117,118,126,127,128,129,130,132,134,138,139,140,141,142,143,196,198,199,200));
		ArrayList<Integer> arrLean = new ArrayList<Integer>(Arrays.asList(115,116,119,120,121,122,123,124,125,131,133,135,136,137,177,146,197));
		
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getSxmlsello();
		if(facturaDto!=null) {
			if(!xml.isEmpty() && xml.trim().length()>5) {
				Comprobante comprobante = this.createComplementoFromXml(xml);
				PdfInfoDto infoPDF = new PdfInfoDto();
				infoPDF.setComplementoConcepto(false);
				if(xml.contains("PorCuentadeTerceros")){
					infoPDF.setComplementoConcepto(true);
				}
				log.info("cfdi:ComplementoConcepto:"+infoPDF.getComplementoConcepto());
				String dirSucursal = consultaService.getDireccionSucursalFactura(kfactura);
				
				if(csucursal.equals(219)){
					dirSucursal = "CALLE OJINAGA 423, ZONA CENTRO C.P. 31000 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(218)){
					dirSucursal = "CALLE GENERAL RETANA 905, SAN FELIPE I C.P. 31203 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(217)){
					dirSucursal = "AV. CANTERA 9139 INT. 110, MISIONES C.P. 31115 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(220)){
					dirSucursal = "CALLE LERDO DE TEJADA 504, CAMARGO C.P. 33700 CAMARGO, CHIHUAHUA";
				}else if(csucursal.equals(216)){
					dirSucursal = "AV. FREDOR DOSTOYESVSKI 1308, ALAMEDAD C.P. 31136 CHIHUAHUA, CHIHUAHUA";
				}
				
				infoPDF.setDirSucursal(dirSucursal);
				
				Integer centidadlegal = null;
				if (cmarca == 1) {
					centidadlegal = 1;
				}
				else if (cmarca == 4) {
					centidadlegal = 5;
				}
				else if (cmarca == 7) {
					if(arrPrado.contains(csucursal)){
						System.out.println("**** Prado *****");
						centidadlegal = 7;				
					}else if(arrLean.contains(csucursal)){
						System.out.println("**** Lean *****");
						centidadlegal = 8;	
					}				
				}
				else if (cmarca == 5) {
					centidadlegal = 6;
				}
				
				String dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
				infoPDF.setDirFiscalEmisor(dirEmisorSucursal);				
				List<TOrdenSucursalFacDto> listTosf = consultaService.getTOrdenSucursalFacByKfactura(kfactura);
				infoPDF.setConsecutivo(listTosf.get(0).getUorden());
				String nombrepaciente = consultaService.getPacienteFactura(kfactura);
				infoPDF.setNombrePaciente(nombrepaciente);
				
				infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
				
			}
		}
		
		
		
		List<TFacturaEntity> facturaObtenida = reportesFacturacionDao.consultarXmlFactura(kfactura);
		String xml = facturaObtenida.get(0).getSxmlsello().toString();
		String ruta = "";
		LOG.info("::XML::: "+xml);
		if(!xml.isEmpty() && xml.trim().length()>5){
						
			
			for (int i = 0; i < xmlTImbrado.getLength(); i++) {		
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Emisor")) {
					nameEmisor = xmlTImbrado.item(i).getAttributes().getNamedItem("Nombre").getTextContent();
					regimenFiscalEmisor = xmlTImbrado.item(i).getAttributes().getNamedItem("RegimenFiscal").getTextContent();
					rfcEmisor = xmlTImbrado.item(i).getAttributes().getNamedItem("Rfc").getTextContent();
					
					Emisor emisor = new Emisor();
					emisor.setNombre(nameEmisor);
					CRegimenFiscal cregimenfiscal = null;
					
					if(regimenFiscalEmisor.equals("601")){
						cregimenfiscal = CRegimenFiscal.C601; 
					}
					else if(regimenFiscalEmisor.equals("603")){
						cregimenfiscal = CRegimenFiscal.C603; 
					}
					else if(regimenFiscalEmisor.equals("605")){
						cregimenfiscal = CRegimenFiscal.C605; 
					}
					else if(regimenFiscalEmisor.equals("606")){
						cregimenfiscal = CRegimenFiscal.C606; 
					}
					else if(regimenFiscalEmisor.equals("607")){
						cregimenfiscal = CRegimenFiscal.C607; 
					}
					else if(regimenFiscalEmisor.equals("608")){
						cregimenfiscal = CRegimenFiscal.C608; 
					}
					else if(regimenFiscalEmisor.equals("609")){
						cregimenfiscal = CRegimenFiscal.C609; 
					}
					else if(regimenFiscalEmisor.equals("610")){
						cregimenfiscal = CRegimenFiscal.C610; 
					}
					else if(regimenFiscalEmisor.equals("611")){
						cregimenfiscal = CRegimenFiscal.C611; 
					}
					else if(regimenFiscalEmisor.equals("612")){
						cregimenfiscal = CRegimenFiscal.C612; 
					}
					else if(regimenFiscalEmisor.equals("614")){
						cregimenfiscal = CRegimenFiscal.C614; 
					}
					else if(regimenFiscalEmisor.equals("615")){
						cregimenfiscal = CRegimenFiscal.C615; 
					}
					else if(regimenFiscalEmisor.equals("616")){
						cregimenfiscal = CRegimenFiscal.C616; 
					}
					else if(regimenFiscalEmisor.equals("620")){
						cregimenfiscal = CRegimenFiscal.C620; 
					}
					else if(regimenFiscalEmisor.equals("621")){
						cregimenfiscal = CRegimenFiscal.C621; 
					}
					else if(regimenFiscalEmisor.equals("622")){
						cregimenfiscal = CRegimenFiscal.C622; 
					}
					else if(regimenFiscalEmisor.equals("623")){
						cregimenfiscal = CRegimenFiscal.C623; 
					}
					else if(regimenFiscalEmisor.equals("624")){
						cregimenfiscal = CRegimenFiscal.C624; 
					}
					else if(regimenFiscalEmisor.equals("628")){
						cregimenfiscal = CRegimenFiscal.C628; 
					}
					else if(regimenFiscalEmisor.equals("629")){
						cregimenfiscal = CRegimenFiscal.C629; 
					}
					else if(regimenFiscalEmisor.equals("630")){
						cregimenfiscal = CRegimenFiscal.C630; 
					}
					emisor.setRegimenFiscal(cregimenfiscal);
					emisor.setRfc(rfcEmisor);
					
					infoPDF.setEmisor(emisor);
				}
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Receptor")) {
					nameReceptor = xmlTImbrado.item(i).getAttributes().getNamedItem("Nombre").getTextContent();
					rfcReceptor = xmlTImbrado.item(i).getAttributes().getNamedItem("Rfc").getTextContent();
					usoCFDI = reportesFacturacionDao.consultarUsoCFDI(xmlTImbrado.item(i).getAttributes().getNamedItem("UsoCFDI").getTextContent().toString());
					
					
					Receptor receptor = new Receptor();
					receptor.setNombre(nameReceptor);
					receptor.setRfc(rfcReceptor);
					receptor.setDescripcionusocfdi(usoCFDI);
					infoPDF.setReceptor(receptor);
				}
				
				
				
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Conceptos")) {
					NodeList conceptos = xmlTImbrado.item(i).getChildNodes();
					Comprobante.Conceptos infoConceptos = new Conceptos();
					lstInfoConceptos = new ArrayList<Concepto>();
					lstTrasladoConcepto = new ArrayList<Traslado>();
					for (int j = 0; j < conceptos.getLength(); j++) {
						if (conceptos.item(j).getNodeName().equals("cfdi:Concepto")) {
							Comprobante.Conceptos.Concepto c = new Comprobante.Conceptos.Concepto();
							Comprobante.Conceptos.Concepto.Impuestos infoImpuestos = new Impuestos();
							cantidad = conceptos.item(j).getAttributes().getNamedItem("Cantidad").getTextContent();
							claveProdServ = conceptos.item(j).getAttributes().getNamedItem("ClaveProdServ").getTextContent();
							claveUnidad = conceptos.item(j).getAttributes().getNamedItem("ClaveUnidad").getTextContent();
							descripcion = conceptos.item(j).getAttributes().getNamedItem("Descripcion").getTextContent();
							
							noIdentificacion = conceptos.item(j).getAttributes().getNamedItem("NoIdentificacion") != null ? 
									conceptos.item(j).getAttributes().getNamedItem("NoIdentificacion").getTextContent(): "";
							unidad = conceptos.item(j).getAttributes().getNamedItem("Unidad") != null ? 
									conceptos.item(j).getAttributes().getNamedItem("Unidad").getTextContent():"";
							valorUnitario = conceptos.item(j).getAttributes().getNamedItem("ValorUnitario").getTextContent();
							LOG.info("VAlores COncepto  "+cantidad+" "+claveProdServ+" "+claveUnidad+" "+descripcion+" "+importeConcepto+" "+noIdentificacion
									+" "+unidad+" "+valorUnitario);
							c.setCantidad(new BigDecimal(cantidad));
							c.setClaveProdServ(claveProdServ);
							c.setClaveUnidad(CClaveUnidad.CE48); //claveUnidad);
							c.setDescripcion(descripcion);
							c.setNoIdentificacion(noIdentificacion);
							c.setUnidad(unidad);
							c.setValorUnitario(new BigDecimal(valorUnitario));
							NodeList concepto = conceptos.item(j).getChildNodes();
							Comprobante.Conceptos.Concepto.Impuestos.Traslados infoTraslados = new Traslados();
							for (int k = 0; k < concepto.getLength(); k++) {
								if (concepto.item(k).getNodeName().equals("cfdi:Impuestos")) {
									NodeList impuestos = concepto.item(k).getChildNodes();
									for (int l = 0; l < impuestos.getLength(); l++) {
										if (impuestos.item(l).getNodeName().equals("cfdi:Traslados")) {
											NodeList traslados = impuestos.item(l).getChildNodes();
											
											for (int m = 0; m < traslados.getLength(); m++) {
												Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado traslado = new  Traslado();
												if (traslados.item(m).getNodeName().equals("cfdi:Traslado")) {
													base = traslados.item(m).getAttributes().getNamedItem("Base").getTextContent();
													importeConcepto = traslados.item(m).getAttributes().getNamedItem("Importe") !=null ? 
															traslados.item(m).getAttributes().getNamedItem("Importe").getTextContent() : "0";
													LOG.info("$$$$$$$$$$$$$$$$ :base "+base+ "  importeConcepto:  "+importeConcepto);
													traslado.setBase(new BigDecimal(base));
													traslado.setImporte(new BigDecimal(importeConcepto));
													lstTrasladoConcepto.add(traslado);
													LOG.info("%%%%%%%%%%%"+lstTrasladoConcepto.size());
													infoTraslados.setTraslado(lstTrasladoConcepto);
													infoImpuestos.setTraslados(infoTraslados);
													c.setImpuestos(infoImpuestos);
													LOG.info("################"+lstInfoConceptos.size());
													lstInfoConceptos.add(c);
													infoConceptos.setConcepto(lstInfoConceptos);
												}
											}
										}
									}
								}
//								if(concepto.item(k).getNodeName().equals("cfdi:ComplementoConcepto")){
//									infoPDF.setComplementoConcepto(true);
//								}
								
								
							}				

						}
					}
					LOG.info(")))))))))))))"+infoConceptos.getConcepto().size());
					infoPDF.setConceptos(infoConceptos);
				}
				
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Impuestos")){
					LOG.info("ENTRE EN ::::: cfdi:Impuestos "+i);
					NodeList impuestos = xmlTImbrado.item(i).getChildNodes();
					//LOG.info("********"+xmlTImbrado.item(i).getAttributes().getNamedItem("TotalImpuestosRetenidos").getTextContent());//////
					LOG.info("********"+xmlTImbrado.item(i).getAttributes().getNamedItem("TotalImpuestosTrasladados").getTextContent());//////
					for (int j = 0; j < impuestos.getLength(); j++) {
						if (impuestos.item(j).getNodeName().equals("cfdi:Traslados")) {
							NodeList traslados = impuestos.item(j).getChildNodes();
							for (int k = 0; k < traslados.getLength(); k++) {
								if (traslados.item(k).getNodeName().equals("cfdi:Traslado")) {
									LOG.info("\t\tENTRE EN ::::: cfdi:Traslado "+k);
									importe = traslados.item(k).getAttributes().getNamedItem("Importe").getTextContent();
									impuesto = traslados.item(k).getAttributes().getNamedItem("Impuesto").getTextContent();
									tasaOcuota = traslados.item(k).getAttributes().getNamedItem("TasaOCuota")
											.getTextContent();
									tipoFactor = traslados.item(k).getAttributes().getNamedItem("TipoFactor")
											.getTextContent();
									infoPDF.setImporteResporte(importe);
									break;
								}
							}
						}
					}
				}
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Complemento")) {
					LOG.info("ENTRE EN ::::: cfdi:Complemento "+i);
					NodeList complemento = xmlTImbrado.item(i).getChildNodes();
					for (int j = 0; j < complemento.getLength(); j++) {
						if (complemento.item(j).getNodeName().equals("tfd:TimbreFiscalDigital")) {
							LOG.info("\tENTRE EN ::::: tfd:TimbreFiscalDigital "+j);
							uuid = complemento.item(j).getAttributes().getNamedItem("UUID").getTextContent();
							RfcProvCertif = complemento.item(j).getAttributes().getNamedItem("RfcProvCertif").getTextContent();
							NoCertificadoSAT = complemento.item(j).getAttributes().getNamedItem("NoCertificadoSAT").getTextContent();
							SelloCFD = complemento.item(j).getAttributes().getNamedItem("SelloCFD").getTextContent();
							SelloSat = complemento.item(j).getAttributes().getNamedItem("SelloSAT").getTextContent();
							String fechaCertificacion = complemento.item(j).getAttributes().getNamedItem("FechaTimbrado").getTextContent();
							infoPDF.setUuid(uuid);
							infoPDF.setRfcprovcertif(RfcProvCertif);
							infoPDF.setNoCertificadosat(NoCertificadoSAT);
							infoPDF.setSello(SelloCFD);
							infoPDF.setSellosat(SelloSat);
							Date fecha2 = sdf.parse(fechaCertificacion);
							infoPDF.setFechaCertificacion(fecha2);
							break;
						}
					}
				}
				
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:CfdiRelacionados")) {
					if(xmlTImbrado.item(i).getAttributes().getNamedItem("TipoRelacion") != null){
						LOG.info("Realacion::  "+xmlTImbrado.item(i).getAttributes().getNamedItem("TipoRelacion").getTextContent());
						infoPDF.setTipoRelacion(xmlTImbrado.item(i).getAttributes().getNamedItem("TipoRelacion").getTextContent() );
					}
					
					LOG.info("ENTRE EN ::::: cfdi:CfdiRelacionados " + i);
					NodeList complemento = xmlTImbrado.item(i).getChildNodes();
					for (int j = 0; j < complemento.getLength(); j++) {
						if (complemento.item(j).getNodeName().equals("cfdi:CfdiRelacionado")) {
							LOG.info("\tENTRE EN ::::: cfdi:CfdiRelacionado " + j);
							if(complemento.item(j).getAttributes().getNamedItem("UUID") != null){
								uuid = complemento.item(j).getAttributes().getNamedItem("UUID").getTextContent();
								LOG.info("uuid::::  "+uuid);
								infoPDF.setUuidRelacionado(uuid);
							}
							break;
						}
					}
				}
				
				if (xmlTImbrado.item(i).getNodeName().contentEquals("cfdi:Addenda")) {
					NodeList adenda = xmlTImbrado.item(i).getChildNodes();
					for (int j = 0; j < adenda.getLength(); j++) {
						if (adenda.item(j).getNodeName().equals("direccion")) {
							String direccion = adenda.item(j).getAttributes().getNamedItem("direccionAddenda").getTextContent();
							infoPDF.setAdendaDireccion(direccion);
							System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+direccion);
							break;
						}
					}
				}
			}
			
			infoPDF.setCadenaoriginal(CadenaOriginal);
			CreacionPDFServiceImpl creacionPdf = new CreacionPDFServiceImpl();
			
	 
			switch (cmarca) {
				case 1:
					LOG.info("OLAB*****");
					ruta = creacionPdf.CrearPdfMarcaOlab(infoPDF,kfactura);
					break;
				case 4:
					LOG.info("*******************AZTECA*****");
					ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF,kfactura);
					break;
				case 5:
					LOG.info("SWISSLAB*****");
					ruta = creacionPdf.CrearPdfMarcaSwiss(infoPDF,kfactura);
					break;
				case 15:
					LOG.info("LIACSA*****");
					ruta = creacionPdf.CrearPdfMarcaLiacsa(infoPDF,kfactura);
					break;
				case 7:
					LOG.info("JENNER*****");
					ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF,kfactura);
					break;
				default:
					break;
			} 
		}
		return ruta;
	}
	
	
	private Comprobante createComplementoFromXml(String xml) throws JAXBException {
		Comprobante comprobante = null;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();

		if (xml.toLowerCase().contains("<tfd:TimbreFiscalDigital".toLowerCase())) {
			classesMarshall.add(TimbreFiscalDigital.class);
		}
		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(this.fixXml(xml));
		comprobante = (Comprobante) unmarshaller.unmarshal(reader);

		return comprobante;
	}
	
	private static String fixXml(String xml) {
		xml = xml.replace("/cfd/3\"xmlns", "/cfd/3\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/3 http", "cfd/3 http");
		return xml;
	}
	

}
