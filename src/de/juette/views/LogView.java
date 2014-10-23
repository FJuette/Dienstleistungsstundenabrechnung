package de.juette.views;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import de.juette.model.CourseOfYear;
import de.juette.model.HibernateUtil;
import de.juette.model.Log;

@SuppressWarnings("serial")
public class LogView extends EditableTable<Log> implements View {

	private final ComboBox oldCourses = new ComboBox(
			"Auswertungen der vergangenen Jahresläufe");
	private BeanItemContainer<CourseOfYear> courses = new BeanItemContainer<>(
			CourseOfYear.class);

	@SuppressWarnings("unchecked")
	public LogView() {
		beans = new BeanItemContainer<>(Log.class);
		beans.addAll((Collection<? extends Log>) HibernateUtil
				.getAllAsList(Log.class));
		beans.addNestedContainerProperty("editor.fullName");

		btnChange.setVisible(false);
		btnNew.setVisible(false);
		extendLayout = initOldCourses();

		initLayout("Historie");
		initTable();
		extendTable();
	}

	@Override
	protected void extendTable() {
		table.removeAllActionHandlers();
		table.setVisibleColumns(new Object[] { "timestamp", "description",
				"editor.fullName" });
		table.setColumnHeaders("Zeitpunkt", "Beschreibung", "Bearbeiter");
		table.setWidth("100%");
		table.setSortContainerPropertyId("timestamp");
		table.setSortAscending(false);
	}

	@SuppressWarnings("unchecked")
	private VerticalLayout initOldCourses() {
		VerticalLayout cLayout = new VerticalLayout();
		
		courses.addAll((Collection<? extends CourseOfYear>) HibernateUtil.getAllAsList(CourseOfYear.class));
		oldCourses.setContainerDataSource(courses);
		oldCourses.setItemCaptionPropertyId("displayName");
		cLayout.addComponent(oldCourses);

		Button saveFile = new Button("Download");
		saveFile.setIcon(FontAwesome.DOWNLOAD);
		saveFile.setStyleName("friendly");
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
