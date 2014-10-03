package Views;

import model.Group;
import model.Role;
import model.User;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class UserView extends HorizontalSplitPanel implements View {

	private final Table tblUsers = new Table();
	@PropertyId("benutzername")
	private TextField txtBenutzername = new TextField("Benutzername:");
	@PropertyId("passwort")
	private PasswordField txtPassword = new PasswordField("Passwort:");
	@PropertyId("rolle")
	private ComboBox cbRole = new ComboBox("Rolle:");
	@PropertyId("aktiv")
	private CheckBox cbAktiv = new CheckBox("Aktiv:");
	
	private FieldGroup fieldGroup;
	private BeanItemContainer<User> users = new BeanItemContainer<>(User.class);
	private BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
	
	public UserView() {
		createDummyRoles();
		cbRole.setContainerDataSource(roles);
		cbRole.setItemCaptionPropertyId("rollenname");
		cbRole.setImmediate(true);
		
		initTable();
		setFirstComponent(tblUsers);
		setSecondComponent(createEditFields());
		
		setSplitPosition(15, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	private void initTable() {
		createDummyUsers();
		
		tblUsers.setContainerDataSource(users);
		tblUsers.setSelectable(true);
		tblUsers.setVisibleColumns( new Object[] {"benutzername", "aktiv"} );
		tblUsers.setColumnHeaders("Benutzername", "Aktiv");
		tblUsers.setHeight("400");
		tblUsers.setConverter("aktiv", new BooleanToGermanConverter());
		
		tblUsers.addItemClickListener(event -> {
			fieldGroup.setItemDataSource(event.getItem());
			fieldGroup.bindMemberFields(this);
		});
	}

	private void createDummyUsers() {
		users.addItem(new User("Administrator", "geheim", true, roles.getIdByIndex(0)));
		users.addItem(new User("Benutzer1", "nochgeheimer", true, roles.getIdByIndex(1)));
		users.addItem(new User("Inaktiver", "geheim", false, roles.getIdByIndex(1)));
	}
	
	private void createDummyRoles() {
		roles.addItem(new Role("Admin"));
		roles.addItem(new Role("Benutzer"));
	}

	private FormLayout createEditFields() {
		
		FormLayout layout = new FormLayout();
		layout.setSizeUndefined();
		layout.setMargin(true);
		
		fieldGroup = new BeanFieldGroup<Group>(Group.class);
		
		Button btnSave = new Button("Speichern");
		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		layout.addComponents(ComponentHelper.getCaptionOnTop(txtBenutzername), 
				ComponentHelper.getCaptionOnTop(txtPassword), 
				ComponentHelper.getCaptionOnTop(cbRole), 
				cbAktiv, btnSave);
		return layout;
	}	

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
