package com.gda.cfdi.pdf.service.complementopago;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.service.PdfComplementoPagoService;
import com.gda.cfdi.pdf.utils.GenerarQRCode;
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
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

public class TemplateJennerComplemento  extends PdfPageEventHelper{
	private Image imagenLogo;
	PdfPTable tabDirSuc = new PdfPTable(1);
	PdfPTable tabInfFact = new PdfPTable(2);
	PdfPTable tabDetalleFact = new PdfPTable(2);
	PdfPTable tabDatosFactura = new PdfPTable(7);
	PdfPTable tabDatosComplemento = new PdfPTable(9);
	PdfPTable tabDetalleTotal = new PdfPTable(1);
	PdfPTable tabPieCFDI = new PdfPTable(2);
	private Image imagenQr;
//	float[] medidaCeldas = {0.5f};
	private static final Logger log = LoggerFactory.getLogger(TemplateJennerComplemento.class);
	public TemplateJennerComplemento(Comprobante comprobante, ComplementoDatosDto complementoDatos, Environment env) throws Exception{
		try {
			String strDirSucursal = "comprobante.getDirsucursal()";
			CTipoDeComprobante tipoComprobante = comprobante.getTipoDeComprobante();
			String strTipoCompr = "";
			if(tipoComprobante.value().equals("I")){
				strTipoCompr = "Ingreso";
			}
			else if (tipoComprobante.value().equals("E")){
				strTipoCompr = "Egreso";
			}
			else if (tipoComprobante.value().equals("N")){
				strTipoCompr = "Nota de Credito";
			}
			else if (tipoComprobante.value().equals("P")){
				strTipoCompr = "Recepción de Pago";
			}
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			GregorianCalendar gc = comprobante.getFecha().toGregorianCalendar();
			String strFechaExped = comprobante.getLugarExpedicion() +" "+  parseador.format(gc.getTime());
			String strFechaCertificacion = parseador.format(gc.getTime());
			String strFolioSerie = comprobante.getSerie()+"-"+comprobante.getFolio();
			Emisor emisor = comprobante.getEmisor();
			String strNomEmisor = emisor.getNombre();
			Receptor receptor = comprobante.getReceptor();
			String strNomReceptor = receptor.getNombre();
			String strRFCEmisor = emisor.getRfc();
			String strConcep = "P01 Por Definir";
			String strRFCReceptor = receptor.getRfc();
//			CRegimenFiscal cregimenfiscal = emisor.getRegimenFiscal();
			String strRegFiscal = emisor.getRegimenFiscal();
			//String strDomicFiscalRecep = "FALTA DATO";
			
			log.info("direccion:::   "+"comprobante.getDirfiscalemisor():::listsize()" + comprobante.getComplemento().size());
			TimbreFiscalDigital timbreFiscalDigital = null;
			for(Comprobante.Complemento compTimbre:comprobante.getComplemento()) {
				for(Object obj : compTimbre.getAny()) {
					log.info("TimbreFiscalDigital.DoctoRelacionado:::" + (obj instanceof TimbreFiscalDigital ? "si" : "no"));
					if(obj instanceof TimbreFiscalDigital) {
						timbreFiscalDigital = (TimbreFiscalDigital) obj;
						break;
					}
				}
				

			}
			
//			String strDomicFiscalEmis = comprobante.getEmisor().getR; 
			String strNomOrdPac = "comprobante.getConsecutivo()"+" "+"comprobante.getNombrepaciente()";
			String strCadenaTimbre = "CADENA ORIGINAL DEL COMPLEMENTO DE CERTIFICACION DIGITAL SAT:" + complementoDatos.getsCadenaOriginal() + " "
					+ "Sello Digital del SAT: "+timbreFiscalDigital.getSelloSAT()+" CERTIFICADO SAT: "+ timbreFiscalDigital.getNoCertificadoSAT();

			String uuid = timbreFiscalDigital.getUUID();
			String sello =  timbreFiscalDigital.getSelloSAT();
//			String sello1 = timbreFiscalDigital.getSelloSAT();
			String uuidRelacionado="";
			
			
			
			String tipoRelacionado = "";
			if(comprobante.getCfdiRelacionados() != null){
				if(comprobante.getCfdiRelacionados().getCfdiRelacionado().size()>0){
					log.info("UUIDRelacionadoOlab:::::     "+comprobante.getCfdiRelacionados().getCfdiRelacionado().get(0).getUUID());
					uuidRelacionado = comprobante.getCfdiRelacionados().getCfdiRelacionado().get(0).getUUID();
				}
				switch (comprobante.getCfdiRelacionados().getTipoRelacion()) {
				case "01":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Nota de crédito de los documentos relacionados";
					break;
				case "02":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Nota de débito de los documentos relacionados";
					break;
				case "03":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Devolución de mercancía sobre facturas o traslados previos";
					break;
				case "04":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Sustitución de los CFDI previos";
					break;
				case "05":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Traslados de mercancías facturados previamente";
					break;
				case "06":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - Factura generada por los traslados previos";
					break;
				case "07":
					tipoRelacionado = comprobante.getCfdiRelacionados().getTipoRelacion()+" - CFDI por aplicación de anticipo";
					break;
				default:
					break;
				}
			}

			
			//BaseColor colorLetraEncabezadoImagen = WebColors.getRGBColor("#1F49B6");
			BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
			BaseColor colorFondoTituloFact = WebColors.getRGBColor("#0971CE");
			BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");			
			Font fuenteDirSucur = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteImport = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteImportPie = new Font(Font.FontFamily.HELVETICA,5,Font.BOLD,BaseColor.BLACK);
			Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraEncabezados);
			Font fuenteTituloTabDetalle = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,colorLetraEncabezados);
			Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteTimbrado = new Font(Font.FontFamily.HELVETICA,4,Font.NORMAL,BaseColor.BLACK);
			Font fuenteTimbradoImpor = new Font(Font.FontFamily.HELVETICA,4,Font.BOLD,BaseColor.BLACK);
			
			
			
			/*rutaproduccion*/ imagenLogo = Image.getInstance(env.getProperty("path.file.ordenes.logo.jenner"));
			imagenLogo.setAbsolutePosition(370, 730f);           
            imagenLogo.scaleAbsoluteWidth(200f);
            imagenLogo.scaleAbsoluteHeight(90f);             
			///////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////	Direccion del PDF
			///////////////////////////////////////////////////////////////////////////////////////////////            
            PdfPCell direccionSucursal = new PdfPCell(new Paragraph("Campos Elíseos 345, Polanco, Polanco III Secc, 11560 Ciudad de México, CDMX",fuenteDirSucur));
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
            //PdfPCell pcCFDI = new PdfPCell(new Paragraph(strConcep,fuenteImport));
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
            
            log.info("Direccion::::---->>>>>>   "+"comprobante.getAdendaDireccion()");
            Chunk DomicilioReceptor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
            Chunk datoDomicilioReceptor = new Chunk(complementoDatos.getReceptorSdireccion().toUpperCase(),fuenteContenidoImporTab);
            Paragraph datosDomicilioReceptor = new Paragraph();
            datosDomicilioReceptor.add(DomicilioReceptor);
            datosDomicilioReceptor.add(datoDomicilioReceptor);
            PdfPCell pcDomicilioReceptor = new PdfPCell(datosDomicilioReceptor);
            
            Chunk DomicilioEmisor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
            Chunk datoDomicilioEmisor = new Chunk(complementoDatos.getEmisorSdireccion().toUpperCase(),fuenteContenidoImporTab);
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
            tabDetalleFact.addCell(pcDomicilioReceptor);
            tabDetalleFact.addCell(pcDomicilioEmisor);
            //tabDetalleFact.addCell(pcNumOrdenPaciente);
            tabDetalleFact.addCell(pcLineas);
            tabDetalleFact.addCell(pcLineasII);
            tabDetalleFact.setTotalWidth(530);
            
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Encabezado de Detalle de Factura
			///////////////////////////////////////////////////////////////////////////////////////////////
            
            PdfPCell pcClavePro = new PdfPCell(new Paragraph("\r\n CLAVE \r\n PRODUCTO \r\n / SERVICIO", fuenteTituloTabDetalle));
            PdfPCell pcCodigo = new PdfPCell(new Paragraph("\r\n \r\nDESCRIPCION",fuenteTituloTabDetalle));
            PdfPCell pcCantidad = new PdfPCell(new Paragraph("\r\n \r\nCANTIDAD",fuenteTituloTabDetalle));
            PdfPCell pcClaveUnidad = new PdfPCell(new Paragraph("\r\n CLAVE \r\n UNIDAD",fuenteTituloTabDetalle));
            PdfPCell pcValorUni = new PdfPCell(new Paragraph("\r\n VALOR \r\n UNTARIO",fuenteTituloTabDetalle));
            PdfPCell pcDescuento = new PdfPCell(new Paragraph("\r\n \r\n IVA",fuenteTituloTabDetalle));
            PdfPCell pcImporte = new PdfPCell(new Paragraph("\r\n \r\n IMPORTE",fuenteTituloTabDetalle));
            PdfPCell pcSubtotal = new PdfPCell(new Paragraph("\r\n \r\n SUBTOTAL",fuenteTituloTabDetalle));
            PdfPCell pcImporteTotal = new PdfPCell(new Paragraph("\r\n \r\n IMPORTE TOTAL",fuenteTituloTabDetalle));
            
            pcClavePro.setBackgroundColor(colorFondoTituloFact);
            pcCodigo.setBackgroundColor(colorFondoTituloFact);
            pcCantidad.setBackgroundColor(colorFondoTituloFact);
            pcClaveUnidad.setBackgroundColor(colorFondoTituloFact);
            pcValorUni.setBackgroundColor(colorFondoTituloFact);
            pcDescuento.setBackgroundColor(colorFondoTituloFact);
            pcImporte.setBackgroundColor(colorFondoTituloFact);
            pcSubtotal.setBackgroundColor(colorFondoTituloFact);
            pcImporteTotal.setBackgroundColor(colorFondoTituloFact);
            
            pcClavePro.setMinimumHeight(40);
            pcCodigo.setMinimumHeight(40);
            pcCantidad.setMinimumHeight(40);
            pcClaveUnidad.setMinimumHeight(40);
            pcValorUni.setMinimumHeight(40);
            pcDescuento.setMinimumHeight(40);
            pcImporte.setMinimumHeight(40);
            pcSubtotal.setMinimumHeight(40);
            pcImporteTotal.setMinimumHeight(40);

			pcClavePro.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setVerticalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImporte.setVerticalAlignment(Element.ALIGN_CENTER);
            pcSubtotal.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImporteTotal.setVerticalAlignment(Element.ALIGN_CENTER);
            
            pcClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcSubtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            pcClavePro.setBorder(Rectangle.RIGHT);
            pcCodigo.setBorder(Rectangle.RIGHT);
            pcCantidad.setBorder(Rectangle.RIGHT);
            pcClaveUnidad.setBorder(Rectangle.RIGHT);
            pcValorUni.setBorder(Rectangle.RIGHT);
            pcDescuento.setBorder(Rectangle.RIGHT);
            pcImporte.setBorder(Rectangle.RIGHT);
            pcSubtotal.setBorder(Rectangle.RIGHT);
            pcImporteTotal.setBorder(Rectangle.UNDEFINED);

            pcClavePro.setBorderColor(colorLetraEncabezados);
            pcCodigo.setBorderColor(colorLetraEncabezados);
            pcCantidad.setBorderColor(colorLetraEncabezados);
            pcClaveUnidad.setBorderColor(colorLetraEncabezados);
            pcValorUni.setBorderColor(colorLetraEncabezados);
            pcDescuento.setBorderColor(colorLetraEncabezados);
            pcImporte.setBorderColor(colorLetraEncabezados);
            pcSubtotal.setBorderColor(colorLetraEncabezados);
            pcImporteTotal.setBorderColor(colorLetraEncabezados);
            tabDatosFactura.addCell(pcClavePro);
            tabDatosFactura.addCell(pcCodigo);
            tabDatosFactura.addCell(pcCantidad);
            tabDatosFactura.addCell(pcClaveUnidad);
            tabDatosFactura.addCell(pcValorUni);
            tabDatosFactura.addCell(pcDescuento);
            tabDatosFactura.addCell(pcImporte);            
//            tabDatosFactura.addCell(pcSubtotal);            
//            tabDatosFactura.addCell(pcImporteTotal);            
            tabDatosFactura.setTotalWidth(530);
            
            /*
             * Encabezado Complemento
             * */
            PdfPCell pcTitulo = new PdfPCell(new Paragraph("INFORMACION DEL COMPLEMENTO DE PAGO", fuenteTituloTabDetalle));
            PdfPCell pcIdDocumento = new PdfPCell(new Paragraph("\r\n \r\n ID DOCUMENTO \r\n", fuenteTituloTabDetalle));
            PdfPCell pcSerieFolio = new PdfPCell(new Paragraph("\r\n \r\nSERIE y FOLIO",fuenteTituloTabDetalle));
            PdfPCell pcMoneda = new PdfPCell(new Paragraph("\r\n \r\nMONEDA",fuenteTituloTabDetalle));
            PdfPCell pcMetodoPago = new PdfPCell(new Paragraph("\r\n  \nMETODO DE PAGO",fuenteTituloTabDetalle));
            PdfPCell pcParcialidad = new PdfPCell(new Paragraph("\r\n  \nPARCIALIDAD",fuenteTituloTabDetalle));
            PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph("\r\n \r\n SALDO ANTERIOR",fuenteTituloTabDetalle));
            PdfPCell pcImportePagado = new PdfPCell(new Paragraph("\r\n \r\n IMPORTE PAGADO",fuenteTituloTabDetalle));
            PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph("\r\n \r\n SALDO INSOLUTO",fuenteTituloTabDetalle));
            pcTitulo.setBackgroundColor(colorFondoTituloFact);
            pcIdDocumento.setBackgroundColor(colorFondoTituloFact);
            pcSerieFolio.setBackgroundColor(colorFondoTituloFact);
            pcMoneda.setBackgroundColor(colorFondoTituloFact);
            pcMetodoPago.setBackgroundColor(colorFondoTituloFact);
            pcParcialidad.setBackgroundColor(colorFondoTituloFact);
            pcSaldoAnterior.setBackgroundColor(colorFondoTituloFact);
            pcImportePagado.setBackgroundColor(colorFondoTituloFact);
            pcSaldoInsoluto.setBackgroundColor(colorFondoTituloFact);
            pcTitulo.setMinimumHeight(40);
            pcIdDocumento.setMinimumHeight(40);
            pcIdDocumento.setColspan(2);
            pcSerieFolio.setMinimumHeight(40);
            pcMoneda.setMinimumHeight(40);
            pcMetodoPago.setMinimumHeight(40);
            pcParcialidad.setMinimumHeight(40);
            pcSaldoAnterior.setMinimumHeight(40);
            pcImportePagado.setMinimumHeight(40);
            pcSaldoInsoluto.setMinimumHeight(40);
            pcTitulo.setVerticalAlignment(Element.ALIGN_CENTER);
			pcIdDocumento.setVerticalAlignment(Element.ALIGN_CENTER);
            pcSerieFolio.setVerticalAlignment(Element.ALIGN_CENTER);
            pcMoneda.setVerticalAlignment(Element.ALIGN_CENTER);
            pcMetodoPago.setVerticalAlignment(Element.ALIGN_CENTER);
            pcParcialidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcSaldoAnterior.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImportePagado.setVerticalAlignment(Element.ALIGN_CENTER);
            pcSaldoInsoluto.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImportePagado.setVerticalAlignment(Element.ALIGN_CENTER);
            pcTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcTitulo.setBorder(Rectangle.RIGHT);
            pcIdDocumento.setBorder(Rectangle.RIGHT);
            pcSerieFolio.setBorder(Rectangle.RIGHT);
            pcMoneda.setBorder(Rectangle.RIGHT);
            pcMetodoPago.setBorder(Rectangle.RIGHT);
            pcParcialidad.setBorder(Rectangle.RIGHT);
            pcSaldoAnterior.setBorder(Rectangle.RIGHT);
            pcImportePagado.setBorder(Rectangle.RIGHT);
            pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
            pcTitulo.setBorderColor(colorLetraEncabezados);
            pcTitulo.setColspan(9);
            pcIdDocumento.setBorderColor(colorLetraEncabezados);
            pcSerieFolio.setBorderColor(colorLetraEncabezados);
            pcMoneda.setBorderColor(colorLetraEncabezados);
            pcMetodoPago.setBorderColor(colorLetraEncabezados);
            pcParcialidad.setBorderColor(colorLetraEncabezados);
            pcSaldoAnterior.setBorderColor(colorLetraEncabezados);
            pcImportePagado.setBorderColor(colorLetraEncabezados);
            pcSaldoInsoluto.setBorderColor(colorLetraEncabezados);
            
//            tabDatosComplemento.addCell(pcTitulo);
            tabDatosComplemento.addCell(pcIdDocumento);
            tabDatosComplemento.addCell(pcSerieFolio);
            tabDatosComplemento.addCell(pcMoneda);
            tabDatosComplemento.addCell(pcMetodoPago);
            tabDatosComplemento.addCell(pcParcialidad);
            tabDatosComplemento.addCell(pcSaldoAnterior);
            tabDatosComplemento.addCell(pcImportePagado);            
            tabDatosComplemento.addCell(pcSaldoInsoluto);            
            tabDatosComplemento.setTotalWidth(530);
            
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Pie de Pagina 
			///////////////////////////////////////////////////////////////////////////////////////////////
            String imgCrearQr = "?re="+strRFCEmisor+"&rr="+strRFCReceptor+"&tt="+comprobante.getTotal().toString()+"&id="+uuid;
            /*rutaproduccion*/ File f = new File(env.getProperty("path.file.qr"));
      	GenerarQRCode qrCode = new GenerarQRCode();
      	qrCode.generateQR(f, imgCrearQr, 600, 600);
          
      		/*rutaproduccion*/ imagenQr = Image.getInstance(env.getProperty("path.file.qr"));
			imagenQr.setAbsolutePosition(35, 60f);           
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
//	            PdfPCell tipoRelacion = new PdfPCell(new Paragraph("Tipo de Relaci�n: "+tipoRelacionado,fuenteTimbradoImpor));
//	            tipoRelacion.setHorizontalAlignment(Element.ALIGN_LEFT);
//	            tipoRelacion.setColspan(2); 
	            
	            PdfPCell pcFolioFiscal = new PdfPCell(new Paragraph("UUID: "+uuid+" Fecha y Hora de Certificación: "+strFechaCertificacion,fuenteTimbradoImpor));
	            pcFolioFiscal.setHorizontalAlignment(Element.ALIGN_LEFT);
	            pcFolioFiscal.setColspan(2); 
	            
	            PdfPCell pcNoSerieSAT = new PdfPCell(new Paragraph("Certificado del Sello digital del emisor: "+sello,fuenteTimbradoImpor));
	            pcNoSerieSAT.setHorizontalAlignment(Element.ALIGN_LEFT);
	            pcNoSerieSAT.setColspan(2);
	            
	            PdfPCell pcCertEmisor = new PdfPCell(new Paragraph("Certificado del emisor: "+comprobante.getCertificado()+
	            		"\t RFC del Proveedor de Certificación: "+ "TLE011122SC2",fuenteTimbradoImpor));
	            pcCertEmisor.setHorizontalAlignment(Element.ALIGN_LEFT);
	            pcCertEmisor.setColspan(2);
	            
	            PdfPCell pcLeyendaDoc = new PdfPCell(new Paragraph("Este documento es una representación impresa de un CFDI",fuenteTimbradoImpor));
	            pcLeyendaDoc.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            pcLeyendaDoc.setColspan(2);
	            pcTituloCFDI.setBorder(Rectangle.UNDEFINED);
	            pcTituloRelacionCFDI.setBorder(Rectangle.UNDEFINED);
	            uuidRelacionados.setBorder(Rectangle.UNDEFINED);
//	            tipoRelacion.setBorder(Rectangle.UNDEFINED);
	            pcTituloCFDIRelacionado.setBorder(Rectangle.UNDEFINED);
	            pcTimbre.setBorder(Rectangle.UNDEFINED);
	            pcFolioFiscal.setBorder(Rectangle.UNDEFINED);
	            pcNoSerieSAT.setBorder(Rectangle.UNDEFINED);
	            pcLeyendaDoc.setBorder(Rectangle.UNDEFINED);
	            pcCertEmisor.setBorder(Rectangle.UNDEFINED);
	            pcLeyendaDoc.setHorizontalAlignment(Element.ALIGN_RIGHT);
			
			tabPieCFDI.setTotalWidth(445);
            tabPieCFDI.addCell(pcTituloCFDI);
            tabPieCFDI.addCell(pcTituloRelacionCFDI);
            tabPieCFDI.addCell(pcTituloCFDIRelacionado);
            tabPieCFDI.addCell(pcTimbre);
            tabPieCFDI.addCell(uuidRelacionados);
//            tabPieCFDI.addCell(tipoRelacion);
            tabPieCFDI.addCell(pcFolioFiscal);
            tabPieCFDI.addCell(pcCertEmisor);
            tabPieCFDI.addCell(pcNoSerieSAT);
            tabPieCFDI.addCell(pcLeyendaDoc);
            
            /*
             * Totales
             */
			PdfPCell pcsSubtotal = new PdfPCell(new Paragraph("Subtotal: 0.0" , fuenteContenidoImporTab));   
		    PdfPCell pcImporteTotalFac = new PdfPCell(new Paragraph("Importe Total: 0.0",fuenteContenidoImporTab));
		    					    	        
		    pcsSubtotal.setBackgroundColor(colorFondoContenidoFact);
		    pcImporteTotalFac.setBackgroundColor(colorFondoContenidoFact);
		    
		    pcsSubtotal.setBorder(Rectangle.UNDEFINED);
		    pcImporteTotalFac.setBorder(Rectangle.UNDEFINED);
		    pcsSubtotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    pcImporteTotalFac.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    
//		    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		    pcsSubtotal.setColspan(3);
//		    pcImporteTotalFac.setColspan(3);
            
		    tabDetalleTotal.addCell(pcsSubtotal);
		    tabDetalleTotal.addCell(pcImporteTotalFac);
		    
            tabDetalleTotal.setWidthPercentage(101);
            tabDetalleTotal.setHorizontalAlignment(0);
//            tabDetalleTotal.setWidths(medidaCeldas);
            tabDetalleTotal.setHeaderRows(1);
            tabDetalleTotal.setFooterRows(1);
            tabDetalleTotal.setTotalWidth(530);
            
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
			tabDetalleTotal.writeSelectedRows(0, -1, 35f, 568f,writer.getDirectContent());
			tabDatosComplemento.writeSelectedRows(0, -1, 35f, 537f, writer.getDirectContent());
			document.add(imagenQr);
			tabPieCFDI.writeSelectedRows(0, -1, 120f, 160f, writer.getDirectContent());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}