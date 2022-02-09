package com.gda.cfdi.dto;

public class FacturaSelloDto {
	private String certificadoB64;
	private String sello;
	private String cadenaOriginal;
	private Integer marca;
	
	
	public Integer getMarca() {
		return marca;
	}
	public void setMarca(Integer marca) {
		this.marca = marca;
	}
	public String getCertificadoB64() {
		return certificadoB64;
	}
	public void setCertificadoB64(String certificadoB64) {
		this.certificadoB64 = certificadoB64;
	}
	public String getSello() {
		return sello;
	}
	public void setSello(String sello) {
		this.sello = sello;
	}
	public String getCadenaOriginal() {
		return cadenaOriginal;
	}
	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}
}
