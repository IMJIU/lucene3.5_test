package com.im.web.dao;


import org.springframework.stereotype.Repository;

import com.im.web.base.BaseDao;
import com.im.web.base.Page;
import com.im.web.entity.Message;
@Repository
public class MessageDao extends BaseDao<Message, Integer>{
	public void delete(int id){
		Message m = get(id);
		if(m!=null){
			delete(m);
		}
	}
	protected final String GET_ALL = " from Message o";
	
	protected final String GET_BY_ID = " from Message o where o.id=? ";
	

	public void saveOrUpdateMsg(Message msg) {
		this.save(msg);
	}

	public Page<Message> getAll(Page<Message> page, Object... values) {
		return this.findPage(page, GET_ALL, values);
	}
	
	public Message getMsgById(String id){
		return this.findUnique(GET_BY_ID, id);
	}
}
