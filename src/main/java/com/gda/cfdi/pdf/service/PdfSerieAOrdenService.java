package com.gda.cfdi.pdf.service;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.controller.PdfSerieAOrdenController;
import com.gda.cfdi.pdf.dto.DatosFiscales;
import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.service.serieaorden.CfdiPdfService;

import mx.gob.sat.cfd._3.Comprobante;

@Service
public class PdfSerieAOrdenService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfSerieAOrdenController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private CfdiPdfService cfdiPdfService;
	
	@Autowired
	private UtilsService utilsService;
	
	public String generarPdfOrden(Integer kfactura,boolean isDescuento, String notaDescuento, boolean isRetencion) throws Exception {
		try {
			TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
			String ruta = "";
			String xml = facturaDto.getXmlTimbrado();
			System.out.println(xml);
			if(facturaDto!=null) {
				if(!xml.isEmpty() && xml.trim().length()>5) {
					Comprobante comprobante = utilsService.createComplementoFromXml(xml);
					Integer cconvenio = facturaDto.getIdConvenio();
					log.info("cconvenio="+cconvenio);
					Integer cmarca = consultaService.obtenerMarcaConvenio(cconvenio);
					PdfInfoDto infoPdf = new PdfInfoDto();
					infoPdf.setSmetodoPago(consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value()));
					infoPdf.setDirSucursal(consultaService.getDireccionSucursalFactura(kfactura));
					infoPdf.setDescripcionUsoCfdi(consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value()));
					
					Integer centidadlegal = null;
					if (cmarca == 1) {
						centidadlegal = 1;
					}
					else if (cmarca == 4) {
						centidadlegal = 5;
					}
					else if (cmarca == 7) {
						centidadlegal = 7;
					}
					else if (cmarca == 8) {
						centidadlegal = 8;
					}
					else if (cmarca == 5) {
						centidadlegal = 6;
					}
					else if (cmarca == 15) {
						centidadlegal = 16;
					}
					
					String dirEmisorSucursal = "";
					if(cmarca == 7){
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
					}else{
						dirEmisorSucursal = consultaService.getDireccionFiscalEmisorConvenio(cconvenio);
					}
					Boolean bDirFiscal = consultaService.getDespliegueFiscalConvenio(cconvenio);
					infoPdf.setDirFiscalEmisor(dirEmisorSucursal);
					infoPdf.setCadenaOriginal(facturaDto.getCadenaOriginal());
					DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenioAndBconvenio(facturaDto.getIdDatoFiscal(), false);
					infoPdf.setAdendaDireccion(datosFiscales.getSdireccion());
					String inicioNom = "";
					String folioFactura = "";
					switch (cmarca) {
					case 5:// Swisslab
						log.info("Swisslab");
						inicioNom = "FacturacionElectronica_AS";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 7);
						ruta = cfdiPdfService.CrearPdfMarcaSwiss(infoPdf, kfactura, inicioNom+folioFactura, isRetencion, comprobante, bDirFiscal);
						break;
					case 1:// Olab
						inicioNom = "FacturacionElectronica_A";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 8);					
						ruta = cfdiPdfService.CrearPdfMarcaOlabCfdi(infoPdf, kfactura, inicioNom+folioFactura, isDescuento, notaDescuento, comprobante, isRetencion,bDirFiscal);
						break;
					case 4:// Azteca
						inicioNom = "FacturacionElectronica_AZ";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 7);
						ruta = cfdiPdfService.CrearPdfMarcaAzteca(infoPdf, kfactura, inicioNom+folioFactura, isRetencion, comprobante, bDirFiscal);
						break;
					case 7:// Jenner
						inicioNom = "FacturacionElectronica_AJP";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 6);
						ruta = cfdiPdfService.CrearPdfMarcaJenner(infoPdf, kfactura, inicioNom+folioFactura, true, isRetencion, comprobante,bDirFiscal);
						break;
					case 8:// Jenner
						inicioNom = "FacturacionElectronica_AJL";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 6);
						ruta = cfdiPdfService.CrearPdfMarcaJenner(infoPdf, kfactura, inicioNom+folioFactura, false, isRetencion, comprobante,bDirFiscal);
						break;
					case 15:// Liacsa
						inicioNom = "FacturacionElectronica_ASL";
						folioFactura = utilsService.formatoFolio(Integer.parseInt(comprobante.getFolio()), 6);
						ruta = cfdiPdfService.CrearPdfMarcaSwiss(infoPdf, kfactura, inicioNom+folioFactura, isRetencion, comprobante, bDirFiscal);
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
