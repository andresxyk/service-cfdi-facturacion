package com.gda.cfdi.dto.empresa;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InformacionGlobal {

	@JsonProperty("Periodicidad") 
    public String periodicidad;
    @JsonProperty("Meses") 
    public String meses;
    @JsonProperty("Año") 
    public String ano;
}
