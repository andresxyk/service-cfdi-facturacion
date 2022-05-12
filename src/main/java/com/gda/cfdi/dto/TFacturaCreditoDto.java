package com.gda.cfdi.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TFacturaCreditoDto {
	private Integer idFactura;
	private Integer idDatoFiscal;
	private String cveSucursal;
	private Integer folio;
	private Integer idCliente;
	private Integer idSucursal;
	private Integer idFormaPago;
	private BigDecimal subtotal;
	private BigDecimal total;
	private Integer idConvenio;
	private String cadenaOriginal;
	private String sello;
	private Integer centidadlegal;
	private String xml;
	private String xmlTimbrado;
	private String serie;
	private String uuid;
	private Integer idMetodoPago;
	private Integer idUsoCfdi;
	private Integer userId;
	private Date dregistro;
	
	
	
	public Date getDregistro() {
		return dregistro;
	}
	public void setDregistro(Date dregistro) {
		this.dregistro = dregistro;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCentidadlegal() {
		return centidadlegal;
	}
	public void setCentidadlegal(Integer centidadlegal) {
		this.centidadlegal = centidadlegal;
	}
	public Integer getIdDatoFiscal() {
		return idDatoFiscal;
	}
	public void setIdDatoFiscal(Integer idDatoFiscal) {
		this.idDatoFiscal = idDatoFiscal;
	}
	public String getCveSucursal() {
		return cveSucursal;
	}
	public void setCveSucursal(String cveSucursal) {
		this.cveSucursal = cveSucursal;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public Integer getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
	public Integer getIdSucursal() {
		return idSucursal;
	}
	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}
	public Integer getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(Integer idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Integer getIdConvenio() {
		return idConvenio;
	}
	public void setIdConvenio(Integer idConvenio) {
		this.idConvenio = idConvenio;
	}
	public String getCadenaOriginal() {
		return cadenaOriginal;
	}
	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}
	public String getSello() {
		return sello;
	}
	public void setSello(String sello) {
		this.sello = sello;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getXmlTimbrado() {
		return xmlTimbrado;
	}
	public void setXmlTimbrado(String xmlTimbrado) {
		this.xmlTimbrado = xmlTimbrado;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getIdMetodoPago() {
		return idMetodoPago;
	}
	public void setIdMetodoPago(Integer idMetodoPago) {
		this.idMetodoPago = idMetodoPago;
	}
	public Integer getIdUsoCfdi() {
		return idUsoCfdi;
	}
	public void setIdUsoCfdi(Integer idUsoCfdi) {
		this.idUsoCfdi = idUsoCfdi;
	}
	
	public Integer getIdFactura() {
		return idFactura;
	}
	public void setIdFactura(Integer idFactura) {
		this.idFactura = idFactura;
	}
	@Override
	public String toString() {
		return "TFacturaDto [idFactura=" + idFactura + ", idDatoFiscal=" + idDatoFiscal + ", cveSucursal=" + cveSucursal
				+ ", folio=" + folio + ", idCliente=" + idCliente + ", idSucursal=" + idSucursal + ", idFormaPago="
				+ idFormaPago + ", subtotal=" + subtotal + ", total=" + total + ", idConvenio=" + idConvenio
				+ ", cadenaOriginal=" + cadenaOriginal + ", sello=" + sello + ", xml=" + xml + ", xmlTimbrado="
				+ xmlTimbrado + ", serie=" + serie + ", uuid=" + uuid + ", idMetodoPago=" + idMetodoPago
				+ ", idUsoCfdi=" + idUsoCfdi + ", centidadlegal=" + centidadlegal + "]";
	}


	
	
}
