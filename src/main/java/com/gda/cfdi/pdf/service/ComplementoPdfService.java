package com.gda.cfdi.pdf.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.service.complementopago.TemplateAztecaComplemento;
import com.gda.cfdi.pdf.service.complementopago.TemplateFamilyLabsNorteComplemento;
import com.gda.cfdi.pdf.service.complementopago.TemplateJennerComplemento;
import com.gda.cfdi.pdf.service.complementopago.TemplateJennerLogoAztecaComplemento;
import com.gda.cfdi.pdf.service.complementopago.TemplateOlabComplemento;
import com.gda.cfdi.pdf.service.complementopago.TemplateSwissComplemento;
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
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.cfd.pagos.Pagos.Pago;

@Service
public class ComplementoPdfService {
	final static Logger logger = LogManager.getLogger(ComplementoPdfService.class);
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private Environment env;

	public String crearPdfMarcaSwiss(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo, String serie) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.swisslab") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateSwissComplemento(comprobante, complementoDatos,serie, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);

			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());


						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);
						
						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);
						
						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto().longValue()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);
//
//							PdfContentByte canvas = writerSwiss.getDirectContent();
//							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
//
//							logger.info("tabDatosComplemento:::");
//						}
//					}
//				}
//			}
//		}
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	public String crearPdfMarcaFamilyLabsNorte(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo, String serie) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.familylabsnorte") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateFamilyLabsNorteComplemento(comprobante, complementoDatos,serie, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);

			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());


						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);
						
						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);
						
						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto().longValue()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);
//
//							PdfContentByte canvas = writerSwiss.getDirectContent();
//							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
//
//							logger.info("tabDatosComplemento:::");
//						}
//					}
//				}
//			}
//		}
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	public String crearPdfMarcaOlab(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.olab") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateOlabComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);

			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());

						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);

						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);

						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	public String crearPdfMarcaAzteca(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.azteca") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateAztecaComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);

			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());

						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);

						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);
						
						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto().longValue()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);

//							PdfContentByte canvas = writerSwiss.getDirectContent();
//							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
//
//							logger.info("tabDatosComplemento:::");
//						}
//					}
//				}
//			}
//		}
//		if (tamanioCOncepto == bandera) {
//			PdfContentByte canvas = writerSwiss.getDirectContent();
//			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
//		}
							
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	
	public String crearPdfMarcaJennerLogoAzteca(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.jenner") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateJennerLogoAztecaComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);

			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());

						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);

						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);
						
						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto().longValue()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);

//							PdfContentByte canvas = writerSwiss.getDirectContent();
//							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
//
//							logger.info("tabDatosComplemento:::");
//						}
//					}
//				}
//			}
//		}
//		if (tamanioCOncepto == bandera) {
//			PdfContentByte canvas = writerSwiss.getDirectContent();
//			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
//		}
							
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	
	
	public String crearPdfMarcaJenner(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(3);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.jenner") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateJennerComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reporteSwisslab.open();
		reporteSwisslab.newPage();

		int cont = 0;
		int bandera = 0;
		int tamanioCOncepto = comprobante.getConceptos().getConcepto().size();
		for (Comprobante.Conceptos.Concepto infConcepto : comprobante.getConceptos().getConcepto()) {

			PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));
			PdfPCell ClavePro = new PdfPCell(new Paragraph(infConcepto.getClaveProdServ(), fuenteContenidoTab));
			PdfPCell Codigo = new PdfPCell(new Paragraph(infConcepto.getDescripcion(), fuenteContenidoTab));
			int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
			PdfPCell Cantidad = new PdfPCell(new Paragraph(Integer.toString(intCantidad), fuenteContenidoTab));
			PdfPCell ClaveUnidad = new PdfPCell(new Paragraph(infConcepto.getClaveUnidad(), fuenteContenidoTab));
			PdfPCell ValorUni = new PdfPCell(
					new Paragraph(infConcepto.getValorUnitario().toString(), fuenteContenidoTab));
			PdfPCell Descuento = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Importe = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell Subtotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			PdfPCell ImporteTotal = new PdfPCell(new Paragraph("0.0", fuenteContenidoTab));
			ClavePro.setBorder(Rectangle.UNDEFINED);
			Codigo.setBorder(Rectangle.UNDEFINED);
			Cantidad.setBorder(Rectangle.UNDEFINED);
			ClaveUnidad.setBorder(Rectangle.UNDEFINED);
			ValorUni.setBorder(Rectangle.UNDEFINED);
			Descuento.setBorder(Rectangle.UNDEFINED);
			Importe.setBorder(Rectangle.UNDEFINED);
			Subtotal.setBorder(Rectangle.UNDEFINED);
			ImporteTotal.setBorder(Rectangle.UNDEFINED);
			espacioBlanco.setBorder(Rectangle.UNDEFINED);
			ClavePro.setHorizontalAlignment(Element.ALIGN_CENTER);
			Codigo.setHorizontalAlignment(Element.ALIGN_CENTER);
			Cantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ClaveUnidad.setHorizontalAlignment(Element.ALIGN_CENTER);
			ValorUni.setHorizontalAlignment(Element.ALIGN_CENTER);
			Descuento.setHorizontalAlignment(Element.ALIGN_CENTER);
			Importe.setHorizontalAlignment(Element.ALIGN_CENTER);
			Subtotal.setHorizontalAlignment(Element.ALIGN_CENTER);
			ImporteTotal.setHorizontalAlignment(Element.ALIGN_CENTER);

			tabDatosFactura.addCell(ClavePro);
			tabDatosFactura.addCell(Codigo);
			tabDatosFactura.addCell(Cantidad);
			tabDatosFactura.addCell(ClaveUnidad);
			tabDatosFactura.addCell(ValorUni);
			tabDatosFactura.addCell(Descuento);
			tabDatosFactura.addCell(Importe);


			cont++;
			bandera++;
		} // fin conceptos
		tabDatosFactura.setWidthPercentage(101);
		tabDatosFactura.setHorizontalAlignment(0);
		reporteSwisslab.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().size());
		int countPage = 0;
		for (Comprobante.Complemento com : comprobante.getComplemento()) {
			logger.info("Comprobante.Complemento.com.getAny:::" + com.getAny().size());
			for (Object obj : com.getAny()) {

				logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
				if (obj instanceof Pagos) {
					Pagos pagos = (Pagos) obj;
					for (Pagos.Pago pago : pagos.getPago()) {
						/*
						 * Tabla Pagos
						 */
						
						String strMonto = df.format(pago.getMonto());


						PdfPCell pcMonedaPeso = new PdfPCell(
								new Paragraph("Moneda: " + pago.getMonedaP(), fuenteContenidoImporTab));
						CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
								.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
						PdfPCell pcCuentaOrden = new PdfPCell(
								new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFormaPago = new PdfPCell(new Paragraph("Forma de pago: " + pago.getFormaDePagoP()
								+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
						PdfPCell pcNombreBanco = new PdfPCell(
								new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
						
						PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
								"Fecha de pago: "
										+ utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
								fuenteContenidoImporTab));
						PdfPCell pcCuentaOrdenante = new PdfPCell(
								new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
						
						PdfPCell pcMontoTotal = new PdfPCell(
								new Paragraph("Monto: " + strMonto, fuenteContenidoImporTab));

						pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
						pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
						pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
						pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
						pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
						pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);

						pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
						pcFormaPago.setBorder(Rectangle.UNDEFINED);
						pcFechaPago.setBorder(Rectangle.UNDEFINED);
						pcMontoTotal.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrden.setBorder(Rectangle.UNDEFINED);
						pcNombreBanco.setBorder(Rectangle.UNDEFINED);
						pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
						pcMontoTotal.setColspan(6);
						pcMonedaPeso.setColspan(3);
						pcFormaPago.setColspan(3);
						pcFechaPago.setColspan(3);
						pcCuentaOrden.setColspan(3);
						pcNombreBanco.setColspan(3);
						pcCuentaOrdenante.setColspan(3);

						tabDetalleMontos.addCell(pcFechaPago);
						tabDetalleMontos.addCell(pcCuentaOrden);
						
						tabDetalleMontos.addCell(pcFormaPago);
						tabDetalleMontos.addCell(pcNombreBanco);
						
						tabDetalleMontos.addCell(pcMonedaPeso);
						tabDetalleMontos.addCell(pcCuentaOrdenante);
						
						tabDetalleMontos.addCell(pcMontoTotal);

						tabDetalleMontos.setWidthPercentage(101);
						tabDetalleMontos.setHorizontalAlignment(0);
						
						tabDetalleMontos.setHeaderRows(2);
						tabDetalleMontos.setFooterRows(2);
						tabDetalleMontos.setTotalWidth(530);

						logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
						/*
						 * Dcomentos relacionados
						 */
						for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
							Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
							PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

							PdfPCell pcIdDocumento = new PdfPCell(
									new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
							PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
									docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
							PdfPCell pcMoneda = new PdfPCell(
									new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
							PdfPCell pcMetodoPago = new PdfPCell(
									new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
							PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
									docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
							PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
							PdfPCell pcImportePagado = new PdfPCell(
									new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
							PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
									df.format(docRelacionado.getImpSaldoInsoluto().longValue()), fuenteContenidoTab));

							pcIdDocumento.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setColspan(2);
							pcSerieFolio.setBorder(Rectangle.UNDEFINED);
							pcMoneda.setBorder(Rectangle.UNDEFINED);
							pcMetodoPago.setBorder(Rectangle.UNDEFINED);
							pcParcialidad.setBorder(Rectangle.UNDEFINED);
							pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
							pcImportePagado.setBorder(Rectangle.UNDEFINED);
							pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
							pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcMetodoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_RIGHT);
							pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
							pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);

							tabDatosComplemento.addCell(pcIdDocumento);
							tabDatosComplemento.addCell(pcSerieFolio);
							tabDatosComplemento.addCell(pcMoneda);
							tabDatosComplemento.addCell(pcMetodoPago);
							tabDatosComplemento.addCell(pcParcialidad);
							tabDatosComplemento.addCell(pcSaldoAnterior);
							tabDatosComplemento.addCell(pcImportePagado);
							tabDatosComplemento.addCell(pcSaldoInsoluto);

							tabDatosComplemento.setTotalWidth(530);

//							PdfContentByte canvas = writerSwiss.getDirectContent();
//							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
//
//							logger.info("tabDatosComplemento:::");
//						}
//					}
//				}
//			}
//		}
//		if (tamanioCOncepto == bandera) {
//			PdfContentByte canvas = writerSwiss.getDirectContent();
//			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
//		}
							if((i  % 25) == 0 && i != 0){
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
								PdfContentByte canvas = writerSwiss.getDirectContent();
								tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
							}else {
								countPage = i;
							}
							logger.info("tabDatosComplemento:::");
						}
						if((countPage  % 25) != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
						}
					}
				}
			}
		}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 495f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 200f, canvas);
		}				
		reporteSwisslab.close();

		return ruta;
	}

	public String montoConLetra(String strMontoTotal) {
		logger.info("inicia**** " + strMontoTotal);
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
		logger.info("***  " + numeroFinal.toUpperCase());
		logger.info("\n");
		return numeroFinal.toUpperCase();
	}
}
