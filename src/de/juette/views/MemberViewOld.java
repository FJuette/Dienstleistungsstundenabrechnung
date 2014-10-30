package de.juette.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import de.juette.dlsa.FileHandler;
import de.juette.dlsa.MyGroupFilter;
import de.juette.dlsa.MySubjectFilter;
import de.juette.model.AbstractEntity;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.ColumnMapping;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberViewOld extends EditableTable<Member> implements View {

	private FieldGroup fieldGroup;
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);

	private Handler actionHandler = new Handler() {
		private final Action EDIT = new Action("Bearbeiten");
		private final Action GROUPS = new Action("Gruppen zuordnen");
		private final Action SUBJECS = new Action("Sparten zuordnen");
		private final Action REMOVE = new Action("Entfernen");
		private final Action MASS_CHANGE = new Action("DLS Buchen");
		private final Action STATISTIC = new Action("DLS-Statistik");
		private final Action[] ACTIONS = new Action[] { EDIT, GROUPS, SUBJECS, STATISTIC,
				MASS_CHANGE, REMOVE };

		@SuppressWarnings("unchecked")
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
							"Gruppen", "groupName");
				}
			} else if (action.getCaption().equals("Sparten zuordnen")) {
				if (table.getValue() != null) {
					openMappingWindow(beans.getItem(table.getValue()),
							"Sparten", "categoryName");
				}
			} else if (action.getCaption().equals("DLS-Statistik")) {
				openStatisticWindow(beans.getItem(((Collection<Member>) table.getValue()).toArray()[0]));
			} else if (action.getCaption().equals("Entfernen")) {
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(
						(Class<? extends AbstractEntity>) table.getValue()
								.getClass(),
						((AbstractEntity) table.getValue()).getId().toString());
				updateTable();
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

	protected void openStatisticWindow(BeanItem<Member> beanItem) {
		if (beanItem != null) {
			Window window = new Window("DLS-Statistik");
			window.setModal(true);
			window.setWidth("500");

			FormLayout layout = new FormLayout();
			layout.setMargin(true);
			window.setContent(layout);
			
			// Must be set on the default value from the settings
			float dlsCount = -10;
			for (Booking b : HibernateUtil.getMembers(beanItem.getBean())) {
				dlsCount += b.getCountDls();
			}
			
			Label lblStatistic = new Label("Aktueller Stand der Dienstleistungsstunden: " + dlsCount);
			layout.addComponent(lblStatistic);
			
			Button btnClose = new Button("Schliessen");
			layout.addComponent(btnClose);
			
			btnClose.addClickListener(event -> {
				window.close();
			});
			
			getUI().addWindow(window);
		} else {
			Notification.show("Bitte erst ein Mitglied auswählen", Type.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("unchecked")
	public MemberViewOld() {
		beans = new BeanItemContainer<Member>(Member.class);
		beans.addAll((Collection<? extends Member>) HibernateUtil
				.getAllAsList(Member.class));

		btnChange.setVisible(false);
		btnNew.setCaption("Neues Mitglied");

		filterLayout = initFilter();

		initLayout("Mitgliederverwaltung");
		initTable();
		extendTable();
		btnMarkAll.setVisible(true);

		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload("", reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				List<ColumnMapping> mapping = (List<ColumnMapping>) HibernateUtil.getAllAsList(ColumnMapping.class);

				reciever.uploadMembers(mapping);
			}
		});

		addComponent(upload);

		btnNew.addClickListener(event -> {
			openMemberWindow(new BeanItem<Member>(new Member()),
					"Anlegen eines neuen Mitglieds");
		});
		
		btnMarkAll.addClickListener(event -> {
			table.setValue(table.getItemIds());
		});

		groups.addAll((Collection<? extends Group>) HibernateUtil
				.getAllAsList(Group.class));
		categories.addAll((Collection<? extends Category>) HibernateUtil
				.getAllAsList(Category.class));
	}

	/*
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Datei erfolgreich hochgeladen",
				Notification.Type.TRAY_NOTIFICATION);
	}
	*/

	private HorizontalLayout initFilter() {
		ComboBox cbFilterGroup = new ComboBox("Filter nach Gruppe:");
		ComboBox cbFilterSubject = new ComboBox("Filter nach Sparte:");

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);

		TextField txtFilterName = new TextField("Filter nach Name:");
		filterLayout.addComponent(txtFilterName);

		cbFilterGroup.setContainerDataSource(groups);
		cbFilterGroup.setItemCaptionPropertyId("groupName");
		cbFilterGroup.setImmediate(true);
		filterLayout.addComponent(cbFilterGroup);

		cbFilterSubject.setContainerDataSource(categories);
		cbFilterSubject.setItemCaptionPropertyId("categoryName");
		cbFilterSubject.setImmediate(true);
		filterLayout.addComponent(cbFilterSubject);

		cbFilterGroup.addValueChangeListener(event -> {
			beans.removeContainerFilters("groups");
			if (cbFilterGroup.getValue() != null
					&& !cbFilterGroup.getValue().equals("")) {
				beans.addContainerFilter(new MyGroupFilter("groups",
						(Group) cbFilterGroup.getValue()));
			}
			updateTable();
		});

		cbFilterSubject.addValueChangeListener(event -> {
			beans.removeContainerFilters("categories");
			if (cbFilterSubject.getValue() != null
					&& !cbFilterSubject.getValue().equals("")) {
				beans.addContainerFilter(new MySubjectFilter("categories",
						(Category) cbFilterSubject.getValue()));
			}
			updateTable();
		});

		txtFilterName.addTextChangeListener(event -> {
			filterTable("surname", event.getText());
		});
		return filterLayout;
	}

	private void filterTable(Object columnId, String value) {
		beans.removeContainerFilters(columnId);
		beans.addContainerFilter(columnId, value, true, false);

		updateTable();
	}

	@Override
	protected void extendTable() {
		/*
		 * Andere Darstellungsmöglichkeit // override html column with a
		 * component, sorting as by the raw html // field
		 * table.addGeneratedColumn("html", new ColumnGenerator() { public
		 * Component generateCell(Table source, Object itemId, Object columnId)
		 * { String html = ((Member)itemId).getHtmlName(); Label label = new
		 * Label(html, ContentMode.HTML); label.setSizeUndefined(); return
		 * label; } }); table.setVisibleColumns(new Object[] { "html" });
		 */
		
		table.removeAllActionHandlers();
		//table.setMultiSelect(true);
		table.setVisibleColumns(new Object[] { "fullName", "memberId" });
		table.setColumnHeaders("Name", "Mitgliedsnummer");
		table.addActionHandler(getActionHandler());
		table.setWidth("40%");
		table.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				openMemberWindow(event.getItem(), "Mitarbeiter bearbeiten");
			}
		});
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

		TextField txtMemberId = new TextField("Mitgliedsnummer:");
		txtMemberId.setNullRepresentation("");
		fieldGroup.bind(txtMemberId, "memberId");

		TextField txtSurname = new TextField("Nachname:");
		txtSurname.setNullRepresentation("");
		fieldGroup.bind(txtSurname, "surname");

		TextField txtForname = new TextField("Vorname:");
		txtForname.setNullRepresentation("");
		fieldGroup.bind(txtForname, "forename");

		DateField dfEntryDate = new DateField("Eintrittsdatum");
		fieldGroup.bind(dfEntryDate, "entryDate");

		layout.addComponents(txtForname, txtSurname, txtMemberId,
				dfEntryDate);

		if (caption.equals("Mitarbeiter bearbeiten")) {
			DateField dfLeavingDate = new DateField("Austrittsdatum");
			fieldGroup.bind(dfLeavingDate, "leavingDate");
			layout.addComponent(dfLeavingDate);

			CheckBox cbActive = new CheckBox("Aktiv");
			fieldGroup.bind(cbActive, "active");
			layout.addComponent(cbActive);
		}

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
				if (caption.equals("Anlegen eines neuen Mitglieds")) {
					beans.addItem(new Member(txtSurname.getValue(), txtForname
							.getValue(), txtMemberId.getValue(),
							dfEntryDate.getValue()));
				}
				updateTable();
				window.close();
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.saveAll(beans.getItemIds());
		});

		getUI().addWindow(window);
	}

	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Category> mCategories;

	// Categories and Groups
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

		ComboBox cbAll = new ComboBox("Alle " + caption + ":");
		// Datenquelle abhängig von der Caption auf Gruppen oder Sparten setzen
		cbAll.setContainerDataSource(caption.equals("Gruppen") ? groups
				: categories);

		cbAll.setItemCaptionPropertyId(columnName);
		cbAll.setImmediate(true);
		cbAll.setWidth("100%");

		layout.addComponent(cbAll);

		Button btnAdd = new Button(FontAwesome.PLUS);
		layout.addComponent(btnAdd);

		mGroups = new BeanItemContainer<Group>(Group.class);
		mGroups.addAll(((BeanItem<Member>) beanItem).getBean().getGroups());
		mCategories = new BeanItemContainer<Category>(Category.class);
		mCategories.addAll(((BeanItem<Member>) beanItem).getBean().getCategories());

		Table tblMemberElements = new Table("Zugeordnete " + caption + ":");
		tblMemberElements
				.setContainerDataSource(caption.equals("Gruppen") ? mGroups
						: mCategories);
		tblMemberElements.setVisibleColumns(new Object[] { columnName });
		tblMemberElements.setColumnHeaders(caption);
		tblMemberElements.setWidth("100%");
		tblMemberElements.setSelectable(true);

		updateTable(tblMemberElements);
		layout.addComponent(tblMemberElements);

		btnAdd.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on
			// the same Object types
			if (caption.equals("Gruppen")) {
				if (cbAll.getValue() != null && !mGroups.containsId(cbAll.getValue())) {
					mGroups.addItem(cbAll.getValue());
					updateTable(tblMemberElements);
				}
			} else {
				if (cbAll.getValue() != null && !mCategories.containsId(cbAll.getValue())) {
					mCategories.addItem(cbAll.getValue());
					updateTable(tblMemberElements);
				}
			}
		});

		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblMemberElements.getValue() != null) {
				tblMemberElements.removeItem(tblMemberElements.getValue());
				updateTable(tblMemberElements);
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
					(beanItem.getBean()).setGroups(mGroups.getItemIds());
				} else {
					(beanItem.getBean()).setCategories((mCategories.getItemIds()));
				}
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});
		HibernateUtil.save(beanItem.getBean());

		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	@Override
	protected void newBeanWindow() {

	}
}
