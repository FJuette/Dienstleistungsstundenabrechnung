package de.juette.dlsa;

import java.util.Iterator;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabsURL extends TabSheet {
	private static final String tabNames[] = 
		{"Start", "Buchung", "Suche", "Verwaltung", "Einstellungen", "Hilfe"};
	
	private void createTabs(String tabNames[]) {
		for (String tabName : tabNames) {
			VerticalLayout layout = new VerticalLayout();
			layout.setCaption(tabName);
			if (tabName == "Hilfe") {
				layout.addComponent(new Hilfe());
			}
			else {
				layout.addComponent(new Label("<h1>" + tabName + "</h1>", ContentMode.HTML));
				layout.setHeight(400, Unit.PIXELS);
			}
			addComponent(layout);
		}
	}
	
	public TabsURL() {
		createTabs(tabNames);
		addSelectedTabChangeListener(e -> {
			String selectedTabName = e.getTabSheet().getSelectedTab().getCaption();
			UI.getCurrent().getPage().setUriFragment(convertNameToFragment(selectedTabName));

		});
	}
	
	public void selectTab() {
		String fragment = UI.getCurrent().getPage().getUriFragment();
		if (fragment == null) {
			setSelectedTab(0);
			return;
		}
		Iterator<Component> iterator = iterator();
		while (iterator.hasNext()) {
			Component tab = iterator.next();
			String name = convertNameToFragment(tab.getCaption().toLowerCase());
			if (fragment.toLowerCase().equals(name)) {
				setSelectedTab(tab);
				return;
			}
		}
		setSelectedTab(0);
	}
	
	private String convertNameToFragment(String name) {
		return name.replaceAll("\\s", "-");
	}
}
