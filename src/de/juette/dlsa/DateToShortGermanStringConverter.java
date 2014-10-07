package de.juette.dlsa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class DateToShortGermanStringConverter implements
		Converter<String, Date> {

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType,
			Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return new SimpleDateFormat("dd.MM.yyyy").parse(value);
		} catch (ParseException e) {
			return new Date();
		}
	}

	@Override
	public String convertToPresentation(Date value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return new SimpleDateFormat("dd.MM.yyyy").format(value);
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
