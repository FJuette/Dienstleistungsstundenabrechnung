package de.juette.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberDataTab extends FormLayout {
	
	public interface DataSaveListener {
		public void dataSaved(DataSaveEvent event);
	}
	
	public class DataSaveEvent {
		
		final BeanItem<Member> beanItem;
		
		public DataSaveEvent(BeanItem<Member> beanItem) {
			this.beanItem = beanItem;
		}
		
		public BeanItem<Member> getBeanItem() {
			return beanItem;
		}
	}
	
	private List<DataSaveListener> listeners = null;
	
	private FieldGroup fieldGroup = new BeanFieldGroup<Member>(Member.class);
	private BeanItem<Member> beanItem;

	public MemberDataTab(BeanItem<Member> beanItem) {
		this.beanItem = beanItem;
		setSizeFull();
		setStyleName("myFormLayout");

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(beanItem);

		TextField txtMemberId = new TextField("Mitgliedsnummer:");
		txtMemberId.setNullRepresentation("");
		fieldGroup.bind(txtMemberId, "memberId");

		TextField txtSurname = new TextField("Nachname:");
		txtSurname.setNullRepresentation("");
		fieldGroup.bind(txtSurname, "surname");

		TextField txtForname = new TextField("Vorname:");
		txtForname.setNullRepresentation("");
		fieldGroup.bind(txtForname, "forename");
		
		DateField dfBirthdate = new DateField("Geburtsdatum");
		fieldGroup.bind(dfBirthdate, "birthdate");

		DateField dfEntryDate = new DateField("Eintrittsdatum");
		fieldGroup.bind(dfEntryDate, "entryDate");

		addComponents(txtForname, txtSurname, txtMemberId, dfBirthdate, dfEntryDate);

		DateField dfLeavingDate = new DateField("Austrittsdatum");
		fieldGroup.bind(dfLeavingDate, "leavingDate");
		addComponent(dfLeavingDate);

		CheckBox cbActive = new CheckBox("Aktiv");
		fieldGroup.bind(cbActive, "active");
		addComponent(cbActive);

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
			HibernateUtil.save(beanItem.getBean());
			fireDataSavedEvent();
		});
	}

	private void fireDataSavedEvent() {
		if (listeners != null) {
			DataSaveEvent event = new DataSaveEvent(this.beanItem);
			for (DataSaveListener listener : listeners) {
				listener.dataSaved(event);
			}
		}
	}
	
	public void addDataSaveListener(DataSaveListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<DataSaveListener>();
		}
		listeners.add(listener);
	}
	
	public void removeDataSaveListener(DataSaveListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<DataSaveListener>();
		}
		listeners.remove(listener);
	}
}
