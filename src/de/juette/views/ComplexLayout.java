package de.juette.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ComplexLayout extends VerticalLayout {

	protected final Table table = new Table();
	protected VerticalLayout headerLayout = new VerticalLayout();
	protected HorizontalSplitPanel contentSplitPanel = new HorizontalSplitPanel();
	protected VerticalLayout contentLayout = new VerticalLayout();
	protected HorizontalLayout contentHeaderLayout = new HorizontalLayout();
	protected HorizontalLayout innerHeadLayout = new HorizontalLayout();
	protected TabSheet contentTabs = new TabSheet();
	protected Label lblContentHeader = new Label("<strong>Mitglied: </strong>",
			ContentMode.HTML);
	protected Button btnNew = new Button("Neu");
	
	protected void initLayout(String title) {
		setSpacing(true);

		Label lblTitle = new Label("<strong>Mitgliederverwaltung</strong>",
				ContentMode.HTML);
		lblTitle.addStyleName("h3 myHeaderLabel");
		
		btnNew.setStyleName("primary tiny myAddButton");
		btnNew.setIcon(FontAwesome.PLUS);
		
		innerHeadLayout.setWidth(100, Unit.PERCENTAGE);
		
		headerLayout.setSpacing(true);
		headerLayout.addComponent(lblTitle);
		headerLayout.addComponent(innerHeadLayout);
		addComponent(headerLayout);

		contentSplitPanel.setSplitPosition(22, Unit.PERCENTAGE);
		contentSplitPanel.setSizeFull();
		contentSplitPanel.setLocked(true);
		contentSplitPanel.setFirstComponent(table);
		contentSplitPanel.setSecondComponent(contentLayout);
		addComponent(contentSplitPanel);

		contentHeaderLayout.setSpacing(true);
		lblContentHeader.setStyleName("myHeaderLabel");
		contentHeaderLayout.addComponent(lblContentHeader);
		contentLayout.addComponent(contentHeaderLayout);
		contentTabs.setSizeFull();
		contentLayout.addComponent(contentTabs);
	}
	
	protected void formatTable() {
		table.setSizeFull();
		table.setSelectable(true);
		table.setImmediate(true);
		table.setPageLength(18);
		table.addStyleName("no-stripes");
		
	}
}
