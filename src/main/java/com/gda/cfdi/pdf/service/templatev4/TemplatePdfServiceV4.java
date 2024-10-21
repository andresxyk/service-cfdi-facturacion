package com.gda.cfdi.pdf.service.templatev4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import mx.gob.sat.addenda.AddendaEmpresa;
import mx.gob.sat.addenda.AddendaEmpresa.Datos;
import mx.gob.sat.addenda.AddendaEmpresa.Datos.Detalle;
import mx.gob.sat.cfd._4.Comprobante;

@Service
public class TemplatePdfServiceV4 implements ITemplatePdfServiceV4Impl {

	private static final Logger log = LoggerFactory.getLogger(TemplatePdfServiceV4.class);

	@Override
	public String CrearPdfMarcaOlab(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException, Exception {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerOlab = null;
//		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#FF4E00");

		Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraEncabezados);

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);

		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.olab") + "OLFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerOlab = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerOlab.setPageEvent(new TemplateOlab(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerOlab.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;
	}

	@Override
	public String CrearPdfMarcaAzteca(Comprobante comprobante, PdfInfoDto infoPDF, Environment env) throws Exception {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerAzteca = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#005CB9");
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 150);
		FileOutputStream ficheroPdf = null;
		try {
			/* rutaproduccion */ ruta = env.getProperty("path.files.pdf.azteca") + "AZFA_" + infoPDF.getKfactura()
					+ ".pdf";
			/* rutapruebas */// ruta = "C:/Users/Desarrollo_GDA/documentos
								// Timbrado/pdf/Azteca/pdf/AZFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerAzteca = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerAzteca.setPageEvent(new TemplateAzteca(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
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
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerAzteca.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;
	}

	@Override
	public String CrearPdfMarcaSwiss(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			/* rutaproduccion */ ruta = env.getProperty("path.files.pdf.swisslab") + "SWFA_" + infoPDF.getKfactura()
					+ ".pdf";
			/* rutapruebas */// ruta = "C:/Users/Desarrollo_GDA/documentos
								// Timbrado/pdf/Olab/pdf/OLFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateSwiss(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerSwiss.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;
	}

	@Override
	public String CrearPdfMarcaLiacsa(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.liacsa") + "LIFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateLiacsa(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerSwiss.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;

	}

	@Override
	public String CrearPdfMarcaFamilyLabsNorte(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.familylabsnorte") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateFamilyLabsNorte(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerSwiss.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;

	}
	
	@Override
	public String CrearPdfMarcaExakta(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerPdf = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#478c75");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.exakta") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateExakta(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				documentReportePdf.add(tabDatosFactura);
				PdfContentByte canvas = writerPdf.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				documentReportePdf.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
		documentReportePdf.add(tabDatosFactura);
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPdf.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		documentReportePdf.close();

		return ruta;

	}
	
	
	@Override
	public String CrearPdfMarcaMoreira(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerPdf = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.moreira") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateMoreira(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				documentReportePdf.add(tabDatosFactura);
				PdfContentByte canvas = writerPdf.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				documentReportePdf.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
		documentReportePdf.add(tabDatosFactura);
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPdf.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		documentReportePdf.close();

		return ruta;

	}
	
	
	@Override
	public String CrearPdfMarcaPolab(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerPdf = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.polab") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplatePolab(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				documentReportePdf.add(tabDatosFactura);
				PdfContentByte canvas = writerPdf.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				documentReportePdf.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
		documentReportePdf.add(tabDatosFactura);
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPdf.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		documentReportePdf.close();

		return ruta;

	}
	
	@Override
	public String CrearPdfMarcaBiomedica(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerPdf = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.referencia") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplateBiomedica(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				documentReportePdf.add(tabDatosFactura);
				PdfContentByte canvas = writerPdf.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				documentReportePdf.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
		documentReportePdf.add(tabDatosFactura);
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPdf.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		documentReportePdf.close();

		return ruta;

	}
	
	
	@Override
	public String CrearPdfMarcaPromedic(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerPdf = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.promedic") + "FNFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerPdf = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPdf.setPageEvent(new TemplatePromedic(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				documentReportePdf.add(tabDatosFactura);
				PdfContentByte canvas = writerPdf.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				documentReportePdf.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
		documentReportePdf.add(tabDatosFactura);
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPdf.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		documentReportePdf.close();

		return ruta;

	}
	
	
	@Override
	public String CrearPdfMarcaAsesoresSur(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.asesoressur") + "ASFA_" + infoPDF.getKfactura() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerSwiss = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateAsesoresSur(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerSwiss.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;

	}

	@Override
	public String CrearPdfMarcaJenner(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException, Exception {

		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerJenner = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#0971CE");
		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);
		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;
		try {
			/* rutaproduccion */ ruta = env.getProperty("path.files.pdf.jenner") + "JEFA_" + infoPDF.getKfactura()
					+ ".pdf";
			/* rutapruebas */// ruta = "C:/Users/Desarrollo_GDA/documentos
								// Timbrado/pdf/Jenner/pdf/JEFA_"+kfactura+".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerJenner = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			try {
				writerJenner.setPageEvent(new TemplateJenner(comprobante, infoPDF, env));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerJenner.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
			}
		}
		reporteAzteca.close();

		return ruta;

	}
	
	@Override
	public String CrearPdfEmpresaGenerico(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException, Exception {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
//		PdfPTable tabDetalleAddenda = new PdfPTable(1);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerOlab = null;
//		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F2F2F2");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);
		
		Font fuenteContenidoAddenda = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#6FA8DC");

		Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraEncabezados);

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);

		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.generico") + "Folio_" + comprobante.getFolio() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerOlab = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerOlab.setPageEvent(new HeaderFooter());
			writerOlab.setPageEvent(new TemplateGenericoEmpresa(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerOlab.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(
					new Paragraph(infConcepto.getDescuento() != null ? infConcepto.getDescuento().toString() : "0.0",
							fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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

			int numberOfPages = writerOlab.getPageNumber();

			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + writerOlab.getPageNumber());
		}

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteAzteca.add(tabDatosFactura);
		
		
		
//		AddendaEmpresa addendaEmpresa = null;
//		for(Object obj : comprobante.getAddenda().getAny()) {
//			log.info("AddendaEmpresa.DoctoRelacionado:::" + (obj instanceof AddendaEmpresa ? "si" : "no"));
//			if(obj instanceof AddendaEmpresa) {
//				addendaEmpresa = (AddendaEmpresa) obj;
//				break;
//			}
//		}
		
//		tabDetalleAddenda.setWidthPercentage(101);
//		tabDetalleAddenda.setHorizontalAlignment(0);
//		tabDetalleAddenda.setHeaderRows(2);
//		tabDetalleAddenda.setFooterRows(2);
//		tabDetalleAddenda.setTotalWidth(530);
//		
//		if(addendaEmpresa!=null) {
//			Datos datos = addendaEmpresa.getDatos();
//			if(datos!=null) {
//				List<Detalle> listDetalles = datos.getDetalle();
//				if(listDetalles!=null ) {
//					for (Detalle detalle : listDetalles) {
//						System.out.println(detalle.getDescripcion());						
//						
//						PdfPCell pcDescripcionAddenda = new PdfPCell(new Paragraph(detalle.getDescripcion(), fuenteContenidoAddenda));
//						pcDescripcionAddenda.setIndent(8);
//						pcDescripcionAddenda.setBackgroundColor(colorFondoContenidoFact);
//						pcDescripcionAddenda.setBorder(Rectangle.BOTTOM);
//						pcDescripcionAddenda.setBorderColor(colorLetraEncabezados);
//						pcDescripcionAddenda.setHorizontalAlignment(Element.ALIGN_LEFT);
//						pcDescripcionAddenda.setVerticalAlignment(Element.ALIGN_CENTER);
//						tabDetalleAddenda.addCell(pcDescripcionAddenda);
//						
//						
//						
//						
//					}
//				}
//			}
//		}		
		
		
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerOlab.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
//				tabDetalleAddenda.writeSelectedRows(0, -1, 35f, 370f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
//				tabDetalleAddenda.writeSelectedRows(0, -1, 35f, 360, canvas);
			}
		}
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + cont);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + bandera);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + writerOlab.getPageNumber());

		reporteAzteca.close();

		return ruta;
	}

	@Override
	public String CrearPdfMarcaExaktaEmpresa(Comprobante comprobante, PdfInfoDto infoPDF, Environment env)
			throws DocumentException, IOException, Exception {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleAddenda = new PdfPTable(1);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfWriter writerOlab = null;
//		float[] medidaCeldas = {2.3f,0.7f,0.5f};
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F2F2F2");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraBlanco = WebColors.getRGBColor("#FFFFFF");

		Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraBlanco);
		
		Font fuenteContenidoAddenda = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);

		BaseColor colorLetraEncabezados = WebColors.getRGBColor("#FFFFFF");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#458C6B");

		Font fuenteTituloTab = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, colorLetraEncabezados);

		String strMontoSubTotal = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
		String strMontoImpuesto = comprobante.getImpuestos().getTraslados().getTraslado().get(0).getImporte()
				.toString();
		String strMontoTotal = comprobante.getTotal().toString();

		Document reporteAzteca = new Document(PageSize.A4, 36, 36, 260, 136);

		FileOutputStream ficheroPdf = null;
		try {
			ruta = env.getProperty("path.files.pdf.exakta") + "EXFA_" + comprobante.getFolio() + ".pdf";
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			writerOlab = PdfWriter.getInstance(reporteAzteca, ficheroPdf);
			writerOlab.setPageEvent(new HeaderFooter());
			writerOlab.setPageEvent(new TemplateExaktaEmpresa(comprobante, infoPDF, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		reporteAzteca.open();
		reporteAzteca.newPage();

		String montoTerceros = "0.00";
		int cont1 = 0;
		Boolean bHonorarioMedico = false;
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			if (infConcepto.getDescripcion().equals("Honorario Medico")
					&& infConcepto.getClaveProdServ().equals("85121600")) {
				montoTerceros = infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString();
				bHonorarioMedico = true;
			}
			cont1++;
		}

		System.out.println("PDFTerceros:" + infoPDF.getComplementoConcepto());

		System.out.println(montoTerceros);

		PdfPCell pcTitleTerceros = new PdfPCell(new Paragraph("Complemento Terceros", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleVersion = new PdfPCell(new Paragraph("Version", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleRFC = new PdfPCell(new Paragraph("RFC", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleNombre = new PdfPCell(new Paragraph("Nombre", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtVersion = new PdfPCell(new Paragraph("1.1", fuenteContenidoImporTab));
		PdfPCell pcTxtRFC = new PdfPCell(new Paragraph("SAE190815RA5", fuenteContenidoImporTab));
		PdfPCell pcTxtNombre = new PdfPCell(new Paragraph(
				"SERVICIOS ADMINISTRATIVOS ESPECIALIZADOS EN LABORATORIOS DE ANALISIS SC", fuenteContenidoImporTab));
		PdfPCell pcTitleImpuestos = new PdfPCell(new Paragraph("Impuestos Traslados", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImpuesto = new PdfPCell(new Paragraph("Impuesto", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleTasa = new PdfPCell(new Paragraph("Tasa", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleImporte = new PdfPCell(new Paragraph("Importe", fuenteContenidoImporTabWhite));
		PdfPCell pcTxtImpuesto = new PdfPCell(new Paragraph("IVA", fuenteContenidoImporTab));
		PdfPCell pcTxtTasa = new PdfPCell(new Paragraph("0.00%", fuenteContenidoImporTab));
		PdfPCell pcTxtImporte = new PdfPCell(new Paragraph("-", fuenteContenidoImporTab));
		PdfPCell pcTitleParte = new PdfPCell(new Paragraph("Parte", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleCantidad = new PdfPCell(new Paragraph("Cantidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleUnidad = new PdfPCell(new Paragraph("Unidad", fuenteContenidoImporTabWhite));
		PdfPCell pcTitleIdentificacion = new PdfPCell(
				new Paragraph("No. Identificación", fuenteContenidoImporTabWhite));
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
		PdfPCell pcFormaPago = new PdfPCell(
				new Paragraph("Forma de pago " + comprobante.getFormaPago(), fuenteContenidoImporTab));
		PdfPCell pcSubTotal = new PdfPCell(new Paragraph("Subtotal(16%): ", fuenteContenidoImporTab));
		PdfPCell pcMontoSubTotal = new PdfPCell(new Paragraph(strMontoSubTotal, fuenteContenidoImporTab));
		PdfPCell pcMetodo = new PdfPCell(
				new Paragraph("Método de pago " + infoPDF.getDescripcionMetodoPago(), fuenteContenidoImporTab));
		PdfPCell pcImpuesto = new PdfPCell(new Paragraph("Impuesto trasladado (IVA 16%):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuesto = new PdfPCell(new Paragraph(strMontoImpuesto, fuenteContenidoImporTab));
		PdfPCell pcSubtotalExento = new PdfPCell(new Paragraph("Subtotal exento:", fuenteContenidoImporTab));
		PdfPCell pcMontoSubtotalExento = new PdfPCell(new Paragraph(montoTerceros, fuenteContenidoImporTab));
		PdfPCell pcImpuestoTrasladado = new PdfPCell(
				new Paragraph("Impuesto trasladado (IVA exento):", fuenteContenidoImporTab));
		PdfPCell pcMontoImpuestoTrasladado = new PdfPCell(new Paragraph("0.00", fuenteContenidoImporTab));

		PdfPCell pcTotal = new PdfPCell(new Paragraph("Total: ", fuenteContenidoImporTab));
		PdfPCell pcMontoTotal = new PdfPCell(new Paragraph(strMontoTotal, fuenteContenidoImporTab));
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
		PdfPCell pcMontoLetra = new PdfPCell(new Paragraph(strMontoTotalLetra, fuenteContenidoImporTab));
		PdfPCell pcMontoVacio1 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));
		PdfPCell pcMontoVacio2 = new PdfPCell(new Paragraph("", fuenteContenidoImporTab));

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
		pcSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcMontoSubtotalExento.setBackgroundColor(colorFondoContenidoFact);
		pcImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoImpuestoTrasladado.setBackgroundColor(colorFondoContenidoFact);
		pcMontoLetra.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio1.setBackgroundColor(colorFondoContenidoFact);
		pcMontoVacio2.setBackgroundColor(colorFondoContenidoFact);

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
		pcSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcMontoSubtotalExento.setBorder(Rectangle.UNDEFINED);
		pcImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoImpuestoTrasladado.setBorder(Rectangle.UNDEFINED);
		pcMontoLetra.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio1.setBorder(Rectangle.UNDEFINED);
		pcMontoVacio2.setBorder(Rectangle.UNDEFINED);

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
		pcMontoVacio1.setHorizontalAlignment(Element.ALIGN_LEFT);
		pcMontoVacio2.setHorizontalAlignment(Element.ALIGN_LEFT);
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
		pcSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoSubtotalExento.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);
		pcMontoImpuestoTrasladado.setHorizontalAlignment(Element.ALIGN_RIGHT);

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
		pcMontoVacio1.setColspan(3);
		pcMontoVacio2.setColspan(3);
		pcTotal.setColspan(2);
		pcSubtotalExento.setColspan(2);
		pcImpuestoTrasladado.setColspan(2);

		tabDetalleMontos.setWidthPercentage(101);
		tabDetalleMontos.setHorizontalAlignment(0);
//	    tabDetalleMontos.setWidths(medidaCeldas);
		tabDetalleMontos.setHeaderRows(2);
		tabDetalleMontos.setFooterRows(2);
		tabDetalleMontos.setTotalWidth(530);

		int limit = 10;
		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();

		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {
			if (limit == cont) {
				tabDatosFactura.setWidthPercentage(101);
				tabDatosFactura.setHorizontalAlignment(0);
				reporteAzteca.add(tabDatosFactura);
				PdfContentByte canvas = writerOlab.getDirectContent();
				if (bHonorarioMedico) {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				} else {
					tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				}
				tabDatosFactura = new PdfPTable(7);
				reporteAzteca.newPage();
				cont = 0;
			}

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getNoIdentificacion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
//		    PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(),fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(
					new Paragraph(infConcepto.getDescuento() != null ? infConcepto.getDescuento().toString() : "0.0",
							fuenteContenidoTab));
			// infConcepto.getImpuestos().getTraslados().getTraslado().get(cont).getImporte().toString()
			PdfPCell Importe = new PdfPCell(
					new Paragraph(infConcepto.getImpuestos().getTraslados().getTraslado().get(0).getBase().toString(),
							fuenteContenidoTab));
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
			PdfPCell pcDescripcion = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoImporTab));
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

			int numberOfPages = writerOlab.getPageNumber();

			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + writerOlab.getPageNumber());
		}

		if (bHonorarioMedico) {
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

		tabDetalleMontos.addCell(pcMontoVacio1);
		tabDetalleMontos.addCell(pcSubtotalExento);
		tabDetalleMontos.addCell(pcMontoSubtotalExento);
		tabDetalleMontos.addCell(pcMontoVacio2);
		tabDetalleMontos.addCell(pcImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoImpuestoTrasladado);
		tabDetalleMontos.addCell(pcMontoLetra);
		tabDetalleMontos.addCell(pcTotal);
		tabDetalleMontos.addCell(pcMontoTotal);

		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteAzteca.add(tabDatosFactura);
		
		
		
		AddendaEmpresa addendaEmpresa = null;
		for(Object obj : comprobante.getAddenda().getAny()) {
			log.info("AddendaEmpresa.DoctoRelacionado:::" + (obj instanceof AddendaEmpresa ? "si" : "no"));
			if(obj instanceof AddendaEmpresa) {
				addendaEmpresa = (AddendaEmpresa) obj;
				break;
			}
		}
		
		tabDetalleAddenda.setWidthPercentage(101);
		tabDetalleAddenda.setHorizontalAlignment(0);
		tabDetalleAddenda.setHeaderRows(2);
		tabDetalleAddenda.setFooterRows(2);
		tabDetalleAddenda.setTotalWidth(530);
		
		if(addendaEmpresa!=null) {
			Datos datos = addendaEmpresa.getDatos();
			if(datos!=null) {
				List<Detalle> listDetalles = datos.getDetalle();
				if(listDetalles!=null ) {
					for (Detalle detalle : listDetalles) {
						System.out.println(detalle.getDescripcion());						
						
						PdfPCell pcDescripcionAddenda = new PdfPCell(new Paragraph(detalle.getDescripcion(), fuenteContenidoAddenda));
						pcDescripcionAddenda.setIndent(8);
						pcDescripcionAddenda.setBackgroundColor(colorFondoContenidoFact);
						pcDescripcionAddenda.setBorder(Rectangle.BOTTOM);
						pcDescripcionAddenda.setBorderColor(colorLetraEncabezados);
						pcDescripcionAddenda.setHorizontalAlignment(Element.ALIGN_LEFT);
						pcDescripcionAddenda.setVerticalAlignment(Element.ALIGN_CENTER);
						tabDetalleAddenda.addCell(pcDescripcionAddenda);
						
						
						
						
					}
				}
			}
		}		
		
		
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerOlab.getDirectContent();
			if (bHonorarioMedico) {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 342f, canvas);
				tabDetalleAddenda.writeSelectedRows(0, -1, 35f, 370f, canvas);
			} else {
				tabDetalleMontos.writeSelectedRows(0, -1, 35f, 232f, canvas);
				tabDetalleAddenda.writeSelectedRows(0, -1, 35f, 360, canvas);
			}
		}
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + cont);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + bandera);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + writerOlab.getPageNumber());

		reporteAzteca.close();

		return ruta;
	}

	private static class HeaderFooter extends PdfPageEventHelper {

		public void onEndPage(PdfWriter writer, Document document) {
			Font fuenteContenidoImporTabWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
			int pageNumber = writer.getPageNumber();
			PdfPTable table = new PdfPTable(1);
			table.setTotalWidth(530);
			PdfPCell pcPaginacion = new PdfPCell(
					new Paragraph(String.format("Página %d", pageNumber), fuenteContenidoImporTabWhite));
			pcPaginacion.setHorizontalAlignment(Element.ALIGN_RIGHT);
			pcPaginacion.setVerticalAlignment(Element.ALIGN_CENTER);
			pcPaginacion.setBorder(Rectangle.UNDEFINED);
			table.addCell(pcPaginacion);

			table.writeSelectedRows(0, -1, 35, 50, writer.getDirectContent());
		}
	}

	public String montoConLetra(String strMontoTotal) {
		System.out.println("inicia**** " + strMontoTotal);
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
		System.out.print("***  " + numeroFinal.toUpperCase());
		System.out.println("\n");
		return numeroFinal.toUpperCase();
	}

}
