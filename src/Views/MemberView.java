package Views;

import model.Group;
import model.Member;
import model.Subject;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class MemberView extends HorizontalSplitPanel implements View {

	private final Table tblMembers = new Table();
	@PropertyId("mitgliedsnummer")
	private TextField txtMitgliedsnummer = new TextField("Mitgliedsnummer:");
	@PropertyId("nachname")
	private TextField txtNachname = new TextField("Nachname:");
	@PropertyId("vorname")
	private TextField txtVorname = new TextField("Vorname:");
	
	private final ComboBox cbAllGroups = new ComboBox("Alle Gruppen:");
	private final Table tblMemberGroups = new Table("Zugeordnete Gruppen:");
	
	private final ComboBox cbAllSubjects = new ComboBox("Alle Sparten:");
	private final Table tblMemberSubjects = new Table("Zugeordnete Sparten:");
	
	private FieldGroup fieldGroup;
	private BeanItemContainer<Member> members = new BeanItemContainer<>(Member.class);
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(Group.class);
	private BeanItemContainer<Subject> subjects = new BeanItemContainer<Subject>(Subject.class);
	
	private BeanItemContainer<Group> mGroups = new BeanItemContainer<Group>(Group.class);
	private BeanItemContainer<Subject> mSubjects = new BeanItemContainer<Subject>(Subject.class);
	
	private static final int MAX_PAGE_LENGTH = 15;
	
	private Button btnImport = new Button("Mitglieder einlesen");
	
	public MemberView() {
		initTables();
		
		VerticalLayout leftLayout = new VerticalLayout();
		leftLayout.addComponent(tblMembers);
		leftLayout.addComponent(btnImport);
		btnImport.addClickListener(event -> {
			//CsvImportWindow upload = new CsvImportWindow();
			UI.getCurrent().addWindow(new CsvImportWindow());
		});
		
		setFirstComponent(leftLayout);
		setSecondComponent(createEditFields());
		
		setSplitPosition(25, Unit.PERCENTAGE);
		setLocked(true);
	}

	private void initTables() {
		members = ComponentHelper.getDummyMembers();
		groups = ComponentHelper.getDummyGroups();
		subjects = ComponentHelper.getDummySubjects();
		
		cbAllGroups.setContainerDataSource(groups);
		cbAllGroups.setItemCaptionPropertyId("gruppenname");
		cbAllGroups.setImmediate(true);
		
		cbAllSubjects.setContainerDataSource(subjects);
		cbAllSubjects.setItemCaptionPropertyId("spartenname");
		cbAllSubjects.setImmediate(true);
		
		tblMembers.setContainerDataSource(members);
		
		tblMembers.setSelectable(true);
		tblMembers.setVisibleColumns( new Object[] {"mitgliedsnummer", "nachname", "vorname"} );
		tblMembers.setColumnHeaders("Mitgliedsnummer", "Nachname", "Vorname");
		tblMembers.setHeight("100%");
		tblMembers.setWidth("100%");
		
		tblMemberGroups.setHeight("0");
		tblMemberGroups.setSelectable(true);
		tblMemberGroups.setWidth("190");
		
		tblMemberSubjects.setHeight("0");
		tblMemberSubjects.setSelectable(true);
		tblMemberSubjects.setWidth("190");
		
		tblMembers.addItemClickListener(event -> {
			fieldGroup.setItemDataSource(event.getItem());
			fieldGroup.bindMemberFields(this);
			
			mGroups.removeAllItems();
			mGroups.addAll(((BeanItem<Member>)event.getItem()).getBean().getGruppen());
			tblMemberGroups.setContainerDataSource(mGroups);
			tblMemberGroups.setVisibleColumns( new Object[] {"gruppenname"} );
			tblMemberGroups.setColumnHeaders("Gruppen");
			tblMemberGroups.setHeightUndefined();
			updateTable(tblMemberGroups);
			
			mSubjects.removeAllItems();
			mSubjects.addAll(((BeanItem<Member>)event.getItem()).getBean().getSparten());
			tblMemberSubjects.setContainerDataSource(mSubjects);
			tblMemberSubjects.setVisibleColumns( new Object[] {"spartenname"} );
			tblMemberSubjects.setColumnHeaders("Sparten");
			tblMemberSubjects.setHeightUndefined();
			updateTable(tblMemberSubjects);
		});
	}
	
	private FormLayout createEditFields() {
		
		FormLayout layout = new FormLayout();
		layout.setSizeUndefined();
		layout.setMargin(true);
		
		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		
		Button btnAddGroup = new Button("+");
		btnAddGroup.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on the same Object types
			if (cbAllGroups.getValue() != null && !groupsContainItem(((Group)cbAllGroups.getValue()).getGruppenname())) {
				mGroups.addItem(cbAllGroups.getValue());
				updateTable(tblMemberGroups);
			}
		});
		
		Button btnAddSubject = new Button("+");
		btnAddSubject.addClickListener(event -> {
			if (cbAllSubjects.getValue() != null && !subjectsContainItem(((Subject)cbAllSubjects.getValue()).getSpartenname())) {
				mSubjects.addItem(cbAllSubjects.getValue());
				updateTable(tblMemberSubjects);
			}
		});
		
		Button btnDeleteGroup = new Button("-");
		btnDeleteGroup.addClickListener(event -> {
			if (tblMemberGroups.getValue() != null) {
				tblMemberGroups.removeItem(tblMemberGroups.getValue());
				updateTable(tblMemberGroups);
			}
		});

		Button btnDeleteSubject = new Button("-");
		btnDeleteSubject.addClickListener(event -> {
			if (tblMemberSubjects.getValue() != null) {
				tblMemberSubjects.removeItem(tblMemberSubjects.getValue());
				updateTable(tblMemberSubjects);
			}
		});
		
		Button btnSave = new Button("Speichern");
		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		layout.addComponents(txtMitgliedsnummer, txtNachname, txtVorname, 
				cbAllGroups, btnAddGroup, tblMemberGroups, btnDeleteGroup,
				cbAllSubjects, btnAddSubject, 
				tblMemberSubjects, btnDeleteSubject, btnSave);
		return layout;
	}	
	
	private boolean groupsContainItem(String search) {
		for (Group group : mGroups.getItemIds()) {
			if (group.getGruppenname().equals(search)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean subjectsContainItem(String search) {
		for (Subject subject : mSubjects.getItemIds()) {
			if (subject.getSpartenname().equals(search)) {
				return true;
			}
		}
		return false;
	}
		
	private void updateTable(Table table) {
		if (table.size() > MAX_PAGE_LENGTH) {
			table.setPageLength(MAX_PAGE_LENGTH);
		} else {
			table.setPageLength(table.size());
		}
		table.markAsDirtyRecursive();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
