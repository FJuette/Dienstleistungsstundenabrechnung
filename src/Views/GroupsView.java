package Views;

import model.Group;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
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
	
	public GroupsView() {
		initLayout();
		initTable();
		initEditor();
	}
	
	private void initLayout() {
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		addComponent(splitPanel);
		
		Label title = new Label("Gruppenverwaltung");
		title.addStyleName("h1");
		addComponent(title);
		
		VerticalLayout leftLayout = new VerticalLayout();
		setSpacing(true);
		setMargin(true);
		addComponent(leftLayout);
		addComponent(editorLayout);
		leftLayout.addComponent(tblGroups);
		
		splitPanel.setSplitPosition(25, Unit.PERCENTAGE);
		splitPanel.setLocked(true);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(btnNewGroup);
		
		leftLayout.setWidth("100%");
		
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
		tblGroups.setVisibleColumns( new Object[] {"gruppenname", "befreit"} );
		tblGroups.setColumnHeaders("Gruppe", "Befreit");
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
		editorLayout.addComponent(cbBefreit);
		editorLayout.addComponent(btnSave);
		editorLayout.setWidth("100%");
		
		btnSave.addClickListener(event -> {
			try {
				editorFields.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
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

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
