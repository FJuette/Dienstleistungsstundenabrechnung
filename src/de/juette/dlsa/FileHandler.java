package de.juette.dlsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.juette.model.Category;
import de.juette.model.ColumnMapping;
import de.juette.model.CourseOfYear;
import de.juette.model.CsvColumn;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.MemberChanges;
import de.juette.model.MemberColumn;

/**
 * This class handles all operations with files on the system
 * @author Fabian Juette
 */
public class FileHandler implements Receiver, SucceededListener {
	
	private static final long serialVersionUID = -7755862096764130729L;
	protected File file;
	private final DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final String basepath = VaadinService.getCurrent().getBaseDirectory()
			.getAbsolutePath()
			+ "/WEB-INF/Files";

	public File getFile() {
		return file;
	}

	/**
	 * Add your own event listener for the upload to do further actions
	 * Needed here if the event do not get overwritten
	 */
	@Override
	public void uploadSucceeded(SucceededEvent event) {
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			file = new File(basepath + "/" + filename);
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

	/**
	 * Tries to get the encoding from a text file
	 * @return Encoding as String
	 */
	private String getCharset() {
		String charset = "UTF-8"; // Default encoding
		byte[] fileContent = null;
		if (file != null) {
			try {
				FileInputStream fin = new FileInputStream(file.getPath());
				fileContent = new byte[(int) file.length()];
				fin.read(fileContent);
				fin.close();
				// Detect the encoding form the file, if not possible use the default
				CharsetDetector detector = new CharsetDetector();
				detector.setText(fileContent);
				CharsetMatch cm = detector.detect();

				if (cm != null) {
					int confidence = cm.getConfidence();
					// Change the confidence if it should be more accurate
					if (confidence > 30) {
						charset = cm.getName();
						System.out.println("Erkanntes Charset: " + charset);
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("Charset der Datei: " + charset);
		return charset;
	}

	/**
	 * Get all column names from a CSV file
	 * @return List of all column names
	 */
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

	/**
	 * Uploads every member from the text file in the database
	 * @param mapping entries from the database
	 */
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
						"memberId = '" + id + "' and aikz = " + true);
				Boolean newMember = true;
				if (m == null) {
					m = new Member();
				} else {
					newMember = false;
				}
				for (ColumnMapping mapp : mapping) {
					if (mapp.getDbColumnName().equals("surname")) {
						if (!(mapp.getCsvColumnIndex() == -1))
							m.setSurname(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("forename")) {
						if (!(mapp.getCsvColumnIndex() == -1))
							m.setForename(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("memberId")) {
						if (!(mapp.getCsvColumnIndex() == -1))
							m.setMemberId(entry[mapp.getCsvColumnIndex()]);
					} else if (mapp.getDbColumnName().equals("entryDate")) {
						if (!(mapp.getCsvColumnIndex() == -1)) {
							try {
								DateTime dt = dateStringFormat.parseDateTime(entry[mapp.getCsvColumnIndex()]);
								if (!newMember) {
									if (!dt.isEqual(new DateTime(m.getEntryDate()))) {
										MemberChanges mc = new MemberChanges();
										mc.setMemberId(m.getId());
										mc.setColumn(MemberColumn.ENTRYDATE.toString());
										if (m.getEntryDate() != null)
											mc.setOldValue(new DateTime(m.getEntryDate()).toString("dd.MM.yyyy"));
										mc.setNewValue(new DateTime(dt.toDate()).toString("dd.MM.yyyy"));
										mc.setRefDate(DateTime.now().toDate());
										HibernateUtil.save(mc);
										m.setEntryDate(dt.toDate());
									}
								} else {
									m.setEntryDate(dt.toDate());
								}
							} catch (Exception e) {
								//e.printStackTrace();
							}
						}
					} else if (mapp.getDbColumnName().equals("leavingDate")) {
						if (!(mapp.getCsvColumnIndex() == -1)) {
							try {
								DateTime dt = dateStringFormat.parseDateTime(entry[mapp.getCsvColumnIndex()]);
								if (!newMember) {
									if (!dt.isEqual(new DateTime(m.getLeavingDate()))) {
										MemberChanges mc = new MemberChanges();
										mc.setMemberId(m.getId());
										mc.setColumn(MemberColumn.LEAVINGDATE.toString());
										if (m.getLeavingDate() != null)
											mc.setOldValue(new DateTime(m.getLeavingDate()).toString("dd.MM.yyyy"));
										mc.setNewValue(new DateTime(dt.toDate()).toString("dd.MM.yyyy"));
										mc.setRefDate(DateTime.now().toDate());
										HibernateUtil.save(mc);
										m.setLeavingDate(dt.toDate());
									}
								} else {
									m.setLeavingDate(dt.toDate());
								}
							} catch (Exception e) {
								//e.printStackTrace();
							}
						}
					} else if (mapp.getDbColumnName().equals("birthdate")) {
						if (!(mapp.getCsvColumnIndex() == -1)) {
							try {
								m.setBirthdate(new SimpleDateFormat("dd.MM.yyyy")
										.parse(entry[mapp.getCsvColumnIndex()]));
							} catch (ParseException e) {
								//e.printStackTrace();
							}
						}
					} else if (mapp.getDbColumnName().equals("categoryName")) {
						if (!(mapp.getCsvColumnIndex() == -1)) {
							List<Category> categories = HibernateUtil.getAllAsList(Category.class);
							Category category = new Category();
							for (Category c : categories) {
								if (c.getCategoryName().equals(entry[mapp.getCsvColumnIndex()])) {
									category = c;
								}
							}
							List<Category> mCategories = new ArrayList<Category>();
							if (category.getCategoryName() == null || category.getCategoryName().equals("")) {
								category.setCategoryName(entry[mapp.getCsvColumnIndex()]);
							}
							mCategories.add(category);
							m.setCategories(mCategories);
						}
					}
				}
				if (newMember) {
					HibernateUtil.saveNewMember(m);
				} else {
					HibernateUtil.save(m);
				}
				
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
		System.out.println("Mitglieder aus der CSV Datei eingespielt");
	}
	
	public File writeCsvFile(Date date, List<String> lines, Boolean finalize, Date dueDate) {
		String filename = new SimpleDateFormat("yyyy-MM-dd").format(date) + "_Jahreslauf.csv";
		String filepath = basepath + "/" + filename;
		File f = new File(basepath);
		if (!f.exists()) {
			f.mkdir();
		}
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(
				    new FileOutputStream(filepath), "UTF-8"));
			for (String line : lines) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch (IOException e) {
			Notification.show("", e.getMessage(), Type.ERROR_MESSAGE);
		}
		File file = new File(filepath);
		byte[] bFile = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			// convert file into array of bytes
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Write the file in the database because it is the final run
		if (finalize) {
			CourseOfYear coy = new CourseOfYear(bFile, new Date(),
					"Jahreslauf vom " + new SimpleDateFormat("dd.MM.yyyy").format(date),
					filename + ".csv", dueDate);
			HibernateUtil.save(coy);
		}
		System.out.println("Jahreslaufdatei geschrieben");
		return file;
	}
}
