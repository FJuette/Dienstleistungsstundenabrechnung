package Views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



import model.Activity;
import model.Booking;
import model.Member;



import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
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
				ComponentHelper.updateTable(tblBookings);
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
	private final Button btnYear = new Button("Jahreslauf durchführen");

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
		btnNewBookings.setIcon(FontAwesome.PLUS);
		btnNewBookings.addClickListener(event -> {
			newBookingsWindow();
		});

		Label lblFilter = new Label("Filter:");
		lblFilter.setStyleName("h3");
		addComponent(lblFilter);
		addComponent(filterLayout);
		addComponent(tblBookings);
		addComponent(btnYear);
		
		btnYear.setStyleName("primary");
		btnYear.addClickListener(event -> {
			YearWindow();
		});
		
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
		tblBookings.setWidth("100%");
		tblBookings.addActionHandler(getActionHandler());
		tblBookings.setColumnExpandRatio("anzahlDLS", (float) 0.1);
		tblBookings.setColumnExpandRatio("ableistungsDatum", (float) 0.2);
		tblBookings.setColumnExpandRatio("bemerkung", (float) 0.5);
		tblBookings.setColumnExpandRatio("mitglied.mitgliedsnummer", (float) 0.2);

		ComponentHelper.updateTable(tblBookings);
	}

	private void initFilter() {
		filterLayout.setSpacing(true);
		TextField txtFilterDls = new TextField("Anzahl DLS:");
		txtFilterDls.setConverter(Integer.class);
		txtFilterDls.setConversionError("Die Anzahl der Dienstleistungsstunden muss eine Zahl sein.");
		txtFilterDls.setNullRepresentation("");
		txtFilterDls.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("anzahlDLS", event.getText());
			} else {
				bookings.removeContainerFilters("anzahlDLS");
			}
			
		});
		
		DateField dfFilterDate = new DateField("Datum:");
		dfFilterDate.addValueChangeListener(event -> {
			if (((DateField)event.getProperty()).getValue() != null) {
				filterTable("ableistungsDatum", ((DateField)event.getProperty()).getValue().toString());
			} else {
				bookings.removeContainerFilters("ableistungsDatum");
			}
			
		});
		
		TextField txtFilterNote = new TextField("Bemerkung:");
		txtFilterNote.setConverter(String.class);
		txtFilterNote.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("bemerkung", event.getText());
			} else {
				bookings.removeContainerFilters("bemerkung");
			}
			
		});
		filterLayout.addComponents(txtFilterDls, dfFilterDate, txtFilterNote);
	}
	
	private void filterTable(Object columnId, String value) {
		//bookings.removeAllContainerFilters();
		bookings.addContainerFilter(columnId, value, true, false);
		
		ComponentHelper.updateTable(tblBookings);
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
		
		TextArea txtContent = new TextArea("Bemerkung");
		txtContent.setWidth("100%");
		layout.addComponent(txtContent);
		
		Button btnSaveNewBooking = new Button("Speichern");
		btnSaveNewBooking.setStyleName("friendly");
		layout.addComponent(btnSaveNewBooking);
		
		btnSaveNewBooking.addClickListener(event -> {
			bookings.addItem(new Booking(
					Integer.parseInt(txtCountDls.getValue()), 
					txtContent.getValue(), 
					dfDate.getValue(), 
					(Member)cbMembers.getValue(), 
					(Activity)cbActivities.getValue(), 
					(Member)cbMembers.getValue()));
			ComponentHelper.updateTable(tblBookings);
			
			txtContent.setValue("");
			cbMembers.setValue(null);
			cbMembers.focus();
		});
		
		getUI().addWindow(window);
	}
	
	private void YearWindow() {
		Window window = new Window("Bestätigung");
		window.setModal(true);
		window.setWidth("500");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		Label lblQuestion = new Label("Soll der Jahreslauf jetzt durchgeführt werden?<br /> "
				+ "Ein erneuter Durchlauf für dieses Jahr ist dann <strong>nicht</strong> mehr möglich.");
		lblQuestion.setStyleName("h4");
		lblQuestion.setContentMode(ContentMode.HTML);
		lblQuestion.setStyleName("center");
		layout.addComponent(lblQuestion);
		
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setStyleName("center");
		layout.addComponent(btnLayout);
		
		Button btnYes = new Button("Ja");
		btnLayout.addComponent(btnYes);
		
		Button btnNo = new Button("Nein");
		btnLayout.addComponent(btnNo);
	
		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
		Resource res = new FileResource(new File(basepath
				+ "/WEB-INF/Files/ExampleResult.csv"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(btnYes);
		
		btnYes.addClickListener(evnet -> {
			window.close();
		});
			
		btnNo.addClickListener(event -> {
			window.close();
		});
		
		getUI().addWindow(window);
	}
	
	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
