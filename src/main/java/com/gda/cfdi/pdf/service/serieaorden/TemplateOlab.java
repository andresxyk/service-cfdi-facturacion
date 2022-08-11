package com.gda.cfdi.pdf.service.serieaorden;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.cfdi.CRegimenFiscal;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.cfdi.CTipoDeComprobante;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.cfdi.Comprobante;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.cfdi.Comprobante.Emisor;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.cfdi.Comprobante.Receptor;
//import com.grupodiagnosticoaries.proceso.facturacion.utils.pdf.GenerarQRCode;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.Comprobante.Emisor;
import mx.gob.sat.cfd._3.Comprobante.Receptor;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

public class TemplateOlab extends PdfPageEventHelper{
	private static final Logger log = LoggerFactory.getLogger(CfdiPdfService.class);
	private Image imagenLogo;
	PdfPTable tabDirSuc = new PdfPTable(1);
	PdfPTable tabInfFact = new PdfPTable(2);
	PdfPTable tabDetalleFact = new PdfPTable(2);
	PdfPTable tabDatosFactura = new PdfPTable(7);
	PdfPTable tabPieCFDI = new PdfPTable(2);
	private Image imagenQr;

	public TemplateOlab(PdfInfoDto infoPdf,boolean bDescuento, Comprobante comprobant33, 
			boolean retencion, boolean bDirFiscal, Environment env) throws Exception{
		try {
			if(retencion){
				tabDatosFactura = new PdfPTable(8);
			}
			String strDirSucursal = infoPdf.getDirSucursal();
			String strTipoCompr = "";
			if(comprobant33.getTipoDeComprobante().value().equals("I")){
				strTipoCompr = "Ingreso";
			}
			else if (comprobant33.getTipoDeComprobante().value().equals("E")){
				strTipoCompr = "Egreso";
			}
			else if (comprobant33.getTipoDeComprobante().value().equals("N")){
				strTipoCompr = "Nota de Credito";
			}
			else if (comprobant33.getTipoDeComprobante().value().equals("P")){
				strTipoCompr = "Recepción de Pago";
			}
			char[] caracteres;
			
			TimbreFiscalDigital timbreFiscalDigital = null;
			for(Comprobante.Complemento compTimbre:comprobant33.getComplemento()) {
				for(Object obj : compTimbre.getAny()) {
					log.info("TimbreFiscalDigital.DoctoRelacionado:::" + (obj instanceof TimbreFiscalDigital ? "si" : "no"));
					if(obj instanceof TimbreFiscalDigital) {
						timbreFiscalDigital = (TimbreFiscalDigital) obj;
						break;
					}
				}
			}
			
			
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String strFechaExped = comprobant33.getLugarExpedicion() +" "+parseador.format(comprobant33.getFecha().toGregorianCalendar().getTime());
			String strFechaCertificacion = parseador.format(timbreFiscalDigital.getFechaTimbrado().toGregorianCalendar().getTime());
			String strFolioSerie = comprobant33.getSerie()+""+comprobant33.getFolio();
			Emisor emisor = comprobant33.getEmisor();
			String strNomEmisor = emisor.getNombre();
			Receptor receptor = comprobant33.getReceptor();
			String strNomReceptor = receptor.getNombre();
			String strRFCEmisor = emisor.getRfc();
			String strConcep = infoPdf.getDescripcionUsoCfdi();
			String strRFCReceptor = receptor.getRfc();
			String strRegFiscal = emisor.getRegimenFiscal();
//			caracteres = comprobante.getAdendaDireccion().toCharArray();
//			for (int i = 0; i < comprobante.getAdendaDireccion().length()- 1; i++) {
//			    if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ','){
//			      caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
//			    }
//			    else{
//			    	caracteres[i + 1] = Character.toLowerCase(caracteres[i + 1]);
//			    }
//			}
//			String strDomicFiscalRecep = new String(caracteres);//FALTA DATO
			log.info("direccion:::   "+infoPdf.getDirFiscalEmisor());
			String strDomicFiscalEmis = infoPdf.getDirFiscalEmisor();
//			String strNomOrdPac = comprobante.getConsecutivo()+" "+comprobante.getNombrepaciente();
			String strNomOrdPac ="";
			String strCadenaTimbre = "CADENA ORIGINAL DEL COMPLEMENTO DE CERTIFICACION DIGITAL SAT:"+infoPdf.getCadenaOriginal()+" "
					+ "Sello Digital del SAT: "+timbreFiscalDigital.getSelloSAT()+" CERTIFICADO SAT: "+ timbreFiscalDigital.getNoCertificadoSAT();
			String uuid = timbreFiscalDigital.getUUID();
			String sello = comprobant33.getSello();
			String uuidRelacionado="";
			
			
			if(comprobant33.getCfdiRelacionados() != null){
				log.info("UUIDRelacionadoOlab:::::     "+comprobant33.getCfdiRelacionados().getCfdiRelacionado().get(0).getUUID());
				uuidRelacionado = comprobant33.getCfdiRelacionados().getCfdiRelacionado().get(0).getUUID();
			}
			
			
			String tipoRelacionado = "";
			if(comprobant33.getCfdiRelacionados() != null){
				switch (comprobant33.getCfdiRelacionados().getTipoRelacion()) {
				case "01":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Nota de crédito de los documentos relacionados";
					break;
				case "02":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Nota de débito de los documentos relacionados";
					break;
				case "03":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Devolución de mercanc�a sobre facturas o traslados previos";
					break;
				case "04":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Sustitución de los CFDI previos";
					break;
				case "05":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Traslados de mercancías facturados previamente";
					break;
				case "06":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - Factura generada por los traslados previos";
					break;
				case "07":
					tipoRelacionado = comprobant33.getCfdiRelacionados().getTipoRelacion()+" - CFDI por aplicación de anticipo";
					break;
				default:
					break;
				}
			}
	
			
			BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
			BaseColor colorFondoTituloFact = WebColors.getRGBColor("#FF4E00");
			BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");			
			Font fuenteDirSucur = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteImport = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteImportPie = new Font(Font.FontFamily.HELVETICA,5,Font.BOLD,BaseColor.BLACK);
			Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraEncabezados);
			Font fuenteTituloTabDetalle = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,colorLetraEncabezados);
			Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteTimbrado = new Font(Font.FontFamily.HELVETICA,4,Font.NORMAL,BaseColor.BLACK);
			Font fuenteTimbradoImpor = new Font(Font.FontFamily.HELVETICA,4,Font.BOLD,BaseColor.BLACK);
			//si se requiere hacer pruebas en la local comentar esta
			imagenLogo = Image.getInstance(env.getProperty("path.file.ordenes.logo.olab"));
			
			imagenLogo.setAbsolutePosition(400,730f);           
            imagenLogo.scaleAbsoluteWidth(160f);
            imagenLogo.scaleAbsoluteHeight(75f);             
			///////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////	Direccion del PDF
			///////////////////////////////////////////////////////////////////////////////////////////////            
            PdfPCell direccionSucursal = new PdfPCell(new Paragraph("Aviación Civil 35, Industrial Puerto Aéreo, Venustiano Carranza C.P. 15710 Ciudad de México",fuenteDirSucur));
            direccionSucursal.setBorder(Rectangle.UNDEFINED);
            tabDirSuc.addCell(direccionSucursal);
            tabDirSuc.setTotalWidth(350);            
			///////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////	Detalle Encabezado Version / CFDI
			///////////////////////////////////////////////////////////////////////////////////////////////            
            PdfPCell pcVersion = new PdfPCell(new Paragraph("Versión 3.3", fuenteImport));
            Chunk folioSer = new Chunk("Folio y serie: ",fuenteDirSucur);
            Chunk datoFolioSer = new Chunk(strFolioSerie,fuenteImport);
            Paragraph datosFolioSer = new Paragraph();
            datosFolioSer.add(folioSer);
            datosFolioSer.add(datoFolioSer);
            PdfPCell pcFolioSerie = new PdfPCell(datosFolioSer);
            
            Chunk tipoComp = new Chunk("Tipo de comprobante: ",fuenteDirSucur);            
            Chunk datotipoComp = new Chunk(strTipoCompr,fuenteImport);
            Paragraph datostipoComp = new Paragraph();
            datostipoComp.add(tipoComp);
            datostipoComp.add(datotipoComp);            
            PdfPCell pcTipoComp = new PdfPCell(datostipoComp);
            
            PdfPCell pcLeyendaCFDI = new PdfPCell(new Paragraph("Uso de CFDI",fuenteDirSucur));
            Paragraph paraFechaTimbrado = new Paragraph("Lugar, fecha y hora de expedición: \r\n",fuenteDirSucur);
            Paragraph paraFechaTimbradoII = new Paragraph(strFechaExped,fuenteImport);
            Paragraph parrafoLeyndaTim = new Paragraph();
            parrafoLeyndaTim.add(paraFechaTimbrado);
            parrafoLeyndaTim.add(paraFechaTimbradoII);
            PdfPCell pcLeyendaTimb = new PdfPCell(parrafoLeyndaTim);
//            PdfPCell pcCFDI = new PdfPCell(new Paragraph("G03 Gastos en General",fuenteImport));
            PdfPCell pcCFDI = new PdfPCell(new Paragraph(strConcep,fuenteImport));
            pcVersion.setBorder(Rectangle.RIGHT);
            pcVersion.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcFolioSerie.setBorder(Rectangle.UNDEFINED);
            pcFolioSerie.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcTipoComp.setBorder(Rectangle.RIGHT);
            pcTipoComp.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcLeyendaCFDI.setBorder(Rectangle.UNDEFINED);
            pcLeyendaCFDI.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcLeyendaTimb.setBorder(Rectangle.RIGHT);
            pcLeyendaTimb.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCFDI.setBorder(Rectangle.UNDEFINED);
            pcCFDI.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            pcVersion.setBorderColor(colorFondoTituloFact);
            pcTipoComp.setBorderColor(colorFondoTituloFact);
            pcLeyendaTimb.setBorderColor(colorFondoTituloFact);
            
            tabInfFact.addCell(pcVersion);
            tabInfFact.addCell(pcFolioSerie);
            tabInfFact.addCell(pcTipoComp);
            tabInfFact.addCell(pcLeyendaCFDI);
            tabInfFact.addCell(pcLeyendaTimb);
            tabInfFact.addCell(pcCFDI);
            tabInfFact.setTotalWidth(300);
            
            ///////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////// Encabezado de Detalle de Factura
            ///////////////////////////////////////////////////////////////////////////////////////////////
            PdfPCell pcDatosEmisor = new PdfPCell(new Paragraph("\t DATOS DEL EMISOR", fuenteTituloTab));
            PdfPCell pcDatosReceptor = new PdfPCell(new Paragraph("\t DATOS DEL RECEPTOR",fuenteTituloTab));
            
            PdfPCell pcLineas = new PdfPCell(new Paragraph("\r\n", fuenteTituloTab));
            PdfPCell pcLineasII = new PdfPCell(new Paragraph("\r\n",fuenteTituloTab));
            
            Chunk NomEmisor = new Chunk("Nombre del Emisor: ",fuenteContenidoTab);
            Chunk datoNomEmisor = new Chunk(strNomEmisor,fuenteContenidoImporTab);
            Paragraph datosEmisor = new Paragraph();
            datosEmisor.add(NomEmisor);
            datosEmisor.add(datoNomEmisor);
            PdfPCell pcNomEmisor = new PdfPCell(datosEmisor);
            
            Chunk NomReceptor = new Chunk("Nombre del Receptor: ",fuenteContenidoTab);
            Chunk datoNomReceptor = new Chunk(strNomReceptor,fuenteContenidoImporTab);
            Paragraph datosReceptor = new Paragraph();
            datosReceptor.add(NomReceptor);
            datosReceptor.add(datoNomReceptor);
            datosReceptor.setLeading(30);
            PdfPCell pcNomReceptor = new PdfPCell(datosReceptor);
            
            Chunk RFCEmisor = new Chunk("RFC Emisor: ",fuenteContenidoTab);
            Chunk datoRFCEmisor = new Chunk(strRFCEmisor,fuenteContenidoImporTab);
            Paragraph datosRFCEmisor = new Paragraph();
            datosRFCEmisor.add(RFCEmisor);
            datosRFCEmisor.add(datoRFCEmisor);
            datosRFCEmisor.setLeading(30);
            PdfPCell pcRFCEmisor = new PdfPCell(datosRFCEmisor);
            
            Chunk RFCReceptor = new Chunk("RFC Receptor: ",fuenteContenidoTab);
            Chunk datoRFCReceptor = new Chunk(strRFCReceptor,fuenteContenidoImporTab);
            Paragraph datosRFCReceptor = new Paragraph();
            datosRFCReceptor.add(RFCReceptor);
            datosRFCReceptor.add(datoRFCReceptor);
            datosReceptor.setLeading(30);
            PdfPCell pcRFCReceptor = new PdfPCell(datosRFCReceptor);
            
            Chunk RegimenEmisor = new Chunk("Régimen fiscal: ",fuenteContenidoTab);
            Chunk datoRegimenEmisor = new Chunk(strRegFiscal+" RÉGIMEN GENERAL DE LEY",fuenteContenidoImporTab);
            Paragraph datosRegimenEmisor = new Paragraph();
            datosRegimenEmisor.add(RegimenEmisor);
            datosRegimenEmisor.add(datoRegimenEmisor);
            PdfPCell pcRegimenEmisor = new PdfPCell(datosRegimenEmisor);
            
            log.info("Direccion::::---->>>>>>   "+infoPdf.getAdendaDireccion());
            Chunk DomicilioReceptor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
            Chunk datoDomicilioReceptor = new Chunk(infoPdf.getAdendaDireccion(),fuenteContenidoImporTab);
            Paragraph datosDomicilioReceptor = new Paragraph();
            datosDomicilioReceptor.add(DomicilioReceptor);
            datosDomicilioReceptor.add(datoDomicilioReceptor);
            PdfPCell pcDomicilioReceptor = new PdfPCell(datosDomicilioReceptor);
            
            Chunk DomicilioEmisor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
            Chunk datoDomicilioEmisor = new Chunk(strDomicFiscalEmis.toUpperCase(),fuenteContenidoImporTab);
            Paragraph datosDomicilioEmisor = new Paragraph();
            datosDomicilioEmisor.add(DomicilioEmisor);
            datosDomicilioEmisor.add(datoDomicilioEmisor);            
            PdfPCell pcDomicilioEmisor = new PdfPCell(datosDomicilioEmisor);

            PdfPCell pcNumOrdenPaciente = new PdfPCell(new Paragraph(strNomOrdPac,fuenteContenidoImporTab));
            

            pcNomEmisor.setPaddingLeft(7);
            pcNomReceptor.setPaddingLeft(7);
            pcRFCEmisor.setPaddingLeft(7);
            pcRFCReceptor.setPaddingLeft(7);
            pcRegimenEmisor.setPaddingLeft(7);
            pcDomicilioReceptor.setPaddingLeft(7);
            pcDomicilioEmisor.setPaddingLeft(7);
            pcNumOrdenPaciente.setPaddingLeft(7);
            
            pcDatosReceptor.setBackgroundColor(colorFondoTituloFact);
            pcDatosEmisor.setBackgroundColor(colorFondoTituloFact);
            pcLineas.setBackgroundColor(colorFondoContenidoFact);
            pcLineasII.setBackgroundColor(colorFondoContenidoFact);
            pcNomEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcNomReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcRFCEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcRFCReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcRegimenEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcDomicilioReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcDomicilioEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcNumOrdenPaciente.setBackgroundColor(colorFondoContenidoFact);
            pcDatosReceptor.setBorder(Rectangle.UNDEFINED);
            pcDatosEmisor.setBorder(Rectangle.UNDEFINED);
            pcLineas.setBorder(Rectangle.UNDEFINED);
            pcLineasII.setBorder(Rectangle.UNDEFINED);
            pcNomEmisor.setBorder(Rectangle.UNDEFINED);
            pcNomReceptor.setBorder(Rectangle.UNDEFINED);
            pcRFCEmisor.setBorder(Rectangle.UNDEFINED);
            pcRFCReceptor.setBorder(Rectangle.UNDEFINED);
            pcRegimenEmisor.setBorder(Rectangle.UNDEFINED);
            pcDomicilioReceptor.setBorder(Rectangle.UNDEFINED);
            pcDomicilioEmisor.setBorder(Rectangle.UNDEFINED);
            pcNumOrdenPaciente.setBorder(Rectangle.UNDEFINED);
            tabDetalleFact.addCell(pcDatosEmisor);
            tabDetalleFact.addCell(pcDatosReceptor);
            tabDetalleFact.addCell(pcLineas);
            tabDetalleFact.addCell(pcLineasII);
            tabDetalleFact.addCell(pcNomEmisor);
            tabDetalleFact.addCell(pcNomReceptor);
            tabDetalleFact.addCell(pcRFCEmisor);
            tabDetalleFact.addCell(pcRFCReceptor);
            tabDetalleFact.addCell(pcRegimenEmisor);
            if(bDirFiscal){
            	tabDetalleFact.addCell(pcDomicilioReceptor);
            	tabDetalleFact.addCell(pcDomicilioEmisor);            	
            }
//            tabDetalleFact.addCell(pcNumOrdenPaciente);
            tabDetalleFact.addCell(pcLineas);
            tabDetalleFact.addCell(pcLineasII);
            tabDetalleFact.setTotalWidth(530);
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Encabezado de Detalle de Factura
			///////////////////////////////////////////////////////////////////////////////////////////////
            PdfPCell pcClavePro = new PdfPCell(new Paragraph("\r\n CLAVE \r\n PRODUCTO \r\n / SERVICIO", fuenteTituloTabDetalle));
            PdfPCell pcCodigo = new PdfPCell(new Paragraph("\r\n \r\nCODIGO",fuenteTituloTabDetalle));
            PdfPCell pcCantidad = new PdfPCell(new Paragraph("\r\n \r\nCANTIDAD",fuenteTituloTabDetalle));
            PdfPCell pcClaveUnidad = new PdfPCell(new Paragraph("\r\n CLAVE \r\n UNIDAD",fuenteTituloTabDetalle));
            PdfPCell pcValorUni = new PdfPCell(new Paragraph("\r\n VALOR \r\n UNITARIO",fuenteTituloTabDetalle));
            String etiquetaIvaDescuento = "";
            if(bDescuento){
            	etiquetaIvaDescuento = "\r\n \r\n DESCUENTO";
            }else{
            	etiquetaIvaDescuento = "\r\n \r\n IVA";
            }
            PdfPCell pcDescuento = new PdfPCell(new Paragraph(etiquetaIvaDescuento,fuenteTituloTabDetalle));
            PdfPCell pcRetencion = new PdfPCell(new Paragraph("\r\n RETENCIÓN \r\n   IVA",fuenteTituloTabDetalle));
            PdfPCell pcImporte = new PdfPCell(new Paragraph("\r\n \r\n IMPORTE",fuenteTituloTabDetalle));
            pcClavePro.setBackgroundColor(colorFondoTituloFact);
            pcCodigo.setBackgroundColor(colorFondoTituloFact);
            pcCantidad.setBackgroundColor(colorFondoTituloFact);
            pcClaveUnidad.setBackgroundColor(colorFondoTituloFact);
            pcValorUni.setBackgroundColor(colorFondoTituloFact);
            pcDescuento.setBackgroundColor(colorFondoTituloFact);
            pcRetencion.setBackgroundColor(colorFondoTituloFact);
            pcImporte.setBackgroundColor(colorFondoTituloFact);
            pcClavePro.setMinimumHeight(40);
            pcCodigo.setMinimumHeight(40);
            pcCantidad.setMinimumHeight(40);
            pcClaveUnidad.setMinimumHeight(40);
            pcValorUni.setMinimumHeight(40);
            pcDescuento.setMinimumHeight(40);
            pcRetencion.setMinimumHeight(40);
            pcImporte.setMinimumHeight(40);
			pcClavePro.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setVerticalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setVerticalAlignment(Element.ALIGN_CENTER);
            pcRetencion.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImporte.setVerticalAlignment(Element.ALIGN_CENTER);
            pcClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcRetencion.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcClavePro.setBorder(Rectangle.RIGHT);
            pcCodigo.setBorder(Rectangle.RIGHT);
            pcCantidad.setBorder(Rectangle.RIGHT);
            pcClaveUnidad.setBorder(Rectangle.RIGHT);
            pcValorUni.setBorder(Rectangle.RIGHT);
            pcDescuento.setBorder(Rectangle.RIGHT);
            pcRetencion.setBorder(Rectangle.RIGHT);
            pcImporte.setBorder(Rectangle.UNDEFINED);
            pcClavePro.setBorderColor(colorLetraEncabezados);
            pcCodigo.setBorderColor(colorLetraEncabezados);
            pcCantidad.setBorderColor(colorLetraEncabezados);
            pcClaveUnidad.setBorderColor(colorLetraEncabezados);
            pcValorUni.setBorderColor(colorLetraEncabezados);
            pcDescuento.setBorderColor(colorLetraEncabezados);
            pcRetencion.setBorderColor(colorLetraEncabezados);
            pcImporte.setBorderColor(colorLetraEncabezados);
            tabDatosFactura.addCell(pcClavePro);
            tabDatosFactura.addCell(pcCodigo);
            tabDatosFactura.addCell(pcCantidad);
            tabDatosFactura.addCell(pcClaveUnidad);
            tabDatosFactura.addCell(pcValorUni);
            tabDatosFactura.addCell(pcDescuento);
            if(retencion){
            	tabDatosFactura.addCell(pcRetencion);            	
            }
            tabDatosFactura.addCell(pcImporte);            
            tabDatosFactura.setTotalWidth(530);
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Pie de Pagina 
			///////////////////////////////////////////////////////////////////////////////////////////////
            String imgCrearQr = "?re="+strRFCEmisor+"&rr="+strRFCReceptor+"&tt="+comprobant33.getTotal().toString()+"&id="+uuid;
         	File f = new File(env.getProperty("path.file.ordenes.qr.olab"));
          	GenerarQRCode qrCode = new GenerarQRCode();
          	qrCode.generateQR(f, imgCrearQr, 600, 600);
          
          	imagenQr = Image.getInstance(env.getProperty("path.file.ordenes.qr.olab"));
			imagenQr.setAbsolutePosition(30, 60f);           
			imagenQr.scaleAbsoluteWidth(90f);
			imagenQr.scaleAbsoluteHeight(90f);
            
            PdfPCell pcTituloCFDI = new PdfPCell(new Paragraph(" ",fuenteImportPie));
            pcTituloCFDI.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcTituloCFDI.setColspan(2);
            PdfPCell pcTituloRelacionCFDI = new PdfPCell(new Paragraph(" ",fuenteImportPie));
            pcTituloRelacionCFDI.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell pcTituloCFDIRelacionado = new PdfPCell(new Paragraph("  ",fuenteImportPie));
            pcTituloCFDIRelacionado.setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell pcTimbre = new PdfPCell(new Paragraph(strCadenaTimbre,fuenteTimbrado));
            pcTimbre.setMinimumHeight(35);
            pcTimbre.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcTimbre.setColspan(2);    
            
            //uuidRelacionado
            PdfPCell uuidRelacionados = new PdfPCell(new Paragraph("CFDI Relacionado: "+uuidRelacionado+" Tipo de Relación: "+tipoRelacionado,fuenteTimbradoImpor));
            uuidRelacionados.setHorizontalAlignment(Element.ALIGN_LEFT);
            uuidRelacionados.setColspan(2); 
            
            //tipoRelacionado
//            PdfPCell tipoRelacion = new PdfPCell(new Paragraph("Tipo de Relaci�n: "+tipoRelacionado,fuenteTimbradoImpor));
//            tipoRelacion.setHorizontalAlignment(Element.ALIGN_LEFT);
//            tipoRelacion.setColspan(2); 
            
            PdfPCell pcFolioFiscal = new PdfPCell(new Paragraph("UUID: "+uuid+" Fecha y Hora de Certificación: "+strFechaCertificacion,fuenteTimbradoImpor));
            pcFolioFiscal.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcFolioFiscal.setColspan(2); 
            
            PdfPCell pcNoSerieSAT = new PdfPCell(new Paragraph("Certificado del Sello digital del emisor: "+sello,fuenteTimbradoImpor));
            pcNoSerieSAT.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcNoSerieSAT.setColspan(2);
            
            PdfPCell pcCertEmisor = new PdfPCell(new Paragraph("Certificado del emisor: "+comprobant33.getNoCertificado()+
            		"\t RFC del Proveedor de Certificación: "+timbreFiscalDigital.getRfcProvCertif(),fuenteTimbradoImpor));
            pcCertEmisor.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcCertEmisor.setColspan(2);
            
            PdfPCell pcLeyendaDoc = new PdfPCell(new Paragraph("Este documento es una representación impresa de un CFDI",fuenteTimbradoImpor));
            pcLeyendaDoc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pcLeyendaDoc.setColspan(2);
            pcTituloCFDI.setBorder(Rectangle.UNDEFINED);
            pcTituloRelacionCFDI.setBorder(Rectangle.UNDEFINED);
            uuidRelacionados.setBorder(Rectangle.UNDEFINED);
//            tipoRelacion.setBorder(Rectangle.UNDEFINED);
            pcTituloCFDIRelacionado.setBorder(Rectangle.UNDEFINED);
            pcTimbre.setBorder(Rectangle.UNDEFINED);
            pcFolioFiscal.setBorder(Rectangle.UNDEFINED);
            pcNoSerieSAT.setBorder(Rectangle.UNDEFINED);
            pcLeyendaDoc.setBorder(Rectangle.UNDEFINED);
            pcCertEmisor.setBorder(Rectangle.UNDEFINED);
            pcLeyendaDoc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            tabPieCFDI.setTotalWidth(445);
            //tabPieCFDI.addCell(pcTituloCFDI);
//            tabPieCFDI.addCell(pcTituloRelacionCFDI);
//            tabPieCFDI.addCell(pcTituloCFDIRelacionado);
            tabPieCFDI.addCell(pcTimbre);
            tabPieCFDI.addCell(uuidRelacionados);
//            tabPieCFDI.addCell(tipoRelacion);
            tabPieCFDI.addCell(pcFolioFiscal);
            tabPieCFDI.addCell(pcCertEmisor);
            tabPieCFDI.addCell(pcNoSerieSAT);
            tabPieCFDI.addCell(pcLeyendaDoc);
            System.out.println("#####################################################");
		} catch (BadElementException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onEndPage(PdfWriter writer, Document document){
		try {
			document.add(imagenLogo);
			tabDirSuc.writeSelectedRows(0, -1, 35f, 800f, writer.getDirectContent());
			tabInfFact.writeSelectedRows(0, -1, 40f, 780f, writer.getDirectContent());
			tabDetalleFact.writeSelectedRows(0, -1, 35f, 710f, writer.getDirectContent());
			tabDatosFactura.writeSelectedRows(0, -1, 35f, 625f, writer.getDirectContent());
			document.add(imagenQr);
			tabPieCFDI.writeSelectedRows(0, -1, 120f, 140f, writer.getDirectContent());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}

