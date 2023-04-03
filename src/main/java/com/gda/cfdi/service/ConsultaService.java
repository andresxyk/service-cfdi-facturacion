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

import com.gda.cfdi.dto.CClaveProductoServicioSatDto;
import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CConvenioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.CUsoCfdiDto;
import com.gda.cfdi.dto.ControlFolioDto;
import com.gda.cfdi.dto.DatosFiscales;
import com.gda.cfdi.dto.DatosFiscalesDto;
import com.gda.cfdi.dto.EstudioDto;
import com.gda.cfdi.dto.PagoDto;
import com.gda.cfdi.dto.TDatoFiscalDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TFacturaCreditoDto;
import com.gda.cfdi.dto.TFacturaDto;
import com.gda.cfdi.dto.TFacturaEntity;
import com.gda.cfdi.dto.TNotaCreditoEntityDto;
import com.gda.cfdi.dto.TOrdenExamenSucursalDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;
import com.gda.cfdi.dto.TSociedadCivilDto;

import facturacion.domain.dto.DatosCfdiDto;

@Service
public class ConsultaService {
	
	private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);
	
	@Autowired
	private Environment env;
	
	
	public Integer getCTipoConvenioByConvenio(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"getCTipoConvenio-by-Convenio";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("cconvenio", cconvenio)
						.build().toString();
			Integer response = template.getForObject(urlParam, Integer.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public List<TSociedadCivilDto> getTSociedadCivilByKOrdenSucursal(Integer kordensucursal){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"getTSociedadCivil-by-KOrdenSucursal";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("kordensucursal", kordensucursal)
					.build().toString();
		TSociedadCivilDto[] array = template.getForObject(urlParam, TSociedadCivilDto[].class);
		return Arrays.asList(array);
	}
	
	public List<TOrdenExamenSucursalDto> getTOrdenExamenSucursalByKOrdenSucursal(Integer kordensucursal){
		RestTemplate template = new RestTemplate();
		String url = env.getProperty("url.service.database")+"tordenexamensucursal-by-kordensucursal";
		String urlParam = UriComponentsBuilder.fromUriString(url)
				.queryParam("kordensucursal", kordensucursal)
					.build().toString();
		TOrdenExamenSucursalDto[] array = template.getForObject(urlParam, TOrdenExamenSucursalDto[].class);
		return Arrays.asList(array);
	}
	
	public Integer updateCControlFolio(Integer ufolioactual,Integer ccontrolfolio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"updateCControlFolio";
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("ccontrolfolio", ccontrolfolio)
					.queryParam("ufolioactual", ufolioactual)
						.build().toString();
			Integer response = template.postForObject(urlParam, null,Integer.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public Integer saveComplementoPago(TFacturaDto dto) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"saveComplementoPago";
			Integer response = template.postForObject(url, dto,Integer.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public ControlFolioDto findControlFolioById(Integer idControlFolio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"findControlFolio-by-id";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("idControlFolio", idControlFolio)
						.build().toString();
			ControlFolioDto response = template.getForObject(urlParam, ControlFolioDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public PagoDto findPagoById(Integer idPago) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"findPago-by-id";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("idPago", idPago)
						.build().toString();
			PagoDto response = template.getForObject(urlParam, PagoDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
	}
	
	public CClaveProductoServicioSatDto findCClaveProductoServicioSatById(Integer cexamen) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"cClaveProductoServicioSat-by-id";			
			String urlParam = UriComponentsBuilder.fromUriString(url)
					.queryParam("cexamen", cexamen)
						.build().toString();
			CClaveProductoServicioSatDto response = template.getForObject(urlParam, CClaveProductoServicioSatDto.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
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
	
	public TFacturaEntity getTFacturaEntityQuery(String query) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"getTFacturaEntityQuery-by-query";			
			TFacturaEntity response = template.postForObject(url, query, TFacturaEntity.class);
			return response;			
		}catch (RestClientResponseException e) {
			log.error(e.getMessage());
			log.error(e.getResponseBodyAsString());
			throw e;
		}
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
			String url = env.getProperty("url.service.database")+"datosFiscales-fac-by-cconvenio";			
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
	
	public DatosCfdiDto getDatosCfdiByConvenio(Integer cconvenio) {
		try {
			RestTemplate template = new RestTemplate();
			String url = env.getProperty("url.service.database")+"datoscfdi-by-convenio?cconvenio="+cconvenio;
			DatosCfdiDto dto = template.getForObject(url, DatosCfdiDto.class);
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
