package com.im.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.im.web.util.IndexUtil;

@Entity
@Table(name="tmp_index")
public class TempIndex {
	private int id;
	private int objId;
	private int operator;
	private String type;
	
	public TempIndex() {}
	
	public TempIndex( int objId, String type, int operator) {
		super();
		this.objId = objId;
		this.type = type;
		this.operator = operator;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	@Column(name="obj_id")
	public int getObjId() {
		return objId;
	}
	@Column(name="opt")
	public int getOperator() {
		return operator;
	}
	@Transient
	public String getType() {
		return type;
	}
	public void setAdd(){
		operator = IndexUtil.ADD_OP;
	}
	public void setDelete(){
		operator = IndexUtil.DEL_OP;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setUpt(){
		operator = IndexUtil.UPT_OP;
	}

}
