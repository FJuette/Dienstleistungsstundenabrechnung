package de.juette.dlsa;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;


import javax.servlet.annotation.WebServlet;


import Views.BookingView;
import Views.GroupsView;
import Views.HelpView;
import Views.LogView;
import Views.MainView;
import Views.MemberView;
import Views.SearchView;
import Views.SettingsView;
import Views.SubjectView;
import Views.UserView;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("dlsaTheme")
@Title("Dienstleistungsstundenabrechung")
public class MainUI extends UI implements ViewChangeListener {
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	private Navigator navigator;
	private LinkedHashMap<String, Class<? extends View>> routes = new LinkedHashMap<String, Class<? extends View>>() {
		{
			put("main", MainView.class);
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
	
	ValoMenuLayout root = new ValoMenuLayout();
	ComponentContainer viewDisplay = root.getContentContainer();
	CssLayout menu = new CssLayout();
	CssLayout menuItemsLayout = new CssLayout();
	{
		menu.setId("MainMenu");
	}
	private LinkedHashMap<String, String> menuItems;
		
	@Override
	protected void init(VaadinRequest request) {
		setLocale(Locale.GERMANY);
		
		setContent(root);
		root.setWidth("100%");
		root.addMenu(buildMenu());
		navigator = new Navigator(this, viewDisplay);
		for (String route : routes.keySet()) {
			navigator.addView(route, routes.get(route));
		}

		final String f = Page.getCurrent().getUriFragment();
		if (f == null || f.equals("")) {
			navigator.navigateTo("main");
		}

		navigator.setErrorView(MainView.class);
	}
	
	CssLayout buildMenu() {
		menuItems = new LinkedHashMap<String, String>() {
			{
				put("main", "Startseite");
				put("booking", "Journal");
				put("user", "Benutzer");
				put("member", "Mitglieder");
				put("groups", "Gruppen");
				put("subject", "Sparten");
				put("settings", "Einstellungen");
				put("search", "Suche");
				put("log", "Historie");
				put("help", "Hilfe");
			}
		};

		final HorizontalLayout top = new HorizontalLayout();
		top.setWidth("100%");
		top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		top.addStyleName("valo-menu-title");
		menu.addComponent(top);

		final Button showMenu = new Button("Menu");
		showMenu.addClickListener(event -> {
			if (menu.getStyleName().contains("valo-menu-visible")) {
				menu.removeStyleName("valo-menu-visible");
			} else {
				menu.addStyleName("valo-menu-visible");
			}
		});

		showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		showMenu.addStyleName("valo-menu-toggle");
		showMenu.setIcon(FontAwesome.LIST);
		menu.addComponent(showMenu);
		
		final Label title = new Label(
				"<h3><strong>DLS-Verwaltung</strong></h3>", ContentMode.HTML);
		title.setSizeUndefined();
		top.addComponent(title);
		top.setExpandRatio(title, 1);
		
		final MenuBar settings = new MenuBar();
		settings.addStyleName("user-menu");
		
		/*
		final MenuItem settingsItem = settings.addItem("Fabian Juette",
				new ThemeResource("../valomenutest/img/profile-pic-300px.jpg"),
				null);
		
		settingsItem.addItem("Edit Profile", null);
		settingsItem.addItem("Preferences", null);
		settingsItem.addSeparator();
		settingsItem.addItem("Sign Out", null);
		menu.addComponent(settings);
		*/
		
		menuItemsLayout.setPrimaryStyleName("valo-menuitems");
		menu.addComponent(menuItemsLayout);
		
		Label label = null;
		for (final Entry<String, String> item : menuItems.entrySet()) {
			if (item.getKey().equals("user")) {
				label = new Label("Einstellungen", ContentMode.HTML);
				label.setPrimaryStyleName("valo-menu-subtitle");
				label.addStyleName("h4");
				label.setSizeUndefined();
				menuItemsLayout.addComponent(label);
			}
			if (item.getKey().equals("search")) {
				label = new Label("Sonstiges", ContentMode.HTML);
				label.setPrimaryStyleName("valo-menu-subtitle");
				label.addStyleName("h4");
				label.setSizeUndefined();
				menuItemsLayout.addComponent(label);
			}
			
			final Button b = new Button(item.getValue());
			b.addClickListener(event -> {
				navigator.navigateTo(item.getKey());
			});
			b.setHtmlContentAllowed(true);
			b.setPrimaryStyleName("valo-menu-item");
			menuItemsLayout.addComponent(b);
		}
		
		// Logout Button
		final Button b = new Button("Abmelden");
		b.addClickListener(event -> {
			Notification.show("Erfolgreich ausgeloggt.", Notification.Type.TRAY_NOTIFICATION);
		});
		b.setHtmlContentAllowed(true);
		b.setPrimaryStyleName("valo-menu-item");
		menuItemsLayout.addComponent(b);
		
		return menu;
	}
	
	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
	}
}