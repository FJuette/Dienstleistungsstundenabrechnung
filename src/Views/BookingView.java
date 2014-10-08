package Views;

import java.util.Date;

import model.Activity;
import model.Booking;
import model.Member;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;
import de.juette.dlsa.DateToShortGermanStringConverter;

@SuppressWarnings("serial")
public class BookingView extends VerticalLayout implements View {

	private Handler actionHandler = new Handler() {
		private final Action STORBNO = new Action("Stornieren");
		private final Action[] ACTIONS = new Action[] { STORBNO };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Stornieren")) {
				bookings.addItem(new Booking(
						-((Booking)tblBookings.getValue()).getAnzahlDLS(), "Stornierung", 
						new Date(), ((Booking)tblBookings.getValue()).getMitglied(), 
						((Booking)tblBookings.getValue()).getAktion(), ((Booking)tblBookings.getValue()).getAbzeichner()));
				updateTable();
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};
	
	private final Table tblBookings = new Table();
	private final HorizontalLayout filterLayout = new HorizontalLayout();
	private final Button btnNewBookings = new Button("Neue Buchung(en)");

	private BeanItemContainer<Booking> bookings = new BeanItemContainer<>(
			Booking.class);

	public BookingView() {
		initLayout();
		initTable();
		initFilter();
	}

	private void initLayout() {
		setSpacing(true);
		setMargin(true);

		Label title = new Label("Journal");
		title.addStyleName("h1");
		addComponent(title);
		
		addComponent(btnNewBookings);
		btnNewBookings.addClickListener(event -> {
			newBookingsWindow();
		});

		Label lblFilter = new Label("Filter:");
		lblFilter.setStyleName("h3");
		addComponent(lblFilter);
		addComponent(filterLayout);
		addComponent(tblBookings);
	}

	private void initTable() {
		bookings = ComponentHelper.getDummyBookings();
		// Add the nested Property to the available columns
		bookings.addNestedContainerProperty("mitglied.mitgliedsnummer");

		tblBookings.setContainerDataSource(bookings);
		tblBookings.setSelectable(true);
		tblBookings.setImmediate(true);
		tblBookings.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblBookings.setVisibleColumns(new Object[] { "anzahlDLS",
				"ableistungsDatum", "bemerkung", "mitglied.mitgliedsnummer" });
		tblBookings.setColumnHeaders("Anzahl DLS", "Ableistungsdatum",
				"Bemerkung", "Mitgliedsnummer");
		tblBookings.setConverter("ableistungsDatum",
				new DateToShortGermanStringConverter());
		tblBookings.setWidth("80%");
		tblBookings.addActionHandler(getActionHandler());

		updateTable();
	}

	private void initFilter() {
		filterLayout.setSpacing(true);
		TextField txtFilterDls = new TextField("Anzahl DLS:");
		txtFilterDls.setConverter(Integer.class);
		txtFilterDls.setConversionError("Die Anzahl der Dienstleistungsstunden muss eine Zahl sein.");
		txtFilterDls.addTextChangeListener(event -> {
			filterTable("anzahlDLS", event.getText());
		});
		
		TextField txtFilterDate = new TextField("Datum:");
		txtFilterDate.setConverter(Date.class);
		
		TextField txtFilterNote = new TextField("Bemerkung:");
		txtFilterNote.setConverter(String.class);
		txtFilterNote.addTextChangeListener(event -> {
			filterTable("bemerkung", event.getText());
		});
		filterLayout.addComponents(txtFilterDls, txtFilterDate, txtFilterNote);
	}
	
	private void filterTable(Object columnId, String value) {
		bookings.removeAllContainerFilters();
		bookings.addContainerFilter(columnId, value, true, false);
		
		updateTable();
	}
	
	private void newBookingsWindow() {
		Window window = new Window("Anlegen einer neuen Buchung");
		window.setModal(true);
		window.setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		DateField dfDate = new DateField("Ableistungsdatum");
		dfDate.setWidth("100%");
		layout.addComponent(dfDate);
		
		ComboBox cbActivities = new ComboBox("Aktion");
		cbActivities.setWidth("100%");
		cbActivities.setImmediate(true);
		cbActivities.setContainerDataSource(ComponentHelper.getDummyActivities());
		cbActivities.setItemCaptionPropertyId("beschreibung");
		cbActivities.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbActivities);

		TextField txtCountDls = new TextField("Anzahl DLS");
		txtCountDls.setWidth("100%");
		layout.addComponent(txtCountDls);	
		
		ComboBox cbMembers = new ComboBox("Mitglied");
		cbMembers.setWidth("100%");
		cbMembers.setImmediate(true);
		cbMembers.setContainerDataSource(ComponentHelper.getDummyMembers());
		cbMembers.setItemCaptionPropertyId("fullName");
		cbMembers.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbMembers);
		
		TextField txtContent = new TextField("Bemerkung");
		txtContent.setWidth("100%");
		layout.addComponent(txtContent);
		
		Button btnSaveNewGroup = new Button("Speichern");
		layout.addComponent(btnSaveNewGroup);
		
		btnSaveNewGroup.addClickListener(event -> {
			bookings.addItem(new Booking(
					Integer.parseInt(txtCountDls.getValue()), 
					txtContent.getValue(), 
					dfDate.getValue(), 
					(Member)cbMembers.getValue(), 
					(Activity)cbActivities.getValue(), 
					(Member)cbMembers.getValue()));
			updateTable();
			
			txtContent.setValue("");
			cbMembers.setValue(null);
			cbMembers.focus();
		});
		
		getUI().addWindow(window);
	}

	private void updateTable() {
		if (tblBookings.size() > 15) {
			tblBookings.setPageLength(15);
		} else {
			tblBookings.setPageLength(tblBookings.size());
		}
		tblBookings.markAsDirtyRecursive();
	}
	
	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
