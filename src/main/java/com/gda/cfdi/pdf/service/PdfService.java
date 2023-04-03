package com.gda.cfdi.pdf.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TNotaCreditoEntityDto;
import com.itextpdf.text.DocumentException;

@Service
public class PdfService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private ConsultaService consultaService;	
	
	@Autowired
	private PdfContadoService contadoService;
	
	@Autowired
	private PdfSerieAOrdenService pdfSerieAOrdenService;
	
	@Autowired 
	private PdfSerieAService pdfSerieAService;
	
	@Autowired
	private PdfSerieBService pdfSerieBService;	
	
	@Autowired
	private PdfNotaCreditoService pdfNotaCreditoService;
	
	@Autowired
	private PdfComplementoPagoService pdfComplementoPagoService;
		
	public String generarPdf(Integer kfactura, Integer cmarca, Integer csucursal) throws DocumentException, IOException, Exception{	
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = contadoService.generarPdfContadoV40(facturaDto, cmarca, csucursal);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = contadoService.generarPdfContadoV33(facturaDto, cmarca, csucursal);
		}		
		return base64;
	}	
	
	public String generarPdfOrden(Integer kfactura,boolean isDescuento, String notaDescuento, boolean isRetencion) throws Exception {
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = pdfSerieAOrdenService.generarPdfOrdenV40(kfactura, isDescuento, notaDescuento, isRetencion);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = pdfSerieAOrdenService.generarPdfOrden(kfactura, isDescuento, notaDescuento, isRetencion);
		}		
		return base64;
	}
	
	public String generarPdfSerieA(Integer kfactura, boolean bRetencion) throws DocumentException, IOException, Exception {
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = pdfSerieAService.generarPdfV4(kfactura, bRetencion);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = pdfSerieAService.generarPdf(kfactura, bRetencion);
		}		
		return base64;
	}
	
	public String generarPdfSerieB(Integer kfactura, boolean bRetencion) throws DocumentException, IOException, Exception {
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = pdfSerieBService.generarPdfV4(kfactura, bRetencion);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = pdfSerieBService.generarPdf(kfactura, bRetencion);
		}		
		return base64;
	}
	
	public String generarPdfNotaCredito(Integer ufoliofactura, boolean bRetencion) throws DocumentException, IOException, Exception {
		TNotaCreditoEntityDto creditoEntityDto = consultaService.obtenerNotaCredito(ufoliofactura);
		String xml = creditoEntityDto.getSxmlsello();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = pdfNotaCreditoService.generarPdfV4(ufoliofactura, bRetencion);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = pdfNotaCreditoService.generarPdf(ufoliofactura, bRetencion);
		}		
		return base64;
	}
	
	public String generarPdfComplementoPago(Integer kfactura, Boolean bReturnBase64) throws Exception {
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String xml = facturaDto.getXmlTimbrado();
		String base64 = "";
		if(xml.contains("Version=\"4.0\"")) {
			base64 = pdfComplementoPagoService.generarPdfOrdenV4(kfactura, bReturnBase64);
		}else if(xml.contains("Version=\"3.3\"")){
			base64 = pdfComplementoPagoService.generarPdfOrden(kfactura, bReturnBase64);
		}		
		return base64;
	}

}
