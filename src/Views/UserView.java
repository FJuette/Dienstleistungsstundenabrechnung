package Views;

import model.Role;
import model.User;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class UserView extends VerticalLayout implements View {

	private final Table tblUsers = new Table();
	
	private final Button btnNewUser = new Button("Neuer Benutzer");
	private final Button btnChange = new Button("Bearbeiten");
	private BeanItemContainer<User> users = new BeanItemContainer<>(User.class);
	
	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				users.removeItem(tblUsers.getValue());
				ComponentHelper.updateTable(tblUsers);
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};
	
	public UserView() {
		initLayout();
		initTable();
	}
	
	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Benutzerverwaltung");
		title.addStyleName("h1");
		addComponent(title);
				
		addComponent(tblUsers);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(btnChange);
		
		btnChange.addClickListener(event -> {
			try {
				if("Bearbeiten".equals(btnChange.getCaption())) {
					tblUsers.setEditable(true);
					btnChange.setCaption("Speichern");
				} else {
					tblUsers.setEditable(false);
					btnChange.setCaption("Bearbeiten");
					Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
				}
				ComponentHelper.updateTable(tblUsers);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		bottomLeftLayout.addComponent(btnNewUser);
		btnNewUser.addClickListener(event -> {
			newUserWindow();
		});
	}
	

	private void initTable() {
		users = ComponentHelper.getDummyUsers();
		users.addNestedContainerProperty("rolle.rollenname");
		
		tblUsers.setContainerDataSource(users);
		tblUsers.setSelectable(true);
		tblUsers.setImmediate(true);
		tblUsers.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblUsers.setVisibleColumns( new Object[] {"benutzername", "aktiv", "rolle.rollenname"} );
		tblUsers.setColumnHeaders("Benutzername", "Aktiv", "Rolle");
		tblUsers.setConverter("aktiv", new BooleanToGermanConverter());
		tblUsers.addActionHandler(getActionHandler());
		tblUsers.setWidth("40%");
		
		ComponentHelper.updateTable(tblUsers);
	}
	

	private void newUserWindow() {
		Window window = new Window("Anlegen eines neuen Benutzers");
		window.setModal(true);
		window.setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		TextField txtUserName = new TextField("Benutzername");
		txtUserName.setWidth("100%");
		layout.addComponent(txtUserName);

		TextField txtUserPass = new TextField("Passwort");
		txtUserPass.setWidth("100%");
		layout.addComponent(txtUserPass);
		
		CheckBox cbActive = new CheckBox("Aktiv");
		layout.addComponent(cbActive);
		
		ComboBox cbRoles = new ComboBox("Rollen");
		cbRoles.setContainerDataSource(ComponentHelper.getDummyRoles());
		layout.addComponent(cbRoles);
		
		Button btnSaveNewGroup = new Button("Speichern");
		layout.addComponent(btnSaveNewGroup);
		
		btnSaveNewGroup.addClickListener(event -> {
			users.addItem(new User(txtUserName.getValue(), txtUserPass.getValue(), 
					cbActive.getValue(), (Role)cbRoles.getValue()));
			ComponentHelper.updateTable(tblUsers);
			window.close();
		});
		
		getUI().addWindow(window);
	}
	
	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
