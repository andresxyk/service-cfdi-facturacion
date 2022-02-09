package com.gda.cfdi.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class PagoDto {

	private Integer idPago;
	private List<PagoFacturaDto> facturasRelacionadas;
	private Timestamp fechaPago;
	private String fechaPagoStr;
	private String fechaRegistro;
	private String formaPago;
	private String moneda;
	private BigDecimal monto;
	private Integer idControlFolio;
	private Boolean complementoGenerado;
	private String rfcBanco;
	private String nombreBanco;
	private String numeroCuenta;
	private String numeroOperacion;
	private Integer idPadre;
	private Integer idEstatus;
	private Integer kfactura;
	private Integer entidadLegal;
	private String suddi;
	
	

	public String getSuddi() {
		return suddi;
	}
	public void setSuddi(String suddi) {
		this.suddi = suddi;
	}
	public String getNumeroOperacion() {
		return numeroOperacion;
	}
	public void setNumeroOperacion(String numeroOperacion) {
		this.numeroOperacion = numeroOperacion;
	}
	public Integer getKfactura() {
		return kfactura;
	}
	public void setKfactura(Integer kfactura) {
		this.kfactura = kfactura;
	}
	
	public Integer getEntidadLegal() {
		return entidadLegal;
	}
	public void setEntidadLegal(Integer entidadLegal) {
		this.entidadLegal = entidadLegal;
	}
	public List<PagoFacturaDto> getFacturasRelacionadas() {
		return facturasRelacionadas;
	}
	public void setFacturasRelacionadas(List<PagoFacturaDto> facturasRelacionadas) {
		this.facturasRelacionadas = facturasRelacionadas;
	}
	public Timestamp getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	public Integer getIdControlFolio() {
		return idControlFolio;
	}
	public void setIdControlFolio(Integer idControlFolio) {
		this.idControlFolio = idControlFolio;
	}
	
	public Boolean getComplementoGenerado() {
		return complementoGenerado;
	}
	public void setComplementoGenerado(Boolean complementoGenerado) {
		this.complementoGenerado = complementoGenerado;
	}
	public String getRfcBanco() {
		return rfcBanco;
	}
	public void setRfcBanco(String rfcBanco) {
		this.rfcBanco = rfcBanco;
	}
	public String getNombreBanco() {
		return nombreBanco;
	}
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}
	public String getNumeroCuenta() {
		return numeroCuenta;
	}
	public void setNumeroCuenta(String numeroCuenta) {
		this.numeroCuenta = numeroCuenta;
	}
	
	public Integer getIdPago() {
		return idPago;
	}
	public void setIdPago(Integer idPago) {
		this.idPago = idPago;
	}
	
	public String getFechaPagoStr() {
		return fechaPagoStr;
	}
	public void setFechaPagoStr(String fechaPagoStr) {
		this.fechaPagoStr = fechaPagoStr;
	}
	
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Integer getIdPadre() {
		return idPadre;
	}
	public void setIdPadre(Integer idPadre) {
		this.idPadre = idPadre;
	}
	
	public Integer getIdEstatus() {
		return idEstatus;
	}
	public void setIdEstatus(Integer idEstatus) {
		this.idEstatus = idEstatus;
	}
	@Override
	public String toString() {
		return "PagoDto [facturasRelacionadas=" + facturasRelacionadas + ", fechaPago=" + fechaPago + ", formaPago="
				+ formaPago + ", moneda=" + moneda + ", monto=" + monto + ", idControlFolio=" + idControlFolio
				+ ", complementoGenerado=" + complementoGenerado + ", rfcBanco=" + rfcBanco + ", nombreBanco="
				+ nombreBanco + ", numeroCuenta=" + numeroCuenta + "]";
	}	
}
