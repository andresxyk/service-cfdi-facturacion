package com.gda.cfdi.service;

import java.math.BigDecimal;
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

import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CConvenioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.CUsoCfdiDto;
import com.gda.cfdi.dto.DatosFiscales;
import com.gda.cfdi.dto.DatosFiscalesDto;
import com.gda.cfdi.dto.EstudioDto;
import com.gda.cfdi.dto.TDatoFiscalDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TFacturaCreditoDto;
import com.gda.cfdi.dto.TFacturaDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;

@Service
public class ConsultaService {
	
	private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);
	
	@Autowired
	private Environment env;
	
	public List<CUsoCfdiDto> getListUsoCFDI(Integer num){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"getListUsoCFDI-by-num";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("num", num)
					.build().toString();
		CUsoCfdiDto[] array = template.getForObject(urlParam, CUsoCfdiDto[].class);
		return Arrays.asList(array);
	}
	
	public DatosFiscales obtenerDatosFiscalesByCConvenio(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"datosFiscales-by-cconvenio";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("cconvenio", cconvenio)
						.build().toString();
			DatosFiscales response = template.getForObject(urlParam, DatosFiscales.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public Integer updateMontosFactura(BigDecimal total, BigDecimal subtotal, BigDecimal iva, BigDecimal descuento, Integer kfactura){
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"updateMontosFactura-credito?total={total}&subtotal={subtotal}"
					+ "&iva={iva}&descuento={descuento}&kfactura={kfactura}";
			String urlParam = url.replace("{total}", total.toString()).replace("{subtotal}", subtotal.toString())
					.replace("{iva}", iva.toString()).replace("{descuento}", descuento.toString()).replace("{kfactura}", kfactura.toString());
			Integer id = template.postForObject(urlParam, null, Integer.class);
			return id;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public List<EstudioDto> getListEstudiosByKfactura(Integer kfactura){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"getListEstudios-by-kfactura?kfactura="+kfactura;
		EstudioDto[] array = template.getForObject(url, EstudioDto[].class);
		return Arrays.asList(array);
	}
	
	public List<TDatoFiscalDto> getListDatosFiscalesByRfc(String rfc){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"getListDatosFiscales-by-rfc?rfc="+rfc;
		TDatoFiscalDto[] array = template.getForObject(url, TDatoFiscalDto[].class);
		return Arrays.asList(array);
	}
	
	public DatosFiscalesDto getDatosFiscalesByConvenio(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"datosFiscales-by-cconvenio?cconvenio={cconvenio}";
			String urlParam = url.replace("{cconvenio}", cconvenio.toString());
			DatosFiscalesDto response = template.getForObject(urlParam, DatosFiscalesDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public TFacturaDto getTFacturaDto(Integer kfactura) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"tfactura-by-id?kfactura={kfactura}";
			String urlParam = url.replace("{kfactura}", kfactura.toString());
			TFacturaDto response = template.getForObject(urlParam, TFacturaDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public Integer getCEntidadLegalByCMarca(Integer cmarca) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"centidadlegal-by-cmarca?cmarca="+cmarca;
			Integer entidad = template.getForObject(url, Integer.class);
			return entidad;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public CConvenioDto getCcovenioDtoById(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"cconvenio-by-id?cconvenio="+cconvenio;
			CConvenioDto dto = template.getForObject(url, CConvenioDto.class);
			return dto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public TDatoFiscalDto getTDatoFiscalById(Integer kdatofiscal) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"tdatofiscal-by-id?kdatofiscal="+kdatofiscal;
			TDatoFiscalDto dto = template.getForObject(url, TDatoFiscalDto.class);
			return dto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public List<TFacturaCanceladaDto> getFacturasCanceladasByKorden(Integer kordensucursal){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tfacturacancelada-by-kordensucursal?kordensucursal="+kordensucursal;
		TFacturaCanceladaDto[] array = template.getForObject(url, TFacturaCanceladaDto[].class);
		return Arrays.asList(array);
	}
	
	public CFormaPagoCfdiDto getCFormaPagoCfdiById(Integer id) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"cformapagocfdi-by-id?id="+id;
			CFormaPagoCfdiDto dto = template.getForObject(url, CFormaPagoCfdiDto.class);
			return dto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public CTipoPagoDto getCTipoPagoById(Integer id) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"ctipopago-by-id?id="+id;
			CTipoPagoDto dto = template.getForObject(url, CTipoPagoDto.class);
			return dto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public String getCPostalByCsucursal(Integer csucursal) {
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"cpostal-by-csucursal?csucursal="+csucursal;
		String result = template.getForObject(url, String.class);
		return result;
	}
	
	public List<TPagoPacienteDto> getTPagoPacienteDto(Integer kordensucursal){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tpagopaciente-by-kordensucursal?kordensucursal="+kordensucursal;
		TPagoPacienteDto[] array = template.getForObject(url, TPagoPacienteDto[].class);
		return Arrays.asList(array);
	}

	public List<TOrdenSucursalDto> getListTOrdenSucursalByKordensucursal(Integer kordensucursal){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tordensucursal-by-kordensucursal?kordensucursal="+kordensucursal;
		TOrdenSucursalDto[] array = template.getForObject(url, TOrdenSucursalDto[].class);
		return Arrays.asList(array);
	}
	
	public CControlFolioDto getControlFolioDto(Integer csucursal) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"ccontrolfolio-by-csucursal?csucursal="+csucursal;
			CControlFolioDto facturaDto = template.getForObject(url, CControlFolioDto.class);
			return facturaDto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public TFacturaCreditoDto getTFacturaCreditoDtoBySerieAndFolio(String sserie, Integer folio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"tfacturaCredito-by-csucursal-and-folio?sserie="+sserie+"&folio="+folio;
			TFacturaCreditoDto dto = template.getForObject(url, TFacturaCreditoDto.class);
			return dto;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			return null;
		}
	}
	
	public Integer updateAjusteFactura(Integer kfactura, Integer user_id_change, BigDecimal msubtotal, BigDecimal miva, BigDecimal mtotal){
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"updateAjusteFactura-credito?kfactura={kfactura}&useridchange={useridchange}"
					+ "&msubtotal={msubtotal}&miva={miva}&mtotal={mtotal}";
			String urlParam = url.replace("{kfactura}", kfactura.toString()).replace("{useridchange}", user_id_change.toString())
					.replace("{msubtotal}", msubtotal.toString()).replace("{miva}", miva.toString()).replace("{mtotal}", mtotal.toString());
			Integer id = template.postForObject(urlParam, null, Integer.class);
			return id;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public Integer updateMetodoPago(String strNoCuenta,String strMetodoPago,Integer cconvenio){
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"updateMetodoPago-credito?strNoCuenta={strNoCuenta}&strMetodoPago={strMetodoPago}"
					+ "&cconvenio={cconvenio}";
			String urlParam = url.replace("{strNoCuenta}", strNoCuenta).replace("{strMetodoPago}", strMetodoPago).replace("{cconvenio}", cconvenio.toString());
			Integer id = template.postForObject(urlParam, null, Integer.class);
			return id;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
}
