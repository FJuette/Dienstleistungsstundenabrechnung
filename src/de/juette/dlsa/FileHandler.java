package de.juette.dlsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

@SuppressWarnings("serial")
public class FileHandler implements Receiver, SucceededListener {
	protected File file;
	private String basepath = VaadinService.getCurrent().getBaseDirectory()
			.getAbsolutePath()
			+ "/WEB-INF/Files/";

	public File getFile() {
		return file;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Datei " + file.getName()
				+ " erfolgreich hochgeladen",
				Notification.Type.TRAY_NOTIFICATION);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// Create upload stream
		FileOutputStream fos = null; // Stream to write to
		try {
			// Open the file for writing
			// System.out.println(basepath + filename);
			file = new File(basepath + filename);
			fos = new FileOutputStream(file);
		} catch (final FileNotFoundException ex) {
			new Notification("Datei nicht gefunden:<br/>", ex.getMessage(),
					Notification.Type.ERROR_MESSAGE);
			return null;
		} catch (final Exception ex) {
			new Notification("Fehler:<br/>", ex.getMessage(),
					Notification.Type.ERROR_MESSAGE);
			return null;
		}
		return fos;
	}

	private String getCharset() {
		String charset = "UTF-8"; // Default chartset
		byte[] fileContent = null;
		if (file != null) {
			try {
				FileInputStream fin = new FileInputStream(file.getPath());
				fileContent = new byte[(int) file.length()];
				fin.read(fileContent);
				fin.close();
				CharsetDetector detector = new CharsetDetector();
				detector.setText(fileContent);
				CharsetMatch cm = detector.detect();

				if (cm != null) {
					int confidence = cm.getConfidence();
					System.out.println("Encoding: " + cm.getName()
							+ " - Confidence: " + confidence + "%");
					if (confidence > 30) {
						charset = cm.getName();
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return charset;
	}

	public String[] getColumnNames() {
		BufferedReader br = null;
		String cvsSplitBy = ";";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), getCharset()));
			return br.readLine().split(cvsSplitBy);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new String[] {};
	}
	
	public String[] getContentForColumns(List<String> columns) {
		BufferedReader br = null;
		String cvsSplitBy = ";";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), getCharset()));
			ArrayList<Integer> colPos = new ArrayList<Integer>();
			String line = br.readLine();
			String[] parts = line.split(cvsSplitBy);
			for (int p = 0; p < parts.length; p++) {
				for (String c : columns) {
					if (c != null && c.equals(parts[p])) {
						colPos.add(p);
					}
				}
			}
			while ((line = br.readLine()) != null) {
		        System.out.println(line);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new String[] {};
	}
}
