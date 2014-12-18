package de.juette.views;

import org.apache.shiro.SecurityUtils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.GeneralHandler;
import de.juette.dlsa.layout.ComplexLayout;
import de.juette.model.HibernateUtil;
import de.juette.model.Sector;
import de.juette.views.tabs.SectorDataTab;
import de.juette.views.tabs.SectorGroupTab;

public class SectorView extends ComplexLayout implements View {

	private static final long serialVersionUID = -850680557559439024L;
	protected BeanItemContainer<Sector> beans;
	private FormLayout tabData = new FormLayout();
	private FormLayout tabGroups = new FormLayout();
	
	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = 8102528189856905975L;
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen") && table.getValue() != null) {
					beans.removeItem(table.getValue());
					HibernateUtil.removeItem(Sector.class, ((Sector) table.getValue())
							.getId().toString());
			}
		}

		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}
	
	public SectorView() {
		if (SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
		btnNew.setCaption("Neuer Bereich");
		initLayout("<strong>Verwaltung der Bereiche</strong>");
		extendLayout();
		initTable();
		formatTable();
		initTabs();

		btnNew.addClickListener(event -> {
			newSectorWindow();
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
		beans = new BeanItemContainer<>(Sector.class);
		beans.addAll(HibernateUtil.getAllAsList(Sector.class));

		table.setContainerDataSource(beans);
		table.removeAllActionHandlers();
		table.addActionHandler(getActionHandler());
		table.addGeneratedColumn("html", new ColumnGenerator() {
			private static final long serialVersionUID = 7932554345700896822L;

			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				String html = ((Sector) itemId).getHtmlName();
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
	
	private void newSectorWindow() {
		Window window = new Window("Bereich anlegen");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		BeanFieldGroup<Sector> fieldGroup = new BeanFieldGroup<Sector>(Sector.class);
		fieldGroup.setItemDataSource(new Sector());

		TextField txtName = new TextField("Bereichsname");
		txtName.setWidth("100%");
		txtName.setNullRepresentation("");
		layout.addComponent(txtName);
		fieldGroup.bind(txtName, "sectorname");

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
		contentTabs.addTab(tabGroups, "Gruppen");
		contentTabs.setStyleName("framed equal-width-tabs padded-tabbar");
	}
	
	private void setTabData(BeanItem<Sector> beanItem) {
		if (beanItem != null) {
			lblContentHeader.setValue("<strong>Bereich: </strong> "
					+ beanItem.getBean().getSectorname());
			tabData.removeAllComponents();
			SectorDataTab userTab = new SectorDataTab(beanItem);
			tabData.addComponent(userTab);
			userTab.addDataSaveListener(event -> {
				beans.addBean((Sector) event.getBeanItem().getBean());
				table.refreshRowCache();
			});
			
			SectorGroupTab groupTab = new SectorGroupTab(beanItem);
			tabGroups.addComponent(groupTab);
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
