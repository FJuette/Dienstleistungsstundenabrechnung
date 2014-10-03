package Views;

import model.Group;
import model.Subject;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class SubjectView extends HorizontalSplitPanel implements View {

	private final Table tblSubjects = new Table();
	@PropertyId("spartenname")
	private TextField txtSpartenname = new TextField("Spartenname:");
	private FieldGroup fieldGroup;
	private BeanItemContainer<Subject> subjects = new BeanItemContainer<>(Subject.class);
	
	public SubjectView() {
		initTable();
		setFirstComponent(tblSubjects);
		setSecondComponent(createEditFields());
		
		setSplitPosition(15, Unit.PERCENTAGE);
		setLocked(true);
	}
	
	private void initTable() {
		createDummySubjects();
		
		tblSubjects.setContainerDataSource(subjects);
		tblSubjects.setSelectable(true);
		tblSubjects.setVisibleColumns( new Object[] {"spartenname"} );
		tblSubjects.setColumnHeaders("Sparte");
		tblSubjects.setHeight("400");
		
		tblSubjects.addItemClickListener(event -> {
			fieldGroup.setItemDataSource(event.getItem());
			fieldGroup.bindMemberFields(this);
		});
	}
	
	private FormLayout createEditFields() {
		// Set the Caption of the TextField on the Top
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(txtSpartenname);
		
		FormLayout layout = new FormLayout();
		layout.setSizeUndefined();
		layout.setMargin(true);
		
		fieldGroup = new BeanFieldGroup<Group>(Group.class);
		
		Button btnSave = new Button("Speichern");
		btnSave.addClickListener(event -> {
			try {
				fieldGroup.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		layout.addComponents(txtLayout, btnSave);
		return layout;
	}
	
	
	private void createDummySubjects() {
		subjects.addItem(new Subject("Spasstänzer"));
		subjects.addItem(new Subject("Standdardstänzer"));
		subjects.addItem(new Subject("Rutinetänzer"));
		subjects.addItem(new Subject("Proditänzer"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
