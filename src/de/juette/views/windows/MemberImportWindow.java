package de.juette.views.windows;

import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.juette.dlsa.FileHandler;
import de.juette.model.ColumnMapping;
import de.juette.model.HibernateUtil;

@SuppressWarnings("serial")
public class MemberImportWindow extends Window {

	private List<ColumnMapping> mapping;
	
	public MemberImportWindow() {
		setModal(true);
		setWidth("400");
		setCaption("Einlesen der Mitglieder");
		
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		FileHandler reciever = new FileHandler();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload("", reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				mapping = (List<ColumnMapping>) HibernateUtil.getAllAsList(ColumnMapping.class);

				reciever.uploadMembers(mapping);
				close();
			}
		});
		
		if (HibernateUtil.getFilterAsList(ColumnMapping.class, "csvColumnIndex > -1").size() == 0) {
			upload.setEnabled(false);
			layout.addComponent(new Label("<p style=\"color:red;\">Es sind noch keine Spalten in den Einstellungen konfiguriert.</p>", ContentMode.HTML));
		}
		
		layout.addComponent(upload);
	}

	public List<ColumnMapping> getMapping() {
		return mapping;
	}
}
