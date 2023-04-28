package com.gda.cfdi.pdf.service;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import mx.gob.sat.addenda.AddendaEmpresa;
import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class UtilsService {
	
private static final Logger log = LoggerFactory.getLogger(UtilsService.class);
	
	@Autowired
	private Environment env;
	
	public String toXmlGregorianCalendar (XMLGregorianCalendar xmlDate, String format) {
		try {
			Date date = xmlDate.toGregorianCalendar().getTime();		
			return this.toStringFormat(date, format);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String toStringFormat(Date date, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setLenient(false);
			return formatter.format(date);
		} catch (Exception e) {
			return "";
		}
		
	}

	public Comprobante createComplementoFromXml(String xml) throws JAXBException {
		Comprobante comprobante = null;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();

		if (xml.toLowerCase().contains("<tfd:TimbreFiscalDigital".toLowerCase())) {
			classesMarshall.add(TimbreFiscalDigital.class);
		}
		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(this.fixXml(xml));
		comprobante = (Comprobante) unmarshaller.unmarshal(reader);

		return comprobante;
	}
	
	public mx.gob.sat.cfd._4.Comprobante createComprobanteFromXml(String xml) throws JAXBException {
		mx.gob.sat.cfd._4.Comprobante comprobante = null;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		if (xml.toLowerCase().contains("<tfd:TimbreFiscalDigital".toLowerCase())) {
			classesMarshall.add(TimbreFiscalDigital.class);
		}
		if (xml.toLowerCase().contains("<ae:AddendaEmpresa".toLowerCase())) {
			
			classesMarshall.add(AddendaEmpresa.class);
		}
		classesMarshall.add(mx.gob.sat.cfd._4.Comprobante.class);
		classesMarshall.add(mx.gob.sat.pagos20.Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(this.fixXmlV4(xml));
		comprobante = (mx.gob.sat.cfd._4.Comprobante) unmarshaller.unmarshal(reader);
		return comprobante;
	}
	
	public static String fixXml(String xml) {
		xml = xml.replace("/cfd/3\"xmlns", "/cfd/3\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/3 http", "cfd/3 http");
		return xml;
	}
	
	public static String fixXmlV4(String xml) {
		xml = xml.replace("/cfd/4\"xmlns", "/cfd/4\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/4 http", "cfd/4 http");
		return xml;
	}
	
	public String formatoFolio(int ufolio, int longCadena) {
		String folio = String.valueOf(ufolio);
		int value = longCadena - folio.length();
		for (int i = 1; i <= value; i++) {
			folio = "0" + folio;
		}
		return folio;
	}
}
