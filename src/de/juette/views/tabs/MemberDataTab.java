package de.juette.views.tabs;

import java.text.SimpleDateFormat;
import java.util.Date;



import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;



import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;



import de.juette.dlsa.GeneralHandler;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

public class MemberDataTab extends MyDataTab<Member> {

	private static final long serialVersionUID = -4275563354183555579L;
	private Boolean activeState;
	private Date entryDate;
	private Date leavingDate;

	public MemberDataTab(BeanItem<Member> beanItem) {
		beanItem.getBean().addPropertyChangeListener(e -> {
			// Normal Change, like active to passive
			if (e.getOldValue() != null && e.getNewValue() != null && !e.getOldValue().equals(e.getNewValue())) {
				System.out.printf("Property '%s': '%s' -> '%s'%n", 
						e.getPropertyName(), e.getOldValue(), e.getNewValue());
			// Change from nothing to value, e.g. leaving date
			} else if(e.getOldValue() == null && e.getNewValue() != null) {
				System.out.printf("Property '%s': '%s' -> '%s'%n", 
						e.getPropertyName(), e.getOldValue(), e.getNewValue());
			// Deleting, e.g the leaving date
			} else if(e.getOldValue() != null && e.getNewValue() == null) {
				System.out.printf("Property '%s': '%s' -> '%s'%n", 
						e.getPropertyName(), e.getOldValue(), e.getNewValue());
			}
			
		});
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
		entryDate = beanItem.getBean().getEntryDate();
		fieldGroup.bind(dfEntryDate, "entryDate");

		addComponents(txtForname, txtSurname, txtMemberId, dfBirthdate,
				dfEntryDate);

		DateField dfLeavingDate = new DateField("Austrittsdatum");
		fieldGroup.bind(dfLeavingDate, "leavingDate");
		leavingDate = beanItem.getBean().getLeavingDate();
		addComponent(dfLeavingDate);

		CheckBox cbActive = new CheckBox("Aktiv");
		fieldGroup.bind(cbActive, "active");
		activeState = beanItem.getBean().getActive();
		addComponent(cbActive);

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		
		
		DateField dfRefDate = new DateField("Bezugsdatum der Änderung");
		dfRefDate.setStyleName("tiny");
		dfRefDate.setValue(DateTime.now().toDate());
		dfRefDate.setDateFormat("dd.MM.yyyy");
		
		// Show these two fields not in the guest role
		if (!SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(btnSave);
			addComponent(dfRefDate);
		}

		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (GeneralHandler.isRefDateValid(dfRefDate.getValue())) {
				if (beanItem.getBean().getActive() != activeState) {
					HibernateUtil.writeLogEntry(beanItem.getBean(), "Aktiv von "
							+ activeState + " nach "
							+ beanItem.getBean().getActive() + " geändert",
							SecurityUtils.getSubject().getPrincipal().toString(),
							dfRefDate.getValue());
				}
				if (beanItem.getBean().getLeavingDate() != null && leavingDate == null) {
					HibernateUtil.writeLogEntry(beanItem.getBean(),
							"Austrittsdatum eingetragen: " + getFormattedDate(beanItem.getBean().getLeavingDate()), 
							SecurityUtils.getSubject().getPrincipal().toString(), 
							dfRefDate.getValue());
				}
				else if (beanItem.getBean().getLeavingDate() == null
						&& leavingDate != null) {
					HibernateUtil.writeLogEntry(beanItem.getBean(),
							"Austrittsdatum gelöscht", 
							SecurityUtils.getSubject().getPrincipal().toString(), 
							dfRefDate.getValue());
				}
				else if (beanItem.getBean().getEntryDate() != null
						&& leavingDate != null
						&& beanItem.getBean().getLeavingDate().compareTo(leavingDate) != 0) {
									
					HibernateUtil.writeLogEntry(beanItem.getBean(),
							"Austrittsdatum von " + getFormattedDate(leavingDate) + " nach "
									+ getFormattedDate(beanItem.getBean().getLeavingDate())
									+ " geändert", SecurityUtils.getSubject()
									.getPrincipal().toString(), 
									dfRefDate.getValue());
				}
				if (beanItem.getBean().getEntryDate() != null
						&& entryDate != null
						&& beanItem.getBean().getEntryDate().compareTo(entryDate) != 0) {
					HibernateUtil.writeLogEntry(beanItem.getBean(),
							"Eintrittsdatum von " + getFormattedDate(entryDate) + " nach "
									+ getFormattedDate(beanItem.getBean().getEntryDate())
									+ " geändert", SecurityUtils.getSubject()
									.getPrincipal().toString(), 
									dfRefDate.getValue());
				}
				if (fieldGroup.isModified()) {
					HibernateUtil.save(beanItem.getBean());
					fireDataSavedEvent();
					Notification.show("Speichern Erfolgreich.", Type.TRAY_NOTIFICATION);
				}
				
			} else {
				GeneralHandler.showNoVaildRefDateException();
			}
		});
	}
	
	private String getFormattedDate(Date d) {
		return new SimpleDateFormat("dd.MM.yyyy").format(d);
	}
}
