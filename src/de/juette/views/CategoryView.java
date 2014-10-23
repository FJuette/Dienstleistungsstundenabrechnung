package de.juette.views;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.Category;
import de.juette.model.HibernateUtil;

@SuppressWarnings("serial")
public class CategoryView extends EditableTable<Category> implements View {

	@SuppressWarnings("unchecked")
	public CategoryView() {
		beans = new BeanItemContainer<>(Category.class);
		beans.addAll((Collection<? extends Category>) HibernateUtil.getAllAsList(Category.class));
		
		btnNew.setCaption("Neue Sparte");
		initLayout("Spartenverwaltung");
		initTable();
		extendTable();
	}

	@Override
	protected void extendTable() {
		table.setVisibleColumns( new Object[] {"categoryName"} );
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
		
		TextField txtNewCategory = new TextField("Name");
		txtNewCategory.setWidth("100%");
		layout.addComponent(txtNewCategory);
				
		Button btnSaveNewSubject = new Button("Speichern");
		btnSaveNewSubject.setStyleName("friendly");
		layout.addComponent(btnSaveNewSubject);
		
		btnSaveNewSubject.addClickListener(event -> {
			beans.addItem(new Category(txtNewCategory.getValue()));
			updateTable();
			HibernateUtil.saveAll(beans.getItemIds());
			window.close();
		});
		
		getUI().addWindow(window);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
	}
}
