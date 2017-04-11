package com.im.web.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_menu")
public class Menu {
	private boolean leaf;
	private int menuId;
	private String menuName;
	private String ord;
	private String parentId;
	private String template;
	private String moduleId;
	
	@Column(name = "MODULE_ID", nullable = true)
	public String getModuleId() {
		return moduleId;
	}
	@Column(name = "MODULE_CLASS", nullable = true)
	public String getModuleClass() {
		return moduleClass;
	}

	private String moduleClass;
	public Menu(){}

	@Id
	@Column(name = "MENU_ID")
	@GeneratedValue
	public int getMenuId() {
		return menuId;
	}
	
	@Column(name = "MENU_NAME", nullable = true)
	public String getMenuName() {
		return menuName;
	}
	@Column(name = "ORD", nullable = true)
	public String getOrd() {
		return ord;
	}
	@Column(name = "PARENT_ID", nullable = true)
	public String getParentId() {
		return parentId;
	}
	@Column(name = "TEMPLATE")
	public String getTemplate() {
		return template;
	}
	@Column(name = "LEAF")
	public boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public void setOrd(String order) {
		this.ord = order;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public void setModuleClass(String moduleClass) {
		this.moduleClass = moduleClass;
	}
}
