package Views;

import model.Subject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
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
	private BeanItemContainer<Subject> subjects = new BeanItemContainer<>(Subject.class);
	
	private final Button btnNewSubject = new Button("Neue Sparte");
	private final Button btnChange = new Button("Bearbeiten");
	
	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				subjects.removeItem(tblSubjects.getValue());
				updateTable();
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};
	
	public SubjectView() {
		initLayout();
		initTable();
	}
	
	private void initLayout() {
		setSpacing(true);
		setMargin(true);
		
		Label title = new Label("Spartenverwaltung");
		title.addStyleName("h1");
		addComponent(title);
		
		addComponent(tblSubjects);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponents(btnChange, btnNewSubject);
		
		btnChange.addClickListener(event -> {
			try {
				if("Bearbeiten".equals(btnChange.getCaption())) {
					tblSubjects.setEditable(true);
					btnChange.setCaption("Speichern");
					updateTable();
				} else {
					tblSubjects.setEditable(false);
					btnChange.setCaption("Bearbeiten");
					Notification.show("Speichern erfolgreich", Notification.Type.TRAY_NOTIFICATION);
					updateTable();
				}
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});
		
		btnNewSubject.addClickListener(event -> {
			newSubjectWindow();
		});
		
	}
		
	private void initTable() {
		subjects = ComponentHelper.getDummySubjects();
		
		tblSubjects.setContainerDataSource(subjects);
		tblSubjects.setSelectable(true);
		tblSubjects.setImmediate(true);
		tblSubjects.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		tblSubjects.setVisibleColumns( new Object[] {"spartenname"} );
		tblSubjects.setColumnHeaders("Sparte");
		tblSubjects.setWidth("40%");
		tblSubjects.addActionHandler(getActionHandler());
		
		updateTable();
	}
	
	private void updateTable() {
		if (tblSubjects.size() > 15) {
			tblSubjects.setPageLength(15);
		} else {
			tblSubjects.setPageLength(tblSubjects.size() + 1);
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
		
		getUI().addWindow(window);
	}
	
	private Handler getActionHandler() {
		return actionHandler;
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
