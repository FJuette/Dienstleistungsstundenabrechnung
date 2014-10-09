package Views;

import java.util.Collection;

import model.Group;
import model.Member;
import model.Subject;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;



import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

import de.juette.dlsa.BooleanToGermanConverter;
import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class MemberView extends VerticalLayout implements View {

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
	
	private Button btnImport = new Button("Mitglieder aus CSV-Datei einlesen");
	private Button btnNewMember = new Button("Neues Mitglied");
	
	public MemberView() {
		initLayout();
		initTable();
	}

	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Mitgliederverwaltung");
		title.addStyleName("h1");
		addComponent(title);
				
		addComponent(tblMembers);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponents(btnImport, btnNewMember);
		
		btnImport.addClickListener(event -> {
			UI.getCurrent().addWindow(new CsvImportWindow());
		});
		
		btnNewMember.addClickListener(event -> {
			openMemberWindow(new BeanItem<Member>(new Member()), "Anlegen eines neuen Mitglieds");
		});
	}

	private void initTable() {
		members = ComponentHelper.getDummyMembers();
		
		tblMembers.setContainerDataSource(members);
		tblMembers.setSelectable(true);
		tblMembers.setImmediate(true);
		tblMembers.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblMembers.setVisibleColumns( new Object[] {"mitgliedsnummer", "nachname", "vorname"} );
		tblMembers.setColumnHeaders("Mitgliedsnummer", "Nachname", "Vorname");
		tblMembers.setWidth("80%");
		tblMembers.addItemClickListener(event -> {
			if(event.isDoubleClick()) {
				openMemberWindow(event.getItem(), "Mitarbeiter bearbeiten");
			}
		});
		
		updateTable(tblMembers);
		
	}
	
	private void openMemberWindow(Item beanItem, String caption) {
		Window window = new Window(caption);
		window.setModal(true);
		window.setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);
		
		layout.addComponents(txtVorname, txtNachname, txtMitgliedsnummer);
		
		Button btnSaveNewMember = new Button("Speichern");
		layout.addComponent(btnSaveNewMember);
		
		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
				members.addItem(fieldGroup.getItemDataSource());
				updateTable(tblMembers);
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});
		
		getUI().addWindow(window);
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
		if (table.size() > 15) {
			table.setPageLength(15);
		} else {
			table.setPageLength(table.size() + 1);
		}
		table.markAsDirtyRecursive();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
