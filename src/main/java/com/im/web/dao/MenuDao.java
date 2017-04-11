package com.im.web.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.im.web.base.BaseDao;
import com.im.web.base.Page;
import com.im.web.entity.Menu;



@Repository
public class MenuDao extends BaseDao<Menu, String> {

	protected final String GET_ALL_MENU = " from Menu order by ord";
	
	protected final String GET_MENU_BY_ID = " from Menu m where m.menuId = ? ";

	public void saveOrUpdateMenu(Menu menu) {
		this.save(menu);
	}

	public Page<Menu> getAllMenu(Page<Menu> page, Object... values) {
		return findPage(page, GET_ALL_MENU, values);
	}
		
	public Menu getMenuByMenuId(String menuId){
		return findUnique(GET_MENU_BY_ID, menuId);
	}
	
	public void deleteMenuByMenuId(String menuId){
		delete(menuId);
	}
	
	public List<Menu> getMenusTabTrip(){
		List<Menu> list = find("from Menu m where m.menuId<>'0' order by m.ord");
		return list;
	}
	/**
	 * 按id删除对象.
	 */
	public void deleteCascade(String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from pmo_menu where menu_id in (");
		sql.append("  select menu_id from pmo_menu");
		sql.append("  start with menu_id = ?");
		sql.append("  connect  by prior menu_id = parent_id");
		sql.append(")");
		getJdbcTemplate().update(sql.toString(),  new Object[]{id});
	}
	public List<Menu> getMenusTree(){
		List<Menu> list = getAll("ord", true);
//		List<Menu> list = find("from Menu m order by m.ord");
		return list;
	}

}

