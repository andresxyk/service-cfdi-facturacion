package com.gda.cfdi.pdf.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;
import com.gda.cfdi.pdf.service.templatev3.ITemplatePdfServiceV3Impl;
import com.gda.cfdi.pdf.service.templatev4.ITemplatePdfServiceV4Impl;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._4.Comprobante;

@Service
public class PdfContadoService {

	private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private ITemplatePdfServiceV4Impl iTemplatePdfServiceV4;
	
	@Autowired
	private ITemplatePdfServiceV3Impl iTemplatePdfServiceV3;
	
	public String generarPdfContadoV40(TFacturaDto facturaDto, Integer cmarca, Integer csucursal) throws DocumentException, IOException, Exception {
		ArrayList<Integer> arrPrado = new ArrayList<Integer>(Arrays.asList(117,118,126,127,128,129,130,132,134,138,139,140,141,142,143,196,198,199,200));
		ArrayList<Integer> arrLean = new ArrayList<Integer>(Arrays.asList(115,116,119,120,121,122,123,124,125,131,133,135,136,137,177,146,197));
		String xml = facturaDto.getXmlTimbrado();
		String ruta = "";
		System.out.println(xml);
		if(facturaDto!=null) {
			if(!xml.isEmpty() && xml.trim().length()>5) {
				Comprobante comprobante = utilsService.createComprobanteFromXml(xml);
				PdfInfoDto infoPDF = new PdfInfoDto();
				infoPDF.setKfactura(facturaDto.getIdFactura());
				infoPDF.setComplementoConcepto(false);
				if(xml.contains("PorCuentadeTerceros")){
					infoPDF.setComplementoConcepto(true);
				}
				log.info("cfdi:ComplementoConcepto:"+infoPDF.getComplementoConcepto());
				String dirSucursal = consultaService.getDireccionSucursalFactura(facturaDto.getIdFactura());
				
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
				}else if (cmarca == 4) {
					centidadlegal = 5;
				}else if (cmarca == 7) {
					if(arrPrado.contains(csucursal)){
						System.out.println("**** Prado *****");
						centidadlegal = 7;				
					}else if(arrLean.contains(csucursal)){
						System.out.println("**** Lean *****");
						centidadlegal = 8;	
					}else {
						centidadlegal = 5;
					}
				}else if (cmarca == 5) {
					centidadlegal = 6;
				}else if (cmarca == 15) {
					centidadlegal = 6;
				}
				
				String dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
				infoPDF.setDirFiscalEmisor(dirEmisorSucursal);				
				List<TOrdenSucursalFacDto> listTosf = consultaService.getTOrdenSucursalFacByKfactura(facturaDto.getIdFactura());
				infoPDF.setConsecutivo(listTosf.get(0).getUorden());
				String nombrepaciente = consultaService.getPacienteFactura(facturaDto.getIdFactura());
				infoPDF.setNombrePaciente(nombrepaciente);
				String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
				infoPDF.setDescripcionMetodoPago(strMetodoPago);
				String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
				infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
				
				infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
				
				
				switch (cmarca) {
					case 1:
						log.info("OLAB*****");
						ruta = iTemplatePdfServiceV4.CrearPdfMarcaOlab(comprobante, infoPDF,env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = iTemplatePdfServiceV4.CrearPdfMarcaAzteca(comprobante, infoPDF,env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = iTemplatePdfServiceV4.CrearPdfMarcaSwiss(comprobante, infoPDF,env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = iTemplatePdfServiceV4.CrearPdfMarcaLiacsa(comprobante, infoPDF,env);
						break;
					case 7:
						log.info("JENNER*****");
						ruta = iTemplatePdfServiceV4.CrearPdfMarcaJenner(comprobante, infoPDF,env);
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
	      file.delete();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		
		return b64;	
	}
	
	public String generarPdfContadoV33(TFacturaDto facturaDto, Integer cmarca, Integer csucursal) throws DocumentException, IOException, Exception {
		ArrayList<Integer> arrPrado = new ArrayList<Integer>(Arrays.asList(117,118,126,127,128,129,130,132,134,138,139,140,141,142,143,196,198,199,200));
		ArrayList<Integer> arrLean = new ArrayList<Integer>(Arrays.asList(115,116,119,120,121,122,123,124,125,131,133,135,136,137,177,146,197));
		String xml = facturaDto.getXmlTimbrado();
		String ruta = "";
		System.out.println(xml);
		if(facturaDto!=null) {
			if(!xml.isEmpty() && xml.trim().length()>5) {
				mx.gob.sat.cfd._3.Comprobante comprobante = utilsService.createComplementoFromXml(xml);
				PdfInfoDto infoPDF = new PdfInfoDto();
				infoPDF.setKfactura(facturaDto.getIdFactura());
				infoPDF.setComplementoConcepto(false);
				if(xml.contains("PorCuentadeTerceros")){
					infoPDF.setComplementoConcepto(true);
				}
				log.info("cfdi:ComplementoConcepto:"+infoPDF.getComplementoConcepto());
				String dirSucursal = consultaService.getDireccionSucursalFactura(facturaDto.getIdFactura());
				
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
				}else if (cmarca == 4) {
					centidadlegal = 5;
				}else if (cmarca == 7) {
					if(arrPrado.contains(csucursal)){
						System.out.println("**** Prado *****");
						centidadlegal = 7;				
					}else if(arrLean.contains(csucursal)){
						System.out.println("**** Lean *****");
						centidadlegal = 8;	
					}else {
						centidadlegal = 5;
					}
				}else if (cmarca == 5) {
					centidadlegal = 6;
				}else if (cmarca == 15) {
					centidadlegal = 6;
				}
				
				String dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
				infoPDF.setDirFiscalEmisor(dirEmisorSucursal);				
				List<TOrdenSucursalFacDto> listTosf = consultaService.getTOrdenSucursalFacByKfactura(facturaDto.getIdFactura());
				infoPDF.setConsecutivo(listTosf.get(0).getUorden());
				String nombrepaciente = consultaService.getPacienteFactura(facturaDto.getIdFactura());
				infoPDF.setNombrePaciente(nombrepaciente);
				String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
				infoPDF.setDescripcionMetodoPago(strMetodoPago);
				String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
				infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
				
				infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
				
				
				switch (cmarca) {
					case 1:
						log.info("OLAB*****");
						ruta = iTemplatePdfServiceV3.CrearPdfMarcaOlab(comprobante, infoPDF,env);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = iTemplatePdfServiceV3.CrearPdfMarcaAzteca(comprobante, infoPDF,env);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = iTemplatePdfServiceV3.CrearPdfMarcaSwiss(comprobante, infoPDF,env);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = iTemplatePdfServiceV3.CrearPdfMarcaLiacsa(comprobante, infoPDF,env);
						break;
					case 7:
						log.info("JENNER*****");
						ruta = iTemplatePdfServiceV3.CrearPdfMarcaJenner(comprobante, infoPDF,env);
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
	      file.delete();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		
		return b64;	
	}
}
