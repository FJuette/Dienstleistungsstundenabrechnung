package de.juette.dlsa;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class MyBooleanFilter implements Container.Filter {

	private static final long serialVersionUID = -1166444080648453589L;
	protected String propertyId;
	
	public MyBooleanFilter(String propertyId) {
		this.propertyId = propertyId;
	}
	
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		@SuppressWarnings("unchecked")
		Property<Boolean> p = item.getItemProperty(propertyId);

		// Should always check validity
		if (p == null || !p.getType().equals(Boolean.class))
			return false;
		else if (p.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return propertyId != null && propertyId.equals(this.propertyId);
	}

}
