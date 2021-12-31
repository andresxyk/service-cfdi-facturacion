package com.gda.cfdi.pdf.service.template;

import java.io.IOException;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

public interface ITemplatePdfServiceImpl {

	String CrearPdfMarcaOlab(Comprobante comprobante, PdfInfoDto infoPDF)
			throws DocumentException, IOException, Exception;

	String CrearPdfMarcaAzteca(Comprobante comprobante, PdfInfoDto infoPDF) throws Exception;

	String CrearPdfMarcaSwiss(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException;

	String CrearPdfMarcaLiacsa(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException;

	String CrearPdfMarcaJenner(Comprobante comprobante, PdfInfoDto infoPDF)
			throws DocumentException, IOException, Exception;

}
