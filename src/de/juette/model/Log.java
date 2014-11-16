package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Historie")
public class Log extends AbstractEntity {
	@Column(name = "zeitstempel")
	private Date timestamp;
	@Column(name = "beschreibung")
	private String description;
	@ManyToOne
	private Member editor;
	@ManyToOne
	private Member changedMember;

	public Log() {

	}

	public Log(Date timestamp, String description, Member editor, Member changedMember) {
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

	public Member getEditor() {
		return editor;
	}

	public void setEditor(Member editor) {
		this.editor = editor;
	}

	public Member getChangedMember() {
		return changedMember;
	}

	public void setChangedMember(Member changedMember) {
		this.changedMember = changedMember;
	}

}
