package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Concepto {

	@JsonProperty("ClaveProdServ") 
    public String claveProdServ;
    public String noIdentificacion;
    @JsonProperty("Cantidad") 
    public String cantidad;
    @JsonProperty("ClaveUnidad") 
    public String claveUnidad;
    @JsonProperty("Unidad") 
    public String unidad;
    @JsonProperty("Descripcion") 
    public String descripcion;
    @JsonProperty("ValorUnitario") 
    public String valorUnitario;
    @JsonProperty("Importe") 
    public String importe;
    @JsonProperty("ObjetoImp") 
    public String objetoImp;
    public String descuento;
    @JsonProperty("Impuestos") 
    public ImpuestosConcepto impuestos;
    @JsonProperty("ACuentaTerceros") 
    public ACuentaTerceros aCuentaTerceros;
}
