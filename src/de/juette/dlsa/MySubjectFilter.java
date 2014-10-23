package de.juette.dlsa;

import java.util.Collection;

import org.hibernate.collection.internal.PersistentBag;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import de.juette.model.Category;

@SuppressWarnings("serial")
public class MySubjectFilter implements Container.Filter {

	protected String propertyId;
	protected Category subject;
	
	public MySubjectFilter(String propertyId, Category subject) {
		this.propertyId = propertyId;
		this.subject = subject;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		Property<PersistentBag> p = item.getItemProperty(propertyId);

		// Should always check validity
		if (p == null || !p.getType().equals(Collection.class))
			return false;
		else if (p.getValue().contains(subject)) {
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
