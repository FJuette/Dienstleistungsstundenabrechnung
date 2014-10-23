package de.juette.views;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.DateToShortGermanStringConverter;
import de.juette.model.AbstractEntity;
import de.juette.model.Booking;
import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class BookingView extends EditableTable<Booking> implements View {

	private Handler actionHandler = new Handler() {
		private final Action STORNO = new Action("Stornieren");
		private final Action[] ACTIONS = new Action[] { STORNO };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Stornieren")) {
				if (!((Booking) table.getValue()).getCanceled()) {
					Booking b = new Booking();
					b.setDoneDate(new Date());
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

	@SuppressWarnings("unchecked")
	public BookingView() {
		beans = new BeanItemContainer<Booking>(Booking.class);
		beans.addAll((Collection<? extends Booking>) HibernateUtil
				.getAllAsList(Booking.class));
		beans.addNestedContainerProperty("member.memberId");

		btnChange.setVisible(false);
		btnNew.setVisible(false);
		
		VerticalLayout newLayout = new VerticalLayout();
		Button btnNewBookings = new Button("Neue Buchung(en)");
		btnNewBookings.setIcon(FontAwesome.PLUS);
		btnNewBookings.addClickListener(event -> {
			newBookingsWindow();
		});
		newLayout.addComponent(btnNewBookings);
		extendLayout = newLayout;

		filterLayout = initFilter();
		initLayout("Journal");
		initTable();
		extendTable();

		Button btnYear = new Button("Jahreslauf durchführen");
		Button btnThisYear = new Button("Vorläufiger Jahreslauf 2014");
		Button btnYearBefore = new Button("Vorläufiger Jahreslauf 2013");

		btnYear.setStyleName("primary");
		btnYearBefore.setStyleName("primary");
		btnThisYear.setStyleName("primary");

		HorizontalLayout btnsYear = new HorizontalLayout();
		btnsYear.setSpacing(true);
		btnsYear.addComponent(btnYearBefore);
		btnsYear.addComponent(btnThisYear);
		btnsYear.addComponent(btnYear);
		addComponent(btnsYear);

		btnYear.addClickListener(event -> {
			YearWindow();
		});
	}

	@Override
	protected void extendTable() {
		table.removeAllActionHandlers();

		table.setVisibleColumns(new Object[] { "countDls", "doneDate",
				"comment", "member.memberId" });
		table.setColumnHeaders("Anzahl DLS", "Ableistungsdatum", "Bemerkung",
				"Mitgliedsnummer");
		table.setConverter("doneDate", new DateToShortGermanStringConverter());
		table.setWidth("100%");
		table.addActionHandler(getActionHandler());
		table.setColumnExpandRatio("countDls", (float) 0.1);
		table.setColumnExpandRatio("doneDate", (float) 0.2);
		table.setColumnExpandRatio("comment", (float) 0.5);
		table.setColumnExpandRatio("member.memberId", (float) 0.2);
	}

	private HorizontalLayout initFilter() {
		HorizontalLayout fLayout = new HorizontalLayout();
		fLayout.setSpacing(true);
		TextField txtFilterDls = new TextField("Anzahl DLS:");
		txtFilterDls.setConverter(Integer.class);
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

		DateField dfFilterDate = new DateField("Datum:");
		dfFilterDate.addValueChangeListener(event -> {
			if (((DateField) event.getProperty()).getValue() != null) {
				filterTable("doneDate", ((DateField) event.getProperty())
						.getValue().toString());
			} else {
				beans.removeContainerFilters("doneDate");
			}

		});

		TextField txtFilterNote = new TextField("Bemerkung:");
		txtFilterNote.setConverter(String.class);
		txtFilterNote.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("comment", event.getText());
			} else {
				beans.removeContainerFilters("comment");
			}

		});
		fLayout.addComponents(txtFilterDls, dfFilterDate, txtFilterNote);
		return fLayout;
	}

	private void filterTable(Object columnId, String value) {
		beans.addContainerFilter(columnId, value, true, false);
		updateTable();
	}

	@SuppressWarnings("unchecked")
	private void newBookingsWindow() {
		Window window = new Window("Anlegen einer neuen Buchung");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		BeanItemContainer<Member> members = new BeanItemContainer<Member>(
				Member.class);
		members.addAll((Collection<? extends Member>) HibernateUtil
				.getAllAsList(Member.class));

		ComboBox cbMembers = new ComboBox("Mitglied");
		cbMembers.setWidth("100%");
		cbMembers.setImmediate(true);
		cbMembers.setContainerDataSource(members);
		cbMembers.setItemCaptionPropertyId("fullName");
		cbMembers.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbMembers);

		DateField dfDate = new DateField("Ableistungsdatum");
		dfDate.setWidth("100%");
		layout.addComponent(dfDate);

		BeanItemContainer<Campaign> activities = new BeanItemContainer<Campaign>(
				Campaign.class);
		activities.addAll((Collection<? extends Campaign>) HibernateUtil
				.getAllAsList(Campaign.class));

		ComboBox cbCampaigns = new ComboBox("Aktion");
		cbCampaigns.setWidth("100%");
		cbCampaigns.setImmediate(true);
		cbCampaigns.setContainerDataSource(activities);
		cbCampaigns.setItemCaptionPropertyId("description");
		cbCampaigns.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbCampaigns);

		TextField txtCountDls = new TextField("Anzahl DLS");
		txtCountDls.setWidth("100%");
		layout.addComponent(txtCountDls);

		TextArea txtComment = new TextArea("Bemerkung");
		txtComment.setWidth("100%");
		layout.addComponent(txtComment);

		Button btnSaveNewBooking = new Button("Speichern");
		btnSaveNewBooking.setStyleName("friendly");
		layout.addComponent(btnSaveNewBooking);

		btnSaveNewBooking.addClickListener(event -> {
			beans.addItem(new Booking(Double.parseDouble(txtCountDls.getValue()
					.replace(',', '.')), txtComment.getValue(), dfDate
					.getValue(), (Member) cbMembers.getValue(),
					(Campaign) cbCampaigns.getValue()));
			updateTable();
			HibernateUtil.saveAll((List<? extends AbstractEntity>) beans
					.getItemIds());
			txtCountDls.setValue("");
			txtComment.setValue("");
			cbCampaigns.setValue(null);
			dfDate.focus();
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

	@Override
	protected void newBeanWindow() {
		// TODO Auto-generated method stub

	}

}
