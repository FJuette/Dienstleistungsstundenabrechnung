package de.juette.views;

import java.util.ArrayList;
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

import de.juette.dlsa.DataHandler;
import de.juette.dlsa.FileHandler;
import de.juette.model.ColumnMapping;
import de.juette.model.CsvColumn;
import de.juette.model.HibernateUtil;
import de.juette.model.Settings;

public class SettingsView extends VerticalLayout implements View {

	private static final long serialVersionUID = -8149901292659978760L;
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
	private VerticalLayout mappinglayout = new VerticalLayout();;
	private BeanItemContainer<CsvColumn> csv = new BeanItemContainer<CsvColumn>(
			CsvColumn.class);

	public SettingsView() {
		BeanItem<Settings> beanItem = new BeanItem<Settings>(new Settings());
		List<Settings> settings = HibernateUtil.getAllAsList(Settings.class);
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

		section = new Label("Spaltenzuordnung");
		section.addStyleName("h2 colored");
		addComponent(btnSave);
		
		form = new FormLayout();
		addComponent(form);
		form.addComponent(section);

		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {

			Notification.show("Speichern erfolgreich.",
					Notification.Type.TRAY_NOTIFICATION);
		});

		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload(
				"Spaltenüberschriften aus der Mitgliederliste einlesen:",
				reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			private static final long serialVersionUID = -8528719905826739005L;

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				for (CsvColumn item : reciever.getColumnNames()) {
					csv.addBean(item);
				}
				reciever.getFile().delete();
				mappinglayout.removeAllComponents();
				mappinglayout.addComponent(getColumnMappingLayout(true));
			}
		});

		form.addComponent(upload);

		mappinglayout.removeAllComponents();
		mappinglayout.addComponent(getColumnMappingLayout(false));
		mappinglayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		addComponent(mappinglayout);
		form.setReadOnly(false);

	}

	private ArrayList<ColumnMapping> mapping = new ArrayList<ColumnMapping>();
	private String previous = "";

	private VerticalLayout getColumnMappingLayout(Boolean newFile) {
		if (newFile == true) {
			HibernateUtil.DeleteAll(ColumnMapping.class);
			DataHandler.createMappingEntrys();
		}
		
		mapping = (ArrayList<ColumnMapping>) HibernateUtil.orderedList(ColumnMapping.class, "id asc");

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);

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
		
		if (newFile) {
			for (ColumnMapping m : mapping) {

				HorizontalLayout boxesLayout = new HorizontalLayout();
				Label lblDb = new Label(m.getDisplayname());
				lblDb.setWidth("200");
				boxesLayout.addComponent(lblDb);
				layout.addComponent(boxesLayout);
				ComboBox cbCsv = new ComboBox();
				cbCsv.setWidth("300");
				cbCsv.setContainerDataSource(csv);
				cbCsv.setNullSelectionAllowed(false);
				cbCsv.setItemCaptionPropertyId("value");
				cbCsv.setValue(m.getCsvColumnName());
				boxesLayout.addComponent(cbCsv);
			}
		} else {
			for (ColumnMapping m : mapping) {

				HorizontalLayout boxesLayout = new HorizontalLayout();
				Label lblDb = new Label(m.getDisplayname());
				lblDb.setWidth("200");
				boxesLayout.addComponent(lblDb);
				layout.addComponent(boxesLayout);
				Label lblCsv = new Label();
				lblCsv.setWidth("300");
				lblCsv.setValue(m.getCsvColumnName());
				boxesLayout.addComponent(lblCsv);
			}
		}
		
		Button btnSaveMapping = new Button();
		btnSaveMapping.setStyleName("friendly");
		btnSaveMapping.setCaption("Speichern");
		layout.addComponent(btnSaveMapping);
		
		btnSaveMapping.addClickListener(event -> {
			
			layout.forEach(c -> {
				if ("com.vaadin.ui.HorizontalLayout".equals(c.getClass()
						.getTypeName())) {
					((HorizontalLayout) c).forEach(p -> {
						if ("com.vaadin.ui.Label".equals(p.getClass()
								.getTypeName())
								&& !"Datenbankfeld".equals(((Label) p)
										.getValue())
								&& !"Spalte aus der Migliederliste"
										.equals(((Label) p).getValue())) {
							System.out.print(((Label) p).getValue() + ":");
							previous = ((Label) p).getValue();
						}
						if ("com.vaadin.ui.ComboBox".equals(p.getClass()
								.getTypeName())) {
							if (((ComboBox) p).getValue() != null) {
								for (ColumnMapping m : mapping) {
									if (m.getDisplayname().equals(previous)) {
										m.setCsvColumnIndex(((CsvColumn) ((ComboBox) p).getValue()).getIndex());
										m.setCsvColumnName(((CsvColumn) ((ComboBox) p).getValue()).getValue());
									}
								}
							}
						}
					});
				}
				HibernateUtil.saveAll(mapping);
			});

			Notification.show("Speichern erfolgreich",
					Notification.Type.TRAY_NOTIFICATION);
		});

		return layout;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}