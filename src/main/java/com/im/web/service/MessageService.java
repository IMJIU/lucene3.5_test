package com.im.web.service;

import java.io.IOException;
import java.util.Date;

import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.im.web.base.Page;
import com.im.web.dao.AttachmentDao;
import com.im.web.dao.MessageDao;
import com.im.web.entity.Attachment;
import com.im.web.entity.Message;
import com.im.web.util.IndexUtil;

@Service("messageService")
public class MessageService {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private AttachService attachService;
	@Autowired
	private AttachmentDao attachDao;
	@Autowired
	private TempIndexService indexService;
	/**
	 * 增加帖子留言
	 */
	public void addMsg(Message msg,Integer[] atts) throws IOException, TikaException{
		msg.setCreateDate(new Date());
		if(atts!=null){
			for (Integer att : atts) {
				Attachment a = attachDao.get(att);
				a.setMessage(msg);
				attachDao.saveOrUpdate(a);
				indexService.addIndex(IndexUtil.attachToIndexField(a), true);
			}
		}
		indexService.addIndex(IndexUtil.msgToIndexField(msg), true);
		messageDao.save(msg);
	}
	/**
	 * 删除对象帖子
	 * @param id
	 */
	public void delete(int id){
		indexService.deleteIndex("0_"+id,IndexUtil.MSG_TYPE);
		attachDao.deleteByMsgId(id);
		messageDao.delete(id);
	}
	/**
	 * 更新留言帖子
	 * @param m
	 */
	public void update(Message m){
		indexService.updateIndex(IndexUtil.msgToIndexField(m));
		messageDao.saveOrUpdate(m);
	}
	public Page<Message> queryAll(int pageNo, int pageSize) {
		Page<Message> page = new Page<Message>(pageSize, pageNo);
		return this.messageDao.getAll(page, new Object[]{});
	}
	
	public Message queryMsgById(String id){
		return this.messageDao.getMsgById(id);
	}
	public Page<Message> search(int parseInt, int parseInt2) {
		indexService.searchBySave("");
		return null;
	}
	

}
