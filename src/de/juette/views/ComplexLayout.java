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

public class ComplexLayout extends VerticalLayout {

	private static final long serialVersionUID = 9009877220691387858L;
	protected final Table table = new Table();
	protected VerticalLayout headerLayout = new VerticalLayout();
	protected HorizontalSplitPanel contentSplitPanel = new HorizontalSplitPanel();
	protected VerticalLayout contentLayout = new VerticalLayout();
	protected HorizontalLayout contentHeaderLayout = new HorizontalLayout();
	protected HorizontalLayout innerHeadLayout = new HorizontalLayout();
	protected VerticalLayout leftContentLayout = new VerticalLayout();
	protected TabSheet contentTabs = new TabSheet();
	protected Label lblContentHeader = new Label("",
			ContentMode.HTML);
	protected Button btnNew = new Button("Neu");

	protected void initLayout(String title) {
		setSpacing(true);

		Label lblTitle = new Label(title, ContentMode.HTML);
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
		leftContentLayout.addComponent(table);
		contentSplitPanel.setFirstComponent(leftContentLayout);
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
