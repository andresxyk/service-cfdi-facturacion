package com.gda.cfdi.pdf.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PaginacionPdf extends PdfPageEventHelper {

    protected PdfTemplate total;

    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        int paginaActual = writer.getPageNumber();
        int totalPaginas = writer.getPageNumber() - 1;
        Rectangle rect = writer.getPageSize();
        float margenInferior = document.bottomMargin();
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        
        Phrase footer = new Phrase(String.format("Página %d de %d", paginaActual, totalPaginas), footerFont);
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, footer,
                (rect.getLeft() + rect.getRight()) / 2, margenInferior - 10, 0);

        total.beginText();
        
        total.setFontAndSize(footerFont.getCalculatedBaseFont(false), 10);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(totalPaginas));
        total.endText();
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_RIGHT, new Phrase(new Chunk()),
                (rect.getRight() - 20), margenInferior - 10, 0);
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        total.beginText();
        total.setFontAndSize(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL).getCalculatedBaseFont(false), 10);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber() - 1));
        total.endText();
    }
}