package com.gda.cfdi.pdf.service.serieaordenV4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.Conceptos.Concepto;


@Service
public class CfdiPdfV4Service {
	private static final Logger log = LoggerFactory.getLogger(CfdiPdfV4Service.class);
	
	@Autowired
	private Environment env;


	public String CrearPdfMarcaOlabCfdi(PdfInfoDto infoPdf,Integer kfactura,String nobreArchivo, boolean bDescuento, 
			String notaDescuento, Comprobante comprobante33, boolean bretencion, boolean bDirFiscal) throws DocumentException, IOException, Exception{
		log.info("CrearPdfMarcaOlab:::::     " +nobreArchivo);
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(bretencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerOlab = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");

		BaseColor colorborde = WebColors.getRGBColor("#FFFFFF");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal());
		String strMontoDescuento = "";
		if(bDescuento){
			strMontoDescuento = df.format(comprobante33.getDescuento());			
		}
		log.info("strMontoSubTotal*******   "+strMontoSubTotal);
		String strMontoImpuesto =  df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		
		String strMontoTotal = df.format(comprobante33.getTotal());
		log.info("strMontoTotal--->>>>>>>>     "+strMontoTotal);
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,156);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.file.ordenes.pdf.olab")+nobreArchivo+".pdf";
			log.info("ruta--->>>   "+ruta);		
			
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerOlab = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerOlab.setPageEvent(new TemplateOlab(infoPdf, bDescuento,comprobante33,bretencion, bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(bretencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcComentario = new PdfPCell(new Paragraph(notaDescuento, fuenteContenidoImporTab));
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcDescuento = new PdfPCell(new Paragraph("Descuento: ", fuenteContenidoImporTab));
		PdfPCell pcMontoDescuento = new PdfPCell(new Paragraph(strMontoDescuento, fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
		PdfPCell pcEspacio = new PdfPCell(new Paragraph("",fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab)); 
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
		strMontoTotal = comprobante33.getTotal().toString();
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    
	    
	    
	    pcComentario.setIndent(8);
	    if(bretencion && bDescuento){
	    	pcSubTotal.setPaddingTop(8);
	    	pcMontoSubTotal.setPaddingTop(8);	
	    }else if(bretencion){
	    	pcImpuRetenido.setPaddingTop(8);
	    	pcMontoRetenido.setPaddingTop(8);	 
	    }else if(bDescuento){
	    	pcSubTotal.setPaddingTop(8);
	    	pcMontoSubTotal.setPaddingTop(8);	    	
	    }
	    
	    
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcEspacio.setIndent(8);
        pcEspacio.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
        pcComentario.setBackgroundColor(colorFondoContenidoFact);	
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);	    
	    pcDescuento.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoDescuento.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    pcEspacio.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcComentario.setBorder(Rectangle.BOTTOM);
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcDescuento.setBorder(Rectangle.UNDEFINED);
	    pcMontoDescuento.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    pcEspacio.setBorder(Rectangle.UNDEFINED);
	    
	    pcComentario.setBorderColor(colorborde);
	    
	    pcMontoDescuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pcEspacio.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    
	    if(bDescuento && bretencion){
	    	pcComentario.setColspan(3);	
	    	tabDetalleMontos.addCell(pcComentario);
	    	
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
		    tabDetalleMontos.addCell(pcDescuento);
		    tabDetalleMontos.addCell(pcMontoDescuento);
		    
		    tabDetalleMontos.addCell(pcMetodo);
		    tabDetalleMontos.addCell(pcImpuesto);
		    tabDetalleMontos.addCell(pcMontoImpuesto);
		    
		    tabDetalleMontos.addCell(pcMontoLetra);
		    tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcEspacio);
		    tabDetalleMontos.addCell(pcTotal);
		    tabDetalleMontos.addCell(pcMontoTotal);
	    }else if(bDescuento){
	    	pcComentario.setColspan(3);	
	    	tabDetalleMontos.addCell(pcComentario);
	    	
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
		    tabDetalleMontos.addCell(pcDescuento);
		    tabDetalleMontos.addCell(pcMontoDescuento);
		    
		    tabDetalleMontos.addCell(pcMetodo);
		    tabDetalleMontos.addCell(pcImpuesto);
		    tabDetalleMontos.addCell(pcMontoImpuesto);
		    
		    tabDetalleMontos.addCell(pcMontoLetra);
		    tabDetalleMontos.addCell(pcTotal);
		    tabDetalleMontos.addCell(pcMontoTotal);	
	    }else if(bretencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	 
	    }else{
	    	pcMonedaPeso.setColspan(3);	
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
	    	
	    }
	    	    
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
//		for(Comprobante.Conceptos.Concepto infConcepto: comprobante.getConceptos().getConcepto()){
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph( df.format(concepto.getValorUnitario()),fuenteContenidoTab));
		    
//		                              concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte();
		    log.info("IVA____>>>>   "+concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte());
		    PdfPCell Descuento = null;
		    if(bDescuento){
		    	Descuento = new PdfPCell(new Paragraph(df.format(concepto.getDescuento()),fuenteContenidoTab));
		    }else{
		    	log.info(""+concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte());
		    	Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte() == null ?
		    			BigDecimal.ZERO : concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
		    			),fuenteContenidoTab));
		    }
	        PdfPCell Importe = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase()),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerOlab.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
		}
		reporteAzteca.close();

		log.info("======================================="+ruta);
		
		return ruta;
	}
	
	
	
	public String CrearPdfMarcaAzteca(PdfInfoDto infoPdf,Integer kfactura,String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, IOException, Exception {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerAzteca = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,150);
		FileOutputStream ficheroPdf = null;
		try {
			ruta =env.getProperty("path.file.ordenes.pdf.azteca")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerAzteca = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerAzteca.setPageEvent(new TemplateAzteca(infoPdf,comprobante33,retencion, bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));
//	    Qulqi qulqi = new Qulqi();
//		qulqi.setDecimalPartVisible(true);
//		qulqi.setCoin(Qulqi$COIN.peso_mexicano);
//		qulqi.setFloating(Qulqi$FLOATING.POINT);
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {		
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));  //(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(df.format(concepto.getValorUnitario()),fuenteContenidoTab));
//	        PdfPCell Descuento = new PdfPCell(new Paragraph("0.0",fuenteContenidoTab));
		    log.info("IVA____>>>>   "+concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte());
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase()),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%18.5) == 0 && cont !=0) {
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
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
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	
	
	
	
	public String CrearPdfMarcaSwiss(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.swisslab")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateSwiss(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
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
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	
	public String CrearPdfMarcaFamilyLabsNorte(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.familylabsnorte")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateFamilyLabsNorte(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
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
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	
	public String CrearPdfMarcaExakta(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerPdf = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.exakta")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateExakta(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		documentReportePdf.open();	
		documentReportePdf.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   documentReportePdf.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerPdf.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
	    documentReportePdf.close();
		
		return ruta;
	}
	
	
	public String CrearPdfMarcaMoreira(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerPdf = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.moreira")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateMoreira(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		documentReportePdf.open();	
		documentReportePdf.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   documentReportePdf.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerPdf.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
	    documentReportePdf.close();
		
		return ruta;
	}
	
	public String CrearPdfMarcaPolab(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerPdf = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.polab")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplatePolab(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		documentReportePdf.open();	
		documentReportePdf.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   documentReportePdf.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerPdf.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
	    documentReportePdf.close();
		
		return ruta;
	}
	
	
	public String CrearPdfMarcaBiomedica(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerPdf = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.referencia")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateBiomedica(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		documentReportePdf.open();	
		documentReportePdf.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   documentReportePdf.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerPdf.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
	    documentReportePdf.close();
		
		return ruta;
	}
	
	public String CrearPdfMarcaPromedic(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerPdf = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.promedic")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplatePromedic(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		documentReportePdf.open();	
		documentReportePdf.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
//	        }
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   documentReportePdf.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerPdf.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
	    documentReportePdf.close();
		
		return ruta;
	}
	
	public String CrearPdfMarcaAsesoresSur(PdfInfoDto infoPdf,Integer kfactura, String nombreArchivo, boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, SocketException, IOException{
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,136);
		FileOutputStream ficheroPdf = null;
		try {			
			ruta = env.getProperty("path.file.ordenes.pdf.asesoressur")+nombreArchivo+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateAsesoresSur(infoPdf,comprobante33,retencion,bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
//	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago  99 Por Definir",fuenteContenidoImporTab));
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    //PdfPCell pcMetodo = new PdfPCell(new Paragraph("M�todo de pago PPD Pago En Parcialidades O Diferido",fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
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
		strMontoTotal = comprobante33.getTotal().toString();
//		System.out.println(Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length()));		
//		String strMontoTotalLetra = Character.toUpperCase(qulqi.showMeTheMoney(strMontoTotal).charAt(0)) + qulqi.showMeTheMoney(strMontoTotal).substring(1,qulqi.showMeTheMoney(strMontoTotal).length());
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));
	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString(),fuenteContenidoTab));
		    
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        //infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
	        PdfPCell Importe = new PdfPCell(new Paragraph(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);

//	        if ((bandera%9) == 0 && cont !=0) {
//	        	tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
//		        tabDatosFactura.addCell(espacioBlanco);
		        
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
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
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
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f,canvas);
		}
		reporteAzteca.close();
		
		return ruta;
	}
	
	
	public String CrearPdfMarcaJenner(PdfInfoDto infoPdf,Integer kfactura,String nombreArchivo, boolean razonsocial, 
			boolean retencion, Comprobante comprobante33, boolean bDirFiscal) throws DocumentException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura  = null;
		if(retencion){
			tabDatosFactura = new PdfPTable(8);
		}else{
			tabDatosFactura = new PdfPTable(7);			
		}
		PdfWriter writerJenner = null;
		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
		
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		String strMontoSubTotal = df.format(comprobante33.getSubTotal()); 
		String strMontoImpuesto = df.format(comprobante33.getImpuestos().getTotalImpuestosTrasladados());
		String strMontoTotal = df.format(comprobante33.getTotal());
		
		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260,156);
		FileOutputStream ficheroPdf = null;
		try {			
			if(razonsocial){
				ruta = env.getProperty("path.file.ordenes.pdf.prado")+nombreArchivo+".pdf";
			}else{
				ruta = env.getProperty("path.file.ordenes.pdf.lean")+nombreArchivo+".pdf";
			}  
			log.info("nobreArchivoJenner::::  "+ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerJenner = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerJenner.setPageEvent(new TemplateJenner(infoPdf,comprobante33,retencion, bDirFiscal,env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		reporteAzteca.open();	
		reporteAzteca.newPage();
		
		String mntRetenido = "";
		if(retencion){
			mntRetenido = df.format(comprobante33.getImpuestos().getTotalImpuestosRetenidos());
		}
		
		PdfPCell pcMonedaPeso = new PdfPCell(new Paragraph("Moneda MXN ", fuenteContenidoImporTab));
		PdfPCell pcImpuRetenido = new PdfPCell(new Paragraph("Impuesto retenido (IVA):",fuenteContenidoImporTab));
		PdfPCell pcMontoRetenido = new PdfPCell(new Paragraph(mntRetenido,fuenteContenidoImporTab));
		
	    PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal,fuenteContenidoImporTab));	    
	    PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago "+comprobante33.getFormaPago(),fuenteContenidoImporTab));
	    PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA)",fuenteContenidoImporTab));
	    PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto,fuenteContenidoImporTab));
	    PdfPCell pcMetodo = new PdfPCell(new Paragraph("Método de pago "+infoPdf.getSmetodoPago(),fuenteContenidoImporTab));
	    PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ",fuenteContenidoImporTab));
	    PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal,fuenteContenidoImporTab));

		strMontoTotal = comprobante33.getTotal().toString();
		String strMontoTotalLetra = montoConLetra(strMontoTotal);
	    PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra,fuenteContenidoImporTab));

	    pcMonedaPeso.setPaddingTop(8);
        pcMonedaPeso.setIndent(8);
        
        if(retencion){
        	pcSubTotal.setPaddingTop(8);
        	pcMontoSubTotal.setPaddingTop(8);
        }
        
        pcFormaPago.setIndent(8);
        
        pcMetodo.setIndent(8);
            
        pcMontoLetra.setIndent(8);
        pcMontoLetra.setPaddingBottom(8);
        pcMontoTotal.setPaddingBottom(8);
        pcTotal.setPaddingBottom(8);
        
	    pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
	    pcSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoRetenido.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoSubTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
	    pcImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoImpuesto.setBackgroundColor(colorFondoContenidoFact);
	    pcMetodo.setBackgroundColor(colorFondoContenidoFact);
	    pcTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
	    pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
	    
	    pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
	    pcSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcImpuRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoRetenido.setBorder(Rectangle.UNDEFINED);
	    pcMontoSubTotal.setBorder(Rectangle.UNDEFINED);
	    pcFormaPago.setBorder(Rectangle.UNDEFINED);
	    pcImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMontoImpuesto.setBorder(Rectangle.UNDEFINED);
	    pcMetodo.setBorder(Rectangle.UNDEFINED);
	    pcTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoTotal.setBorder(Rectangle.UNDEFINED);
	    pcMontoLetra.setBorder(Rectangle.UNDEFINED);
	    
	    pcMontoSubTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoRetenido.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoImpuesto.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    pcMontoLetra.setHorizontalAlignment(Element.ALIGN_LEFT);
	    
	    if(retencion){
	    	tabDetalleMontos.addCell(pcMonedaPeso);
	    	tabDetalleMontos.addCell(pcSubTotal);
	    	tabDetalleMontos.addCell(pcMontoSubTotal);
	    	
	    	tabDetalleMontos.addCell(pcFormaPago);
	    	tabDetalleMontos.addCell(pcImpuesto);
	    	tabDetalleMontos.addCell(pcMontoImpuesto);
	    	
	    	tabDetalleMontos.addCell(pcMetodo);
	    	tabDetalleMontos.addCell(pcImpuRetenido);
	    	tabDetalleMontos.addCell(pcMontoRetenido);
	    	
	    	tabDetalleMontos.addCell(pcMontoLetra);
	    	tabDetalleMontos.addCell(pcTotal);
	    	tabDetalleMontos.addCell(pcMontoTotal);	   
	    }else{
	    	pcMonedaPeso.setColspan(3);
	    	
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
	    }
	    tabDetalleMontos.setWidthPercentage(101);
	    tabDetalleMontos.setHorizontalAlignment(0);
	    tabDetalleMontos.setWidths(medidaCeldas);
	    tabDetalleMontos.setHeaderRows(2);
	    tabDetalleMontos.setFooterRows(2);
	    tabDetalleMontos.setTotalWidth(530);
		
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante33.getConceptos().getConcepto().size();
		
		List<Concepto> listConceptos = comprobante33.getConceptos().getConcepto();
		for (Concepto concepto : listConceptos) {	
			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(concepto.getClaveProdServ(), fuenteContenidoTab));
		    PdfPCell Codigo = new PdfPCell(new Paragraph(concepto.getNoIdentificacion(),fuenteContenidoTab));
		    int intCantidad = new Double(concepto.getCantidad().toString()).intValue();
		    PdfPCell Cantidad = new PdfPCell(new Paragraph( Integer.toString(intCantidad) ,fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(concepto.getClaveUnidad(),fuenteContenidoTab));
		    PdfPCell ValorUni = new PdfPCell(new Paragraph( df.format(concepto.getValorUnitario()),fuenteContenidoTab));
		    log.info("IVA____>>>>   "+concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte());
	        PdfPCell Descuento = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getImporte()),fuenteContenidoTab));
	        PdfPCell Importe = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getTraslados().getTraslado().get(0).getBase()),fuenteContenidoTab));
	        PdfPCell montoRetencion = null;
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion = new PdfPCell(new Paragraph(df.format(concepto.getImpuestos().getRetenciones().getRetencion()
	        			.get(0).getImporte()),fuenteContenidoTab));
	        }
	        ClavePro.setBorder(Rectangle.UNDEFINED);
	        Codigo.setBorder(Rectangle.UNDEFINED);
	        Cantidad.setBorder(Rectangle.UNDEFINED);
	        ClaveUnidad.setBorder(Rectangle.UNDEFINED);
	        ValorUni.setBorder(Rectangle.UNDEFINED);
	        Descuento.setBorder(Rectangle.UNDEFINED);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setBorder(Rectangle.UNDEFINED);
	        }
	        Importe.setBorder(Rectangle.UNDEFINED);
	        espacioBlanco.setBorder(Rectangle.UNDEFINED);
	        ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
	        ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
	        Descuento.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	montoRetencion.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        }
	        Importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        PdfPCell pcDescripcion = new PdfPCell(new Paragraph(concepto.getDescripcion(),fuenteContenidoImporTab));
	        if(concepto.getImpuestos().getRetenciones()!=null){
	        	pcDescripcion.setColspan(8);
	        }else{
	        	pcDescripcion.setColspan(7);	        	
	        }
	        pcDescripcion.setBorder(Rectangle.UNDEFINED);


	        	tabDatosFactura.addCell(ClavePro);
		        tabDatosFactura.addCell(Codigo);
		        tabDatosFactura.addCell(Cantidad);
		        tabDatosFactura.addCell(ClaveUnidad);
		        tabDatosFactura.addCell(ValorUni);
		        tabDatosFactura.addCell(Descuento);
		        if(concepto.getImpuestos().getRetenciones()!=null){
		        	tabDatosFactura.addCell(montoRetencion);
		        }
		        tabDatosFactura.addCell(Importe);
		        tabDatosFactura.addCell(pcDescripcion);
	        cont++;
	        bandera++;
		}
		   tabDatosFactura.setWidthPercentage(101);
		   tabDatosFactura.setHorizontalAlignment(0);
		   reporteAzteca.add(tabDatosFactura);
	    if (tamanioCOncepto == bandera) {
        	PdfContentByte canvas = writerJenner.getDirectContent();
        	tabDetalleMontos.writeSelectedRows(0, -1, 35f, 230f,canvas);
		}
		reporteAzteca.close();

		return ruta;
	}	
	

	public String montoConLetra(String strMontoTotal) {
		log.info("inicia**** " + strMontoTotal);
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
		numeroFinal = res + " PESOS " + parte_decimal + "/100 MXN";
		log.info("***  " + numeroFinal.toUpperCase());
		log.info("\n");
		return numeroFinal.toUpperCase();
	}
}
