package com.gda.cfdi.pdf.service.templatev3;

import java.io.IOException;

import org.springframework.core.env.Environment;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;

public interface ITemplatePdfServiceV3Impl {

	String CrearPdfMarcaOlab(Comprobante comprobante, PdfInfoDto infoPDF,Environment env)
			throws DocumentException, IOException, Exception;

	String CrearPdfMarcaAzteca(Comprobante comprobante, PdfInfoDto infoPDF,Environment env) throws Exception;

	String CrearPdfMarcaSwiss(Comprobante comprobante, PdfInfoDto infoPDF,Environment env) throws DocumentException, IOException;

	String CrearPdfMarcaLiacsa(Comprobante comprobante, PdfInfoDto infoPDF,Environment env) throws DocumentException, IOException;

	String CrearPdfMarcaJenner(Comprobante comprobante, PdfInfoDto infoPDF,Environment env)
			throws DocumentException, IOException, Exception;

	String CrearPdfMarcaFamilyLabsNorte(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException, Exception;

}
