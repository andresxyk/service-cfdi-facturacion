package com.gda.cfdi.dto;

import java.util.List;

public class FacturaDto {
	protected String idUsoCfdi;
	protected String formaPago;
	protected String metodoPago;
	protected String montoTotal;
	protected Integer idMarca;
	protected String subTotal;
	protected List<ConceptoDto> conceptos;

	protected FacturaDto() {
		super();
	}
	
	protected FacturaDto(String idUsoCfdi, String formaPago, String montoTotal, Integer idMarca, String subTotal,
			List<ConceptoDto> conceptos) {
		super();
		this.idUsoCfdi = idUsoCfdi;
		this.formaPago = formaPago;
		this.montoTotal = montoTotal;
		this.idMarca = idMarca;
		this.subTotal = subTotal;
		this.conceptos = conceptos;
	}

	public String getIdUsoCfdi() {
		return idUsoCfdi;
	}

	public void setIdUsoCfdi(String idUsoCfdi) {
		this.idUsoCfdi = idUsoCfdi;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public String getMontoTotal() {
		return montoTotal;
	}

	public void setMontoTotal(String montoTotal) {
		this.montoTotal = montoTotal;
	}

	public Integer getIdMarca() {
		return idMarca;
	}

	public void setIdMarca(Integer idMarca) {
		this.idMarca = idMarca;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public List<ConceptoDto> getConceptos() {
		return conceptos;
	}

	public void setConceptos(List<ConceptoDto> conceptos) {
		this.conceptos = conceptos;
	}
}
