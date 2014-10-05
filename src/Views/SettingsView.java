package Views;

import java.util.Date;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SettingsView extends VerticalLayout implements View {

	private final DateField dfStichtag = new DateField("Stichtag");
	private final TextField txtCountDls = new TextField("Anzahl der Dienstleistungsstunden pro Jahr");
	private final TextField txtCostDls = new TextField("Kosten pro Dienstleistungsstunde");
	private final OptionGroup groupKind = new OptionGroup();
	private final CheckBox cbAusgleich = new CheckBox("Ausgleichungsbuchungen beim Jahreslauf");
	private final CheckBox cbUebername = new CheckBox("Übername von DLS beim Jahreswechsel");
	private final Button btnSave = new Button("Speichern");
	
	public SettingsView() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Einstellungen");
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
		form.addComponent(dfStichtag);
				
		txtCostDls.setWidth("25%");
		form.addComponent(txtCostDls);
		
		section = new Label("Buchungsverwalten");
		section.addStyleName("h2");
		section.addStyleName("colored");
		form.addComponent(section);
		
		HorizontalLayout wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Berechnung beim Jahreslauf");
		groupKind.addItem("Volles Jahr zum Stichtag");
		groupKind.addItem("Anteilig bis zum Stichtag");
		groupKind.addItem("Nur volle Monate im Verein");
		groupKind.addStyleName("horizontal");
		wrap.addComponent(groupKind);
		form.addComponent(wrap);
		
		wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Ausgleichsbuchungen");
		wrap.addComponent(cbAusgleich);
		form.addComponent(wrap);
		
		wrap = new HorizontalLayout();
		wrap.setSpacing(true);
		wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		wrap.setCaption("Übernahme von DLS");
		wrap.addComponent(cbUebername);
		form.addComponent(wrap);
		
		form.addComponent(btnSave);
		
		btnSave.addClickListener(event -> {
			Notification.show("Speichern erfolgreich.", Notification.Type.TRAY_NOTIFICATION);
		});
		
		form.setReadOnly(false);
		
		addDummyData();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}
	
	@SuppressWarnings("deprecation")
	private void addDummyData() {
		dfStichtag.setValue(new Date(114, 11, 31));
		txtCountDls.setValue("5");
		txtCostDls.setValue("10 €");
		groupKind.select("Volles Jahr zum Stichtag");
		cbUebername.setValue(true);
	}
}
