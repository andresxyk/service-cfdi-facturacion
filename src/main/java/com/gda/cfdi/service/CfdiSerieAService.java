package com.gda.cfdi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dto.AnticipadaSerieADto;
import com.gda.cfdi.dto.CUsoCfdiDto;
import com.gda.cfdi.dto.DatosFiscales;

@Service
public class CfdiSerieAService {
	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;
	
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private UtilsService utilsService;
	
	private void generarCfdiSerieA(AnticipadaSerieADto serieADto) throws Exception {
//		timbrado.cadena.seriea=2|N|S|@idConvenio@|@cveUsoCfdi@|@idMarca@|N|@montoSubtotal@|N|0|0|@cveFormaPago@|@cveMetodoPago@|@cantidadConceptos@|@conceptos@								
//				timbrado.cadena.seriea.concepto=@codigoConcepto@|@cantidad@|@cveUnidad@|@descripcion@|@precioUnitaorio@|@iva@|@unidad@|
		try {
			Boolean isRFC = false;		
			String convenioRfc = serieADto.getIdConvenio().toString();
			DatosFiscales datosFiscales = consultaService.obtenerDatosFiscalesByCConvenio(serieADto.getIdConvenio());
			List<CUsoCfdiDto> listUsoCFDI = null;
			String usoCFDI = serieADto.getIdUsoCfdi();
			Integer marca = serieADto.getIdMarca();
			String subtotal = serieADto.getSubTotal();
			
			if(!utilsService.validarRFC(datosFiscales.getSrfc().trim())) {
				throw new Exception("El RFC del Emisor es invalido.");
			}			
			if(datosFiscales.getSrfc().trim().length()==13) {
				listUsoCFDI = consultaService.getListUsoCFDI(1);
			}else {
				listUsoCFDI = consultaService.getListUsoCFDI(2);
			}
			Boolean busoCfdi = false;
			if (usoCFDI.equals("G01") || usoCFDI.equals("G02") || usoCFDI.equals("G03")
					|| usoCFDI.equals("I01") || usoCFDI.equals("I02") || usoCFDI.equals("I03")
					|| usoCFDI.equals("I04") || usoCFDI.equals("I05") || usoCFDI.equals("I06")
					|| usoCFDI.equals("I07") || usoCFDI.equals("I08") || usoCFDI.equals("D01")
					|| usoCFDI.equals("D02") || usoCFDI.equals("D03") || usoCFDI.equals("D04")
					|| usoCFDI.equals("D05") || usoCFDI.equals("D06") || usoCFDI.equals("D07")
					|| usoCFDI.equals("D08") || usoCFDI.equals("D09") || usoCFDI.equals("D10")
					|| usoCFDI.equals("P01")) {
				busoCfdi = true;
			}
			if(!busoCfdi) {
				throw new Exception("El UsoCfdi es invalido.");
			}
			
			
			
		} catch (Exception e) {
			throw e;
		}
	}

}
