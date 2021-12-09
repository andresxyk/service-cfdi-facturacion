package com.gda.cfdi.dto;

public class DatosMarcaDto {
	
	private String rutaCadenaOriginal;
	private String pasword;
	private String rfcMarca;
	private String razonSocialMarca;
	private String numeroCertificado;
	private String rutaKey;
	private String rutaCer;
	
	public DatosMarcaDto() {
		super();
	}

	public DatosMarcaDto(String rutaCadenaOriginal, String pasword, String rfcMarca, String razonSocialMarca,
			String numeroCertificado, String rutaKey, String rutaCer) {
		super();
		this.rutaCadenaOriginal = rutaCadenaOriginal;
		this.pasword = pasword;
		this.rfcMarca = rfcMarca;
		this.razonSocialMarca = razonSocialMarca;
		this.numeroCertificado = numeroCertificado;
		this.rutaKey = rutaKey;
		this.rutaCer = rutaCer;
	}

	public String getRutaCadenaOriginal() {
		return rutaCadenaOriginal;
	}

	public void setRutaCadenaOriginal(String rutaCadenaOriginal) {
		this.rutaCadenaOriginal = rutaCadenaOriginal;
	}

	public String getPasword() {
		return pasword;
	}

	public void setPasword(String pasword) {
		this.pasword = pasword;
	}

	public String getRfcMarca() {
		return rfcMarca;
	}

	public void setRfcMarca(String rfcMarca) {
		this.rfcMarca = rfcMarca;
	}

	public String getRazonSocialMarca() {
		return razonSocialMarca;
	}

	public void setRazonSocialMarca(String razonSocialMarca) {
		this.razonSocialMarca = razonSocialMarca;
	}

	public String getNumeroCertificado() {
		return numeroCertificado;
	}

	public void setNumeroCertificado(String numeroCertificado) {
		this.numeroCertificado = numeroCertificado;
	}

	public String getRutaKey() {
		return rutaKey;
	}

	public void setRutaKey(String rutaKey) {
		this.rutaKey = rutaKey;
	}

	public String getRutaCer() {
		return rutaCer;
	}

	public void setRutaCer(String rutaCer) {
		this.rutaCer = rutaCer;
	}
	
	

}
