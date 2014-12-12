package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class GroupChanges extends AbstractEntity {
	@Column(name = "bezugsdatum")
	private Date refDate;
	
	@Column(name = "alterWert")
	private Boolean oldValue;
	
	@Column(name = "neuerWert")
	private Boolean newValue;
	
	private Long groupId;

	public Date getRefDate() {
		return refDate;
	}

	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Boolean getOldValue() {
		return oldValue;
	}

	public void setOldValue(Boolean oldValue) {
		this.oldValue = oldValue;
	}

	public Boolean getNewValue() {
		return newValue;
	}

	public void setNewValue(Boolean newValue) {
		this.newValue = newValue;
	}
}
