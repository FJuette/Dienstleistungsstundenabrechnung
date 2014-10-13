package de.juette.dlsa;

import java.util.Locale;

import model.Role;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class RoleToRolenameConverter implements Converter<String, Role> {

	@Override
	public Role convertToModel(String value, Class<? extends Role> targetType,
			Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		for (Role role : ComponentHelper.getDummyRoles().getItemIds()) {
			if (value.equals(role.getRollenname())) {
				return role;
			}
		}
		return null;
	}

	@Override
	public String convertToPresentation(Role value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.getRollenname();
	}

	@Override
	public Class<Role> getModelType() {
		// TODO Auto-generated method stub
		return Role.class;
	}

	@Override
	public Class<String> getPresentationType() {
		// TODO Auto-generated method stub
		return String.class;
	}

}
