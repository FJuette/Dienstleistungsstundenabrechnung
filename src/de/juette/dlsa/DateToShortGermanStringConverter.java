package de.juette.dlsa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Converts the type "Date" to the German format "dd.MM.yyyy" for the Vaadin tables
 * @author Fabian Juette
 */
public class DateToShortGermanStringConverter implements
		Converter<String, Date> {

	private static final long serialVersionUID = -7898028118732663851L;

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
