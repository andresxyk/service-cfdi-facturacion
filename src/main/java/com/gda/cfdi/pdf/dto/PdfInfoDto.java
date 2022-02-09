package com.gda.cfdi.pdf.dto;

public class PdfInfoDto {

	private Boolean complementoConcepto;
	private String dirSucursal;
	private String dirFiscalEmisor;
	private Integer consecutivo;
	private String nombrePaciente;
	private String cadenaOriginal;
	private Integer kfactura;
	private String descripcionMetodoPago;
	private String descripcionUsoCfdi;
	private String adendaDireccion;
	private String smetodoPago;
	
	
	public String getSmetodoPago() {
		return smetodoPago;
	}

	public void setSmetodoPago(String smetodoPago) {
		this.smetodoPago = smetodoPago;
	}

	public String getAdendaDireccion() {
		return adendaDireccion;
	}

	public void setAdendaDireccion(String adendaDireccion) {
		this.adendaDireccion = adendaDireccion;
	}

	public String getDescripcionUsoCfdi() {
		return descripcionUsoCfdi;
	}

	public void setDescripcionUsoCfdi(String descripcionUsoCfdi) {
		this.descripcionUsoCfdi = descripcionUsoCfdi;
	}

	public String getDescripcionMetodoPago() {
		return descripcionMetodoPago;
	}

	public void setDescripcionMetodoPago(String descripcionMetodoPago) {
		this.descripcionMetodoPago = descripcionMetodoPago;
	}

	public Integer getKfactura() {
		return kfactura;
	}

	public void setKfactura(Integer kfactura) {
		this.kfactura = kfactura;
	}

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
