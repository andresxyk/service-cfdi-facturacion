package com.gda.cfdi.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TPagoPacienteDto {

	private Integer kpagopaciente;
	private BigDecimal mpagopacientetotal;
	private BigDecimal manticipo;
	private BigDecimal mpagopacienteparcial;
	private BigDecimal mdevolucionpaciente;
	private BigDecimal msaldo;
	private Date dregistro;
	private String sdigitostarjeta;
	private int kcortecajaparcial;
	private Integer cestadoregistro;
	private Integer user_id;
	private Integer ctipopago;
	
	
	public Integer getCtipopago() {
		return ctipopago;
	}
	public void setCtipopago(Integer ctipopago) {
		this.ctipopago = ctipopago;
	}
	public Integer getKpagopaciente() {
		return kpagopaciente;
	}
	public void setKpagopaciente(Integer kpagopaciente) {
		this.kpagopaciente = kpagopaciente;
	}
	public BigDecimal getMpagopacientetotal() {
		return mpagopacientetotal;
	}
	public void setMpagopacientetotal(BigDecimal mpagopacientetotal) {
		this.mpagopacientetotal = mpagopacientetotal;
	}
	public BigDecimal getManticipo() {
		return manticipo;
	}
	public void setManticipo(BigDecimal manticipo) {
		this.manticipo = manticipo;
	}
	public BigDecimal getMpagopacienteparcial() {
		return mpagopacienteparcial;
	}
	public void setMpagopacienteparcial(BigDecimal mpagopacienteparcial) {
		this.mpagopacienteparcial = mpagopacienteparcial;
	}
	public BigDecimal getMdevolucionpaciente() {
		return mdevolucionpaciente;
	}
	public void setMdevolucionpaciente(BigDecimal mdevolucionpaciente) {
		this.mdevolucionpaciente = mdevolucionpaciente;
	}
	public BigDecimal getMsaldo() {
		return msaldo;
	}
	public void setMsaldo(BigDecimal msaldo) {
		this.msaldo = msaldo;
	}
	public Date getDregistro() {
		return dregistro;
	}
	public void setDregistro(Date dregistro) {
		this.dregistro = dregistro;
	}
	public String getSdigitostarjeta() {
		return sdigitostarjeta;
	}
	public void setSdigitostarjeta(String sdigitostarjeta) {
		this.sdigitostarjeta = sdigitostarjeta;
	}
	public int getKcortecajaparcial() {
		return kcortecajaparcial;
	}
	public void setKcortecajaparcial(int kcortecajaparcial) {
		this.kcortecajaparcial = kcortecajaparcial;
	}
	public Integer getCestadoregistro() {
		return cestadoregistro;
	}
	public void setCestadoregistro(Integer cestadoregistro) {
		this.cestadoregistro = cestadoregistro;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	
	

}
