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
import com.vaadin.ui.TextField;

import de.juette.model.HibernateUtil;
import de.juette.model.Member;

public class MemberDataTab extends MyDataTab<Member> {

	private static final long serialVersionUID = -4275563354183555579L;
	private Boolean activeState;
	private Date entryDate;
	private Date leavingDate;

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
		addComponent(btnSave);

		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (beanItem.getBean().getActive() != activeState) {
				HibernateUtil.writeLogEntry(beanItem.getBean().getFullName(), "Aktiv von "
						+ activeState + " nach "
						+ beanItem.getBean().getActive() + " geändert",
						SecurityUtils.getSubject().getPrincipal().toString(),
						beanItem.getBean().getId(), DateTime.now().toDate());
			}
			if (beanItem.getBean().getLeavingDate() != null && leavingDate == null) {
				HibernateUtil.writeLogEntry(beanItem.getBean().getFullName(),
						"Austrittsdatum eingetragen: " + getFormattedDate(beanItem.getBean().getLeavingDate()), 
						SecurityUtils.getSubject().getPrincipal().toString(), 
						beanItem.getBean().getId(), DateTime.now().toDate());
			}
			else if (beanItem.getBean().getLeavingDate() == null
					&& leavingDate != null) {
				HibernateUtil.writeLogEntry(beanItem.getBean().getFullName(),
						"Austrittsdatum gelöscht", 
						SecurityUtils.getSubject().getPrincipal().toString(), 
						beanItem.getBean().getId(), DateTime.now().toDate());
			}
			else if (beanItem.getBean().getEntryDate() != null
					&& leavingDate != null
					&& beanItem.getBean().getLeavingDate().compareTo(leavingDate) != 0) {
				HibernateUtil.writeLogEntry(beanItem.getBean().getFullName(),
						"Austrittsdatum von " + getFormattedDate(leavingDate) + " nach "
								+ getFormattedDate(beanItem.getBean().getLeavingDate())
								+ " geändert", SecurityUtils.getSubject()
								.getPrincipal().toString(), beanItem.getBean()
								.getId(), DateTime.now().toDate());
			}
			if (beanItem.getBean().getEntryDate() != null
					&& entryDate != null
					&& beanItem.getBean().getEntryDate().compareTo(entryDate) != 0) {
				HibernateUtil.writeLogEntry(beanItem.getBean().getFullName(),
						"Eintrittsdatum von " + getFormattedDate(entryDate) + " nach "
								+ getFormattedDate(beanItem.getBean().getEntryDate())
								+ " geändert", SecurityUtils.getSubject()
								.getPrincipal().toString(), beanItem.getBean()
								.getId(), DateTime.now().toDate());
			}
			HibernateUtil.save(beanItem.getBean());
			fireDataSavedEvent();
		});
	}
	
	private String getFormattedDate(Date d) {
		return new SimpleDateFormat("dd.MM.yyyy").format(d);
	}
}
