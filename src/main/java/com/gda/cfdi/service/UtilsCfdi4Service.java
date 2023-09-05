package com.gda.cfdi.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import mx.gob.sat.addenda.AddendaEmpresa;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.pagos20.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class UtilsCfdi4Service {
	
	private static final Logger log = LoggerFactory.getLogger(UtilsCfdi4Service.class);
	
	@Autowired
	private Environment env;
	
	public String createXmlFromComprobante(Comprobante comprobante) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		if(comprobante.getComplemento()!=null) {
			List<Object> listComplementos = comprobante.getComplemento().getAny();
			for (Object object : listComplementos) {
				if (object instanceof TimbreFiscalDigital) {
					classesMarshall.add(TimbreFiscalDigital.class);
				}
			}			
		}
		if(comprobante.getAddenda()!=null) {
			List<Object> listAddendas = comprobante.getAddenda().getAny();
			for (Object object : listAddendas) {
				if (object instanceof AddendaEmpresa) {
					classesMarshall.add(AddendaEmpresa.class);
				}
			}			
		}
		
		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd");
		StringWriter sw = new StringWriter();
		marshaller.marshal(comprobante, sw);
		xml = sw.toString();
		xml = StringUtils.replace(xml, "'", "&apos;").replace( "&amp;apos;", "&apos;");

		log.info("log de prueba 1");
		return xml;
	}
	
	public String createXmlFromComplementoPago(Comprobante comprobante) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		if(comprobante.getComplemento()!=null) {
			List<Object> listComplementos = comprobante.getComplemento().getAny();
			for (Object object : listComplementos) {
				if (object instanceof TimbreFiscalDigital) {
					classesMarshall.add(TimbreFiscalDigital.class);
				}
			}			
		}
		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd http://www.sat.gob.mx/Pagos20 http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos20.xsd");

		StringWriter sw = new StringWriter();
		marshaller.marshal(comprobante, sw);
		xml = sw.toString();
		xml = StringUtils.replace(xml, "'", "&apos;").replace( "&amp;apos;", "&apos;");
		return xml;
	}	
	
	public Comprobante createComprobanteFromXml(String xml) throws JAXBException {
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
		StringReader reader = new StringReader(this.fixXmlV4(xml));
		comprobante = (Comprobante) unmarshaller.unmarshal(reader);
		return comprobante;
	}
		
	public static String fixXmlV4(String xml) {
		xml = xml.replace("/cfd/4\"xmlns", "/cfd/4\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/4 http", "cfd/4 http");
		return xml;
	}
	
}
