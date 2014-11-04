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

	public static void setTableSize(Table tbl) {
		if (tbl.size() > 15) {
			tbl.setPageLength(15);
		} else {
			tbl.setPageLength(tbl.size() + 1);
		}
		tbl.markAsDirtyRecursive();
	}
}
