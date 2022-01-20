package com.gda.cfdi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.ssl.PKCS8Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.cancelacion.Cancelacion;
import com.gda.cfdi.dto.cancelacion.Cancelacion.Folios;
import com.gda.cfdi.dto.cancelacion.Cancelacion.Folios.Folio;


@Service
public class CfdiCancenlacionService {

	private static final Logger log = LoggerFactory.getLogger(CfdiCancenlacionService.class);
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	public String generarCfdiCancelacion(String uuid, String rfcEmisor, String uuidSustitucion, String motivo, Integer marca, Integer csucursal) throws Exception {
		
		String requestCancelacion = this.requestCancelacion(uuid, rfcEmisor, uuidSustitucion != "" ? uuidSustitucion : null, motivo,marca,csucursal);
		return requestCancelacion;
	}
	
	private String requestCancelacion(String uuid, String rfcEmisor, String uuidSustitucion, String motivo, Integer marca, Integer csucursal) throws Exception {
		Cancelacion cancelacion = new Cancelacion();		
		cancelacion.setFecha(utilsService.toXmlGregorianCalendar(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
		cancelacion.setRfcEmisor(rfcEmisor);
		Folios folios = new Folios();
		Folio folio = new Folio();
		folio.setUUID(uuid);
		folio.setMotivo(motivo);
		folio.setFolioSustitucion(uuidSustitucion);
		folios.setFolio(folio);
		cancelacion.setFolios(folios);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String xml =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Cancelacion xmlns=\"http://cancelacfd.sat.gob.mx\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Fecha=\""
		+ dateFormat.format(new Date()) + "\" RfcEmisor=\"" + rfcEmisor + "\">" + "<Folios>" + "<Folio UUID=\""+uuid+"\" Motivo=\""+motivo+"\" FolioSustitucion=\""+uuidSustitucion+"\">"  
		+ "</Folio >" + "</Folios>" + "</Cancelacion>";
		log.info("requestCancelacionOriginal==="+xml);
		
		String xml1 = utilsService.createXmlFromCancelacion(cancelacion).replace("xmlns=\"http://cancelacfd.sat.gob.mx\"", "xmlns=\"http://cancelacfd.sat.gob.mx\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		log.info("requestCancelacionOriginal==="+xml1);
		String requestCancelacion = this.getXMLCancelacionConFirmaDigital(xml, marca, csucursal);
		return requestCancelacion;
	}
	
	public String getXMLCancelacionConFirmaDigital(String xmlCancelacion,Integer marca, Integer csucursal) throws Exception {
		System.out.println("ObtenerFirmaDigial*****");
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosMarca(marca, csucursal, true);
		X509Certificate x509 = null;
		x509 = getX509Certificate(new File(datosMarcaDto.getRutaCer()));
		System.out.println("x509--->>>   " + x509);
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		Reference ref = fac.newReference("", fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", null),
				Collections.singletonList(
						fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (XMLStructure) null)),
				null, null);
		System.out.println("ref:::   " + ref);
		SignedInfo si = fac.newSignedInfo(
				fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (XMLStructure) null),
				fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null),
				Collections.singletonList(ref));
		System.out.println("si-->>>   " + si);

		PrivateKey privateKey = null;
		privateKey = getPrivateKey(new File(datosMarcaDto.getRutaKey()),datosMarcaDto.getPasword());
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		System.out.println("kif--->>>  " + kif);
		List<Object> x509Content = new ArrayList();
		X509IssuerSerial issuer = kif.newX509IssuerSerial(x509.getIssuerX500Principal().getName(),
				x509.getSerialNumber());
		System.out.println("issuer--->>>   " + issuer);
		x509Content.add(x509.getSubjectX500Principal().getName());
		x509Content.add(issuer);
		x509Content.add(x509);

		X509Data xd = kif.newX509Data(x509Content);
		System.out.println("xd-->>  " + xd);

		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
		System.out.println("ki-->>>  " + ki);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlCancelacion)));
		System.out.println("doc-->>>   " + doc);
		DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
		System.out.println("dsc:::  " + dsc);
		XMLSignature signature = fac.newXMLSignature(si, ki);
		System.out.println("signature-->>>   " + signature);
		signature.sign(dsc);

		TransformerFactory tf = TransformerFactory.newInstance();
		System.out.println("tf-->>  " + tf);
		Transformer trans = tf.newTransformer();
		System.out.println("trans-->>   " + trans);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.out.println("output::::   " + output);
		trans.transform(new DOMSource(doc), new StreamResult(output));
		System.out.println("*************output.toString()--->>>>    " + output.toString());
		return output.toString();
	}
	
	private X509Certificate getX509Certificate(File certificado)
			throws IOException, CertificateException, java.security.cert.CertificateException {
		System.out.println("Obtener Certificado:: ");
		FileInputStream is = null;
		try {
			is = new FileInputStream(certificado);
			System.out.println("is-->>  " + is);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			System.out.println("cf:::   " + cf);
			return (X509Certificate) cf.generateCertificate(is);
		} finally {
			is.close();
		}
	}
	
	private PrivateKey getPrivateKey(File key, String password) throws IOException, GeneralSecurityException {
		System.out.println("Obtener Ckey-->>  ");
		FileInputStream in = new FileInputStream(key);
		System.out.println("in-->>  " + in);
		PKCS8Key pkcs8 = new PKCS8Key(in, password.toCharArray());
		System.out.println("pkcs8-->>   " + pkcs8);
		byte[] decrypted = pkcs8.getDecryptedBytes();
		System.out.println("decrypted-->>>  " + decrypted);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
		System.out.println("spec-->>>   " + spec);
		PrivateKey pk = null;
		if (pkcs8.isDSA()) {
			pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
		} else if (pkcs8.isRSA()) {
			pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
		}
		System.out.println("pk-->>>  " + pk);
		pk = pkcs8.getPrivateKey();
		System.out.println("pk--->>>   " + pk);
		return pk;
	}
	
	
}
