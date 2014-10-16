package de.juette.dlsa;

import java.util.ArrayList;
import java.util.Collection;

import model.Group;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class MyGroupFilter implements Container.Filter {

	protected String propertyId;
	protected Group group;
	
	public MyGroupFilter(String propertyId, Group group) {
		this.propertyId = propertyId;
		this.group = group;
	}
	
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		Property p = item.getItemProperty(propertyId);
        
        // Should always check validity
        if (p == null || !p.getType().equals(Collection.class))
            return false;
        ArrayList<Group> values = (ArrayList<Group>) p.getValue();
        
    	for (Group g : values) {
        	if (g.getGruppenname().equals(group.getGruppenname())) {
    			return true;
    		}
    	}
        
        return false;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return propertyId != null &&
				propertyId.equals(this.propertyId);
	}

}
