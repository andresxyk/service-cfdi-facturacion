package com.gda.cfdi.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class XMLGregorianCalendarDeserializer implements JsonDeserializer<XMLGregorianCalendar> {
  
	@Override
	public XMLGregorianCalendar deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(json.getAsString());
        } catch (DatatypeConfigurationException e) {
            throw new JsonParseException(e);
        }
	}
}