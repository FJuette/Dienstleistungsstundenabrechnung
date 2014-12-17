package de.juette.views.windows;

import java.util.Collection;

import org.joda.time.DateTime;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

public class MemberRefBookingWindow extends Window {

	private static final long serialVersionUID = 4757843366829822469L;
	private final ComboBox cbMethod = new ComboBox("Was soll verändert werden?");
	private final VerticalLayout contentLayout = new VerticalLayout();
	private final DateField dfRefDate = new DateField("Bezugsdatum");
	
	private BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
			Group.class);
	
	public MemberRefBookingWindow(Collection<Member> members) {
		setModal(true);
		setWidth("450");
		setCaption("Änderung zu einem Bezugsdatum");
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		cbMethod.addItem("Eintrittsdatum");
		cbMethod.addItem("Austrittsdatum");
		cbMethod.addItem("Aktiv/Passiv");
		cbMethod.addItem("Funktionsgruppenzugehörigkeit");
		cbMethod.setImmediate(true);
		cbMethod.setNullSelectionAllowed(false);
		cbMethod.select("Eintrittsdatum");
		
		layout.addComponent(cbMethod);
		layout.setComponentAlignment(cbMethod, Alignment.TOP_CENTER);
		dfRefDate.setImmediate(true);
		dfRefDate.setValue(DateTime.now().toDate());
		layout.addComponent(dfRefDate);
		layout.setComponentAlignment(dfRefDate, Alignment.TOP_CENTER);
		
		layout.addComponent(contentLayout);
		layout.setComponentAlignment(contentLayout, Alignment.MIDDLE_CENTER);
		contentLayout.addComponent(getEntryDateLayout());
		contentLayout.setComponentAlignment(contentLayout.getComponent(0), Alignment.MIDDLE_CENTER);
		
		cbMethod.addValueChangeListener(event -> {
			contentLayout.removeAllComponents();
			switch (event.getProperty().getValue().toString()) {
			case "Eintrittsdatum":
				contentLayout.addComponent(getEntryDateLayout());
				break;
			case "Austrittsdatum":
				contentLayout.addComponent(getLeavingDateLayout());
				break;
			case "Aktiv/Passiv":
				contentLayout.addComponent(getActiveLayout());
				break;
			case "Funktionsgruppenzugehörigkeit":
				contentLayout.addComponent(getGroupLayout());
				break;
	
			default:
				break;
			}
		});
	}
	
	private VerticalLayout getActiveLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		Label lblHead = new Label("<strong>Aktiv/Passiv verändern<strong>", ContentMode.HTML);
		layout.addComponent(lblHead);
		layout.setComponentAlignment(lblHead, Alignment.MIDDLE_CENTER);
		OptionGroup single = new OptionGroup("Typ auswählen");
		single.addItems("Aktiv", "Passiv");
		single.setValue("Aktiv");
		layout.addComponent(single);
		layout.setComponentAlignment(single, Alignment.MIDDLE_CENTER);
		
		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			if (single.getValue().toString().equals("Aktiv")) {
				// TODO
			} else
				// TODO
			close();
		});
		layout.addComponent(btnSave);
		layout.setComponentAlignment(btnSave, Alignment.MIDDLE_CENTER);
		return layout;
	}
	
	private VerticalLayout getEntryDateLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		Label lblHead = new Label("<strong>Eintrittsdatum verändern<strong>", ContentMode.HTML);
		layout.addComponent(lblHead);
		layout.setComponentAlignment(lblHead, Alignment.MIDDLE_CENTER);
		DateField dfNewDate = new DateField("Neues Datum:");
		dfNewDate.setImmediate(true);
		layout.addComponent(dfNewDate);
		layout.setComponentAlignment(dfNewDate, Alignment.MIDDLE_CENTER);
		
		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			//TODO
			close();
		});
		layout.addComponent(btnSave);
		layout.setComponentAlignment(btnSave, Alignment.MIDDLE_CENTER);
		return layout;
	}
	
	private VerticalLayout getLeavingDateLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		Label lblHead = new Label("<strong>Austrittsdatum verändern<strong>", ContentMode.HTML);
		layout.addComponent(lblHead);
		layout.setComponentAlignment(lblHead, Alignment.MIDDLE_CENTER);
		DateField dfNewDate = new DateField("Neues Datum:");
		dfNewDate.setImmediate(true);
		layout.addComponent(dfNewDate);
		layout.setComponentAlignment(dfNewDate, Alignment.MIDDLE_CENTER);
		
		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			//TODO
			close();
		});
		layout.addComponent(btnSave);
		layout.setComponentAlignment(btnSave, Alignment.MIDDLE_CENTER);
		return layout;
	}
	
	private VerticalLayout getGroupLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		groups.addAll(HibernateUtil.getAllAsList(Group.class));
		Label lblHead = new Label("<strong>Funktionsgruppenzugehörigkeit verändern<strong>", ContentMode.HTML);
		layout.addComponent(lblHead);
		layout.setComponentAlignment(lblHead, Alignment.MIDDLE_CENTER);
		ComboBox cbAll = new ComboBox("Gruppe wählen:");
		cbAll.setContainerDataSource(groups);
		cbAll.setItemCaptionPropertyId("groupName");
		cbAll.setImmediate(true);
		cbAll.setWidth("300");
		layout.addComponent(cbAll);
		layout.setComponentAlignment(cbAll, Alignment.MIDDLE_CENTER);
		
		OptionGroup ogAddRem = new OptionGroup();
		ogAddRem.addItems("Hinzufügen", "Entfernen");
		ogAddRem.setValue("Hinzufügen");
		layout.addComponent(ogAddRem);
		layout.setComponentAlignment(ogAddRem, Alignment.MIDDLE_CENTER);
		
		Button btnSave = new Button("Speichern");
		btnSave.setStyleName("friendly");
		btnSave.addClickListener(event -> {
			if (ogAddRem.getValue().toString().equals("Hinzufügen")) {
				// TODO
				//group = (Group) cbAll.getValue();
			} else if (ogAddRem.getValue().toString().equals("Entfernen"))
				// TODO
			close();
		});
		layout.addComponent(btnSave);
		layout.setComponentAlignment(btnSave, Alignment.MIDDLE_CENTER);
		return layout;
	}
}
