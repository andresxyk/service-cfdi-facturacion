package com.gda.cfdi.dto;

import java.math.BigDecimal;

public class PagoFacturaDto {	
	private String idDocumento;
	private String serie;
	private String folio;
	private String moneda;
	private String metodoPago;
	private Integer numParcialidad;
	private BigDecimal importeSaldoAnterior;
	private BigDecimal importePagado;
	private BigDecimal saldoInsoluto;
	private String xml;
	private Integer estatus;
	private Integer csucursal;
	private BigDecimal manticipo;
	private BigDecimal mpago;
	private Integer cestadoregistro;
	private Integer kdatofiscal;
	
	
	public Integer getKdatofiscal() {
		return kdatofiscal;
	}
	public void setKdatofiscal(Integer kdatofiscal) {
		this.kdatofiscal = kdatofiscal;
	}
	public Integer getCsucursal() {
		return csucursal;
	}
	public void setCsucursal(Integer csucursal) {
		this.csucursal = csucursal;
	}
	public BigDecimal getManticipo() {
		return manticipo;
	}
	public void setManticipo(BigDecimal manticipo) {
		this.manticipo = manticipo;
	}
	public BigDecimal getMpago() {
		return mpago;
	}
	public void setMpago(BigDecimal mpago) {
		this.mpago = mpago;
	}
	public Integer getCestadoregistro() {
		return cestadoregistro;
	}
	public void setCestadoregistro(Integer cestadoregistro) {
		this.cestadoregistro = cestadoregistro;
	}
	public String getIdDocumento() {
		return idDocumento;
	}
	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getMetodoPago() {
		return metodoPago;
	}
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}
	public Integer getNumParcialidad() {
		return numParcialidad;
	}
	public void setNumParcialidad(Integer numParcialidad) {
		this.numParcialidad = numParcialidad;
	}
	public BigDecimal getImporteSaldoAnterior() {
		return importeSaldoAnterior;
	}
	public void setImporteSaldoAnterior(BigDecimal importeSaldoAnterior) {
		this.importeSaldoAnterior = importeSaldoAnterior;
	}
	public BigDecimal getImportePagado() {
		return importePagado;
	}
	public void setImportePagado(BigDecimal importePagado) {
		this.importePagado = importePagado;
	}
	public BigDecimal getSaldoInsoluto() {
		return saldoInsoluto;
	}
	public void setSaldoInsoluto(BigDecimal saldoInsoluto) {
		this.saldoInsoluto = saldoInsoluto;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public Integer getEstatus() {
		return estatus;
	}
	public void setEstatus(Integer estatus) {
		this.estatus = estatus;
	}
	@Override
	public String toString() {
		return "PagoFacturaDto [idDocumento=" + idDocumento + ", serie=" + serie + ", folio=" + folio + ", moneda="
				+ moneda + ", metodoPago=" + metodoPago + ", numParcialidad=" + numParcialidad
				+ ", importeSaldoAnterior=" + importeSaldoAnterior + ", importePagado=" + importePagado
				+ ", saldoInsoluto=" + saldoInsoluto + ", xml=" + xml + ", estatus=" + estatus + "]";
	}
}
