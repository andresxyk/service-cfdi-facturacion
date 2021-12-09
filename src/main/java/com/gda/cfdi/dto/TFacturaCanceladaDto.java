package com.gda.cfdi.dto;

import java.util.Date;

public class TFacturaCanceladaDto {

	private String factura_id;
	private Integer kfactura;
	private String acuse_cancelacion;
	private Date fecha_creacion;
	private String folio_fiscal;
	
	
	public String getFactura_id() {
		return factura_id;
	}
	public void setFactura_id(String factura_id) {
		this.factura_id = factura_id;
	}
	public Integer getKfactura() {
		return kfactura;
	}
	public void setKfactura(Integer kfactura) {
		this.kfactura = kfactura;
	}
	public String getAcuse_cancelacion() {
		return acuse_cancelacion;
	}
	public void setAcuse_cancelacion(String acuse_cancelacion) {
		this.acuse_cancelacion = acuse_cancelacion;
	}
	public Date getFecha_creacion() {
		return fecha_creacion;
	}
	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}
	public String getFolio_fiscal() {
		return folio_fiscal;
	}
	public void setFolio_fiscal(String folio_fiscal) {
		this.folio_fiscal = folio_fiscal;
	}
	
	
	
	
}
