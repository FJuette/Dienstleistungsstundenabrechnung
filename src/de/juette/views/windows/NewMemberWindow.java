package de.juette.views.windows;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.Member;

public class NewMemberWindow extends Window {

	private static final long serialVersionUID = -2452111229377020184L;
	private FieldGroup fieldGroup = new BeanFieldGroup<Member>(Member.class);
	private BeanItem<Member> member = new BeanItem<Member>(new Member());

	public BeanItem<Member> getMember() {
		return member;
	}

	public NewMemberWindow() {
		setModal(true);
		setWidth("400");
		setCaption("Anlegen eines neuen Mitglieds");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);

		fieldGroup = new BeanFieldGroup<Member>(Member.class);
		fieldGroup.setItemDataSource(member);

		TextField txtMemberId = new TextField("Mitgliedsnummer:");
		txtMemberId.setNullRepresentation("");
		txtMemberId.setRequired(true);
		txtMemberId.setRequiredError("Bitte eine Mitgliedsnummer eingeben.");
		txtMemberId.setImmediate(true);
		fieldGroup.bind(txtMemberId, "memberId");

		TextField txtSurname = new TextField("Nachname:");
		txtSurname.setNullRepresentation("");
		txtSurname.setRequired(true);
		txtSurname.setImmediate(true);
		txtSurname.setRequiredError("Bitte einen Nachnamen eingeben.");
		fieldGroup.bind(txtSurname, "surname");

		TextField txtForname = new TextField("Vorname:");
		txtForname.setNullRepresentation("");
		txtForname.setRequired(true);
		txtForname.setImmediate(true);
		txtForname.setRequiredError("Bitte einen Vornamen eingeben.");
		fieldGroup.bind(txtForname, "forename");
		
		DateField dfBirthdate = new DateField("Geburtsdatum");
		dfBirthdate.setRequired(true);
		dfBirthdate.setRequiredError("Bitte das Geburtsdatum eingeben.");
		dfBirthdate.setImmediate(true);
		fieldGroup.bind(dfBirthdate, "birthdate");

		DateField dfEntryDate = new DateField("Eintrittsdatum");
		dfEntryDate.setRequired(true);
		dfEntryDate.setRequiredError("Bitte das Eintrittsdatum eingeben.");
		dfEntryDate.setImmediate(true);
		fieldGroup.bind(dfEntryDate, "entryDate");

		layout.addComponents(txtForname, txtSurname, txtMemberId, dfBirthdate, dfEntryDate);

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				if (fieldGroup.isValid()) {
					fieldGroup.commit();
					close();
				} else {
					Notification.show("Bitte alle benötigten Felder ausfüllen.", Type.WARNING_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

	}

}
