package de.juette.views.tabs;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

import de.juette.dlsa.layout.DataTabLayout;
import de.juette.model.HibernateUtil;
import de.juette.model.Sector;

public class SectorDataTab extends DataTabLayout<Sector>{

	private static final long serialVersionUID = -2227095821508190725L;
	private FieldGroup fieldGroup = new BeanFieldGroup<Sector>(Sector.class);
	
	public SectorDataTab(BeanItem<Sector> beanItem) {
		this.beanItem = beanItem;
		setSizeFull();
		setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<Sector>(Sector.class);
		fieldGroup.setItemDataSource(beanItem);

		TextField txtName = new TextField("Bereichsname");
		txtName.setWidth("40%");
		txtName.setNullRepresentation("");
		addComponent(txtName);
		fieldGroup.bind(txtName, "sectorname");

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		addComponent(btnSave);

		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(beanItem.getBean());
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			fireDataSavedEvent();
		});
	}
	
}
