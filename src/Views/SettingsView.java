package Views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;

@SuppressWarnings("serial")
public class SettingsView extends VerticalLayout implements View {

	private final TextField txtStichtag = new TextField("Stichtag:");
	private final TextField txtCountDls = new TextField("Anzahl der Dienstleistungsstunden pro Jahr:");
	private final TextField txtCostDls = new TextField("Kosten pro Dienstleistungsstunde:");
	private final OptionGroup groupKind = new OptionGroup("Art des Jahreslaufes:");
	private final CheckBox cbAusgleich = new CheckBox("Ausgleichungsbuchungen beim Jahreslauf");
	private final CheckBox cbUebername = new CheckBox("Übername von DLS beim Jahreswechsel");
	private final Button btnSave = new Button("Speichern");
	
	public SettingsView() {
		groupKind.addItem("Volles Jahr zum Stichtag");
		groupKind.addItem("Anteilig bis zum Stichtag");
		groupKind.addItem("Nur volle Monate im Verein");
		btnSave.addClickListener(event -> {
			Notification.show("Speichern erfolgreich.", Notification.Type.TRAY_NOTIFICATION);
		});
		
		addComponents(txtStichtag, txtCountDls, txtCostDls, groupKind, cbAusgleich, cbUebername, btnSave);
	
		addDummyData();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
	
	private void addDummyData() {
		txtStichtag.setValue("31.01.");
		txtCountDls.setValue("5");
		txtCostDls.setValue("10 €");
		groupKind.select("Volles Jahr zum Stichtag");
		cbUebername.setValue(true);
	}
}
