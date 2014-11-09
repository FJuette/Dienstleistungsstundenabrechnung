package de.juette.views;

import java.util.Collection;

import org.apache.shiro.crypto.hash.Sha256Hash;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Notification.Type;

import de.juette.model.HibernateUtil;
import de.juette.model.Role;
import de.juette.model.User;

@SuppressWarnings("serial")
public class UserDataTab extends FormLayout {
	
	private FieldGroup fieldGroup = new BeanFieldGroup<User>(User.class);

	@SuppressWarnings("unchecked")
	public UserDataTab(BeanItem<User> beanItem) {
		setSizeFull();
		setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<User>(User.class);
		fieldGroup.setItemDataSource(beanItem);
		
		TextField txtUserName = new TextField("Benutzername");
		txtUserName.setWidth("40%");
		txtUserName.setNullRepresentation("");
		addComponent(txtUserName);
		fieldGroup.bind(txtUserName, "username");

		
		PasswordField txtUserPass = new PasswordField("Passwort");
		txtUserPass.addBlurListener(event -> {
			if (txtUserPass.getValue() != null) {
				txtUserPass.setValue(new Sha256Hash(txtUserPass.getValue()).toString());
			}
		});
		txtUserPass.setWidth("40%");
		txtUserPass.setNullRepresentation("");
		addComponent(txtUserPass);
		fieldGroup.bind(txtUserPass, "password");

		CheckBox cbActive = new CheckBox("Aktiv");
		addComponent(cbActive);
		fieldGroup.bind(cbActive, "active");

		ComboBox cbRoles = new ComboBox("Rolle");
		BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
		roles.addAll((Collection<? extends Role>) HibernateUtil.getAllAsList(Role.class));
		cbRoles.setContainerDataSource(roles);
		cbRoles.setItemCaptionPropertyId("rolename");
		cbRoles.setWidth("40%");
		fieldGroup.bind(cbRoles, "role");
		cbRoles.setNullSelectionAllowed(false);
		addComponent(cbRoles);

		Button btnSaveUser = new Button("Speichern");
		btnSaveUser.setStyleName("friendly");
		addComponent(btnSaveUser);

		btnSaveUser.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(beanItem.getBean());
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});
	}

}
