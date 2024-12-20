package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DoctoRelacionado {

	@JsonProperty("IdDocumento") 
    public String idDocumento;
    @JsonProperty("Serie") 
    public String serie;
    @JsonProperty("Folio") 
    public String folio;
    @JsonProperty("MonedaDR") 
    public String monedaDR;
    @JsonProperty("EquivalenciaDR") 
    public String equivalenciaDR;
    @JsonProperty("NumParcialidad") 
    public String numParcialidad;
    @JsonProperty("ImpSaldoAnt") 
    public String impSaldoAnt;
    @JsonProperty("ImpPagado") 
    public String impPagado;
    @JsonProperty("ImpSaldoInsoluto") 
    public String impSaldoInsoluto;
    @JsonProperty("ObjetoImpDR") 
    public String objetoImpDR;
    @JsonProperty("ImpuestosDR") 
    public ImpuestosDR impuestosDR;
}
