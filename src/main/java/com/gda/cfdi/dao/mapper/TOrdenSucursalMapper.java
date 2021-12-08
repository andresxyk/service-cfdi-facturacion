package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.TOrdenSucursalDto;

public class TOrdenSucursalMapper implements RowMapper<TOrdenSucursalDto>{

	
	@Override
	public TOrdenSucursalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TOrdenSucursalDto dto = new TOrdenSucursalDto();
		dto.setKordensucursal(rs.getInt("kordensucursal"));
		dto.setUorden(rs.getInt("uorden"));
		dto.setSsucursal(rs.getString("ssucursal"));
		dto.setCsucursal(rs.getInt("csucursal"));
		dto.setKpaciente(rs.getInt("kpaciente"));
		dto.setCmedico(rs.getInt("cmedico"));
		dto.setSmedico(rs.getString("smedico"));
		dto.setSobservacion(rs.getString("sobservacion"));
		dto.setCconvenio(rs.getInt("cconvenio"));
		dto.setSordenexterna(rs.getString("sordenexterna"));
		dto.setCmarca(rs.getInt("cmarca"));
		return dto;
	}

}
