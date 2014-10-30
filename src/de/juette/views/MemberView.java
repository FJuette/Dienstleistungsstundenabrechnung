package de.juette.views;

import java.util.Collection;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.MyGroupFilter;
import de.juette.dlsa.MySubjectFilter;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberView extends VerticalLayout implements View {

	protected final Table table = new Table();
	protected BeanItemContainer<Member> beans;
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);
	private BeanItemContainer<Group> mGroups;
	private BeanItemContainer<Category> mCategories;

	private VerticalLayout headerLayout = new VerticalLayout();
	private HorizontalSplitPanel contentSplitPanel = new HorizontalSplitPanel();
	private VerticalLayout contentLayout = new VerticalLayout();
	private HorizontalLayout contentHeaderLayout = new HorizontalLayout();
	private HorizontalLayout innerHeadLayout = new HorizontalLayout();
	private TabSheet contentTabs = new TabSheet();
	private FormLayout tabData = new FormLayout();
	private FormLayout tabGroups = new FormLayout();
	private FormLayout tabCategories = new FormLayout();
	private FormLayout tabDls = new FormLayout();
	private Label lblContentHeader = new Label("<strong>Mitglied: </strong>",
			ContentMode.HTML);
	private Button btnNew = new Button("Neu");

	private FieldGroup fieldGroup = new BeanFieldGroup<Member>(Member.class);

	private Handler actionHandler = new Handler() {
		private final Action DLS = new Action("DLS Buchen");
		private final Action GROUPS_ADD = new Action("Gruppe hinzufügen");
		private final Action CATEGORIES_ADD = new Action("Sparte hinzufügen");
		private final Action GROUPS_DEL = new Action("Gruppe löschen");
		private final Action CATEGORIES_DEL = new Action("Sparte löschen");
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { DLS, GROUPS_ADD,
				CATEGORIES_ADD, GROUPS_DEL, CATEGORIES_DEL, REMOVE };

		@SuppressWarnings("unchecked")
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Gruppe hinzufügen")) {
				if (table.getValue() != null) {
					// TODO: Layout
				}
			} else if (action.getCaption().equals("Sparte hinzufügen")) {
				if (table.getValue() != null) {
					// TODO: Layout
				}
			} else if (action.getCaption().equals("Gruppe löschen")) {
				if (table.getValue() != null) {
					// TODO: Layout
				}
			} else if (action.getCaption().equals("Sparte löschen")) {
				if (table.getValue() != null) {
					// TODO: Layout
				}
			} else if (action.getCaption().equals("Entfernen")) {
				for (Member bean : (Collection<Member>) table.getValue()) {
					try {
						beans.removeItem(bean);
						HibernateUtil.removeItem(Member.class, bean.getId()
								.toString());
					} catch (Exception e) {
						System.out
								.println("Dieses Mitglied kann nicht gelöscht werden.");
					}

				}
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

	@SuppressWarnings("unchecked")
	public MemberView() {
		initLayout();
		initTable();
		initTabs();

		groups.addAll((Collection<? extends Group>) HibernateUtil
				.getAllAsList(Group.class));
		categories.addAll((Collection<? extends Category>) HibernateUtil
				.getAllAsList(Category.class));
	}

	private void initLayout() {
		setSpacing(true);

		Label title = new Label("<strong>Mitgliederverwaltung</strong>",
				ContentMode.HTML);
		title.addStyleName("h3");
		title.addStyleName("myHeaderLabel");

		btnNew.setStyleName("primary tiny myAddButton");
		btnNew.setIcon(FontAwesome.PLUS);
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		innerButtonLayout.addComponent(btnNew);

		innerHeadLayout.addComponent(initFilter());
		innerHeadLayout.setWidth(100, Unit.PERCENTAGE);
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);

		headerLayout.setSpacing(true);
		headerLayout.addComponent(title);
		headerLayout.addComponent(innerHeadLayout);
		addComponent(headerLayout);

		contentSplitPanel.setSplitPosition(22, Unit.PERCENTAGE);
		contentSplitPanel.setSizeFull();
		contentSplitPanel.setLocked(true);
		contentSplitPanel.setFirstComponent(table);
		contentSplitPanel.setSecondComponent(contentLayout);
		addComponent(contentSplitPanel);

		contentHeaderLayout.setSpacing(true);
		lblContentHeader.setStyleName("myHeaderLabel");
		contentHeaderLayout.addComponent(lblContentHeader);
		contentLayout.addComponent(contentHeaderLayout);
		contentTabs.setSizeFull();
		contentLayout.addComponent(contentTabs);
	}

	@SuppressWarnings("unchecked")
	private void initTable() {
		beans = new BeanItemContainer<Member>(Member.class);
		beans.addAll((Collection<? extends Member>) HibernateUtil.orderedList(
				Member.class, "surname asc, forename asc, memberId asc"));

		table.setSizeFull();
		table.setContainerDataSource(beans);
		table.setSelectable(true);
		table.setImmediate(true);
		table.setPageLength(22);
		table.setMultiSelect(true);
		table.addActionHandler(getActionHandler());
		table.addStyleName("no-stripes");
		table.addGeneratedColumn("html", new ColumnGenerator() {
			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				String html = ((Member) itemId).getHtmlName();
				Label label = new Label(html, ContentMode.HTML);
				label.setSizeUndefined();
				return label;
			}
		});
		table.setVisibleColumns(new Object[] { "html" });
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

		table.addItemClickListener(event -> {
			setTabData((BeanItem<Member>) event.getItem());
		});

		table.select(table.firstItemId());
		table.focus();
		table.setValue(table.firstItemId());
		if (beans.size() > 0) {
			setTabData(beans.getItem(beans.getIdByIndex(0)));
		}
	}

	private void initTabs() {
		contentTabs.addTab(tabData, "Daten");
		contentTabs.addTab(tabGroups, "Gruppen");
		contentTabs.addTab(tabCategories, "Sparten");
		contentTabs.addTab(tabDls, "DLS-Statistik");

		contentTabs.setStyleName("framed equal-width-tabs padded-tabbar");

		contentTabs
				.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
					public void selectedTabChange(SelectedTabChangeEvent event) {
						// Find the tabsheet
						TabSheet tabsheet = event.getTabSheet();

						// Find the tab (here we know it's a layout)
						FormLayout tab = (FormLayout) tabsheet.getSelectedTab();

						// Get the tab caption from the tab object
						String caption = tabsheet.getTab(tab).getCaption();
						System.out.println(caption);
					}
				});
	}

	private HorizontalLayout initFilter() {
		ComboBox cbFilterGroup = new ComboBox();
		ComboBox cbFilterSubject = new ComboBox();

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);

		Label title = new Label("<strong>Filter:</strong>", ContentMode.HTML);
		title.addStyleName("myHeaderLabel");
		filterLayout.addComponent(title);

		TextField txtFilterName = new TextField();
		txtFilterName.setInputPrompt("Name");
		txtFilterName.setStyleName("tiny");
		filterLayout.addComponent(txtFilterName);

		cbFilterGroup.setContainerDataSource(groups);
		cbFilterGroup.setItemCaptionPropertyId("groupName");
		cbFilterGroup.setImmediate(true);
		cbFilterGroup.setStyleName("tiny");
		cbFilterGroup.setInputPrompt("Gruppe");
		filterLayout.addComponent(cbFilterGroup);

		cbFilterSubject.setContainerDataSource(categories);
		cbFilterSubject.setItemCaptionPropertyId("categoryName");
		cbFilterSubject.setImmediate(true);
		cbFilterSubject.setStyleName("tiny");
		cbFilterSubject.setInputPrompt("Sparte");
		filterLayout.addComponent(cbFilterSubject);

		cbFilterGroup.addValueChangeListener(event -> {
			beans.removeContainerFilters("groups");
			if (cbFilterGroup.getValue() != null
					&& !cbFilterGroup.getValue().equals("")) {
				beans.addContainerFilter(new MyGroupFilter("groups",
						(Group) cbFilterGroup.getValue()));
			}
		});

		cbFilterSubject.addValueChangeListener(event -> {
			beans.removeContainerFilters("categories");
			if (cbFilterSubject.getValue() != null
					&& !cbFilterSubject.getValue().equals("")) {
				beans.addContainerFilter(new MySubjectFilter("categories",
						(Category) cbFilterSubject.getValue()));
			}
		});

		txtFilterName.addTextChangeListener(event -> {
			filterTable("fullName", event.getText());
		});
		return filterLayout;
	}

	private void filterTable(Object columnId, String value) {
		beans.removeContainerFilters(columnId);
		beans.addContainerFilter(columnId, value, true, false);
	}

	private FormLayout MemberDataTab(BeanItem<Member> beanItem) {

		FormLayout layout = new FormLayout();
		layout.setSizeFull();
		layout.setStyleName("myFormLayout");

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

		layout.addComponents(txtForname, txtSurname, txtMemberId, dfEntryDate);

		DateField dfLeavingDate = new DateField("Austrittsdatum");
		fieldGroup.bind(dfLeavingDate, "leavingDate");
		layout.addComponent(dfLeavingDate);

		CheckBox cbActive = new CheckBox("Aktiv");
		fieldGroup.bind(cbActive, "active");
		layout.addComponent(cbActive);

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.saveAll(beans.getItemIds());
		});
		return layout;
	}

	protected FormLayout StatisticTab(BeanItem<Member> beanItem) {

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		layout.setStyleName("myFormLayout");

		// Must be set on the default value from the settings
		float dlsCount = -10;
		for (Booking b : HibernateUtil.getMembers(beanItem.getBean())) {
			dlsCount += b.getCountDls();
		}

		Label lblStatistic = new Label(
				"Aktueller Stand der Dienstleistungsstunden: " + dlsCount);
		layout.addComponent(lblStatistic);

		return layout;
	}

	// Categories and Groups
	private FormLayout MappingTab(BeanItem<Member> beanItem, String caption,
			String columnName) {

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		layout.setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);

		ComboBox cbAll = new ComboBox("Alle " + caption + ":");
		// Datenquelle abhängig von der Caption auf Gruppen oder Sparten setzen
		cbAll.setContainerDataSource(caption.equals("Gruppen") ? groups
				: categories);

		cbAll.setItemCaptionPropertyId(columnName);
		cbAll.setImmediate(true);
		cbAll.setWidth("350");

		layout.addComponent(cbAll);

		Button btnAdd = new Button(FontAwesome.PLUS);
		layout.addComponent(btnAdd);

		mGroups = new BeanItemContainer<Group>(Group.class);
		mGroups.addAll(((BeanItem<Member>) beanItem).getBean().getGroups());
		mCategories = new BeanItemContainer<Category>(Category.class);
		mCategories.addAll(((BeanItem<Member>) beanItem).getBean()
				.getCategories());

		Table tblMemberElements = new Table("Zugeordnete " + caption + ":");
		tblMemberElements
				.setContainerDataSource(caption.equals("Gruppen") ? mGroups
						: mCategories);
		tblMemberElements.setVisibleColumns(new Object[] { columnName });
		tblMemberElements.setColumnHeaders(caption);
		tblMemberElements.setWidth("350");
		tblMemberElements.setSelectable(true);
		setTableSize(tblMemberElements);

		layout.addComponent(tblMemberElements);

		btnAdd.addClickListener(event -> {
			// Compare the Strings because the Object ID is different even on
			// the same Object types
			if (caption.equals("Gruppen")) {
				if (cbAll.getValue() != null
						&& !mGroups.containsId(cbAll.getValue())) {
					mGroups.addItem(cbAll.getValue());
				}
			} else {
				if (cbAll.getValue() != null
						&& !mCategories.containsId(cbAll.getValue())) {
					mCategories.addItem(cbAll.getValue());
				}
			}
			setTableSize(tblMemberElements);
		});

		Button btnRemove = new Button(FontAwesome.MINUS);
		btnRemove.addClickListener(event -> {
			if (tblMemberElements.getValue() != null) {
				tblMemberElements.removeItem(tblMemberElements.getValue());
			}
			setTableSize(tblMemberElements);
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
					(beanItem.getBean()).setCategories((mCategories
							.getItemIds()));
				}
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.save(beanItem.getBean());
		});

		return layout;
	}

	private void setTabData(BeanItem<Member> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Mitglied: </strong> "
					+ beanItem.getBean().getFullName());
			tabData.removeAllComponents();
			tabData.addComponent(MemberDataTab(beanItem));

			tabGroups.removeAllComponents();
			tabGroups
					.addComponent(MappingTab(beanItem, "Gruppen", "groupName"));

			tabCategories.removeAllComponents();
			tabCategories.addComponent(MappingTab(beanItem, "Sparten",
					"categoryName"));

			tabDls.removeAllComponents();
			tabDls.addComponent(StatisticTab(beanItem));
		}
	}

	private void setTableSize(Table tbl) {
		if (tbl.size() > 15) {
			tbl.setPageLength(15);
		} else {
			tbl.setPageLength(tbl.size() + 1);
		}
		tbl.markAsDirtyRecursive();
	}

	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
