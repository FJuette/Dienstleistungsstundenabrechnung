package Views;

import model.Activity;
import model.Group;
import model.Member;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;

public class ActivityView extends EditableTable<Activity> implements View {

	public ActivityView() {
		beans = new BeanItemContainer<>(Activity.class);
		beans = ComponentHelper.getDummyActivities();

		btnNew.setCaption("Neue Aktion");
		initLayout("Aktionsverwaltung");
		initTable();
		extendTable();
	}

	@Override
	protected void extendTable() {
		table.setVisibleColumns(new Object[] { "jahr", "beschreibung",
				"umfangDLS" });
		table.setColumnHeaders("Jahr", "Beschreibung", "Umfang DLS");
	}

	@Override
	protected void newBeanWindow() {
		Window window = new Window("Anlegen einer neuen Aktion");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		TextField txtYear = new TextField("Jahr");
		txtYear.setWidth("100%");
		layout.addComponent(txtYear);

		TextField txtContent = new TextField("Beschreibung");
		txtContent.setWidth("100%");
		layout.addComponent(txtContent);

		TextField txtDls = new TextField("Umfang der DLS");
		txtDls.setWidth("100%");
		layout.addComponent(txtDls);

		ComboBox cbContact = new ComboBox("Ansprechpartner");
		cbContact.setContainerDataSource(ComponentHelper.getDummyMembers());
		cbContact.setItemCaptionPropertyId("fullName");
		cbContact.setFilteringMode(FilteringMode.CONTAINS);
		cbContact.setImmediate(true);
		layout.addComponent(cbContact);

		ComboBox cbAuthorised = new ComboBox("Autorisiert von");
		cbAuthorised.setContainerDataSource(ComponentHelper.getDummyMembers());
		cbAuthorised.setItemCaptionPropertyId("fullName");
		cbAuthorised.setFilteringMode(FilteringMode.CONTAINS);
		cbAuthorised.setImmediate(true);
		layout.addComponent(cbAuthorised);

		Button btnSaveNewGroup = new Button("Speichern");
		btnSaveNewGroup.setStyleName("friendly");
		layout.addComponent(btnSaveNewGroup);

		btnSaveNewGroup.addClickListener(event -> {
			beans.addItem(new Activity(txtYear.getValue(), txtContent
					.getValue(), Float.parseFloat(txtDls.getValue()),
					(Member) cbContact.getValue(), (Member) cbAuthorised
							.getValue()));
			ComponentHelper.updateTable(table);
			window.close();
		});

		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
