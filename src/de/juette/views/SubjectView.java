package de.juette.views;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;
import de.juette.model.HibernateUtil;
import de.juette.model.Subject;

@SuppressWarnings("serial")
public class SubjectView extends EditableTable<Subject> implements View {

	public SubjectView() {
		beans = new BeanItemContainer<>(Subject.class);
		beans.addAll((Collection<? extends Subject>) HibernateUtil.getAllAsList(Subject.class));
		
		btnNew.setCaption("Neue Sparte");
		initLayout("Spartenverwaltung");
		initTable();
		extendTable();
	}

	@Override
	protected void extendTable() {
		table.setVisibleColumns( new Object[] {"spartenname"} );
		table.setColumnHeaders("Sparte");
	}

	@Override
	protected void newBeanWindow() {
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
		btnSaveNewSubject.setStyleName("friendly");
		layout.addComponent(btnSaveNewSubject);
		
		btnSaveNewSubject.addClickListener(event -> {
			beans.addItem(new Subject(txtNewSubject.getValue()));
			ComponentHelper.updateTable(table);
			window.close();
		});
		
		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}
}
