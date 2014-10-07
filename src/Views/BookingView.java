package Views;

import model.Booking;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BookingView extends VerticalLayout implements View {

	private final Table tblBookings = new Table();
	
	private BeanItemContainer<Booking> bookings = new BeanItemContainer<>(Booking.class);
		
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
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
