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

public class L_02_IndexUtil extends Constans{
	public static void main(String[] args) {
		L_02_IndexUtil lucence = new L_02_IndexUtil();
		//1.创建索引
//		lucence.index();
		//2.打印文档信息
//		lucence.query();
		//3.删除符合条件索引(删除后，执行query()会发现num:4max:5，因为删除后放入回收站
		//多了xx.del文件
//		lucence.delete("1","2");//删除指定
		lucence.deleteAll();//删除所有,并清除回收站
		//4.恢复回收站中的文件,(IndexReader把ReadOnly设置为false)
//		lucence.undelete();
		//5.真正清空删除（之前版本：强制优化）
//		lucence.forceDelete(1);//不建议使用,影响性能，清空至指定值（小于等于总记录数有效）
//		lucence.forceDelete(null);//把所有的删除清空
		//6.更新索引(其实就是删除指定的，增加一个新的)
//		lucence.update();
		//7.增加加权值（数字/日期 索引）
		lucence.index_Boost();
		//8.搜索
		lucence.search("you");
		//9.通过IndexReader删除索引信息，好处，马上更新索引
		// 不过要reader.close();
//		lucence.deleteByReader();
	}
	public void index(){
		IndexWriter writer = null;
		Document doc = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			/**
			 * Field.Store.YES 域内容存储在磁盘中，方便读取文本内容
			 * Field.Store.NO  域内容不存储磁盘，但可以被索引，无法读取内容
			 * Index.ANALYZED   进行分词和索引，适合于标题和内容、摘要
			 * Index.NOT_ANALYZED  进行索引，但不进行分词，如：身份证、姓名、ID等
			 * Index.ANALYZED_NOT_NORMS  进行分词但是不存储norms信息（创建索引时间、权值等）。
			 * Index.NOT_ANALYZED_NOT_NORMS:即不进行分词也不存储NORM信息
			 * Index.NO   不进行索引
			 */
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id",ids[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email",emails[i],Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("content",contents[i],Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("name",names[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
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
	public void delete(String ... ids){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			for (String string : ids) {
				writer.deleteDocuments(new Term("id",string));
			}
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
	public void deleteByReader(){
		IndexReader reader = null;
		try {
			reader = IndexReader.open(dir,false);
			for (String string : ids) {
				reader.deleteDocuments(new Term("id",string));
			}
			reader.close();
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
	public void undelete(){
		IndexReader reader = null;
		try {
			reader = IndexReader.open(dir,false);
			reader.undeleteAll();
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
	public void forceDelete(Integer seg){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			if(seg!=null){
				writer.forceMerge(seg);
			}else{
				writer.forceMergeDeletes();
			}
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
	public void update(){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			Document doc = new Document();
			doc.add(new Field("id","9999",Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email","null@sina.com",Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("content","this is update!",Field.Store.NO,Field.Index.ANALYZED));
			doc.add(new Field("name","null",Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
			writer.updateDocument(new Term("id","1"),doc);
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
	public void search(String key){
		IndexReader reader = null;
		try {
			reader = IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
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

}
