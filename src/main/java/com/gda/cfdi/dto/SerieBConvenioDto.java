package com.gda.cfdi.dto;

import java.util.List;

public class SerieBConvenioDto extends FacturaDto {
	
	private Integer idConvenio;
	private Boolean sustitucion;
	private String uuid;
	
	private Boolean retencion;
	
	public SerieBConvenioDto() {
		super();
	}

	protected SerieBConvenioDto(String idUsoCfdi, String formaPago, String montoTotal, Integer idMarca,
			String subTotal, List<ConceptoDto> conceptos, Integer idConvenio) {
		super(idUsoCfdi, formaPago, montoTotal, idMarca, subTotal, conceptos);
		this.idConvenio = idConvenio;
	}
	
	

	public Boolean getRetencion() {
		return retencion;
	}

	public void setRetencion(Boolean retencion) {
		this.retencion = retencion;
	}

	public Boolean getSustitucion() {
		return sustitucion;
	}

	public void setSustitucion(Boolean sustitucion) {
		this.sustitucion = sustitucion;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getIdConvenio() {
		return idConvenio;
	}

	public void setIdConvenio(Integer idConvenio) {
		this.idConvenio = idConvenio;
	}

	@Override
	public String toString() {
		return "SerieBConvenioDto [idConvenio=" + idConvenio + ", idUsoCfdi=" + idUsoCfdi + ", formaPago=" + formaPago
				+ ", montoTotal=" + montoTotal + ", idMarca=" + idMarca + ", subTotal=" + subTotal + ", conceptos="
				+ conceptos + "]";
	}	
}
