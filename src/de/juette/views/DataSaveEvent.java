package de.juette.views;

import com.vaadin.data.util.BeanItem;

import de.juette.model.AbstractEntity;

public class DataSaveEvent<T extends AbstractEntity> {
	final BeanItem<T> beanItem;
	
	public DataSaveEvent(BeanItem<T> beanItem) {
		this.beanItem = beanItem;
	}
	
	public BeanItem<T> getBeanItem() {
		return beanItem;
	}
}