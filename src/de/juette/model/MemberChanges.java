package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.joda.time.*;

@Entity
public class MemberChanges extends AbstractEntity {
	private Date timestamp = DateTime.now().toDate();
	
	@Column(name = "bezugsdatum")
	private Date refDate;

	@Column(name = "spaltenname")
	private String column;

	@Column(name = "alterWert")
	private String oldValue;
	
	@Column(name = "neuerWert")
	private String newValue;
	
	private Long memberId;
	
	public String getMemberName() {
		Member m = (Member) HibernateUtil.getUnique(Member.class, "id = " + memberId);
		return m.getFullName() + " (" + m.getMemberId() + ")";
	}
	
	public String getColumnName() {
		if (column.equals(MemberColumn.ACTIVE.toString())) {
			return "Aktiv";
		} else if (column.equals(MemberColumn.ENTRYDATE.toString())) {
			return "Eintrittsdatum";
		} else if (column.equals(MemberColumn.LEAVINGDATE.toString())) {
			return "Austrittsdatum";
		} else if (column.equals(MemberColumn.GROUP.toString())) {
			return "Funktionsgruppe";
		} else {
			return "";
		}
	}
	
	public String getFormattedNewValue() {
		if (column.equals(MemberColumn.ENTRYDATE.toString())) {
			return newValue;
		} else if (column.equals(MemberColumn.LEAVINGDATE.toString())) {
			return newValue;
		} else if (column.equals(MemberColumn.GROUP.toString())) {
			Collection<Group> groups = new ArrayList<Group>();
			String[] ids = newValue.trim().split(" ");
			for (String id : ids) {
				if (id != null && !id.equals("")) {
					groups.add((Group) HibernateUtil.getUnique(Group.class, "id = " + id));
				}
			}
			String res = "";
			for (Group g : groups) {
				res += g.getGroupName() + " ;";
			}
			if (res.length() > 0) 
				return res.substring(0, res.lastIndexOf(";"));
			else
				return "";
		} else {
			return newValue;
		}
	}
	
	public String getFormattedOldValue() {
		if (column.equals(MemberColumn.ENTRYDATE.toString())) {
			return oldValue;
		} else if (column.equals(MemberColumn.LEAVINGDATE.toString())) {
			return oldValue;
		} else if (column.equals(MemberColumn.GROUP.toString())) {
			Collection<Group> groups = new ArrayList<Group>();
			String[] ids = oldValue.trim().split(" ");
			for (String id : ids) {
				if (id != null && !id.equals("")) {
					groups.add((Group) HibernateUtil.getUnique(Group.class, "id = " + id));
				}
			}
			String res = "";
			for (Group g : groups) {
				res += g.getGroupName() + " ;";
			}
			if (res.length() > 0) 
				return res.substring(0, res.lastIndexOf(";"));
			else
				return "";
			
		} else {
			return oldValue;
		}
	}

	public Date getRefDate() {
		return refDate;
	}

	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
