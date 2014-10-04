package Views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CsvImportWindow extends Window {
	public CsvImportWindow() {
		setCaption("Einlesen der Mitglieder von einer CSV-Datei");
		setModal(true);
		setHeight("170");
		setWidth("400");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		FileUploader reciever = new FileUploader();
		// Create the upload with a caption and set reciever later
		Upload upload = new Upload("", reciever);
		upload.setButtonCaption("Einlesen");
		upload.addSucceededListener(reciever);
				
		layout.addComponent(upload);
	}
	
	class FileUploader implements Receiver, SucceededListener {
		
		public File file;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			// Create upload stream
			FileOutputStream fos = null; // Stream to write to
			try {
				// Open the file for writing
				file = new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/Files/" + filename);
				fos = new FileOutputStream(file);
			}
			catch (final FileNotFoundException ex) {
				new Notification("Datei nicht gefunden:<br/>", 
						ex.getMessage(), 
						Notification.Type.ERROR_MESSAGE);
				return null;
			} 
			catch (final Exception ex) {
				new Notification("Fehler:<br/>", 
						ex.getMessage(), 
						Notification.Type.ERROR_MESSAGE);
				return null;
			}
			return fos;
		}
		
		public void uploadSucceeded(SucceededEvent e) {
			close();
		}
	}
	
}
