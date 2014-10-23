package de.juette.dlsa;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class ComponentHelper {

	// Set the Caption of the TextField on the Top
	public static HorizontalLayout getCaptionOnTop(Component c) {
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(c);
		return txtLayout;
	}

	public static void updateTable(Table table) {
		if (table.size() > 15) {
			table.setPageLength(15);
		} else {
			table.setPageLength(table.size() + 1);
		}
		table.markAsDirtyRecursive();
	}
}
