package de.juette.views;

import java.util.List;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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

import de.juette.dlsa.FileHandler;
import de.juette.model.HibernateUtil;
import de.juette.model.Settings;

@SuppressWarnings("serial")
public class SettingsView extends VerticalLayout implements View {

	private final TextField txtDueDate = new TextField("Stichtag");
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
	private VerticalLayout mappinglayout;
	private BeanItemContainer<String> csv = new BeanItemContainer<String>(
			String.class);

	public SettingsView() {
		BeanItem<Settings> beanItem = new BeanItem<Settings>(new Settings());
		@SuppressWarnings("unchecked")
		List<Settings> settings = (List<Settings>) HibernateUtil
				.getAllAsList(Settings.class);
		for (Settings s : settings) {
			beanItem = new BeanItem<Settings>(s);
			break;
		}

		FieldGroup fieldGroup = new BeanFieldGroup<Settings>(Settings.class);
		fieldGroup.setItemDataSource(beanItem);

		setSpacing(true);
		setMargin(true);

		Label title = new Label("Grundeinstellungen");
		title.addStyleName("h1");
		addComponent(title);

		final FormLayout form = new FormLayout();
		form.setMargin(false);
		form.setWidth("800px");
		addComponent(form);

		Label section = new Label("Berechnungsgrundlagen");
		section.addStyleName("h2");
		section.addStyleName("colored");
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
		section.addStyleName("h2");
		section.addStyleName("colored");
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

		section = new Label("Spaltenzuordnung");
		section.addStyleName("h2");
		section.addStyleName("colored");
		form.addComponent(section);

		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload(
				"Spaltenüberschriften aus der Mitgliederliste einlesen:",
				reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				for (String item : reciever.getColumnNames()) {
					csv.addBean(item);
				}
				reciever.getFile().delete();
				// Notification.show("Hier fehlt noch der Code..." +
				// reciever.getFile().getAbsolutePath(), Type.ERROR_MESSAGE);
			}
		});

		form.addComponent(upload);

		mappinglayout = getColumnMappingLayout(new String[] { "Nachname",
				"Vorname", "Mitgliedsnummer", "Eintrittsdatum",
				"Austrittsdatum", "Aktiv" });
		mappinglayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		addComponent(mappinglayout);

		mappinglayout.forEach(c -> {
			((HorizontalLayout) c).forEach(p -> {
				// Demo of setting the Values
					if ("com.vaadin.ui.ComboBox".equals(p.getClass()
							.getTypeName())) {
						System.out.println(p.getClass().getTypeName());
					}
				});
		});

		addComponent(btnSave);

		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			Notification.show("Speichern erfolgreich.",
					Notification.Type.TRAY_NOTIFICATION);
		});

		form.setReadOnly(false);

	}

	private VerticalLayout getColumnMappingLayout(String[] database) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		BeanItemContainer<String> db = new BeanItemContainer<String>(
				String.class);
		for (String col : database) {
			db.addItem(col);
		}

		HorizontalLayout headLayout = new HorizontalLayout();
		layout.addComponent(headLayout);

		Label lblDbHead = new Label("Datenbankfeld");
		lblDbHead.setStyleName("h4");
		lblDbHead.setWidth("200");
		headLayout.addComponent(lblDbHead);

		Label lblCsvHead = new Label("Spalte aus der Migliederliste");
		lblCsvHead.setStyleName("h4");
		lblCsvHead.setWidth("300");
		headLayout.addComponent(lblCsvHead);

		for (String col : database) {
			HorizontalLayout boxesLayout = new HorizontalLayout();
			layout.addComponent(boxesLayout);

			Label lblDb = new Label(col);
			lblDb.setWidth("200");
			boxesLayout.addComponent(lblDb);

			ComboBox cbCsv = new ComboBox();
			cbCsv.setWidth("300");
			cbCsv.setContainerDataSource(csv);
			boxesLayout.addComponent(cbCsv);
		}
		return layout;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}
