package de.juette.views.windows;

import java.util.ArrayList;
import java.util.Iterator;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

import de.juette.model.ColumnMapping;
import de.juette.model.CsvColumn;

public class NewColumnMappingWindow extends Window {

	private static final long serialVersionUID = 2441687195090369802L;

	public NewColumnMappingWindow() {
		// TODO Auto-generated constructor stub
	}
	
	private void setMatchingLayout(ArrayList<ColumnMapping> mapping) {
		/*
		Iterator<Component> iterAll = layoutMapping.iterator();

		while (iterAll.hasNext()) {
			Component c = iterAll.next();
			
			if ("com.vaadin.ui.HorizontalLayout".equals(c.getClass()
					.getTypeName())) {
				Iterator<Component> iterHor = ((HorizontalLayout) c)
						.iterator();

				while (iterHor.hasNext()) {
					Component p = iterHor.next();

					if ("com.vaadin.ui.Label"
							.equals(p.getClass().getTypeName())
							&& !"Datenbankfeld".equals(((Label) p).getValue())
							&& !"Spalte aus der Migliederliste"
									.equals(((Label) p).getValue())) {
						// put the value of the current column in a variable
						previous = ((Label) p).getValue();
					}

					if ("com.vaadin.ui.ComboBox".equals(p.getClass()
							.getTypeName())) {
						ComboBox cb = (ComboBox) p;
						
						if (cb.isRequired()
								&& (cb.getValue() == null || cb.getValue()
										.equals(""))) {
							Notification
									.show("Bitte alle Felder mit * mit einer Auswahl füllen.",
											Type.ERROR_MESSAGE);
							return;
						}
						
						if (((ComboBox) p).getValue() != null) {
							for (ColumnMapping m : mapping) {
								if (m.getDisplayname().equals(previous)) {
									m.setCsvColumnIndex(((CsvColumn) ((ComboBox) p)
											.getValue()).getIndex());
									m.setCsvColumnName(((CsvColumn) ((ComboBox) p)
											.getValue()).getValue());
								}
							}
						} // End If comboBox value
					} // End If comboBox class
				} // End Iterator label + comboBox
			} // End If HorizontalLayout
		} // End Iterator layoutMapping
		*/
	} // End of function
}
