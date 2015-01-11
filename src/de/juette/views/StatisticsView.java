package de.juette.views;

import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.GeneralHandler;

public class StatisticsView extends VerticalLayout implements View, IStatisticsView {

	private static final long serialVersionUID = -6517121952079289467L;
	
	Label lblCategory = new Label("<strong>Aktuell gebuchte DLS pro Sparte:</strong>", ContentMode.HTML);
	Label lblGroup = new Label("<strong>Aktuell gebuchte DLS pro Gruppe:</strong>", ContentMode.HTML);
	Label lblSection = new Label("<strong>Aktuell gebuchte DLS pro Bereich:</strong>", ContentMode.HTML);
	
	public StatisticsView() {
		Label title = new Label("<h3><strong>Statistik</strong></h3>", ContentMode.HTML);
		addComponent(title);
		addStyleName("myHeaderLabel");
		
		lblCategory.addStyleName("myFormLayout");
		lblGroup.addStyleName("myFormLayout");
		lblSection.addStyleName("myFormLayout");
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
	}

	@Override
	public void setCategoryData(List<String> value) {
		addComponent(lblCategory);
		for (String item : value) {
			Label lblEntry = new Label(item, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
	}

	@Override
	public void setGroupData(List<String> value) {
		addComponent(lblGroup);
		for (String item : value) {
			Label lblEntry = new Label(item, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
	}

	@Override
	public void setSectionData(List<String> value) {
		addComponent(lblSection);
		for (String item : value) {
			Label lblEntry = new Label(item, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
	}
}
