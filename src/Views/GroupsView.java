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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class GroupsView extends HorizontalSplitPanel implements View {

	private final Table tblGroups = new Table();
	@PropertyId("gruppenname")
	private TextField txtGruppenname = new TextField("Gruppenname:");
	@PropertyId("befreit")
	private CheckBox cbBefreit = new CheckBox("Befreit");
	private FieldGroup fieldGroup;
	private BeanItemContainer<Group> groups = new BeanItemContainer<>(Group.class);
	
	public GroupsView() {
		
		initTable();
		setFirstComponent(tblGroups);
		setSecondComponent(createEditFields());
		
		setSplitPosition(15, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	private void initTable() {
		groups = ComponentHelper.getDummyGroups();
		
		tblGroups.setContainerDataSource(groups);
		tblGroups.setSelectable(true);
		tblGroups.setVisibleColumns( new Object[] {"gruppenname", "befreit"} );
		tblGroups.setColumnHeaders("Gruppe", "Befreit");
		tblGroups.setHeight("400");
		tblGroups.setConverter("befreit", new BooleanToGermanConverter());
		
		tblGroups.addItemClickListener(event -> {
			fieldGroup.setItemDataSource(event.getItem());
			fieldGroup.bindMemberFields(this);
		});
	}
	
	private FormLayout createEditFields() {
		// Set the Caption of the TextField on the Top
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(txtGruppenname);
		
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
		
		layout.addComponents(txtLayout, cbBefreit, btnSave);
		return layout;
	}


	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
