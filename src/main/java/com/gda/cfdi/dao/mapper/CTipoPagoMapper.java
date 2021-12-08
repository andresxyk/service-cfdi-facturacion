package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.CTipoPagoDto;

public class CTipoPagoMapper implements RowMapper<CTipoPagoDto>{

	@Override
	public CTipoPagoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		CTipoPagoDto dto = new CTipoPagoDto();
		dto.setCtipopago(rs.getInt("ctipopago"));
		dto.setStipopago(rs.getString("stipopago"));
		dto.setCestadoregistro(rs.getInt("cestadoregistro"));
		dto.setCmarca(rs.getInt("cmarca"));
		dto.setUtipopagofactura(rs.getString("utipopagofactura"));
		dto.setCformapagocfdi(rs.getInt("cformapagocfdi"));
		return dto;
	}

}
