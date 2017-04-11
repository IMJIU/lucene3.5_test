package com.im.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class L_05_Paging_FileUtil extends Constans{
	
	private static IndexReader reader = null;
	
	public static void main(String[] args) {
		L_05_Paging_FileUtil page = new L_05_Paging_FileUtil();
		//创建索引
//		page.index_File(true);
		//分页1
//		page.searchPage1("java", 2, 5);
		page.searchPage2("java", 1, 5);
	}
	/**
	 * 老版本翻页
	 * @param queryStr
	 * @param page
	 * @param pageSize
	 */
	public void searchPage1(String queryStr,int page,int pageSize){
		IndexSearcher searcher = getSearcher();
		QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
		Query query;
		try {
			query = parser.parse(queryStr);
			TopDocs topdocs = searcher.search(query, 500);
			ScoreDoc[] scoreDocs = topdocs.scoreDocs;
			int start = (page-1)*pageSize;
			int end = page * pageSize;
			for (int i = start ; i<end;i++) {
				Document doc = searcher.doc(scoreDocs[i].doc);
				System.out.println(scoreDocs[i].doc+"|"+doc.get("filename")+"|"+doc.get("size"));
			}
			searcher.close();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private ScoreDoc getLastScoreDoc(int page,int pageSize,Query query,IndexSearcher searcher) throws IOException{
		if(page == 1){
			return null;
		}
		int last = (page-1)*pageSize;
		TopDocs topDocs = searcher.search(query, last);
		return topDocs.scoreDocs[last-1];
	}
	/**
	 * 新版本翻页  用searchAfter
	 * @param queryStr
	 * @param page
	 * @param pageSize
	 */
	public void searchPage2(String queryStr,int page,int pageSize){
		IndexSearcher searcher = getSearcher();
		QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
		Query query=null;
		try {
			query = parser.parse(queryStr);
			ScoreDoc last = getLastScoreDoc(page, pageSize, query, searcher);
			TopDocs topdocs = searcher.searchAfter(last, query, pageSize);
			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println(scoreDoc.doc+"|"+doc.get("filename")+"|"+doc.get("size"));
			}
			searcher.close();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public IndexSearcher getSearcher(){
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
	
	public  void index_File(boolean isNew){
		IndexWriter writer = null;
		Document doc = null;
		File parent = new File("E:\\workspace\\Lucene3.5\\doc");
		if(isNew){
			deleteAll();
		}
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			for (File file : parent.listFiles()) {
				doc = new Document();
				doc.add(new Field("content",new FileReader(file)));
				doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(file.lastModified()));
				doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(file.length())));
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
	public void copyFile(){
		String[] ends = {".ini",".java",".c++"};
		File file = new File("E:\\workspace\\Lucene3.5\\doc");
		for (File f : file.listFiles()) {
			System.out.println(f.getAbsolutePath());
			System.out.println(FilenameUtils.getFullPath(f.getAbsolutePath()));
			System.out.println(FilenameUtils.getBaseName(f.getName()));
			System.out.println("================");
			for (String end : ends) {
				String descFileName = FilenameUtils.getFullPath(f.getAbsolutePath())+FilenameUtils.getBaseName(f.getName())+end;
				try {
					FileUtils.copyFile(f, new File(descFileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
