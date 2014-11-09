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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.AbstractEntity;
import de.juette.model.HibernateUtil;
import de.juette.model.Role;
import de.juette.model.User;

@SuppressWarnings("serial")
public class UserView extends ComplexLayout implements View {

	protected BeanItemContainer<User> beans;
	private FieldGroup fieldGroup;
	private FormLayout tabData = new FormLayout();

	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		@SuppressWarnings("unchecked")
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				// TODO: Auf aktuellen Benutzer prüfen
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(
						(Class<? extends AbstractEntity>) table.getValue()
								.getClass(),
						((AbstractEntity) table.getValue()).getId().toString());
			}
		}

		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}

	public UserView() {
		btnNew.setCaption("Neuer Benutzer");
		initLayout("<strong>Benutzerverwaltung</strong>");
		extendLayout();
		initTable();
		formatTable();
		initTabs();

		btnNew.addClickListener(event -> {
			openUserWindow(new BeanItem<User>(new User()), "Benutzer anlegen");
		});
	}
	
	private void extendLayout() {
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		innerButtonLayout.addComponent(btnNew);
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);
		
		btnNew.addClickListener(event -> {
			
		});
	}

	@SuppressWarnings("unchecked")
	private void initTable() {
		beans = new BeanItemContainer<>(User.class);
		beans.addAll((Collection<? extends User>) HibernateUtil.getAllAsList(User.class));

		table.setContainerDataSource(beans);
		table.removeAllActionHandlers();
		table.addActionHandler(getActionHandler());
		table.addGeneratedColumn("html", new ColumnGenerator() {
			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				String html = ((User) itemId).getHtmlName();
				Label label = new Label(html, ContentMode.HTML);
				label.setSizeUndefined();
				return label;
			}
		});
		table.setVisibleColumns(new Object[] { "html" });
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.addItemClickListener(event -> {
			setTabData((BeanItem<User>) event.getItem());
		});
		table.select(table.firstItemId());
		table.focus();
		table.setValue(table.firstItemId());
		if (beans.size() > 0) {
			setTabData(beans.getItem(beans.getIdByIndex(0)));
		}
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
				HibernateUtil.saveAll(beans.getItemIds());
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

		getUI().addWindow(window);
	}
	
	private void initTabs() {
		contentTabs.addTab(tabData, "Daten");
		contentTabs.setStyleName("framed equal-width-tabs padded-tabbar");
	}
	
	private void setTabData(BeanItem<User> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Benutzer: </strong> "
					+ beanItem.getBean().getUsername());
			tabData.removeAllComponents();
			tabData.addComponent(new UserDataTab(beanItem));
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
