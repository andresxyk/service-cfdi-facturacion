package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.TPagoPacienteDto;

public class TPagoPacienteMapper  implements RowMapper<TPagoPacienteDto>{

	@Override
	public TPagoPacienteDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TPagoPacienteDto dto = new TPagoPacienteDto();
		dto.setKpagopaciente(rs.getInt("kpagopaciente"));
		dto.setMpagopacientetotal(rs.getBigDecimal("mpagopacientetotal"));
		dto.setManticipo(rs.getBigDecimal("manticipo"));
		dto.setMpagopacienteparcial(rs.getBigDecimal("mpagopacienteparcial"));
		dto.setMdevolucionpaciente(rs.getBigDecimal("mdevolucionpaciente"));
		dto.setMsaldo(rs.getBigDecimal("msaldo"));
		dto.setDregistro(rs.getDate("dregistro"));
		dto.setSdigitostarjeta(rs.getString("sdigitostarjeta"));
		dto.setKcortecajaparcial(rs.getInt("kcortecajaparcial"));
		dto.setCestadoregistro(rs.getInt("cestadoregistro"));
		dto.setUser_id(rs.getInt("user_id"));
		dto.setCtipopago(rs.getInt("ctipopago"));
		
		return dto;
	}

}
