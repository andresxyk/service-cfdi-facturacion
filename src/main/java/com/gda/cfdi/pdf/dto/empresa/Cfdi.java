package com.gda.cfdi.pdf.dto.empresa;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Cfdi {
	@JsonProperty("Version") 
    public String version;
    @JsonProperty("Serie") 
    public String serie;
    @JsonProperty("Folio") 
    public String folio;
    @JsonProperty("Fecha") 
    public Date fecha;
    @JsonProperty("Sello") 
    public String sello;
    @JsonProperty("FormaPago") 
    public String formaPago;
    @JsonProperty("NoCertificado") 
    public String noCertificado;
    @JsonProperty("Certificado") 
    public String certificado;
    @JsonProperty("SubTotal") 
    public String subTotal;
    @JsonProperty("Moneda") 
    public String moneda;
    @JsonProperty("TipoCambio") 
    public String tipoCambio;
    @JsonProperty("Total") 
    public String total;
    @JsonProperty("TipoDeComprobante") 
    public String tipoDeComprobante;
    @JsonProperty("Exportacion") 
    public String exportacion;
    @JsonProperty("MetodoPago") 
    public String metodoPago;
    @JsonProperty("LugarExpedicion") 
    public String lugarExpedicion;
    public String schemaLocation;
    @JsonProperty("CfdiRelacionados") 
    public ArrayList<CfdiRelacionado> cfdiRelacionados;
    @JsonProperty("InformacionGlobal") 
    public InformacionGlobal informacionGlobal;
    @JsonProperty("Emisor") 
    public Emisor emisor;
    @JsonProperty("Receptor") 
    public Receptor receptor;
    @JsonProperty("Conceptos") 
    public Conceptos conceptos;
    @JsonProperty("Impuestos") 
    public Impuestos impuestos;
    @JsonProperty("Complemento") 
    public Complemento complemento;
}
