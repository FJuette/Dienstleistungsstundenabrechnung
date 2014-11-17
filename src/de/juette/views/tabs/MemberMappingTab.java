package de.juette.views.tabs;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;

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
import de.juette.model.Log;
import de.juette.model.Member;

public class MemberMappingTab extends FormLayout {
	
	private static final long serialVersionUID = 7673240871018265523L;
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);
	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Category> mCategories;
	
	private Member mBefore;
	
	public MemberMappingTab(BeanItem<Member> beanItem, String caption,
			String columnName) {
		
		mBefore = beanItem.getBean();
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
				if (caption.equals("Gruppen")) {
					(beanItem.getBean()).setGroups(mGroups.getItemIds());
					
					Log log = new Log();
					if (!(beanItem.getBean().getGroups().size() == mGroups.size() && beanItem.getBean().getGroups().containsAll(mGroups.getItemIds()))) {
						log.setChangedMember(beanItem.getBean().getFullName());
						log.setDescription("Gruppenzugehörigkeit verändert");
						log.setEditor(SecurityUtils.getSubject().getPrincipal().toString());
						log.setChangedMemberId(beanItem.getBean().getId());
						HibernateUtil.save(log);
					}
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
