package com.gda.cfdi.pdf.service.empresa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.empresa.TimbradoCfdiDto;
import com.gda.cfdi.pdf.service.ConsultaService;
import com.gda.cfdi.pdf.service.UtilsService;
import com.gda.cfdi.pdf.service.complementopagoV4.ComplementoPdfV4Service;
import com.gda.cfdi.pdf.service.templatev4.ITemplatePdfServiceV4Impl;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;

@Service
public class PdfEmpresaService {

	private static final Logger log = LoggerFactory.getLogger(PdfEmpresaService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private ITemplatePdfServiceV4Impl iTemplatePdfServiceV4;
	@Autowired
	private ComplementoPdfV4Service complementoPdfV4Service;
	
	public String generarPdfEmpresa(TimbradoCfdiDto timbradoCfdiDto) throws DocumentException, IOException, Exception {
		String ruta = "";
//		System.out.println(cfdiBean.getXml());
		if(!timbradoCfdiDto.getDocumentoTimbrado().getXml().isEmpty()) {
			byte[] asBytes = Base64.getDecoder().decode(timbradoCfdiDto.getDocumentoTimbrado().getXml());
			String decodedXml = new String(asBytes, "utf-8");
			Comprobante comprobante = utilsService.createComprobanteFromXml(decodedXml);
			if(comprobante.getTipoDeComprobante() == CTipoDeComprobante.I || 
					comprobante.getTipoDeComprobante() == CTipoDeComprobante.E) {
				PdfInfoDto infoPDF = new PdfInfoDto();
				infoPDF.setKfactura(0);
				infoPDF.setComplementoConcepto(false);
				if(decodedXml.contains("PorCuentadeTerceros")){
					infoPDF.setComplementoConcepto(true);
				}
				log.info("cfdi:ComplementoConcepto:"+infoPDF.getComplementoConcepto());
				
				infoPDF.setDirSucursal("");
				
				infoPDF.setDirFiscalEmisor("");
				infoPDF.setConsecutivo(0);
//				if(cfdiBean.getInformacionAdicionalBean()!=null) {
//					infoPDF.setNombrePaciente(cfdiBean.getInformacionAdicionalBean().getNombrepaciente()!=null ? cfdiBean.getInformacionAdicionalBean().getNombrepaciente() : "");
//				}else {
//				}
				infoPDF.setNombrePaciente("");
				String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
				infoPDF.setDescripcionMetodoPago(strMetodoPago);
				String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
				infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
				
				infoPDF.setCadenaOriginal("");
				ruta = iTemplatePdfServiceV4.CrearPdfEmpresaGenerico(comprobante, infoPDF, env);				
			}else if (comprobante.getTipoDeComprobante() == CTipoDeComprobante.P) {
				String inicioNom = "";
				String folioFactura = "";
				inicioNom = "FacturacionElectronica_ACC";
//				folioFactura =utilsService.formatoFolio(comprobante.getFolio(), 6);
				ComplementoDatosDto complementoDato = new ComplementoDatosDto();
				complementoDato.setEmisorSdireccion("");
				complementoDato.setReceptorSdireccion("");
				complementoDato.setsCadenaOriginal("");
				ruta = complementoPdfV4Service.crearPdfMarcaExaktaEmpresa(comprobante, complementoDato, inicioNom + comprobante.getFolio());
			}
		}
		
		String b64 = null;
		try {
	      File file = new File(ruta);
	      byte [] bytes = Files.readAllBytes(file.toPath());

	      b64 = Base64.getEncoder().encodeToString(bytes);
	      file.delete();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		
		return b64;	
	}
	
}
