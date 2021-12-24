package com.gda.cfdi.pdf.exception;

public class ResponseErrorException  extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 588289282012883823L;
	
	private ResponseErrorDto responseErrorDto;
	
	public ResponseErrorException(String codigo, String descripcion) {
		this.responseErrorDto = new ResponseErrorDto(codigo, descripcion);
	}

	public ResponseErrorException(ResponseErrorDto responseErrorDto) {
		super();
		this.responseErrorDto = responseErrorDto;
	}

	public ResponseErrorDto getResponseErrorDto() {
		return responseErrorDto;
	}

	public void setResponseErrorDto(ResponseErrorDto responseErrorDto) {
		this.responseErrorDto = responseErrorDto;
	}

	
	
}
