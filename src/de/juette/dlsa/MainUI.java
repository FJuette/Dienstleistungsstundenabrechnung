package de.juette.dlsa;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import Views.*;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

@SuppressWarnings("serial")
@Theme("dlsaTheme")
@Title("Dienstleistungsstundenabrechung")
public class MainUI extends UI {
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	private HorizontalLayout layout = new HorizontalLayout();
	private Navigator navigator;
	private CssLayout content = new CssLayout(); // Placeholder for the views of the Navigator
	private String[] viewNames = new String[] { "main", "help" }; // All Views should be writte in low case
	private HashMap<String, Class<? extends View>> routes = new HashMap<String, Class<? extends View>>() {
		{
			put("", MainView.class);
			put("help", HelpView.class);
		}
	};
		
	@Override
	protected void init(VaadinRequest request) {
		setLocale(Locale.GERMANY);
		
		buildMainView();
		setContent(layout);
	}
	
	private void buildMainView() {
		navigator = new Navigator(this, content);
		// Add all routes dynamically to the navigator
		for (String route : routes.keySet()) {
			navigator.addView(route, routes.get(route));
		}
		
		layout.addComponent(buildSidebar());
		
		layout.addComponent(content);
		content.setSizeFull();
		layout.setExpandRatio(content, 1);
	}
	
	private VerticalLayout buildSidebar() {
		VerticalLayout sidebar = new VerticalLayout();
		sidebar.setWidth(150, Unit.PIXELS);
		
		// Adding the Buttons for the navigation
		for (final String view : viewNames) {
			Button b = new Button(view);
			b.addClickListener(event -> {
				if (view.equals("main")) {
					navigator.navigateTo("");
				} else {
					navigator.navigateTo(view);
				}
			});
			sidebar.addComponent(b);
		}
		return sidebar;
	}
}