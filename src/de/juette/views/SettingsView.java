package de.juette.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import de.juette.dlsa.FileHandler;
import de.juette.dlsa.GeneralHandler;
import de.juette.model.ColumnMapping;
import de.juette.model.HibernateUtil;
import de.juette.model.Settings;
import de.juette.views.windows.NewColumnMappingWindow;

public class SettingsView extends VerticalLayout implements View {

	private static final long serialVersionUID = -8149901292659978760L;
	private final TextField txtDueDate = new TextField(
			"Stichtag (Enddatum eines Monats)");
	private final TextField txtCountDls = new TextField(
			"Anzahl der Dienstleistungsstunden pro Jahr");
	private final TextField txtCostDls = new TextField(
			"Kosten pro Dienstleistungsstunde");
	private final TextField txtDlsFromYear = new TextField(
			"Alter ab wann Dienstleistungsstunden geleistet werden müssen");
	private final TextField txtDlsToYear = new TextField(
			"Alter bis Dienstleistungsstunden geleistet werden müssen");
	private final OptionGroup groupKind = new OptionGroup();
	private final CheckBox cbClearing = new CheckBox(
			"Ausgleichungsbuchungen beim Jahreslauf");
	private final CheckBox cbTransfer = new CheckBox(
			"Übername von DLS beim Jahreswechsel");
	private final Button btnSave = new Button("Speichern");
	private VerticalLayout mappinglayout = new VerticalLayout();
	private final ComboBox cbGranularity = new ComboBox("Granularität");

	private VerticalLayout layoutMapping = new VerticalLayout();

	public SettingsView() {
		if (!SecurityUtils.getSubject().hasRole("Administrator")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
		BeanItem<Settings> beanItem = new BeanItem<Settings>(new Settings());
		List<Settings> settings = HibernateUtil.getAllAsList(Settings.class);
		for (Settings s : settings) {
			beanItem = new BeanItem<Settings>(s);
			break;
		}

		BeanFieldGroup<Settings> fieldGroup = new BeanFieldGroup<Settings>(
				Settings.class);
		fieldGroup.setItemDataSource(beanItem);

		setSpacing(true);
		setMargin(true);

		Label title = new Label("Grundeinstellungen");
		title.addStyleName("h1");
		addComponent(title);

		FormLayout form = new FormLayout();
		form.setMargin(false);
		form.setWidth("800px");
		addComponent(form);

		Label section = new Label("Berechnungsgrundlagen");
		section.addStyleName("h2 colored");
		form.addComponent(section);
		form.addComponent(txtDueDate);
		fieldGroup.bind(txtDueDate, "dueDate");

		txtCostDls.setWidth("15%");
		form.addComponent(txtCostDls);
		fieldGroup.bind(txtCostDls, "costDls");

		txtCountDls.setWidth("12%");
		form.addComponent(txtCountDls);
		fieldGroup.bind(txtCountDls, "countDls");

		txtDlsFromYear.setWidth("12%");
		form.addComponent(txtDlsFromYear);
		fieldGroup.bind(txtDlsFromYear, "ageFrom");

		txtDlsToYear.setWidth("12%");
		form.addComponent(txtDlsToYear);
		fieldGroup.bind(txtDlsToYear, "ageTo");

		section = new Label("Buchungsverhalten");
		section.addStyleName("h2 colored");
		form.addComponent(section);

		HorizontalLayout wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Berechnung beim Jahreslauf");
		groupKind.addItem("Volles Jahr zum Stichtag");
		groupKind.addItem("Anteilig bis zum Stichtag");
		groupKind.addStyleName("horizontal");
		wrap.addComponent(groupKind);
		form.addComponent(wrap);
		fieldGroup.bind(groupKind, "bookingMethod");

		wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Ausgleichsbuchungen");
		wrap.addComponent(cbClearing);
		form.addComponent(wrap);
		fieldGroup.bind(cbClearing, "clearing");

		wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Übernahme von DLS");
		wrap.addComponent(cbTransfer);
		form.addComponent(wrap);
		fieldGroup.bind(cbTransfer, "dlsTransfer");

		// cbGranularity
		cbGranularity.addItem("Keine");
		cbGranularity.addItem("Ganze");
		cbGranularity.addItem("Halbe");
		cbGranularity.addItem("Viertel");
		form.addComponent(cbGranularity);
		fieldGroup.bind(cbGranularity, "granularity");

		section = new Label("Spaltenzuordnung");
		section.addStyleName("h2 colored");
		addComponent(btnSave);

		form = new FormLayout();
		addComponent(form);
		form.addComponent(section);

		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(fieldGroup.getItemDataSource().getBean());
			} catch (Exception e) {
				e.printStackTrace();
			}

			Notification.show("Speichern erfolgreich.",
					Notification.Type.TRAY_NOTIFICATION);
		});

		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set receiver later
		Upload upload = new Upload(
				"Spaltenüberschriften aus der Mitgliederliste einlesen:",
				reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			private static final long serialVersionUID = -8528719905826739005L;

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				NewColumnMappingWindow w = new NewColumnMappingWindow(reciever.getColumnNames());
				w.addCloseListener(new CloseListener() {
					private static final long serialVersionUID = -3371096132762110457L;

					@Override
					public void windowClose(CloseEvent e) {
						Page.getCurrent().reload();
					}
				});
				reciever.getFile().delete();
				getUI().addWindow(w);
			}
		});

		form.addComponent(upload);
		
		createMappingLayout();
		mappinglayout.addComponent(layoutMapping);
		mappinglayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		addComponent(mappinglayout);
		
		form.setReadOnly(false);
	}

	private void createMappingLayout() {
		// Get the mapping list from the database
		ArrayList<ColumnMapping> mapping = (ArrayList<ColumnMapping>) HibernateUtil.orderedList(
				ColumnMapping.class, "id asc");

		layoutMapping.setSpacing(true);

		// Columnns header text
		HorizontalLayout headLayout = new HorizontalLayout();
		Label lblDbHead = new Label("Datenbankfeld");
		lblDbHead.setStyleName("h4");
		lblDbHead.setWidth("200");
		headLayout.addComponent(lblDbHead);

		Label lblCsvHead = new Label("Spalte aus der Migliederliste");
		lblCsvHead.setStyleName("h4");
		lblCsvHead.setWidth("300");
		headLayout.addComponent(lblCsvHead);
		
		layoutMapping.addComponent(headLayout);

		for (ColumnMapping m : mapping) {

			HorizontalLayout boxesLayout = new HorizontalLayout();
			Label lblDb = new Label(m.getDisplayname());
			lblDb.setWidth("200");
			boxesLayout.addComponent(lblDb);
			layoutMapping.addComponent(boxesLayout);
			Label lblCsv = new Label();
			lblCsv.setWidth("300");
			lblCsv.setValue(m.getCsvColumnName());
			boxesLayout.addComponent(lblCsv);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}
