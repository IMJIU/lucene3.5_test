package com.im.web.controller;

import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.ModelAndView;

import com.im.web.base.Page;
import com.im.web.entity.Tmpl;
import com.im.web.service.DowloadService;
import com.im.web.util.FileOperateUtil;



@Controller
@RequestMapping("/tmpl")
public class DownloadController {
	@Autowired
	private DowloadService tmplService;
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody Page<Tmpl> getUsers(@RequestParam("page") String page,@RequestParam("pageSize") String pageSize) {
		Page<Tmpl> projs = tmplService.queryAll(Integer.parseInt(page), Integer.parseInt(pageSize));
		return projs;
	}
	
    @RequestMapping(value = "/upload")  
    public @ResponseBody String upload(HttpServletRequest request) throws Exception {  
        List<Tmpl>  result = FileOperateUtil.upload(request);
        for (Tmpl tmpl : result) {
			tmplService.addTmpl(tmpl);
		}
        if(result.size()>0){
        	return "";
        }
        return null;
    }  
  

    @RequestMapping(value = "download/{id}", method = RequestMethod.GET)  
    public void download(HttpServletRequest request,  
            HttpServletResponse response,@PathVariable("id") String tmplId) throws Exception {  
    	
        Tmpl tmpl = tmplService.queryTmplById(tmplId);  
  
        FileOperateUtil.download(request, response, tmpl.getVirName(), tmpl.getContentType(), tmpl.getRealName());  
    }  
	
	@RequestMapping(value = "/getTmpl/{id}", method = RequestMethod.GET)
	public @ResponseBody Tmpl getTmpl(@RequestParam("id") String tmpl) {
		Tmpl user = tmplService.queryTmplById(tmpl);
		return user;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody String deleteTmpl(@RequestBody String str) {
		//System.out.println("delete id:"+id);
		//Project project = (Project)JsonParser.parseJsonStrToBean(proj,Project.class);
		this.tmplService.deleteTmplByName(str.split("=")[1]);
		return "";
	}
	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody String removeTmpl(@RequestBody String id) {
		//System.out.println("delete id:"+id);
		//Project project = (Project)JsonParser.parseJsonStrToBean(proj,Project.class);
		this.tmplService.deleteTmpl(id.split("=")[1]);
		return "";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST )
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addTmpl(@RequestBody String proj) {
		Tmpl tmpl = (Tmpl)JsonParser.parseJsonStrToBean(URLDecoder.decode(proj),Tmpl.class);
		this.tmplService.addTmpl(tmpl);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateTmpl(@RequestBody String json) {
		Tmpl tmpl = (Tmpl)JsonParser.parseJsonStrToBean(json,Tmpl.class);
		this.tmplService.updateTmpl(tmpl);
	}
	
	
	
	
}
