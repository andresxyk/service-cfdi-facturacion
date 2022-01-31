package com.gda.cfdi.dto;

import java.math.BigDecimal;

public class EstudioDto {
	private Integer cantidad;
	private Integer cexamen;
	private String unidad;
	private String sexamen;
	private BigDecimal valorUnitario;
	private BigDecimal importe;
	private Integer kfactura;
	
	
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Integer getCexamen() {
		return cexamen;
	}
	public void setCexamen(Integer cexamen) {
		this.cexamen = cexamen;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	public String getSexamen() {
		return sexamen;
	}
	public void setSexamen(String sexamen) {
		this.sexamen = sexamen;
	}
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public Integer getKfactura() {
		return kfactura;
	}
	public void setKfactura(Integer kfactura) {
		this.kfactura = kfactura;
	}
	@Override
	public String toString() {
		return "EstudioDto [cantidad=" + cantidad + ", cexamen=" + cexamen + ", unidad=" + unidad + ", sexamen="
				+ sexamen + ", valorUnitario=" + valorUnitario + ", importe=" + importe + ", kfactura=" + kfactura
				+ "]";
	}
	
	
}
