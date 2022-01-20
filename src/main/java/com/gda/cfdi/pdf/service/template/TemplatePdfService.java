package com.gda.cfdi.pdf.service.template;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.utils.ConvertirMontosConLetra;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import mx.gob.sat.cfd._3.Comprobante;


@Service
public class TemplatePdfService implements ITemplatePdfServiceImpl {
	
	private static final Logger log = LoggerFactory.getLogger(TemplatePdfService.class);
	
	@Override
	public String CrearPdfMarcaOlab(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException, Exception{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerOlab = null;
//		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");
		
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraBlanco);
		
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#FF4E00");
		
		Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraEncabezados);
		
		
		
		String strMontoSubTotal = comprobante.getSubTotal().toString(); 
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte().toString();
		String strMontoTotal = comprobante.getTotal().toString();
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		
		FileOutputStream ficheroPdf = null;
		try {
			/*rutaproduccion*/ ruta = "/mnt/gda/DesarrolloGDA/documentosTimbrao/pdf/Olab/pdf/OLFA_"+infoPDF.getKfactura()+".pdf";
			/*rutapruebas*/// ruta = "C:/Users/Desarrollo_GDA/documentos Timbrado/pdf/Olab/pdf/OLFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerOlab = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerOlab.setPageEvent(new TemplateOlab(comprobante, infoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String montoTerceros = null;
		int cont1 = 0;
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			if(infConcepto.getDescripcion().equals("Honorario Medico") && infConcepto.getClaveProdServ().equals("85121600")){
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
			}
			cont1++;
		}
		
		System.out.println("PDFTerceros:"+infoPDF.getComplementoConcepto());
		
		System.out.println(montoTerceros);
		
		
		
		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte= new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleDescripcion = new PdfPCell(new Paragraph("Descripción", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnitario = new PdfPCell(new Paragraph("Valor Unitario", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporteParte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtCantidad = new PdfPCell(new Paragraph("1.000", fuenteContenidoImporTab));
		PdfPCell pcTxtUnidad = new PdfPCell(new Paragraph("ACT", fuenteContenidoImporTab));
		PdfPCell pcTxtIdentificacion = new PdfPCell(new Paragraph("001", fuenteContenidoImporTab));
		PdfPCell pcTxtDescripcion = new PdfPCell(new Paragraph("Honorario Medico", fuenteContenidoImporTab));
		PdfPCell pcTxtUnitario = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcTxtImporteParte = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));

		
		
		

		
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante.getFormaPago(),fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
		PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPDF.getDescripcionMetodoPago(),fuenteContenidoImporTab)); 
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
		
		
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		
//		System.out.println(qulqi.showMeTheMoney(strMontoTotal));
//	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(qulqi.showMeTheMoney(strMontoTotal),fuenteContenidoImporTab));
//	    pcMonedaPeso.setPaddingTop(8);
//        pcMonedaPeso.setIndent(8);
	    strMontoTotal = comprobante.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
	    String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcTitleTerceros.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        
        
        
        pcTitleTerceros.setBackgroundColor(colorFondoTituloFact);
        pcTitleVersion.setBackgroundColor(colorFondoTituloFact);
        pcTitleRFC.setBackgroundColor(colorFondoTituloFact);
        pcTitleNombre.setBackgroundColor(colorFondoTituloFact);
        pcTxtVersion.setBackgroundColor(colorFondoContenidoFact);
        pcTxtRFC.setBackgroundColor(colorFondoContenidoFact);
        pcTxtNombre.setBackgroundColor(colorFondoContenidoFact);
        pcTitleImpuestos.setBackgroundColor(colorFondoTituloFact);
		pcTitleImpuesto.setBackgroundColor(colorFondoTituloFact);
		pcTitleTasa.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporte.setBackgroundColor(colorFondoTituloFact);
        pcTxtImpuesto.setBackgroundColor(colorFondoContenidoFact);
        pcTxtTasa.setBackgroundColor(colorFondoContenidoFact);
        pcTxtImporte.setBackgroundColor(colorFondoContenidoFact);
        pcTitleParte.setBackgroundColor(colorFondoTituloFact);
		pcTitleCantidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleIdentificacion.setBackgroundColor(colorFondoTituloFact);
		pcTitleDescripcion.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnitario.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporteParte.setBackgroundColor(colorFondoTituloFact);
		pcTxtCantidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtIdentificacion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtDescripcion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnitario.setBackgroundColor(colorFondoContenidoFact);
		pcTxtImporteParte.setBackgroundColor(colorFondoContenidoFact);


        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcTitleTerceros.setBorder(Rectangle.BOTTOM);
	    pcTitleVersion.setBorder(Rectangle.RIGHT);
	    pcTitleRFC.setBorder(Rectangle.RIGHT);
	    pcTitleNombre.setBorder(Rectangle.UNDEFINED);
	    pcTxtVersion.setBorder(Rectangle.UNDEFINED);
	    pcTxtRFC.setBorder(Rectangle.UNDEFINED);
	    pcTxtNombre.setBorder(Rectangle.UNDEFINED);
	    pcTitleImpuestos.setBorder(Rectangle.BOTTOM);
		pcTitleImpuesto.setBorder(Rectangle.RIGHT);
		pcTitleTasa.setBorder(Rectangle.RIGHT);
		pcTitleImporte.setBorder(Rectangle.UNDEFINED);
	    pcTxtImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcTxtTasa.setBorder(Rectangle.UNDEFINED);
	    pcTxtImporte.setBorder(Rectangle.UNDEFINED);
	    pcTitleParte.setBorder(Rectangle.BOTTOM);
		pcTitleCantidad.setBorder(Rectangle.RIGHT);
		pcTitleUnidad.setBorder(Rectangle.RIGHT);
		pcTitleIdentificacion.setBorder(Rectangle.RIGHT);
		pcTitleDescripcion.setBorder(Rectangle.RIGHT);
		pcTitleUnitario.setBorder(Rectangle.RIGHT);
		pcTitleImporteParte.setBorder(Rectangle.UNDEFINED);		
		pcTxtCantidad.setBorder(Rectangle.BOTTOM);
		pcTxtUnidad.setBorder(Rectangle.BOTTOM);
		pcTxtIdentificacion.setBorder(Rectangle.BOTTOM);
		pcTxtDescripcion.setBorder(Rectangle.BOTTOM);
		pcTxtUnitario.setBorder(Rectangle.BOTTOM);
		pcTxtImporteParte.setBorder(Rectangle.BOTTOM);
		

	    pcTitleTerceros.setBorderColor(colorLetraEncabezados);
	    pcTitleVersion.setBorderColor(colorLetraEncabezados);
	    pcTitleRFC.setBorderColor(colorLetraEncabezados);
	    pcTitleImpuestos.setBorderColor(colorLetraEncabezados);
		pcTitleImpuesto.setBorderColor(colorLetraEncabezados);
		pcTitleTasa.setBorderColor(colorLetraEncabezados);
		pcTitleImporte.setBorderColor(colorLetraEncabezados);
	    pcTitleParte.setBorderColor(colorLetraEncabezados);
		pcTitleCantidad.setBorderColor(colorLetraEncabezados);
		pcTitleUnidad.setBorderColor(colorLetraEncabezados);
		pcTitleIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTitleDescripcion.setBorderColor(colorLetraEncabezados);
		pcTitleUnitario.setBorderColor(colorLetraEncabezados);
		pcTitleImporteParte.setBorderColor(colorLetraEncabezados);
		pcTxtCantidad.setBorderColor(colorLetraEncabezados);
		pcTxtUnidad.setBorderColor(colorLetraEncabezados);
		pcTxtIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTxtDescripcion.setBorderColor(colorLetraEncabezados);
		pcTxtUnitario.setBorderColor(colorLetraEncabezados);
		pcTxtImporteParte.setBorderColor(colorLetraEncabezados);
	    
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcTitleTerceros.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleTerceros.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTitleImpuestos.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleImpuestos.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTxtImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		
	    
	    pcTitleTerceros.setColspan(6);
	    pcTitleNombre.setColspan(4);
	    pcTxtNombre.setColspan(4);
	    pcTitleImpuestos.setColspan(6);
		pcTitleImpuesto.setColspan(2);
		pcTitleTasa.setColspan(2);
		pcTitleImporte.setColspan(2);
	    pcTxtImpuesto.setColspan(2);
	    pcTxtTasa.setColspan(2);
	    pcTxtImporte.setColspan(2);
	    pcTitleParte.setColspan(6);
	    pcMonedaPeso.setColspan(6);
	    pcFormaPago.setColspan(3);
	    pcSubTotal.setColspan(2);
	    pcMetodo.setColspan(3);
	    pcImpuesto.setColspan(2);
	    pcMontoLetra.setColspan(3);
	    pcTotal.setColspan(2);
	    
	    
	    if(infoPDF.getComplementoConcepto()){
		    tabDetalleMontos.addCell(pcTitleTerceros);
		    tabDetalleMontos.addCell(pcTitleVersion);
		    tabDetalleMontos.addCell(pcTitleRFC);
		    tabDetalleMontos.addCell(pcTitleNombre);
		    tabDetalleMontos.addCell(pcTxtVersion);
		    tabDetalleMontos.addCell(pcTxtRFC);
		    tabDetalleMontos.addCell(pcTxtNombre);
		    tabDetalleMontos.addCell(pcTitleImpuestos);
		    tabDetalleMontos.addCell(pcTitleImpuesto);
		    tabDetalleMontos.addCell(pcTitleTasa);
		    tabDetalleMontos.addCell(pcTitleImporte);
		    tabDetalleMontos.addCell(pcTxtImpuesto);
		    tabDetalleMontos.addCell(pcTxtTasa);
		    tabDetalleMontos.addCell(pcTxtImporte);
		    tabDetalleMontos.addCell(pcTitleParte);
		    tabDetalleMontos.addCell(pcTitleCantidad);
		    tabDetalleMontos.addCell(pcTitleUnidad);
		    tabDetalleMontos.addCell(pcTitleIdentificacion);
		    tabDetalleMontos.addCell(pcTitleDescripcion);
		    tabDetalleMontos.addCell(pcTitleUnitario);
		    tabDetalleMontos.addCell(pcTitleImporteParte);	    
		    tabDetalleMontos.addCell(pcTxtCantidad);
		    tabDetalleMontos.addCell(pcTxtUnidad);
		    tabDetalleMontos.addCell(pcTxtIdentificacion);
		    tabDetalleMontos.addCell(pcTxtDescripcion);
		    tabDetalleMontos.addCell(pcTxtUnitario);
		    tabDetalleMontos.addCell(pcTxtImporteParte);
	    }

	    
	    tabDetalleMontos.addCell(pcMonedaPeso);
	    
	    tabDetalleMontos.addCell(pcFormaPago);
	    tabDetalleMontos.addCell(pcSubTotal);
	    tabDetalleMontos.addCell(pcMontoSubTotal);
	    
	    tabDetalleMontos.addCell(pcMetodo);
	    tabDetalleMontos.addCell(pcImpuesto);
	    tabDetalleMontos.addCell(pcMontoImpuesto);
	    
	    tabDetalleMontos.addCell(pcMontoLetra);
	    tabDetalleMontos.addCell(pcTotal);
	    tabDetalleMontos.addCell(pcMontoTotal);
	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		
		
		
		
		
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(infConcepto.getValorUnitario().toString(),fuenteContenidoTab));
	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(),fuenteContenidoImporTab));
	        pcDescripcion.setColspan(7);
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//	        }
//	        else{
	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		
		
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerOlab.getDirectContent();
        	if(infoPDF.getComplementoConcepto()){
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 340f,canvas);
        	}else{        		
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
        	}
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	@Override
	public String CrearPdfMarcaAzteca(Comprobante comprobante, PdfInfoDto infoPDF) throws Exception{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerAzteca = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#005CB9");
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraBlanco);
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		
		String strMontoSubTotal = comprobante.getSubTotal().toString(); 
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte().toString();
		String strMontoTotal = comprobante.getTotal().toString();
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,150);
		FileOutputStream ficheroPdf = null;
		try {
	/*rutaproduccion*/ ruta = "/mnt/gda/DesarrolloGDA/documentosTimbrao/pdf/Azteca/pdf/AZFA_"+infoPDF.getKfactura()+".pdf";
	/*rutapruebas*///	ruta = "C:/Users/Desarrollo_GDA/documentos Timbrado/pdf/Azteca/pdf/AZFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerAzteca = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerAzteca.setPageEvent(new TemplateAzteca(comprobante, infoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String montoTerceros = null;
		int cont1 = 0;
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			if(infConcepto.getDescripcion().equals("Honorario Medico") && infConcepto.getClaveProdServ().equals("85121600")){
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(cont1).getBase().toString();
			}
			cont1++;
		}
			
		System.out.println(montoTerceros);
		
		
		
		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte= new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleDescripcion = new PdfPCell(new Paragraph("Descripción", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnitario = new PdfPCell(new Paragraph("Valor Unitario", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporteParte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtCantidad = new PdfPCell(new Paragraph("1.000", fuenteContenidoImporTab));
		PdfPCell pcTxtUnidad = new PdfPCell(new Paragraph("ACT", fuenteContenidoImporTab));
		PdfPCell pcTxtIdentificacion = new PdfPCell(new Paragraph("001", fuenteContenidoImporTab));
		PdfPCell pcTxtDescripcion = new PdfPCell(new Paragraph("Honorario Medico", fuenteContenidoImporTab));
		PdfPCell pcTxtUnitario = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcTxtImporteParte = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));

		
		
		

		
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante.getFormaPago(),fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
		PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPDF.getDescripcionMetodoPago(),fuenteContenidoImporTab)); 
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
		
		
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		
//		System.out.println(qulqi.showMeTheMoney(strMontoTotal));
//	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(qulqi.showMeTheMoney(strMontoTotal),fuenteContenidoImporTab));
//	    pcMonedaPeso.setPaddingTop(8);
//        pcMonedaPeso.setIndent(8);
	    strMontoTotal = comprobante.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
	    String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcTitleTerceros.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        
        
        
        pcTitleTerceros.setBackgroundColor(colorFondoTituloFact);
        pcTitleVersion.setBackgroundColor(colorFondoTituloFact);
        pcTitleRFC.setBackgroundColor(colorFondoTituloFact);
        pcTitleNombre.setBackgroundColor(colorFondoTituloFact);
        pcTxtVersion.setBackgroundColor(colorFondoContenidoFact);
        pcTxtRFC.setBackgroundColor(colorFondoContenidoFact);
        pcTxtNombre.setBackgroundColor(colorFondoContenidoFact);
        pcTitleImpuestos.setBackgroundColor(colorFondoTituloFact);
		pcTitleImpuesto.setBackgroundColor(colorFondoTituloFact);
		pcTitleTasa.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporte.setBackgroundColor(colorFondoTituloFact);
        pcTxtImpuesto.setBackgroundColor(colorFondoContenidoFact);
        pcTxtTasa.setBackgroundColor(colorFondoContenidoFact);
        pcTxtImporte.setBackgroundColor(colorFondoContenidoFact);
        pcTitleParte.setBackgroundColor(colorFondoTituloFact);
		pcTitleCantidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleIdentificacion.setBackgroundColor(colorFondoTituloFact);
		pcTitleDescripcion.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnitario.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporteParte.setBackgroundColor(colorFondoTituloFact);
		pcTxtCantidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtIdentificacion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtDescripcion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnitario.setBackgroundColor(colorFondoContenidoFact);
		pcTxtImporteParte.setBackgroundColor(colorFondoContenidoFact);


        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcTitleTerceros.setBorder(Rectangle.BOTTOM);
	    pcTitleVersion.setBorder(Rectangle.RIGHT);
	    pcTitleRFC.setBorder(Rectangle.RIGHT);
	    pcTitleNombre.setBorder(Rectangle.UNDEFINED);
	    pcTxtVersion.setBorder(Rectangle.UNDEFINED);
	    pcTxtRFC.setBorder(Rectangle.UNDEFINED);
	    pcTxtNombre.setBorder(Rectangle.UNDEFINED);
	    pcTitleImpuestos.setBorder(Rectangle.BOTTOM);
		pcTitleImpuesto.setBorder(Rectangle.RIGHT);
		pcTitleTasa.setBorder(Rectangle.RIGHT);
		pcTitleImporte.setBorder(Rectangle.UNDEFINED);
	    pcTxtImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcTxtTasa.setBorder(Rectangle.UNDEFINED);
	    pcTxtImporte.setBorder(Rectangle.UNDEFINED);
	    pcTitleParte.setBorder(Rectangle.BOTTOM);
		pcTitleCantidad.setBorder(Rectangle.RIGHT);
		pcTitleUnidad.setBorder(Rectangle.RIGHT);
		pcTitleIdentificacion.setBorder(Rectangle.RIGHT);
		pcTitleDescripcion.setBorder(Rectangle.RIGHT);
		pcTitleUnitario.setBorder(Rectangle.RIGHT);
		pcTitleImporteParte.setBorder(Rectangle.UNDEFINED);		
		pcTxtCantidad.setBorder(Rectangle.BOTTOM);
		pcTxtUnidad.setBorder(Rectangle.BOTTOM);
		pcTxtIdentificacion.setBorder(Rectangle.BOTTOM);
		pcTxtDescripcion.setBorder(Rectangle.BOTTOM);
		pcTxtUnitario.setBorder(Rectangle.BOTTOM);
		pcTxtImporteParte.setBorder(Rectangle.BOTTOM);
		

	    pcTitleTerceros.setBorderColor(colorLetraEncabezados);
	    pcTitleVersion.setBorderColor(colorLetraEncabezados);
	    pcTitleRFC.setBorderColor(colorLetraEncabezados);
	    pcTitleImpuestos.setBorderColor(colorLetraEncabezados);
		pcTitleImpuesto.setBorderColor(colorLetraEncabezados);
		pcTitleTasa.setBorderColor(colorLetraEncabezados);
		pcTitleImporte.setBorderColor(colorLetraEncabezados);
	    pcTitleParte.setBorderColor(colorLetraEncabezados);
		pcTitleCantidad.setBorderColor(colorLetraEncabezados);
		pcTitleUnidad.setBorderColor(colorLetraEncabezados);
		pcTitleIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTitleDescripcion.setBorderColor(colorLetraEncabezados);
		pcTitleUnitario.setBorderColor(colorLetraEncabezados);
		pcTitleImporteParte.setBorderColor(colorLetraEncabezados);		
		pcTxtCantidad.setBorderColor(colorLetraEncabezados);
		pcTxtUnidad.setBorderColor(colorLetraEncabezados);
		pcTxtIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTxtDescripcion.setBorderColor(colorLetraEncabezados);
		pcTxtUnitario.setBorderColor(colorLetraEncabezados);
		pcTxtImporteParte.setBorderColor(colorLetraEncabezados);
	    
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcTitleTerceros.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleTerceros.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTitleImpuestos.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleImpuestos.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTxtImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		
	    
	    pcTitleTerceros.setColspan(6);
	    pcTitleNombre.setColspan(4);
	    pcTxtNombre.setColspan(4);
	    pcTitleImpuestos.setColspan(6);
		pcTitleImpuesto.setColspan(2);
		pcTitleTasa.setColspan(2);
		pcTitleImporte.setColspan(2);
	    pcTxtImpuesto.setColspan(2);
	    pcTxtTasa.setColspan(2);
	    pcTxtImporte.setColspan(2);
	    pcTitleParte.setColspan(6);
	    pcMonedaPeso.setColspan(6);
	    pcFormaPago.setColspan(3);
	    pcSubTotal.setColspan(2);
	    pcMetodo.setColspan(3);
	    pcImpuesto.setColspan(2);
	    pcMontoLetra.setColspan(3);
	    pcTotal.setColspan(2);
	    
	    
	    if(infoPDF.getComplementoConcepto()){
		    tabDetalleMontos.addCell(pcTitleTerceros);
		    tabDetalleMontos.addCell(pcTitleVersion);
		    tabDetalleMontos.addCell(pcTitleRFC);
		    tabDetalleMontos.addCell(pcTitleNombre);
		    tabDetalleMontos.addCell(pcTxtVersion);
		    tabDetalleMontos.addCell(pcTxtRFC);
		    tabDetalleMontos.addCell(pcTxtNombre);
		    tabDetalleMontos.addCell(pcTitleImpuestos);
		    tabDetalleMontos.addCell(pcTitleImpuesto);
		    tabDetalleMontos.addCell(pcTitleTasa);
		    tabDetalleMontos.addCell(pcTitleImporte);
		    tabDetalleMontos.addCell(pcTxtImpuesto);
		    tabDetalleMontos.addCell(pcTxtTasa);
		    tabDetalleMontos.addCell(pcTxtImporte);
		    tabDetalleMontos.addCell(pcTitleParte);
		    tabDetalleMontos.addCell(pcTitleCantidad);
		    tabDetalleMontos.addCell(pcTitleUnidad);
		    tabDetalleMontos.addCell(pcTitleIdentificacion);
		    tabDetalleMontos.addCell(pcTitleDescripcion);
		    tabDetalleMontos.addCell(pcTitleUnitario);
		    tabDetalleMontos.addCell(pcTitleImporteParte);	    
		    tabDetalleMontos.addCell(pcTxtCantidad);
		    tabDetalleMontos.addCell(pcTxtUnidad);
		    tabDetalleMontos.addCell(pcTxtIdentificacion);
		    tabDetalleMontos.addCell(pcTxtDescripcion);
		    tabDetalleMontos.addCell(pcTxtUnitario);
		    tabDetalleMontos.addCell(pcTxtImporteParte);
	    }

	    
	    tabDetalleMontos.addCell(pcMonedaPeso);
	    
	    tabDetalleMontos.addCell(pcFormaPago);
	    tabDetalleMontos.addCell(pcSubTotal);
	    tabDetalleMontos.addCell(pcMontoSubTotal);
	    
	    tabDetalleMontos.addCell(pcMetodo);
	    tabDetalleMontos.addCell(pcImpuesto);
	    tabDetalleMontos.addCell(pcMontoImpuesto);
	    
	    tabDetalleMontos.addCell(pcMontoLetra);
	    tabDetalleMontos.addCell(pcTotal);
	    tabDetalleMontos.addCell(pcMontoTotal);
	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		
		
		
		
		
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(infConcepto.getValorUnitario().toString(),fuenteContenidoTab));
	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(),fuenteContenidoImporTab));
	        pcDescripcion.setColspan(7);
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//	        }
//	        else{
	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		
		
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerAzteca.getDirectContent();
        	if(infoPDF.getComplementoConcepto()){
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 340f,canvas);
        	}else{        		
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
        	}
		}
		reporteAzteca.close();
		
		
		return ruta;
	}
	
	
	@Override
	public String CrearPdfMarcaSwiss(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");
		
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraBlanco);
		
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		
		String strMontoSubTotal = comprobante.getSubTotal().toString(); 
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte().toString();
		String strMontoTotal = comprobante.getTotal().toString();
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {
			/*rutaproduccion*/ ruta = "/mnt/gda/DesarrolloGDA/documentosTimbrao/pdf/Swisslab/pdf/SWFA_"+infoPDF.getKfactura()+".pdf";
			/*rutapruebas*/// ruta = "C:/Users/Desarrollo_GDA/documentos Timbrado/pdf/Olab/pdf/OLFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateSwiss(comprobante, infoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String montoTerceros = null;
		int cont1 = 0;
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			if(infConcepto.getDescripcion().equals("Honorario Medico") && infConcepto.getClaveProdServ().equals("85121600")){
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(cont1).getBase().toString();
			}
			cont1++;
		}
		
		System.out.println("PDFTerceros:"+infoPDF.getComplementoConcepto());
		
		System.out.println(montoTerceros);
		
		
		
		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte= new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleDescripcion = new PdfPCell(new Paragraph("Descripción", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnitario = new PdfPCell(new Paragraph("Valor Unitario", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporteParte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtCantidad = new PdfPCell(new Paragraph("1.000", fuenteContenidoImporTab));
		PdfPCell pcTxtUnidad = new PdfPCell(new Paragraph("ACT", fuenteContenidoImporTab));
		PdfPCell pcTxtIdentificacion = new PdfPCell(new Paragraph("001", fuenteContenidoImporTab));
		PdfPCell pcTxtDescripcion = new PdfPCell(new Paragraph("Honorario Medico", fuenteContenidoImporTab));
		PdfPCell pcTxtUnitario = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcTxtImporteParte = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));

		
		
		

		
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante.getFormaPago(),fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
		PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPDF.getDescripcionMetodoPago(),fuenteContenidoImporTab)); 
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
		
		
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		
//		System.out.println(qulqi.showMeTheMoney(strMontoTotal));
//	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(qulqi.showMeTheMoney(strMontoTotal),fuenteContenidoImporTab));
//	    pcMonedaPeso.setPaddingTop(8);
//        pcMonedaPeso.setIndent(8);
	    strMontoTotal = comprobante.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
	    String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcTitleTerceros.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        
        
        
        pcTitleTerceros.setBackgroundColor(colorFondoTituloFact);
        pcTitleVersion.setBackgroundColor(colorFondoTituloFact);
        pcTitleRFC.setBackgroundColor(colorFondoTituloFact);
        pcTitleNombre.setBackgroundColor(colorFondoTituloFact);
        pcTxtVersion.setBackgroundColor(colorFondoContenidoFact);
        pcTxtRFC.setBackgroundColor(colorFondoContenidoFact);
        pcTxtNombre.setBackgroundColor(colorFondoContenidoFact);
        pcTitleImpuestos.setBackgroundColor(colorFondoTituloFact);
		pcTitleImpuesto.setBackgroundColor(colorFondoTituloFact);
		pcTitleTasa.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporte.setBackgroundColor(colorFondoTituloFact);
        pcTxtImpuesto.setBackgroundColor(colorFondoContenidoFact);
        pcTxtTasa.setBackgroundColor(colorFondoContenidoFact);
        pcTxtImporte.setBackgroundColor(colorFondoContenidoFact);
        pcTitleParte.setBackgroundColor(colorFondoTituloFact);
		pcTitleCantidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleIdentificacion.setBackgroundColor(colorFondoTituloFact);
		pcTitleDescripcion.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnitario.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporteParte.setBackgroundColor(colorFondoTituloFact);
		pcTxtCantidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtIdentificacion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtDescripcion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnitario.setBackgroundColor(colorFondoContenidoFact);
		pcTxtImporteParte.setBackgroundColor(colorFondoContenidoFact);


        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcTitleTerceros.setBorder(Rectangle.BOTTOM);
	    pcTitleVersion.setBorder(Rectangle.RIGHT);
	    pcTitleRFC.setBorder(Rectangle.RIGHT);
	    pcTitleNombre.setBorder(Rectangle.UNDEFINED);
	    pcTxtVersion.setBorder(Rectangle.UNDEFINED);
	    pcTxtRFC.setBorder(Rectangle.UNDEFINED);
	    pcTxtNombre.setBorder(Rectangle.UNDEFINED);
	    pcTitleImpuestos.setBorder(Rectangle.BOTTOM);
		pcTitleImpuesto.setBorder(Rectangle.RIGHT);
		pcTitleTasa.setBorder(Rectangle.RIGHT);
		pcTitleImporte.setBorder(Rectangle.UNDEFINED);
	    pcTxtImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcTxtTasa.setBorder(Rectangle.UNDEFINED);
	    pcTxtImporte.setBorder(Rectangle.UNDEFINED);
	    pcTitleParte.setBorder(Rectangle.BOTTOM);
		pcTitleCantidad.setBorder(Rectangle.RIGHT);
		pcTitleUnidad.setBorder(Rectangle.RIGHT);
		pcTitleIdentificacion.setBorder(Rectangle.RIGHT);
		pcTitleDescripcion.setBorder(Rectangle.RIGHT);
		pcTitleUnitario.setBorder(Rectangle.RIGHT);
		pcTitleImporteParte.setBorder(Rectangle.UNDEFINED);		
		pcTxtCantidad.setBorder(Rectangle.BOTTOM);
		pcTxtUnidad.setBorder(Rectangle.BOTTOM);
		pcTxtIdentificacion.setBorder(Rectangle.BOTTOM);
		pcTxtDescripcion.setBorder(Rectangle.BOTTOM);
		pcTxtUnitario.setBorder(Rectangle.BOTTOM);
		pcTxtImporteParte.setBorder(Rectangle.BOTTOM);
		

	    pcTitleTerceros.setBorderColor(colorLetraEncabezados);
	    pcTitleVersion.setBorderColor(colorLetraEncabezados);
	    pcTitleRFC.setBorderColor(colorLetraEncabezados);
	    pcTitleImpuestos.setBorderColor(colorLetraEncabezados);
		pcTitleImpuesto.setBorderColor(colorLetraEncabezados);
		pcTitleTasa.setBorderColor(colorLetraEncabezados);
		pcTitleImporte.setBorderColor(colorLetraEncabezados);
	    pcTitleParte.setBorderColor(colorLetraEncabezados);
		pcTitleCantidad.setBorderColor(colorLetraEncabezados);
		pcTitleUnidad.setBorderColor(colorLetraEncabezados);
		pcTitleIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTitleDescripcion.setBorderColor(colorLetraEncabezados);
		pcTitleUnitario.setBorderColor(colorLetraEncabezados);
		pcTitleImporteParte.setBorderColor(colorLetraEncabezados);
		pcTxtCantidad.setBorderColor(colorLetraEncabezados);
		pcTxtUnidad.setBorderColor(colorLetraEncabezados);
		pcTxtIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTxtDescripcion.setBorderColor(colorLetraEncabezados);
		pcTxtUnitario.setBorderColor(colorLetraEncabezados);
		pcTxtImporteParte.setBorderColor(colorLetraEncabezados);
	    
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcTitleTerceros.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleTerceros.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTitleImpuestos.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleImpuestos.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTxtImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		
	    
	    pcTitleTerceros.setColspan(6);
	    pcTitleNombre.setColspan(4);
	    pcTxtNombre.setColspan(4);
	    pcTitleImpuestos.setColspan(6);
		pcTitleImpuesto.setColspan(2);
		pcTitleTasa.setColspan(2);
		pcTitleImporte.setColspan(2);
	    pcTxtImpuesto.setColspan(2);
	    pcTxtTasa.setColspan(2);
	    pcTxtImporte.setColspan(2);
	    pcTitleParte.setColspan(6);
	    pcMonedaPeso.setColspan(6);
	    pcFormaPago.setColspan(3);
	    pcSubTotal.setColspan(2);
	    pcMetodo.setColspan(3);
	    pcImpuesto.setColspan(2);
	    pcMontoLetra.setColspan(3);
	    pcTotal.setColspan(2);
	    
	    
	    if(infoPDF.getComplementoConcepto()){
		    tabDetalleMontos.addCell(pcTitleTerceros);
		    tabDetalleMontos.addCell(pcTitleVersion);
		    tabDetalleMontos.addCell(pcTitleRFC);
		    tabDetalleMontos.addCell(pcTitleNombre);
		    tabDetalleMontos.addCell(pcTxtVersion);
		    tabDetalleMontos.addCell(pcTxtRFC);
		    tabDetalleMontos.addCell(pcTxtNombre);
		    tabDetalleMontos.addCell(pcTitleImpuestos);
		    tabDetalleMontos.addCell(pcTitleImpuesto);
		    tabDetalleMontos.addCell(pcTitleTasa);
		    tabDetalleMontos.addCell(pcTitleImporte);
		    tabDetalleMontos.addCell(pcTxtImpuesto);
		    tabDetalleMontos.addCell(pcTxtTasa);
		    tabDetalleMontos.addCell(pcTxtImporte);
		    tabDetalleMontos.addCell(pcTitleParte);
		    tabDetalleMontos.addCell(pcTitleCantidad);
		    tabDetalleMontos.addCell(pcTitleUnidad);
		    tabDetalleMontos.addCell(pcTitleIdentificacion);
		    tabDetalleMontos.addCell(pcTitleDescripcion);
		    tabDetalleMontos.addCell(pcTitleUnitario);
		    tabDetalleMontos.addCell(pcTitleImporteParte);	    
		    tabDetalleMontos.addCell(pcTxtCantidad);
		    tabDetalleMontos.addCell(pcTxtUnidad);
		    tabDetalleMontos.addCell(pcTxtIdentificacion);
		    tabDetalleMontos.addCell(pcTxtDescripcion);
		    tabDetalleMontos.addCell(pcTxtUnitario);
		    tabDetalleMontos.addCell(pcTxtImporteParte);
	    }

	    
	    tabDetalleMontos.addCell(pcMonedaPeso);
	    
	    tabDetalleMontos.addCell(pcFormaPago);
	    tabDetalleMontos.addCell(pcSubTotal);
	    tabDetalleMontos.addCell(pcMontoSubTotal);
	    
	    tabDetalleMontos.addCell(pcMetodo);
	    tabDetalleMontos.addCell(pcImpuesto);
	    tabDetalleMontos.addCell(pcMontoImpuesto);
	    
	    tabDetalleMontos.addCell(pcMontoLetra);
	    tabDetalleMontos.addCell(pcTotal);
	    tabDetalleMontos.addCell(pcMontoTotal);
	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		
		
		
		
		
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(infConcepto.getValorUnitario().toString(),fuenteContenidoTab));
	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(),fuenteContenidoImporTab));
	        pcDescripcion.setColspan(7);
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//	        }
//	        else{
	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		
		
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerSwiss.getDirectContent();
        	if(infoPDF.getComplementoConcepto()){
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 340f,canvas);
        	}else{        		
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
        	}
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	@Override
	public String CrearPdfMarcaLiacsa(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException{

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");
		
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraBlanco);
		
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		
		String strMontoSubTotal = comprobante.getSubTotal().toString(); 
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte().toString();
		String strMontoTotal = comprobante.getTotal().toString();
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {
			/*rutaproduccion*/ ruta = "/mnt/gda/DesarrolloGDA/documentosTimbrao/pdf/Liacsa/pdf/LIFA_"+infoPDF.getKfactura()+".pdf";
			/*rutapruebas*/// ruta = "C:/Users/Desarrollo_GDA/documentos Timbrado/pdf/Olab/pdf/OLFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateLiacsa(comprobante, infoPDF));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String montoTerceros = null;
		int cont1 = 0;
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			if(infConcepto.getDescripcion().equals("Honorario Medico") && infConcepto.getClaveProdServ().equals("85121600")){
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(cont1).getBase().toString();
			}
			cont1++;
		}
		
		System.out.println("PDFTerceros:"+infoPDF.getComplementoConcepto());
		
		System.out.println(montoTerceros);
		
		
		
		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte= new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleDescripcion = new PdfPCell(new Paragraph("Descripción", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnitario = new PdfPCell(new Paragraph("Valor Unitario", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporteParte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtCantidad = new PdfPCell(new Paragraph("1.000", fuenteContenidoImporTab));
		PdfPCell pcTxtUnidad = new PdfPCell(new Paragraph("ACT", fuenteContenidoImporTab));
		PdfPCell pcTxtIdentificacion = new PdfPCell(new Paragraph("001", fuenteContenidoImporTab));
		PdfPCell pcTxtDescripcion = new PdfPCell(new Paragraph("Honorario Medico", fuenteContenidoImporTab));
		PdfPCell pcTxtUnitario = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcTxtImporteParte = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));

		
		
		

		
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante.getFormaPago(),fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
		PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPDF.getDescripcionMetodoPago(),fuenteContenidoImporTab)); 
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
		
		
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		
//		System.out.println(qulqi.showMeTheMoney(strMontoTotal));
//	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(qulqi.showMeTheMoney(strMontoTotal),fuenteContenidoImporTab));
//	    pcMonedaPeso.setPaddingTop(8);
//        pcMonedaPeso.setIndent(8);
	    strMontoTotal = comprobante.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
	    String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcTitleTerceros.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        
        
        
        pcTitleTerceros.setBackgroundColor(colorFondoTituloFact);
        pcTitleVersion.setBackgroundColor(colorFondoTituloFact);
        pcTitleRFC.setBackgroundColor(colorFondoTituloFact);
        pcTitleNombre.setBackgroundColor(colorFondoTituloFact);
        pcTxtVersion.setBackgroundColor(colorFondoContenidoFact);
        pcTxtRFC.setBackgroundColor(colorFondoContenidoFact);
        pcTxtNombre.setBackgroundColor(colorFondoContenidoFact);
        pcTitleImpuestos.setBackgroundColor(colorFondoTituloFact);
		pcTitleImpuesto.setBackgroundColor(colorFondoTituloFact);
		pcTitleTasa.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporte.setBackgroundColor(colorFondoTituloFact);
        pcTxtImpuesto.setBackgroundColor(colorFondoContenidoFact);
        pcTxtTasa.setBackgroundColor(colorFondoContenidoFact);
        pcTxtImporte.setBackgroundColor(colorFondoContenidoFact);
        pcTitleParte.setBackgroundColor(colorFondoTituloFact);
		pcTitleCantidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleIdentificacion.setBackgroundColor(colorFondoTituloFact);
		pcTitleDescripcion.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnitario.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporteParte.setBackgroundColor(colorFondoTituloFact);
		pcTxtCantidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtIdentificacion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtDescripcion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnitario.setBackgroundColor(colorFondoContenidoFact);
		pcTxtImporteParte.setBackgroundColor(colorFondoContenidoFact);


        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcTitleTerceros.setBorder(Rectangle.BOTTOM);
	    pcTitleVersion.setBorder(Rectangle.RIGHT);
	    pcTitleRFC.setBorder(Rectangle.RIGHT);
	    pcTitleNombre.setBorder(Rectangle.UNDEFINED);
	    pcTxtVersion.setBorder(Rectangle.UNDEFINED);
	    pcTxtRFC.setBorder(Rectangle.UNDEFINED);
	    pcTxtNombre.setBorder(Rectangle.UNDEFINED);
	    pcTitleImpuestos.setBorder(Rectangle.BOTTOM);
		pcTitleImpuesto.setBorder(Rectangle.RIGHT);
		pcTitleTasa.setBorder(Rectangle.RIGHT);
		pcTitleImporte.setBorder(Rectangle.UNDEFINED);
	    pcTxtImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcTxtTasa.setBorder(Rectangle.UNDEFINED);
	    pcTxtImporte.setBorder(Rectangle.UNDEFINED);
	    pcTitleParte.setBorder(Rectangle.BOTTOM);
		pcTitleCantidad.setBorder(Rectangle.RIGHT);
		pcTitleUnidad.setBorder(Rectangle.RIGHT);
		pcTitleIdentificacion.setBorder(Rectangle.RIGHT);
		pcTitleDescripcion.setBorder(Rectangle.RIGHT);
		pcTitleUnitario.setBorder(Rectangle.RIGHT);
		pcTitleImporteParte.setBorder(Rectangle.UNDEFINED);		
		pcTxtCantidad.setBorder(Rectangle.BOTTOM);
		pcTxtUnidad.setBorder(Rectangle.BOTTOM);
		pcTxtIdentificacion.setBorder(Rectangle.BOTTOM);
		pcTxtDescripcion.setBorder(Rectangle.BOTTOM);
		pcTxtUnitario.setBorder(Rectangle.BOTTOM);
		pcTxtImporteParte.setBorder(Rectangle.BOTTOM);
		

	    pcTitleTerceros.setBorderColor(colorLetraEncabezados);
	    pcTitleVersion.setBorderColor(colorLetraEncabezados);
	    pcTitleRFC.setBorderColor(colorLetraEncabezados);
	    pcTitleImpuestos.setBorderColor(colorLetraEncabezados);
		pcTitleImpuesto.setBorderColor(colorLetraEncabezados);
		pcTitleTasa.setBorderColor(colorLetraEncabezados);
		pcTitleImporte.setBorderColor(colorLetraEncabezados);
	    pcTitleParte.setBorderColor(colorLetraEncabezados);
		pcTitleCantidad.setBorderColor(colorLetraEncabezados);
		pcTitleUnidad.setBorderColor(colorLetraEncabezados);
		pcTitleIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTitleDescripcion.setBorderColor(colorLetraEncabezados);
		pcTitleUnitario.setBorderColor(colorLetraEncabezados);
		pcTitleImporteParte.setBorderColor(colorLetraEncabezados);
		pcTxtCantidad.setBorderColor(colorLetraEncabezados);
		pcTxtUnidad.setBorderColor(colorLetraEncabezados);
		pcTxtIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTxtDescripcion.setBorderColor(colorLetraEncabezados);
		pcTxtUnitario.setBorderColor(colorLetraEncabezados);
		pcTxtImporteParte.setBorderColor(colorLetraEncabezados);
	    
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcTitleTerceros.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleTerceros.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTitleImpuestos.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleImpuestos.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTxtImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		
	    
	    pcTitleTerceros.setColspan(6);
	    pcTitleNombre.setColspan(4);
	    pcTxtNombre.setColspan(4);
	    pcTitleImpuestos.setColspan(6);
		pcTitleImpuesto.setColspan(2);
		pcTitleTasa.setColspan(2);
		pcTitleImporte.setColspan(2);
	    pcTxtImpuesto.setColspan(2);
	    pcTxtTasa.setColspan(2);
	    pcTxtImporte.setColspan(2);
	    pcTitleParte.setColspan(6);
	    pcMonedaPeso.setColspan(6);
	    pcFormaPago.setColspan(3);
	    pcSubTotal.setColspan(2);
	    pcMetodo.setColspan(3);
	    pcImpuesto.setColspan(2);
	    pcMontoLetra.setColspan(3);
	    pcTotal.setColspan(2);
	    
	    
	    if(infoPDF.getComplementoConcepto()){
		    tabDetalleMontos.addCell(pcTitleTerceros);
		    tabDetalleMontos.addCell(pcTitleVersion);
		    tabDetalleMontos.addCell(pcTitleRFC);
		    tabDetalleMontos.addCell(pcTitleNombre);
		    tabDetalleMontos.addCell(pcTxtVersion);
		    tabDetalleMontos.addCell(pcTxtRFC);
		    tabDetalleMontos.addCell(pcTxtNombre);
		    tabDetalleMontos.addCell(pcTitleImpuestos);
		    tabDetalleMontos.addCell(pcTitleImpuesto);
		    tabDetalleMontos.addCell(pcTitleTasa);
		    tabDetalleMontos.addCell(pcTitleImporte);
		    tabDetalleMontos.addCell(pcTxtImpuesto);
		    tabDetalleMontos.addCell(pcTxtTasa);
		    tabDetalleMontos.addCell(pcTxtImporte);
		    tabDetalleMontos.addCell(pcTitleParte);
		    tabDetalleMontos.addCell(pcTitleCantidad);
		    tabDetalleMontos.addCell(pcTitleUnidad);
		    tabDetalleMontos.addCell(pcTitleIdentificacion);
		    tabDetalleMontos.addCell(pcTitleDescripcion);
		    tabDetalleMontos.addCell(pcTitleUnitario);
		    tabDetalleMontos.addCell(pcTitleImporteParte);	    
		    tabDetalleMontos.addCell(pcTxtCantidad);
		    tabDetalleMontos.addCell(pcTxtUnidad);
		    tabDetalleMontos.addCell(pcTxtIdentificacion);
		    tabDetalleMontos.addCell(pcTxtDescripcion);
		    tabDetalleMontos.addCell(pcTxtUnitario);
		    tabDetalleMontos.addCell(pcTxtImporteParte);
	    }

	    
	    tabDetalleMontos.addCell(pcMonedaPeso);
	    
	    tabDetalleMontos.addCell(pcFormaPago);
	    tabDetalleMontos.addCell(pcSubTotal);
	    tabDetalleMontos.addCell(pcMontoSubTotal);
	    
	    tabDetalleMontos.addCell(pcMetodo);
	    tabDetalleMontos.addCell(pcImpuesto);
	    tabDetalleMontos.addCell(pcMontoImpuesto);
	    
	    tabDetalleMontos.addCell(pcMontoLetra);
	    tabDetalleMontos.addCell(pcTotal);
	    tabDetalleMontos.addCell(pcMontoTotal);
	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		
		
		
		
		
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(infConcepto.getValorUnitario().toString(),fuenteContenidoTab));
	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(),fuenteContenidoImporTab));
	        pcDescripcion.setColspan(7);
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//	        }
//	        else{
	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		
		
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerSwiss.getDirectContent();
        	if(infoPDF.getComplementoConcepto()){
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 340f,canvas);
        	}else{        		
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
        	}
		}
		reporteAzteca.close();
		
		return ruta;
	
	}
	
	@Override
	public String CrearPdfMarcaJenner(Comprobante comprobante, PdfInfoDto infoPDF) throws DocumentException, IOException, Exception  {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerJenner = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");
		
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#0971CE");
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraBlanco);
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		
		String strMontoSubTotal = comprobante.getSubTotal().toString(); 
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte().toString();
		String strMontoTotal = comprobante.getTotal().toString();
		
		
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {
			/*rutaproduccion*/ ruta = "/mnt/gda/DesarrolloGDA/documentosTimbrao/pdf/Jenner/pdf/JEFA_"+infoPDF.getKfactura()+".pdf";
			/*rutapruebas*///ruta = "C:/Users/Desarrollo_GDA/documentos Timbrado/pdf/Jenner/pdf/JEFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerJenner = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			try {
				writerJenner.setPageEvent(new TemplateJenner(comprobante,infoPDF));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String montoTerceros = null;
		int cont1 = 0;
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			if(infConcepto.getDescripcion().equals("Honorario Medico") && infConcepto.getClaveProdServ().equals("85121600")){
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(cont1).getBase().toString();
			}
			cont1++;
		}
		
		System.out.println("PDFTerceros:"+infoPDF.getComplementoConcepto());
		
		System.out.println(montoTerceros);
		
		
		
		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph("SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte= new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleDescripcion = new PdfPCell(new Paragraph("Descripción", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnitario = new PdfPCell(new Paragraph("Valor Unitario", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporteParte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtCantidad = new PdfPCell(new Paragraph("1.000", fuenteContenidoImporTab));
		PdfPCell pcTxtUnidad = new PdfPCell(new Paragraph("ACT", fuenteContenidoImporTab));
		PdfPCell pcTxtIdentificacion = new PdfPCell(new Paragraph("001", fuenteContenidoImporTab));
		PdfPCell pcTxtDescripcion = new PdfPCell(new Paragraph("Honorario Medico", fuenteContenidoImporTab));
		PdfPCell pcTxtUnitario = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcTxtImporteParte = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));

		
		
		

		
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante.getFormaPago(),fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
		PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPDF.getDescripcionMetodoPago(),fuenteContenidoImporTab)); 
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
		
		
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		
//		System.out.println(qulqi.showMeTheMoney(strMontoTotal));
//	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(qulqi.showMeTheMoney(strMontoTotal),fuenteContenidoImporTab));
//	    pcMonedaPeso.setPaddingTop(8);
//        pcMonedaPeso.setIndent(8);
	    strMontoTotal = comprobante.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
	    String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcTitleTerceros.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        
        
        
        pcTitleTerceros.setBackgroundColor(colorFondoTituloFact);
        pcTitleVersion.setBackgroundColor(colorFondoTituloFact);
        pcTitleRFC.setBackgroundColor(colorFondoTituloFact);
        pcTitleNombre.setBackgroundColor(colorFondoTituloFact);
        pcTxtVersion.setBackgroundColor(colorFondoContenidoFact);
        pcTxtRFC.setBackgroundColor(colorFondoContenidoFact);
        pcTxtNombre.setBackgroundColor(colorFondoContenidoFact);
        pcTitleImpuestos.setBackgroundColor(colorFondoTituloFact);
		pcTitleImpuesto.setBackgroundColor(colorFondoTituloFact);
		pcTitleTasa.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporte.setBackgroundColor(colorFondoTituloFact);
        pcTxtImpuesto.setBackgroundColor(colorFondoContenidoFact);
        pcTxtTasa.setBackgroundColor(colorFondoContenidoFact);
        pcTxtImporte.setBackgroundColor(colorFondoContenidoFact);
        pcTitleParte.setBackgroundColor(colorFondoTituloFact);
		pcTitleCantidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnidad.setBackgroundColor(colorFondoTituloFact);
		pcTitleIdentificacion.setBackgroundColor(colorFondoTituloFact);
		pcTitleDescripcion.setBackgroundColor(colorFondoTituloFact);
		pcTitleUnitario.setBackgroundColor(colorFondoTituloFact);
		pcTitleImporteParte.setBackgroundColor(colorFondoTituloFact);
		pcTxtCantidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnidad.setBackgroundColor(colorFondoContenidoFact);
		pcTxtIdentificacion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtDescripcion.setBackgroundColor(colorFondoContenidoFact);
		pcTxtUnitario.setBackgroundColor(colorFondoContenidoFact);
		pcTxtImporteParte.setBackgroundColor(colorFondoContenidoFact);


        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcTitleTerceros.setBorder(Rectangle.BOTTOM);
	    pcTitleVersion.setBorder(Rectangle.RIGHT);
	    pcTitleRFC.setBorder(Rectangle.RIGHT);
	    pcTitleNombre.setBorder(Rectangle.UNDEFINED);
	    pcTxtVersion.setBorder(Rectangle.UNDEFINED);
	    pcTxtRFC.setBorder(Rectangle.UNDEFINED);
	    pcTxtNombre.setBorder(Rectangle.UNDEFINED);
	    pcTitleImpuestos.setBorder(Rectangle.BOTTOM);
		pcTitleImpuesto.setBorder(Rectangle.RIGHT);
		pcTitleTasa.setBorder(Rectangle.RIGHT);
		pcTitleImporte.setBorder(Rectangle.UNDEFINED);
	    pcTxtImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcTxtTasa.setBorder(Rectangle.UNDEFINED);
	    pcTxtImporte.setBorder(Rectangle.UNDEFINED);
	    pcTitleParte.setBorder(Rectangle.BOTTOM);
		pcTitleCantidad.setBorder(Rectangle.RIGHT);
		pcTitleUnidad.setBorder(Rectangle.RIGHT);
		pcTitleIdentificacion.setBorder(Rectangle.RIGHT);
		pcTitleDescripcion.setBorder(Rectangle.RIGHT);
		pcTitleUnitario.setBorder(Rectangle.RIGHT);
		pcTitleImporteParte.setBorder(Rectangle.UNDEFINED);		
		pcTxtCantidad.setBorder(Rectangle.BOTTOM);
		pcTxtUnidad.setBorder(Rectangle.BOTTOM);
		pcTxtIdentificacion.setBorder(Rectangle.BOTTOM);
		pcTxtDescripcion.setBorder(Rectangle.BOTTOM);
		pcTxtUnitario.setBorder(Rectangle.BOTTOM);
		pcTxtImporteParte.setBorder(Rectangle.BOTTOM);
		

	    pcTitleTerceros.setBorderColor(colorLetraEncabezados);
	    pcTitleVersion.setBorderColor(colorLetraEncabezados);
	    pcTitleRFC.setBorderColor(colorLetraEncabezados);
	    pcTitleImpuestos.setBorderColor(colorLetraEncabezados);
		pcTitleImpuesto.setBorderColor(colorLetraEncabezados);
		pcTitleTasa.setBorderColor(colorLetraEncabezados);
		pcTitleImporte.setBorderColor(colorLetraEncabezados);
	    pcTitleParte.setBorderColor(colorLetraEncabezados);
		pcTitleCantidad.setBorderColor(colorLetraEncabezados);
		pcTitleUnidad.setBorderColor(colorLetraEncabezados);
		pcTitleIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTitleDescripcion.setBorderColor(colorLetraEncabezados);
		pcTitleUnitario.setBorderColor(colorLetraEncabezados);
		pcTitleImporteParte.setBorderColor(colorLetraEncabezados);
		pcTxtCantidad.setBorderColor(colorLetraEncabezados);
		pcTxtUnidad.setBorderColor(colorLetraEncabezados);
		pcTxtIdentificacion.setBorderColor(colorLetraEncabezados);
		pcTxtDescripcion.setBorderColor(colorLetraEncabezados);
		pcTxtUnitario.setBorderColor(colorLetraEncabezados);
		pcTxtImporteParte.setBorderColor(colorLetraEncabezados);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcTitleTerceros.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleTerceros.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtVersion.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtRFC.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTitleImpuestos.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleImpuestos.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleTasa.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcTxtImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtTasa.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTxtImporte.setVerticalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pcTitleParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);	    
		pcTitleCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTitleImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcTxtCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtIdentificacion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtDescripcion.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtUnitario.setVerticalAlignment(Element.ALIGN_CENTER);
		pcTxtImporteParte.setVerticalAlignment(Element.ALIGN_CENTER);
		pcSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		
	    
	    pcTitleTerceros.setColspan(6);
	    pcTitleNombre.setColspan(4);
	    pcTxtNombre.setColspan(4);
	    pcTitleImpuestos.setColspan(6);
		pcTitleImpuesto.setColspan(2);
		pcTitleTasa.setColspan(2);
		pcTitleImporte.setColspan(2);
	    pcTxtImpuesto.setColspan(2);
	    pcTxtTasa.setColspan(2);
	    pcTxtImporte.setColspan(2);
	    pcTitleParte.setColspan(6);
	    pcMonedaPeso.setColspan(6);
	    pcFormaPago.setColspan(3);
	    pcSubTotal.setColspan(2);
	    pcMetodo.setColspan(3);
	    pcImpuesto.setColspan(2);
	    pcMontoLetra.setColspan(3);
	    pcTotal.setColspan(2);
	    
	    
	    if(infoPDF.getComplementoConcepto()){
		    tabDetalleMontos.addCell(pcTitleTerceros);
		    tabDetalleMontos.addCell(pcTitleVersion);
		    tabDetalleMontos.addCell(pcTitleRFC);
		    tabDetalleMontos.addCell(pcTitleNombre);
		    tabDetalleMontos.addCell(pcTxtVersion);
		    tabDetalleMontos.addCell(pcTxtRFC);
		    tabDetalleMontos.addCell(pcTxtNombre);
		    tabDetalleMontos.addCell(pcTitleImpuestos);
		    tabDetalleMontos.addCell(pcTitleImpuesto);
		    tabDetalleMontos.addCell(pcTitleTasa);
		    tabDetalleMontos.addCell(pcTitleImporte);
		    tabDetalleMontos.addCell(pcTxtImpuesto);
		    tabDetalleMontos.addCell(pcTxtTasa);
		    tabDetalleMontos.addCell(pcTxtImporte);
		    tabDetalleMontos.addCell(pcTitleParte);
		    tabDetalleMontos.addCell(pcTitleCantidad);
		    tabDetalleMontos.addCell(pcTitleUnidad);
		    tabDetalleMontos.addCell(pcTitleIdentificacion);
		    tabDetalleMontos.addCell(pcTitleDescripcion);
		    tabDetalleMontos.addCell(pcTitleUnitario);
		    tabDetalleMontos.addCell(pcTitleImporteParte);	    
		    tabDetalleMontos.addCell(pcTxtCantidad);
		    tabDetalleMontos.addCell(pcTxtUnidad);
		    tabDetalleMontos.addCell(pcTxtIdentificacion);
		    tabDetalleMontos.addCell(pcTxtDescripcion);
		    tabDetalleMontos.addCell(pcTxtUnitario);
		    tabDetalleMontos.addCell(pcTxtImporteParte);
	    }

	    
	    tabDetalleMontos.addCell(pcMonedaPeso);
	    
	    tabDetalleMontos.addCell(pcFormaPago);
	    tabDetalleMontos.addCell(pcSubTotal);
	    tabDetalleMontos.addCell(pcMontoSubTotal);
	    
	    tabDetalleMontos.addCell(pcMetodo);
	    tabDetalleMontos.addCell(pcImpuesto);
	    tabDetalleMontos.addCell(pcMontoImpuesto);
	    
	    tabDetalleMontos.addCell(pcMontoLetra);
	    tabDetalleMontos.addCell(pcTotal);
	    tabDetalleMontos.addCell(pcMontoTotal);
	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		
		
		
		
		
		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
			
			
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(infConcepto.getValorUnitario().toString(),fuenteContenidoTab));
	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(),fuenteContenidoImporTab));
	        pcDescripcion.setColspan(7);
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//	        }
//	        else{
	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		
		
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerJenner.getDirectContent();
        	if(infoPDF.getComplementoConcepto()){
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 340f,canvas);
        	}else{        		
        		tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
        	}
		}
		reporteAzteca.close();
		
		return ruta;
	
	}
	
	public String montoConLetra(String strMontoTotal) {
		System.out.println("inicia**** "+strMontoTotal);
		String numero;
		ConvertirMontosConLetra numero_letras;
		String res;
		String numeroFinal;
		String parte_decimal;
		numero = String.valueOf(strMontoTotal).replace(".", ",");
		if (numero.indexOf(",") == -1) {
			numero = numero + ",00";
		}
		String Num[] = numero.split(",");
		if (String.valueOf(Num[1]).length() == 1) {
			parte_decimal = Num[1] + "0";
		} else {
			parte_decimal = Num[1];
		}
		numero_letras = new ConvertirMontosConLetra(Integer.parseInt(Num[0]));
		res = numero_letras.convertirLetras(Integer.parseInt(Num[0]));
		numeroFinal =res+ " PESOS " + parte_decimal + "/100 MXN";
		System.out.print("***  " +numeroFinal.toUpperCase());
		System.out.println("\n");
		return numeroFinal.toUpperCase();
	}

}
