package com.gda.cfdi.dto;

public class DatosFiscalesDto {
	private Integer kdatofiscalonly;
	private Integer kdatofiscal;
	private String stipopago;
	
	
	public Integer getKdatofiscalonly() {
		return kdatofiscalonly;
	}
	public void setKdatofiscalonly(Integer kdatofiscalonly) {
		this.kdatofiscalonly = kdatofiscalonly;
	}
	public Integer getKdatofiscal() {
		return kdatofiscal;
	}
	public void setKdatofiscal(Integer kdatofiscal) {
		this.kdatofiscal = kdatofiscal;
	}
	public String getStipopago() {
		return stipopago;
	}
	public void setStipopago(String stipopago) {
		this.stipopago = stipopago;
	}
	@Override
	public String toString() {
		return "DatosFiscalesDto [kdatofiscalonly=" + kdatofiscalonly + ", kdatofiscal=" + kdatofiscal + ", stipopago="
				+ stipopago + "]";
	}
	
	
}
