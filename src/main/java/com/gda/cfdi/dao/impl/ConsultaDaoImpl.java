package com.gda.cfdi.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.gda.cfdi.dao.inte.IConsultaDao;
import com.gda.cfdi.dao.mapper.CClaveProductoServicioSatMapper;
import com.gda.cfdi.dao.mapper.CControlFolioMapper;
import com.gda.cfdi.dao.mapper.CFormaPagoCfdiMapper;
import com.gda.cfdi.dao.mapper.CTipoPagoMapper;
import com.gda.cfdi.dao.mapper.TFacturaCanceladaMapper;
import com.gda.cfdi.dao.mapper.TOrdenExamenSucursalMapper;
import com.gda.cfdi.dao.mapper.TOrdenSucursalMapper;
import com.gda.cfdi.dao.mapper.TPagoPacienteMapper;
import com.gda.cfdi.dao.mapper.TSociedadCivilMapper;
import com.gda.cfdi.dto.CClaveProductoServicioSatDto;
import com.gda.cfdi.dto.CControlFolioDto;
import com.gda.cfdi.dto.CFormaPagoCfdiDto;
import com.gda.cfdi.dto.CTipoPagoDto;
import com.gda.cfdi.dto.TFacturaCanceladaDto;
import com.gda.cfdi.dto.TOrdenExamenSucursalDto;
import com.gda.cfdi.dto.TOrdenSucursalDto;
import com.gda.cfdi.dto.TPagoPacienteDto;
import com.gda.cfdi.dto.TSociedadCivilDto;

@Repository("consultaDaoImpl")
public class ConsultaDaoImpl extends JdbcDaoSupport implements IConsultaDao{

	@Autowired
	DataSource dataSource;
	
	@Autowired
	Environment env;
	
	@PostConstruct
	private void initialize(){
		setDataSource(dataSource);
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public CControlFolioDto getControlFolioDto(Integer csucursal) {
		CControlFolioDto dto = null;
		logger.info("ejecutando getControlFolioDto:: csucursal="+csucursal);		
		String query = "SELECT * FROM C_Control_Folio WHERE csucursal = ? "
				+ "AND cestadoregistro=31 ORDER BY ccontrolfolio DESC";
		try {
			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {csucursal}, new CControlFolioMapper());			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		logger.info("getControlFolioDto ejecutado");
		return dto;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<TPagoPacienteDto> getTPagoPacienteDto(Integer kordensucursal) {
		logger.info("ejecutando getTPagoPacienteDto");
		List<TPagoPacienteDto> list = null;
		String query = "SELECT * FROM T_Pago_Paciente  WHERE \r\n"
				+ "kordensucursal = ? ORDER BY kpagopaciente DESC" ;
		try {
			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TPagoPacienteMapper());
			logger.info("getTPagoPacienteDto ejecutado:");			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return list;
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public List<TOrdenSucursalDto> getListTOrdenSucursalByKordensucursal(Integer kordensucursal){
		logger.info("ejecutando getListTOrdenSucursalByKordensucursal");
		List<TOrdenSucursalDto> list;
		String query = "select * from t_orden_sucursal where kordensucursal = ?" ;
		list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TOrdenSucursalMapper());
		logger.info("getListTOrdenSucursalByKordensucursal ejecutado:"+list.size());
		return list;
	}
	
	
	@Override
	public Integer updateCControlFolio(Integer ccontrolfolio, Integer ufolioactual){
		logger.info("ejecutando updateCControlFolio [ccontrolfolio=" + ccontrolfolio + ", ufolioactual="+ufolioactual+"]");
		Integer row = null;
		String query = "UPDATE c_control_folio SET ufolioactual=? WHERE ccontrolfolio = ? ";
		row = this.getJdbcTemplate().update(query, new Object[] {ufolioactual,ccontrolfolio});
		logger.info("ejecutando updateCControlFolio");
		return row;
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public String getCPostalByCsucursal(Integer csucursal) {
		String cpostal = null;
		logger.info("ejecutando getCPostalByCsucursal:: csucursal="+csucursal);		
		String query = "select ccp.cpostal from c_sucursal cs\r\n"
				+ "inner join c_codigo_postal ccp on cs.ccodigopostal = ccp.ccodigopostal\r\n"
				+ "where csucursal = ?";
		try {
			cpostal = this.getJdbcTemplate().queryForObject(query, new Object[] {csucursal}, String.class);			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		logger.info("getCPostalByCsucursal ejecutado");
		return cpostal;
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public CTipoPagoDto getCTipoPagoById(Integer id) {
		CTipoPagoDto dto = null;
		logger.info("ejecutando getCTipoPagoById:: id="+id);
		
		String query = "select * from c_tipo_pago where ctipopago = ? ;";
		try {
			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {id}, new CTipoPagoMapper());			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		logger.info("getCTipoPagoById ejecutado");
		return dto;
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public CFormaPagoCfdiDto getCFormaPagoCfdiById(Integer id) {
		CFormaPagoCfdiDto dto = null;
		logger.info("ejecutando getCFormaPagoCfdiById:: id="+id);
		
		String query = "select * from c_forma_pago_cfdi where cformapagocfdi = ?;";
		try {
			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {id}, new CFormaPagoCfdiMapper());			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		logger.info("getCFormaPagoCfdiById ejecutado");
		return dto;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<TFacturaCanceladaDto> getFacturasCanceladasByKorden(Integer kordensucursal){
		logger.info("ejecutando getFacturasCanceladasByKorden");
		List<TFacturaCanceladaDto> list;
		String query = "SELECT tc.factura_id,tc.kfactura,tc.acuse_cancelacion,tc.fecha_creacion,tc.folio_fiscal FROM t_factura_cancelada tc "
				+ "INNER JOIN t_orden_sucursal_fac tosf  "
				+ "ON tc.kfactura = tosf.kfactura\r\n" + 
				"where tosf.kordensucursal = ? ORDER BY fecha_creacion DESC" ;
		list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TFacturaCanceladaMapper());
		logger.info("getFacturasCanceladasByKorden ejecutado:"+list.size());
		return list;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<TOrdenExamenSucursalDto> getTOrdenExamenSucursalByKOrdenSucursal(Integer kordensucursal) {
		logger.info("ejecutando getTOrdenExamenSucursalByKOrdenSucursal: kordensucursal="+kordensucursal);
		List<TOrdenExamenSucursalDto> list = null;
		String query = "SELECT * FROM t_orden_examen_sucursal "
				+ "WHERE kordensucursal = ? "
				+ "ORDER BY 1 DESC" ;
		try {
			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TOrdenExamenSucursalMapper());
			logger.info("getTOrdenExamenSucursalByKOrdenSucursal ejecutado:"+list.size());			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return list;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<TSociedadCivilDto> getTSociedadCivilByKOrdenSucursal(Integer kordensucursal) {
		logger.info("ejecutando getTSociedadCivilByKOrdenSucursal");
		List<TSociedadCivilDto> list = null;
		String query = "SELECT toessc.* " +
				"FROM t_orden_sucursal tos INNER JOIN public.t_orden_examen_sucursal toes ON tos.kordensucursal=toes.kordensucursal " +
				"INNER JOIN sociedadcivil.t_orden_examen_sucursal_sociedadcivil toessc ON toes.kordenexamensucursal = toessc.kordenexamensucursal " +
				"WHERE tos.kordensucursal IN (?) " ;
		try {
			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TSociedadCivilMapper());
			logger.info("getTSociedadCivilByKOrdenSucursal ejecutado:");			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return list;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public CClaveProductoServicioSatDto findCClaveProductoServicioSatById(Integer cexamen) {
		CClaveProductoServicioSatDto dto = null;
		logger.info("ejecutando findCClaveProductoServicioSatById:: cexamen="+cexamen);
		
		String query = "SELECT ccpss.* FROM c_clave_producto_servicio_sat ccpss\r\n"
				+ "inner join web2lablis.c_examen ce on ce.cclaveproductoserviciosat = ccpss.cclaveproductoserviciosat\r\n"
				+ "where ce.cexamen = ?";
		try {
			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {cexamen}, new CClaveProductoServicioSatMapper());			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}

		logger.info("findCClaveProductoServicioSatById ejecutado");
		return dto;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public Integer getCTipoConvenioByConvenio(Integer cconvenio) {
		Integer cpostal = null;
		logger.info("ejecutando getCTipoConvenioByConvenio:: cconvenio="+cconvenio);		
		String query = "select ctipoconvenio from c_convenio where cconvenio = ?";
		try {
			cpostal = this.getJdbcTemplate().queryForObject(query, new Object[] {cconvenio}, Integer.class);			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		logger.info("getCTipoConvenioByConvenio ejecutado");
		return cpostal;
	}
	
	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TOrdenSucursalFacDto> getTOrdenSucursalFac(Integer kordensucursal) {
//		logger.info("ejecutando getTOrdenSucursalFac");
//		List<TOrdenSucursalFacDto> list = null;
//		String query = "SELECT * FROM t_orden_sucursal_fac "
//				+ "WHERE kordensucursal = ? "
//				+ "AND cestadoregistro<>39 "
//				+ "ORDER BY kordensucursalfac DESC" ;
//		try {
//			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TOrdenSucursalFacMapper());
//			logger.info("getTOrdenSucursalFac ejecutado:");			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		return list;
//	}
//	
//	

//	
//	

//	
//	

//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public TFacturaDto findFacturaById(Integer kfactura) {
//		TFacturaDto tFactura = null;
//		logger.info("ejecutando findFacturaById:: kfactura="+kfactura);
//		
//		String query = "SELECT * FROM t_factura WHERE kfactura=? ";
//		try {
//			tFactura = this.getJdbcTemplate().queryForObject(query, new Object[] {kfactura}, new TFacturaMapper());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//
//		logger.info("findFacturaById ejecutado");
//		return tFactura;
//	}
//	

//	
//	

//	
//	

//	
//	
//	
//	

//	
//	

//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public Integer getCclienteByConvenio(Integer cconvenio) {
//		Integer dato = null;
//		logger.info("ejecutando getCclienteByConvenio:: cconvenio="+cconvenio);		
//		String query = "select ccliente from c_convenio where cconvenio = ?";
//		try {
//			dato = this.getJdbcTemplate().queryForObject(query, new Object[] {cconvenio}, Integer.class);			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getCclienteByConvenio ejecutado");
//		return dato;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public String getNombreSucursalByCsucursal(Integer csucursal) {
//		String name = null;
//		logger.info("ejecutando getNombreSucursalByCsucursal:: csucursal="+csucursal);		
//		String query = "select snombresucursal from c_sucursal where csucursal = ?";
//		try {
//			name = this.getJdbcTemplate().queryForObject(query, new Object[] {csucursal}, String.class);			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getNombreSucursalByCsucursal ejecutado");
//		return name;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public Integer insertTFacturaCancelada(TFacturaCanceladaDto dto){
//		logger.info("ejecutando insertTFacturaCancelada");
//		Integer status = 0;
//		String query = "INSERT INTO public.t_factura_cancelada(  "  +
//				"	factura_id, kfactura, acuse_cancelacion, fecha_creacion, folio_fiscal)  "  +
//				"	VALUES (?, ?, ?, ?, ?) RETURNING kfactura;  "  ;		
//		try {
//			status = this.getJdbcTemplate().queryForObject(query, new Object[] {
//					dto.getFactura_id(), dto.getKfactura(), dto.getAcuse_cancelacion(),
//					dto.getFecha_creacion(), dto.getFolio_fiscal()
//			}, Integer.class);
//			logger.info("ejecutando insertTFacturaCancelada");
//			return status;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public Integer insertTFactura(TFacturaDto dto){
//		logger.info("ejecutando insertTFactura");
//		Integer status = 0;
//		String query = "INSERT INTO public.t_factura(\r\n"
//				+ "	 kdatofiscal, ssucursal, ufoliofactura, \r\n"
//				+ "	ccliente, csucursal, cformapago, msubtotal, \r\n"
//				+ "	mdescuento, mcopago, miva, mtotal, \r\n"
//				+ "	ctipoimpuesto, cconvenio, scadenaoriginal, ssellodigital, \r\n"
//				+ "	centidadlegal, dregistro, cestadoregistro, dcancelacionfactura, \r\n"
//				+ "	user_id_change, user_id, sobservacion, sxml, \r\n"
//				+ "	sxmlsello, sserie, suddi, surl, \r\n"
//				+ "	cformapagocfdi, cmetodopagocfdi, cusocfdi, cregimenfiscal, \r\n"
//				+ "	ctipocomprobantecfdi, ctiporelacioncfdi)\r\n"
//				+ "	VALUES ( ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?, ?, ?, \r\n"
//				+ "			?, ?) RETURNING kfactura;"  ;		
//		try {
//			status = this.getJdbcTemplate().queryForObject(query, new Object[] {
//					dto.getKdatofiscal(),dto.getSsucursal(),dto.getFolio(),
//					dto.getCcliente(),dto.getCsucursal(),dto.getCformapago(),dto.getMsubtotal(),
//					dto.getMdescuento(),dto.getMcopago(),dto.getMiva(),dto.getMtotal(),
//					dto.getCtipoimpuesto(),dto.getCconvenio(),dto.getScadenaoriginal(),dto.getSsellodigital(),
//					dto.getCentidadlegal(),dto.getDregistro(),dto.getCestadoregistro(),new Date(),
//					dto.getUseridchange(),dto.getUserId(),dto.getSobservacion(), dto.getSxml(),
//					dto.getSxmlsello(),dto.getSserie(),dto.getSuddi(),dto.getSurl(),
//					1,1,0,1,
//					1,1
//			}, Integer.class);
//			logger.info("insertTFactura ejecutado: "+status);
//			return status;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	
//	@Override
//	public Integer updateTFactura(Integer kfactura){
//		logger.info("ejecutando updateTFactura [kfactura=" + kfactura + "]");
//		Integer row = null;
//		String query = "UPDATE t_factura SET cestadoregistro=34 WHERE kfactura = ? ;";
//		row = this.getJdbcTemplate().update(query, new Object[] {kfactura});
//		logger.info("ejecutando updateTFactura");
//		return row;
//	}
//	
//	
//	@Override
//	public Integer updateTOrdenSucursalFac(Integer kfactura){
//		logger.info("ejecutando updateTOrdenSucursalFac [kfactura=" + kfactura + "]");
//		Integer row = null;
//		String query = "UPDATE t_orden_sucursal_fac SET cestadoregistro=39 WHERE kfactura = ? ";
//		row = this.getJdbcTemplate().update(query, new Object[] {kfactura});
//		logger.info("ejecutando updateTOrdenSucursalFac");
//		return row;
//	}
//	
//	
//	

//	

//	
//	
//	@Override
//	public Map<String, Object> callFactDetalle(Integer kordensucursal, Integer kfactura){
//		logger.info("ejecutando callFactDetalle");
//		
//		List<SqlParameter> parameters = new ArrayList<SqlParameter>();
//
//		parameters.add(new SqlParameter(Types.INTEGER));
//		parameters.add(new SqlParameter(Types.INTEGER));
//		
////		List<SqlParameter> parameters = Arrays.asList(new SqlParameter(Types.INTEGER));
//	    
//	    return this.getJdbcTemplate().call(new CallableStatementCreator() {
//	      @Override
//	      public CallableStatement createCallableStatement(Connection con) throws SQLException {
//	        CallableStatement cs = con.prepareCall("{call public.fact_detalle(?, ?, 0, 35)}");
//	        cs.setInt(1, kordensucursal);
//	        cs.setInt(2, kfactura);
//	        return cs;
//	      }
//	    }, parameters);
//	}
	
}
