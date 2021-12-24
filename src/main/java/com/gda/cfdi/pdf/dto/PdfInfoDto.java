package com.gda.cfdi.pdf.dto;

public class PdfInfoDto {

	private Boolean complementoConcepto;
	private String dirSucursal;
	private String dirFiscalEmisor;
	private Integer consecutivo;
	private String nombrePaciente;
	private String cadenaOriginal;
	
	public String getCadenaOriginal() {
		return cadenaOriginal;
	}

	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}

	public String getNombrePaciente() {
		return nombrePaciente;
	}

	public void setNombrePaciente(String nombrePaciente) {
		this.nombrePaciente = nombrePaciente;
	}

	public Integer getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getDirFiscalEmisor() {
		return dirFiscalEmisor;
	}

	public void setDirFiscalEmisor(String dirFiscalEmisor) {
		this.dirFiscalEmisor = dirFiscalEmisor;
	}

	public String getDirSucursal() {
		return dirSucursal;
	}

	public void setDirSucursal(String dirSucursal) {
		this.dirSucursal = dirSucursal;
	}

	public Boolean getComplementoConcepto() {
		return complementoConcepto;
	}

	public void setComplementoConcepto(Boolean complementoConcepto) {
		this.complementoConcepto = complementoConcepto;
	}
	
	
	
}
