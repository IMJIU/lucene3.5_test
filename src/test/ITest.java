package com.im;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.im.web.base.Page;
import com.im.web.dao.MessageDao;
import com.im.web.entity.IndexField;
import com.im.web.entity.Message;
import com.im.web.entity.TempIndex;
import com.im.web.service.MessageService;
import com.im.web.service.TempIndexService;
import com.im.web.util.IndexUtil;
import com.im.web.vo.Index;




public class ITest {
	BeanFactory factory;
	
	@Before
	public  void setup(){
		factory=new ClassPathXmlApplicationContext("config/applicationContext.xml");
	}
	
	@Test
	public void addMsg() throws IOException, TikaException{
		Object obj = factory.getBean("messageService");
		System.out.println(obj);
		MessageService msgService = (MessageService)obj;
		Message msg3 = new Message("title3","content3",new Date());
		msgService.addMsg(msg3,null);
		System.out.println(3);
		Message msg1 = new Message("title1","content1",new Date());
		msgService.addMsg(msg1,null);
		System.out.println(2);
		Message msg2 = new Message("title2","content2",new Date());
		msgService.addMsg(msg2,null);
		System.out.println(1);
	}
	@Test
	public void addAttachment() throws IOException, TikaException{
		Object obj = factory.getBean("attachService");
		System.out.println(obj);
		MessageService msgDao = (MessageService)obj;
		Message msg3 = new Message("title3","content3",new Date());
		msgDao.addMsg(msg3,null);
		Message msg1 = new Message("title1","content1",new Date());
		msgDao.addMsg(msg1,null);
		Message msg2 = new Message("title2","content2",new Date());
		msgDao.addMsg(msg2,null);
	}
	@Test
	public void addIndex() throws IOException, TikaException{
		Object obj = factory.getBean("tempIndexService");
		System.out.println(obj);
		TempIndexService indexDao = (TempIndexService)obj;
		IndexField index = new IndexField("0_111","title","content",0,1111,IndexUtil.MSG_TYPE,new Date());
		indexDao.addIndex(index, true);
	}
	@Test
	public void construct() throws CorruptIndexException, IOException, InterruptedException {
		Object obj = factory.getBean("tempIndexService");
		System.out.println(obj);
		TempIndexService iservice = (TempIndexService)obj;
		iservice.constructIndex();
		//提交磁盘后就不用沉睡6秒再查询了，所以这里注释掉
//		iservice.commitIndex();
		//因为会延迟 5秒后reopen! 
		//所以要沉睡6秒 才能查询到数据
		Thread.sleep(6000);
		Page<Index> pages = iservice.searchBySave("content");
		for ( Index i : pages.getResult()) {
			System.out.println(i.getTitle());
		}
	}
	@Test
	public void query()throws CorruptIndexException, IOException{
		IndexReader reader = null;
		reader = IndexReader.open(FSDirectory.open(new File("E:\\workspace\\Lucene3.5\\index02")));
		System.out.println("num:"+reader.numDocs());
		System.out.println("max:"+reader.maxDoc());
		System.out.println("deletes:"+reader.numDeletedDocs());
	}
	@Test
	public void search1()throws CorruptIndexException, IOException{
		Object obj = factory.getBean("tempIndexService");
		System.out.println(obj);
		TempIndexService iservice = (TempIndexService)obj;
		Page<Index> p = iservice.searchBySave("oracle");
		for (Index i : p.getResult()) {
			System.out.println("title:"+i.getTitle());
			System.out.println("summy:"+i.getSummary());
			System.out.println("======================");
		}
	}
	@Test
	public void search2()throws CorruptIndexException, IOException{
		Object obj = factory.getBean("tempIndexService");
		System.out.println(obj);
		TempIndexService iservice = (TempIndexService)obj;
		Page<Index> p = iservice.searchByDB("oracle");
		for (Index i : p.getResult()) {
			System.out.println("title:"+i.getTitle());
			System.out.println("summy:"+i.getSummary());
			System.out.println("======================");
		}
	}
	@Test
	public void table(){
		new SchemaExport(new AnnotationConfiguration().configure()).create(true, true);
	}
	
}
