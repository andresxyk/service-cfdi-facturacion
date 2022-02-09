package com.gda.cfdi.dto;

import java.util.List;

public class MultiPagoDto {
	private Boolean unicoMovimiento;
	private List<Integer> pagos;
	private PagoDto pago;
	
	public Boolean getUnicoMovimiento() {
		return unicoMovimiento;
	}
	public void setUnicoMovimiento(Boolean unicoMovimiento) {
		this.unicoMovimiento = unicoMovimiento;
	}
	public List<Integer> getPagos() {
		return pagos;
	}
	public void setPagos(List<Integer> pagos) {
		this.pagos = pagos;
	}
	public PagoDto getPago() {
		return pago;
	}
	public void setPago(PagoDto pago) {
		this.pago = pago;
	}
	@Override
	public String toString() {
		return "MultiPagoDto [unicoMovimiento=" + unicoMovimiento + ", pagos=" + pagos + ", pago=" + pago + "]";
	}
}
