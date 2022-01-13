package com.gda.cfdi.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;

@Service
public class ConsultaService {
	
	private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);
	
	@Autowired
	private Environment env;
	
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
	
}
