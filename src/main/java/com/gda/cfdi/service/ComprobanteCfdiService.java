package com.gda.cfdi.service;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.TFacturaDto;

import mx.gob.sat.cfd._3.Comprobante;

@Service
public class ComprobanteCfdiService {

	private static final Logger log = LoggerFactory.getLogger(ComprobanteCfdiService.class);
		
	@Autowired
	private UtilsService utilsService;
	
	@Autowired
	private ConsultaService consultaService;
	
	public Comprobante comprobanteCfdi3OfXml(Integer kfactura) throws JAXBException {
		TFacturaDto tf = consultaService.getTFacturaDto(kfactura);
		if(tf.getXmlTimbrado()!=null && tf.getXmlTimbrado().length() > 0) {
			try {
				log.info(tf.getSxmlsello());
				Comprobante comprobante = utilsService.createComplementoFromXml(tf.getXmlTimbrado());
				return comprobante;
			} catch (JAXBException e) {
				e.printStackTrace();
				throw e;
			}			
		}else {
			return null;
		}
	}
	
	public mx.gob.sat.cfd._4.Comprobante comprobanteCfdi4OfXml(Integer kfactura) throws JAXBException {
		TFacturaDto tf = consultaService.getTFacturaDto(kfactura);
		if(tf.getXmlTimbrado()!=null && tf.getXmlTimbrado().length() > 0) {
			try {
				mx.gob.sat.cfd._4.Comprobante comprobante = utilsService.createComprabanteFromXml4(tf.getXmlTimbrado());
				return comprobante;
			} catch (JAXBException e) {
				e.printStackTrace();
				throw e;
			}			
		}else {
			return null;
		}
	}
	
}
