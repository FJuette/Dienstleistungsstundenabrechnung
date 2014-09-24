package de.juette.dlsa;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("dlsaTheme")
@Title("Dienstleistungsstundenabrechung")
public class MainUI extends UI {
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
	public static class Servlet extends VaadinServlet {
	}
	
	VerticalLayout layout = new VerticalLayout();
		
	@Override
	protected void init(VaadinRequest request) {
		setLocale(Locale.GERMANY);
		final TabsURL tabsURL = new TabsURL();
		setContent(layout);
		layout.addComponent(tabsURL);
		tabsURL.selectTab();
		
		getPage().addUriFragmentChangedListener(e -> {
			tabsURL.selectTab();
			
		});
	}
}