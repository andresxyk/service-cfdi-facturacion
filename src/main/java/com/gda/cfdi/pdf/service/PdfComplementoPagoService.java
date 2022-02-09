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
import com.gda.cfdi.pdf.service.serieaorden.CfdiPdfService;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

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
	
	public String generarPdfOrden(Integer kfactura) throws Exception {
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
					sserie = comprobante.getSerie();
					String inicioNom = "";
					String folioFactura = "";
					try {
						List<ComplementoDatosDto> complementoDatos = consultaService.findComplementoDatosById(comprobante.getEmisor().getRfc(), sserie, comprobante.getFolio());
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
							default:
								break;
							}
						} else {
							throw new NullPointerException("La consulta de complementos esta vacía");
						}

					} catch (DocumentException | IOException | NullPointerException e) {
						log.error("Error al generar el pdf" + e.getMessage());
					}					
				}
			}
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
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

}
