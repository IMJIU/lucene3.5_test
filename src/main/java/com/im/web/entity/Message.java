package com.im.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="t_msg")
public class Message {
	private String content;
	private Date createDate; 
	private int id;
	private String title;
	public Message() {}
	public Message( String title,String content, Date createDate ) {
		super();
		this.content = content;
		this.createDate = createDate;
		this.title = title;
	}
	@Column(name="content",length=1000)
	public String getContent() {
		return content;
	}
	@Column(name="create_date")
	public Date getCreateDate() {
		return createDate;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
