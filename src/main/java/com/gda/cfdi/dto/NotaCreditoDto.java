package com.gda.cfdi.dto;

import java.util.List;

public class NotaCreditoDto extends FacturaDto {

	private String uuid;
	private Integer numNotaCredito;
	private Integer numFactura;
	private boolean sustitucion;
	private boolean retencion;

	public NotaCreditoDto() {
		super();
	}


	protected NotaCreditoDto(String formaPago,
			List<ConceptoDto> conceptos, String uuid, Integer numNotaCredito, Integer numFactura, String metodoPago) {

		super(null, formaPago, null, null, null, conceptos);
		this.uuid = uuid;
		this.numNotaCredito = numNotaCredito;
		this.numFactura = numFactura;
		super.metodoPago = metodoPago;

	}
	
	


	public boolean isRetencion() {
		return retencion;
	}


	public void setRetencion(boolean retencion) {
		this.retencion = retencion;
	}


	public boolean isSustitucion() {
		return sustitucion;
	}


	public void setSustitucion(boolean sustitucion) {
		this.sustitucion = sustitucion;
	}


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getNumNotaCredito() {
		return numNotaCredito;
	}

	public void setNumNotaCredito(Integer numNotaCredito) {
		this.numNotaCredito = numNotaCredito;
	}

	public Integer getNumFactura() {
		return numFactura;
	}

	public void setNumFactura(Integer numFactura) {
		this.numFactura = numFactura;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	@Override
	public String toString() {
		return "NotaCreditoDto [uuid=" + uuid + ", numNotaCredito=" + numNotaCredito + ", numFactura=" + numFactura
				+ ", metodoPago=" + metodoPago + ", formaPago=" + formaPago + ", conceptos=" + conceptos.toString() + "]";
	}

}
