package com.gda.cfdi.pdf.service.complementopagoV4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.service.ConsultaService;
import com.gda.cfdi.pdf.service.UtilsService;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateAztecaComplemento;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateFamilyLabsNorteComplemento;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateJennerComplemento;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateJennerLogoAztecaComplemento;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateOlabComplemento;
import com.gda.cfdi.pdf.service.complementopagoV4.TemplateSwissComplemento;
import com.gda.cfdi.pdf.utils.ConvertirMontosConLetra;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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
import mx.gob.sat.pagos20.Pagos;
import mx.gob.sat.pagos20.Pagos.Pago;

@Service
public class ComplementoPdfV4Service {
	final static Logger logger = LogManager.getLogger(ComplementoPdfV4Service.class);
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private Environment env;

	public String crearPdfMarcaSwiss(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo, String serie) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						
						if (limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}
						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	public String crearPdfMarcaFamilyLabsNorte(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo, String serie) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	public String crearPdfMarcaAsesoresSur(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.asesoressur") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateAsesoresSurComplemento(comprobante, complementoDatos, env));
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	public String crearPdfMarcaExakta(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerPDF = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#458C6B");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.exakta") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivo::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerPDF = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPDF.setPageEvent(new TemplateExaktaComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

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
		documentReportePdf.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerPDF.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							documentReportePdf.newPage();
							documentReportePdf.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerPDF.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerPDF.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						documentReportePdf.newPage();
						documentReportePdf.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerPDF.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerPDF.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPDF.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		documentReportePdf.close();

		return ruta;
	}
	
	public String crearPdfMarcaMoreira(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerPDF = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.moreira") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivo::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerPDF = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPDF.setPageEvent(new TemplateMoreiraComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

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
		documentReportePdf.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerPDF.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							documentReportePdf.newPage();
							documentReportePdf.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerPDF.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerPDF.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						documentReportePdf.newPage();
						documentReportePdf.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerPDF.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerPDF.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPDF.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		documentReportePdf.close();

		return ruta;
	}
	
	
	public String crearPdfMarcaPolab(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerPDF = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.polab") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivo::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerPDF = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPDF.setPageEvent(new TemplatePolabComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

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
		documentReportePdf.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerPDF.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							documentReportePdf.newPage();
							documentReportePdf.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerPDF.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerPDF.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						documentReportePdf.newPage();
						documentReportePdf.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerPDF.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerPDF.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPDF.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		documentReportePdf.close();

		return ruta;
	}
	
	public String crearPdfMarcaBiomedica(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerPDF = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.referencia") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivo::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerPDF = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPDF.setPageEvent(new TemplateBiomedicaComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

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
		documentReportePdf.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerPDF.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							documentReportePdf.newPage();
							documentReportePdf.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerPDF.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerPDF.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						documentReportePdf.newPage();
						documentReportePdf.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerPDF.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerPDF.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPDF.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		documentReportePdf.close();

		return ruta;
	}
	
	public String crearPdfMarcaPromedic(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerPDF = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F4F4F9");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#1F49B6");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		
		Document documentReportePdf = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.file.ordenes.pdf.promedic") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivo::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerPDF = PdfWriter.getInstance(documentReportePdf, ficheroPdf);
			writerPDF.setPageEvent(new TemplatePromedicComplemento(comprobante, complementoDatos, env));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		documentReportePdf.open();
		documentReportePdf.newPage();

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
		documentReportePdf.add(tabDatosFactura);
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerPDF.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							documentReportePdf.newPage();
							documentReportePdf.add(tabDatosFactura);
							contPagos = 0;
						}						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerPDF.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerPDF.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						documentReportePdf.newPage();
						documentReportePdf.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerPDF.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerPDF.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerPDF.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerPDF.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		documentReportePdf.close();

		return ruta;
	}
	
	public String crearPdfMarcaOlab(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#FCECE1");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#FF4E00");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						
						if (limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDetalleMontos);
							contPagos = 0;
						}
						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDetalleMontos);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	public String crearPdfMarcaExaktaEmpresa(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#F2F2F2");
		
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#458C6B");
		
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
		DecimalFormat df = new DecimalFormat("###,###,###.##");
		Document reporteSwisslab = new Document(PageSize.A4, 36, 36, 260, 136);
		FileOutputStream ficheroPdf = null;

		try {
			/* rutaproduccion */
			ruta = env.getProperty("path.files.pdf.exakta") + nombreArchivo
					+ ".pdf";
			logger.info("nobreArchivoOLAB::::  " + ruta);
			ficheroPdf = new FileOutputStream(ruta);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			writerSwiss = PdfWriter.getInstance(reporteSwisslab, ficheroPdf);
			writerSwiss.setPageEvent(new TemplateExaktaComplemento(comprobante, complementoDatos, env));
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+countPage);
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	
	public String crearPdfMarcaAzteca(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#005CB9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}
						
						
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	
	public String crearPdfMarcaJennerLogoAzteca(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E0E8F7");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#005CB9");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
		}
		reporteSwisslab.close();

		return ruta;
	}
	
	
	
	
	public String crearPdfMarcaJenner(Comprobante comprobante, ComplementoDatosDto complementoDatos,
			String nombreArchivo) throws DocumentException, SocketException, IOException {
		String ruta = "";
		PdfPTable tabDetalleMontos = new PdfPTable(6);
		PdfPTable tabDetalleMontosTotales = new PdfPTable(9);
		PdfPTable tabDatosFactura = new PdfPTable(7);
		PdfPTable tabDatosComplemento = new PdfPTable(9);
		PdfWriter writerSwiss = null;
		float[] medidaCeldas = { 2.3f, 0.7f, 0.5f };
		BaseColor colorFondoContenidoFact = WebColors.getRGBColor("#E6ECF8");
		BaseColor colorFondoTituloFact = WebColors.getRGBColor("#0971CE");
		Font fuenteContenidoTab = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabNota = new Font(Font.FontFamily.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoTabIDDoc = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
		Font fuenteContenidoImporTab = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
		Font fuenteContenidoTitleMontos = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
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
		logger.info("Comprobante.Complemento:::" + comprobante.getComplemento().getAny().size());
		int countPage = 0;
		for (Object obj : comprobante.getComplemento().getAny()) {

			logger.info("Pago.DoctoRelacionado:::" + (obj instanceof Pagos ? "si" : "no"));
			if (obj instanceof Pagos) {
				Pagos pagos = (Pagos) obj;
				BigDecimal TotalTrasladosBaseIVA16 = pagos.getTotales().getTotalTrasladosBaseIVA16();
				BigDecimal TotalTrasladosImpuestoIVA16 = pagos.getTotales().getTotalTrasladosImpuestoIVA16();
				BigDecimal MontoTotalPagos = pagos.getTotales().getMontoTotalPagos();
				for (Pagos.Pago pago : pagos.getPago()) {
					
					
					PdfPCell pcTitleMontos = new PdfPCell(
							new Paragraph("Montos Totales de los Pagos", fuenteContenidoTitleMontos));					
					pcTitleMontos.setBackgroundColor(colorFondoTituloFact);
					pcTitleMontos.setBorder(Rectangle.UNDEFINED);
					pcTitleMontos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTitleMontos.setColspan(6);
					tabDetalleMontos.addCell(pcTitleMontos);
					
					PdfPCell pcTrasladosBase = new PdfPCell(
							new Paragraph("Total Traslados Base IVA 16:  "+df.format(TotalTrasladosBaseIVA16), fuenteContenidoImporTab));
					pcTrasladosBase.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosBase.setBorder(Rectangle.UNDEFINED);
					pcTrasladosBase.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosBase.setColspan(2);
					pcTrasladosBase.setBorder(Rectangle.RIGHT);
					pcTrasladosBase.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosBase);
					
					PdfPCell pcTrasladosImpuesto = new PdfPCell(
							new Paragraph("Total Traslados Impuesto IVA 16:  "+df.format(TotalTrasladosImpuestoIVA16), fuenteContenidoImporTab));
					pcTrasladosImpuesto.setBackgroundColor(colorFondoContenidoFact);
					pcTrasladosImpuesto.setBorder(Rectangle.UNDEFINED);
					pcTrasladosImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setVerticalAlignment(Element.ALIGN_CENTER);
					pcTrasladosImpuesto.setColspan(2);
					pcTrasladosImpuesto.setBorder(Rectangle.RIGHT);
					pcTrasladosImpuesto.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcTrasladosImpuesto);
					
					PdfPCell pcMontoTotalPagos = new PdfPCell(
							new Paragraph("Monto Total Pagos:  "+df.format(MontoTotalPagos), fuenteContenidoImporTab));
					pcMontoTotalPagos.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotalPagos.setBorder(Rectangle.UNDEFINED);
					pcMontoTotalPagos.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotalPagos.setColspan(2);
					pcMontoTotalPagos.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
					tabDetalleMontos.addCell(pcMontoTotalPagos);
					
					tabDetalleMontos.setWidthPercentage(101);
					tabDetalleMontos.setHorizontalAlignment(0);

					tabDetalleMontos.setHeaderRows(2);
					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontos.setTotalWidth(530);
					
					/*
					 * Tabla Pagos
					 */
					
					String strMonto = df.format(pago.getMonto());
					CFormaPagoCfdiDto cFormaPagoCFDIDto = consultaService
							.selectAllCFormaPagoCfdiBySClave(pago.getFormaDePagoP());
					
					PdfPCell pcFechaPago = new PdfPCell(new Paragraph(
							utilsService.toXmlGregorianCalendar(pago.getFechaPago(), "yyyy-MM-dd'T'HH:mm:ss"),
							fuenteContenidoImporTab));
					PdfPCell pcFormaPago = new PdfPCell(new Paragraph(pago.getFormaDePagoP()
							+ " " + cFormaPagoCFDIDto.getSformapagocfdi(), fuenteContenidoImporTab));
					PdfPCell pcMonedaPeso = new PdfPCell(
							new Paragraph(pago.getMonedaP()+"", fuenteContenidoImporTab));
					PdfPCell pcMontoTotal = new PdfPCell(
							new Paragraph(strMonto, fuenteContenidoImporTab));

//					PdfPCell pcCuentaOrden = new PdfPCell(
//							new Paragraph( pago.getRfcEmisorCtaOrd() != null ? "RFC Banco: " + pago.getRfcEmisorCtaOrd() : "", fuenteContenidoImporTab));
//					
//					PdfPCell pcNombreBanco = new PdfPCell(
//							new Paragraph( pago.getNomBancoOrdExt() != null ? "Nombre Banco: " + pago.getNomBancoOrdExt() : "", fuenteContenidoImporTab));
//					
//					
//					PdfPCell pcCuentaOrdenante = new PdfPCell(
//							new Paragraph(pago.getCtaOrdenante() != null ? "No Cuenta o CLABE: " + pago.getCtaOrdenante() : "", fuenteContenidoImporTab));
//					

					pcFechaPago.setBackgroundColor(colorFondoContenidoFact);
					pcFormaPago.setBackgroundColor(colorFondoContenidoFact);
					pcMonedaPeso.setBackgroundColor(colorFondoContenidoFact);
					pcMontoTotal.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrden.setBackgroundColor(colorFondoContenidoFact);
//					pcNombreBanco.setBackgroundColor(colorFondoContenidoFact);
//					pcCuentaOrdenante.setBackgroundColor(colorFondoContenidoFact);
					
					pcFechaPago.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcFechaPago.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMonedaPeso.setVerticalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
					pcMontoTotal.setVerticalAlignment(Element.ALIGN_CENTER);

					pcFechaPago.setBorder(Rectangle.UNDEFINED);
					pcFormaPago.setBorder(Rectangle.UNDEFINED);
					pcMonedaPeso.setBorder(Rectangle.UNDEFINED);
					pcMontoTotal.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrden.setBorder(Rectangle.TOP);
//					pcCuentaOrden.setBorderWidth(2.5f);
//					pcCuentaOrden.setBorderColor(WebColors.getRGBColor("#FFFFFF"));
//					pcNombreBanco.setBorder(Rectangle.UNDEFINED);
//					pcCuentaOrdenante.setBorder(Rectangle.UNDEFINED);

//					    pcMontoTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
					pcFechaPago.setColspan(2);
					pcFormaPago.setColspan(4);
					pcMontoTotal.setColspan(2);
//					pcCuentaOrden.setColspan(3);
//					pcNombreBanco.setColspan(3);
//					pcCuentaOrdenante.setColspan(3);

					tabDetalleMontosTotales.addCell(pcFechaPago);
					tabDetalleMontosTotales.addCell(pcFormaPago);
					tabDetalleMontosTotales.addCell(pcMonedaPeso);
					tabDetalleMontosTotales.addCell(pcMontoTotal);
					
//					tabDetalleMontos.addCell(pcCuentaOrden);
//					
//					tabDetalleMontos.addCell(pcNombreBanco);
//					
//					tabDetalleMontos.addCell(pcCuentaOrdenante);
//					
//
//					tabDetalleMontos.setWidthPercentage(101);
//					tabDetalleMontos.setHorizontalAlignment(0);
//
//					tabDetalleMontos.setHeaderRows(2);
//					tabDetalleMontos.setFooterRows(2);
					tabDetalleMontosTotales.setTotalWidth(530);

					logger.info("Pago.DoctoRelacionado:::" + pago.getDoctoRelacionado().size());
					/*
					 * Dcomentos relacionados
					 */
					int limit = 7;
					int contPagos = 0;
					for (int i = 0 ; i < pago.getDoctoRelacionado().size(); i++) {
						if(limit == contPagos) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
							tabDatosComplemento = new PdfPTable(9);
							PdfContentByte canvasMontos = writerSwiss.getDirectContent();
							tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
							tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
							reporteSwisslab.newPage();
							reporteSwisslab.add(tabDatosFactura);
							contPagos = 0;
						}
						Pago.DoctoRelacionado docRelacionado = pago.getDoctoRelacionado().get(i);
						Pago.DoctoRelacionado.ImpuestosDR.TrasladosDR.TrasladoDR trasladoDR = pago.getDoctoRelacionado().get(i).getImpuestosDR().getTrasladosDR().getTrasladoDR().get(0);
						PdfPCell espacioBlanco = new PdfPCell(new Paragraph(" ", fuenteContenidoTab));

						PdfPCell pcIdDocumento = new PdfPCell(
								new Paragraph(docRelacionado.getIdDocumento(), fuenteContenidoTabIDDoc));
						PdfPCell pcSerieFolio = new PdfPCell(new Paragraph(
								docRelacionado.getSerie() + "-" + docRelacionado.getFolio(), fuenteContenidoTab));
//				    int intCantidad = new Double(infConcepto.getCantidad().toString()).intValue();
						PdfPCell pcMoneda = new PdfPCell(
								new Paragraph(docRelacionado.getMonedaDR().value(), fuenteContenidoTab));
//						PdfPCell pcMetodoPago = new PdfPCell(
//								new Paragraph(docRelacionado.getMetodoDePagoDR().value(), fuenteContenidoTab));
						PdfPCell pcParcialidad = new PdfPCell(new Paragraph(
								docRelacionado.getNumParcialidad().intValue() + "", fuenteContenidoTab));
						PdfPCell pcSaldoAnterior = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoAnt()), fuenteContenidoTab));
						
						PdfPCell pcImportePagado = new PdfPCell(
								new Paragraph(df.format(docRelacionado.getImpPagado()), fuenteContenidoTab));
						PdfPCell pcSaldoInsoluto = new PdfPCell(new Paragraph(
								df.format(docRelacionado.getImpSaldoInsoluto()), fuenteContenidoTab));
						PdfPCell pcObjetoImpuesto = new PdfPCell(
								new Paragraph("Si Objeto de Impuesto", fuenteContenidoTab));

						pcIdDocumento.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setColspan(2);
						pcSerieFolio.setBorder(Rectangle.UNDEFINED);
						pcMoneda.setBorder(Rectangle.UNDEFINED);						
						pcParcialidad.setBorder(Rectangle.UNDEFINED);
						pcSaldoAnterior.setBorder(Rectangle.UNDEFINED);
						pcImportePagado.setBorder(Rectangle.UNDEFINED);
						pcSaldoInsoluto.setBorder(Rectangle.UNDEFINED);
						pcObjetoImpuesto.setBorder(Rectangle.UNDEFINED);
						pcIdDocumento.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSerieFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcMoneda.setHorizontalAlignment(Element.ALIGN_CENTER);						
						pcParcialidad.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoAnterior.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImportePagado.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcSaldoInsoluto.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcObjetoImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcIdDocumento);
						tabDatosComplemento.addCell(pcSerieFolio);
						tabDatosComplemento.addCell(pcMoneda);
						tabDatosComplemento.addCell(pcParcialidad);
						tabDatosComplemento.addCell(pcSaldoAnterior);
						tabDatosComplemento.addCell(pcImportePagado);
						tabDatosComplemento.addCell(pcSaldoInsoluto);
						tabDatosComplemento.addCell(pcObjetoImpuesto);

						tabDatosComplemento.setTotalWidth(530);
						
						
						Chunk cTextoImpuestp = new Chunk("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n",fuenteContenidoTabNota);
			            Chunk cBaseDr = new Chunk(df.format(trasladoDR.getBaseDR()),fuenteContenidoTab);
			            Paragraph pgBaseDr = new Paragraph();
			            pgBaseDr.add(cTextoImpuestp);
			            pgBaseDr.add(cBaseDr);
			            PdfPCell pcBase = new PdfPCell(pgBaseDr);
						
//						PdfPCell pcBase = new PdfPCell(
//								new Paragraph("IMPUESTOS DEL DOCUMENTO RELACIONADO\r\n"+ trasladoDR.getBaseDR().toPlainString(), fuenteContenidoTabIDDoc));
						PdfPCell pcImpuesto = new PdfPCell(new Paragraph(
								"\r\nIVA", fuenteContenidoTab));
						PdfPCell pcTipoFactor = new PdfPCell(new Paragraph(
								"\r\n"+trasladoDR.getTipoFactorDR().value(), fuenteContenidoTab));					
						PdfPCell pcTasaCuota = new PdfPCell(
								new Paragraph("\r\n"+trasladoDR.getTasaOCuotaDR().toPlainString(), fuenteContenidoTab));
						PdfPCell pcImporteP = new PdfPCell(
								new Paragraph("\r\n"+df.format(trasladoDR.getImporteDR()), fuenteContenidoTab));

						pcBase.setBorder(Rectangle.UNDEFINED);
						pcBase.setColspan(2);
						pcImpuesto.setBorder(Rectangle.UNDEFINED);
						pcImpuesto.setColspan(2);				
						pcTipoFactor.setBorder(Rectangle.UNDEFINED);
						pcTipoFactor.setColspan(2);
						pcTasaCuota.setBorder(Rectangle.UNDEFINED);
						pcTasaCuota.setColspan(2);
						pcImporteP.setBorder(Rectangle.UNDEFINED);
						pcBase.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImpuesto.setHorizontalAlignment(Element.ALIGN_CENTER);					
						pcTipoFactor.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcTasaCuota.setHorizontalAlignment(Element.ALIGN_CENTER);
						pcImporteP.setHorizontalAlignment(Element.ALIGN_CENTER);

						tabDatosComplemento.addCell(pcBase);
						tabDatosComplemento.addCell(pcImpuesto);
						tabDatosComplemento.addCell(pcTipoFactor);
						tabDatosComplemento.addCell(pcTasaCuota);
						tabDatosComplemento.addCell(pcImporteP);

						tabDatosComplemento.setTotalWidth(530);
						
						
						
						if((i  % 25) == 0 && i != 0){
						PdfContentByte canvas = writerSwiss.getDirectContent();
						tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						tabDatosComplemento = new PdfPTable(9);
						PdfContentByte canvasMontos = writerSwiss.getDirectContent();
						tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
						tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvasMontos);
						reporteSwisslab.newPage();
						reporteSwisslab.add(tabDatosFactura);
						}if(i == 0 &&  pago.getDoctoRelacionado().size() == 1) {
							PdfContentByte canvas = writerSwiss.getDirectContent();
							tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
						}else {
							countPage = i;
						}
						logger.info("tabDatosComplemento:::");
						contPagos++;
					}
					if((countPage  % 25) != 0){
					PdfContentByte canvas = writerSwiss.getDirectContent();
					tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
					}
				}
			}
		
	}
		if((countPage  % 25) != 0){
		PdfContentByte canvas = writerSwiss.getDirectContent();
		tabDatosComplemento.writeSelectedRows(0, -1, 35f, 475f, canvas);
		}
		if (tamanioCOncepto == bandera) {
			PdfContentByte canvas = writerSwiss.getDirectContent();
			tabDetalleMontosTotales.writeSelectedRows(0, -1, 35f, 528f, canvas);
			tabDetalleMontos.writeSelectedRows(0, -1, 35f, 170f, canvas);
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
