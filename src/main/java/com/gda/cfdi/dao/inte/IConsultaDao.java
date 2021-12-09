package com.gda.cfdi.dao.inte;

import java.util.List;

import com.gda.cfdi.dto.CClaveProductoServicioSatDto;
import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TOrdenExamenSucursalDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;
import com.gda.cfdi.dto.TSociedadCivilDto;

public interface IConsultaDao {

	CControlFolioDto getControlFolioDto(Integer csucursal);

	List<TPagoPacienteDto> getTPagoPacienteDto(Integer kordensucursal);

	List<TOrdenSucursalDto> getListTOrdenSucursalByKordensucursal(Integer kordensucursal);

	Integer updateCControlFolio(Integer ccontrolfolio, Integer ufolioactual);

	String getCPostalByCsucursal(Integer csucursal);

	CTipoPagoDto getCTipoPagoById(Integer id);

	CFormaPagoCfdiDto getCFormaPagoCfdiById(Integer id);

	List<TFacturaCanceladaDto> getFacturasCanceladasByKorden(Integer kordensucursal);

	List<TOrdenExamenSucursalDto> getTOrdenExamenSucursalByKOrdenSucursal(Integer kordensucursal);

	List<TSociedadCivilDto> getTSociedadCivilByKOrdenSucursal(Integer kordensucursal);

	CClaveProductoServicioSatDto findCClaveProductoServicioSatById(Integer cexamen);

	Integer getCTipoConvenioByConvenio(Integer cconvenio);

	

}
