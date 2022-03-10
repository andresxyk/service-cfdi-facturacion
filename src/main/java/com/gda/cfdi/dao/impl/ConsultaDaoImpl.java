package com.gda.cfdi.dao.impl;

import org.springframework.stereotype.Repository;

@Repository("consultaDaoImpl")
public class ConsultaDaoImpl 
//extends JdbcDaoSupport implements IConsultaDao
{

//	@Autowired
//	DataSource dataSource;
//	
//	@Autowired
//	Environment env;
//	
//	@PostConstruct
//	private void initialize(){
//		setDataSource(dataSource);
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public CControlFolioDto getControlFolioDto(Integer csucursal) {
//		CControlFolioDto dto = null;
//		logger.info("ejecutando getControlFolioDto:: csucursal="+csucursal);		
//		String query = "SELECT * FROM C_Control_Folio WHERE csucursal = ? "
//				+ "AND cestadoregistro=31 ORDER BY ccontrolfolio DESC";
//		try {
//			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {csucursal}, new CControlFolioMapper());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getControlFolioDto ejecutado");
//		return dto;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TPagoPacienteDto> getTPagoPacienteDto(Integer kordensucursal) {
//		logger.info("ejecutando getTPagoPacienteDto");
//		List<TPagoPacienteDto> list = null;
//		String query = "SELECT * FROM T_Pago_Paciente  WHERE \r\n"
//				+ "kordensucursal = ? ORDER BY kpagopaciente DESC" ;
//		try {
//			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TPagoPacienteMapper());
//			logger.info("getTPagoPacienteDto ejecutado:"+list.size());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		return list;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TOrdenSucursalDto> getListTOrdenSucursalByKordensucursal(Integer kordensucursal){
//		logger.info("ejecutando getListTOrdenSucursalByKordensucursal");
//		List<TOrdenSucursalDto> list;
//		String query = "select * from t_orden_sucursal where kordensucursal = ?" ;
//		list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TOrdenSucursalMapper());
//		logger.info("getListTOrdenSucursalByKordensucursal ejecutado:"+list.size());
//		return list;
//	}
//	
//	
//	@Override
//	public Integer updateCControlFolio(Integer ccontrolfolio, Integer ufolioactual){
//		logger.info("ejecutando updateCControlFolio [ccontrolfolio=" + ccontrolfolio + ", ufolioactual="+ufolioactual+"]");
//		Integer row = null;
//		String query = "UPDATE c_control_folio SET ufolioactual=? WHERE ccontrolfolio = ? ";
//		row = this.getJdbcTemplate().update(query, new Object[] {ufolioactual,ccontrolfolio});
//		logger.info("ejecutando updateCControlFolio");
//		return row;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public String getCPostalByCsucursal(Integer csucursal) {
//		String cpostal = null;
//		logger.info("ejecutando getCPostalByCsucursal:: csucursal="+csucursal);		
//		String query = "select ccp.cpostal from c_sucursal cs\r\n"
//				+ "inner join c_codigo_postal ccp on cs.ccodigopostal = ccp.ccodigopostal\r\n"
//				+ "where csucursal = ?";
//		try {
//			cpostal = this.getJdbcTemplate().queryForObject(query, new Object[] {csucursal}, String.class);			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getCPostalByCsucursal ejecutado");
//		return cpostal;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public CTipoPagoDto getCTipoPagoById(Integer id) {
//		CTipoPagoDto dto = null;
//		logger.info("ejecutando getCTipoPagoById:: id="+id);
//		
//		String query = "select * from c_tipo_pago where ctipopago = ? ;";
//		try {
//			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {id}, new CTipoPagoMapper());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getCTipoPagoById ejecutado");
//		return dto;
//	}
//	
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public CFormaPagoCfdiDto getCFormaPagoCfdiById(Integer id) {
//		CFormaPagoCfdiDto dto = null;
//		logger.info("ejecutando getCFormaPagoCfdiById:: id="+id);
//		
//		String query = "select * from c_forma_pago_cfdi where cformapagocfdi = ?;";
//		try {
//			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {id}, new CFormaPagoCfdiMapper());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getCFormaPagoCfdiById ejecutado");
//		return dto;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TFacturaCanceladaDto> getFacturasCanceladasByKorden(Integer kordensucursal){
//		logger.info("ejecutando getFacturasCanceladasByKorden");
//		List<TFacturaCanceladaDto> list;
//		String query = "SELECT tc.factura_id,tc.kfactura,tc.acuse_cancelacion,tc.fecha_creacion,tc.folio_fiscal FROM t_factura_cancelada tc "
//				+ "INNER JOIN t_orden_sucursal_fac tosf  "
//				+ "ON tc.kfactura = tosf.kfactura\r\n" + 
//				"where tosf.kordensucursal = ? ORDER BY fecha_creacion DESC" ;
//		list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TFacturaCanceladaMapper());
//		logger.info("getFacturasCanceladasByKorden ejecutado:"+list.size());
//		return list;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TOrdenExamenSucursalDto> getTOrdenExamenSucursalByKOrdenSucursal(Integer kordensucursal) {
//		logger.info("ejecutando getTOrdenExamenSucursalByKOrdenSucursal: kordensucursal="+kordensucursal);
//		List<TOrdenExamenSucursalDto> list = null;
//		String query = "SELECT * FROM t_orden_examen_sucursal "
//				+ "WHERE kordensucursal = ? "
//				+ "ORDER BY 1 DESC" ;
//		try {
//			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TOrdenExamenSucursalMapper());
//			logger.info("getTOrdenExamenSucursalByKOrdenSucursal ejecutado:"+list.size());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		return list;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public List<TSociedadCivilDto> getTSociedadCivilByKOrdenSucursal(Integer kordensucursal) {
//		logger.info("ejecutando getTSociedadCivilByKOrdenSucursal");
//		List<TSociedadCivilDto> list = null;
//		String query = "SELECT toessc.* " +
//				"FROM t_orden_sucursal tos INNER JOIN public.t_orden_examen_sucursal toes ON tos.kordensucursal=toes.kordensucursal " +
//				"INNER JOIN sociedadcivil.t_orden_examen_sucursal_sociedadcivil toessc ON toes.kordenexamensucursal = toessc.kordenexamensucursal " +
//				"WHERE tos.kordensucursal IN (?) " ;
//		try {
//			list = this.getJdbcTemplate().query(query, new Object[]{kordensucursal},new TSociedadCivilMapper());
//			logger.info("getTSociedadCivilByKOrdenSucursal ejecutado:");			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		return list;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public CClaveProductoServicioSatDto findCClaveProductoServicioSatById(Integer cexamen) {
//		CClaveProductoServicioSatDto dto = null;
//		logger.info("ejecutando findCClaveProductoServicioSatById:: cexamen="+cexamen);
//		
//		String query = "SELECT ccpss.* FROM c_clave_producto_servicio_sat ccpss\r\n"
//				+ "inner join web2lablis.c_examen ce on ce.cclaveproductoserviciosat = ccpss.cclaveproductoserviciosat\r\n"
//				+ "where ce.cexamen = ?";
//		try {
//			dto = this.getJdbcTemplate().queryForObject(query, new Object[] {cexamen}, new CClaveProductoServicioSatMapper());			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//
//		logger.info("findCClaveProductoServicioSatById ejecutado");
//		return dto;
//	}
//	
//	@Override
//	@SuppressWarnings("deprecation")
//	public Integer getCTipoConvenioByConvenio(Integer cconvenio) {
//		Integer cpostal = null;
//		logger.info("ejecutando getCTipoConvenioByConvenio:: cconvenio="+cconvenio);		
//		String query = "select ctipoconvenio from c_convenio where cconvenio = ?";
//		try {
//			cpostal = this.getJdbcTemplate().queryForObject(query, new Object[] {cconvenio}, Integer.class);			
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			return null;
//		}
//		logger.info("getCTipoConvenioByConvenio ejecutado");
//		return cpostal;
//	}
//	
	
	
}
