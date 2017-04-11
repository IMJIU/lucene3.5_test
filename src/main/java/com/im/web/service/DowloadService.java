package com.im.web.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.im.web.base.Page;
import com.im.web.dao.DowloadDao;
import com.im.web.entity.Tmpl;

@Service
public class DowloadService {
	@Autowired
	private DowloadDao tmplDao;
	
	public Page<Tmpl> queryAll(int pageNo, int pageSize) {
		Page<Tmpl> page = new Page<Tmpl>(pageSize, pageNo);
		return this.tmplDao.getAllTmpl(page, new Object[]{});
	}

	public void addTmpl(Tmpl tmpl) {
		this.tmplDao.saveOrUpdateTmpl(tmpl);
	}
	
	public Tmpl queryTmplById(String tmpl){
		return this.tmplDao.getTmplById(tmpl);
	}
	
	public void updateTmpl(Tmpl tmpl){
		this.tmplDao.saveOrUpdateTmpl(tmpl);
	}
	
	public void deleteTmpl(String tid){
		Tmpl tmpl= this.queryTmplById(tid);
		this.tmplDao.delete(tmpl);
		destroyTmpl(tmpl);
	}
	public void deleteTmplByName(String name){
		this.tmplDao.deleteTmplByName(name);
	}
	
	private void destroyTmpl(Tmpl t) {
		File file = new File(t.getPath());
		if(file.exists()){
			file.delete();
		}
	}
}
