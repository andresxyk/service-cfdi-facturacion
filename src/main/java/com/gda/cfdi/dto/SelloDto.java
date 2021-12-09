package com.gda.cfdi.dto;

public class SelloDto {
	private String selloCFDI;
	private String cadenaOriginal;
	
	
	
	public SelloDto() {
		super();
	}
	public SelloDto(String selloCFDI, String cadenaOriginal) {
		super();
		this.selloCFDI = selloCFDI;
		this.cadenaOriginal = cadenaOriginal;
	}
	public String getSelloCFDI() {
		return selloCFDI;
	}
	public void setSelloCFDI(String selloCFDI) {
		this.selloCFDI = selloCFDI;
	}
	public String getCadenaOriginal() {
		return cadenaOriginal;
	}
	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}
	
	
}
