package com.gda.cfdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gda.cfdi.dto.DatosMarcaDto;
import com.gda.cfdi.dto.SelloDto;

import facturacion.domain.dto.FacturacionComprobanteDto;
import mx.gob.sat.cfd._3.Comprobante;

@Service
public class CfdiComprobanteService {
	private static final Logger log = LoggerFactory.getLogger(CfdiComprobanteService .class);
	
	@Autowired
	private UtilsService utilsService;
	
	public FacturacionComprobanteDto requestCfdiComprobante(FacturacionComprobanteDto facturacionComprobanteDto) throws Exception {	
		Comprobante cfdi = utilsService.createComplementoFromXml(facturacionComprobanteDto.gettFacturaEntityDto().getSxml());
		DatosMarcaDto datosMarcaDto = utilsService.obtenerDatosRfcEmisor(cfdi.getEmisor().getRfc());
		String xmlOriginal = utilsService.getXmlFromComprobante(cfdi);	
		log.info(xmlOriginal);
		SelloDto selloDto = utilsService.obtenerSello(datosMarcaDto, xmlOriginal);		
		cfdi.setSello(selloDto.getSelloCFDI());		
		String xmlOriginalSello = utilsService.getXmlFromComprobante(cfdi);		
		facturacionComprobanteDto.gettFacturaEntityDto().setSxml(xmlOriginalSello);		
		facturacionComprobanteDto.gettFacturaEntityDto().setScadenaoriginal(selloDto.getCadenaOriginal().length()>4000?selloDto.getCadenaOriginal().substring(0, 3999):selloDto.getCadenaOriginal());
		facturacionComprobanteDto.gettFacturaEntityDto().setSsellodigital(selloDto.getSelloCFDI());
		facturacionComprobanteDto.setComprobante(cfdi);
		return facturacionComprobanteDto;
	}
	
}
