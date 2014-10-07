package model;

import javax.persistence.Entity;

@Entity
public class Group extends AbstractEntity {
	private String gruppenname;
	private Boolean befreit = false;
	
	public Group(String gruppenname, Boolean befreit) {
		super();
		this.gruppenname = gruppenname;
		this.befreit = befreit;
	}
	public String getGruppenname() {
		return gruppenname;
	}
	public void setGruppenname(String gruppenname) {
		this.gruppenname = gruppenname;
	}
	public Boolean getBefreit() {
		return befreit;
	}
	public void setBefreit(Boolean befreit) {
		this.befreit = befreit;
	}
}
