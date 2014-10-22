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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.ComponentHelper;
import de.juette.model.Cycle;
import de.juette.model.HibernateUtil;
import de.juette.model.Log;

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

	@SuppressWarnings("unchecked")
	private void initLayout() {
		setSpacing(true);
		setMargin(true);

		Label title = new Label("Historie");
		title.addStyleName("h1");
		addComponent(title);

		cycles.addAll((Collection<? extends Cycle>) HibernateUtil.getAllAsList(Cycle.class));

		oldCycles.setContainerDataSource(cycles);
		oldCycles.setItemCaptionPropertyId("anzeigename");
		addComponent(oldCycles);
		
		Button saveFile = new Button("Download");
		saveFile.setIcon(FontAwesome.DOWNLOAD);
		saveFile.setStyleName("friendly");
		addComponent(saveFile);

		oldCycles.addValueChangeListener(event -> {
			if (oldCycles.getValue() != null) {
				String basepath = VaadinService.getCurrent().getBaseDirectory()
						.getAbsolutePath();
				Cycle cycle = (Cycle)oldCycles.getValue();
				byte[] bFile = cycle.getDatei();
				String filepath = basepath + "/WEB-INF/Files/" + cycle.getDateiname();
				 
		        try{
		            FileOutputStream fos = new FileOutputStream(filepath); 
		            fos.write(bFile);
		            fos.close();
		        }catch(Exception e){
		            e.printStackTrace();
		        }
				Resource res = new FileResource(new File(filepath));
				FileDownloader fd = new FileDownloader(res);
				fd.extend(saveFile);
			}
		});

		addComponent(tblLog);
	}

	@SuppressWarnings("unchecked")
	private void initTable() {
		logEntrys.addAll((Collection<? extends Log>) HibernateUtil.getAllAsList(Log.class));
		logEntrys.addNestedContainerProperty("bearbeiter.fullName");

		tblLog.setContainerDataSource(logEntrys);
		tblLog.setSelectable(true);
		tblLog.setImmediate(true);
		tblLog.setVisibleColumns(new Object[] { "timestamp", "beschreibung",
				"bearbeiter.fullName" });
		tblLog.setColumnHeaders("Zeitpunkt", "Beschreibung", "Bearbeiter");
		tblLog.setWidth("100%");
		tblLog.setSortContainerPropertyId("timestamp");
		tblLog.setSortAscending(false);

		ComponentHelper.updateTable(tblLog);
	}
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
