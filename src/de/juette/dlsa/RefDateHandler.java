package de.juette.dlsa;

import java.util.Date;

import org.joda.time.DateTime;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.juette.model.HibernateUtil;

public class RefDateHandler {

	public static Boolean isRefDateValid(Date dfRefDate) {
		DateTime dtLastCoy = new DateTime();
		try {
			dtLastCoy = new DateTime (HibernateUtil.getLastCOYDate());
		} catch (NoCOYAvailableException ex) {  }
		return (!dtLastCoy.equals(DateTime.now()) && dtLastCoy.isBefore(new DateTime(dfRefDate)));
	}
	
	public static void showNoVaildRefDateException() {
		Notification.show("Das Bezugsdatum kann nicht in oder vor einem abgeschlossenem Jahreslaufzeitraum liegen.", Type.ERROR_MESSAGE);
	}
}
