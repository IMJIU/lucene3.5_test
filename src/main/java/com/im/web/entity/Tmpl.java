package com.im.web.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_tmpl")
public class Tmpl {
	private int tmplId;
	private String realName;
	private String virName;
	private String path;
	private String type;
	private String contentType;
	private String createTime;
	private long size;
	public Tmpl(){}
	@Column(name = "PATH", nullable = true)
	public String getPath() {
		return path;
	}
	@Column(name = "REAL_NAME", nullable = true)
	public String getRealName() {
		return realName;
	}
	@Column(name = "FILE_SIZE", nullable = true)
	public long getSize() {
		return size;
	}
	@Id
	@Column(name = "TMPL_ID")
	public int getTmplId() {
		return tmplId;
	}
	@Column(name = "TYPE", nullable = true)
	public String getType() {
		return type;
	}
	@Column(name = "VIR_NAME", nullable = true)
	public String getVirName() {
		return virName;
	}
	@Column(name = "CONTENT_TYPE", nullable = true)
	public String getContentType() {
		return contentType;
	}
	@Column(name = "CREATE_TIME", nullable = true)
	public String getCreateTime() {
		return createTime;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setTmplId(int tmplId) {
		this.tmplId = tmplId;
	}

	
	public void setType(String type) {
		this.type = type;
	}
	public void setVirName(String virName) {
		this.virName = virName;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
