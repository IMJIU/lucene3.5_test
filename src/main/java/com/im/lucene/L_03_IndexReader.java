package com.im.lucene;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class L_03_IndexReader extends Constans{
	private IndexReader reader = null;
	public L_03_IndexReader() {
		try {
			/**
			 * dir = FSDirectory.open(new File(INDEX_PATH));
			 * 会根据当前运行环境选择最合理的打开方式（可能Simple/NIO等）
			 */
			reader = IndexReader.open(dir);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public IndexSearcher getSearcher(){
		/**
		 *	IndexReader单例，查询过程中执行的删除记录会不受影响，可以查询出来
		 *	为防止这样的现象，IndexReader.openIfChanged(reader)
		 *	若无变化，返回null,变化过就返回新的IndexReader
		 */
			try {
				if(reader==null){
					reader = IndexReader.open(dir);
				}else{
					IndexReader reader2 = IndexReader.openIfChanged(reader);
					if(reader2 != null){
						reader.close();
						reader = reader2;
					}
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new IndexSearcher(reader);
	}
	public static void main(String[] args) {
		L_03_IndexReader lucence = new L_03_IndexReader();
		lucence.deleteAll();
		lucence.index_Boost();
		/**
		 *	IndexReader单例
		 */
		lucence.search("you");
		/**
		 * 其实IndexWriter也可以做单例，
		 * 用writer.commit();提交
		 */
	}
	
	public void search(String key){
		try {
			IndexSearcher searcher = getSearcher();
			TermQuery query = new TermQuery(new Term("content",key));
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println("docNo:"+scoreDoc.doc);
				//内部算分
				System.out.println("score:"+scoreDoc.score);
				System.out.println("id:"+doc.get("id"));
				System.out.println("name:"+doc.get("name"));
				System.out.println("email:"+doc.get("email"));
				System.out.println("attachs:"+doc.get("attachs"));
				//这里是得不到boost信息的
				System.out.println("boost:"+doc.getBoost());
				System.out.println("date:"+doc.get("date"));
				System.out.println("content:"+doc.get("content"));
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void index_Boost(){
		IndexWriter writer = null;
		Document doc = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id",ids[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email",emails[i],Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("content",contents[i],Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("name",names[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
				//数字型索引
				doc.add(new NumericField("attachs",Field.Store.YES,true).setIntValue(attachs[i]));
				//日期做索引
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(ds[i].getTime()));
				if(emails[i].endsWith("@gmail.com")){
					doc.setBoost(2f);
				}else if(emails[i].endsWith("@qq.com")){
					doc.setBoost(0f);
				}else {
					doc.setBoost(1f);
				}
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void query(){
		IndexReader reader = null;
		try {
			reader = IndexReader.open(dir);
			System.out.println("num:"+reader.numDocs());
			System.out.println("max:"+reader.maxDoc());
			System.out.println("deletes:"+reader.numDeletedDocs());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public void deleteAll(){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			writer.deleteAll();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
