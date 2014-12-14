package de.juette.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.DateToShortGermanStringConverter;
import de.juette.dlsa.FileHandler;
import de.juette.dlsa.GeneralHandler;
import de.juette.dlsa.MyDateRangeFilter;
import de.juette.dlsa.MyDateRangeValidator;
import de.juette.dlsa.NoCOYAvailableException;
import de.juette.model.AbstractEntity;
import de.juette.model.Booking;
import de.juette.model.CourseOfYearWorker;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Year;
import de.juette.views.windows.NewBookingWindow;

public class BookingView extends EditableTable<Booking> implements View {

	private static final long serialVersionUID = 6324354416931646341L;

	private HorizontalLayout innerHeadLayout = new HorizontalLayout();
	private ComboBox cbYears = new ComboBox();
	private Button btnYear = new Button("Jahreslauf durchführen");
	private Button btnYearTest = new Button("Vorläufigen Jahreslauf durchführen");

	private Handler actionHandler = new Handler() {

		private static final long serialVersionUID = 8008706058827363288L;
		private final Action STORNO = new Action("Stornieren");
		private final Action[] ACTIONS = new Action[] { STORNO };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Stornieren")) {
				if (!((Booking) table.getValue()).getCanceled()) {
					Booking b = new Booking();
					b.setDoneDate(((Booking) table.getValue()).getDoneDate());
					b.setCampaign(((Booking) table.getValue()).getCampaign());
					b.setCountDls(-((Booking) table.getValue()).getCountDls());
					b.setComment("Stornierung von "
							+ ((Booking) table.getValue()).getComment());
					b.setMember(((Booking) table.getValue()).getMember());

					((Booking) table.getValue()).setCanceled(true);

					beans.addItem(b);
					updateTable();
					HibernateUtil
							.saveAll((List<? extends AbstractEntity>) beans
									.getItemIds());
				} else {
					Notification.show(
							"Das Stornieren der Buchung ist leider nicht möglich, "
									+ "da diese bereits Storniert wurde.",
							Type.ERROR_MESSAGE);
				}
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	public BookingView() {
		if (SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
		beans = new BeanItemContainer<Booking>(Booking.class);
		beans.addAll(HibernateUtil.getAllAsList(Booking.class));
		beans.addNestedContainerProperty("member.memberId");

		btnChange.setVisible(false);
		btnNew.setVisible(false);

		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSizeUndefined();

		Button btnNewBookings = new Button("Neue Buchung(en)");
		btnNewBookings.setStyleName("primary tiny");
		btnNewBookings.setIcon(FontAwesome.PLUS);
		innerButtonLayout.addComponent(btnNewBookings);

		initTable();
		extendTable();
		HorizontalLayout fLayout = initFilter();

		innerHeadLayout.addComponent(fLayout);
		innerHeadLayout.setWidth(90, Unit.PERCENTAGE);
		innerHeadLayout.setExpandRatio(fLayout, (float) 0.8);
		innerHeadLayout.addComponent(innerButtonLayout);
		innerHeadLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);

		filterLayout = innerHeadLayout;

		initLayout("Journal");

		btnYear.setStyleName("danger tiny");
		btnYearTest.setStyleName("primary tiny");

		HorizontalLayout btnsYear = new HorizontalLayout();
		btnsYear.addComponent(cbYears);
		btnsYear.setSpacing(true);
		btnsYear.addComponent(btnYearTest);
		btnsYear.addComponent(btnYear);
		addComponent(btnsYear);

		btnYear.addClickListener(event -> {
			DateTime lastCoyDate = null;
			try {
				lastCoyDate = new DateTime(HibernateUtil.getLastCOYDate());
			} catch (NoCOYAvailableException e) {
				
			}
			CourseOfYearWorker worker;
			if (lastCoyDate != null) {
				worker = new CourseOfYearWorker((Year)cbYears.getValue(), HibernateUtil.getSettings(), lastCoyDate.toDate());
				
			} else {
				worker = new CourseOfYearWorker((Year)cbYears.getValue(), HibernateUtil.getSettings());
			}
			if (DateTime.now().isAfter(worker.getToDate())) {
				YearWindow(worker);
				// Do the COY....
			} else {
				Notification.show("Der Jahreslauf kann erst gestartet werden, wenn der Zeitraum beendet ist.", Type.ERROR_MESSAGE);
			}
		});
		
		btnYearTest.addClickListener(event -> {
			DateTime lastCoyDate = null;
			try {
				lastCoyDate = new DateTime(HibernateUtil.getLastCOYDate());
			} catch (NoCOYAvailableException e) {
				
			}
			CourseOfYearWorker worker;
			if (lastCoyDate != null) {
				worker = new CourseOfYearWorker((Year)cbYears.getValue(), HibernateUtil.getSettings(), lastCoyDate.toDate());
				
			} else {
				worker = new CourseOfYearWorker((Year)cbYears.getValue(), HibernateUtil.getSettings());
			}
			// Old code but still needed for later use
			//CourseOfYearWorker worker = new CourseOfYearWorker((Year) cbYears.getValue(), new Settings());
			//CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2014), new Settings());
			FileResource res = new FileResource(runCourseOfYear(worker, false));
			setResource("download", res);
			ResourceReference rr = ResourceReference.create(res, this, "download");
			Page.getCurrent().open(rr.getURL(), null);
		});

		btnNewBookings.addClickListener(event -> {
			NewBookingWindow w;
			getUI().addWindow(w = new NewBookingWindow(true));
			w.addCloseListener(closeEvent -> {
				if (w.getBookings().size() > 0) {
					beans.addAll(w.getBookings());
				}
			});
		});
	}
	
	private File runCourseOfYear(CourseOfYearWorker worker, Boolean finalize) {
		List<String> lines = new ArrayList<String>();
		lines.add("Vorname;Nachname;Geleistete DLS;Benötigte DLS;Kosten Pro nicht geleisteter DLS;Zu Zahlen in Euro;Bemerkung");
		for (Member member : HibernateUtil.getAllAsList(Member.class)) {
			worker.setMember(member);
			int fullMonth = worker.getFullDlsMonth();
			Double debit = worker.getMemberDebit(HibernateUtil.getBookingsFromYear(member, worker.getFromDate().toDate(), worker.getToDate().toDate()), fullMonth);
			String line = member.getForename() + ";" + 
						  member.getSurname() + ";" + 
						  worker.getAchievedDls().toString().replace(".", ",") + ";" +
						  ((Double)worker.getRequiredDls(fullMonth)).toString().replace(".", ",") + ";" +
						  ((Double)worker.getSettings().getCostDls()).toString().replace(".", ",") + ";" +
						  debit.toString().replace(".", ",") + ";";
			if (fullMonth > 0 && fullMonth < 12) {
				if (fullMonth == 1) {
					line += "1 Monat DLS befreit";
				} else 
					line += 12 - fullMonth + " Monate DLS befreit";
			} else if (fullMonth == 0) {
				line += "Alle Monat DLS befreit";
			} else if (fullMonth == 12) {
				line += "Keinen Monat DLS befreit";
			}
			lines.add(line);
		}
		FileHandler fh = new FileHandler();
		return fh.writeCsvFile(worker.getToDate().toDate(), lines, finalize, worker.getToDate().toDate());
	}

	@Override
	protected void extendTable() {
		table.removeAllActionHandlers();

		table.setVisibleColumns(new Object[] { "countDls", "doneDate",
				"comment", "member.memberId" });
		table.setColumnHeaders("Anzahl DLS", "Ableistungsdatum", "Bemerkung",
				"Mitgliedsnummer");
		table.setConverter("doneDate", new DateToShortGermanStringConverter());
		table.setWidth("90%");
		table.addActionHandler(getActionHandler());
		table.setColumnExpandRatio("countDls", (float) 0.1);
		table.setColumnExpandRatio("doneDate", (float) 0.2);
		table.setColumnExpandRatio("comment", (float) 0.5);
		table.setColumnExpandRatio("member.memberId", (float) 0.2);
	}

	private HorizontalLayout initFilter() {
		HorizontalLayout fLayout = new HorizontalLayout();
		fLayout.setSpacing(true);

		Label title = new Label("<strong>Filter:</strong>", ContentMode.HTML);
		fLayout.addComponent(title);

		BeanItemContainer<Year> years = new BeanItemContainer<Year>(Year.class);
		years.addAll(HibernateUtil.orderedList(Year.class, "year asc"));

		cbYears.setImmediate(true);
		cbYears.setStyleName("tiny");
		cbYears.setWidth(85, Unit.PIXELS);
		cbYears.setItemCaptionPropertyId("year");
		cbYears.setContainerDataSource(years);
		cbYears.setNullSelectionAllowed(false);
		//fLayout.addComponent(cbYears);
		cbYears.addValueChangeListener(event -> {
			//beans.removeContainerFilters("doneDate");
			//beans.addContainerFilter(new MyYearFilter("doneDate", ((Year) event
			//		.getProperty().getValue()).getYear()));
			//updateTable();
			btnYear.setCaption("Jahreslauf "
					+ ((Year) event.getProperty().getValue()).getYear()
					+ " durchführen");
			btnYearTest.setCaption("Vorläufiger Jahreslauf "
					+ ((Year) event.getProperty().getValue()).getYear());
		});
		if (cbYears.getItemIds().size() > 0) {
			cbYears.setValue(cbYears.getItemIds().toArray()[cbYears
					.getItemIds().size() - 1]);
		}

		TextField txtFilterDls = new TextField();
		txtFilterDls.setInputPrompt("Anzahl DLS");
		txtFilterDls.setConverter(Integer.class);
		txtFilterDls.setStyleName("tiny");
		txtFilterDls
				.setConversionError("Die Anzahl der Dienstleistungsstunden muss eine Zahl sein.");
		txtFilterDls.setNullRepresentation("");
		txtFilterDls.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("countDls", event.getText());
			} else {
				beans.removeContainerFilters("countDls");
			}

		});

		TextField txtFilterDate = new TextField();
		txtFilterDate.setDescription("Filter nach Ableistungsdatum");
		txtFilterDate.setInputPrompt("Filter nach Ableistungsdatum");
		txtFilterDate.setDescription("<ul>" +
			    "  <li>01.01.2014</li>" +
			    "  <li>= 01.01.2014</li>" +
			    "  <li>> 01.01.2014</li>" +
			    "  <li>< 01.01.2014</li>" +
			    "  <li>01.01.2014 - 31.12.2014</li>" +
			    "</ul>");
		txtFilterDate.setStyleName("tiny");
		txtFilterDate.addValidator(new MyDateRangeValidator());
		txtFilterDate.setValue("> " + DateTime.now().minusYears(1).toString("dd.MM.yyyy"));
		txtFilterDate.addBlurListener(event -> {
			beans.removeContainerFilters("doneDate");
			if (txtFilterDate.getValue() != null && !txtFilterDate.getValue().trim().equals("") && txtFilterDate.isValid()) {
				beans.addContainerFilter(new MyDateRangeFilter("doneDate", txtFilterDate.getValue()));
			}
		});
		beans.addContainerFilter(new MyDateRangeFilter("doneDate", txtFilterDate.getValue()));

		TextField txtFilterNote = new TextField();
		txtFilterNote.setConverter(String.class);
		txtFilterNote.setInputPrompt("Bemerkung");
		txtFilterNote.setStyleName("tiny");
		txtFilterNote.addBlurListener(event -> {
			if (!txtFilterNote.getValue().equals("")) {
				filterTable("comment", txtFilterNote.getValue());
			} else {
				beans.removeContainerFilters("comment");
			}
		});
		fLayout.addComponents(txtFilterDls, txtFilterDate, txtFilterNote);
		return fLayout;
	}

	private void filterTable(Object columnId, String value) {
		beans.addContainerFilter(columnId, value, true, false);
		updateTable();
	}

	private void YearWindow(CourseOfYearWorker worker) {
		Window window = new Window("Bestätigung");
		window.setModal(true);
		window.setWidth("500");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		Label lblQuestion = new Label(
				"Soll der Jahreslauf jetzt durchgeführt werden?<br /> "
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
		
		btnYes.addClickListener(evnet -> {
			FileResource res = new FileResource(runCourseOfYear(worker, true));
			setResource("download", res);
			ResourceReference rr = ResourceReference.create(res, this, "download");
			Page.getCurrent().open(rr.getURL(), null);
			
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

	@Override
	protected void newBeanWindow() {

	}

}
