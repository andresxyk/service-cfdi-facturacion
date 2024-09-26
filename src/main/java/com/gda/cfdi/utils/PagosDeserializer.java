package com.gda.cfdi.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;

public class PagosDeserializer implements JsonDeserializer<CTipoFactor> {

	@Override
	public CTipoFactor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String enumValue = json.getAsString();
        try {
            return CTipoFactor.fromValue(enumValue);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Valor de enum no válido: " + enumValue, e);
        }
	}
  
}