package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Historie")
public class Log extends AbstractEntity {
	@Column(name = "zeitstempel")
	private Date timestamp = new Date();
	@Column(name = "beschreibung")
	private String description;
	@Column(name = "bearbeiter")
	private String editor;
	@Column(name = "veraendertesMitglied")
	private String changedMember;
	@Column(name = "mitgliedId")
	private long changedMemberId;
	@Column(name = "bezugsdatum")
	private Date referenceDate;
	private Long mLogId;
	
	public Log() {
		
	}

	public Log(Date timestamp, String description, String editor,
			String changedMember) {
		this.timestamp = timestamp;
		this.description = description;
		this.editor = editor;
		this.changedMember = changedMember;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getChangedMember() {
		return changedMember;
	}

	public void setChangedMember(String changedMember) {
		this.changedMember = changedMember;
	}

	public long getChangedMemberId() {
		return changedMemberId;
	}

	public void setChangedMemberId(long changedMemberId) {
		this.changedMemberId = changedMemberId;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Long getmLogId() {
		return mLogId;
	}

	public void setmLogId(Long mLogId) {
		this.mLogId = mLogId;
	}

}
