package de.juette.views;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;

import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;

@SuppressWarnings("serial")
public class MemberMappingWindow extends Window {
	
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);
	
	private Group group;
	private Category category;

	@SuppressWarnings("unchecked")
	public MemberMappingWindow(String caption, String columnName, String method) {
		groups.addAll((Collection<? extends Group>) HibernateUtil
				.getAllAsList(Group.class));
		categories.addAll((Collection<? extends Category>) HibernateUtil
				.getAllAsList(Category.class));
		
		setModal(true);
		setWidth("500");
		if (caption.equals("Gruppe") && method.equals("add")) {
			setCaption("Hinzufügen einer Gruppe");
		} else if (caption.equals("Gruppe") && method.equals("remove")) {
			setCaption("Entfernen einer Gruppe");
		} else if (caption.equals("Sparte") && method.equals("add")) {
			setCaption("Hinzufügen einer Sparte");
		} else {
			setCaption("Entfernen einer Sparte");
		}
		

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		ComboBox cbAll = new ComboBox(caption + " auswählen:");
		cbAll.setContainerDataSource(caption.equals("Gruppe") ? groups
				: categories);

		cbAll.setItemCaptionPropertyId(columnName);
		cbAll.setImmediate(true);
		cbAll.setWidth("300");
		layout.addComponent(cbAll);
		
		Button btnSave;
		
		if (method.equals("add")) {
			btnSave = new Button("Hinzufügen", FontAwesome.PLUS);
			btnSave.setStyleName("primary");
		}
		else {
			btnSave = new Button("Entfernen", FontAwesome.MINUS);
			btnSave.setStyleName("danger");
		}
		btnSave.addClickListener(event -> {
			if (caption.equals("Gruppe")) {
				group = (Group) cbAll.getValue();
			}
			else {
				category = (Category) cbAll.getValue();
			}
			close();
		});
		layout.addComponent(btnSave);
		
	}

	public Group getGroup() {
		return group;
	}

	public Category getCategory() {
		return category;
	}
}
