package com.gda.cfdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CfdiEstatusService {
	
	private static final Logger log = LoggerFactory.getLogger(CfdiEstatusService.class);
	
	public String generarCfdiEstatus(String rfcEmisor, String rfcReceptor, String total, String uuid) {
		return getXmlEstatus(rfcEmisor, rfcReceptor, total, uuid);
	}
	
	public String getXmlEstatus(String rfcEmisor, String rfcReceptor, String total, String uuid){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">" +
				"<soapenv:Body>" +
				"<tem:Consulta>" +
				"<tem:expresionImpresa><![CDATA[?re="+rfcEmisor+"&rr="+rfcReceptor+"&tt="+total+"&id="+uuid+"]]></tem:expresionImpresa>" +
				"</tem:Consulta>" +
				"</soapenv:Body>" +
				"</soapenv:Envelope>";
	}
	

}
