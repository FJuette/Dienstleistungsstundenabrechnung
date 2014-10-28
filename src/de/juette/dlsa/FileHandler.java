package de.juette.dlsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.juette.model.ColumnMapping;
import de.juette.model.CsvColumn;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

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

	public List<CsvColumn> getColumnNames() {
		BufferedReader br = null;
		String cvsSplitBy = ";";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), getCharset()));
			String line = br.readLine();
			String[] columns = line.split(cvsSplitBy);

			List<CsvColumn> list = new ArrayList<CsvColumn>();
			for (int i = 0; i < columns.length; i++) {
				if (!columns[i].equals(""))
					list.add(new CsvColumn(i, columns[i]));
			}
			return list;

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
		return new ArrayList<CsvColumn>();
	}

	public void uploadMembers(List<ColumnMapping> mapping) {
		BufferedReader br = null;
		String cvsSplitBy = ";";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), getCharset()));
			// Header line
			String line = br.readLine();

			List<String[]> lines = new ArrayList<String[]>();

			while ((line = br.readLine()) != null) {
				lines.add(line.split(cvsSplitBy));
			}
			for (String[] entry : lines) {
				String id = "";
				for (ColumnMapping mapp : mapping) {
					if (mapp.getDbColumnName().equals("memberId")) {
						id = entry[mapp.getCsvColumnIndex()];
					}
				}
				Member m = (Member) HibernateUtil.getUnique(Member.class,
						"memberId = '" + id + "'");
				if (m == null) {
					m = new Member();
				}
				for (ColumnMapping mapp : mapping) {
					if (mapp.getDbColumnName().equals("surname")) {
						m.setSurname(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("forename")) {
						m.setForename(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("memberId")) {
						m.setMemberId(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("entryDate")) {
						try {
							m.setEntryDate(new SimpleDateFormat("dd.MM.yyyy")
									.parse(entry[mapp.getCsvColumnIndex()]));
						} catch (ParseException e) {
							//e.printStackTrace();
						}
					} else if (mapp.getDbColumnName().equals("leavingDate")) {
						try {
							m.setLeavingDate(new SimpleDateFormat("dd.MM.yyyy")
									.parse(entry[mapp.getCsvColumnIndex()]));
						} catch (ParseException e) {
							//e.printStackTrace();
						}
					}
				}
				HibernateUtil.save(m);
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
	}
}
