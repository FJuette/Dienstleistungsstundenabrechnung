package de.juette.dlsa;

import java.util.Date;

import org.joda.time.DateTime;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class MyLeavingMemberDateFilter implements Container.Filter {

	private static final long serialVersionUID = -6426216791811615941L;
	protected String propertyId;
	protected Date date;
	
	public MyLeavingMemberDateFilter(String propertyId, Date date) {
		this.propertyId = propertyId;
		this.date = date;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		@SuppressWarnings("unchecked")
		Property<Date> p = item.getItemProperty(propertyId);
		// Should always check validity
		if (p == null || !p.getType().equals(Date.class))
			return true;
		else {
			if (new DateTime(p.getValue()).isAfter(new DateTime(date))) {
				return true;
			} else 
				return false;
		}
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return propertyId != null && propertyId.equals(this.propertyId);
	}

}
