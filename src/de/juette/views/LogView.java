package de.juette.views;

import java.io.File;
import java.io.FileOutputStream;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.juette.model.CourseOfYear;
import de.juette.model.HibernateUtil;
import de.juette.model.Log;

public class LogView extends EditableTable<Log> implements View {

	private static final long serialVersionUID = -1649381071729669860L;
	private HorizontalLayout innerHeadLayout = new HorizontalLayout();

	private final ComboBox oldCourses = new ComboBox();
	private TextField txtMaxItems = new TextField();

	private BeanItemContainer<CourseOfYear> courses = new BeanItemContainer<>(
			CourseOfYear.class);

	public LogView() {
		btnChange.setVisible(false);
		btnNew.setVisible(false);

		VerticalLayout headContentLayout = new VerticalLayout();
		headContentLayout.setSpacing(true);
		headContentLayout.addComponents(initOldCourses(), initFilter());
		innerHeadLayout.addComponent(headContentLayout);

		innerHeadLayout.setSpacing(true);
		filterLayout = innerHeadLayout;

		beans = new BeanItemContainer<>(Log.class);
		beans.addAll(HibernateUtil.getMaxList(Log.class,
				Integer.parseInt(txtMaxItems.getValue()), "timestamp desc"));

		initLayout("Historie");
		getTableData();
		initTable();
		extendTable();
	}

	private void getTableData() {
		beans.removeAllItems();
		beans.addAll(HibernateUtil.getMaxList(Log.class,
				Integer.parseInt(txtMaxItems.getValue()), "timestamp desc"));
		table.refreshRowCache();
		updateTable();
	}

	private HorizontalLayout initFilter() {
		HorizontalLayout fLayout = new HorizontalLayout();
		fLayout.setSpacing(true);

		Label maxCount = new Label("<strong>Anzahl Datensätze:</strong>",
				ContentMode.HTML);
		fLayout.addComponent(maxCount);

		txtMaxItems.setInputPrompt("Anzahl");
		txtMaxItems.setWidth(50, Unit.PIXELS);
		txtMaxItems.setConverter(Integer.class);
		txtMaxItems.setStyleName("tiny");
		txtMaxItems
				.setConversionError("Bitte nur eine Ganzzahlige Zahl eingeben.");
		txtMaxItems.setNullRepresentation("");
		txtMaxItems.setNullSettingAllowed(false);
		txtMaxItems.setValue("20");
		fLayout.addComponent(txtMaxItems);

		txtMaxItems.addBlurListener(event -> {
			if (tryParseInt(txtMaxItems.getValue())) {
				getTableData();
			}
		});

		Label title = new Label("<strong>Filter:</strong>", ContentMode.HTML);
		fLayout.addComponent(title);

		TextField txtFilter = new TextField();
		txtFilter.setInputPrompt("Beschreibung");
		txtFilter.setConverter(String.class);
		txtFilter.setStyleName("tiny");
		txtFilter.setNullRepresentation("");
		fLayout.addComponent(txtFilter);

		return fLayout;
	}

	private Boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	protected void extendTable() {
		table.removeAllActionHandlers();
		table.setVisibleColumns(new Object[] { "timestamp", "description",
				"changedMember", "editor" });
		table.setColumnHeaders("Zeitpunkt", "Beschreibung", "Mitglied",
				"Bearbeiter");
		table.setWidth("90%");
		table.setColumnExpandRatio("timestamp", (float) 0.15);
		table.setColumnExpandRatio("description", (float) 0.6);
		table.setColumnExpandRatio("editor", (float) 0.2);
	}

	private HorizontalLayout initOldCourses() {
		HorizontalLayout cLayout = new HorizontalLayout();
		cLayout.setSpacing(true);

		courses.addAll(HibernateUtil.getAllAsList(CourseOfYear.class));
		oldCourses.setContainerDataSource(courses);
		oldCourses.setItemCaptionPropertyId("displayName");
		oldCourses.setStyleName("tiny");
		oldCourses.setInputPrompt("Auswertungen der vergangenen Jahresläufe");
		oldCourses.setWidth(300, Unit.PIXELS);
		cLayout.addComponent(oldCourses);

		Button saveFile = new Button("Download");
		saveFile.setIcon(FontAwesome.DOWNLOAD);
		saveFile.setStyleName("tiny friendly");
		cLayout.addComponent(saveFile);

		oldCourses.addValueChangeListener(event -> {
			if (oldCourses.getValue() != null) {
				String basepath = VaadinService.getCurrent().getBaseDirectory()
						.getAbsolutePath();
				CourseOfYear coy = (CourseOfYear) oldCourses.getValue();
				byte[] bFile = coy.getFile();
				String filepath = basepath + "/WEB-INF/Files/"
						+ coy.getFilename();

				try {
					FileOutputStream fos = new FileOutputStream(filepath);
					fos.write(bFile);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Resource res = new FileResource(new File(filepath));
				FileDownloader fd = new FileDownloader(res);
				fd.extend(saveFile);
			}
		});
		return cLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	protected void newBeanWindow() {
		// Not needed here
	}

}
