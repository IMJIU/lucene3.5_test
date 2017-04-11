package com.im.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.im.web.service.TempIndexService;


@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private TempIndexService indexService;
	@RequestMapping(value = "/commit", method = RequestMethod.GET)
	public void commitIndex() {
		indexService.commitIndex();
	}
	@RequestMapping(value = "/construct", method = RequestMethod.GET)
	public void construct(){
		indexService.constructIndex();
	}
	@RequestMapping(value = "/commitTempIndex", method = RequestMethod.GET)
	public void commitTempIndex(){
		indexService.updateSetIndex();
	}
}
