package de.juette.dlsa.layout;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.FormLayout;

import de.juette.dlsa.DataSaveEvent;
import de.juette.dlsa.DataSaveListener;
import de.juette.model.AbstractEntity;

public abstract class DataTabLayout<T extends AbstractEntity> extends FormLayout {

	private static final long serialVersionUID = 4724189679115180836L;
	protected BeanItem<T> beanItem;
	private List<DataSaveListener> listeners = null;
	protected FieldGroup fieldGroup;

	protected void fireDataSavedEvent() {
		if (listeners != null) {
			DataSaveEvent<T> event = new DataSaveEvent<T>(beanItem);
			for (DataSaveListener listener : listeners) {
				listener.dataSaved(event);
			}
		}
	}
	
	public void addDataSaveListener(DataSaveListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<DataSaveListener>();
		}
		listeners.add(listener);
	}
	
	public void removeDataSaveListener(DataSaveListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<DataSaveListener>();
		}
		listeners.remove(listener);
	}
}
