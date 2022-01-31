package com.gda.cfdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.AnticipadaSerieADto;

@Service
public class CfdiSerieAService {
	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;
	
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	private void generarCfdiSerieA(AnticipadaSerieADto SerieADto) {
//		timbrado.cadena.seriea=2|N|S|@idConvenio@|@cveUsoCfdi@|@idMarca@|N|@montoSubtotal@|N|0|0|@cveFormaPago@|@cveMetodoPago@|@cantidadConceptos@|@conceptos@								
//				timbrado.cadena.seriea.concepto=@codigoConcepto@|@cantidad@|@cveUnidad@|@descripcion@|@precioUnitaorio@|@iva@|@unidad@|
		
		Boolean isRFC = false;
		
//		String convenioRfc = SerieADto.getIdConvenio();
	
	}

}
