package de.juette.views.windows;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;

public class MemberAPWindow extends Window {

	private static final long serialVersionUID = 3438130859223366418L;
	private Boolean choice;

	public MemberAPWindow() {
		setModal(true);
		setWidth("500");
		setCaption("Aktiv/Passiv ändern");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		OptionGroup single = new OptionGroup("Typ auswählen");
		single.addItems("Aktiv", "Passiv");
		
		layout.addComponent(single);
		
		Button btnSave = new Button("Speichern", FontAwesome.SAVE);
		btnSave.addClickListener(event -> {
			if (single.getValue().toString().equals("Aktiv")) {
				choice = true;
			} else
				choice = false;
			close();
		});
		layout.addComponent(btnSave);
	}

	public Boolean getChoice() {
		return choice;
	}
	
}
