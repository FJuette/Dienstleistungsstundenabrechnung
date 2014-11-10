package de.juette.views;

import java.util.Collection;

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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;

import de.juette.dlsa.MyGroupFilter;
import de.juette.dlsa.MySubjectFilter;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberView extends ComplexLayout implements View {

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
					MemberMappingWindow w;
					getUI().addWindow(w = new MemberMappingWindow("Gruppe", "groupName", "add"));
					w.addCloseListener(closeEvent -> {
						if (w.getGroup() != null) {
							for (Member bean : (Collection<Member>) table.getValue()) {
								if (!bean.getGroups().contains(w.getGroup())) {
									beans.getItem(bean).getBean().getGroups().add(w.getGroup());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Sparte hinzufügen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(w = new MemberMappingWindow("Sparte", "categoryName", "add"));
					w.addCloseListener(closeEvent -> {
						if (w.getCategory() != null) {
							for (Member bean : (Collection<Member>) table.getValue()) {
								if (!bean.getCategories().contains(w.getCategory())) {
									beans.getItem(bean).getBean().getCategories().add(w.getCategory());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Gruppe löschen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(w = new MemberMappingWindow("Gruppe", "groupName", "remove"));
					w.addCloseListener(closeEvent -> {
						if (w.getGroup() != null) {
							for (Member bean : (Collection<Member>) table.getValue()) {
								if (bean.getGroups().contains(w.getGroup())) {
									beans.getItem(bean).getBean().getGroups().remove(w.getGroup());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
						}
					});
				}
			} else if (action.getCaption().equals("Sparte löschen")) {
				if (table.getValue() != null) {
					MemberMappingWindow w;
					getUI().addWindow(w = new MemberMappingWindow("Sparte", "categoryName", "remove"));
					w.addCloseListener(closeEvent -> {
						if (w.getCategory() != null) {
							for (Member bean : (Collection<Member>) table.getValue()) {
								if (bean.getCategories().contains(w.getCategory())) {
									beans.getItem(bean).getBean().getCategories().remove(w.getCategory());
								}
							}
							HibernateUtil.saveAll(beans.getItemIds());
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
						for (Member bean : (Collection<Member>) table.getValue()) {
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
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	@SuppressWarnings("unchecked")
	public MemberView() {
		initLayout("<strong>Mitgliederverwaltung</strong>");
		extendLayout();
		initTable();
		formatTable();
		initTabs();

		groups.addAll((Collection<? extends Group>) HibernateUtil
				.getAllAsList(Group.class));
		categories.addAll((Collection<? extends Category>) HibernateUtil
				.getAllAsList(Category.class));
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
	}

	@SuppressWarnings("unchecked")
	private void initTable() {
		beans = new BeanItemContainer<Member>(Member.class);
		beans.addAll((Collection<? extends Member>) HibernateUtil.orderedList(
				Member.class, "surname asc, forename asc, memberId asc"));

		table.setContainerDataSource(beans);
		table.setMultiSelect(true);
		table.addActionHandler(getActionHandler());
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
		//table.setColumnHeaders(new String[] { "" });
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.addHeaderClickListener(event -> {
			table.setValue(table.getItemIds());
		});

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
		txtBirthdateFrom.setInputPrompt("ab Geburtsdatum");
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
				beans.addBean(event.getBeanItem().getBean());
				table.refreshRowCache();
			});

			tabGroups.removeAllComponents();
			tabGroups
					.addComponent(new MemberMappingTab(beanItem, "Gruppen", "groupName"));

			tabCategories.removeAllComponents();
			tabCategories.addComponent(new MemberMappingTab(beanItem, "Sparten",
					"categoryName"));

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
