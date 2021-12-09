package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.TSociedadCivilDto;

public class TSociedadCivilMapper implements RowMapper<TSociedadCivilDto>{

	@Override
	public TSociedadCivilDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TSociedadCivilDto dto = new TSociedadCivilDto();
		dto.setKordenexamensucursalsociedadcivil(rs.getInt("kordenexamensucursalsociedadcivil"));
		dto.setKordenexamensucursal(rs.getInt("kordenexamensucursal"));
		dto.setKordensucursal(rs.getInt("kordensucursal"));
		dto.setCexamen(rs.getInt("cexamen"));
		dto.setSexamen(rs.getString("sexamen"));
		dto.setMtotal(rs.getBigDecimal("mtotal"));
		dto.setMinterpretacion(rs.getBigDecimal("minterpretacion"));
		dto.setMoperacion(rs.getBigDecimal("moperacion"));
		return dto;
	}

}
