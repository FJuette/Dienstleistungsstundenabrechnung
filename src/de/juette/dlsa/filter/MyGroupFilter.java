package de.juette.dlsa.filter;

import java.util.Collection;

import org.hibernate.collection.internal.PersistentBag;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import de.juette.model.Group;

public class MyGroupFilter implements Container.Filter {

	private static final long serialVersionUID = 2997422335009722124L;
	protected String propertyId;
	protected Group group;

	public MyGroupFilter(String propertyId, Group group) {
		this.propertyId = propertyId;
		this.group = group;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		Property<PersistentBag> p = item.getItemProperty(propertyId);

		// Should always check validity
		if (p == null || !p.getType().equals(Collection.class))
			return false;
		else if (p.getValue().contains(group)) {
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
