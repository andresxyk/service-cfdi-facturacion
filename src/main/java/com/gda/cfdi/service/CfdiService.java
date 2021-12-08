package com.gda.cfdi.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.controller.CfdiController;
import com.gda.cfdi.dao.inte.IConsultaDao;
import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd._3.Comprobante.Emisor;
import mx.gob.sat.cfd._3.Comprobante.Receptor;
import mx.gob.sat.cfd._3.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

@Service
public class CfdiService {

	private static final Logger log = LoggerFactory.getLogger(CfdiController .class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private IConsultaDao consultaDao;
	
	public String generarCfdi(Integer kordensucursal) {	
		try {
			Integer cusoCfdi = 3;
			Boolean bandAzteca;
			List<TPagoPacienteDto> list =  consultaDao.getTPagoPacienteDto(kordensucursal);
			List<TOrdenSucursalDto> listTos = consultaDao.getListTOrdenSucursalByKordensucursal(kordensucursal);
			Boolean contieneSaldo = list.get(0).getMsaldo().intValue() > 0 ? true :false;
			Integer csucursal = listTos.get(0).getCsucursal();
			Integer cmarca = listTos.get(0).getCmarca();
			Integer cconvenio = listTos.get(0).getCconvenio();
			TPagoPacienteDto pacienteDto = this.obtenerPago(list);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			
			Date fechaInicio = null;
			Date fechaFin = null;			
			fechaInicio = sdf.parse("01-01-2019");
			fechaFin = sdf.parse("30-09-2019");			
			if((pacienteDto.getDregistro().equals(fechaInicio)|| pacienteDto.getDregistro().after(fechaInicio)) && 
					(pacienteDto.getDregistro().equals(fechaFin) || pacienteDto.getDregistro().before(fechaFin)) ){
				bandAzteca = false;
			}else{
				bandAzteca = true;
			}			
			
			String tipoPago = "";
			if (contieneSaldo) {
				tipoPago = "PPD";
			} else {
				tipoPago = "PUE";
			}
			String FORMATER = "yyyy-MM-dd'T'HH:mm:ss";
			DateFormat format = new SimpleDateFormat(FORMATER);
			XMLGregorianCalendar value = null;
			
			if(cmarca.equals(15)){
				value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss");
			}else{
				if(csucursal == 250 
						|| csucursal == 238 
						|| csucursal == 240 
						|| csucursal.equals(250)
						|| csucursal.equals(238)
						|| csucursal.equals(240)){
					value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-62), "yyyy-MM-dd'T'HH:mm:ss");
				}else{
					value = toXmlGregorianCalendar(this.sumarORestarMinutosAFecha(new Date(),-2), "yyyy-MM-dd'T'HH:mm:ss");				
				}
			}
			
			ObjectFactory of = new ObjectFactory();
			Comprobante cfdi = of.createComprobante();
			cfdi.setVersion(env.getProperty("cfdi.version"));
			CControlFolioDto controlFolioDto = consultaDao.getControlFolioDto(kordensucursal);
			Integer ufolio = obtenerFolio(controlFolioDto); 
			
			cfdi.setFolio(String.valueOf(ufolio));
			cfdi.setSerie(controlFolioDto.getSserie());
			cfdi.setTipoCambio(BigDecimal.valueOf(1));
			cfdi.setFecha(value);
//			cfdi.setNoCertificado(numeroCertificado);
			
//			cfdi.setCertificado(obtenerCertificadocer());
			cfdi.setMoneda(CMoneda.MXN);
			cfdi.setTipoDeComprobante(CTipoDeComprobante.I);
			
			cfdi.setLugarExpedicion(consultaDao.getCPostalByCsucursal(csucursal));
			
			CTipoPagoDto tipoPagoDto = consultaDao.getCTipoPagoById(pacienteDto.getCtipopago());
			
			log.info("tip pago infogda-->>  "+tipoPagoDto.getCtipopago());
			CFormaPagoCfdiDto cFormaPagoCfdiDto = consultaDao.getCFormaPagoCfdiById(tipoPagoDto.getCformapagocfdi());
			log.info("Forma Pago cfdi"+cFormaPagoCfdiDto.getSclaveformapagocfdi()
				+" form "+cFormaPagoCfdiDto.getSformapagocfdi());
			cfdi.setFormaPago(cFormaPagoCfdiDto.getSclaveformapagocfdi());
			cfdi.setMetodoPago(contieneSaldo ? CMetodoPago.PPD : CMetodoPago.PUE);
			
			
			// Emisor
			Emisor emisor = of.createComprobanteEmisor();
//			emisor.setNombre(darFormatoCFDI( razonSocialMarca.toUpperCase()  ));  
//			emisor.setRfc( rfcMarca.toUpperCase()  );  
//			emisor.setRegimenFiscal(CRegimenFiscal.valueOf("C" + "601").getValue());
			cfdi.setEmisor(emisor);
			
			// Receptor
			Receptor receptor = of.createComprobanteReceptor();
			receptor.setNombre("PUBLICO EN GENERAL");  
			log.info("Nombre:::-----    "+receptor.getNombre());
			receptor.setRfc("XAXX010101000");
			log.info("cusoCfdi--->>>   " + cusoCfdi);
			switch (cusoCfdi) {
			case 3:
				receptor.setUsoCFDI(CUsoCFDI.G_03);
				break;
			case 12:
				receptor.setUsoCFDI(CUsoCFDI.D_01);
				break;
			case 13:
				receptor.setUsoCFDI(CUsoCFDI.D_02);
				break;
			case 22:
//									case 44:
				receptor.setUsoCFDI(CUsoCFDI.P_01);
				break;
			default:
				break;
			}
			cfdi.setReceptor(receptor);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private  XMLGregorianCalendar toXmlGregorianCalendar(Date date, String format) throws DatatypeConfigurationException {
	    return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat(format).format(date));
	}
	
	private Date sumarORestarMinutosAFecha(Date fecha, int minutos){	
        Calendar calendar = Calendar.getInstance();	
        calendar.setTime(fecha); 	
        calendar.add(Calendar.MINUTE, minutos);  	
        return calendar.getTime(); 	
    }
	
	private Integer obtenerFolio(CControlFolioDto ccontrol) {
		log.info("ENTRY::: insertarFacturaCancelada:::  " + ccontrol.getCcontrolfolio() + " folioActual "
				+ ccontrol.getUfolioactual());
		Integer actualizo = ccontrol.getUfolioactual() + 1;
		ccontrol.setUfolioactual(actualizo);
		log.info("ufolio + 1 *********   "+actualizo);
		consultaDao.updateCControlFolio(ccontrol.getCcontrolfolio(), actualizo);
		return actualizo;
	}
	
	private TPagoPacienteDto obtenerPago(List<TPagoPacienteDto> list) {
		Integer mayor = list.get(0).getMpagopacienteparcial().intValue();
		int posicion = 0;
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).getMpagopacienteparcial().intValue() > mayor) {
				mayor = list.get(i).getMpagopacienteparcial().intValue();
				posicion = i;
			}
		}
		TPagoPacienteDto pagoPacienteDto = list.get(posicion);
		return pagoPacienteDto;
	}
	
}
