package de.juette.views;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class CampaignView extends EditableTable<Campaign> implements View {

	@SuppressWarnings("unchecked")
	public CampaignView() {
		beans = new BeanItemContainer<>(Campaign.class);
		beans.addAll((Collection<? extends Campaign>) HibernateUtil.getAllAsList(Campaign.class));

		btnNew.setCaption("Neue Aktion");
		initLayout("Aktionsverwaltung");
		initTable();
		extendTable();
	}

	@Override
	protected void extendTable() {
		beans.addNestedContainerProperty("contact.fullName");
		
		table.setVisibleColumns(new Object[] { "year", "description", "contact.fullName" });
		table.setColumnHeaders("Jahr", "Beschreibung", "Kontakt");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void newBeanWindow() {
		Window window = new Window("Anlegen einer neuen Aktion");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		TextField txtYear = new TextField("Jahr");
		txtYear.setWidth("100%");
		layout.addComponent(txtYear);

		TextField txtContent = new TextField("Beschreibung");
		txtContent.setWidth("100%");
		layout.addComponent(txtContent);

		BeanItemContainer<Member> members = new BeanItemContainer<Member>(Member.class);
		members.addAll((Collection<? extends Member>) HibernateUtil.getAllAsList(Member.class));
		ComboBox cbContact = new ComboBox("Ansprechpartner");
		cbContact.setContainerDataSource(members);
		cbContact.setItemCaptionPropertyId("fullName");
		cbContact.setFilteringMode(FilteringMode.CONTAINS);
		cbContact.setImmediate(true);
		layout.addComponent(cbContact);

		Button btnSaveNewGroup = new Button("Speichern");
		btnSaveNewGroup.setStyleName("friendly");
		layout.addComponent(btnSaveNewGroup);

		btnSaveNewGroup.addClickListener(event -> {
			table.commit();
			beans.addItem(new Campaign(txtYear.getValue(), txtContent
					.getValue(), (Member) cbContact.getValue()));
			updateTable();
			HibernateUtil.saveAll(beans.getItemIds());
			window.close();
		});

		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
