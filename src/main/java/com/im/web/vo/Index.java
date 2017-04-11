package com.im.web.vo;

import java.util.Date;

public class Index {
	//留言ID 如果通过attach搜索的数据需要传递的field是parentId
	private int msgId;
	//带高亮标题
	private String title;
	//带高亮的留言摘要
	private String summary;
	//创建时间
	private Date createDate;
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
