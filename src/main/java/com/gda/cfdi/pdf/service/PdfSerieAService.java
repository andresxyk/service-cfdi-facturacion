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
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

@Service
public class PdfSerieAService {

private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private CreacionPDFService creacionPdf;
	
	@Autowired
	private UtilsService utilsService;
	
	public String generarPdf(Integer kfactura,boolean bRetencion) throws DocumentException, IOException, Exception{
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		System.out.println(facturaDto.toString());
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
					log.info(facturaDto.getIdConvenio().toString());
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
						if (centidadlegal == 7) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, true, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,true);	
						} else if (centidadlegal == 8) {
							ruta = creacionPdf.CrearPdfMarcaJenner(infoPDF, facturaDto.getFolio(),
									nobreArchivo, false, bRetencion, comprobante, bDirFiscal, env);
//						ruta = creacionPdf.CrearPdfMarcaJennerLogoAzteca(infoPDF,facturaKnota,nobreArchivo,false);
						} else if (centidadlegal == 5) {
							log.info("**************JENNER****AZTECA*****");
							ruta = creacionPdf.CrearPdfMarcaAzteca(infoPDF,
									facturaDto.getFolio(),
									nobreArchivo, bRetencion, comprobante, bDirFiscal, env);
						}
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
			
			return ruta;	
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
	}
	
}
