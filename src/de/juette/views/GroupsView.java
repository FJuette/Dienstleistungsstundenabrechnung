package de.juette.views;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;

@SuppressWarnings("serial")
public class GroupsView extends EditableTable<Group> implements View {
	
	@SuppressWarnings("unchecked")
	public GroupsView() {
		beans = new BeanItemContainer<>(Group.class);
		beans.addAll((Collection<? extends Group>) HibernateUtil.getAllAsList(Group.class));
		
		btnNew.setCaption("Neue Gruppe");
		initLayout("Gruppenverwaltung");
		initTable();
		extendTable();
	}
	
	@Override
	protected void extendTable() {
		table.setVisibleColumns( new Object[] {"groupName", "liberated"} );
		table.setColumnHeaders("Gruppe", "DLS Befreit");
		table.setConverter("liberated", new BooleanToGermanConverter());
	}


	@Override
	protected void newBeanWindow() {
		Window window = new Window("Anlegen einer neuen Gruppe");
		window.setModal(true);
		window.setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		TextField txtNewGroup = new TextField("Name");
		txtNewGroup.setWidth("100%");
		layout.addComponent(txtNewGroup);
		
		CheckBox cbNewGroup = new CheckBox("DLS befreit");
		layout.addComponent(cbNewGroup);
		
		Button btnSaveNewGroup = new Button("Speichern");
		btnSaveNewGroup.setStyleName("friendly");
		layout.addComponent(btnSaveNewGroup);
		
		btnSaveNewGroup.addClickListener(event -> {
			beans.addItem(new Group(txtNewGroup.getValue(), cbNewGroup.getValue()));
			ComponentHelper.updateTable(table);
			HibernateUtil.saveAll(beans.getItemIds());
			window.close();
		});
		
		getUI().addWindow(window);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
