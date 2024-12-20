package com.gda.cfdi.pdf.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TimbradoCfdiDto {

	public Header header;
    public DatosComplementarios datosComplementarios;
    public Cfdi cfdi;
    public DocumentoTimbrado documentoTimbrado;
    @JsonProperty("GDA_menssage") 
    public GDAMenssage gDA_menssage;
}
