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
import org.springframework.web.util.UriComponentsBuilder;

import com.gda.cfdi.pdf.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.pdf.dto.ComplementoDatosDto;
import com.gda.cfdi.pdf.dto.DatosFiscales;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TNotaCreditoEntityDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;

@Service
public class ConsultaService {

	private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);
	
	@Autowired
	private Environment env;
	
	public CFormaPagoCfdiDto selectAllCFormaPagoCfdiBySClave(String sclave) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"selectAllCFormaPagoCfdi-by-SClave";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("sclave", sclave)
						.build().toString();
			CFormaPagoCfdiDto response = template.getForObject(urlParam, CFormaPagoCfdiDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public List<ComplementoDatosDto> findComplementoDatosById(String srfcEmisor, String serie, String ufolioFactura){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"findComplementoDatos-by-Id";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("srfcEmisor", srfcEmisor).queryParam("serie", serie)
				.queryParam("ufolioFactura", ufolioFactura)
					.build().toString();
		ComplementoDatosDto[] array = template.getForObject(urlParam, ComplementoDatosDto[].class);
		return Arrays.asList(array);
	}
	
	public String getDireccionSucursalFacturaConsulta(Integer kfactura, boolean isNotaCredito) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"direccion-sucursal-by-condicion";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("kfactura", kfactura).queryParam("isNotaCredito", isNotaCredito)
					.build().toString();
		String result = template.getForObject(urlParam, String.class);
		return result;
	}
	
	public TNotaCreditoEntityDto obtenerNotaCredito(Integer ufoliofactura) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"tnotacredito-by-ufoliofactura";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("ufoliofactura", ufoliofactura)
						.build().toString();
			TNotaCreditoEntityDto response = template.getForObject(urlParam, TNotaCreditoEntityDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public Boolean getDespliegueFiscalConvenio(Integer cconvenio) {
		log.info("getDespliegueFiscalConvenio:: cconvenio="+cconvenio);
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"despliegue-fiscal-by-convenio";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("cconvenio", cconvenio)
					.build().toString();
		Boolean result = template.getForObject(urlParam, Boolean.class);
		return result;
	}
	
	public DatosFiscales obtenerDatosFiscalesByCConvenioAndBconvenio(Integer cconvenio, boolean bconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"datosFiscales-fac-by-cconvenio-and-bconvenio";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("cconvenio", cconvenio)
					.queryParam("bconvenio", bconvenio)
						.build().toString();
			DatosFiscales response = template.getForObject(urlParam, DatosFiscales.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public String getDireccionFiscalEmisorConvenio(Integer cconvenio) {
		log.info("getDireccionFiscalEmisorConvenio:: cconvenio="+cconvenio);
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"direccion-fiscal-emisor-by-convenio";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("cconvenio", cconvenio)
					.build().toString();
		String result = template.getForObject(urlParam, String.class);
		return result;
	}
	
	public Integer obtenerMarcaConvenio(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"getMarca-by-cconvenio";
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("cconvenio", cconvenio)
						.build().toString();
			Integer entidad = template.getForObject(urlParam, Integer.class);
			return entidad;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
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
