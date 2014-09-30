package Views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MainView extends VerticalLayout implements View {

	public MainView() {
		addComponent(new Label("Welcome to the MainView"));
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
