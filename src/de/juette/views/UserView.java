package de.juette.views;

import org.apache.shiro.crypto.hash.Sha256Hash;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
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

import de.juette.model.HibernateUtil;
import de.juette.model.Role;
import de.juette.model.User;
import de.juette.views.tabs.UserDataTab;

public class UserView extends ComplexLayout implements View {

	private static final long serialVersionUID = -2447708014208927305L;
	protected BeanItemContainer<User> beans;
	private FormLayout tabData = new FormLayout();

	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = 8102528189856905975L;
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				// TODO: Auf aktuellen Benutzer prüfen
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(User.class, ((User) table.getValue())
						.getId().toString());
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
			newUserWindow();
		});
	}

	private void extendLayout() {
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		innerButtonLayout.addComponent(btnNew);
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);
	}

	private void initTable() {
		beans = new BeanItemContainer<>(User.class);
		beans.addAll(HibernateUtil.getAllAsList(User.class));

		table.setContainerDataSource(beans);
		table.removeAllActionHandlers();
		table.addActionHandler(getActionHandler());
		table.addGeneratedColumn("html", new ColumnGenerator() {
			private static final long serialVersionUID = 7932554345700896822L;

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
			setTabData(beans.getItem(event.getItemId()));
		});
		table.select(table.firstItemId());
		table.focus();
		table.setValue(table.firstItemId());
		if (beans.size() > 0) {
			setTabData(beans.getItem(beans.getIdByIndex(0)));
		}
	}

	private void newUserWindow() {
		Window window = new Window("Benutzer anlegen");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<User>(User.class);
		fieldGroup.setItemDataSource(new User());

		TextField txtUserName = new TextField("Benutzername");
		txtUserName.setWidth("100%");
		txtUserName.setNullRepresentation("");
		layout.addComponent(txtUserName);
		fieldGroup.bind(txtUserName, "username");

		PasswordField txtUserPass = new PasswordField("Passwort");
		txtUserPass.addBlurListener(event -> {
			if (txtUserPass.getValue() != null) {
				txtUserPass.setValue(new Sha256Hash(txtUserPass.getValue())
						.toString());
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
		roles.addAll(HibernateUtil.getAllAsList(Role.class));
		cbRoles.setContainerDataSource(roles);
		cbRoles.setItemCaptionPropertyId("rolename");
		cbRoles.setNullSelectionAllowed(false);
		fieldGroup.bind(cbRoles, "role");
		layout.addComponent(cbRoles);

		Button btnSaveUser = new Button("Speichern");
		btnSaveUser.setStyleName("friendly");
		layout.addComponent(btnSaveUser);

		btnSaveUser.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(fieldGroup.getItemDataSource().getBean());
				beans.addItem(fieldGroup.getItemDataSource().getBean());
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
			UserDataTab userTab = new UserDataTab(beanItem);
			tabData.addComponent(userTab);
			userTab.addDataSaveListener(event -> {
				beans.addBean((User) event.getBeanItem().getBean());
				table.refreshRowCache();
			});
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
}
