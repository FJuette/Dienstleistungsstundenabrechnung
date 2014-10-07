package Views;

import model.Booking;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.ComponentHelper;
import de.juette.dlsa.DateToShortGermanStringConverter;
import de.juette.dlsa.MainUI;

@SuppressWarnings("serial")
public class BookingView extends VerticalLayout implements View {

	private Handler actionHandler = new Handler() {
		private final Action ACTION_ONE = new Action("Action One");
		private final Action ACTION_TWO = new Action("Action Two");
		private final Action ACTION_THREE = new Action("Action Three");
		private final Action[] ACTIONS = new Action[] { ACTION_ONE, ACTION_TWO,
				ACTION_THREE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			Notification.show(action.getCaption());
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};
	
	private final Table tblBookings = new Table();

	private BeanItemContainer<Booking> bookings = new BeanItemContainer<>(
			Booking.class);

	public BookingView() {
		initLayout();
		initTable();
	}

	private void initLayout() {
		setSpacing(true);
		setMargin(true);

		Label title = new Label("Journal");
		title.addStyleName("h1");
		addComponent(title);

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
