package com.gda.cfdi.pdf.dto;

public class ComplementoDatosDto {

	private String emisorSdireccion;
	private String receptorSdireccion;
	private String sCadenaOriginal;
	private Integer uFolioFactura;
	
	
	public String getEmisorSdireccion() {
		return emisorSdireccion;
	}
	public void setEmisorSdireccion(String emisorSdireccion) {
		this.emisorSdireccion = emisorSdireccion;
	}
	public String getReceptorSdireccion() {
		return receptorSdireccion;
	}
	public void setReceptorSdireccion(String receptorSdireccion) {
		this.receptorSdireccion = receptorSdireccion;
	}
	public String getsCadenaOriginal() {
		return sCadenaOriginal;
	}
	public void setsCadenaOriginal(String sCadenaOriginal) {
		this.sCadenaOriginal = sCadenaOriginal;
	}
	public Integer getuFolioFactura() {
		return uFolioFactura;
	}
	public void setuFolioFactura(Integer uFolioFactura) {
		this.uFolioFactura = uFolioFactura;
	}
	@Override
	public String toString() {
		return "ComplementoDatosDto [emisorSdireccion=" + emisorSdireccion + ", receptorSdireccion="
				+ receptorSdireccion + ", sCadenaOriginal=" + sCadenaOriginal + ", uFolioFactura=" + uFolioFactura
				+ "]";
	}

	
}
