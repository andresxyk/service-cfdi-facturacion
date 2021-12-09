package com.gda.cfdi.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gda.cfdi.dto.TOrdenExamenSucursalDto;

public class TOrdenExamenSucursalMapper implements RowMapper<TOrdenExamenSucursalDto>{

	@Override
	public TOrdenExamenSucursalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		TOrdenExamenSucursalDto dto = new TOrdenExamenSucursalDto();
		dto.setKordenexamensucursal(rs.getInt("kordenexamensucursal"));
		dto.setSexamen(rs.getString("sexamen"));
		dto.setMsubtotal(rs.getBigDecimal("msubtotal"));
		dto.setMdescuentopromocion(rs.getBigDecimal("mdescuentopromocion"));
		dto.setMdescuentoempresa(rs.getBigDecimal("mdescuentoempresa"));
		dto.setMdescuentomedico(rs.getBigDecimal("mdescuentomedico"));
		dto.setMfacturaempresa(rs.getBigDecimal("mfacturaempresa"));
		dto.setMpagopaciente(rs.getBigDecimal("mpagopaciente"));
		dto.setMiva(rs.getBigDecimal("miva"));
		dto.setMtotal(rs.getBigDecimal("mtotal"));
		dto.setUmuestra(rs.getInt("umuestra"));
		dto.setDresultadoentrega(rs.getDate("dresultadoentrega"));
		dto.setDregistro(rs.getDate("dregistro"));
		dto.setKpromocion(rs.getInt("kpromocion"));
		dto.setSmotivocancelacion(rs.getString("smotivocancelacion"));
		dto.setCperfil(rs.getInt("cperfil"));
		dto.setMcomisionmedico(rs.getBigDecimal("mcomisionmedico"));
		dto.setUvolumenexamen(rs.getShort("uvolumenexamen"));
		dto.setUpuntosmedico(rs.getInt("upuntosmedico"));
		dto.setDtomamuestrainicio(rs.getDate("dtomamuestrainicio"));
		dto.setDtomamuestratermino(rs.getDate("dtomamuestratermino"));
//		dto.setSloginName(rs.getString("sloginName"));
		dto.setCsucursaltranslado(rs.getInt("csucursaltranslado"));
		dto.setCsucursalentregaresultado(rs.getInt("csucursalentregaresultado"));
		dto.setDresultadoentregatoma(rs.getDate("dresultadoentregatoma"));
		dto.setMcostolaboratorio(rs.getBigDecimal("mcostolaboratorio"));
		dto.setCmoneda(rs.getShort("cmoneda"));
//		dto.setUserIdAutoriza(rs.getInt("userIdAutoriza"));
		dto.setBventacruzada(rs.getBoolean("bventacruzada"));
		dto.setCexamen(rs.getInt("cexamen"));
		dto.setCconvenio(rs.getInt("cconvenio"));
		return dto;
	}

}
