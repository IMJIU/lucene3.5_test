package com.im.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="t_attach")
public class Attachment {
	private int id;
	private String contentType;
	private String newName;
	private String oldName;
	private Date createDate;
	private Message message;
	public Attachment(){	}
	public Attachment(int id, String contentType, String newName,
			String oldName, Date createDate, Message message) {
		super();
		this.id = id;
		this.contentType = contentType;
		this.newName = newName;
		this.oldName = oldName;
		this.createDate = createDate;
		this.message = message;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	@ManyToOne
	@JoinColumn(name="p_id")
	public Message getMessage() {
		return message;
	}
	@Column(name="content_type")
	public String getContentType() {
		return contentType;
	}
	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}
	
	
	public String getNewName() {
		return newName;
	}
	@Column(name="new_name")
	public String getOldName() {
		return oldName;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	
}
