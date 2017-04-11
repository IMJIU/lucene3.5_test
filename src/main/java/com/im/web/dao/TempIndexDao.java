package com.im.web.dao;

import org.springframework.stereotype.Repository;

import com.im.web.base.BaseDao;
import com.im.web.entity.TempIndex;
@Repository
public class TempIndexDao extends BaseDao<TempIndex, String>{
	public void delAll(){
		this.getSession().createSQLQuery("truncate table tmp_index").executeUpdate();
	}
	
	public void saveIndex(TempIndex index){
		save(index);
	}
}
