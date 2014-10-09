package Views;

import model.Group;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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
public class GroupsView extends VerticalLayout implements View {

	private final Table tblGroups = new Table();
	private BeanItemContainer<Group> groups = new BeanItemContainer<>(Group.class);
	
	private final Button btnNewGroup = new Button("Neue Gruppe");
	private final Button btnChange = new Button("Bearbeiten");
	
	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				groups.removeItem(tblGroups.getValue());
				updateTable();
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};
	
	public GroupsView() {
		initLayout();
		initTable();
	}
	
	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Gruppenverwaltung");
		title.addStyleName("h1");
		addComponent(title);
				
		addComponent(tblGroups);
		
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(btnChange);
		
		btnChange.addClickListener(event -> {
			try {
				if("Bearbeiten".equals(btnChange.getCaption())) {
					tblGroups.setEditable(true);
					btnChange.setCaption("Speichern");
					updateTable();
				} else {
					tblGroups.setEditable(false);
					btnChange.setCaption("Bearbeiten");
					Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
					updateTable();
				}
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		bottomLeftLayout.addComponent(btnNewGroup);
		btnNewGroup.addClickListener(event -> {
			newGroupWindow();
		});
	}
	
	private void initTable() {
		groups = ComponentHelper.getDummyGroups();
		
		tblGroups.setContainerDataSource(groups);
		tblGroups.setSelectable(true);
		tblGroups.setImmediate(true);
		tblGroups.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblGroups.setVisibleColumns( new Object[] {"gruppenname", "befreit"} );
		tblGroups.setColumnHeaders("Gruppe", "DLS Befreit");
		tblGroups.setConverter("befreit", new BooleanToGermanConverter());
		tblGroups.addActionHandler(getActionHandler());
		tblGroups.setWidth("40%");

		updateTable();
	}
	
	private void updateTable() {
		if (tblGroups.size() > 15) {
			tblGroups.setPageLength(15);
		} else {
			tblGroups.setPageLength(tblGroups.size() + 1);
		}
		tblGroups.markAsDirtyRecursive();
	}
	
	private void newGroupWindow() {
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
		layout.addComponent(btnSaveNewGroup);
		
		btnSaveNewGroup.addClickListener(event -> {
			groups.addItem(new Group(txtNewGroup.getValue(), cbNewGroup.getValue()));
			updateTable();
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
