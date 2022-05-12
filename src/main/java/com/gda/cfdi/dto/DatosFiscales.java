package com.gda.cfdi.dto;

public class DatosFiscales {


	private Integer kdatofiscal;
	private String srazonsocial;
	private String srfc;
	private String sdireccion;
	private Integer cconvenio;
	
	

	public Integer getCconvenio() {
		return cconvenio;
	}

	public void setCconvenio(Integer cconvenio) {
		this.cconvenio = cconvenio;
	}

	public Integer getKdatofiscal() {
		return kdatofiscal;
	}

	public void setKdatofiscal(Integer kdatofiscal) {
		this.kdatofiscal = kdatofiscal;
	}

	public String getSrazonsocial() {
		return srazonsocial;
	}

	public void setSrazonsocial(String srazonsocial) {
		this.srazonsocial = srazonsocial;
	}

	public String getSrfc() {
		return srfc;
	}

	public void setSrfc(String srfc) {
		this.srfc = srfc;
	}

	public String getSdireccion() {
		return sdireccion;
	}

	public void setSdireccion(String sdireccion) {
		this.sdireccion = sdireccion;
	}

	@Override
	public String toString() {
		return "DatosFiscales [kdatofiscal=" + kdatofiscal + ", srazonsocial=" + srazonsocial + ", srfc=" + srfc
				+ ", sdireccion=" + sdireccion + "]";
	}


}
