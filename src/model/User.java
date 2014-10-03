package model;

public class User {
	private String benutzername;
	private String passwort;
	private Boolean aktiv = true;
	private Role rolle;
	
	public User(String benutzername, String passwort, Boolean aktiv, Role rolle) {
		super();
		this.benutzername = benutzername;
		this.passwort = passwort;
		this.aktiv = aktiv;
		this.rolle = rolle;
	}
	public String getBenutzername() {
		return benutzername;
	}
	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}
	public String getPasswort() {
		return passwort;
	}
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	public Boolean getAktiv() {
		return aktiv;
	}
	public void setAktiv(Boolean aktiv) {
		this.aktiv = aktiv;
	}
	public Role getRolle() {
		return rolle;
	}
	public void setRolle(Role rolle) {
		this.rolle = rolle;
	}
}
