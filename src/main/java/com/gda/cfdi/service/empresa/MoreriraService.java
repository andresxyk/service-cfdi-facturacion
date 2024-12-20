package com.gda.cfdi.service.empresa;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.service.UtilsCfdi4Service;
import com.gda.cfdi.service.UtilsService;

import mx.gob.sat.cfd._4.Comprobante;

@Service
public class MoreriraService {
	
	private static final Logger log = LoggerFactory.getLogger(MoreriraService .class);
	
	@Autowired
	private UtilsCfdi4Service utilsCfdi4Service;
	@Autowired
	private UtilsService utilsService;
	@Autowired
	private Environment env;

	public String generarCfdi(String xmlPlano) throws JAXBException, GeneralSecurityException, IOException, TransformerException {
	    Comprobante cfdi = utilsCfdi4Service.createComprobanteFromXml(xmlPlano);
	    cfdi.setVersion(env.getProperty("cfdi.version.4"));
	    DatosMarcaDto datosMarcaDto = null;
	    datosMarcaDto = utilsService.obtenerDatosRfcEmisor("BIO7603164H0", 4);
	    cfdi.getEmisor().setRfc(datosMarcaDto.getRfcMarca());
		cfdi.setNoCertificado(datosMarcaDto.getNumeroCertificado());
	    
		String xml = utilsCfdi4Service.createXmlFromComprobante(cfdi);
		log.info(xml);
		cfdi.setCertificado(utilsService.getCertificadoB64(datosMarcaDto.getRutaCer()));
		String cadenaOriginal = utilsService.createCadenaOriginal(xml, datosMarcaDto.getRutaCadenaOriginal());
		cfdi.setSello(utilsService.createSello(cadenaOriginal, datosMarcaDto.getRutaKey(), datosMarcaDto.getPasword()));
		
		String xmlOriginalSello =  utilsCfdi4Service.createXmlFromComprobante(cfdi);
		return xmlOriginalSello;
	}
	
}
