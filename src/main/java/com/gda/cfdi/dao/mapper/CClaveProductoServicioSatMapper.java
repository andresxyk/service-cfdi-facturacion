package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.CClaveProductoServicioSatDto;

public class CClaveProductoServicioSatMapper implements RowMapper<CClaveProductoServicioSatDto>{

	@Override
	public CClaveProductoServicioSatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		CClaveProductoServicioSatDto dto = new CClaveProductoServicioSatDto();
		dto.setCclaveproductoserviciosat(rs.getInt("cclaveproductoserviciosat"));
		dto.setSclaveproductoserviciosat(rs.getString("sclaveproductoserviciosat"));
		dto.setSproductoserviciosat(rs.getString("sproductoserviciosat"));
		dto.setBincluiriva(rs.getBoolean("bincluiriva"));
		dto.setBincluirieps(rs.getBoolean("bincluirieps"));
		return dto;
	}

}
