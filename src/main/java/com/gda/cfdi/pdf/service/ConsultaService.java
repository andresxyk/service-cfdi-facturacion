package com.gda.cfdi.pdf.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;

@Service
public class ConsultaService {

	@Autowired
	private Environment env;
	
	public TFacturaDto getTFacturaById(Integer kfactura) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tfactura-by-id?kfactura="+kfactura;
		TFacturaDto facturaDto = template.getForObject(url, TFacturaDto.class);
		return facturaDto;
	}
	
	public String getDireccionSucursalFactura(Integer kfactura) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"direccion-sucursal?kfactura="+kfactura;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public String getDireccionFiscalEmisor(Integer centidadlegal) {
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
}
