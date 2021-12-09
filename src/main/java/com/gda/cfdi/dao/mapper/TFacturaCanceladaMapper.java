package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.TFacturaCanceladaDto;

public class TFacturaCanceladaMapper implements RowMapper<TFacturaCanceladaDto>{

	@Override
	public TFacturaCanceladaDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TFacturaCanceladaDto dto = new TFacturaCanceladaDto();
		dto.setFactura_id(rs.getString("factura_id"));
		dto.setKfactura(rs.getInt("kfactura"));
		dto.setAcuse_cancelacion(rs.getString("acuse_cancelacion"));
		dto.setFecha_creacion(rs.getDate("fecha_creacion"));
		dto.setFolio_fiscal(rs.getString("folio_fiscal"));
		return dto;
	}

}
