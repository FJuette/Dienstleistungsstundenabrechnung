package de.juette.views;

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

@SuppressWarnings("serial")
public class NewMemberWindow extends Window {

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
		fieldGroup.bind(txtMemberId, "memberId");

		TextField txtSurname = new TextField("Nachname:");
		txtSurname.setNullRepresentation("");
		fieldGroup.bind(txtSurname, "surname");

		TextField txtForname = new TextField("Vorname:");
		txtForname.setNullRepresentation("");
		fieldGroup.bind(txtForname, "forename");

		DateField dfEntryDate = new DateField("Eintrittsdatum");
		fieldGroup.bind(dfEntryDate, "entryDate");

		layout.addComponents(txtForname, txtSurname, txtMemberId, dfEntryDate);

		Button btnSaveNewMember = new Button("Speichern");
		btnSaveNewMember.setStyleName("friendly");
		layout.addComponent(btnSaveNewMember);

		btnSaveNewMember.addClickListener(event -> {
			try {
				fieldGroup.commit();
				close();
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}
		});

	}

}
