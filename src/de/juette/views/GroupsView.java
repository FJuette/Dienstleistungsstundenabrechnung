package de.juette.views;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.GeneralHandler;
import de.juette.dlsa.layout.EditableTable;
import de.juette.model.BasicGroup;
import de.juette.model.Group;
import de.juette.model.GroupChanges;
import de.juette.model.HibernateUtil;

public class GroupsView extends EditableTable<Group> implements View {

	private static final long serialVersionUID = 3464957902386384502L;
	DateTimeFormatter dateStringFormat = DateTimeFormat
			.forPattern("dd.MM.yyyy");

	@SuppressWarnings("unchecked")
	public GroupsView() {
		if (SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
		beans = new BeanItemContainer<>(Group.class);
		beans.addAll(HibernateUtil.getAllAsList(Group.class));

		registerPropertyChangeListener();

		btnNew.setCaption("Neue Gruppe");
		initLayout("Gruppenverwaltung");
		initTable();
		extendTable();

		btnChange.addClickListener(event -> {
			try {
				if ("Bearbeiten".equals(btnChange.getCaption())) {
					table.setEditable(true);
					btnChange.setCaption("Speichern");
					btnChange.setStyleName("friendly");
					updateTable();
				} else {
					table.setEditable(false);
					btnChange.setCaption("Bearbeiten");
					btnChange.setStyleName("normal");
					table.commit();
					HibernateUtil.saveAll((List<Group>) table.getItemIds());
					Notification.show("Speichern erfolgreich",
							Notification.Type.TRAY_NOTIFICATION);
					updateTable();
				}
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			}
		});
	}
	
	private void registerPropertyChangeListener() {
		for (Group g : beans.getItemIds()) {
			g.removeAllPropertyChangeListeners();
			g.addPropertyChangeListener(e -> {
				GroupChanges gc = new GroupChanges();
				gc.setGroupId(g.getId());
				gc.setOldValue((Boolean) e.getOldValue());
				gc.setNewValue((Boolean) e.getNewValue());
				gc.setRefDate(DateTime.now().toDate());
				HibernateUtil.save(gc);
			});
		}
	}

	@Override
	protected void extendTable() {
		table.setVisibleColumns(new Object[] { "groupName", "liberated" });
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
			Group g = new Group(txtNewGroup.getValue(), cbNewGroup.getValue());
			beans.addItem(g);
			updateTable();
			HibernateUtil.save(g);
			BasicGroup bg = new BasicGroup();
			bg.setGroup(g);
			bg.setGroupName(g.getGroupName());
			bg.setLiberate(g.getLiberated());
			HibernateUtil.save(bg);
			g.setBasicGroup(bg);
			HibernateUtil.save(g);
			registerPropertyChangeListener();
			window.close();
		});

		getUI().addWindow(window);

	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
