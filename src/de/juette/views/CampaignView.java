package de.juette.views;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.views.tabs.CampaignDataTab;

public class CampaignView extends ComplexLayout implements View {

	private static final long serialVersionUID = -1397474522844910652L;
	protected BeanItemContainer<Campaign> beans;
	private FormLayout tabData = new FormLayout();

	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = 8102528189856905975L;
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(Campaign.class,
						((Campaign) table.getValue()).getId().toString());
			}
		}

		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}

	public CampaignView() {
		btnNew.setCaption("Neue Aktion");
		initLayout("<strong>Aktionsverwaltung</strong>");
		extendLayout();
		initTable();
		formatTable();
		initTabs();

		btnNew.addClickListener(event -> {
			newCampaignWindow();
		});
	}

	private void extendLayout() {
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();
		innerButtonLayout.addComponent(btnNew);
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);
	}

	private void initTable() {
		beans = new BeanItemContainer<Campaign>(Campaign.class);
		beans.addAll(HibernateUtil.orderedList(Campaign.class, "year asc, description asc"));

		table.setContainerDataSource(beans);
		table.removeAllActionHandlers();
		table.addActionHandler(getActionHandler());
		table.addGeneratedColumn("html", new ColumnGenerator() {
			private static final long serialVersionUID = 7932554345700896822L;

			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				String html = ((Campaign) itemId).getHtml();
				Label label = new Label(html, ContentMode.HTML);
				label.setSizeUndefined();
				return label;
			}
		});
		table.setVisibleColumns(new Object[] { "html" });
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.addItemClickListener(event -> {
			setTabData(beans.getItem(event.getItemId()));
		});
		table.select(table.firstItemId());
		table.focus();
		table.setValue(table.firstItemId());
		if (beans.size() > 0) {
			setTabData(beans.getItem(beans.getIdByIndex(0)));
		}
	}

	protected void newCampaignWindow() {
		Window window = new Window("Anlegen einer neuen Aktion");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		BeanFieldGroup<Campaign> fieldGroup = new BeanFieldGroup<Campaign>(Campaign.class);
		fieldGroup.setItemDataSource(new Campaign());

		TextField txtYear = new TextField("Jahr");
		txtYear.setWidth("100%");
		txtYear.setNullRepresentation("");
		fieldGroup.bind(txtYear, "year");
		layout.addComponent(txtYear);

		TextField txtDescription = new TextField("Beschreibung");
		txtDescription.setWidth("100%");
		txtDescription.setNullRepresentation("");
		fieldGroup.bind(txtDescription, "description");
		layout.addComponent(txtDescription);

		BeanItemContainer<Member> members = new BeanItemContainer<Member>(
				Member.class);
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		ComboBox cbContact = new ComboBox("Ansprechpartner");
		cbContact.setContainerDataSource(members);
		cbContact.setItemCaptionPropertyId("fullName");
		cbContact.setFilteringMode(FilteringMode.CONTAINS);
		cbContact.setImmediate(true);
		fieldGroup.bind(cbContact, "contact");
		layout.addComponent(cbContact);

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		layout.addComponent(btnSave);

		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(fieldGroup.getItemDataSource().getBean());
				beans.addItem(fieldGroup.getItemDataSource().getBean());
				window.close();
			} catch (Exception e) {
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			
		});

		getUI().addWindow(window);
	}

	private void initTabs() {
		contentTabs.addTab(tabData, "Daten");
		contentTabs.setStyleName("framed equal-width-tabs padded-tabbar");
	}

	private void setTabData(BeanItem<Campaign> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Aktion: </strong> "
					+ beanItem.getBean().getDescription());
			tabData.removeAllComponents();
			CampaignDataTab userTab = new CampaignDataTab(beanItem);
			tabData.addComponent(userTab);
			userTab.addDataSaveListener(event -> {
				beans.addBean((Campaign) event.getBeanItem().getBean());
				table.refreshRowCache();
			});
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
}
