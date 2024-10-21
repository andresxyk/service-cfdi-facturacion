package com.gda.cfdi.pdf.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.DatosFiscales;
import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.service.seriea.CreacionPDFService;
import com.gda.cfdi.pdf.service.serieaV4.CreacionPDFV4Service;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

@Service
public class PdfSerieBService {

private static final Logger log = LoggerFactory.getLogger(PdfSerieBService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private CreacionPDFService creacionPdf;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private CreacionPDFV4Service creacionPDFV4;
	
	public String generarPdf(Integer kfactura,boolean bRetencion) throws DocumentException, IOException, Exception{
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		try {
			String ruta = "";
			String xml = facturaDto.getXmlTimbrado();
			System.out.println(xml);
			if(facturaDto!=null) {
				if(!xml.isEmpty() && xml.trim().length()>5) {
					Comprobante comprobante = utilsService.createComplementoFromXml(xml);
					PdfInfoDto infoPDF = new PdfInfoDto();
					infoPDF.setKfactura(kfactura);
					
					String dirSucursal = consultaService.getDireccionSucursalFactura(kfactura);
					Integer cmarca = consultaService.obtenerMarcaConvenio(facturaDto.getIdConvenio());
					Integer centidadlegal = facturaDto.getCentidadlegal();
					infoPDF.setDirSucursal(dirSucursal);
					
					String dirEmisorSucursal = "";
					if(cmarca == 7){					
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);		
					}else{					
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisorConvenio(facturaDto.getIdConvenio());							
					}
					infoPDF.setDirFiscalEmisor(dirEmisorSucursal);
					infoPDF.setConsecutivo(facturaDto.getFolio());
					
					DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(facturaDto.getIdDatoFiscal(), false);
					infoPDF.setNombrePaciente(datosFiscales.getSrazonsocial());
					infoPDF.setAdendaDireccion(datosFiscales.getSdireccion());
					
					String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
					infoPDF.setDescripcionMetodoPago(strMetodoPago);
					String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
					infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
					
					infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
					
					Boolean bDirFiscal = consultaService.getDespliegueFiscalConvenio(facturaDto.getIdConvenio());
					log.info("centidadlegal:"+centidadlegal);
					String carpeta = "";
					String inicioNom = "";
					String folioFactura = "";
					log.info("marcaFac--->>>>   " + cmarca);
					if (cmarca == 1) {
						carpeta = "Olab";
						inicioNom = "FacturacionElectronica_B";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					} else if (cmarca == 4) {
						carpeta = "Azteca";
						inicioNom = "FacturacionElectronica_B";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					} else if (cmarca == 5) {
						carpeta = "Swisslab";
						inicioNom = "FacturacionElectronica_B";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					} else if (cmarca == 15) {
						carpeta = "Swisslab";
						inicioNom = "FacturacionElectronica_B";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					} else if (cmarca == 7) {
						if (centidadlegal == 7 || comprobante.getEmisor().getRfc().equals("LCP061017PA9")) {
							carpeta = "Jenner/Prado";
							inicioNom = "FacturacionElectronica_B";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
						} else if (centidadlegal == 8 || comprobante.getEmisor().getRfc().equals("LCL050622DD9")) {
							carpeta = "Jenner/Lean";
							inicioNom = "FacturacionElectronica_B";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
						}else if (centidadlegal == 5 || comprobante.getEmisor().getRfc().equals("LQC920131M20")){
							carpeta = "Azteca";
							inicioNom = "FacturacionElectronica_B";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
						}

					} else if (cmarca == 19) {
						carpeta = "FamilyLabsNorte";
						inicioNom = "FacturacionElectronica_B";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					}
					
					String nobreArchivo = inicioNom + folioFactura;
					
					switch (cmarca) {
					case 1:
						log.info("OLAB*****     " + infoPDF + "   " + facturaDto.getFolio() + "  "
								+ nobreArchivo);
						ruta = creacionPdf.CrearPdfMarcaOlab(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = creacionPdf.CrearPdfMarcaSwiss(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = creacionPdf.CrearPdfMarcaLiacsa(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 7:
						log.info("JENNER*****");
						if (centidadlegal == 7 || comprobante.getEmisor().getRfc().equals("LCP061017PA9")) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, true, bRetencion, comprobante, bDirFiscal, env);	
						} else if (centidadlegal == 8 || comprobante.getEmisor().getRfc().equals("LCL050622DD9")) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, false, bRetencion, comprobante, bDirFiscal, env);
						} else if (centidadlegal == 5 || comprobante.getEmisor().getRfc().equals("LQC920131M20")) {
							log.info("**************JENNER****AZTECA*****");
							ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF,
									facturaDto.getFolio(),
									nobreArchivo, bRetencion, comprobante, bDirFiscal, env);
						}
						break;
					case 19:
						log.info("FamilyLabsNorte*****");
						ruta = creacionPdf.CrearPdfMarcaFamilyLabsNorte(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					default:
						break;
					}
					
				}
			}
			String b64 = null;
			try {
				File file = new File(ruta);
				byte [] bytes = Files.readAllBytes(file.toPath());
				
				b64 = Base64.getEncoder().encodeToString(bytes);
//				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return b64;	
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
	public String generarPdfV4(Integer kfactura,boolean bRetencion) throws DocumentException, IOException, Exception{
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		System.out.println(facturaDto.toString());
		try {
			String ruta = "";
			String xml = facturaDto.getXmlTimbrado();
			System.out.println(xml);
			if(facturaDto!=null) {
				if(!xml.isEmpty() && xml.trim().length()>5) {
					mx.gob.sat.cfd._4.Comprobante comprobante = utilsService.createComprobanteFromXml(xml);
					PdfInfoDto infoPDF = new PdfInfoDto();
					infoPDF.setKfactura(kfactura);
					
					String dirSucursal = consultaService.getDireccionSucursalFactura(kfactura);
					log.info(facturaDto.getIdConvenio().toString());
					Integer cmarca = consultaService.obtenerMarcaConvenio(facturaDto.getIdConvenio());
					Integer centidadlegal = facturaDto.getCentidadlegal();
					log.info("centidadlegal:"+centidadlegal);
					infoPDF.setDirSucursal(dirSucursal);
					
					String dirEmisorSucursal = "";
					if(cmarca == 7){					
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);		
					}else{					
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisorConvenio(facturaDto.getIdConvenio());							
					}
					infoPDF.setDirFiscalEmisor(dirEmisorSucursal);
					infoPDF.setConsecutivo(facturaDto.getFolio());
					
					DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(facturaDto.getIdDatoFiscal(), false);
					infoPDF.setNombrePaciente(datosFiscales.getSrazonsocial());
					infoPDF.setAdendaDireccion(datosFiscales.getSdireccion());
					
					String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
					infoPDF.setDescripcionMetodoPago(strMetodoPago);
					String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
					infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
					
					infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
					
					Boolean bDirFiscal = consultaService.getDespliegueFiscalConvenio(facturaDto.getIdConvenio());
					
					String carpeta = "";
					String inicioNom = "";
					String folioFactura = "";
					log.info("marcaFac--->>>>   " + cmarca);
					if (cmarca == 1) {
						carpeta = "Olab";
						inicioNom = "FacturacionElectronica_A";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 8);
					} else if (cmarca == 4) {
						carpeta = "Azteca";
						inicioNom = "FacturacionElectronica_AZ";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 7);
					} else if (cmarca == 5) {
						carpeta = "Swisslab";
						inicioNom = "FacturacionElectronica_AS";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 7);
					} else if (cmarca == 15) {
						carpeta = "Swisslab";
						inicioNom = "FacturacionElectronica_ASL";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					} else if (cmarca == 7) {
						if (centidadlegal == 7) {
							carpeta = "Jenner/Prado";
							inicioNom = "FacturacionElectronica_AJP";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
//							carpeta = "Azteca";
//							inicioNom = "FacturacionElectronica_AZ";
//							folioFactura = formatoFolio(tFacturaEntity.getUfoliofactura(), 7);
						} else if (centidadlegal == 8) {
							carpeta = "Jenner/Lean";
							inicioNom = "FacturacionElectronica_AJL";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
//							carpeta = "Azteca";
//							inicioNom = "FacturacionElectronica_AZ";
//							folioFactura = formatoFolio(tFacturaEntity.getUfoliofactura(), 7);
						}else if (centidadlegal == 5){
							carpeta = "Azteca";
							inicioNom = "FacturacionElectronica_AZ";
							folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 7);
						}

					}else if (cmarca == 19) {
						carpeta = "FamilyLabsNorte";
						inicioNom = "FacturacionElectronica_AFN";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 21) {
						carpeta = "AsesoresSur";
						inicioNom = "FacturacionElectronica_AAS";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 20) {
						carpeta = "Exakta";
						inicioNom = "FacturacionElectronica_AJK";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 16) {
						carpeta = "Moreira";
						inicioNom = "FacturacionElectronica_AMO";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 22) {
						carpeta = "Polab";
						inicioNom = "FacturacionElectronica_APO";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 25) {
						carpeta = "BiomedicaReferencia";
						inicioNom = "FacturacionElectronica_ABR";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}else if (cmarca == 26) {
						carpeta = "Promedic";
						inicioNom = "FacturacionElectronica_APR";
						folioFactura = utilsService.formatoFolio(facturaDto.getFolio(), 6);
					}
					
					
					String nobreArchivo = inicioNom + folioFactura;
					
					switch (cmarca) {
					case 1:
						log.info("OLAB*****     " + infoPDF + "   " + facturaDto.getFolio() + "  "
								+ nobreArchivo);
						ruta = creacionPDFV4.CrearPdfMarcaOlab(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = creacionPDFV4.CrearPdfMarcaAzteca(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = creacionPDFV4.CrearPdfMarcaSwiss(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = creacionPDFV4.CrearPdfMarcaLiacsa(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 7:
						log.info("JENNER*****");
						if (centidadlegal == 7) {
							ruta = creacionPDFV4.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, true, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,true);	
						} else if (centidadlegal == 8) {
							ruta = creacionPDFV4.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, false, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,false);
						} else if (centidadlegal == 5) {
							log.info("**************JENNER****AZTECA*****");
							ruta = creacionPDFV4.CrearPdfMarcaAzteca(infoPDF,
									facturaDto.getFolio(),
									nobreArchivo, bRetencion, comprobante, bDirFiscal, env);
						}
						break;
					case 19:
						log.info("FamilyLabsNorte*****");
						ruta = creacionPDFV4.CrearPdfMarcaFamilyLabsNorte(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 21:
						log.info("AsesoresSur*****");
						ruta = creacionPDFV4.CrearPdfMarcaAsesoresSur(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 20:
						log.info("Exakta*****");
						ruta = creacionPDFV4.CrearPdfMarcaExakta(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 16:
						log.info("Moreira*****");
						ruta = creacionPDFV4.CrearPdfMarcaMoreira(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 22:
						log.info("Polab*****");
						ruta = creacionPDFV4.CrearPdfMarcaPolab(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 25:
						log.info("Biomedica*****");
						ruta = creacionPDFV4.CrearPdfMarcaBiomedica(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 26:
						log.info("Promedic*****");
						ruta = creacionPDFV4.CrearPdfMarcaPromedic(infoPDF, facturaDto.getFolio(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					default:
						break;
					}
					
				}
			}
			String b64 = null;
			try {
				log.info(ruta);
				File file = new File(ruta);
				byte [] bytes = Files.readAllBytes(file.toPath());
				
				b64 = Base64.getEncoder().encodeToString(bytes);
//				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return b64;	
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
	}
	
}
