package de.juette.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.data.util.filter.And;

import de.juette.dlsa.MyBooleanFilter;
import de.juette.dlsa.MyGroupFilter;
import de.juette.dlsa.MyLeavingMemberDateFilter;
import de.juette.dlsa.MySubjectFilter;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.views.tabs.MemberDataTab;
import de.juette.views.tabs.MemberMappingTab;
import de.juette.views.tabs.MemberStatisticTab;
import de.juette.views.windows.MemberAPWindow;
import de.juette.views.windows.MemberImportWindow;
import de.juette.views.windows.MemberMappingWindow;
import de.juette.views.windows.NewBookingWindow;
import de.juette.views.windows.NewMemberWindow;

public class MemberView extends ComplexLayout implements View {

	private static final long serialVersionUID = 3274586292369413485L;
	protected BeanItemContainer<Member> beans;
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	private BeanItemContainer<Category> categories = new BeanItemContainer<Category>(
			Category.class);

	private FormLayout tabData = new FormLayout();
	private FormLayout tabGroups = new FormLayout();
	private FormLayout tabCategories = new FormLayout();
	private FormLayout tabDls = new FormLayout();
	private Button btnImport = new Button("Importieren");
	private CheckBox cbPassive = new CheckBox("Passive ausblenden");
	private DateField dfLostMembers = new DateField("Ausgetretene anzeigen ab:");

	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = -2704399406149413121L;
		private final Action DLS = new Action("DLS Buchen");
		private final Action GROUPS_ADD = new Action("Gruppe hinzufügen");
		private final Action CATEGORIES_ADD = new Action("Sparte hinzufügen");
		private final Action GROUPS_DEL = new Action("Gruppe löschen");
		private final Action CATEGORIES_DEL = new Action("Sparte löschen");
		private final Action ACTIVE_PASSIVE = new Action("Aktiv/Passiv buchen");
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { DLS, GROUPS_ADD,
				CATEGORIES_ADD, GROUPS_DEL, CATEGORIES_DEL, ACTIVE_PASSIVE, REMOVE };

		@SuppressWarnings("unchecked")
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Gruppe hinzufügen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(
							w = new MemberMappingWindow("Gruppe", "groupName",
									"add"));
					w.addCloseListener(closeEvent -> {
						if (w.getGroup() != null) {
							for (Member member : (Collection<Member>) table
									.getValue()) {
								if (!member.getGroups().contains(w.getGroup())) {
									beans.getItem(member).getBean().getGroups()
											.add(w.getGroup());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Sparte hinzufügen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(
							w = new MemberMappingWindow("Sparte",
									"categoryName", "add"));
					w.addCloseListener(closeEvent -> {
						if (w.getCategory() != null) {
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								if (!bean.getCategories().contains(
										w.getCategory())) {
									beans.getItem(bean).getBean()
											.getCategories()
											.add(w.getCategory());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Gruppe löschen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(
							w = new MemberMappingWindow("Gruppe", "groupName",
									"remove"));
					w.addCloseListener(closeEvent -> {
						if (w.getGroup() != null) {
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								if (bean.getGroups().contains(w.getGroup())) {
									beans.getItem(bean).getBean().getGroups()
											.remove(w.getGroup());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Sparte löschen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(
							w = new MemberMappingWindow("Sparte",
									"categoryName", "remove"));
					w.addCloseListener(closeEvent -> {
						if (w.getCategory() != null) {
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								if (bean.getCategories().contains(
										w.getCategory())) {
									beans.getItem(bean).getBean()
											.getCategories()
											.remove(w.getCategory());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			}  else if (action.getCaption().equals("Aktiv/Passiv buchen")) {
				if (table.getValue() != null) {
					MemberAPWindow w;
					getUI().addWindow(
							w = new MemberAPWindow());
					w.addCloseListener(closeEvent -> {
						if (w.getChoice() != null) {
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								beans.getItem(bean).getBean()
										.setActive(w.getChoice());
							}
							HibernateUtil.saveAll(beans.getItemIds());
							table.refreshRowCache();
						}
					});
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
				NewBookingWindow w;
				getUI().addWindow(w = new NewBookingWindow(false));
				w.addCloseListener(closeEvent -> {
					if (w.getBooking() != null) {
						for (Member bean : (Collection<Member>) table
								.getValue()) {
							Booking b = new Booking();
							b.setCampaign(w.getBooking().getCampaign());
							b.setComment(w.getBooking().getComment());
							b.setCountDls(w.getBooking().getCountDls());
							b.setDoneDate(w.getBooking().getDoneDate());
							b.setMember(bean);
							HibernateUtil.save(b);
						}
						HibernateUtil.saveAll(beans.getItemIds());
					}
				});
			}
			table.refreshRowCache();
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	public MemberView() {
		initLayout("<strong>Mitgliederverwaltung</strong>");
		extendLayout();
		initTable();
		formatTable();
		initTabs();

		groups.addAll(HibernateUtil.getAllAsList(Group.class));
		categories.addAll(HibernateUtil.getAllAsList(Category.class));
	}

	private void extendLayout() {

		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		btnImport.setStyleName("tiny myAddButton");
		btnImport.setIcon(FontAwesome.ARROW_UP);
		innerButtonLayout.addComponent(btnImport);
		innerButtonLayout.addComponent(btnNew);

		innerHeadLayout.addComponent(initFilter());
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);

		btnNew.addClickListener(event -> {
			NewMemberWindow w;
			getUI().addWindow(w = new NewMemberWindow());
			w.addCloseListener(closeEvent -> {
				if (w.getMember().getBean().getSurname() != null) {
					HibernateUtil.save(w.getMember().getBean());
					beans.addItem(w.getMember().getBean());
				}
			});
		});

		btnImport.addClickListener(event -> {
			MemberImportWindow w;
			getUI().addWindow(w = new MemberImportWindow());
			w.addCloseListener(closeEvent -> {
				getUI().getPage().reload();
				Notification.show("Upload fertig", Type.TRAY_NOTIFICATION);
			});
		});

		cbPassive.setImmediate(true);
		cbPassive.setStyleName("tiny myHeaderLabel");
		cbPassive.setValue(true);
		leftContentLayout.addComponent(cbPassive);

		cbPassive.addValueChangeListener(event -> {
			filterActives();
		});
		
		dfLostMembers.setImmediate(true);
		dfLostMembers.setStyleName("tiny myHeaderLabel");
		dfLostMembers.setValue(DateTime.now().minusYears(1).toDate());
		leftContentLayout.addComponent(dfLostMembers);
		
		dfLostMembers.addValueChangeListener(event -> {
			filterLeavingDate();
		});
	}

	private void filterLeavingDate() {
		beans.removeContainerFilters("leavingDate");
		if (dfLostMembers.getValue() != null) {
			beans.addContainerFilter(new MyLeavingMemberDateFilter("leavingDate", dfLostMembers.getValue()));
		}
	}

	private void filterActives() {
		if (cbPassive.getValue()) {
			beans.addContainerFilter(new MyBooleanFilter("active"));
		} else {
			beans.removeContainerFilters("active");
		}
	}

	private void initTable() {
		beans = new BeanItemContainer<Member>(Member.class);
		beans.addAll(HibernateUtil.orderedList(Member.class,
				"surname asc, forename asc, memberId asc"));
		filterActives();
		filterLeavingDate();

		table.setContainerDataSource(beans);
		table.setMultiSelect(true);
		table.addActionHandler(getActionHandler());
		table.addGeneratedColumn("html", new ColumnGenerator() {

			private static final long serialVersionUID = -7474540528395636510L;

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
		table.addHeaderClickListener(event -> {
			table.setValue(table.getItemIds());
		});

		table.addItemClickListener(event -> {
			setTabData(beans.getItem(event.getItemId()));
		});

		table.select(table.firstItemId());
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
	}

	private HorizontalLayout initFilter() {
		ComboBox cbFilterGroup = new ComboBox();
		ComboBox cbFilterSubject = new ComboBox();
		TextField txtBirthdateFrom = new TextField();

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

		txtBirthdateFrom.setImmediate(true);
		txtBirthdateFrom.setStyleName("tiny");
		txtBirthdateFrom.setWidth(250, Unit.PIXELS);
		txtBirthdateFrom.setInputPrompt("Geburtsdatum, 15.10.2004-31.11.2014");
		txtBirthdateFrom.setDescription("Beispiel: 15.10.2004 - 31.11.2014");
		filterLayout.addComponent(txtBirthdateFrom);

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

		txtBirthdateFrom.addBlurListener(event -> {
			beans.removeContainerFilters("birthdate");
			if (!"".equals(txtBirthdateFrom.getValue())
					&& txtBirthdateFrom.getValue().contains("-")) {
				String[] parts = txtBirthdateFrom.getValue().split("-");
				if (parseToDate(parts[0]) != null
						&& parseToDate(parts[1]) != null) {
					Date from = parseToDate(parts[0]);
					Date to = parseToDate(parts[1]);
					beans.addContainerFilter(new And(
							new Compare.GreaterOrEqual("birthdate", from),
							new Compare.LessOrEqual("birthdate", to)));
				}
			}
		});

		txtFilterName.addTextChangeListener(event -> {
			filterTable("fullName", event.getText());
		});
		return filterLayout;
	}

	private Date parseToDate(String s) {
		try {
			Date d = new SimpleDateFormat("dd.MM.yyyy").parse(s.trim());
			return d;
		} catch (ParseException e) {
			return null;
		}
	}

	private void filterTable(Object columnId, String value) {
		beans.removeContainerFilters(columnId);
		beans.addContainerFilter(columnId, value, true, false);
	}

	private void setTabData(BeanItem<Member> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Mitglied: </strong> "
					+ beanItem.getBean().getFullName());
			tabData.removeAllComponents();

			MemberDataTab dataTab = new MemberDataTab(beanItem);
			tabData.addComponent(dataTab);
			dataTab.addDataSaveListener(event -> {
				beans.addBean((Member) event.getBeanItem().getBean());
				table.refreshRowCache();
				filterActives();
			});

			tabGroups.removeAllComponents();
			tabGroups.addComponent(new MemberMappingTab(beanItem, "Gruppen",
					"groupName"));

			tabCategories.removeAllComponents();
			tabCategories.addComponent(new MemberMappingTab(beanItem,
					"Sparten", "categoryName"));

			tabDls.removeAllComponents();
			tabDls.addComponent(new MemberStatisticTab(beanItem));
		}
	}

	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
