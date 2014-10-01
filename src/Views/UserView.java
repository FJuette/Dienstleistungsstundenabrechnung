package Views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UserView extends VerticalLayout implements View {

	@Override
	public void enter(ViewChangeEvent event) {
		addComponent(new Label("Benutzerverwaltung"));
	}

}
