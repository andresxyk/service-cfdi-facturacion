package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.CControlFolioDto;

public class CControlFolioMapper implements RowMapper<CControlFolioDto>{

	@Override
	public CControlFolioDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		CControlFolioDto dto = new CControlFolioDto();
		dto.setCcontrolfolio(rs.getInt("ccontrolfolio"));
		dto.setUfolioinicial(rs.getInt("ufolioinicial"));
		dto.setUfoliofinal(rs.getInt("ufoliofinal"));
		dto.setUfolioactual(rs.getInt("ufolioactual"));
		dto.setDregistromodificacion(rs.getDate("dregistromodificacion"));
		dto.setDregistro(rs.getDate("dregistro"));
		dto.setSnombresequence(rs.getString("snombresequence"));
		dto.setNaprobacion(rs.getLong("naprobacion"));
		dto.setNanoprobacion(rs.getShort("nanoprobacion"));
		dto.setSserie(rs.getString("sserie"));
		dto.setCentidadlegal(rs.getInt("centidadlegal"));
		return dto;
	}

}
