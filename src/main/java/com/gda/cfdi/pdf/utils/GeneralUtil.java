package com.gda.cfdi.pdf.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import mx.gob.sat.addenda.AddendaEmpresa;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.pagos20.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class GeneralUtil {
	
	final static Logger log = LogManager.getLogger(GeneralUtil.class);
	
	@Autowired
	private Environment env;

	public String generarCadenaOriginalTFD(String xml) {
		log.info(xml);
		String cadenaOriginal = "";
		File xslt = new File(env.getProperty("path.file.cadena.original.tfd"));
		StreamSource sourceXSL = new StreamSource(xslt);		
		StreamSource sourceXML = new StreamSource(new StringReader(xml));
		byte[] output1 = null;
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(sourceXSL);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			transformer.transform(sourceXML, new StreamResult(output));
			output1 = output.toString().getBytes("UTF-8");
			cadenaOriginal = new String(output1);
		} catch (Exception e) {
			System.out.println("Error de codificacion: " + e.getMessage());
			e.printStackTrace();
		}
		return cadenaOriginal;
	}
	
	public TimbreFiscalDigital getTimbreFiscalDigital(Comprobante comprobante) {
		TimbreFiscalDigital timbreFiscalDigital = null;
		for(Object obj : comprobante.getComplemento().getAny()) {
			log.info("TimbreFiscalDigital.DoctoRelacionado:::" + (obj instanceof TimbreFiscalDigital ? "si" : "no"));
			if(obj instanceof TimbreFiscalDigital) {
				timbreFiscalDigital = (TimbreFiscalDigital) obj;
				break;
			}
		}
		if(timbreFiscalDigital!=null) {
			timbreFiscalDigital.setXmlns(null);
			return timbreFiscalDigital;			
		}else {
			return null;
		}
	}
	
	public String getXMLTimbreFiscalDigital(TimbreFiscalDigital tfd) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		classesMarshall.add(TimbreFiscalDigital.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
//				"http://www.sat.gob.mx/TimbreFiscalDigital http://www.sat.gob.mx/sitio_internet/cfd/TimbreFiscalDigital/TimbreFiscalDigitalv11.xsd");
		StringWriter sw = new StringWriter();
		marshaller.marshal(tfd, sw);
		xml = sw.toString();
		return xml;
	}
}
