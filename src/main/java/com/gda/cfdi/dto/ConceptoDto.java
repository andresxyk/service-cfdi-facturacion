package com.gda.cfdi.dto;

public class ConceptoDto {
	private String claveProdcuto;
	private String codigoOlabAzteca;
	private String claveUnidad;
	private String unidad;
	private String nombreConcepto;
	private String precioUnitatorio;
	private Integer cantidad;
	private String iva;
	
	public String getClaveProdcuto() {
		return claveProdcuto;
	}
	public void setClaveProdcuto(String claveProdcuto) {
		this.claveProdcuto = claveProdcuto;
	}
	public String getCodigoOlabAzteca() {
		return codigoOlabAzteca;
	}
	public void setCodigoOlabAzteca(String codigoOlabAzteca) {
		this.codigoOlabAzteca = codigoOlabAzteca;
	}
	public String getClaveUnidad() {
		return claveUnidad;
	}
	public void setClaveUnidad(String claveUnidad) {
		this.claveUnidad = claveUnidad;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	public String getNombreConcepto() {
		return nombreConcepto;
	}
	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}
	public String getPrecioUnitatorio() {
		return precioUnitatorio;
	}
	public void setPrecioUnitatorio(String precioUnitatorio) {
		this.precioUnitatorio = precioUnitatorio;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}
	@Override
	public String toString() {
		return "ConceptoDto [claveProdcuto=" + claveProdcuto + ", codigoOlabAzteca=" + codigoOlabAzteca
				+ ", claveUnidad=" + claveUnidad + ", unidad=" + unidad + ", nombreConcepto=" + nombreConcepto
				+ ", precioUnitatorio=" + precioUnitatorio + ", cantidad=" + cantidad + ", iva=" + iva + "]";
	}
}
