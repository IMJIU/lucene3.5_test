package com.im.web.dao;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.im.web.base.BaseDao;
import com.im.web.entity.Attachment;
import com.im.web.service.TempIndexService;
import com.im.web.util.IndexUtil;

@Repository("attachmentDao")
public class AttachmentDao extends BaseDao<Attachment, Integer>{
	@Autowired
	private TempIndexService tempIndexService;

	public void deleteByMsgId(int msgId){
		deleteFile(msgId);
		batchExecute("delete a from Attachment a where a.message.id=?",msgId);
	}
	/**
	 * 删除附件的文件
	 * @param msgId
	 */
	private void deleteFile(int msgId){
		List<Object[]> files = getHibernateTemplate()
		.find("select id,newName,message.id from Attachment a where a.message.id=? ",msgId);
		for (Object[] objs : files) {
			File f = new File("G:/workspace/Lucene3.5/upload/"+objs[1]);
			String id = objs[2]+"_"+objs[0];
			tempIndexService.deleteIndex(id, IndexUtil.ATTACHMENT_TYPE);
			f.delete();
		}
	}
}
