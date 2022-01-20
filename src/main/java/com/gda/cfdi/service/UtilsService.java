package com.gda.cfdi.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.cancelacion.Cancelacion;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.terceros.PorCuentadeTerceros;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class UtilsService {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiCancenlacionService.class);
	
	@Autowired
	private Environment env;
	
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
	
	private static String fixXml(String xml) {
		xml = xml.replace("/cfd/3\"xmlns", "/cfd/3\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/3 http", "cfd/3 http");
		return xml;
	}

	public  XMLGregorianCalendar toXmlGregorianCalendar(Date date, String format) throws DatatypeConfigurationException {
	    return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat(format).format(date));
	}
	
	
	public String createXmlFromCancelacion(Cancelacion cancelacion) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		classesMarshall.add(Cancelacion.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
//				"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/terceros http://www.sat.gob.mx/sitio_internet/cfd/terceros/terceros11.xsd");
		StringWriter sw = new StringWriter();
		marshaller.marshal(cancelacion, sw);
		xml = sw.toString();
		return xml;
	}
	
	public DatosMarcaDto obtenerDatosMarca(Integer marca, Integer csucursal, boolean bandAzteca){
		String sucursalesPrado = env.getProperty("list.sucursal.jenner.prado");
		String sucursalesLean = env.getProperty("list.sucursal.jenner.lean");
		List<String> listPrado = new ArrayList<String>(Arrays.asList(sucursalesPrado.split(",")));
		List<String> listLean = new ArrayList<String>(Arrays.asList(sucursalesLean.split(",")));
		
		String rutaCadenaOriginal = env.getProperty("path.file.cadena.original");
		String pasword = "";
		String rfcMarca = "";
		String razonSocialMarca = "";
		String numeroCertificado = "";
		String rutaKey = "";
		String rutaCer = "";
		switch (marca) {
		case 1:
			log.info("**** OLAB *****");
			rutaKey = env.getProperty("path.file.key.olab");
			rutaCer = env.getProperty("path.file.cer.olab");		
			pasword = env.getProperty("password.cer.olab");	
			rfcMarca = env.getProperty("rfc.marca.olab");	
			razonSocialMarca = env.getProperty("razon.social.olab");
			numeroCertificado = env.getProperty("numero.certificado.olab");
			break;
		case 4:
			log.info("**** AZTECA *****");
			rutaKey = env.getProperty("path.file.key.azteca");
			rutaCer = env.getProperty("path.file.cer.azteca");		
			pasword = env.getProperty("password.cer.azteca");	
			rfcMarca = env.getProperty("rfc.marca.azteca");	
			razonSocialMarca = env.getProperty("razon.social.azteca");
			numeroCertificado = env.getProperty("numero.certificado.azteca");
			break;
		case 7:
			log.info("**** JENNER *****");
			if(bandAzteca){
				log.info("**** JENNER Timbrado con Azteca *****");
				rutaKey = env.getProperty("path.file.key.azteca");
				rutaCer = env.getProperty("path.file.cer.azteca");		
				pasword = env.getProperty("password.cer.azteca");	
				rfcMarca = env.getProperty("rfc.marca.azteca");	
				razonSocialMarca = env.getProperty("razon.social.azteca");
				numeroCertificado = env.getProperty("numero.certificado.azteca");			
			}else{				
				if(listPrado.contains(csucursal.toString())){
					log.info("**** PRADO *****");
					rutaKey = env.getProperty("path.file.key.prado");
					rutaCer = env.getProperty("path.file.cer.prado");		
					pasword = env.getProperty("password.cer.prado");	
					rfcMarca = env.getProperty("rfc.marca.prado");	
					razonSocialMarca = env.getProperty("razon.social.prado");
					numeroCertificado = env.getProperty("numero.certificado.prado");	
				}else if(listLean.contains(csucursal.toString())){
					log.info("**** LEAN *****");
					rutaKey = env.getProperty("path.file.key.lean");
					rutaCer = env.getProperty("path.file.cer.lean");		
					pasword = env.getProperty("password.cer.lean");	
					rfcMarca = env.getProperty("rfc.marca.lean");	
					razonSocialMarca = env.getProperty("razon.social.lean");
					numeroCertificado = env.getProperty("numero.certificado.lean");
				}
			}
			break;
		case 5:
			log.info("**** SWISSLAB *****");
			rutaKey = env.getProperty("path.file.key.swisslab");
			rutaCer = env.getProperty("path.file.cer.swisslab");		
			pasword = env.getProperty("password.cer.swisslab");	
			rfcMarca = env.getProperty("rfc.marca.swisslab");	
			razonSocialMarca = env.getProperty("razon.social.swisslab");
			numeroCertificado = env.getProperty("numero.certificado.swisslab");
			break;			
		case 15:
			log.info("**** LIACSA *****");
			rutaKey = env.getProperty("path.file.key.swisslab");
			rutaCer = env.getProperty("path.file.cer.swisslab");		
			pasword = env.getProperty("password.cer.swisslab");	
			rfcMarca = env.getProperty("rfc.marca.swisslab");	
			razonSocialMarca = env.getProperty("razon.social.swisslab");
			numeroCertificado = env.getProperty("numero.certificado.swisslab");
			break;		
		default:
			break;
		}
		
		DatosMarcaDto datosMarcaDto= new DatosMarcaDto(rutaCadenaOriginal, pasword, rfcMarca, 
				razonSocialMarca, numeroCertificado, rutaKey, rutaCer);
		return datosMarcaDto;
	}
}
