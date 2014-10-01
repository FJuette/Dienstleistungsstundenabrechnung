package de.juette.dlsa;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
	
	private VerticalLayout layout = new VerticalLayout();
	private HorizontalSplitPanel center = new HorizontalSplitPanel();
	private Navigator navigator;
	private CssLayout content = new CssLayout(); // Placeholder for the views of the Navigator
	
	// All Views should be writte in low case
	Map<String, String> viewNames = new LinkedHashMap<String, String>() {
		{
			put("main", "Startseite");
			put("booking", "Journal");
			put("groups", "Gruppenverwaltung");
			put("subject", "Spartenverwaltung");
			put("user", "Benutzerverwaltung");
			put("member", "Mitgliederverwaltung");
			put("settings", "Einstellungen");
			put("log", "Historie");
			put("search", "Suche");
			put("help", "Hilfe");
		}
	};
	private HashMap<String, Class<? extends View>> routes = new HashMap<String, Class<? extends View>>() {
		{
			put("", MainView.class);
			put("booking", BookingView.class);
			put("groups", GroupsView.class);
			put("subject", SubjectView.class);
			put("user", UserView.class);
			put("member", MemberView.class);
			put("settings", SettingsView.class);
			put("log", LogView.class);
			put("search", SearchView.class);
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
		Label lblHeader = new Label("Dienstleistungsstundenabrechungsverwaltung");
		lblHeader.setStyleName("h2");
		
		layout.addComponents(lblHeader, center);
		
		
		navigator = new Navigator(this, content);
		// Add all routes dynamically to the navigator
		for (String route : routes.keySet()) {
			navigator.addView(route, routes.get(route));
		}
		
		center.setFirstComponent(buildSidebar());
		center.setSecondComponent(content);
		center.setSplitPosition(11, Unit.PERCENTAGE);
		center.setLocked(true);
		
		content.setSizeFull();
		//layout.setExpandRatio(content, 1);
	}
	
	private VerticalLayout buildSidebar() {
		VerticalLayout sidebar = new VerticalLayout();
		//sidebar.setWidth(200, Unit.PIXELS);
		
		// Adding the Buttons for the navigation
		for (final String view : viewNames.keySet()) {
			Button b = new NativeButton(viewNames.get(view));
			b.setWidth("90%");
			b.addClickListener(event -> {
				if (view.equals("main")) {
					navigator.navigateTo("");
				} else {
					navigator.navigateTo(view);
				}
			});
			sidebar.addComponent(b);
		}
		Button btnLogout = new NativeButton("Logout");
		btnLogout.setWidth("90%");
		btnLogout.addClickListener(event -> {
			//TODO: Logout code here when Shiro is active
			Notification.show("Ausloggen erfolgreich.", Notification.Type.TRAY_NOTIFICATION);
		});
		sidebar.addComponent(btnLogout);
		return sidebar;
	}
}