package com.gda.cfdi.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TOrdenExamenSucursalDto {

	private Integer kordenexamensucursal;
	private String sexamen;
	private BigDecimal msubtotal;
	private BigDecimal mdescuentopromocion;
	private BigDecimal mdescuentoempresa;
	private BigDecimal mdescuentomedico;
	private BigDecimal mfacturaempresa;
	private BigDecimal mpagopaciente;
	private BigDecimal miva;
	private BigDecimal mtotal;
	private int umuestra;
	private Date dresultadoentrega;
	private Date dregistro;
	private Integer kpromocion;
	private String smotivocancelacion;
	private Integer cperfil;
	private BigDecimal mcomisionmedico;
	private short uvolumenexamen;
	private int upuntosmedico;
	private Date dtomamuestrainicio;
	private Date dtomamuestratermino;
	private String sloginName;
	private int csucursaltranslado;
	private int csucursalentregaresultado;
	private Date dresultadoentregatoma;
	private BigDecimal mcostolaboratorio;
	private Short cmoneda;
	private int userIdAutoriza;
	private boolean bventacruzada;
	private Integer cexamen;
	private Integer cconvenio;
	
	
	public Integer getCconvenio() {
		return cconvenio;
	}
	public void setCconvenio(Integer cconvenio) {
		this.cconvenio = cconvenio;
	}
	public Integer getCexamen() {
		return cexamen;
	}
	public void setCexamen(Integer cexamen) {
		this.cexamen = cexamen;
	}
	public Integer getKordenexamensucursal() {
		return kordenexamensucursal;
	}
	public void setKordenexamensucursal(Integer kordenexamensucursal) {
		this.kordenexamensucursal = kordenexamensucursal;
	}
	public String getSexamen() {
		return sexamen;
	}
	public void setSexamen(String sexamen) {
		this.sexamen = sexamen;
	}
	public BigDecimal getMsubtotal() {
		return msubtotal;
	}
	public void setMsubtotal(BigDecimal msubtotal) {
		this.msubtotal = msubtotal;
	}
	public BigDecimal getMdescuentopromocion() {
		return mdescuentopromocion;
	}
	public void setMdescuentopromocion(BigDecimal mdescuentopromocion) {
		this.mdescuentopromocion = mdescuentopromocion;
	}
	public BigDecimal getMdescuentoempresa() {
		return mdescuentoempresa;
	}
	public void setMdescuentoempresa(BigDecimal mdescuentoempresa) {
		this.mdescuentoempresa = mdescuentoempresa;
	}
	public BigDecimal getMdescuentomedico() {
		return mdescuentomedico;
	}
	public void setMdescuentomedico(BigDecimal mdescuentomedico) {
		this.mdescuentomedico = mdescuentomedico;
	}
	public BigDecimal getMfacturaempresa() {
		return mfacturaempresa;
	}
	public void setMfacturaempresa(BigDecimal mfacturaempresa) {
		this.mfacturaempresa = mfacturaempresa;
	}
	public BigDecimal getMpagopaciente() {
		return mpagopaciente;
	}
	public void setMpagopaciente(BigDecimal mpagopaciente) {
		this.mpagopaciente = mpagopaciente;
	}
	public BigDecimal getMiva() {
		return miva;
	}
	public void setMiva(BigDecimal miva) {
		this.miva = miva;
	}
	public BigDecimal getMtotal() {
		return mtotal;
	}
	public void setMtotal(BigDecimal mtotal) {
		this.mtotal = mtotal;
	}
	public int getUmuestra() {
		return umuestra;
	}
	public void setUmuestra(int umuestra) {
		this.umuestra = umuestra;
	}
	public Date getDresultadoentrega() {
		return dresultadoentrega;
	}
	public void setDresultadoentrega(Date dresultadoentrega) {
		this.dresultadoentrega = dresultadoentrega;
	}
	public Date getDregistro() {
		return dregistro;
	}
	public void setDregistro(Date dregistro) {
		this.dregistro = dregistro;
	}
	public Integer getKpromocion() {
		return kpromocion;
	}
	public void setKpromocion(Integer kpromocion) {
		this.kpromocion = kpromocion;
	}
	public String getSmotivocancelacion() {
		return smotivocancelacion;
	}
	public void setSmotivocancelacion(String smotivocancelacion) {
		this.smotivocancelacion = smotivocancelacion;
	}
	public Integer getCperfil() {
		return cperfil;
	}
	public void setCperfil(Integer cperfil) {
		this.cperfil = cperfil;
	}
	public BigDecimal getMcomisionmedico() {
		return mcomisionmedico;
	}
	public void setMcomisionmedico(BigDecimal mcomisionmedico) {
		this.mcomisionmedico = mcomisionmedico;
	}
	public short getUvolumenexamen() {
		return uvolumenexamen;
	}
	public void setUvolumenexamen(short uvolumenexamen) {
		this.uvolumenexamen = uvolumenexamen;
	}
	public int getUpuntosmedico() {
		return upuntosmedico;
	}
	public void setUpuntosmedico(int upuntosmedico) {
		this.upuntosmedico = upuntosmedico;
	}
	public Date getDtomamuestrainicio() {
		return dtomamuestrainicio;
	}
	public void setDtomamuestrainicio(Date dtomamuestrainicio) {
		this.dtomamuestrainicio = dtomamuestrainicio;
	}
	public Date getDtomamuestratermino() {
		return dtomamuestratermino;
	}
	public void setDtomamuestratermino(Date dtomamuestratermino) {
		this.dtomamuestratermino = dtomamuestratermino;
	}
	public String getSloginName() {
		return sloginName;
	}
	public void setSloginName(String sloginName) {
		this.sloginName = sloginName;
	}
	public int getCsucursaltranslado() {
		return csucursaltranslado;
	}
	public void setCsucursaltranslado(int csucursaltranslado) {
		this.csucursaltranslado = csucursaltranslado;
	}
	public int getCsucursalentregaresultado() {
		return csucursalentregaresultado;
	}
	public void setCsucursalentregaresultado(int csucursalentregaresultado) {
		this.csucursalentregaresultado = csucursalentregaresultado;
	}
	public Date getDresultadoentregatoma() {
		return dresultadoentregatoma;
	}
	public void setDresultadoentregatoma(Date dresultadoentregatoma) {
		this.dresultadoentregatoma = dresultadoentregatoma;
	}
	public BigDecimal getMcostolaboratorio() {
		return mcostolaboratorio;
	}
	public void setMcostolaboratorio(BigDecimal mcostolaboratorio) {
		this.mcostolaboratorio = mcostolaboratorio;
	}
	public Short getCmoneda() {
		return cmoneda;
	}
	public void setCmoneda(Short cmoneda) {
		this.cmoneda = cmoneda;
	}
	public int getUserIdAutoriza() {
		return userIdAutoriza;
	}
	public void setUserIdAutoriza(int userIdAutoriza) {
		this.userIdAutoriza = userIdAutoriza;
	}
	public boolean isBventacruzada() {
		return bventacruzada;
	}
	public void setBventacruzada(boolean bventacruzada) {
		this.bventacruzada = bventacruzada;
	}

	
	
}
