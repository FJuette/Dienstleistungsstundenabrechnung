package de.juette.views.tabs;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

import de.juette.dlsa.GeneralHandler;
import de.juette.dlsa.layout.DataTabLayout;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Sector;

public class SectorGroupTab extends DataTabLayout<Sector> {

	private static final long serialVersionUID = 4064120897573084508L;
	private BeanItemContainer<Group> sectorGroups = new BeanItemContainer<Group>(Group.class);
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	
	public SectorGroupTab(BeanItem<Sector> beanItem) {
		this.beanItem = beanItem;
		groups.addAll(HibernateUtil.getAllAsList(Group.class));
		setSizeFull();
		setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<Sector>(Sector.class);
		fieldGroup.setItemDataSource(beanItem);

		sectorGroups.addAll(beanItem.getBean().getGroups());
		
		ComboBox cbAll = new ComboBox("Alle Gruppen:");
		cbAll.setContainerDataSource(groups);
		cbAll.setItemCaptionPropertyId("groupName");
		cbAll.setImmediate(true);
		cbAll.setWidth("350");
		addComponent(cbAll);
		
		Button btnAdd = new Button(FontAwesome.PLUS);
		addComponent(btnAdd);
		
		Table tblEntrys = new Table("Zugeordnete Gruppen:");
		tblEntrys.setContainerDataSource(sectorGroups);
		tblEntrys.setVisibleColumns( new Object[] {"groupName"} );
		tblEntrys.setWidth("350");
		tblEntrys.setSelectable(true);
		tblEntrys.addStyleName("no-stripes");
		tblEntrys.setColumnHeaders("Gruppenname");
		GeneralHandler.setTableSize(tblEntrys);
		addComponent(tblEntrys);
		
		btnAdd.addClickListener(event -> {
			if (cbAll.getValue() != null
					&& !sectorGroups.containsId(cbAll.getValue())) {
				sectorGroups.addItem(cbAll.getValue());
			}
			GeneralHandler.setTableSize(tblEntrys);
		});
		
		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblEntrys.getValue() != null) {
				tblEntrys.removeItem(tblEntrys.getValue());
			}
			GeneralHandler.setTableSize(tblEntrys);
		});
		addComponent(btnRemove);
		

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		addComponent(btnSave);

		btnSave.addClickListener(event -> {
			try {
				(beanItem.getBean()).setGroups(sectorGroups
						.getItemIds());
				HibernateUtil.save(beanItem.getBean());
				Notification.show("Speichern Erfolgreich.", Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			fireDataSavedEvent();
		});
	}
}
