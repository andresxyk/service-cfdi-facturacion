package com.gda.cfdi.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TFacturaDto {
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
	private Integer kdatofiscal;
	private String ssucursal;
	private Integer ccliente;
	private Integer csucursal;
	private Integer cformapago;
	private BigDecimal msubtotal;
	private BigDecimal mdescuento;
	private BigDecimal mcopago;
	private BigDecimal miva;
	private BigDecimal mtotal;
	private Integer ctipoimpuesto;
	private Integer cconvenio;
	private String scadenaoriginal;
	private Integer cestadoregistro;
	private Integer useridchange;
	private String sxml;
	private String sxmlsello;
	private String sserie;
	private String surl;
	private String suddi;
	private String ssellodigital;
	private String sobservacion;
	
	
	public String getSobservacion() {
		return sobservacion;
	}
	public void setSobservacion(String sobservacion) {
		this.sobservacion = sobservacion;
	}
	public String getSsellodigital() {
		return ssellodigital;
	}
	public void setSsellodigital(String ssellodigital) {
		this.ssellodigital = ssellodigital;
	}
	public String getSuddi() {
		return suddi;
	}
	public void setSuddi(String suddi) {
		this.suddi = suddi;
	}
	public String getSurl() {
		return surl;
	}
	public void setSurl(String surl) {
		this.surl = surl;
	}
	public String getSserie() {
		return sserie;
	}
	public void setSserie(String sserie) {
		this.sserie = sserie;
	}
	public String getSxmlsello() {
		return sxmlsello;
	}
	public void setSxmlsello(String sxmlsello) {
		this.sxmlsello = sxmlsello;
	}
	public String getSxml() {
		return sxml;
	}
	public void setSxml(String sxml) {
		this.sxml = sxml;
	}
	public Integer getUseridchange() {
		return useridchange;
	}
	public void setUseridchange(Integer useridchange) {
		this.useridchange = useridchange;
	}
	public Integer getCestadoregistro() {
		return cestadoregistro;
	}
	public void setCestadoregistro(Integer cestadoregistro) {
		this.cestadoregistro = cestadoregistro;
	}
	public String getScadenaoriginal() {
		return scadenaoriginal;
	}
	public void setScadenaoriginal(String scadenaoriginal) {
		this.scadenaoriginal = scadenaoriginal;
	}
	public Integer getCconvenio() {
		return cconvenio;
	}
	public void setCconvenio(Integer cconvenio) {
		this.cconvenio = cconvenio;
	}
	public BigDecimal getMdescuento() {
		return mdescuento;
	}
	public void setMdescuento(BigDecimal mdescuento) {
		this.mdescuento = mdescuento;
	}
	public BigDecimal getMcopago() {
		return mcopago;
	}
	public void setMcopago(BigDecimal mcopago) {
		this.mcopago = mcopago;
	}
	public BigDecimal getMiva() {
		return miva;
	}
	public void setMiva(BigDecimal miva) {
		this.miva = miva;
	}
	public BigDecimal getMtotal() {
		return mtotal;
	}
	public void setMtotal(BigDecimal mtotal) {
		this.mtotal = mtotal;
	}
	public Integer getCtipoimpuesto() {
		return ctipoimpuesto;
	}
	public void setCtipoimpuesto(Integer ctipoimpuesto) {
		this.ctipoimpuesto = ctipoimpuesto;
	}
	public BigDecimal getMsubtotal() {
		return msubtotal;
	}
	public void setMsubtotal(BigDecimal msubtotal) {
		this.msubtotal = msubtotal;
	}
	public Integer getCformapago() {
		return cformapago;
	}
	public void setCformapago(Integer cformapago) {
		this.cformapago = cformapago;
	}
	public Integer getCsucursal() {
		return csucursal;
	}
	public void setCsucursal(Integer csucursal) {
		this.csucursal = csucursal;
	}
	public Integer getCcliente() {
		return ccliente;
	}
	public void setCcliente(Integer ccliente) {
		this.ccliente = ccliente;
	}
	public String getSsucursal() {
		return ssucursal;
	}
	public void setSsucursal(String ssucursal) {
		this.ssucursal = ssucursal;
	}
	public Integer getKdatofiscal() {
		return kdatofiscal;
	}
	public void setKdatofiscal(Integer kdatofiscal) {
		this.kdatofiscal = kdatofiscal;
	}
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
