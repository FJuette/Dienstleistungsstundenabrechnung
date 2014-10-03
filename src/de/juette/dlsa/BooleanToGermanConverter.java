package de.juette.dlsa;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class BooleanToGermanConverter implements Converter<String, Boolean> {

	@Override
	public Boolean convertToModel(String value,
			Class<? extends Boolean> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value.equals("Ja"))
			return true;
		else
			return false;
	}

	@Override
	public String convertToPresentation(Boolean value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value)
			return "Ja";
		else
			return "Nein";
	}

	@Override
	public Class<Boolean> getModelType() {
		// TODO Auto-generated method stub
		return Boolean.class;
	}

	@Override
	public Class<String> getPresentationType() {
		// TODO Auto-generated method stub
		return String.class;
	}

}
