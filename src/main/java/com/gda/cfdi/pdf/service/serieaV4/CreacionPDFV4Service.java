package com.gda.cfdi.pdf.service.serieaV4;

import java.io.IOException;
import java.net.SocketException;

import org.springframework.core.env.Environment;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._4.Comprobante;

public interface CreacionPDFV4Service {
	
	
	String CrearPdfMarcaJennerLogoAzteca(Comprobante comprobante, Integer kfactura, String nombreArchivo,
			boolean razonsocial) throws DocumentException, IOException, Exception;
		


	String CrearPdfMarcaOlab(PdfInfoDto infoPDF,Integer kfactura,String nobreArchivo, 
			boolean retencion, mx.gob.sat.cfd._4.Comprobante comprobante33, boolean bDirFiscal, Environment env)
			throws DocumentException, IOException, Exception;

	String CrearPdfMarcaAzteca(PdfInfoDto infoPDF,Integer kfactura,String nombreArchivo, 
			boolean retencion, Comprobante comprobante, boolean bDirFiscal, Environment env) throws DocumentException, IOException, Exception;


	String CrearPdfMarcaSwiss(PdfInfoDto infoPDF,Integer kfactura, String nombreArchivo, 
			boolean retencion, mx.gob.sat.cfd._4.Comprobante comprobante33, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;




	String CrearPdfMarcaLiacsa(PdfInfoDto infoPDF,Integer kfactura,String nombreArchivo, 
			boolean retencion, Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;




	String CrearPdfMarcaJenner(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean razonsocial,
			boolean retencion, mx.gob.sat.cfd._4.Comprobante comprobante33, boolean bDirFiscal,Environment env)
			throws DocumentException, IOException, Exception;



	String CrearPdfMarcaFamilyLabsNorte(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaAsesoresSur(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaExakta(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaMoreira(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaPolab(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaBiomedica(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;



	String CrearPdfMarcaPromedic(PdfInfoDto infoPDF, Integer kfactura, String nombreArchivo, boolean retencion,
			Comprobante comprobante, boolean bDirFiscal, Environment env)
			throws DocumentException, SocketException, IOException;
}
