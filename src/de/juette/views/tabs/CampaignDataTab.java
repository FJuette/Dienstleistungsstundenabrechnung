package de.juette.views.tabs;

import java.util.Collection;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Notification.Type;

import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

public class CampaignDataTab extends MyDataTab<Campaign> {

	private static final long serialVersionUID = -5078471603960939464L;

	@SuppressWarnings("unchecked")
	public CampaignDataTab(BeanItem<Campaign> beanItem) {
		this.beanItem = beanItem;
		setSizeFull();
		setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<Campaign>(Campaign.class);
		fieldGroup.setItemDataSource(beanItem);
		
		TextField txtYear = new TextField("Jahr");
		fieldGroup.bind(txtYear, "year");
		txtYear.setNullRepresentation("");
		addComponent(txtYear);

		TextField txtContent = new TextField("Beschreibung");
		txtContent.setNullRepresentation("");
		fieldGroup.bind(txtContent, "description");
		addComponent(txtContent);

		BeanItemContainer<Member> members = new BeanItemContainer<Member>(Member.class);
		members.addAll((Collection<? extends Member>) HibernateUtil.getAllAsList(Member.class));
		ComboBox cbContact = new ComboBox("Ansprechpartner");
		cbContact.setContainerDataSource(members);
		cbContact.setItemCaptionPropertyId("fullName");
		cbContact.setFilteringMode(FilteringMode.CONTAINS);
		cbContact.setImmediate(true);
		fieldGroup.bind(cbContact, "contact");
		addComponent(cbContact);

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		addComponent(btnSave);
		
		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.save(beanItem.getBean());
			fireDataSavedEvent();
		});
	}
}
