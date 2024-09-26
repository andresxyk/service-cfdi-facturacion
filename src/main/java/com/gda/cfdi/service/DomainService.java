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
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.gda.cfdi.dto.DatosMarcaDto;

@Service
public class DomainService {

	private static final Logger log = LoggerFactory.getLogger(DomainService.class);
	
	@Autowired
	private Environment env;
	
	public DatosMarcaDto obtenerDatosRfcEmisor(String rfcEmisor) {
		String rutaCadenaOriginal = env.getProperty("path.file.cadena.original");
		String pasword = "";
		String rfcMarca = "";
		String razonSocialMarca = "";
		String numeroCertificado = "";
		String rutaKey = "";
		String rutaCer = "";
		
		switch (rfcEmisor) {
		case "ECD741021QA5":
			log.info("**** OLAB *****");
			rutaKey = env.getProperty("path.file.key.olab");
			rutaCer = env.getProperty("path.file.cer.olab");		
			pasword = env.getProperty("password.cer.olab");	
			rfcMarca = env.getProperty("rfc.marca.olab");	
			razonSocialMarca = env.getProperty("razon.social.olab");
			numeroCertificado = env.getProperty("numero.certificado.olab");
			break;
		case "LQC920131M20":
			log.info("**** AZTECA *****");
			rutaKey = env.getProperty("path.file.key.azteca");
			rutaCer = env.getProperty("path.file.cer.azteca");		
			pasword = env.getProperty("password.cer.azteca");	
			rfcMarca = env.getProperty("rfc.marca.azteca");	
			razonSocialMarca = env.getProperty("razon.social.azteca");
			numeroCertificado = env.getProperty("numero.certificado.azteca");
			break;
		case "LCP061017PA9":
			log.info("**** PRADO *****");
			rutaKey = env.getProperty("path.file.key.prado");
			rutaCer = env.getProperty("path.file.cer.prado");		
			pasword = env.getProperty("password.cer.prado");	
			rfcMarca = env.getProperty("rfc.marca.prado");	
			razonSocialMarca = env.getProperty("razon.social.prado");
			numeroCertificado = env.getProperty("numero.certificado.prado");
			break;
		case "LCL050622DD9":
			log.info("**** LEAN *****");
			rutaKey = env.getProperty("path.file.key.lean");
			rutaCer = env.getProperty("path.file.cer.lean");		
			pasword = env.getProperty("password.cer.lean");	
			rfcMarca = env.getProperty("rfc.marca.lean");	
			razonSocialMarca = env.getProperty("razon.social.lean");
			numeroCertificado = env.getProperty("numero.certificado.lean");
			break;		
		case "SWI1201268J8":
			log.info("**** SWISSLAB *****");
			rutaKey = env.getProperty("path.file.key.swisslab");
			rutaCer = env.getProperty("path.file.cer.swisslab");		
			pasword = env.getProperty("password.cer.swisslab");	
			rfcMarca = env.getProperty("rfc.marca.swisslab");	
			razonSocialMarca = env.getProperty("razon.social.swisslab");
			numeroCertificado = env.getProperty("numero.certificado.swisslab");
			break;	
		case "LAB020416Q67":
			log.info("**** FAMILYLABSNORTE *****");
			rutaKey = env.getProperty("path.file.key.familylabsnorte");
			rutaCer = env.getProperty("path.file.cer.familylabsnorte");		
			pasword = env.getProperty("password.cer.familylabsnorte");	
			rfcMarca = env.getProperty("rfc.marca.familylabsnorte");	
			razonSocialMarca = env.getProperty("razon.social.familylabsnorte");
			numeroCertificado = env.getProperty("numero.certificado.familylabsnorte");
			break;	
		case "AEL9703115B0":
			log.info("**** Asesores del Sur *****");
			rutaKey = env.getProperty("path.file.key.asesoressur");
			rutaCer = env.getProperty("path.file.cer.asesoressur");		
			pasword = env.getProperty("password.cer.asesoressur");	
			rfcMarca = env.getProperty("rfc.marca.asesoressur");	
			razonSocialMarca = env.getProperty("razon.social.asesoressur");
			numeroCertificado = env.getProperty("numero.certificado.asesoressur");
			break;	
		case "LBA840320GC8":
			log.info("**** Exakta Laboartorio *****");
			rutaKey = env.getProperty("path.file.key.exakta.lab");
			rutaCer = env.getProperty("path.file.cer.exakta.lab");		
			pasword = env.getProperty("password.cer.exakta.lab");	
			rfcMarca = env.getProperty("rfc.marca.exakta.lab");	
			razonSocialMarca = env.getProperty("razon.social.exakta.lab");
			numeroCertificado = env.getProperty("numero.certificado.exakta.lab");
			break;
		case "IME0103012D3":
			log.info("**** Exakta Imagen *****");
			rutaKey = env.getProperty("path.file.key.exakta.imagen");
			rutaCer = env.getProperty("path.file.cer.exakta.imagen");		
			pasword = env.getProperty("password.cer.exakta.imagen");	
			rfcMarca = env.getProperty("rfc.marca.exakta.imagen");	 
			razonSocialMarca = env.getProperty("razon.social.exakta.imagen");
			numeroCertificado = env.getProperty("numero.certificado.exakta.imagen");
			break;
		case "BIO7603164H0":
			log.info("**** Moreira *****");
			rutaKey = env.getProperty("path.file.key.moreira");
			rutaCer = env.getProperty("path.file.cer.moreira");		
			pasword = env.getProperty("password.cer.moreira");	
			rfcMarca = env.getProperty("rfc.marca.moreira");	
			razonSocialMarca = env.getProperty("razon.social.moreira");
			numeroCertificado = env.getProperty("numero.certificado.moreira");
			break;	
		case "RFCPOLAB0000":
			log.info("**** Polab *****");
			rutaKey = env.getProperty("path.file.key.polab");
			rutaCer = env.getProperty("path.file.cer.polab");		
			pasword = env.getProperty("password.cer.polab");	
			rfcMarca = env.getProperty("rfc.marca.polab");	
			razonSocialMarca = env.getProperty("razon.social.polab");
			numeroCertificado = env.getProperty("numero.certificado.polab");
			break;	
		case "BRE9205181I1":
			log.info("**** Biomedica Referencia *****");
			rutaKey = env.getProperty("path.file.key.referencia");
			rutaCer = env.getProperty("path.file.cer.referencia");		
			pasword = env.getProperty("password.cer.referencia");	
			rfcMarca = env.getProperty("rfc.marca.referencia");	
			razonSocialMarca = env.getProperty("razon.social.referencia");
			numeroCertificado = env.getProperty("numero.certificado.referencia");
			break;
		case "RFCPROMEDIC0":
			log.info("**** Promedic *****");
			rutaKey = env.getProperty("path.file.key.promedic");
			rutaCer = env.getProperty("path.file.cer.promedic");		
			pasword = env.getProperty("password.cer.promedic");	
			rfcMarca = env.getProperty("rfc.marca.promedic");	
			razonSocialMarca = env.getProperty("razon.social.promedic");
			numeroCertificado = env.getProperty("numero.certificado.promedic");
			break;
			
			
		default:
			break;
		}
		DatosMarcaDto datosMarcaDto = new DatosMarcaDto(rutaCadenaOriginal, pasword, rfcMarca, 
				razonSocialMarca, numeroCertificado, rutaKey, rutaCer);
		return datosMarcaDto;
	}
	
	public String getXMLCancelacionConFirmaDigital(String xmlCancelacion,DatosMarcaDto datosMarcaDto) throws Exception {
		log.info("ObtenerFirmaDigial*****");
		X509Certificate x509 = null;
		x509 = getX509Certificate(new File(datosMarcaDto.getRutaCer()));
		log.info("x509--->>>   " + x509);
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		Reference ref = fac.newReference("", fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", null),
				Collections.singletonList(
						fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (XMLStructure) null)),
				null, null);
		log.info("ref:::   " + ref);
		SignedInfo si = fac.newSignedInfo(
				fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (XMLStructure) null),
				fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null),
				Collections.singletonList(ref));
		log.info("si-->>>   " + si);
		PrivateKey privateKey = null;
		privateKey = getPrivateKey(new File(datosMarcaDto.getRutaKey()),datosMarcaDto.getPasword());
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		log.info("kif--->>>  " + kif);
		List<Object> x509Content = new ArrayList();
		X509IssuerSerial issuer = kif.newX509IssuerSerial(x509.getIssuerX500Principal().getName(),
				x509.getSerialNumber());
		log.info("issuer--->>>   " + issuer);
		x509Content.add(x509.getSubjectX500Principal().getName());
		x509Content.add(issuer);
		x509Content.add(x509);
		X509Data xd = kif.newX509Data(x509Content);
		log.info("xd-->>  " + xd);
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
		log.info("ki-->>>  " + ki);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlCancelacion)));
		log.info("doc-->>>   " + doc);
		DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
		log.info("dsc:::  " + dsc);
		XMLSignature signature = fac.newXMLSignature(si, ki);
		log.info("signature-->>>   " + signature);
		signature.sign(dsc);
		TransformerFactory tf = TransformerFactory.newInstance();
		log.info("tf-->>  " + tf);
		Transformer trans = tf.newTransformer();
		log.info("trans-->>   " + trans);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		log.info("output::::   " + output);
		trans.transform(new DOMSource(doc), new StreamResult(output));
		log.info("*************output.toString()--->>>>    " + output.toString());
		return output.toString();
	}
	
	private X509Certificate getX509Certificate(File certificado)
			throws IOException, CertificateException, java.security.cert.CertificateException {
		log.info("Obtener Certificado:: ");
		FileInputStream is = null;
		try {
			is = new FileInputStream(certificado);
			log.info("is-->>  " + is);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			log.info("cf:::   " + cf);
			return (X509Certificate) cf.generateCertificate(is);
		} finally {
			is.close();
		}
	}
	
	private PrivateKey getPrivateKey(File key, String password) throws IOException, GeneralSecurityException {
		log.info("Obtener Ckey-->>  ");
		FileInputStream in = new FileInputStream(key);
		log.info("in-->>  " + in);
		PKCS8Key pkcs8 = new PKCS8Key(in, password.toCharArray());
		log.info("pkcs8-->>   " + pkcs8);
		byte[] decrypted = pkcs8.getDecryptedBytes();
		log.info("decrypted-->>>  " + decrypted);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypted);
		log.info("spec-->>>   " + spec);
		PrivateKey pk = null;
		if (pkcs8.isDSA()) {
			pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
		} else if (pkcs8.isRSA()) {
			pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
		}
		log.info("pk-->>>  " + pk);
		pk = pkcs8.getPrivateKey();
		log.info("pk--->>>   " + pk);
		return pk;
	}
}
