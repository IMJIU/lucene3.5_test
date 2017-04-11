package com.im.web.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import com.im.web.entity.Attachment;
import com.im.web.entity.IndexField;
import com.im.web.entity.Message;

public class IndexUtil {
	public static final String MSG_TYPE = "MSG";
	public static final String ATTACHMENT_TYPE = "ATTACH";
	
	public final static int ADD_OP = 1;
	public final static int DEL_OP = 2;
	public final static int UPT_OP = 3;
	
	public static final List<String> indexAttachType = new ArrayList<String>();
	static{
		indexAttachType.add("doc");
		indexAttachType.add("docx");
		indexAttachType.add("pdf");
		indexAttachType.add("txt");
		indexAttachType.add("xls");
	}
	public static IndexField msgToIndexField(Message message) {
		IndexField field = new IndexField();
		field.setId("0_"+message.getId());
		field.setObjId(message.getId());
		field.setTitle(message.getTitle());
		field.setType(MSG_TYPE);
		field.setCreateDate(message.getCreateDate());
		try {
			field.setContent(new Tika().parseToString(
			new ByteArrayInputStream(message.getContent().getBytes())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		return field;
	}
	public static IndexField attachToIndexField(Attachment attachment) throws IOException, TikaException {
		IndexField field = new IndexField();
		String filename = attachment.getNewName();
		if(indexAttachType.contains(FilenameUtils.getExtension(filename))){
			field.setId(attachment.getMessage().getId()+"_"+attachment.getId());
			field.setObjId(attachment.getId());
			field.setParentId(attachment.getMessage().getId());
			field.setTitle("");
			field.setType(ATTACHMENT_TYPE);
			field.setCreateDate(attachment.getCreateDate());
			String path = "E:\\workspace\\Lucene3.5\\update\\"+attachment.getNewName();
			field.setContent(new Tika().parseToString(new File(path)));
			return field;
		}else{
			return null;
		}
	}
}
