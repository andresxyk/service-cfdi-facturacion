package com.gda.cfdi.dto;

public class CfdiDto {

	private String xml;
	private String cadenaOriginal;
	private String xmlTimbrado;
	private Integer csucursal;
	private String ssucursal;
	private Integer cusocfdi;
	private Integer kdatofiscal;
	private Integer ccliente;
	private Integer cformapago;
	
	
	public Integer getCformapago() {
		return cformapago;
	}
	public void setCformapago(Integer cformapago) {
		this.cformapago = cformapago;
	}
	public Integer getCcliente() {
		return ccliente;
	}
	public void setCcliente(Integer ccliente) {
		this.ccliente = ccliente;
	}
	public Integer getCsucursal() {
		return csucursal;
	}
	public void setCsucursal(Integer csucursal) {
		this.csucursal = csucursal;
	}
	public String getSsucursal() {
		return ssucursal;
	}
	public void setSsucursal(String ssucursal) {
		this.ssucursal = ssucursal;
	}
	public Integer getCusocfdi() {
		return cusocfdi;
	}
	public void setCusocfdi(Integer cusocfdi) {
		this.cusocfdi = cusocfdi;
	}
	public Integer getKdatofiscal() {
		return kdatofiscal;
	}
	public void setKdatofiscal(Integer kdatofiscal) {
		this.kdatofiscal = kdatofiscal;
	}
	public String getXmlTimbrado() {
		return xmlTimbrado;
	}
	public void setXmlTimbrado(String xmlTimbrado) {
		this.xmlTimbrado = xmlTimbrado;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getCadenaOriginal() {
		return cadenaOriginal;
	}
	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}
	
	
	
}
