package de.juette.dlsa;

import java.util.Date;

import org.joda.time.DateTime;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.juette.model.HibernateUtil;

public class GeneralHandler {

	public static Boolean isRefDateValid(Date dfRefDate) {
		DateTime dtLastCoy = new DateTime();
		dtLastCoy = dtLastCoy.minusYears(100);
		try {
			dtLastCoy = new DateTime (HibernateUtil.getLastCOYDate());
		} catch (NoCOYAvailableException ex) {  }
		return validateRefDate(dtLastCoy, dfRefDate);
	}
	
	public static Boolean validateRefDate(DateTime dtLastCoy, Date dfRefDate) {
		return (!dtLastCoy.equals(dfRefDate) && dtLastCoy.isBefore(new DateTime(dfRefDate)));
	}
	
	public static void showNoVaildRefDateException() {
		Notification.show("Das Bezugsdatum kann nicht in oder vor einem abgeschlossenem Jahreslaufzeitraum liegen.", Type.ERROR_MESSAGE);
		System.out.println("Das Bezugsdatum kann nicht in oder vor einem abgeschlossenem Jahreslaufzeitraum liegen.");
	}
	
	public static Label getNoGuestLabel() {
		return new Label("<strong>Diese Funktion ist nicht für Ihre freigeschaltet</strong>", ContentMode.HTML);
	}
}
