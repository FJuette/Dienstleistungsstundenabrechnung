package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Sparten")
public class Category extends AbstractEntity {
	@Column(name = "spartenname")
	private String categoryName;

	@ManyToMany(mappedBy = "categories")
	private Collection<Member> member = new ArrayList<Member>();

	public Category() {

	}

	public Category(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Collection<Member> getMember() {
		return member;
	}

	public void setMember(Collection<Member> member) {
		this.member = member;
	}

}
