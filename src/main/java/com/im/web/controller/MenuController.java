package com.im.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.im.web.base.Page;
import com.im.web.entity.Menu;
import com.im.web.service.MenuService;

@Controller
@RequestMapping("/menu")
public class MenuController {
	@Autowired
	private MenuService menuService;
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody List<Menu> getMenus() {
		List<Menu> list = menuService.queryAllMenu();
		return list;
	}
	@RequestMapping(value = "/getPageAll", method = RequestMethod.GET)
	public @ResponseBody Page<Menu> getPageAll(@RequestParam("page") String page,@RequestParam("pageSize") String pageSize) {
		Page<Menu> list = menuService.queryAllMenu(Integer.parseInt(page), Integer.parseInt(pageSize));
		return list;
	}
	@RequestMapping(value = "/add", method = RequestMethod.PUT )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addMenu(@RequestBody String menu) {
		Object object=JsonParser.parseJsonStrToBean(menu, Menu.class);
		Menu m = (Menu)object;
		menuService.addMenu(m);
	}
	@RequestMapping(value = "/delete", method = RequestMethod.PUT )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenu(@RequestBody String menu) {
		Object object=JsonParser.parseJsonStrToBean(menu, Menu.class);
		Menu m = (Menu)object;
		menuService.deleteCascadeMenu(""+m.getMenuId());
	}
	@RequestMapping(value = "/update", method = RequestMethod.PUT )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateMenu(@RequestBody String menu) {
		Object object=JsonParser.parseJsonStrToBean(menu, Menu.class);
		Menu m = (Menu)object;
		menuService.updateMenu(m);
	}
	@RequestMapping(value = "/getString", method = RequestMethod.GET )
	public @ResponseBody String getString1() {
		return menuService.queryMenuTree();
	}
	@RequestMapping(value = "/getMenuTree", 
			method = RequestMethod.GET ,produces="text/plain;charset=utf-8")
	public @ResponseBody String getMenuTree() {
		String resultTree = menuService.getMenuTree();
		return resultTree;
	}
	@RequestMapping(value = "/order", method = RequestMethod.PUT )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void adjustOrder(@RequestBody String menus) {
		menuService.dealMenuOrderString(menus);
	}
}
