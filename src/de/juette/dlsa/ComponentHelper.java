package de.juette.dlsa;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class ComponentHelper {
	
	// Set the Caption of the TextField on the Top
	public static HorizontalLayout getCaptionOnTop(Component c) {
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(c);
		return txtLayout;
	}
}
