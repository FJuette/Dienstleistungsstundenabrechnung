package de.juette.views.tabs;

import org.apache.shiro.SecurityUtils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

import de.juette.dlsa.layout.DataTabLayout;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;


public class MemberDataTab extends DataTabLayout<Member> {

	private static final long serialVersionUID = -4275563354183555579L;

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

		addComponents(txtForname, txtSurname, txtMemberId, dfBirthdate,
				dfEntryDate);

		DateField dfLeavingDate = new DateField("Austrittsdatum");
		fieldGroup.bind(dfLeavingDate, "leavingDate");
		addComponent(dfLeavingDate);

		CheckBox cbActive = new CheckBox("Aktiv");
		fieldGroup.bind(cbActive, "active");
		addComponent(cbActive);

		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");

		// Show these two fields not in the guest role
		if (!SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(btnSave);
		}

		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				HibernateUtil.save(beanItem.getBean());
				fireDataSavedEvent();
				Notification.show("Speichern Erfolgreich.",
						Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
