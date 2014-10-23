package de.juette.views;

import java.util.Collection;

import org.apache.shiro.crypto.hash.Sha256Hash;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;
import de.juette.model.AbstractEntity;
import de.juette.model.HibernateUtil;
import de.juette.model.Role;
import de.juette.model.User;

@SuppressWarnings("serial")
public class UserView extends EditableTable<User> implements View {

	private FieldGroup fieldGroup;

	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action CHANGE = new Action("Bearbeiten");
		private final Action[] ACTIONS = new Action[] { CHANGE, REMOVE };

		@SuppressWarnings("unchecked")
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(
						(Class<? extends AbstractEntity>) table.getValue()
								.getClass(),
						((AbstractEntity) table.getValue()).getId().toString());
				ComponentHelper.updateTable(table);
			} else if (action.getCaption().equals("Bearbeiten")) {
				if (table.getValue() != null) {
					openUserWindow(beans.getItem(table.getValue()),
							"Benutzer bearbeiten");
				}
			}
		}

		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}

	@SuppressWarnings("unchecked")
	public UserView() {

		beans = new BeanItemContainer<>(User.class);
		beans.addAll((Collection<? extends User>) HibernateUtil.getAllAsList(User.class));

		btnChange.setVisible(false);

		btnNew.setCaption("Neuer Benutzer");
		initLayout("Benutzerverwaltung");
		initTable();
		extendTable();

		btnNew.addClickListener(event -> {
			openUserWindow(new BeanItem<User>(new User()), "Benutzer anlegen");
		});
	}

	@Override
	protected void extendTable() {
		beans.addNestedContainerProperty("role.rolename");

		table.removeAllActionHandlers();
		table.addActionHandler(getActionHandler());
		table.setVisibleColumns(new Object[] { "username", "active",
				"role.rolename" });
		table.setColumnHeaders("Benutzername", "Aktiv", "Rolle");
		table.setConverter("active", new BooleanToGermanConverter());
		table.setWidth("60%");
	}

	@Override
	protected void newBeanWindow() {

	}

	@SuppressWarnings("unchecked")
	private void openUserWindow(Item beanItem, String caption) {
		Window window = new Window(caption);
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		fieldGroup = new BeanFieldGroup<User>(User.class);
		fieldGroup.setItemDataSource(beanItem);

		TextField txtUserName = new TextField("Benutzername");
		txtUserName.setWidth("100%");
		txtUserName.setNullRepresentation("");
		layout.addComponent(txtUserName);
		fieldGroup.bind(txtUserName, "username");

		
		PasswordField txtUserPass = new PasswordField("Passwort");
		txtUserPass.addBlurListener(event -> {
			if (txtUserPass.getValue() != null) {
				txtUserPass.setValue(new Sha256Hash(txtUserPass.getValue()).toString());
			}
		});
		txtUserPass.setWidth("100%");
		txtUserPass.setNullRepresentation("");
		layout.addComponent(txtUserPass);
		fieldGroup.bind(txtUserPass, "password");

		CheckBox cbActive = new CheckBox("Aktiv");
		layout.addComponent(cbActive);
		fieldGroup.bind(cbActive, "active");

		ComboBox cbRoles = new ComboBox("Rolle");
		BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
		roles.addAll((Collection<? extends Role>) HibernateUtil.getAllAsList(Role.class));
		cbRoles.setContainerDataSource(roles);
		cbRoles.setItemCaptionPropertyId("rolename");
		fieldGroup.bind(cbRoles, "role");
		cbRoles.setNullSelectionAllowed(false);
		//cbRoles.setNullSelectionItemId(roles.getIdByIndex(2));
		layout.addComponent(cbRoles);

		Button btnSaveUser = new Button("Speichern");
		btnSaveUser.setStyleName("friendly");
		layout.addComponent(btnSaveUser);

		btnSaveUser.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Benutzer anlegen")) {
					beans.addItem(new User(txtUserName.getValue(), new Sha256Hash(txtUserPass
							.getValue()).toString(), cbActive.getValue(), (Role) cbRoles
							.getValue()));
				}
				ComponentHelper.updateTable(table);
				HibernateUtil.saveAll(beans.getItemIds());
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
}
