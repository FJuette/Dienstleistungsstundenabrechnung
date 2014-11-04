package de.juette.views;

import java.util.Collection;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Notification.Type;

import de.juette.dlsa.ComponentHelper;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberMappingTab extends FormLayout {
	
	private FieldGroup fieldGroup = new BeanFieldGroup<Member>(Member.class);
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);
	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Category> mCategories;
	
	@SuppressWarnings("unchecked")
	public MemberMappingTab(BeanItem<Member> beanItem, String caption,
			String columnName) {
		
		groups.addAll((Collection<? extends Group>) HibernateUtil
				.getAllAsList(Group.class));
		categories.addAll((Collection<? extends Category>) HibernateUtil
				.getAllAsList(Category.class));
		
		setMargin(true);
		setStyleName("myFormLayout");

		fieldGroup.setItemDataSource(beanItem);

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
		mGroups.addAll(((BeanItem<Member>) beanItem).getBean().getGroups());
		mCategories = new BeanItemContainer<Category>(Category.class);
		mCategories.addAll(((BeanItem<Member>) beanItem).getBean()
				.getCategories());

		Table tblMemberElements = new Table("Zugeordnete " + caption + ":");
		tblMemberElements
				.setContainerDataSource(caption.equals("Gruppen") ? mGroups
						: mCategories);
		tblMemberElements.setVisibleColumns(new Object[] { columnName });
		tblMemberElements.setColumnHeaders(caption);
		tblMemberElements.setWidth("350");
		tblMemberElements.setSelectable(true);
		tblMemberElements.addStyleName("no-stripes");
		ComponentHelper.setTableSize(tblMemberElements);

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
			ComponentHelper.setTableSize(tblMemberElements);
		});

		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblMemberElements.getValue() != null) {
				tblMemberElements.removeItem(tblMemberElements.getValue());
			}
			ComponentHelper.setTableSize(tblMemberElements);
		});

		addComponent(btnRemove);

		Button btnSaveChanges = new Button("Speichern");
		btnSaveChanges.setStyleName("friendly");
		addComponent(btnSaveChanges);

		btnSaveChanges.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Gruppen")) {
					(beanItem.getBean()).setGroups(mGroups.getItemIds());
				} else {
					(beanItem.getBean()).setCategories((mCategories
							.getItemIds()));
				}
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.save(beanItem.getBean());
		});
	}
	
}
