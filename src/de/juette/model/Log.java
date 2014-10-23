package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Historie")
public class Log extends AbstractEntity {
	private Date timestamp;
	@Column(name = "beschreibung")
	private String description;

	@ManyToOne
	private Member editor;

	public Log() {

	}

	public Log(Date timestamp, String description, Member editor) {
		this.timestamp = timestamp;
		this.description = description;
		this.editor = editor;
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

}
