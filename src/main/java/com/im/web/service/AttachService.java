package com.im.web.service;

import org.aspectj.weaver.bcel.AtAjAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.im.web.dao.AttachmentDao;
import com.im.web.dao.MessageDao;
import com.im.web.entity.Attachment;
import com.im.web.entity.Message;

@Service
public class AttachService {
	@Autowired
	private AttachmentDao attachmentDao;
	public void addAttach(Attachment att){
		attachmentDao.save(att);
	}
	public Attachment load(Integer id){
		return attachmentDao.get(id);
	}
	public void saveOrUpdate(Attachment a){
		attachmentDao.saveOrUpdate(a);
	}
}
