package com.gda.cfdi.pdf.service.templatev4;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
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

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.cfd._4.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.sitio_internet.cfd.catalogos.CRegimenFiscal;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

public class TemplateAsesoresSur extends PdfPageEventHelper{
	private static final Logger log = LoggerFactory.getLogger(TemplateAsesoresSur.class);
	
	private Image imagenLogo;
	PdfPTable tabDirSuc = new PdfPTable(1);
	PdfPTable tabInfFact = new PdfPTable(2);
	PdfPTable tabDetalleFact = new PdfPTable(2);
	PdfPTable tabDatosFactura = new PdfPTable(7);
	PdfPTable tabPieCFDI = new PdfPTable(2);
	private Image imagenQr;
	
	public TemplateAsesoresSur(Comprobante comprobante, PdfInfoDto infoPDF, Environment env) throws Exception{
		try {
			String strDirSucursal = infoPDF.getDirSucursal();
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
			
			TimbreFiscalDigital timbreFiscalDigital = null;
			for(Object obj : comprobante.getComplemento().getAny()) {
				log.info("TimbreFiscalDigital.DoctoRelacionado:::" + (obj instanceof TimbreFiscalDigital ? "si" : "no"));
				if(obj instanceof TimbreFiscalDigital) {
					timbreFiscalDigital = (TimbreFiscalDigital) obj;
					break;
				}
			}
			
			SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String strFechaExped = comprobante.getLugarExpedicion() +" "+parseador.format(comprobante.getFecha().toGregorianCalendar().getTime());
			String strFechaCertificacion = parseador.format(timbreFiscalDigital.getFechaTimbrado().toGregorianCalendar().getTime());
			String strFolioSerie = comprobante.getSerie()+""+comprobante.getFolio();
			Emisor emisor = comprobante.getEmisor();
			String strNomEmisor = emisor.getNombre();
			Receptor receptor = comprobante.getReceptor();
			String strNomReceptor = receptor.getNombre();
			String strRFCEmisor = emisor.getRfc();
			String strConcep = infoPDF.getDescripcionUsoCfdi();
			String strRFCReceptor = receptor.getRfc();
			String strRegFiscal = getRegimen(emisor.getRegimenFiscal());
			String strRegFiscalReceptor = getRegimen(receptor.getRegimenFiscalReceptor());
			String strDomicilioFiscalReceptor = receptor.getDomicilioFiscalReceptor();
			
			//String strDomicFiscalRecep = "FALTA DATO";
			System.out.println("direccion:::   "+infoPDF.getDirFiscalEmisor());
			String strDomicFiscalEmis = infoPDF.getDirFiscalEmisor();
			String strNomOrdPac = infoPDF.getConsecutivo()+" "+infoPDF.getNombrePaciente();
			String strCadenaTimbre = "CADENA ORIGINAL DEL COMPLEMENTO DE CERTIFICACION DIGITAL SAT:"+infoPDF.getCadenaOriginal()+" "
					+ "Sello Digital del SAT: "+timbreFiscalDigital.getSelloSAT()+" CERTIFICADO SAT: "+ timbreFiscalDigital.getNoCertificadoSAT();
			String uuid = timbreFiscalDigital.getUUID();
			String sello = comprobante.getSello();


			String uuidRelacionado="";
			String codeTipoRelacionado=null;
			if(comprobante.getCfdiRelacionados()!=null && comprobante.getCfdiRelacionados().size()>0) {
				CfdiRelacionados cfdiRelacionado = comprobante.getCfdiRelacionados().get(0);				
				codeTipoRelacionado = cfdiRelacionado.getTipoRelacion();
				List<CfdiRelacionado> listRelacionados = cfdiRelacionado.getCfdiRelacionado();
				if(listRelacionados.size()>0) {
					uuidRelacionado = listRelacionados.get(0).getUUID();
				}
			}
			
			String tipoRelacionado = "";
			if(codeTipoRelacionado != null){
				switch (codeTipoRelacionado) {
				case "01":
					tipoRelacionado = codeTipoRelacionado+" - Nota de crédito de los documentos relacionados";
					break;
				case "02":
					tipoRelacionado = codeTipoRelacionado+" - Nota de débito de los documentos relacionados";
					break;
				case "03":
					tipoRelacionado = codeTipoRelacionado+" - Devolución de mercancía sobre facturas o traslados previos";
					break;
				case "04":
					tipoRelacionado = codeTipoRelacionado+" - Sustitución de los CFDI previos";
					break;
				case "05":
					tipoRelacionado = codeTipoRelacionado+" - Traslados de mercancías facturados previamente";
					break;
				case "06":
					tipoRelacionado = codeTipoRelacionado+" - Factura generada por los traslados previos";
					break;
				case "07":
					tipoRelacionado = codeTipoRelacionado+" - CFDI por aplicación de anticipo";
					break;
				default:
					break;
				}
			}
			
			//BaseColor colorLetraEncabezadoImagen = WebColors.getRGBColor("#1F49B6");
			BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
			BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
			BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");			
			Font fuenteDirSucur = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteImport = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteImportPie = new Font(Font.FontFamily.HELVETICA,5,Font.BOLD,BaseColor.BLACK);
			Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,colorLetraEncabezados);
			Font fuenteTituloTabDetalle = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,colorLetraEncabezados);
			Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
			Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
			Font fuenteTimbrado = new Font(Font.FontFamily.HELVETICA,4,Font.NORMAL,BaseColor.BLACK);
			Font fuenteTimbradoImpor = new Font(Font.FontFamily.HELVETICA,4,Font.BOLD,BaseColor.BLACK);
			
			
			
			/*rutaproduccion*/ imagenLogo = Image.getInstance(env.getProperty("path.file.logo.asesoressur"));
			imagenLogo.setAbsolutePosition(370, 730f);           
            imagenLogo.scaleAbsoluteWidth(200f);
            imagenLogo.scaleAbsoluteHeight(90f);             
			///////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////	Direccion del PDF
			///////////////////////////////////////////////////////////////////////////////////////////////            
            PdfPCell direccionSucursal = new PdfPCell(new Paragraph("  ",fuenteDirSucur));
            direccionSucursal.setBorder(Rectangle.UNDEFINED);
            tabDirSuc.addCell(direccionSucursal);
            tabDirSuc.setTotalWidth(350);                   
			///////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////	Detalle Encabezado Version / CFDI
			///////////////////////////////////////////////////////////////////////////////////////////////            
            PdfPCell pcVersion = new PdfPCell(new Paragraph("Versión "+comprobante.getVersion(), fuenteImport));
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
            PdfPCell pcLeyendaTimb = new PdfPCell(new Paragraph("Lugar, fecha y hora de expedición:",fuenteDirSucur));
            PdfPCell pcInputFechaTimbrado = new PdfPCell(new Paragraph(strFechaExped,fuenteImport));
            PdfPCell pcCFDI = new PdfPCell(new Paragraph(strConcep,fuenteImport));
            
            Chunk paraExportacionLabel = new Chunk("Exportación: ",fuenteDirSucur);
            Chunk paraExportacionTxt = new Chunk("No aplica",fuenteImport);
            Paragraph parraExportacion = new Paragraph();
            parraExportacion.add(paraExportacionLabel);
            parraExportacion.add(paraExportacionTxt);
            PdfPCell pcExportacion = new PdfPCell(parraExportacion);
            
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
            pcInputFechaTimbrado.setBorder(Rectangle.RIGHT);
            pcInputFechaTimbrado.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCFDI.setBorder(Rectangle.UNDEFINED);
            pcCFDI.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcExportacion.setBorder(Rectangle.UNDEFINED);
            pcExportacion.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            pcVersion.setBorderColor(colorFondoTituloFact);
            pcTipoComp.setBorderColor(colorFondoTituloFact);
            pcLeyendaTimb.setBorderColor(colorFondoTituloFact);
            pcInputFechaTimbrado.setBorderColor(colorFondoTituloFact);
            
            tabInfFact.addCell(pcVersion);
            tabInfFact.addCell(pcFolioSerie);
            tabInfFact.addCell(pcTipoComp);
            tabInfFact.addCell(pcLeyendaCFDI);
            tabInfFact.addCell(pcLeyendaTimb);
            tabInfFact.addCell(pcCFDI);
            tabInfFact.addCell(pcInputFechaTimbrado);
            tabInfFact.addCell(pcExportacion);
            tabInfFact.setTotalWidth(300);
            
            ///////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////// Encabezado de Detalle de Factura
            ///////////////////////////////////////////////////////////////////////////////////////////////
            PdfPCell pcDatosEmisor = new PdfPCell(new Paragraph("\t DATOS DEL EMISOR", fuenteTituloTab));
            PdfPCell pcDatosReceptor = new PdfPCell(new Paragraph("\t DATOS DEL RECEPTOR",fuenteTituloTab));
            
            PdfPCell pcLineas = new PdfPCell(new Paragraph("\r\n", fuenteTituloTab));
            PdfPCell pcLineasII = new PdfPCell(new Paragraph("\r\n",fuenteTituloTab));
            
            Chunk NomEmisor = new Chunk("Nombre: ",fuenteContenidoTab);
            Chunk datoNomEmisor = new Chunk(strNomEmisor,fuenteContenidoImporTab);
            Paragraph datosEmisor = new Paragraph();
            datosEmisor.add(NomEmisor);
            datosEmisor.add(datoNomEmisor);
            PdfPCell pcNomEmisor = new PdfPCell(datosEmisor);
            
            Chunk NomReceptor = new Chunk("Nombre: ",fuenteContenidoTab);
            Chunk datoNomReceptor = new Chunk(strNomReceptor,fuenteContenidoImporTab);
            Paragraph datosReceptor = new Paragraph();
            datosReceptor.add(NomReceptor);
            datosReceptor.add(datoNomReceptor);
            datosReceptor.setLeading(30);
            PdfPCell pcNomReceptor = new PdfPCell(datosReceptor);
            
            Chunk RFCEmisor = new Chunk("RFC: ",fuenteContenidoTab);
            Chunk datoRFCEmisor = new Chunk(strRFCEmisor,fuenteContenidoImporTab);
            Paragraph datosRFCEmisor = new Paragraph();
            datosRFCEmisor.add(RFCEmisor);
            datosRFCEmisor.add(datoRFCEmisor);
            datosRFCEmisor.setLeading(30);
            PdfPCell pcRFCEmisor = new PdfPCell(datosRFCEmisor);
            
            Chunk RFCReceptor = new Chunk("RFC: ",fuenteContenidoTab);
            Chunk datoRFCReceptor = new Chunk(strRFCReceptor,fuenteContenidoImporTab);
            Paragraph datosRFCReceptor = new Paragraph();
            datosRFCReceptor.add(RFCReceptor);
            datosRFCReceptor.add(datoRFCReceptor);
            datosReceptor.setLeading(30);
            PdfPCell pcRFCReceptor = new PdfPCell(datosRFCReceptor);
            
            Chunk RegimenEmisor = new Chunk("Régimen: ",fuenteContenidoTab);
            Chunk datoRegimenEmisor = new Chunk(strRegFiscal,fuenteContenidoImporTab);
            Paragraph datosRegimenEmisor = new Paragraph();
            datosRegimenEmisor.add(RegimenEmisor);
            datosRegimenEmisor.add(datoRegimenEmisor);
            PdfPCell pcRegimenEmisor = new PdfPCell(datosRegimenEmisor);
            
            PdfPCell pcEmisorSeccion1 = new PdfPCell(new Paragraph("",fuenteContenidoImporTab));
            PdfPCell pcEmisorSeccion2 = new PdfPCell(new Paragraph("",fuenteContenidoImporTab));
            
//            System.out.println("Direccion::::---->>>>>>   "+comprobante.getAdendaDireccion());
//            Chunk DomicilioReceptor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
//            Chunk datoDomicilioReceptor = new Chunk(comprobante.getAdendaDireccion(),fuenteContenidoImporTab);
//            Paragraph datosDomicilioReceptor = new Paragraph();
//            datosDomicilioReceptor.add(DomicilioReceptor);
//            datosDomicilioReceptor.add(datoDomicilioReceptor);
//            PdfPCell pcDomicilioReceptor = new PdfPCell(datosDomicilioReceptor);
//            
//            Chunk DomicilioEmisor = new Chunk("Domicilio fiscal: ",fuenteContenidoTab);
//            Chunk datoDomicilioEmisor = new Chunk(strDomicFiscalEmis.toUpperCase(),fuenteContenidoImporTab);
//            Paragraph datosDomicilioEmisor = new Paragraph();
//            datosDomicilioEmisor.add(DomicilioEmisor);
//            datosDomicilioEmisor.add(datoDomicilioEmisor);            
//            PdfPCell pcDomicilioEmisor = new PdfPCell(datosDomicilioEmisor);

            PdfPCell pcNumOrdenPaciente = new PdfPCell(new Paragraph(strNomOrdPac,fuenteContenidoImporTab));
            
            Chunk RegimenReceptor = new Chunk("Régimen Fiscal: ",fuenteContenidoTab);
            Chunk datoRegimenReceptor = new Chunk(strRegFiscalReceptor,fuenteContenidoImporTab);
            Paragraph datosRegimenReceptor = new Paragraph();
            datosRegimenReceptor.add(RegimenReceptor);
            datosRegimenReceptor.add(datoRegimenReceptor);
            PdfPCell pcRegimenFiscalReceptor = new PdfPCell(datosRegimenReceptor);
            
            Chunk DomFiscalReceptor = new Chunk("Domicilio Fiscal: ",fuenteContenidoTab);
            Chunk datoDomFiscalReceptor = new Chunk(strDomicilioFiscalReceptor,fuenteContenidoImporTab);
            Paragraph datosDomFiscalReceptor = new Paragraph();
            datosDomFiscalReceptor.add(DomFiscalReceptor);
            datosDomFiscalReceptor.add(datoDomFiscalReceptor);
            PdfPCell pcDomFiscalReceptor = new PdfPCell(datosDomFiscalReceptor);
            
            pcNomEmisor.setPaddingLeft(7);
            pcNomReceptor.setPaddingLeft(7);
            pcRFCEmisor.setPaddingLeft(7);
            pcRFCReceptor.setPaddingLeft(7);
            pcRegimenEmisor.setPaddingLeft(7);
            pcEmisorSeccion1.setPaddingLeft(7);
            pcEmisorSeccion2.setPaddingLeft(7);
//            pcDomicilioReceptor.setPaddingLeft(7);
//            pcDomicilioEmisor.setPaddingLeft(7);
            pcNumOrdenPaciente.setPaddingLeft(7);
            pcRegimenFiscalReceptor.setPaddingLeft(7);
            pcDomFiscalReceptor.setPaddingLeft(7);
            
            pcDatosReceptor.setBackgroundColor(colorFondoTituloFact);
            pcDatosEmisor.setBackgroundColor(colorFondoTituloFact);
            pcLineas.setBackgroundColor(colorFondoContenidoFact);
            pcLineasII.setBackgroundColor(colorFondoContenidoFact);
            pcNomEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcNomReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcRFCEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcRFCReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcRegimenEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcEmisorSeccion1.setBackgroundColor(colorFondoContenidoFact);
            pcEmisorSeccion2.setBackgroundColor(colorFondoContenidoFact);
//            pcDomicilioReceptor.setBackgroundColor(colorFondoContenidoFact);
//            pcDomicilioEmisor.setBackgroundColor(colorFondoContenidoFact);
            pcNumOrdenPaciente.setBackgroundColor(colorFondoContenidoFact);
            pcRegimenFiscalReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcDomFiscalReceptor.setBackgroundColor(colorFondoContenidoFact);
            pcDatosReceptor.setBorder(Rectangle.UNDEFINED);
            pcDatosEmisor.setBorder(Rectangle.UNDEFINED);
            pcLineas.setBorder(Rectangle.UNDEFINED);
            pcLineasII.setBorder(Rectangle.UNDEFINED);
            pcNomEmisor.setBorder(Rectangle.UNDEFINED);
            pcNomReceptor.setBorder(Rectangle.UNDEFINED);
            pcRFCEmisor.setBorder(Rectangle.UNDEFINED);
            pcRFCReceptor.setBorder(Rectangle.UNDEFINED);
            pcRegimenEmisor.setBorder(Rectangle.UNDEFINED);
            pcEmisorSeccion1.setBorder(Rectangle.UNDEFINED);
            pcEmisorSeccion2.setBorder(Rectangle.UNDEFINED);
//            pcDomicilioReceptor.setBorder(Rectangle.UNDEFINED);
//            pcDomicilioEmisor.setBorder(Rectangle.UNDEFINED);
            pcNumOrdenPaciente.setBorder(Rectangle.UNDEFINED);
            pcRegimenFiscalReceptor.setBorder(Rectangle.UNDEFINED);
            pcDomFiscalReceptor.setBorder(Rectangle.UNDEFINED);
            tabDetalleFact.addCell(pcDatosEmisor);
            tabDetalleFact.addCell(pcDatosReceptor);
//            tabDetalleFact.addCell(pcLineas);
//            tabDetalleFact.addCell(pcLineasII);
            tabDetalleFact.addCell(pcNomEmisor);
            tabDetalleFact.addCell(pcNomReceptor);
            tabDetalleFact.addCell(pcRFCEmisor);
            tabDetalleFact.addCell(pcRFCReceptor);
            tabDetalleFact.addCell(pcRegimenEmisor);
//            tabDetalleFact.addCell(pcDomicilioReceptor);
//            tabDetalleFact.addCell(pcDomicilioEmisor);
            tabDetalleFact.addCell(pcRegimenFiscalReceptor);
            tabDetalleFact.addCell(pcEmisorSeccion1);
            tabDetalleFact.addCell(pcDomFiscalReceptor);
            tabDetalleFact.addCell(pcEmisorSeccion2);
            tabDetalleFact.addCell(pcNumOrdenPaciente);
            tabDetalleFact.addCell(pcLineas);
            tabDetalleFact.addCell(pcLineasII);
            tabDetalleFact.setTotalWidth(530);
            
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Encabezado de Detalle de Factura
			///////////////////////////////////////////////////////////////////////////////////////////////
            
            PdfPCell pcClavePro = new PdfPCell(new Paragraph("\r\n CLAVE \r\n PRODUCTO \r\n / SERVICIO", fuenteTituloTabDetalle));
            PdfPCell pcCodigo = new PdfPCell(new Paragraph("\r\nNÚMERO \r\n DE ORDEN",fuenteTituloTabDetalle));
            PdfPCell pcCantidad = new PdfPCell(new Paragraph("\r\n \r\nCANTIDAD",fuenteTituloTabDetalle));
            PdfPCell pcClaveUnidad = new PdfPCell(new Paragraph("\r\n CLAVE \r\n UNIDAD",fuenteTituloTabDetalle));
            PdfPCell pcValorUni = new PdfPCell(new Paragraph("\r\n VALOR \r\n UNTARIO",fuenteTituloTabDetalle));
            PdfPCell pcDescuento = new PdfPCell(new Paragraph("\r\n \r\n IVA",fuenteTituloTabDetalle));
            PdfPCell pcImporte = new PdfPCell(new Paragraph("\r\n \r\n IMPORTE",fuenteTituloTabDetalle));
            pcClavePro.setBackgroundColor(colorFondoTituloFact);
            pcCodigo.setBackgroundColor(colorFondoTituloFact);
            pcCantidad.setBackgroundColor(colorFondoTituloFact);
            pcClaveUnidad.setBackgroundColor(colorFondoTituloFact);
            pcValorUni.setBackgroundColor(colorFondoTituloFact);
            pcDescuento.setBackgroundColor(colorFondoTituloFact);
            pcImporte.setBackgroundColor(colorFondoTituloFact);
            pcClavePro.setMinimumHeight(40);
            pcCodigo.setMinimumHeight(40);
            pcCantidad.setMinimumHeight(40);
            pcClaveUnidad.setMinimumHeight(40);
            pcValorUni.setMinimumHeight(40);
            pcDescuento.setMinimumHeight(40);
            pcImporte.setMinimumHeight(40);
			pcClavePro.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setVerticalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setVerticalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setVerticalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setVerticalAlignment(Element.ALIGN_CENTER);
            pcImporte.setVerticalAlignment(Element.ALIGN_CENTER);
            pcClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcDescuento.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcImporte.setHorizontalAlignment(Element.ALIGN_CENTER);
            pcClavePro.setBorder(Rectangle.RIGHT);
            pcCodigo.setBorder(Rectangle.RIGHT);
            pcCantidad.setBorder(Rectangle.RIGHT);
            pcClaveUnidad.setBorder(Rectangle.RIGHT);
            pcValorUni.setBorder(Rectangle.RIGHT);
            pcDescuento.setBorder(Rectangle.RIGHT);
            pcImporte.setBorder(Rectangle.UNDEFINED);
            pcClavePro.setBorderColor(colorLetraEncabezados);
            pcCodigo.setBorderColor(colorLetraEncabezados);
            pcCantidad.setBorderColor(colorLetraEncabezados);
            pcClaveUnidad.setBorderColor(colorLetraEncabezados);
            pcValorUni.setBorderColor(colorLetraEncabezados);
            pcDescuento.setBorderColor(colorLetraEncabezados);
            pcImporte.setBorderColor(colorLetraEncabezados);
            tabDatosFactura.addCell(pcClavePro);
            tabDatosFactura.addCell(pcCodigo);
            tabDatosFactura.addCell(pcCantidad);
            tabDatosFactura.addCell(pcClaveUnidad);
            tabDatosFactura.addCell(pcValorUni);
            tabDatosFactura.addCell(pcDescuento);
            tabDatosFactura.addCell(pcImporte);            
            tabDatosFactura.setTotalWidth(530);
			///////////////////////////////////////////////////////////////////////////////////////////////
			////////////////// Pie de Pagina 
			///////////////////////////////////////////////////////////////////////////////////////////////
//            String imgCrearQr = "?re="+strRFCEmisor+"&rr="+strRFCReceptor+"&tt="+comprobante.getTotal().toString()+"&id="+uuid;
            String imgCrearQr = "https://verificacfdi.facturaelectronica.sat.gob.mx/?id="+uuid+"&re="+strRFCEmisor+"&rr="+strRFCReceptor+
            		"&tt="+comprobante.getTotal().toString()+"&fe="+timbreFiscalDigital.getSelloCFD().substring(timbreFiscalDigital.getSelloCFD().length()-8, timbreFiscalDigital.getSelloCFD().length());
            /*rutaproduccion*/ File f = new File(env.getProperty("path.file.qr")); 
	      	GenerarQRCode qrCode = new GenerarQRCode();
	      	qrCode.generateQR(f, imgCrearQr, 600, 600);
          
      		/*rutaproduccion*/ imagenQr = Image.getInstance(env.getProperty("path.file.qr"));
      		imagenQr.setAbsolutePosition(32, 60f);         
			imagenQr.scaleAbsoluteWidth(97.06f);
			imagenQr.scaleAbsoluteHeight(97.06f);
			
			 PdfPCell pcTituloCFDI = new PdfPCell(new Paragraph("CFDI RELACIONADO",fuenteImportPie));
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
         
            PdfPCell uuidRelacionados = null;
            if(comprobante.getCfdiRelacionados() != null && comprobante.getCfdiRelacionados().size()>0) {
            	List<CfdiRelacionado> listRelacionados = comprobante.getCfdiRelacionados().get(0).getCfdiRelacionado();
				if(listRelacionados.size()>0) {
					uuidRelacionados = new PdfPCell(new Paragraph("CFDI Relacionado: "+uuidRelacionado+" Tipo de Relación: "+tipoRelacionado,fuenteTimbradoImpor));
		            uuidRelacionados.setHorizontalAlignment(Element.ALIGN_LEFT);
		            uuidRelacionados.setColspan(2);
				}
            }
            
            PdfPCell pcFolioFiscal = new PdfPCell(new Paragraph("UUID: "+uuid+" Fecha y Hora de Certificación: "+strFechaCertificacion,fuenteTimbradoImpor));
            pcFolioFiscal.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcFolioFiscal.setColspan(2); 
            
            PdfPCell pcNoSerieSAT = new PdfPCell(new Paragraph("Certificado del Sello digital del emisor: "+sello,fuenteTimbradoImpor));
            pcNoSerieSAT.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcNoSerieSAT.setColspan(2);
            
            PdfPCell pcCertEmisor = new PdfPCell(new Paragraph("Certificado del emisor: "+comprobante.getNoCertificado()+
            		"\t RFC del Proveedor de Certificación: "+timbreFiscalDigital.getRfcProvCertif(),fuenteTimbradoImpor));
            pcCertEmisor.setHorizontalAlignment(Element.ALIGN_LEFT);
            pcCertEmisor.setColspan(2);
            
            PdfPCell pcLeyendaDoc = new PdfPCell(new Paragraph("Este documento es una representación impresa de un CFDI",fuenteTimbradoImpor));
            pcLeyendaDoc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pcLeyendaDoc.setColspan(2);
            pcTituloCFDI.setBorder(Rectangle.UNDEFINED);
            pcTituloRelacionCFDI.setBorder(Rectangle.UNDEFINED);
            pcTituloCFDIRelacionado.setBorder(Rectangle.UNDEFINED);
            if(uuidRelacionados != null){
            	uuidRelacionados.setBorder(Rectangle.UNDEFINED);
            }
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
            if(uuidRelacionados != null){
            	tabPieCFDI.addCell(uuidRelacionados);
            }
            tabPieCFDI.addCell(pcFolioFiscal);
            tabPieCFDI.addCell(pcCertEmisor);
            tabPieCFDI.addCell(pcNoSerieSAT);
            tabPieCFDI.addCell(pcLeyendaDoc);
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
			tabPieCFDI.writeSelectedRows(0, -1, 120f, 160f, writer.getDirectContent());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public String getRegimen(String code) {
		String strRegFiscalReceptor = "";
		switch (code) {
		case "601":
			strRegFiscalReceptor = "601 - GENERAL DE LEY PERSONAS MORALES";
			break;
		case "603":
			strRegFiscalReceptor = "603 - PERSONAS MORALES CON FINES NO LUCRATIVOS";
			break;
		case "605":
			strRegFiscalReceptor = "605 - SUELDOS Y SALARIOS E INGRESOS ASIMILADOS A SALARIOS";
			break;
		case "606":
			strRegFiscalReceptor = "606 - ARRENDAMIENTO";
			break;
		case "608":
			strRegFiscalReceptor = "608 - DEMAS INGRESOS";
			break;
		case "609":
			strRegFiscalReceptor = "609 - CONSOLIDACION";
			break;
		case "610":
			strRegFiscalReceptor = "610 - RESIDENTES EN EL EXTRANJERO SIN ESTABLECIMIENTO PERMANENTE EN MEXICO";
			break;
		case "611":
			strRegFiscalReceptor = "611 - INGRESOS POR DIVIDENDOS (SOCIOS Y ACCIONISTAS)";
			break;
		case "612":
			strRegFiscalReceptor = "612 - PERSONAS FISICAS CON ACTIVIDADES EMPRESARIALES Y PROFESIONALES";
			break;
		case "614":
			strRegFiscalReceptor = "614 - INGRESOS POR INTERESES";
			break;
		case "616":
			strRegFiscalReceptor = "616 - SIN OBLIGACIONES FISCALES";
			break;
		case "620":
			strRegFiscalReceptor = "620 - SOCIEDADES COOPERATIVAS DE PRODUCCION QUE OPTAN POR DIFERIR SUS INGRESOS";
			break;
		case "621":
			strRegFiscalReceptor = "621 - INCORPORACION FISCAL";
			break;
		case "622":
			strRegFiscalReceptor = "622 - ACTIVIDADES AGRICOLAS, GANADERAS, SILVICOLAS Y PESQUERAS";
			break;
		case "623":
			strRegFiscalReceptor = "623 - OPCIONAL PARA GRUPOS DE SOCIEDADES";
			break;
		case "624":
			strRegFiscalReceptor = "624 - COORDINADOS";
			break;
		case "628":
			strRegFiscalReceptor = "628 - HIDROCARBUROS";
			break;
		case "607":
			strRegFiscalReceptor = "607 - REGIMEN DE ENAJENACION O ADQUISICION DE BIENES";
			break;
		case "629":
			strRegFiscalReceptor = "629 - DE LOS REGIMENES FISCALES PREFERENTES Y DE LAS EMPRESAS MULTINACIONALES";
			break;
		case "630":
			strRegFiscalReceptor = "630 - ENAJENACION DE ACCIONES EN BOLSA DE VALORES";
			break;
		case "615":
			strRegFiscalReceptor = "615 - REGIMEN DE LOS INGRESOS POR OBTENCION DE PREMIOS";
			break;
		case "625":
			strRegFiscalReceptor = "625 - REGIMEN DE LAS ACTIVIDADES EMPRESARIALES CON INGRESOS A TRAVES DE PLATAFORMAS TECNOLOGICAS";
			break;
		case "626":
			strRegFiscalReceptor = "626 - REGIMEN SIMPLIFICADO DE CONFIANZA";
			break;
			
		default:
			break;
		}		
		return strRegFiscalReceptor;
	}
}
