package de.juette.views.windows;

import java.util.Iterator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.DataHandler;
import de.juette.model.ColumnMapping;
import de.juette.model.CsvColumn;
import de.juette.model.HibernateUtil;

public class NewColumnMappingWindow extends Window {

	private static final long serialVersionUID = 2441687195090369802L;
	private final VerticalLayout layoutMapping = new VerticalLayout();
	private BeanItemContainer<CsvColumn> csv;
	List<ColumnMapping> mapping;
	

	public NewColumnMappingWindow(List<CsvColumn> columns) {
		setModal(true);
		setWidth(600f, Unit.PIXELS);
		setCaption("Festlegen der Spaltenbeziehungen");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		layoutMapping.setSpacing(true);
		
		// All column names from the csv file
		csv = new BeanItemContainer<CsvColumn>(CsvColumn.class);
		for (CsvColumn column : columns) {
			csv.addBean(column);
		}
		
		// Delete all mapping entries in the database
		HibernateUtil.DeleteAll(ColumnMapping.class);
		mapping = DataHandler.getMappingEntrys();
		
		// Dynamically create all the fields
		createMappingLayout();
		layout.addComponent(layoutMapping);
	}
	
	private void createMappingLayout() {
		
		
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

			Label lblDb = new Label(m.getDisplayname());
			lblDb.setWidth("200");
			
			ComboBox cbCsv = new ComboBox();
			cbCsv.setWidth("300");
			cbCsv.setContainerDataSource(csv);
			cbCsv.setNullSelectionAllowed(false);
			cbCsv.setItemCaptionPropertyId("value");
			cbCsv.setValue(m.getCsvColumnName());
			if (m.getDisplayname().equals("Mitgliedsnummer")
					|| m.getDisplayname().equals("Vorname")
					|| m.getDisplayname().equals("Nachname")) {
				cbCsv.setRequired(true);
				cbCsv.setImmediate(true);
			}
			
			HorizontalLayout boxesLayout = new HorizontalLayout();
			boxesLayout.addComponents(lblDb, cbCsv);
			
			layoutMapping.addComponent(boxesLayout);
		}
		
		Button btnSaveMapping = new Button();
		btnSaveMapping.setStyleName("friendly");
		btnSaveMapping.setCaption("Speichern");
		layoutMapping.addComponent(btnSaveMapping);

		btnSaveMapping.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1724868827863258284L;

			@Override
			public void buttonClick(ClickEvent event) {
				validateAndWriteMappingsToDatabase();
				close();
			}
		});
	}

	private void validateAndWriteMappingsToDatabase() {
		String previous = "";
		Iterator<Component> iterAll = layoutMapping.iterator();

		while (iterAll.hasNext()) {
			Component c = iterAll.next();
			
			if ("com.vaadin.ui.HorizontalLayout".equals(c.getClass()
					.getTypeName())) {
				Iterator<Component> iterHor = ((HorizontalLayout) c)
						.iterator();

				while (iterHor.hasNext()) {
					Component p = iterHor.next();

					if ("com.vaadin.ui.Label"
							.equals(p.getClass().getTypeName())
							&& !"Datenbankfeld".equals(((Label) p).getValue())
							&& !"Spalte aus der Migliederliste"
									.equals(((Label) p).getValue())) {
						// put the value of the current column in a variable
						previous = ((Label) p).getValue();
					}

					if ("com.vaadin.ui.ComboBox".equals(p.getClass()
							.getTypeName())) {
						ComboBox cb = (ComboBox) p;
						
						if (cb.isRequired()
								&& (cb.getValue() == null || cb.getValue()
										.equals(""))) {
							Notification
									.show("Bitte alle Felder mit * mit einer Auswahl füllen.",
											Type.ERROR_MESSAGE);
							return;
						}
						
						if (((ComboBox) p).getValue() != null) {
							for (ColumnMapping m : mapping) {
								if (m.getDisplayname().equals(previous)) {
									m.setCsvColumnIndex(((CsvColumn) ((ComboBox) p)
											.getValue()).getIndex());
									m.setCsvColumnName(((CsvColumn) ((ComboBox) p)
											.getValue()).getValue());
								}
							}
						} // End If comboBox value
					} // End If comboBox class
				} // End Iterator label + comboBox
			} // End If HorizontalLayout
		} // End Iterator layoutMapping
		HibernateUtil.saveAll(mapping);
	} // End of function
}
