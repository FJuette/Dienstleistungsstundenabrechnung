package Views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import model.Group;
import model.Member;
import model.Subject;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class MemberView extends VerticalLayout implements View {

	private final Table tblMembers = new Table();

	private FieldGroup fieldGroup;
	private BeanItemContainer<Member> members = new BeanItemContainer<Member>(
			Member.class);

	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Subject> mSubjects;

	private Button btnNewMember = new Button("Neues Mitglied");

	private ComboBox cbFilterGroup = new ComboBox("Filter nach Gruppe:");
	private ComboBox cbFilterSubject = new ComboBox("Filter nach Sparte:");

	private Handler actionHandler = new Handler() {
		private final Action EDIT = new Action("Bearbeiten");
		private final Action GROUPS = new Action("Gruppen zuordnen");
		private final Action SUBJECS = new Action("Sparten zuordnen");
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { EDIT, GROUPS, SUBJECS,
				REMOVE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Bearbeiten")) {
				if (tblMembers.getValue() != null) {
					openMemberWindow(members.getItem(tblMembers.getValue()),
							"Mitarbeiter bearbeiten");
				}
			} else if (action.getCaption().equals("Gruppen zuordnen")) {
				if (tblMembers.getValue() != null) {
					openGroupWindow(members.getItem(tblMembers.getValue()));
				}
			} else if (action.getCaption().equals("Sparten zuordnen")) {
				if (tblMembers.getValue() != null) {
					openSubjectWindow(members.getItem(tblMembers.getValue()));
				}
			} else if (action.getCaption().equals("Entfernen")) {
				members.removeItem(tblMembers.getValue());
				ComponentHelper.updateTable(tblMembers);
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

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

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);

		cbFilterGroup.setContainerDataSource(ComponentHelper.getDummyGroups());
		cbFilterGroup.setItemCaptionPropertyId("gruppenname");
		cbFilterGroup.setImmediate(true);
		filterLayout.addComponent(cbFilterGroup);

		cbFilterSubject.setContainerDataSource(ComponentHelper
				.getDummySubjects());
		cbFilterSubject.setItemCaptionPropertyId("spartenname");
		cbFilterSubject.setImmediate(true);
		filterLayout.addComponent(cbFilterSubject);

		addComponent(filterLayout);
		addComponent(tblMembers);

		VerticalLayout bottomLeftLayout = new VerticalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);

		FileUploader reciever = new FileUploader();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload("", reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(reciever);

		bottomLeftLayout.addComponents(btnNewMember, upload);

		btnNewMember.addClickListener(event -> {
			openMemberWindow(new BeanItem<Member>(new Member("", "", "")),
					"Anlegen eines neuen Mitglieds");
		});

		cbFilterGroup.addValueChangeListener(event -> {
			Notification.show("Noch nicht implementiert",
					Notification.Type.HUMANIZED_MESSAGE);
		});

		cbFilterSubject.addValueChangeListener(event -> {
			Notification.show("Noch nicht implementiert",
					Notification.Type.HUMANIZED_MESSAGE);
		});
	}

	class FileUploader implements Receiver, SucceededListener {

		public File file;

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			// Create upload stream
			FileOutputStream fos = null; // Stream to write to
			try {
				// Open the file for writing
				System.out.println(VaadinService.getCurrent()
						.getBaseDirectory().getAbsolutePath()
						+ "/WEB-INF/Files/" + filename);
				file = new File(VaadinService.getCurrent().getBaseDirectory()
						.getAbsolutePath()
						+ "/WEB-INF/Files/" + filename);
				fos = new FileOutputStream(file);
			} catch (final FileNotFoundException ex) {
				new Notification("Datei nicht gefunden:<br/>", ex.getMessage(),
						Notification.Type.ERROR_MESSAGE);
				return null;
			} catch (final Exception ex) {
				new Notification("Fehler:<br/>", ex.getMessage(),
						Notification.Type.ERROR_MESSAGE);
				return null;
			}
			return fos;
		}

		public void uploadSucceeded(SucceededEvent e) {
			/*
			 * Für jede Spalte links aus der Klasse/DB eine Combobox mit jeweils
			 * allen Feldern Rechte Combobox mit allen Spalten aus der CSV
			 */
			Notification.show(file.getName(),
					Notification.Type.TRAY_NOTIFICATION);
			for (String col : getColumnNames(VaadinService.getCurrent()
					.getBaseDirectory().getAbsolutePath()
					+ "/WEB-INF/Files/" + file.getName())) {
				System.out.println(col);
			}
		}
	}

	// http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	private String[] getColumnNames(String file) {
		// Make a class for handling CSV files
		// Path as property
		// Content as Object?
		BufferedReader br = null;
		String cvsSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(file));
			return br.readLine().split(cvsSplitBy);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private void initTable() {
		members = ComponentHelper.getDummyMembers();

		tblMembers.setContainerDataSource(members);
		tblMembers.setSelectable(true);
		tblMembers.setImmediate(true);
		tblMembers.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblMembers.setVisibleColumns(new Object[] { "mitgliedsnummer",
				"nachname", "vorname" });
		tblMembers.setColumnHeaders("Mitgliedsnummer", "Nachname", "Vorname");
		tblMembers.setWidth("80%");
		tblMembers.addActionHandler(getActionHandler());
		tblMembers.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				openMemberWindow(event.getItem(), "Mitarbeiter bearbeiten");
			}
		});

		ComponentHelper.updateTable(tblMembers);

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

		TextField txtMitgliedsnummer = new TextField("Mitgliedsnummer:");
		fieldGroup.bind(txtMitgliedsnummer, "mitgliedsnummer");

		TextField txtNachname = new TextField("Nachname:");
		fieldGroup.bind(txtNachname, "nachname");

		TextField txtVorname = new TextField("Vorname:");
		fieldGroup.bind(txtVorname, "vorname");

		layout.addComponents(txtVorname, txtNachname, txtMitgliedsnummer);

		Button btnSaveNewMember = new Button("Speichern");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Anlegen eines neuen Mitglieds")) {
					members.addItem(new Member(txtNachname.getValue(),
							txtVorname.getValue(), txtMitgliedsnummer
									.getValue()));
				} else {
					members.addItem((BeanItem<Member>) fieldGroup
							.getItemDataSource());
				}
				ComponentHelper.updateTable(tblMembers);
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

		getUI().addWindow(window);
	}

	private void openGroupWindow(Item beanItem) {
		Window window = new Window("Gruppen zuordnen");
		window.setModal(true);
		window.setWidth("500");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);

		BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
				Group.class);
		groups = ComponentHelper.getDummyGroups();

		ComboBox cbAllGroups = new ComboBox("Alle Gruppen:");
		cbAllGroups.setContainerDataSource(groups);
		cbAllGroups.setItemCaptionPropertyId("gruppenname");
		cbAllGroups.setImmediate(true);
		cbAllGroups.setWidth("100%");

		layout.addComponent(cbAllGroups);

		Button btnAddGroup = new Button(FontAwesome.PLUS);
		layout.addComponent(btnAddGroup);

		mGroups = new BeanItemContainer<Group>(Group.class);
		mGroups.addAll(((BeanItem<Member>) beanItem).getBean().getGruppen());

		Table tblMemberGroups = new Table("Zugeordnete Gruppen:");
		tblMemberGroups.setContainerDataSource(mGroups);
		tblMemberGroups.setVisibleColumns(new Object[] { "gruppenname" });
		tblMemberGroups.setColumnHeaders("Gruppen");
		tblMemberGroups.setWidth("100%");
		tblMemberGroups.setSelectable(true);

		ComponentHelper.updateTable(tblMemberGroups);
		layout.addComponent(tblMemberGroups);

		btnAddGroup.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on
			// the same Object types
				if (cbAllGroups.getValue() != null
						&& !groupsContainItem(((Group) cbAllGroups.getValue())
								.getGruppenname())) {
					mGroups.addItem(cbAllGroups.getValue());
					ComponentHelper.updateTable(tblMemberGroups);
				}
			});

		Button btnDeleteGroup = new Button(FontAwesome.MINUS);
		btnDeleteGroup.addClickListener(event -> {
			if (tblMemberGroups.getValue() != null) {
				tblMemberGroups.removeItem(tblMemberGroups.getValue());
				ComponentHelper.updateTable(tblMemberGroups);
			}
		});

		layout.addComponent(btnDeleteGroup);

		Button btnSaveChanges = new Button("Speichern");
		layout.addComponent(btnSaveChanges);

		btnSaveChanges.addClickListener(event -> {
			try {
				fieldGroup.commit();
				(((BeanItem<Member>) beanItem).getBean()).setGruppen(mGroups
						.getItemIds());
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

		getUI().addWindow(window);
	}

	private void openSubjectWindow(Item beanItem) {
		Window window = new Window("Sparten zuordnen");
		window.setModal(true);
		window.setWidth("500");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);

		BeanItemContainer<Subject> subjects = new BeanItemContainer<Subject>(
				Subject.class);
		subjects = ComponentHelper.getDummySubjects();

		ComboBox cbAllSubjects = new ComboBox("Alle Sparten:");
		cbAllSubjects.setContainerDataSource(subjects);
		cbAllSubjects.setItemCaptionPropertyId("spartenname");
		cbAllSubjects.setImmediate(true);
		cbAllSubjects.setWidth("100%");

		layout.addComponent(cbAllSubjects);

		Button btnAddSubject = new Button(FontAwesome.PLUS);
		layout.addComponent(btnAddSubject);

		mSubjects = new BeanItemContainer<Subject>(Subject.class);
		mSubjects.addAll(((BeanItem<Member>) beanItem).getBean().getSparten());

		Table tblMemberSubjects = new Table("Zugeordnete Sparten:");
		tblMemberSubjects.setContainerDataSource(mSubjects);
		tblMemberSubjects.setVisibleColumns(new Object[] { "spartenname" });
		tblMemberSubjects.setColumnHeaders("Sparten");
		tblMemberSubjects.setWidth("100%");
		tblMemberSubjects.setSelectable(true);

		ComponentHelper.updateTable(tblMemberSubjects);
		layout.addComponent(tblMemberSubjects);

		btnAddSubject
				.addClickListener(event -> {
					if (cbAllSubjects.getValue() != null
							&& !subjectsContainItem(((Subject) cbAllSubjects
									.getValue()).getSpartenname())) {
						mSubjects.addItem(cbAllSubjects.getValue());
						ComponentHelper.updateTable(tblMemberSubjects);
					}
				});

		Button btnDeleteSubject = new Button(FontAwesome.MINUS);
		btnDeleteSubject.addClickListener(event -> {
			if (tblMemberSubjects.getValue() != null) {
				tblMemberSubjects.removeItem(tblMemberSubjects.getValue());
				ComponentHelper.updateTable(tblMemberSubjects);
			}
		});

		layout.addComponent(btnDeleteSubject);

		Button btnSaveChanges = new Button("Speichern");
		layout.addComponent(btnSaveChanges);

		btnSaveChanges.addClickListener(event -> {
			try {
				fieldGroup.commit();
				(((BeanItem<Member>) beanItem).getBean()).setSparten((mSubjects
						.getItemIds()));
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

	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
