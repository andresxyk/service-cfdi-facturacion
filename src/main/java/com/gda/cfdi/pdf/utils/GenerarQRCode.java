package com.gda.cfdi.pdf.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerarQRCode {
	
	public File generateQR(File file, String text, int h, int w) throws Exception {
		 
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
 
        BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
 
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrix.getWidth(), matrix.getHeight());
        graphics.setColor(Color.BLACK);
 
        for (int i = 0; i < matrix.getWidth(); i++) {
            for (int j = 0; j < matrix.getHeight(); j++) {
                if (matrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
 
        ImageIO.write(image, "png", file);
 
        return file;
 
    }
	
}
