package de.juette.dlsa;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class MyYearFilter implements Container.Filter {

	private static final long serialVersionUID = -505914895668804706L;
	protected String propertyId;
	protected int year;
	
	public MyYearFilter(String propertyId, int year) {
		this.propertyId = propertyId;
		this.year = year;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		@SuppressWarnings("unchecked")
		Property<Date> p = item.getItemProperty(propertyId);
		Calendar c = Calendar.getInstance();
		
		// Should always check validity
		if (p == null || !p.getType().equals(Date.class))
			return false;
		else {
			c.setTime(p.getValue());
			if (c.get(Calendar.YEAR) == year) {
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
