package com.gda.cfdi.dao.inte;

import java.util.List;

import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;

public interface IConsultaDao {

	CControlFolioDto getControlFolioDto(Integer csucursal);

	List<TPagoPacienteDto> getTPagoPacienteDto(Integer kordensucursal);

	List<TOrdenSucursalDto> getListTOrdenSucursalByKordensucursal(Integer kordensucursal);

	Integer updateCControlFolio(Integer ccontrolfolio, Integer ufolioactual);

	String getCPostalByCsucursal(Integer csucursal);

	CTipoPagoDto getCTipoPagoById(Integer id);

	CFormaPagoCfdiDto getCFormaPagoCfdiById(Integer id);

	

}
