package de.juette.model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class MemberLog extends AbstractEntity {

	private Long refMemberId;
	private boolean liberated;
	private Date refDate;
	
	public Long getRefMemberId() {
		return refMemberId;
	}
	public void setRefMemberId(Long refMemberId) {
		this.refMemberId = refMemberId;
	}
	public boolean isLiberated() {
		return liberated;
	}
	public void setLiberated(boolean liberated) {
		this.liberated = liberated;
	}
	public Date getRefDate() {
		return refDate;
	}
	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}
}
