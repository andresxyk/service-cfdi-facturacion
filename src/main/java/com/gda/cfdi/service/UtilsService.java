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
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import com.gda.cfdi.dto.cancelacion.Cancelacion;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class UtilsService {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiCancenlacionService.class);
	
	@Autowired
	private Environment env;
	
	
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
	
}
