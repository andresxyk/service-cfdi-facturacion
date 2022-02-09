package com.gda.cfdi.dto;

public class ControlFolioDto {
	private String serie;
	private Integer folio;
	private String cveScursal;
	private Integer idSucursal;
	
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getCveScursal() {
		return cveScursal;
	}
	public void setCveScursal(String cveScursal) {
		this.cveScursal = cveScursal;
	}
	public Integer getIdSucursal() {
		return idSucursal;
	}
	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}
	@Override
	public String toString() {
		return "ControlFolioDto [serie=" + serie + ", folio=" + folio + ", cveScursal=" + cveScursal + ", idSucursal="
				+ idSucursal + "]";
	}
}
