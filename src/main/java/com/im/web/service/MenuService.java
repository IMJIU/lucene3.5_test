package com.im.web.service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.im.web.base.Page;
import com.im.web.dao.MenuDao;
import com.im.web.entity.Menu;
@Service
public class MenuService {
	@Autowired
	private MenuDao menuDao;
	
	public List<Menu> queryAllMenu() {
		return menuDao.getMenusTabTrip();
	}
	public List<Menu> queryAllMenu(String property,boolean isAsc) {
		return this.menuDao.getAll(property,isAsc);
	}
	/**
	 * 分页查询用户
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<Menu> queryAllMenu(int pageNo, int pageSize) {
		Page<Menu> page = new Page<Menu>(pageSize, pageNo);
		return this.menuDao.getAllMenu(page, new Object[] {});
	}
	/**
	 * 新增菜单
	 * @param user
	 */
	public void addMenu(Menu menu) {
		this.menuDao.saveOrUpdateMenu(menu);
	}
	
	public Menu queryMenuById(String menuId){
		
		return this.menuDao.getMenuByMenuId(menuId);
	}
	
	public void updateMenu(Menu menu){
		this.menuDao.saveOrUpdateMenu(menu);
	}
	
	public void deleteMenu(String menuId){
		this.menuDao.delete(menuId);
	}
	
	public void deleteCascadeMenu(String menuId){
		this.menuDao.deleteCascade(menuId);
	}
	
	public String queryMenuTree(){
		String tree = convertMenusToString(menuDao.getMenusTree());
		return tree;
	}
	/**
	 * 得到转化Ext树
	 * @return
	 */
	public String getMenuTree(){
		List<Menu> list = menuDao.getMenusTree();
		String tree = convertMenusToExtTree(list);
//		try {
//			return new String(tree.getBytes("GBK"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return tree;
	}
	public void dealMenuOrderString(String json){
		List<JSONObject>list2 = JsonParser.parseJsonArray(json);
		int order = 0;
		for (JSONObject jsonObject : list2) {
			String menuId = jsonObject.getString("menuId");
			Menu menu = menuDao.get(menuId);
			menu.setOrd(""+order);
			menuDao.saveOrUpdateMenu(menu);
			if(!jsonObject.getBoolean("leaf")){
				iteratorList(jsonObject.getJSONArray("items"),order+1);
			}
		}
	}
	public void iteratorList(JSONArray list,int order){
		for (int i = 0; i < list.size(); i++) {
			JSONObject jsonObject = list.getJSONObject(i);
			String menuId = jsonObject.getString("menuId");
			Menu menu = menuDao.get(menuId);
			menu.setOrd(""+order);
			menuDao.saveOrUpdateMenu(menu);
			if(!jsonObject.getBoolean("leaf")){
				iteratorList(jsonObject.getJSONArray("items"),order*10);
			}
			order++;
		}
		
	}
	/**
	 * Ext tree
	 * @param list
	 * @return
	 */
	public String convertMenusToExtTree(List<Menu> list){
		StringBuilder sql = new StringBuilder();
		sql.append("[");
		Menu pre = null;
		Menu menu = null;
		int level = 0;
		for (int i = 1; i < list.size(); i++) {
			 menu = list.get(i);
			boolean leaf = menu.getLeaf();
			
			if(i>0){
				pre = list.get(i-1);
			}
			//当前为父节点 
			if(!leaf && pre != null ){//父节点
				if(!pre.getLeaf()){//上一节点为 父节点
					
				}else if(pre.getLeaf()){//上一节点为子节点
					level-=1;
					sql.append("}]");
				}
			}
			//上一个 子节点
			if(pre != null && pre.getLeaf()){
				sql.append("},");
			}
			
			sql.append("{\"menuId\":\"").append(menu.getMenuId()).append("\",");
			sql.append("\"mid\":\"").append(menu.getModuleId()).append("\",");
			sql.append("\"url\":\"").append(menu.getModuleClass()).append("\",");
			//sql.append("\"template\":\"").append(menu.getTemplate()).append("\",");
			sql.append("\"ord\":\"").append(menu.getOrd()).append("\",");
			sql.append("\"text\":\"").append(menu.getMenuName()).append("\",");
			sql.append("\"parentId\":\"").append(menu.getParentId()).append("\",");
			if(!leaf){
				level+=1;
				sql.append("spriteCssClass: \"folder\",");
			}else{
				sql.append("spriteCssClass: \"html\",");
			}
			sql.append("\"leaf\":").append(menu.getLeaf());
		
			if(!leaf){//父节点
				//sql.append(",expanded: true,\"items\":[");
				sql.append(",\"children\":[");
			}
		}
		if(menu.getLeaf()){
			sql.append("}]");
		}else{
			sql.append("]");
		}
		for (int i = 0; i < level; i++) {
			sql.append("}]");
		}
		return sql.toString(); 
	}
	/**
	 * kendo tree
	 * @param list
	 * @return
	 */
	public String convertMenusToString(List<Menu> list){
		StringBuilder sql = new StringBuilder();
		sql.append("[");
		Menu pre = null;
		Menu menu = null;
		int level = 0;
		for (int i = 0; i < list.size(); i++) {
			 menu = list.get(i);
			boolean leaf = menu.getLeaf();
			
			if(i>0){
				pre = list.get(i-1);
			}
			//当前为父节点 
			if(!leaf && pre != null ){//父节点
				if(!pre.getLeaf()){//上一节点为 父节点
					
				}else if(pre.getLeaf()){//上一节点为子节点
					level-=1;
					sql.append("}]");
				}
			}
			//上一个 子节点
			if(pre != null && pre.getLeaf()){
				sql.append("},");
			}
			
			sql.append("{'menuId':'").append(menu.getMenuId()).append("',");
			//sql.append("'url':'").append(menu.getUrl()).append("',");
			sql.append("'template':'").append(menu.getTemplate()).append("',");
			sql.append("'ord':'").append(menu.getOrd()).append("',");
			sql.append("'menuName':'").append(menu.getMenuName()).append("',");
			sql.append("'parentId':'").append(menu.getParentId()).append("',");
			if(!leaf){
				level+=1;
				sql.append("spriteCssClass: \"folder\",");
			}else{
				sql.append("spriteCssClass: \"html\",");
			}
			sql.append("'leaf':").append(menu.getLeaf());
		
			if(!leaf){//父节点
				sql.append(",expanded: true,'items':[");
			}
		}
		if(menu.getLeaf()){
			sql.append("}]");
		}else{
			sql.append("]");
		}
		for (int i = 0; i < level; i++) {
			sql.append("}]");
		}
		return sql.toString(); 
	}
}
