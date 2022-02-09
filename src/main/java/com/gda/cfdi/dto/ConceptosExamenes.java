package com.gda.cfdi.dto;

import java.math.BigDecimal;

public class ConceptosExamenes {

	private String codigo;
	private Integer cantidad;
	private String unidadMedida;
	private String unidad;
	private String concepto;
	private BigDecimal precioUnitario;
	private BigDecimal importe;
	private String clave;
	private BigDecimal iva;
	
	public BigDecimal getIva() {
		return iva;
	}
	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public String getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	@Override
	public String toString() {
		return "ConceptosExamenes [codigo=" + codigo + ", cantidad=" + cantidad + ", unidadMedida=" + unidadMedida
				+ ", unidad=" + unidad + ", concepto=" + concepto + ", precioUnitario=" + precioUnitario + ", importe="
				+ importe + ", clave=" + clave + ", iva=" + iva + "]";
	}	

}
