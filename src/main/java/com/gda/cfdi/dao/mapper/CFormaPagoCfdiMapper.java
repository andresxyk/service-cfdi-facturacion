package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.CFormaPagoCfdiDto;

public class CFormaPagoCfdiMapper implements RowMapper<CFormaPagoCfdiDto>{

	@Override
	public CFormaPagoCfdiDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		CFormaPagoCfdiDto dto = new CFormaPagoCfdiDto();
		dto.setCformapagocfdi(rs.getInt("cformapagocfdi"));
		dto.setSclaveformapagocfdi(rs.getString("sclaveformapagocfdi"));
		dto.setSformapagocfdi(rs.getString("sformapagocfdi"));
		dto.setCestadoregistro(rs.getInt("cestadoregistro"));
		return dto;
	}

}
