package com.gda.cfdi.dto;

import java.util.List;

public class SerieBRfcDto extends FacturaDto {
	
	private String rfc;
	private String idDatoFiscal;
	private Boolean sustitucion;
	private String uuid;
	private Boolean retencion;
	
	public SerieBRfcDto() {
		super();
	}

	protected SerieBRfcDto(String idUsoCfdi, String formaPago, String montoTotal, Integer idMarca,
			String subTotal, List<ConceptoDto> conceptos, String rfc, String idDatoFiscal) {
		super(idUsoCfdi, formaPago, montoTotal, idMarca, subTotal, conceptos);
		this.rfc = rfc;
		this.idDatoFiscal = idDatoFiscal;
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

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getIdDatoFiscal() {
		return idDatoFiscal;
	}

	public void setIdDatoFiscal(String idDatoFiscal) {
		this.idDatoFiscal = idDatoFiscal;
	}

	@Override
	public String toString() {
		return "SerieBRfcDto [rfc=" + rfc + " idDatoFiscal=" + idDatoFiscal+",  idUsoCfdi=" + idUsoCfdi + ", formaPago=" + formaPago + ", montoTotal="
				+ montoTotal + ", idMarca=" + idMarca + ", subTotal=" + subTotal + ", conceptos=" + conceptos + "]";
	}

}
