package Views;

import java.io.File;

import model.Cycle;
import model.Log;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.ComponentHelper;

@SuppressWarnings("serial")
public class LogView extends VerticalLayout implements View {

	private final Table tblLog = new Table();
	private BeanItemContainer<Log> logEntrys = new BeanItemContainer<>(
			Log.class);
	private final ComboBox oldCycles = new ComboBox(
			"Auswertungen der vergangenen Jahresläufe");
	private BeanItemContainer<Cycle> cycles = new BeanItemContainer<>(
			Cycle.class);

	public LogView() {
		initLayout();
		initTable();
	}

	private void initLayout() {
		setSpacing(true);
		setMargin(true);

		Label title = new Label("Historie");
		title.addStyleName("h1");
		addComponent(title);

		cycles = ComponentHelper.getDummyCycles();

		oldCycles.setContainerDataSource(cycles);
		oldCycles.setItemCaptionPropertyId("anzeigename");
		addComponent(oldCycles);

		Button saveFile = new Button("Download");
		saveFile.setIcon(FontAwesome.DOWNLOAD);
		saveFile.setStyleName("friendly");
		addComponent(saveFile);

		oldCycles.addValueChangeListener(event -> {
			String basepath = VaadinService.getCurrent().getBaseDirectory()
					.getAbsolutePath();
			Resource res = new FileResource(new File(basepath
					+ "/WEB-INF/Files/ExampleResult.csv"));
			FileDownloader fd = new FileDownloader(res);
			fd.extend(saveFile);
		});

		addComponent(tblLog);
	}

	private void initTable() {
		logEntrys = ComponentHelper.getDummyLog();
		logEntrys.addNestedContainerProperty("bearbeiter.fullName");

		tblLog.setContainerDataSource(logEntrys);
		tblLog.setSelectable(true);
		tblLog.setImmediate(true);
		tblLog.setVisibleColumns(new Object[] { "timestamp", "beschreibung",
				"bearbeiter.fullName" });
		tblLog.setColumnHeaders("Zeitpunkt", "Beschreibung", "Bearbeiter");
		tblLog.setWidth("100%");

		ComponentHelper.updateTable(tblLog);
	}
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
