package Views;

import model.Subject;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class SubjectView extends VerticalLayout implements View {

	private final Table tblSubjects = new Table();
	@PropertyId("spartenname")
	private TextField txtSpartenname = new TextField("Spartenname");
	
	private FormLayout editorLayout = new FormLayout();
	private FieldGroup editorFields = new FieldGroup();
	private BeanItemContainer<Subject> subjects = new BeanItemContainer<>(Subject.class);
	
	private final Button btnNewSubject = new Button("Neue Sparte");
	private final Button btnSave = new Button("Speichern");
	private final Button btnDelete = new Button("Löschen");
	
	public SubjectView() {
		initLayout();
		initTable();
		initEditor();
	}
	
	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Spartenverwaltung");
		title.addStyleName("h1");
		addComponent(title);
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		addComponent(splitPanel);
		
		VerticalLayout leftLayout = new VerticalLayout();
		splitPanel.addComponent(leftLayout);
		splitPanel.addComponent(editorLayout);
		leftLayout.addComponent(tblSubjects);
		
		splitPanel.setSplitPosition(25, Unit.PERCENTAGE);
		splitPanel.setLocked(true);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(btnNewSubject);
		
		btnNewSubject.addClickListener(event -> {
			newSubjectWindow();
		});
		
		leftLayout.setWidth("100%");
		leftLayout.setSpacing(true);
		leftLayout.setExpandRatio(tblSubjects, 1);
		tblSubjects.setSizeFull();
		
		bottomLeftLayout.setWidth("100%");
		editorLayout.setMargin(true);
		editorLayout.setVisible(false);
	}
		
	private void initTable() {
		subjects = ComponentHelper.getDummySubjects();
		
		tblSubjects.setContainerDataSource(subjects);
		tblSubjects.setSelectable(true);
		tblSubjects.setImmediate(true);
		tblSubjects.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblSubjects.setVisibleColumns( new Object[] {"spartenname"} );
		tblSubjects.setColumnHeaders("Sparte");
		
		tblSubjects.addItemClickListener(event -> {
			editorFields.setItemDataSource(event.getItem());
			editorFields.bindMemberFields(this);
			editorLayout.setVisible(event.getItem() != null);
		});
		
		updateTable();
	}
	
	private void initEditor() {
		editorLayout.addComponent(txtSpartenname);
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponents(btnSave, btnDelete);
		editorLayout.addComponent(buttonLayout);
		editorLayout.setWidth("100%");
				
		btnSave.addClickListener(event -> {
			try {
				editorFields.commit();
				Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		btnDelete.setStyleName("danger");
		btnDelete.addClickListener(event -> {
			try {
				subjects.removeItem(tblSubjects.getValue());
				updateTable();
			} catch (Exception e2) {
				Notification.show("Fehler: " + e2.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		editorFields.setBuffered(false);
	}
	
	private void updateTable() {
		if (tblSubjects.size() > 15) {
			tblSubjects.setPageLength(15);
		} else {
			tblSubjects.setPageLength(tblSubjects.size());
		}
		tblSubjects.markAsDirtyRecursive();
	}
	
	private void newSubjectWindow() {
		Window window = new Window("Anlegen einer neuen Sparte");
		window.setModal(true);
		window.setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);
		
		TextField txtNewSubject = new TextField("Name");
		txtNewSubject.setWidth("100%");
		layout.addComponent(txtNewSubject);
				
		Button btnSaveNewSubject = new Button("Speichern");
		layout.addComponent(btnSaveNewSubject);
		
		btnSaveNewSubject.addClickListener(event -> {
			subjects.addItem(new Subject(txtNewSubject.getValue()));
			updateTable();
			window.close();
		});
		
		btnDelete.setStyleName("danger");
		btnDelete.addClickListener(event -> {
			try {
				subjects.removeItem(tblSubjects.getValue());
				updateTable();
				editorLayout.setVisible(false);
			} catch (Exception e2) {
				Notification.show("Fehler: " + e2.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		getUI().addWindow(window);
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
