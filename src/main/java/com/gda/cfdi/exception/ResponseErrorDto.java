package com.gda.cfdi.exception;

public class ResponseErrorDto {

	private String codigo;
	private String descripcion;
	
	public ResponseErrorDto() {
		super();
	}

	public ResponseErrorDto(String codigo, String descripcion) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "ResponseErrorDto [codigo=" + codigo + ", descripcion=" + descripcion + "]";
	}
	
	
	
}
