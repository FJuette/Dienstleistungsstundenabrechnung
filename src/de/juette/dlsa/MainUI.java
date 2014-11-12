package de.juette.dlsa;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

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
import com.vaadin.ui.Notification.Type;
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
import de.juette.views.LoginView;
import de.juette.views.MemberViewOld;
import de.juette.views.MemberView;
import de.juette.views.SettingsView;
import de.juette.views.CategoryView;
import de.juette.views.UserView;

@Theme("dlsaTheme")
@Title("Dienstleistungsstundenabrechung")
public class MainUI extends UI implements ViewChangeListener {

	private static final long serialVersionUID = 7386404806620809880L;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

		private static final long serialVersionUID = 6737000763577891447L;

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
		private static final long serialVersionUID = 703638756416772379L;

		{
			put("booking", BookingView.class);
			put("groups", GroupsView.class);
			put("category", CategoryView.class);
			put("campain", CampaignView.class);
			put("user", UserView.class);
			put("member", MemberView.class);
			put("settings", SettingsView.class);
			put("log", LogView.class);
			put("", LoginView.class);
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
		navigator.addViewChangeListener(this);
		for (String route : routes.keySet()) {
			navigator.addView(route, routes.get(route));
		}

		final String f = Page.getCurrent().getUriFragment();
		if (f == null || f.equals("")) {
			navigator.navigateTo("");
		}

		navigator.setErrorView(ErrorView.class);
	}

	CssLayout buildMenu() {
		menuItems = new LinkedHashMap<String, String>() {
			private static final long serialVersionUID = 8039574987517700848L;

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
			// TODO problem lösen
			Page.getCurrent().reload();
		});
		b.setHtmlContentAllowed(true);
		b.setPrimaryStyleName("valo-menu-item");
		menuItemsLayout.addComponent(b);

		return menu;
	}

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		boolean isAuthenticated = SecurityUtils.getSubject().isAuthenticated();
		// User is logged in but tries to access the LoginView, navigating him to the journal
		if (isAuthenticated && "".equals(event.getViewName())) {
			getNavigator().navigateTo("booking");
			return false;
		}
		// User is not authenticated and tries to access something else than the login view
		if (!isAuthenticated && !"".equals(event.getViewName())) {
			getNavigator().navigateTo("");
			return false;
		}
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
		if (!SecurityUtils.getSubject().isAuthenticated() && getUI().getWindows().size() == 0) {
			getUI().addWindow(LoginView.getLoginWindow());
		}
	}
}