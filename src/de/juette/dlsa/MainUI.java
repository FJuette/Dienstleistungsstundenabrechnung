package de.juette.dlsa;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import de.juette.model.HibernateUtil;
import de.juette.views.CampaignView;
import de.juette.views.BookingView;
import de.juette.views.ErrorView;
import de.juette.views.GroupsView;
import de.juette.views.LogView;
import de.juette.views.MemberViewOld;
import de.juette.views.MemberView;
import de.juette.views.SettingsView;
import de.juette.views.CategoryView;
import de.juette.views.UserView;

@SuppressWarnings("serial")
@Theme("dlsaTheme")
@Title("Dienstleistungsstundenabrechung")
public class MainUI extends UI implements ViewChangeListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

		@Override
		protected void servletInitialized() throws ServletException {
			super.servletInitialized();
			getService().addSessionInitListener(this);
			getService().addSessionDestroyListener(this);
		}
		
		@Override
		public void sessionDestroy(SessionDestroyEvent event) {
			HibernateUtil.closeSession();
		}

		@Override
		public void sessionInit(SessionInitEvent event) throws ServiceException {
			HibernateUtil.getSessionFactory();
			
			// Creates Example data, for fresh Database and to show and test the functionality
			if (true) {
				DataHandler.createDummySubjects();
				DataHandler.createDummyGroups();
				DataHandler.createDummyMember();
				DataHandler.createDummyActivities();
				DataHandler.createDummyBookings();
				DataHandler.createDummyRoles();
				DataHandler.createDummyUsers();
				DataHandler.createDummyLogs();
				DataHandler.createDummyCycles();
				DataHandler.createDummySettings();
				// Must be created once
				DataHandler.createMappingEntrys();
			}
		}
	}

	private Navigator navigator;
	private LinkedHashMap<String, Class<? extends View>> routes = new LinkedHashMap<String, Class<? extends View>>() {
		{
			put("booking", BookingView.class);
			put("groups", GroupsView.class);
			put("category", CategoryView.class);
			put("campain", CampaignView.class);
			put("user", UserView.class);
			put("member", MemberView.class);
			put("settings", SettingsView.class);
			put("log", LogView.class);
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
			navigator.navigateTo("booking");
			LoginWindow();
		}

		navigator.setErrorView(ErrorView.class);
	}

	CssLayout buildMenu() {
		menuItems = new LinkedHashMap<String, String>() {
			{
				put("booking", "Journal");
				put("user", "Benutzer");
				put("member", "Mitglieder");
				put("campain", "Aktionen");
				put("groups", "Gruppen");
				put("category", "Sparten");
				put("settings", "Einstellungen");
				put("log", "Historie");
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
		 * final MenuItem settingsItem = settings.addItem("Fabian Juette", new
		 * ThemeResource("../valomenutest/img/profile-pic-300px.jpg"), null);
		 * 
		 * settingsItem.addItem("Edit Profile", null);
		 * settingsItem.addItem("Preferences", null);
		 * settingsItem.addSeparator(); settingsItem.addItem("Sign Out", null);
		 * menu.addComponent(settings);
		 */

		menuItemsLayout.setPrimaryStyleName("valo-menuitems");
		menu.addComponent(menuItemsLayout);

		Label label = null;
		for (final Entry<String, String> item : menuItems.entrySet()) {
			if (item.getKey().equals("user")) {
				label = new Label("Verwaltung", ContentMode.HTML);
				label.setPrimaryStyleName("valo-menu-subtitle");
				label.addStyleName("h4");
				label.setSizeUndefined();
				menuItemsLayout.addComponent(label);
			}
			if (item.getKey().equals("settings")) {
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
			LoginWindow();
			Notification.show("Erfolgreich ausgeloggt.",
					Notification.Type.TRAY_NOTIFICATION);
		});
		b.setHtmlContentAllowed(true);
		b.setPrimaryStyleName("valo-menu-item");
		menuItemsLayout.addComponent(b);

		return menu;
	}

	private void LoginWindow() {
		Window window = new Window("");
		window.setModal(true);
		window.setWidth("600");
		window.setHeight("250");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");
		
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");
		Label title = new Label("DLS-Verwaltung Login");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		loginPanel.addComponent(labels);
		
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");
		final TextField username = new TextField("Benutzername");
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		final PasswordField password = new PasswordField("Passwort");
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		final Button signin = new Button("Einloggen");
		signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signin.setClickShortcut(KeyCode.ENTER);
		signin.focus();
		fields.addComponents(username, password, signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);
		signin.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				window.close();
				UI.getCurrent().getNavigator().navigateTo("booking");
			}
		});
		
		loginPanel.addComponent(fields);
		loginPanel.addComponent(new CheckBox("Angemeldet bleiben", true));

		layout.addComponent(loginPanel);
		getUI().addWindow(window);
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
	}
}