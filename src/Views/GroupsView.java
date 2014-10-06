package Views;

import model.Group;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
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

	@PropertyId("gruppenname")
	private TextField txtGruppenname = new TextField("Gruppenname:");
	@PropertyId("befreit")
	private CheckBox cbBefreit = new CheckBox("Befreit");

	private final Table tblGroups = new Table();
	private FormLayout editorLayout = new FormLayout();
	private FieldGroup editorFields = new FieldGroup();
	private BeanItemContainer<Group> groups = new BeanItemContainer<>(Group.class);
	
	private final Button btnNewGroup = new Button("Neue Gruppe");
	private final Button btnSave = new Button("Speichern");
	private final Button btnDelete = new Button("Löschen");
	
	public GroupsView() {
		initLayout();
		initTable();
		initEditor();
	}
	
	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Gruppenverwaltung");
		title.addStyleName("h1");
		addComponent(title);
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		addComponent(splitPanel);
		
		VerticalLayout leftLayout = new VerticalLayout();
		splitPanel.addComponent(leftLayout);
		splitPanel.addComponent(editorLayout);
		leftLayout.addComponent(tblGroups);
		
		splitPanel.setSplitPosition(25, Unit.PERCENTAGE);
		splitPanel.setLocked(true);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(btnNewGroup);
		
		btnNewGroup.addClickListener(event -> {
			newGroupWindow();
		});
		
		leftLayout.setWidth("100%");
		leftLayout.setSpacing(true);
		leftLayout.setExpandRatio(tblGroups, 1);
		tblGroups.setSizeFull();
		
		bottomLeftLayout.setWidth("100%");
		editorLayout.setMargin(true);
		editorLayout.setVisible(false);
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
		
		tblGroups.addItemClickListener(event -> {
			editorFields.setItemDataSource(event.getItem());
			editorFields.bindMemberFields(this);
			editorLayout.setVisible(event.getItem() != null);
		});
		
		updateTable();
	}
	
	private void initEditor() {
		editorLayout.addComponent(txtGruppenname);
		txtGruppenname.setWidth("25%");
		editorLayout.addComponent(cbBefreit);
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponents(btnSave, btnDelete);
		editorLayout.addComponent(buttonLayout);
		editorLayout.setWidth("100%");
				
		btnSave.addClickListener(event -> {
			try {
				editorFields.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		btnDelete.setStyleName("danger");
		btnDelete.addClickListener(event -> {
			try {
				groups.removeItem(tblGroups.getValue());
				updateTable();
				editorLayout.setVisible(false);
			} catch (Exception e2) {
				Notification.show("Fehler: " + e2.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		editorFields.setBuffered(false);
	}
	
	private void updateTable() {
		if (tblGroups.size() > 15) {
			tblGroups.setPageLength(15);
		} else {
			tblGroups.setPageLength(tblGroups.size());
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

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
