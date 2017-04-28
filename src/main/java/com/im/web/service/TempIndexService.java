//package com.im.web.service;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.im.lucene.L_19_NRTManager;
//import javassist.bytecode.ByteArray;
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.NumericField;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryParser.MultiFieldQueryParser;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.NRTManager;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.search.highlight.Formatter;
//import org.apache.lucene.search.highlight.Fragmenter;
//import org.apache.lucene.search.highlight.Highlighter;
//import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
//import org.apache.lucene.search.highlight.QueryScorer;
//import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
//import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
//import org.apache.tika.Tika;
//import org.apache.tika.exception.TikaException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.im.web.base.Conts;
//import com.im.web.base.LuceneContext;
//import com.im.web.base.Page;
//import com.im.web.dao.AttachmentDao;
//import com.im.web.dao.MessageDao;
//import com.im.web.dao.TempIndexDao;
//import com.im.web.entity.Attachment;
//import com.im.web.entity.IndexField;
//import com.im.web.entity.Message;
//import com.im.web.entity.TempIndex;
//import com.im.web.util.IndexUtil;
//import com.im.web.vo.Index;
//
//@Service
//public class TempIndexService {
//	private Tika tika = new Tika();
//	/**
//	 * 添加索引
//	 */
//	public void addIndex(IndexField field,boolean toDB){
//		if(toDB){
//			TempIndex i = new TempIndex();
//			i.setAdd();
//			i.setObjId(field.getObjId());
//			i.setType(field.getType());
//			tempIndexDao.saveIndex(i);
//		}
//		NRTManager nrtMgr = LuceneContext.getInstance().getNRTManager();
//		Document doc = fieldToDoc(field);
//		try {
//			nrtMgr.addDocument(doc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 删除索引
//	 * @param id
//	 */
//	public void deleteIndex(String id,String type){
//		TempIndex i = new TempIndex();
//		i.setDelete();
//		i.setId(Integer.parseInt(id.split("_")[1]));
//		tempIndexDao.delete(i);
//		try {
//			LuceneContext.getInstance().getNRTManager().deleteDocuments(new Term("id",id));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 更新索引
//	 * @param field
//	 */
//	public void updateIndex(IndexField field){
//		TempIndex i = new TempIndex();
//		i.setDelete();
//		i.setObjId(field.getObjId());
//		i.setType(field.getType());
//		tempIndexDao.add(i);
//
//		NRTManager nrtMgr = LuceneContext.getInstance().getNRTManager();
//		Document doc = fieldToDoc(field);
//		try {
//			nrtMgr.updateDocument(new Term("id",field.getId()), doc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 提交索引,将数据库中的记录清空
//	 */
//	public void commitIndex(){
//		LuceneContext.getInstance().commitIndex();
//		tempIndexDao.delAll();
//	}
//	/**
//	 * 重构索引 把所有数据全部取出来重新构建所有的索引
//	 */
//	public void constructIndex(){
//		List<Message>msglist = messageDao.find("from Message",new Object[]{});
//		indexMessage(msglist);
//		List<Attachment>attlist = messageDao.find("from Attachment",new Object[]{});
//		indexAttachment(attlist);
//		commitIndex();
//	}
//	/**
//	 * 把tempIndex表中数据来出来增加索引
//	 */
//	public void updateSetIndex(){
//		List<TempIndex> tis = tempIndexDao.find("from TempIndex");
//		System.out.println(tis.size()+"条");
//		try {
//			for (TempIndex i : tis) {
//				IndexField field = null;
//				if(i.getType().equals(IndexUtil.ATTACHMENT_TYPE)){
//					Attachment a = attachmentDao.load(i.getObjId());
//					field = IndexUtil.attachToIndexField(a);
//				}else if(i.getType().equals(IndexUtil.MSG_TYPE)){
//					Message m = messageDao.load(i.getObjId());
//					field = IndexUtil.msgToIndexField(m);
//				}
//				if(field!=null){
//					addIndex(field, false);
//				}
//			}
//			commitIndex();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 把附件索引
//	 */
//	private void indexAttachment(List<Attachment> list){
//		for (Attachment attachment : list) {
//			IndexField field = null;
//			try {
//				field = IndexUtil.attachToIndexField(attachment);
//				if(field!=null){
//					addIndex(field, false);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (TikaException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	/**
//	 * 把帖子信息索引
//	 */
//	private void indexMessage(List<Message> list){
//		for (Message message : list) {
//			IndexField field = IndexUtil.msgToIndexField(message);
//			addIndex(field, false);
//		}
//	}
//	/**
//	 * 搜索(保存内容，为了高亮显示)
//	 * 优点：快速
//	 * 缺点：耗存储空间换时间
//	 */
//	public Page<Index> searchBySave(String condition){
//		IndexSearcher searcher =null;
//		Page<Index> page = new Page<Index>();
//		List<Index> dataList = new ArrayList<Index>();
//		try {
//			int pageSize = Conts.PageSize;
//			int pageOffset = Conts.PageOffset;
//			searcher = LuceneContext.getInstance().getSearch();
//			MultiFieldQueryParser parser = new MultiFieldQueryParser(Conts.v35, new String[]{"title","content"}, LuceneContext.getInstance().getAnalyzer());
//			Query query = parser.parse(condition);
//			ScoreDoc last = getLastScoreDoc(pageOffset,pageSize, query, searcher);
//			TopDocs topdocs = searcher.searchAfter(last, query, pageSize);
//			List<Integer> ids = new ArrayList<Integer>();
//			int totalRecords = topdocs.totalHits;
//			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
//				Document doc = searcher.doc(scoreDoc.doc);
//				Index index = new Index();
//				String title = doc.get("title");
//				System.out.println(doc.get("objId"));
//				//高亮
//				higlight(query, index, "title",title);
//				higlight(query, index, "content",doc.get("content"));
//				int parentId = Integer.parseInt(doc.get("parentId"));
//				int msgId = 0;
//				if(parentId == 0){
//					msgId = Integer.parseInt(doc.get("objId"));
//				}else{
//					msgId = parentId;
//				}
//				if(!ids.contains(msgId)){
//					ids.add(msgId);
//					index.setMsgId(msgId);
//					index.setCreateDate(new Date(Long.parseLong(doc.get("createDate").toString())));
//					dataList.add(index);
//				}else{
//					totalRecords--;
//				}
//			}
//			page.setResult(dataList);
//			page.setLimit(pageSize);
//			page.setPageNo(pageOffset);
//			page.setTotalCount(totalRecords);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			LuceneContext.getInstance().release(searcher);
//		}
//		return page;
//	}
//	/**
//	 * 搜索(不保存内容，为了高亮显示，查询数据库)
//	 * 优点：节省空间
//	 * 缺点：再次查询数据，影响性能
//	 */
//	public Page<Index> searchByDB(String condition){
//		IndexSearcher searcher =null;
//		Page<Index> page = new Page<Index>();
//		List<Index> dataList = new ArrayList<Index>();
//		try {
//			int pageSize = Conts.PageSize;
//			int pageOffset = Conts.PageOffset;
//			searcher = LuceneContext.getInstance().getSearch();
//			MultiFieldQueryParser parser = new MultiFieldQueryParser(Conts.v35, new String[]{"title","content"}, LuceneContext.getInstance().getAnalyzer());
//			Query query = parser.parse(condition);
//			ScoreDoc last = getLastScoreDoc(pageOffset,pageSize, query, searcher);
//			TopDocs topdocs = searcher.searchAfter(last, query, pageSize);
//			List<Integer> ids = new ArrayList<Integer>();
//			int totalRecords = topdocs.totalHits;
//			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
//				Document doc = searcher.doc(scoreDoc.doc);
//				int msgId  = Integer.parseInt(doc.get("objId"));
//				if(!ids.contains(msgId)){
//					ids.add(msgId);
//				}else{
//					totalRecords--;
//				}
//			}
//			String idStr = convertStr(ids);
//			List<Message>list = messageDao.find(" from Message m where m.id in ("+idStr+")");
//			Map<Integer, Message>map = new HashMap<Integer, Message>();
//			for (Message message : list) {
//				map.put(message.getId(),message);
//			}
//			for (Integer id : ids) {
//				Index i = new Index();
//				i.setMsgId(id);
//				//其实存的时候就应该用tika存储，这里特麻烦
//				higlight(query, i, "title",tika.parseToString(new ByteArrayInputStream(map.get(id).getTitle().getBytes())));
//				higlight(query, i, "content",tika.parseToString(new ByteArrayInputStream(map.get(id).getContent().getBytes())));
//				dataList.add(i);
//			}
//			//高亮
////			Index index = new Index();
////			higlight(query, index, "title",title);
////			higlight(query, index, "content",doc.get("content"));
//			page.setResult(dataList);
//			page.setLimit(pageSize);
//			page.setPageNo(pageOffset);
//			page.setTotalCount(totalRecords);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			LuceneContext.getInstance().release(searcher);
//		}
//		return page;
//	}
//	private String convertStr(List<Integer> ids) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < ids.size(); i++) {
//			sb.append(ids.get(i));
//			if(i!=ids.size()-1){
//				sb.append(",");
//			}
//		}
//		return sb.toString();
//	}
//	private void higlight(Query query, Index index, String field,String text)
//			throws IOException, InvalidTokenOffsetsException {
//		QueryScorer scorer = new QueryScorer(query);
//		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
//		Formatter formatter = new SimpleHTMLFormatter("<red>", "</red>");
//		Highlighter highlighter = new Highlighter(formatter,scorer);
//		highlighter.setTextFragmenter(fragmenter);
//		String preFix= highlighter.getBestFragment(LuceneContext.getInstance().getAnalyzer(),field,text);
//		if(preFix!=null){
//			if(field.equals("title"))
//				index.setTitle(preFix.trim());
//			else if(field.equals("content"))
//				index.setSummary(preFix.trim());
//		}else{
//			if(text.length()>80){
//				text = text.substring(0,80)+"...";
//			}
//			if(field.equals("title"))
//				index.setTitle(text);
//			else if(field.equals("content"))
//				index.setSummary(text);
//		}
//	}
//	private ScoreDoc getLastScoreDoc(int page,int pageSize,Query query,IndexSearcher searcher) {
//		if(page == 1){
//			return null;
//		}
//		TopDocs topDocs = null;
//		try {
//			int last = (page-1)*pageSize;
//			topDocs = searcher.search(query, last);
//			return topDocs.scoreDocs[last-1];
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	public Document fieldToDoc(IndexField field){
//		Document doc = new Document();
//		doc.add(new Field("id",field.getId(),Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
//		doc.add(new Field("title",field.getTitle(),Field.Store.YES,Field.Index.ANALYZED));
//		doc.add(new Field("content",field.getContent(),Field.Store.YES,Field.Index.ANALYZED));
//		doc.add(new NumericField("objId",Field.Store.YES,true).setIntValue(field.getObjId()));
//		doc.add(new NumericField("parentId",Field.Store.YES,true).setIntValue(field.getParentId()));
//		doc.add(new NumericField("createDate",Field.Store.YES,true).setLongValue(field.getCreateDate().getTime()));
//		return doc;
//	}
//}
