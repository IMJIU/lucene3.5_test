package com.im.web.entity;

import java.util.Date;

/**
 * 要添加的索引域对象  目的是将相应的对象转换为IndexField之后
 * 传输到IndexService进行更新操作
 * @author root
 *
 */
public class IndexField {
	//索引的唯一标识：如若是留言，使用0_留言ID来表示
	//如果是福建使用 留言ID_附件ID来表示
	private String id;
	//留言标题
	private String title;
	//留言内容
	private String content;
	//当前对象的父类ID，如果是留言，该ID为0
	private int parentId;
	//存储对象的ID
	private int objId;
	//类型
	private String type;
	
	public IndexField(){}
	public IndexField(String id, String title, String content, int parentId,
			int objId, String type, Date createDate) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.parentId = parentId;
		this.objId = objId;
		this.type = type;
		this.createDate = createDate;
	}
	private Date createDate;
	
	public String getId() {
		return id;
	}
	public String getContent() {
		return content;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public int getObjId() {
		return objId;
	}
	public int getParentId() {
		return parentId;
	}
	public String getTitle() {
		return title;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
