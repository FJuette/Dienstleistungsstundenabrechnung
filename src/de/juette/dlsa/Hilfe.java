package de.juette.dlsa;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class Hilfe extends HorizontalLayout {
	public Hilfe() {
		setSizeFull();
		addComponent(new Label("Hilfe für die Dienstleistungsstundenabrechnung"));
	}
}
