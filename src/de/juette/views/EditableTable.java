package de.juette.views;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.ComponentHelper;
import de.juette.model.AbstractEntity;
import de.juette.model.HibernateUtil;

@SuppressWarnings("serial")
public abstract class EditableTable<T> extends VerticalLayout {

	protected abstract void extendTable();

	protected HorizontalLayout filterLayout;

	protected final Table table = new Table();
	protected BeanItemContainer<T> beans;

	protected final Button btnNew = new Button("Neu");
	protected final Button btnChange = new Button("Bearbeiten");

	private Handler actionHandler = new Handler() {
		private final Action REMOVE = new Action("Entfernen");
		private final Action[] ACTIONS = new Action[] { REMOVE };

		@SuppressWarnings("unchecked")
		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Entfernen")) {
				beans.removeItem(table.getValue());
				HibernateUtil.removeItem(
						(Class<? extends AbstractEntity>) table.getValue()
								.getClass(),
						((AbstractEntity) table.getValue()).getId().toString());
				// saveAll((List<? extends AbstractEntity>) beans.getItemIds());
				ComponentHelper.updateTable(table);
			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private Handler getActionHandler() {
		return actionHandler;
	}

	@SuppressWarnings("unchecked")
	protected void initLayout(String caption) {
		setSpacing(true);
		setMargin(true);

		Label title = new Label(caption);
		title.addStyleName("h1");

		addComponent(title);

		if (filterLayout != null) {
			addComponent(filterLayout);
		}

		addComponent(table);

		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponents(btnChange, btnNew);

		btnChange.addClickListener(event -> {
			try {
				if ("Bearbeiten".equals(btnChange.getCaption())) {
					table.setEditable(true);
					btnChange.setCaption("Speichern");
					btnChange.setStyleName("friendly");
					ComponentHelper.updateTable(table);
				} else {
					table.setEditable(false);
					btnChange.setCaption("Bearbeiten");
					btnChange.setStyleName("normal");
					table.commit();
					HibernateUtil.saveAll((List<? extends AbstractEntity>) table.getItemIds());
					Notification.show("Speichern erfolgreich",
							Notification.Type.TRAY_NOTIFICATION);
					ComponentHelper.updateTable(table);
				}
			} catch (Exception e) {
				Notification.show("Fehler: " + e.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			}
		});

		btnNew.addClickListener(event -> {
			newBeanWindow();
		});
	}

	protected void initTable() {

		table.setContainerDataSource(beans);
		table.setSelectable(true);
		table.setImmediate(true);
		// table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
		table.setWidth("40%");
		table.addActionHandler(getActionHandler());

		ComponentHelper.updateTable(table);
	}

	protected abstract void newBeanWindow();

}
