package de.juette.dlsa.layout;

import com.vaadin.ui.*;

public class ValoMenuLayout extends HorizontalLayout {

	private static final long serialVersionUID = 1983074862397171634L;
	CssLayout contentArea = new CssLayout();
	CssLayout menuArea = new CssLayout();

	public ValoMenuLayout() {
		setSizeFull();
		menuArea.setPrimaryStyleName("valo-menu");
		contentArea.setPrimaryStyleName("valo-content");
		contentArea.addStyleName("v-scrollable");
		contentArea.setSizeFull();
		addComponents(menuArea, contentArea);
		setExpandRatio(contentArea, 1);
	}

	public ComponentContainer getContentContainer() {
		return contentArea;
	}

	public void addMenu(Component menu) {
		menu.addStyleName("valo-menu-part");
		menuArea.addComponent(menu);
	}
}