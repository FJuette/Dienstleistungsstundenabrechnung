package de.juette.views;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class LoginView extends VerticalLayout implements View {

	private static final long serialVersionUID = -1825948076087301147L;
	public LoginView() {
		
	}

	private CheckBox cbRemember = new CheckBox("Angemeldet bleiben", true);
	public Window getLoginWindow() {
		Window window = new Window("");
		window.setModal(true);
		window.setWidth("600");
		window.setHeight("250");
		
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");
		
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");
		Label title = new Label("DLS-Verwaltung Login");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		loginPanel.addComponent(labels);
		
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");
		final TextField username = new TextField("Benutzername");
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		final PasswordField password = new PasswordField("Passwort");
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		final Button signin = new Button("Anmelden");
		signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signin.setClickShortcut(KeyCode.ENTER);
		signin.focus();
		fields.addComponents(username, password, signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);
		signin.addClickListener(event -> {
			UsernamePasswordToken token = new UsernamePasswordToken(
					username.getValue(), password.getValue(), cbRemember.getValue());
			try {
				SecurityUtils.getSubject().login(token);
				System.out.println("Ist in Adminrolle: " + SecurityUtils.getSubject().hasRole("Administrator"));
				window.close();
				UI.getCurrent().getNavigator().navigateTo("member");
			} catch (AuthenticationException ex) {
				Notification.show("Leider war der Login nicht erfolgreich.", Type.ERROR_MESSAGE);
			}
		});
		
		loginPanel.addComponent(fields);
		
		loginPanel.addComponent(cbRemember);

		layout.addComponent(loginPanel);
		return window;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
