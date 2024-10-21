package com.gda.cfdi.pdf.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.service.complementopagoV4.ComplementoPdfV4Service;
import com.gda.cfdi.pdf.utils.GeneralUtil;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.cfd.pagos.Pagos.Pago.DoctoRelacionado;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class PdfComplementoPagoService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfComplementoPagoService.class);
	
	@Autowired
	private Environment env;	
	@Autowired
	private ConsultaService consultaService;		
	@Autowired
	private UtilsService utilsService;	
	@Autowired
	private ComplementoPdfService complementoPdfBo;	
	@Autowired
	private ComplementoPdfV4Service complementoPdfV4Service;
	@Autowired
	private GeneralUtil generalUtil;
	
	
	public String generarPdfOrden(Integer kfactura, Boolean bReturnBase64) throws Exception {
		try {
			TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
			String ruta = "";
			String xml = facturaDto.getXmlTimbrado();
			System.out.println(xml);
			if(facturaDto!=null) {
				if(!xml.isEmpty() && xml.trim().length()>5) {
					Integer cmarca = null;
					String sserie = null;
					Comprobante comprobante = utilsService.createComplementoFromXml(xml);
					if (comprobante.getEmisor().getRfc().equals("ECD741021QA5")) {
						cmarca = 1;
					}
					if (comprobante.getEmisor().getRfc().equals("LQC920131M20")) {
						cmarca = 4;
					}
					if (comprobante.getEmisor().getRfc().equals("SWI1201268J8")) {
						cmarca = 5;
					}
					if (comprobante.getEmisor().getRfc().equals("LCP061017PA9")) {
						cmarca = 7;
					}
					if (comprobante.getEmisor().getRfc().equals("LCL050622DD9")) {
						cmarca = 8;
					}
					if (comprobante.getEmisor().getRfc().equals("LAB020416Q67")) {
						cmarca = 19;
					}
					if (comprobante.getEmisor().getRfc().equals("AEL9703115B0")) {
						cmarca = 21;
					}
					if (comprobante.getEmisor().getRfc().equals("LBA840320GC8")) {
						cmarca = 20;
					}
					if (comprobante.getEmisor().getRfc().equals("IME0103012D3")) {
						cmarca = 20;
					}
					if (comprobante.getEmisor().getRfc().equals("BIO7603164H0")) {
						cmarca = 16;
					}
					if (comprobante.getEmisor().getRfc().equals("RFCPOLAB0000")) {
						cmarca = 22;
					}
					if (comprobante.getEmisor().getRfc().equals("BRE9205181I1")) {
						cmarca = 25;
					}
					if (comprobante.getEmisor().getRfc().equals("RFCPROMEDIC0")) {
						cmarca = 26;
					}
					
					
					sserie = this.getSerieFacturaRelacionada(comprobante);
					String inicioNom = "";
					String folioFactura = "";
					try {
						List<ComplementoDatosDto> complementoDatos = consultaService.findComplementoDatosById(comprobante.getEmisor().getRfc(), comprobante.getSerie(), comprobante.getFolio());
						if (complementoDatos.size() > 0 && complementoDatos != null) {
							ComplementoDatosDto complementoDato = complementoDatos.get(0);
							switch (comprobante.getEmisor().getRfc()) {
							case "SWI1201268J8":// Swisslab
								log.info("Swisslab");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfBo.crearPdfMarcaSwiss(comprobante, complementoDato, inicioNom + folioFactura, sserie);
								break;
							case "ECD741021QA5":// Olab
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura =utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfBo.crearPdfMarcaOlab(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "LQC920131M20":// Azteca
								if(cmarca == 4){
									inicioNom = "FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfBo.crearPdfMarcaAzteca(comprobante, complementoDato, inicioNom + folioFactura);
								}
								if(cmarca == 7){
									inicioNom = "Prado/XMLTMP/PDF/FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfBo.crearPdfMarcaJennerLogoAzteca(comprobante, complementoDato, inicioNom + folioFactura);
//									 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato, inicioNom + folioFactura);
								}
								if(cmarca == 8){
									inicioNom = "Lean/XMLTMP/PDF/FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfBo.crearPdfMarcaJennerLogoAzteca(comprobante, complementoDato, inicioNom + folioFactura);
//									 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato,inicioNom + folioFactura);
								}
								
								break;
							case "LCP061017PA9":// Jenner
								inicioNom = "Prado/XMLTMP/PDF/FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "LCL050622DD9":// Jenner
								inicioNom = "Lean/XMLTMP/PDF/FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato,inicioNom + folioFactura);
								break;
							case "LAB020416Q67":// FamilyLabsNorte
								log.info("FamilyLabsNorte");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfBo.crearPdfMarcaFamilyLabsNorte(comprobante, complementoDato, inicioNom + folioFactura, sserie);
								break;
							case "AEL9703115B0":// AsesoresSur
								log.info("AsesoresSur");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfBo.crearPdfMarcaFamilyLabsNorte(comprobante, complementoDato, inicioNom + folioFactura, sserie);
								break;
								
							default:
								break;
							}
						} else {
							throw new NullPointerException("La consulta de complementos esta vacía");
						}

					} catch (DocumentException | IOException | NullPointerException e) {
						log.error("Error al generar el pdf " + e.getMessage());
					}					
				}
			}
			log.info("ruta:"+ruta);
			if(bReturnBase64) {
				String b64 = null;
				try {
					File file = new File(ruta);
					byte [] bytes = Files.readAllBytes(file.toPath());
					
					b64 = Base64.getEncoder().encodeToString(bytes);
//		      file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return b64;						
			}else {
				return ruta;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	public String generarPdfOrdenV4(Integer kfactura, Boolean bReturnBase64) throws Exception {
		try {
			TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
			String ruta = "";
			String xml = facturaDto.getXmlTimbrado();
			System.out.println(xml);
			if(facturaDto!=null) {
				if(!xml.isEmpty() && xml.trim().length()>5) {
					Integer cmarca = null;
					String sserie = null;
					mx.gob.sat.cfd._4.Comprobante comprobante = utilsService.createComprobanteFromXml(xml);
					if (comprobante.getEmisor().getRfc().equals("ECD741021QA5")) {
						cmarca = 1;
					}
					if (comprobante.getEmisor().getRfc().equals("LQC920131M20")) {
						cmarca = 4;
					}
					if (comprobante.getEmisor().getRfc().equals("SWI1201268J8")) {
						cmarca = 5;
					}
					if (comprobante.getEmisor().getRfc().equals("LCP061017PA9")) {
						cmarca = 7;
					}
					if (comprobante.getEmisor().getRfc().equals("LCL050622DD9")) {
						cmarca = 8;
					}
					if (comprobante.getEmisor().getRfc().equals("LAB020416Q67")) {
						cmarca = 19;
					}
					if (comprobante.getEmisor().getRfc().equals("AEL9703115B0")) {
						cmarca = 21;
					}
					if (comprobante.getEmisor().getRfc().equals("LBA840320GC8")) {
						cmarca = 20;
					}
					if (comprobante.getEmisor().getRfc().equals("IME0103012D3")) {
						cmarca = 20;
					}
					if (comprobante.getEmisor().getRfc().equals("BIO7603164H0")) {
						cmarca = 16;
					}
					if (comprobante.getEmisor().getRfc().equals("RFCPOLAB0000")) {
						cmarca = 22;
					}
					if (comprobante.getEmisor().getRfc().equals("BRE9205181I1")) {
						cmarca = 25;
					}
					if (comprobante.getEmisor().getRfc().equals("RFCPROMEDIC0")) {
						cmarca = 26;
					}
					
					sserie = this.getSerieFacturaRelacionadaV4(comprobante);
					String inicioNom = "";
					String folioFactura = "";
					try {
						List<ComplementoDatosDto> complementoDatos = consultaService.findComplementoDatosById(comprobante.getEmisor().getRfc(), comprobante.getSerie(), comprobante.getFolio());
						if (complementoDatos.size() > 0 && complementoDatos != null) {
							ComplementoDatosDto complementoDato = complementoDatos.get(0);
							TimbreFiscalDigital tfd = generalUtil.getTimbreFiscalDigital(comprobante);
							String xmlTfd = generalUtil.getXMLTimbreFiscalDigital(tfd);
							complementoDato.setsCadenaOriginal(generalUtil.generarCadenaOriginalTFD(xmlTfd));
							
							switch (comprobante.getEmisor().getRfc()) {
							case "SWI1201268J8":// Swisslab
								log.info("Swisslab");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaSwiss(comprobante, complementoDato, inicioNom + folioFactura, sserie);
								break;
							case "ECD741021QA5":// Olab
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura =utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaOlab(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "LQC920131M20":// Azteca
								if(cmarca == 4){
									inicioNom = "FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfV4Service.crearPdfMarcaAzteca(comprobante, complementoDato, inicioNom + folioFactura);
								}
								if(cmarca == 7){
									inicioNom = "Prado/XMLTMP/PDF/FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfV4Service.crearPdfMarcaJennerLogoAzteca(comprobante, complementoDato, inicioNom + folioFactura);
//									 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato, inicioNom + folioFactura);
								}
								if(cmarca == 8){
									inicioNom = "Lean/XMLTMP/PDF/FacturacionElectronica_ACC";
									folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
									ruta = complementoPdfV4Service.crearPdfMarcaJennerLogoAzteca(comprobante, complementoDato, inicioNom + folioFactura);
//									 ruta = complementoPdfBo.crearPdfMarcaJenner(comprobante, complementoDato,inicioNom + folioFactura);
								}
								
								break;
							case "LCP061017PA9":// Jenner
								inicioNom = "Prado/XMLTMP/PDF/FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								 ruta = complementoPdfV4Service.crearPdfMarcaJenner(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "LCL050622DD9":// Jenner
								inicioNom = "Lean/XMLTMP/PDF/FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								 ruta = complementoPdfV4Service.crearPdfMarcaJenner(comprobante, complementoDato,inicioNom + folioFactura);
								break;
							case "LAB020416Q67":// FamilyLabsNorte
								log.info("FamilyLabsNorte");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaFamilyLabsNorte(comprobante, complementoDato, inicioNom + folioFactura, sserie);
								break;
							case "AEL9703115B0":// AsesoresSur
								log.info("AsesoresSur");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaAsesoresSur(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "LBA840320GC8":// Exakta Laboartorio
								log.info("Exakta");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaExakta(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "IME0103012D3":// Exakta Imagen
								log.info("Exakta");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaExakta(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "BIO7603164H0":// Moreira
								log.info("Moreira");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaMoreira(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "RFCPOLAB0000":// Polab
								log.info("Polab");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaPolab(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "BRE9205181I1":// Biomedica
								log.info("Biomedica");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaBiomedica(comprobante, complementoDato, inicioNom + folioFactura);
								break;
							case "RFCPROMEDIC0":// Promedic
								log.info("Promedic");
								inicioNom = "FacturacionElectronica_ACC";
								folioFactura = utilsService.formatoFolio(complementoDato.getuFolioFactura(), 6);
								ruta = complementoPdfV4Service.crearPdfMarcaPromedic(comprobante, complementoDato, inicioNom + folioFactura);
								break;
								
							default:
								break;
							}
						} else {
							throw new NullPointerException("La consulta de complementos esta vacía");
						}

					} catch (DocumentException | IOException | NullPointerException e) {
						log.error("Error al generar el pdf " + e.getMessage());
					}					
				}
			}
			log.info("ruta:"+ruta);
			if(bReturnBase64) {
				String b64 = null;
				try {
					File file = new File(ruta);
					byte [] bytes = Files.readAllBytes(file.toPath());
					
					b64 = Base64.getEncoder().encodeToString(bytes);
//					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return b64;						
			}else {
				return ruta;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	
	public String getSerieFacturaRelacionada(Comprobante comprobante) {
		String sserie = "";
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			log.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {
				log.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						DoctoRelacionado dr = pago.getDoctoRelacionado().get(0);
						sserie = dr.getSerie();
						break;
					}
				}
			}
		}
		return sserie;
	}
	
	public String getSerieFacturaRelacionadaV4(mx.gob.sat.cfd._4.Comprobante comprobante) {
		String sserie = "";		
		for (Object obj : comprobante.getComplemento().getAny()) {
			log.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof mx.gob.sat.pagos20.Pagos) {
				mx.gob.sat.pagos20.Pagos pagos = (mx.gob.sat.pagos20.Pagos) obj;
				for (mx.gob.sat.pagos20.Pagos.Pago pago : pagos.getPago()) {
					mx.gob.sat.pagos20.Pagos.Pago.DoctoRelacionado dr = pago.getDoctoRelacionado().get(0);
					sserie = dr.getSerie();
					break;
				}
			}
		}
		return sserie;
	}

}
