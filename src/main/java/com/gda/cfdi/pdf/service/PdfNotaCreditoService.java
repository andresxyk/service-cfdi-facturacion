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
import com.gda.cfdi.pdf.dto.TNotaCreditoEntityDto;
import com.gda.cfdi.pdf.service.seriea.CreacionPDFService;
import com.gda.cfdi.pdf.service.serieaV4.CreacionPDFV4Service;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

@Service
public class PdfNotaCreditoService {

	private static final Logger log = LoggerFactory.getLogger(PdfNotaCreditoService.class);

	@Autowired
	private Environment env;

	@Autowired
	private ConsultaService consultaService;

	@Autowired
	private UtilsService utilsService;

	@Autowired
	private CreacionPDFService creacionPdf;
	
	@Autowired
	private CreacionPDFV4Service creacionPDFV4;

	public String generarPdf(Integer ufoliofactura, boolean bRetencion)
			throws DocumentException, IOException, Exception {
		try {
			
			TNotaCreditoEntityDto creditoEntityDto = consultaService.obtenerNotaCredito(ufoliofactura);
			System.out.println(creditoEntityDto.toString());
			String ruta = "";
			String xml = creditoEntityDto.getSxmlsello();
			System.out.println(xml);
			if (creditoEntityDto != null) {
				if (!xml.isEmpty() && xml.trim().length() > 5) {
					Comprobante comprobante = utilsService.createComplementoFromXml(xml);
					Integer cmarca = consultaService.obtenerMarcaConvenio(creditoEntityDto.getCconvenio());
					
					PdfInfoDto infoPDF = new PdfInfoDto();
//				infoPDF.setKfactura(kfactura);
					
					String dirSucursal = consultaService
							.getDireccionSucursalFacturaConsulta(creditoEntityDto.getUfoliofactura(), true);
					Integer centidadlegal = creditoEntityDto.getCentidalegal();
					infoPDF.setDirSucursal(dirSucursal);
					
					String dirEmisorSucursal = "";
					if (cmarca == 7) {
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
					} else {
						dirEmisorSucursal = consultaService
								.getDireccionFiscalEmisorConvenio(creditoEntityDto.getCconvenio());
					}
					infoPDF.setDirFiscalEmisor(dirEmisorSucursal);
					infoPDF.setConsecutivo(creditoEntityDto.getUfoliofactura());
					
//				DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(facturaDto.getIdDatoFiscal(), false);
					infoPDF.setNombrePaciente("");
					infoPDF.setAdendaDireccion("");
					
					String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
					infoPDF.setDescripcionMetodoPago(strMetodoPago);
					log.info("UsoCfdi:"+comprobante.getReceptor().getUsoCFDI().value());
					String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
					infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
					
					infoPDF.setCadenaOriginal(creditoEntityDto.getScadenaoriginal());
					
					Boolean bDirFiscal = consultaService.getDespliegueFiscalConvenio(creditoEntityDto.getCconvenio());
					
					String carpeta = "";
					String inicioNom = "";
					String folioFactura = "";
					log.info("marcaFac--->>>>   " + cmarca);
					if (cmarca == 1) {
						carpeta = "Olab";
					} else if (cmarca == 4) {
						carpeta = "Azteca";
					} else if (cmarca == 5) {
						carpeta = "Swisslab";
					} else if (cmarca == 15) {
						carpeta = "Swisslab";
					} else if (cmarca == 7) {
						if (centidadlegal == 7) {
							carpeta = "Jenner/Prado";
						} else if (centidadlegal == 8) {
							carpeta = "Jenner/Lean";
						} else if (centidadlegal == 5) {
							carpeta = "Azteca";
						}
						
					} else if (cmarca == 19) {
						carpeta = "FamilyLabsNorte";
					}
					
					if (cmarca == 15) {
						inicioNom = "NotaCredito_NCASL";
					} else {
						inicioNom = "NotaCredito_NCA";
					}
					folioFactura = utilsService.formatoFolio(creditoEntityDto.getUfoliofactura(), 7);
					
					String nobreArchivo = inicioNom + folioFactura;
					
					switch (cmarca) {
					case 1:
						log.info("OLAB*****     " + infoPDF + "   " + creditoEntityDto.getUfoliofactura() + "  "
								+ nobreArchivo);
						ruta = creacionPdf.CrearPdfMarcaOlab(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = creacionPdf.CrearPdfMarcaSwiss(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = creacionPdf.CrearPdfMarcaLiacsa(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 7:
						log.info("JENNER*****");
						if (centidadlegal == 7) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, true, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,true);	
						} else if (centidadlegal == 8) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, false, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,false);
						} else if (centidadlegal == 5) {
							log.info("**************JENNER****AZTECA*****");
							ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, bRetencion, comprobante, bDirFiscal, env);
						}
						break;
					case 19:
						log.info("FamilyLabsNorte*****");
						ruta = creacionPdf.CrearPdfMarcaSwiss(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
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
	
	public String generarPdfV4(Integer ufoliofactura, boolean bRetencion)
			throws DocumentException, IOException, Exception {
		try {
			
			TNotaCreditoEntityDto creditoEntityDto = consultaService.obtenerNotaCredito(ufoliofactura);
			System.out.println(creditoEntityDto.toString());
			String ruta = "";
			String xml = creditoEntityDto.getSxmlsello();
			System.out.println(xml);
			if (creditoEntityDto != null) {
				if (!xml.isEmpty() && xml.trim().length() > 5) {
					mx.gob.sat.cfd._4.Comprobante comprobante = utilsService.createComprobanteFromXml(xml);
					Integer cmarca = consultaService.obtenerMarcaConvenio(creditoEntityDto.getCconvenio());
					
					PdfInfoDto infoPDF = new PdfInfoDto();
//				infoPDF.setKfactura(kfactura);
					
					String dirSucursal = consultaService
							.getDireccionSucursalFacturaConsulta(creditoEntityDto.getUfoliofactura(), true);
					Integer centidadlegal = creditoEntityDto.getCentidalegal();
					infoPDF.setDirSucursal(dirSucursal);
					
					String dirEmisorSucursal = "";
					if (cmarca == 7) {
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
					} else {
						dirEmisorSucursal = consultaService
								.getDireccionFiscalEmisorConvenio(creditoEntityDto.getCconvenio());
					}
					infoPDF.setDirFiscalEmisor(dirEmisorSucursal);
					infoPDF.setConsecutivo(creditoEntityDto.getUfoliofactura());
					
					DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(creditoEntityDto.getKdatofiscal(), false);
					infoPDF.setNombrePaciente("");
					infoPDF.setAdendaDireccion(datosFiscales.getSdireccion());
					
					String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
					infoPDF.setDescripcionMetodoPago(strMetodoPago);
					log.info("UsoCfdi:"+comprobante.getReceptor().getUsoCFDI().value());
					String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
					infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
					
					infoPDF.setCadenaOriginal(creditoEntityDto.getScadenaoriginal());
					
					Boolean bDirFiscal = consultaService.getDespliegueFiscalConvenio(creditoEntityDto.getCconvenio());
					
					String carpeta = "";
					String inicioNom = "";
					String folioFactura = "";
					log.info("marcaFac--->>>>   " + cmarca);
					if (cmarca == 1) {
						carpeta = "Olab";
					} else if (cmarca == 4) {
						carpeta = "Azteca";
					} else if (cmarca == 5) {
						carpeta = "Swisslab";
					} else if (cmarca == 15) {
						carpeta = "Swisslab";
					} else if (cmarca == 7) {
						if (centidadlegal == 7) {
							carpeta = "Jenner/Prado";
						} else if (centidadlegal == 8) {
							carpeta = "Jenner/Lean";
						} else if (centidadlegal == 5) {
							carpeta = "Azteca";
						}
						
					} else if (cmarca == 19) {
						carpeta = "FamilyLabsNorte";
					} else if (cmarca == 21) {
						carpeta = "AsesoresSur";
					} else if (cmarca == 20) {
						carpeta = "Exakta";
					} else if (cmarca == 16) {
						carpeta = "Moreira";
					} else if (cmarca == 22) {
						carpeta = "Polab";
					} else if (cmarca == 25) {
						carpeta = "BiomedicaReferencia";
					} else if (cmarca == 26) {
						carpeta = "Promedic";
					}
					
					if (cmarca == 15) {
						inicioNom = "NotaCredito_NCASL";
					} else {
						inicioNom = "NotaCredito_NCA";
					}
					folioFactura = utilsService.formatoFolio(creditoEntityDto.getUfoliofactura(), 7);
					
					String nobreArchivo = inicioNom + folioFactura;
					
					switch (cmarca) {
					case 1:
						log.info("OLAB*****     " + infoPDF + "   " + creditoEntityDto.getUfoliofactura() + "  "
								+ nobreArchivo);
						ruta = creacionPDFV4.CrearPdfMarcaOlab(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = creacionPDFV4.CrearPdfMarcaAzteca(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = creacionPDFV4.CrearPdfMarcaSwiss(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = creacionPDFV4.CrearPdfMarcaLiacsa(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 7:
						log.info("JENNER*****");
						if (centidadlegal == 7) {
							ruta = creacionPDFV4.CrearPdfMarcaJenner(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, true, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,true);	
						} else if (centidadlegal == 8) {
							ruta = creacionPDFV4.CrearPdfMarcaJenner(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, false, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,false);
						} else if (centidadlegal == 5) {
							log.info("**************JENNER****AZTECA*****");
							ruta = creacionPDFV4.CrearPdfMarcaAzteca(infoPDF, creditoEntityDto.getUfoliofactura(),
									nobreArchivo, bRetencion, comprobante, bDirFiscal, env);
						}
						break;
					case 19:
						log.info("FamilyLabsNorte*****");
						ruta = creacionPDFV4.CrearPdfMarcaFamilyLabsNorte(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 21:
						log.info("AsesoresSur*****");
						ruta = creacionPDFV4.CrearPdfMarcaAsesoresSur(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 20:
						log.info("Exakta*****");
						ruta = creacionPDFV4.CrearPdfMarcaExakta(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 16:
						log.info("Moreira*****");
						ruta = creacionPDFV4.CrearPdfMarcaMoreira(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 22:
						log.info("Polab*****");
						ruta = creacionPDFV4.CrearPdfMarcaPolab(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 25:
						log.info("Biomedica*****");
						ruta = creacionPDFV4.CrearPdfMarcaBiomedica(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
								bRetencion, comprobante, bDirFiscal, env);
						break;
					case 26:
						log.info("Promedic*****");
						ruta = creacionPDFV4.CrearPdfMarcaPromedic(infoPDF, creditoEntityDto.getUfoliofactura(), nobreArchivo,
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

}
