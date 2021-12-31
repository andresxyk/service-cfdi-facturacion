package com.gda.cfdi.pdf.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;
import com.gda.cfdi.pdf.service.template.TemplatePdfService;

@Service
public class ConsultaService {

	private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);
	
	@Autowired
	private Environment env;
	
	public TFacturaDto getTFacturaById(Integer kfactura) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"tfactura-by-id?kfactura="+kfactura;
			TFacturaDto facturaDto = template.getForObject(url, TFacturaDto.class);
			return facturaDto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public String getDireccionSucursalFactura(Integer kfactura) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"direccion-sucursal?kfactura="+kfactura;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public String getDireccionFiscalEmisor(Integer centidadlegal) {
		log.info("getDireccionFiscalEmisor:: centidadlegal="+centidadlegal);
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"direccion-fiscal-emisor?centidadlegal="+centidadlegal;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public List<TOrdenSucursalFacDto> getTOrdenSucursalFacByKfactura(Integer kfactura){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tordesucursalfac-by-kfactura?kfactura="+kfactura;
		TOrdenSucursalFacDto[] array = template.getForObject(url, TOrdenSucursalFacDto[].class);
		return Arrays.asList(array);
	}
	
	public String getPacienteFactura(Integer kfactura) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"paciente-factura?kfactura="+kfactura;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public String getConceptoMetodoPago(String metodopago) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"metodo-pago?metodopago="+metodopago;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public String getUsoCfdi(String cfdi) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"uso-cfdi?cfdi="+cfdi;
		String result = template.getForObject(url, String.class);
		return result;
	}
}
