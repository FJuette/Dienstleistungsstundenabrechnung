package de.juette.views.tabs;

import org.apache.shiro.SecurityUtils;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;

import de.juette.dlsa.GeneralHandler;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

public class MemberMappingTab extends FormLayout {
	
	private static final long serialVersionUID = 7673240871018265523L;
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);
	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Category> mCategories;
	
	public MemberMappingTab(BeanItem<Member> beanItem, String caption,
			String columnName) {
		groups.addAll(HibernateUtil.getAllAsList(Group.class));
		categories.addAll(HibernateUtil.getAllAsList(Category.class));
		
		setMargin(true);
		setStyleName("myFormLayout");

		ComboBox cbAll = new ComboBox("Alle " + caption + ":");
		// Datenquelle abhängig von der Caption auf Gruppen oder Sparten setzen
		cbAll.setContainerDataSource(caption.equals("Gruppen") ? groups
				: categories);

		cbAll.setItemCaptionPropertyId(columnName);
		cbAll.setImmediate(true);
		cbAll.setWidth("350");

		addComponent(cbAll);

		Button btnAdd = new Button(FontAwesome.PLUS);
		addComponent(btnAdd);

		mGroups = new BeanItemContainer<Group>(Group.class);
		mGroups.addAll(beanItem.getBean().getGroups());
		mCategories = new BeanItemContainer<Category>(Category.class);
		mCategories.addAll(beanItem.getBean().getCategories());

		Table tblMemberElements = new Table("Zugeordnete " + caption + ":");
		tblMemberElements
				.setContainerDataSource(caption.equals("Gruppen") ? mGroups
						: mCategories);
		tblMemberElements.setVisibleColumns(new Object[] { columnName });
		tblMemberElements.setColumnHeaders(caption);
		tblMemberElements.setWidth("350");
		tblMemberElements.setSelectable(true);
		tblMemberElements.addStyleName("no-stripes");
		GeneralHandler.setTableSize(tblMemberElements);

		addComponent(tblMemberElements);

		btnAdd.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on
			// the same Object types
			if (caption.equals("Gruppen")) {
				if (cbAll.getValue() != null
						&& !mGroups.containsId(cbAll.getValue())) {
					mGroups.addItem(cbAll.getValue());
				}
			} else {
				if (cbAll.getValue() != null
						&& !mCategories.containsId(cbAll.getValue())) {
					mCategories.addItem(cbAll.getValue());
				}
			}
			GeneralHandler.setTableSize(tblMemberElements);
		});

		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblMemberElements.getValue() != null) {
				tblMemberElements.removeItem(tblMemberElements.getValue());
			}
			GeneralHandler.setTableSize(tblMemberElements);
		});

		addComponent(btnRemove);

		Button btnSaveChanges = new Button("Speichern");
		btnSaveChanges.setStyleName("friendly");
		
		// Show these two fields not in the guest role
		if (!SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(btnSaveChanges);
		}

		btnSaveChanges.addClickListener(event -> {
			try {
				if (caption.equals("Gruppen")) {
					(beanItem.getBean()).setGroups(mGroups.getItemIds());
				} else {
					(beanItem.getBean()).setCategories((mCategories
							.getItemIds()));
				}
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			if (caption.equals("Sparten")) {
				HibernateUtil.save(beanItem.getBean());
				Notification.show("Speichern Erfolgreich.", Type.TRAY_NOTIFICATION);
			} else if (caption.equals("Gruppen")) {
				HibernateUtil.save(beanItem.getBean());
				Notification.show("Speichern Erfolgreich.", Type.TRAY_NOTIFICATION);
			}
		});
	}
	
}
