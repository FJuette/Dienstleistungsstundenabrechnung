package de.juette.views;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
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

import de.juette.dlsa.MyBooleanFilter;
import de.juette.dlsa.MyDateRangeFilter;
import de.juette.dlsa.MyDateRangeValidator;
import de.juette.dlsa.MyGroupFilter;
import de.juette.dlsa.MyLeavingMemberDateFilter;
import de.juette.dlsa.MySubjectFilter;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.MemberColumn;
import de.juette.model.MemberInfo;
import de.juette.views.tabs.MemberDataTab;
import de.juette.views.tabs.MemberMappingTab;
import de.juette.views.tabs.MemberStatisticTab;
import de.juette.views.windows.MemberAPWindow;
import de.juette.views.windows.MemberImportWindow;
import de.juette.views.windows.MemberMappingWindow;
import de.juette.views.windows.MemberRefBookingWindow;
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
	private CheckBox cbPassive = new CheckBox();
	private DateField dfLostMembers = new DateField();

	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = -2704399406149413121L;
		private final Action DLS = new Action("DLS Buchen");
		private final Action GROUPS_ADD = new Action("Gruppe hinzufügen");
		private final Action CATEGORIES_ADD = new Action("Sparte hinzufügen");
		private final Action GROUPS_DEL = new Action("Gruppe löschen");
		private final Action CATEGORIES_DEL = new Action("Sparte löschen");
		private final Action ACTIVE_PASSIVE = new Action("Aktiv/Passiv buchen");
		private final Action REF_DATE_CHANGE = new Action("Änderung zu einem Bezugsdatum");
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { DLS, GROUPS_ADD,
				CATEGORIES_ADD, GROUPS_DEL, CATEGORIES_DEL, ACTIVE_PASSIVE, REF_DATE_CHANGE, REMOVE };
		
		@SuppressWarnings("unchecked")
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Änderung zu einem Bezugsdatum")) {
				MemberRefBookingWindow w = new MemberRefBookingWindow((Collection<Member>) table.getValue());
				getUI().addWindow(w);
			} else if (action.getCaption().equals("Gruppe hinzufügen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(
							w = new MemberMappingWindow("Gruppe", "groupName",
									"add"));
					w.addCloseListener(closeEvent -> {
						if (w.getGroup() != null) {
							Collection<Member> savedChanges = new ArrayList<Member>();
							for (Member member : (Collection<Member>) table
									.getValue()) {
								if (!member.getGroups().contains(w.getGroup())) {
									if (!savedChanges.contains(member)) {
										Collection<Group> oldValue = new ArrayList<Group>();
										oldValue.addAll(member.getGroups());
										beans.getItem(member).getBean().getGroups()
												.add(w.getGroup());
										PropertyChangeEvent e = new PropertyChangeEvent(sender, MemberColumn.GROUP.toString(), oldValue, member.getGroups());
										HibernateUtil.saveMemberChanges(member, e);
										savedChanges.add(member);
									}
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
							Collection<Member> savedChanges = new ArrayList<Member>();
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								if (bean.getGroups().contains(w.getGroup())) {
									if (!savedChanges.contains(bean)) {
										Collection<Group> oldValue = new ArrayList<Group>();
										oldValue.addAll(bean.getGroups());
										beans.getItem(bean).getBean().getGroups()
										.remove(w.getGroup());
										PropertyChangeEvent e = new PropertyChangeEvent(sender, MemberColumn.GROUP.toString(), oldValue, bean.getGroups());
										HibernateUtil.saveMemberChanges(bean, e);
										savedChanges.add(bean);
									}
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
							Collection<Member> savedChanges = new ArrayList<Member>();
							for (Member bean : (Collection<Member>) table
									.getValue()) {
								if (bean.getActive() != w.getChoice() && !savedChanges.contains(bean)) {
									PropertyChangeEvent e = new PropertyChangeEvent(sender, MemberColumn.ACTIVE.toString(), bean.getActive(), w.getChoice());
									HibernateUtil.saveMemberChanges(bean, e);
									savedChanges.add(bean);
									beans.getItem(bean).getBean()
									.setActive(w.getChoice());
								}
								
							}
							HibernateUtil.saveAll(beans.getItemIds());
							table.refreshRowCache();
						}
					});
				}
			} else if (action.getCaption().equals("Entfernen")) {
				for (Member bean : (Collection<Member>) table.getValue()) {
					try {
						bean.setAikz(false);
						HibernateUtil.save(bean);
						Page.getCurrent().reload();
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

		HorizontalLayout innerHeadFirstLineLayout = new HorizontalLayout();
		innerHeadFirstLineLayout.setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		btnImport.setStyleName("tiny myAddButton");
		btnImport.setIcon(FontAwesome.ARROW_UP);
		innerButtonLayout.addComponent(btnImport);
		innerButtonLayout.addComponent(btnNew);

		innerHeadLayout.addComponent(innerHeadFirstLineLayout);
		innerHeadFirstLineLayout.addComponent(initFilter());
		innerHeadFirstLineLayout.addComponent(innerButtonLayout);
		innerHeadFirstLineLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);
		
		Label lblShowPassive = new Label("<strong>Passive ausblenden: </strong>", ContentMode.HTML);
		lblShowPassive.setStyleName("myLabelStyle mySecondHeadLayoutFirst");
		
		cbPassive.setImmediate(true);
		cbPassive.setValue(true);
		cbPassive.setStyleName("mySecondHeadLayoutRest");
		leftContentLayout.addComponent(cbPassive);

		cbPassive.addValueChangeListener(event -> {
			filterActives();
		});
		
		Label lblLostMembers = new Label("<strong>Ausgetretene anzeigen ab:</strong>", ContentMode.HTML);
		lblLostMembers.setStyleName("myLabelStyle mySecondHeadLayoutRest");
		
		dfLostMembers.setImmediate(true);
		dfLostMembers.setStyleName("tiny mySecondHeadLayoutDF");
		dfLostMembers.setValue(DateTime.now().minusYears(1).toDate());
		leftContentLayout.addComponent(dfLostMembers);
		
		dfLostMembers.addValueChangeListener(event -> {
			filterLeavingDate();
		});
		HorizontalLayout innerHeadSecondLineLayout = new HorizontalLayout();
		innerHeadLayout.addComponent(innerHeadSecondLineLayout);
		innerHeadSecondLineLayout.setSpacing(true);
		innerHeadSecondLineLayout.addComponent(lblShowPassive);
		innerHeadSecondLineLayout.addComponent(cbPassive);
		innerHeadSecondLineLayout.addComponent(lblLostMembers);
		innerHeadSecondLineLayout.addComponent(dfLostMembers);
		

		btnNew.addClickListener(event -> {
			NewMemberWindow w;
			getUI().addWindow(w = new NewMemberWindow());
			w.addCloseListener(closeEvent -> {
				// Save the new member
				HibernateUtil.saveNewMember(w.getMember().getBean());
				/* TODO
				HibernateUtil.writeLogEntry(w.getMember().getBean(),
						"Mitglied neu angelegt", SecurityUtils.getSubject()
								.getPrincipal().toString(), DateTime.now().toDate());
								*/
				beans.addItem(w.getMember().getBean());
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
		beans.addAll(HibernateUtil.orderedWhereList(Member.class, "aikz = " + true,
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
		contentTabs.addTab(tabGroups, "Funktionsgruppen");
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
		txtBirthdateFrom.setInputPrompt("Geburtsdatum");
		txtBirthdateFrom.addValidator(new MyDateRangeValidator());
		txtBirthdateFrom.setDescription("Beispiel <br /> "
				+ "15.10.2004 - 31.11.2014 <br />"
				+ "<>= 01.01.2014");
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
			if (txtBirthdateFrom.getValue() != null && !txtBirthdateFrom.getValue().trim().equals("") && txtBirthdateFrom.isValid()) {
				beans.addContainerFilter(new MyDateRangeFilter("birthdate", txtBirthdateFrom.getValue()));
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

	private void setTabData(BeanItem<Member> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Mitglied: </strong> "
					+ beanItem.getBean().getFullName());
			tabData.removeAllComponents();

			MemberDataTab dataTab = new MemberDataTab(beanItem);
			tabData.addComponent(dataTab);
			dataTab.addDataSaveListener(event -> {
				Member m = (Member) event.getBeanItem().getBean();
				beans.addBean(m);
				MemberInfo mi = new MemberInfo(m);
				mi.printHistory();	
				System.out.println("Are they equal: " + mi.compareMembers(HibernateUtil.getBasicMember(m.getId())));
				table.refreshRowCache();
				lblContentHeader.setValue("<strong>Mitglied: </strong> "
						+ beanItem.getBean().getFullName());
				filterActives();
				filterLeavingDate();
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
