package com.gda.cfdi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.Timestamp;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.encoding.Base64;
import org.apache.commons.ssl.PKCS8Key;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.FacturaSelloDto;
import com.gda.cfdi.dto.cancelacion.Cancelacion;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.pagos20.Pagos.Pago;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class UtilsService {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiCancenlacionService.class);
	
	@Autowired
	private Environment env;
	
	public Integer getCEntidadLegalByRFC(String rfc) throws IOException {
		log.info("ejecutando getCEntidadLegalByRFC[rfc: " + rfc + "]");

		Integer centidadlegal = 0;

		String rfcOlab = env.getProperty("rfc.marca.olab");
		String rfcAzteca = env.getProperty("rfc.marca.azteca");
		String rfcSwisslab = env.getProperty("rfc.marca.swisslab");
		String rfcJennerPrado = env.getProperty("rfc.marca.prado");
		String rfcJennerLean = env.getProperty("password.cer.lean");
		
		if (rfc.equals(rfcOlab)) {
			centidadlegal = 1;
		}
		if (rfc.equals(rfcAzteca)) {
			centidadlegal = 5;
		}
		if (rfc.equals(rfcSwisslab)) {
			centidadlegal = 6;
		}
		if (rfc.equals(rfcJennerPrado)) {
			centidadlegal =  7; 
		}
		if (rfc.equals(rfcJennerLean)) {
			centidadlegal =  8; 
		}
		return centidadlegal;
	}
	
	public static Date parseDateTime(String s) {
	    try {
	      DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	      return formatter.parse(s);
	    } catch (ParseException e) {
	      throw new RuntimeException(e);
	    }
	}
	  
	public static String printDateTime(Date dt) {
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    return formatter.format(dt);
	}
	
	
	public boolean validarRFC(String rfc) throws Exception {
		log.info("Validar Fisica/moral " + rfc);
		boolean correcto = false;
		if (rfc.length() == 13) {
			log.info("Validar persona fisica:::  " + rfc);
			correcto = rfc.toUpperCase()
					.matches("[ÑA-Z|&]{4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]");
		} else if (rfc.length() == 12) {
			log.info("Validar persona moral:::  " + rfc);
			correcto = rfc.toUpperCase()
					.matches("[ÑA-Z|&]{3}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]");
		}
		log.info("RFC valido-->><  " + correcto);
		return correcto;
	}
	
	public String createXmlFromComprobante(Comprobante comprobante) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();

		comprobante.getComplemento().forEach(complemento -> {
			complemento.getAny().forEach(object -> {
				if (object instanceof TimbreFiscalDigital) {
					classesMarshall.add(TimbreFiscalDigital.class);
				}
			});
		});

		classesMarshall.add(Comprobante.class);
//		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd");

		StringWriter sw = new StringWriter();
		marshaller.marshal(comprobante, sw);
		xml = sw.toString();

		return xml;
	}
	
	public String createXmlFromComplementoPago(Comprobante comprobante) throws JAXBException {
		String xml;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();

		comprobante.getComplemento().forEach(complemento -> {
			complemento.getAny().forEach(object -> {
				if (object instanceof TimbreFiscalDigital) {
					classesMarshall.add(TimbreFiscalDigital.class);
				}
			});
		});

		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/Pagos http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos10.xsd");

		StringWriter sw = new StringWriter();
		marshaller.marshal(comprobante, sw);
		xml = sw.toString();

		return xml;
	}
	
	public String createSello(String cadenaOriginal, String urlKey, String password)
			throws IOException, GeneralSecurityException {
		String sello;

		FileInputStream fis = new FileInputStream(urlKey);
		byte[] bytesKey = new byte[fis.available()];
		fis.read(bytesKey);
		fis.close();
		PKCS8Key pkcs8 = new PKCS8Key(bytesKey, password.toCharArray());
		KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec pkcs8Encoded = new PKCS8EncodedKeySpec(pkcs8.getDecryptedBytes());
		PrivateKey privateKey = privateKeyFactory.generatePrivate(pkcs8Encoded);
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		byte[] cadenaOriginalByte = cadenaOriginal.getBytes();
		signature.update(cadenaOriginalByte);
		sello = new String(Base64.encode(signature.sign()));

		return sello;
	}
	
	public String createCadenaOriginal(String xml, String pathXslt) throws TransformerException, IOException, JAXBException {
		log.info("ejecutando createCadenaOriginal");
		String cadenaOriginal;

		File xslt = new File(pathXslt);

		StreamSource sourceXSL = new StreamSource(xslt);
		StreamSource sourceXML = new StreamSource(new StringReader(xml));

		byte[] bytesOutput = null;

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(sourceXSL);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		transformer.transform(sourceXML, new StreamResult(baos));
		bytesOutput = baos.toString().getBytes("UTF-8");
		cadenaOriginal = new String(bytesOutput);

		log.info("createCadenaOriginal ejecutado: [cadenaOriginal=" + cadenaOriginal + "]");
		return cadenaOriginal;
	}

	
	public String getCertificadoB64(String urlCer) throws GeneralSecurityException, IOException {
		File file = new File(urlCer);
		byte[] fileArray = new byte[(int) file.length()];
		InputStream inputStream;

		String encodedFile;

		inputStream = new FileInputStream(file);
		inputStream.read(fileArray);
		encodedFile = new String(Base64.encode(fileArray));
		inputStream.close();

		return encodedFile;
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
	
	public mx.gob.sat.cfd._4.Comprobante createComprabanteFromXml4(String xml) throws JAXBException{
		mx.gob.sat.cfd._4.Comprobante comprobante = null;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();
		if (xml.toLowerCase().contains("<tfd:TimbreFiscalDigital".toLowerCase())) {
			classesMarshall.add(TimbreFiscalDigital.class);
		}
		classesMarshall.add(mx.gob.sat.cfd._4.Comprobante.class);
		classesMarshall.add(mx.gob.sat.pagos20.Pagos.Pago.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(this.fixXml(xml));
		comprobante = (mx.gob.sat.cfd._4.Comprobante) unmarshaller.unmarshal(reader);
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
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
//				"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/terceros http://www.sat.gob.mx/sitio_internet/cfd/terceros/terceros11.xsd");
		StringWriter sw = new StringWriter();
		marshaller.marshal(cancelacion, sw);
		xml = sw.toString();
		return xml;
	}
	
	public DatosMarcaDto obtenerDatosMarca(Integer marca){
		String smarca = marca.equals(1)?"olab":marca.equals(4)?"azteca":marca.equals(5)?"swisslab":marca.equals(7)?
				"prado":marca.equals(8)?"lean":marca.equals(15)?"swisslab":"";
		
		String rutaCadenaOriginal = env.getProperty("path.file.cadena.original");
		String password = "";
		String rfcMarca = "";
		String razonSocialMarca = "";
		String numeroCertificado = "";
		String rutaKey = "";
		String rutaCer = "";
		
		rutaKey = env.getProperty("path.file.key." + smarca);
		rutaCer = env.getProperty("path.file.cer." + smarca);
		password = env.getProperty("password.cer." + smarca);	
		rfcMarca = env.getProperty("rfc.marca." + smarca);
		razonSocialMarca = env.getProperty("razon.social." + smarca);
		numeroCertificado = env.getProperty("numero.certificado." + smarca);
		
		DatosMarcaDto datosMarcaDto= new DatosMarcaDto(rutaCadenaOriginal, password, rfcMarca, 
				razonSocialMarca, numeroCertificado, rutaKey, rutaCer);
		return datosMarcaDto;
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
	
	public DatosMarcaDto obtenerDatosMarcaAnticipada(Integer marca, Integer razonSocialJen) {
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
			if(razonSocialJen==7){
				log.info("**** PRADO *****");
				rutaKey = env.getProperty("path.file.key.prado");
				rutaCer = env.getProperty("path.file.cer.prado");		
				pasword = env.getProperty("password.cer.prado");	
				rfcMarca = env.getProperty("rfc.marca.prado");	
				razonSocialMarca = env.getProperty("razon.social.prado");
				numeroCertificado = env.getProperty("numero.certificado.prado");
			}else if(razonSocialJen==8){
				log.info("**** LEAN *****");
				rutaKey = env.getProperty("path.file.key.lean");
				rutaCer = env.getProperty("path.file.cer.lean");		
				pasword = env.getProperty("password.cer.lean");	
				rfcMarca = env.getProperty("rfc.marca.lean");	
				razonSocialMarca = env.getProperty("razon.social.lean");
				numeroCertificado = env.getProperty("numero.certificado.lean");
			}else if(razonSocialJen==9){
				log.info("**** AZTECA *****");
				rutaKey = env.getProperty("path.file.key.azteca");
				rutaCer = env.getProperty("path.file.cer.azteca");		
				pasword = env.getProperty("password.cer.azteca");	
				rfcMarca = env.getProperty("rfc.marca.azteca");	
				razonSocialMarca = env.getProperty("razon.social.azteca");
				numeroCertificado = env.getProperty("numero.certificado.azteca");
			}else if(razonSocialJen==5){
				log.info("**** AZTECA *****");
				rutaKey = env.getProperty("path.file.key.azteca");
				rutaCer = env.getProperty("path.file.cer.azteca");		
				pasword = env.getProperty("password.cer.azteca");	
				rfcMarca = env.getProperty("rfc.marca.azteca");	
				razonSocialMarca = env.getProperty("razon.social.azteca");
				numeroCertificado = env.getProperty("numero.certificado.azteca");
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
	
	public static String toStringFormat(Date date, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setLenient(false);
			return formatter.format(date);
		} catch (Exception e) {
			return "";
		}
		
	}
	
	public static String toStringFormat(Timestamp date, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setLenient(false);
			return formatter.format(date);
		} catch (Exception e) {
			return "";
		}
		
	}
	
	public static Date toDate(String dateStr, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setLenient(false);
			Date date = formatter.parse(dateStr);
			return date;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static LocalDateTime toLocalDateTime(String dateStr, String format) {
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
			LocalDateTime date = formatter.parseLocalDateTime(dateStr);
			return date;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static Date suctracDays(Date date, Integer days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -days);
		return cal.getTime();
	}
	
	
	public static XMLGregorianCalendar toXmlGregorianCalendar (Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			return null;
		}
	}
	
	public static XMLGregorianCalendar toXmlGregorianCalendar2(Date date, String format) throws DatatypeConfigurationException {
	    return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat(format).format(date));
	}
	
	
	public static String toXmlGregorianCalendar (XMLGregorianCalendar xmlDate, String format) {
		try {
			Date date = xmlDate.toGregorianCalendar().getTime();		
			return UtilsService.toStringFormat(date, format);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date sumarORestarMinutosAFecha(Date fecha, int minutos){	
        Calendar calendar = Calendar.getInstance();	
        calendar.setTime(fecha); 	
        calendar.add(Calendar.MINUTE, minutos);  	
        return calendar.getTime(); 	
    }
	
	public String darFormatoCFDI(String cadenaOrigianl){
		String cadenaNormalize = Normalizer.normalize(cadenaOrigianl, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
//		System.out.println("Resultado: " + cadenaSinAcentos);
		return cadenaSinAcentos;
	}
	
	
	public String getRutaXMLByIdSucursal(Integer idSucursal) throws IOException {
		String url;
//		Properties env = loadProperties();
		url = env.getProperty("facturas.documentos.servidor");
		
		switch (idSucursal) {
		case 1013:
			url += env.getProperty("facturas.dowload.folder.swisslab");
			break;
		case 1003:
			url += env.getProperty("facturas.dowload.folder.olab");
			break;
		case 1012:
			url += env.getProperty("facturas.dowload.folder.azteca");
			break;
		case 1014:
			url += env.getProperty("facturas.dowload.folder.prado");
			break;
		case 1015:
			url += env.getProperty("facturas.dowload.folder.lean");
			break;
		default:
			break;
		}
		
		url += env.getProperty("facturas.download.url.xml");
		
		return url;
	}
	
	/**
	 * Devuelve el nombre de los archivos 
	 * para la factura de acuerdo al rfc
	 * y al folio especificados
	 * 
	 * @param rfc
	 * @param folio
	 * @return
	 * @throws IOException 
	 */
	public String getNombreFacturasByIdSucursal(Integer idSucursal, Integer folio) throws IOException {
		String prefijo = null;
		String sufijo = null;
//		Properties env = loadProperties();
		
		switch (idSucursal) {
		case 1013:
			prefijo = env.getProperty("facturas.dowload.prefijo.swisslab");
			sufijo = formatFolio(folio, 7);
			break;
		case 1003:
			prefijo = env.getProperty("facturas.dowload.prefijo.olab");
			sufijo = formatFolio(folio, 8);
			break;
		case 1012:
			prefijo = env.getProperty("facturas.dowload.prefijo.azteca");
			sufijo = formatFolio(folio, 7);
			break;
		case 1014:
			prefijo = env.getProperty("facturas.dowload.prefijo.prado");
			sufijo = formatFolio(folio, 6);
			break;
		case 1015:
			prefijo = env.getProperty("facturas.dowload.prefijo.lean");
			sufijo = formatFolio(folio, 6);
			break;
		default:
			break;
		}

		return prefijo + sufijo;
	}

	/**
	 * Agrega ceros
	 * 
	 * @param ufolio
	 * @param longCadena
	 * @return
	 */
	public static String formatFolio(int ufolio, int longCadena) {
		String folio = String.valueOf(ufolio);
		int value = longCadena - folio.length();
		for (int i = 1; i <= value; i++) {
			folio = "0" + folio;
		}
		return folio;
	}
	
	public Comprobante createComplementoPagoFromXml(String xml) throws JAXBException {
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
	
	public FacturaSelloDto buildSelloFactura(String rfc, String xml, boolean procJenner)
			throws IOException, GeneralSecurityException, TransformerException, JAXBException {
		log.info("ejecutando sellarComprobanteFromRfc[rfc: " + rfc + "]");
		FacturaSelloDto facturaSello;
		String dynamicKeyProperty = null;
		String urlKey, urlCer;
		String password;
		String pathXslt = env.getProperty("path.file.cadena.original");

		String rfcOlab = env.getProperty("rfc.marca.olab");
		String rfcAzteca = env.getProperty("rfc.marca.azteca");
		String rfcSwisslab = env.getProperty("rfc.marca.swisslab");
		String rfcJennerPrado = env.getProperty("rfc.marca.prado");
		String rfcJennerLean = env.getProperty("rfc.marca.lean");
		
		facturaSello = new FacturaSelloDto();
		
		if (rfc.equals(rfcOlab)) {
			facturaSello.setMarca(1);
			dynamicKeyProperty = "olab";
		}
		if (rfc.equals(rfcAzteca)) {
			facturaSello.setMarca(4);
			dynamicKeyProperty = "azteca";
		}
		if (rfc.equals(rfcSwisslab)) {
			facturaSello.setMarca(5);
			dynamicKeyProperty = "swisslab";
		}
		if (rfc.equals("LCP061017PA9")) {
			facturaSello.setMarca(7);
			dynamicKeyProperty =  "prado";	
//			if(procJenner){
//				dynamicKeyProperty =  "prado";				
//			}else{
//				dynamicKeyProperty = "azteca";
//			}
		}
		if (rfc.equals("LCL050622DD9")) {
			facturaSello.setMarca(8);
			dynamicKeyProperty =  "lean"; 
//			if(procJenner){
//				dynamicKeyProperty =  "lean"; 				
//			}else{
//				dynamicKeyProperty = "azteca";				
//			}
		}

		urlKey = env.getProperty("path.file.key." + dynamicKeyProperty );
		urlCer = env.getProperty("path.file.cer." + dynamicKeyProperty );
		password = env.getProperty("password.cer." + dynamicKeyProperty );

		
		facturaSello.setCertificadoB64(getCertificadoB64(urlCer));
		facturaSello.setCadenaOriginal(createCadenaOriginal(xml, pathXslt));
		facturaSello.setSello(createSello(facturaSello.getCadenaOriginal(), urlKey, password));

		return facturaSello;
	}
	
}
