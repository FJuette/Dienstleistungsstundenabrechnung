package Views;

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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;
import de.juette.dlsa.FileHandler;
import de.juette.dlsa.MyGroupFilter;

@SuppressWarnings("serial")
public class MemberView extends EditableTable<Member> implements View {

	private FieldGroup fieldGroup;

	private Handler actionHandler = new Handler() {
		private final Action EDIT = new Action("Bearbeiten");
		private final Action GROUPS = new Action("Gruppen zuordnen");
		private final Action SUBJECS = new Action("Sparten zuordnen");
		private final Action REMOVE = new Action("Entfernen");
		private final Action MASS_CHANGE = new Action("DLS Buchen");
		private final Action[] ACTIONS = new Action[] { EDIT, GROUPS, SUBJECS,
				MASS_CHANGE, REMOVE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Bearbeiten")) {
				if (table.getValue() != null) {
					openMemberWindow(beans.getItem(table.getValue()),
							"Mitarbeiter bearbeiten");
				}
			} else if (action.getCaption().equals("Gruppen zuordnen")) {
				if (table.getValue() != null) {
					openMappingWindow(beans.getItem(table.getValue()),
							"Gruppen", "gruppenname");
				}
			} else if (action.getCaption().equals("Sparten zuordnen")) {
				if (table.getValue() != null) {
					openMappingWindow(beans.getItem(table.getValue()),
							"Sparten", "spartenname");
				}
			} else if (action.getCaption().equals("Entfernen")) {
				beans.removeItem(table.getValue());
				ComponentHelper.updateTable(table);
			} else if (action.getCaption().equals("DLS Buchen")) {
				Notification.show("Noch nicht implementiert.",
						Notification.Type.HUMANIZED_MESSAGE);
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}

	public MemberView() {
		beans = ComponentHelper.getDummyMembers();

		btnChange.setVisible(false);
		btnNew.setCaption("Neues Mitglied");

		filterLayout = initFilter();

		initLayout("Mitgliederverwaltung");
		initTable();
		extendTable();

		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload("", reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				Notification.show("Hier fehlt noch der Code...", Type.ERROR_MESSAGE);
			}
		});
		
		addComponent(upload);
		
		btnNew.addClickListener(event -> {
			openMemberWindow(new BeanItem<Member>(new Member("", "", "")),
					"Anlegen eines neuen Mitglieds");
		});
	}
	
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Datei erfolgreich hochgeladen",
				Notification.Type.TRAY_NOTIFICATION);
	}

	private HorizontalLayout initFilter() {
		ComboBox cbFilterGroup = new ComboBox("Filter nach Gruppe:");
		ComboBox cbFilterSubject = new ComboBox("Filter nach Sparte:");

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);

		TextField txtFilterName = new TextField("Filter nach Name:");
		filterLayout.addComponent(txtFilterName);

		cbFilterGroup.setContainerDataSource(ComponentHelper.getDummyGroups());
		cbFilterGroup.setItemCaptionPropertyId("gruppenname");
		cbFilterGroup.setImmediate(true);
		filterLayout.addComponent(cbFilterGroup);

		cbFilterSubject.setContainerDataSource(ComponentHelper
				.getDummySubjects());
		cbFilterSubject.setItemCaptionPropertyId("spartenname");
		cbFilterSubject.setImmediate(true);
		filterLayout.addComponent(cbFilterSubject);

		cbFilterGroup.addValueChangeListener(event -> {
			beans.removeContainerFilters("gruppen");
			if (cbFilterGroup.getValue() != null
					&& !cbFilterGroup.getValue().equals("")) {
				beans.addContainerFilter(new MyGroupFilter("gruppen",
						(Group) cbFilterGroup.getValue()));
			}
		});

		cbFilterSubject.addValueChangeListener(event -> {
			Notification.show("Noch nicht implementiert",
					Notification.Type.HUMANIZED_MESSAGE);
		});

		txtFilterName.addTextChangeListener(event -> {
			filterTable("nachname", event.getText());
		});
		return filterLayout;
	}

	private void filterTable(Object columnId, String value) {
		beans.removeAllContainerFilters();
		beans.addContainerFilter(columnId, value, true, false);

		ComponentHelper.updateTable(table);
	}
	
	@Override
	protected void extendTable() {
		table.removeAllActionHandlers();

		// table.setMultiSelect(true);
		// table.setMultiSelectMode(MultiSelectMode.DEFAULT);

		/*
		 * Andere Darstellungsmöglichkeit // override html column with a
		 * component, sorting as by the raw html // field
		 * table.addGeneratedColumn("html", new ColumnGenerator() { public
		 * Component generateCell(Table source, Object itemId, Object columnId)
		 * { String html = ((Member)itemId).getHtmlName(); Label label = new
		 * Label(html, ContentMode.HTML); label.setSizeUndefined(); return
		 * label; } }); table.setVisibleColumns(new Object[] { "html" });
		 */
		table.setVisibleColumns(new Object[] { "fullName", "mitgliedsnummer" });
		table.setColumnHeaders("Naame", "Mitgliedsnummer");
		table.addActionHandler(getActionHandler());
		table.setWidth("40%");
		table.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				openMemberWindow(event.getItem(), "Mitarbeiter bearbeiten");
			}
		});
	}

	protected void ImportWindow(String[] csvCols, String[] database) {
		Window window = new Window("Zuordnung der Spalten");
		window.setModal(true);
		window.setWidth("600");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		BeanItemContainer<String> db = new BeanItemContainer<String>(
				String.class);
		for (String col : database) {
			db.addItem(col);
		}
		BeanItemContainer<String> csv = new BeanItemContainer<String>(
				String.class);
		for (String col : csvCols) {
			csv.addItem(col);
		}

		HorizontalLayout headLayout = new HorizontalLayout();
		headLayout.setSpacing(true);
		layout.addComponent(headLayout);

		Label lblDbHead = new Label("Datenbankfelder");
		lblDbHead.setStyleName("h4");
		lblDbHead.setWidth("200");
		headLayout.addComponent(lblDbHead);

		Label lblCsvHead = new Label("CSV-Spalten");
		lblCsvHead.setStyleName("h4");
		lblCsvHead.setWidth("300");
		headLayout.addComponent(lblCsvHead);

		for (String col : database) {
			HorizontalLayout boxesLayout = new HorizontalLayout();
			boxesLayout.setSpacing(true);
			layout.addComponent(boxesLayout);

			Label lblDb = new Label(col);
			lblDb.setWidth("200");
			boxesLayout.addComponent(lblDb);

			ComboBox cbCsv = new ComboBox();
			cbCsv.setContainerDataSource(csv);
			cbCsv.setWidth("300");
			boxesLayout.addComponent(cbCsv);
		}

		Button btnSaveNewGroup = new Button("Speichern");
		btnSaveNewGroup.setStyleName("friendly");
		layout.addComponent(btnSaveNewGroup);

		btnSaveNewGroup.addClickListener(event -> {
			window.close();
		});

		getUI().addWindow(window);

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

		DateField dfEintrittsdatum = new DateField("Eintrittsdatum");
		fieldGroup.bind(dfEintrittsdatum, "eintrittsdatum");

		layout.addComponents(txtVorname, txtNachname, txtMitgliedsnummer,
				dfEintrittsdatum);

		if (caption.equals("Mitarbeiter bearbeiten")) {
			DateField dfAustrittsdatum = new DateField("Austrittsdatum");
			fieldGroup.bind(dfAustrittsdatum, "austrittsdatum");
			layout.addComponent(dfAustrittsdatum);

			CheckBox cbAktiv = new CheckBox("Aktiv");
			fieldGroup.bind(cbAktiv, "aktiv");
			layout.addComponent(cbAktiv);
		}

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Anlegen eines neuen Mitglieds")) {
					beans.addItem(new Member(txtNachname.getValue(), txtVorname
							.getValue(), txtMitgliedsnummer.getValue(),
							dfEintrittsdatum.getValue()));
				} else {
					beans.addItem((Member)fieldGroup.getItemDataSource());
				}
				ComponentHelper.updateTable(table);
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

		getUI().addWindow(window);
	}

	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Subject> mSubjects;

	private void openMappingWindow(BeanItem<Member> beanItem, String caption,
			String columnName) {
		Window window = new Window(caption + " zuordnen");
		window.setModal(true);
		window.setWidth("500");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);

		//
		BeanItemContainer<Group> groups = ComponentHelper.getDummyGroups();
		BeanItemContainer<Subject> subjects = ComponentHelper
				.getDummySubjects();

		ComboBox cbAll = new ComboBox("Alle " + caption + ":");
		// Datenquelle abhängig von der Caption auf Gruppen oder Sparten setzen
		cbAll.setContainerDataSource(caption.equals("Gruppen") ? groups
				: subjects);

		cbAll.setItemCaptionPropertyId(columnName);
		cbAll.setImmediate(true);
		cbAll.setWidth("100%");

		layout.addComponent(cbAll);

		Button btnAdd = new Button(FontAwesome.PLUS);
		layout.addComponent(btnAdd);

		mGroups = new BeanItemContainer<Group>(Group.class);
		mGroups.addAll(((BeanItem<Member>) beanItem).getBean().getGruppen());
		mSubjects = new BeanItemContainer<Subject>(Subject.class);
		mSubjects.addAll(((BeanItem<Member>) beanItem).getBean().getSparten());

		Table tblMemberElements = new Table("Zugeordnete " + caption + ":");
		tblMemberElements
				.setContainerDataSource(caption.equals("Gruppen") ? mGroups
						: mSubjects);
		tblMemberElements.setVisibleColumns(new Object[] { columnName });
		tblMemberElements.setColumnHeaders(caption);
		tblMemberElements.setWidth("100%");
		tblMemberElements.setSelectable(true);

		ComponentHelper.updateTable(tblMemberElements);
		layout.addComponent(tblMemberElements);

		btnAdd.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on
			// the same Object types
			if (caption.equals("Gruppen")) {
				if (cbAll.getValue() != null
						&& !groupsContainItem(((Group) cbAll.getValue())
								.getGruppenname())) {
					mGroups.addItem(cbAll.getValue());
					ComponentHelper.updateTable(tblMemberElements);
				}
			} else {
				if (cbAll.getValue() != null
						&& !subjectsContainItem(((Subject) cbAll.getValue())
								.getSpartenname())) {
					mSubjects.addItem(cbAll.getValue());
					ComponentHelper.updateTable(tblMemberElements);
				}
			}
		});

		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblMemberElements.getValue() != null) {
				tblMemberElements.removeItem(tblMemberElements.getValue());
				ComponentHelper.updateTable(tblMemberElements);
			}
		});

		layout.addComponent(btnRemove);

		Button btnSaveChanges = new Button("Speichern");
		btnSaveChanges.setStyleName("friendly");
		layout.addComponent(btnSaveChanges);

		btnSaveChanges.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Gruppen")) {
					(((BeanItem<Member>) beanItem).getBean())
							.setGruppen(mGroups.getItemIds());
				} else {
					(((BeanItem<Member>) beanItem).getBean())
							.setSparten((mSubjects.getItemIds()));
				}
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

	@Override
	public void enter(ViewChangeEvent event) {

	}

	@Override
	protected void newBeanWindow() {

	}

}
