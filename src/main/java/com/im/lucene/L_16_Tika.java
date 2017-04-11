package com.im.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;

public class L_16_Tika extends Constans{
	public static void main(String[] args) {
		L_16_Tika tika = new L_16_Tika();
//		tika.index_File(true);
		tika.search("hibernate");
	}
	public Document convertToDoc(File file) throws IOException{
		Document doc = new Document();
		doc.add(new Field("content",new Tika().parse(file)));
		doc.add(new Field("title",FilenameUtils.getBaseName(file.getName()),Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(file.lastModified()));
		doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(file.length())));
		return doc;
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
				Metadata meta = new Metadata();
				doc.add(new Field("content",new Tika().parse(new FileInputStream(file),meta)));
				doc.add(new Field("title",FilenameUtils.getBaseName(file.getName()),Field.Store.YES,Field.Index.ANALYZED));
				doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(file.lastModified()));
				doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(file.length())));
				String page = meta.get("xmpTPg:NPages");
				if(page!=null){
					doc.add(new NumericField("page",Field.Store.YES,true).setIntValue( Integer.parseInt(page)));
				}
				writer.addDocument(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
	}
	public void search(String key){
		try {
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			Query query = new TermQuery(new Term("content",key));
			TopDocs topDocs = searcher.search(query, 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("id:"+doc.get("id")+"|score:"+sd.score+"|doc:"+sd.doc+"|FileName:"+doc.get("filename")+"|size:"+doc.get("size")+"|Myscore:"+doc.get("score")+"|"+sdf.format(new Date(Long.parseLong(doc.get("date")))));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
