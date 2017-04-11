package com.im.lucene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.xml.sax.ContentHandler;

/**
 * java -jar lukeall.3.5.0.jar
 * java -jar tika-app-1.6.jar --help
 * 
 *
 */
public class L_15_lukeall_Tika extends Constans{

	public static void main(String[] args) {
		Analyzer analyzer = new IKAnalyzer();
		L_15_lukeall_Tika l15 = new L_15_lukeall_Tika();
		l15.index("D:\\BaiduYunDownload/第十四课 数据库安全（2）.pdf", true,analyzer);
		l15.search("数据库",analyzer );
	}
	/** 简单方法 */
	public String simpleMethod(File file) throws IOException, TikaException{
		Tika tika = new Tika();
		return tika.parseToString(file);
	}
	/** 简单方法 */
	public String simpleMethod2(File file) throws IOException, TikaException{
		Tika tika = new Tika();
		Metadata metadata = new Metadata();
		/** 自己增加Metadata */
		metadata.add(Metadata.COMMENT, "zlfkyo");
		metadata.add("WHY", "zlfkyo");
		for (String name : metadata.names()) {
			System.out.println("name:"+metadata.get(name));
		}
		String result = tika.parseToString(new FileInputStream(file),metadata);
		return result;
	}
	/** tika转换方法 */
	public String fileToTxt(File f){
		Parser parser = new AutoDetectParser();
		InputStream in = null;
		try {
			Metadata metadata = new Metadata();
			/** 自己增加Metadata */
			metadata.add(Metadata.COMMENT, "zlfkyo");
			metadata.add("WHY", "zlfkyo");
			in = new FileInputStream(f);
			ContentHandler handler = new BodyContentHandler();
			ParseContext context = new ParseContext();
			context.set(Parser.class, parser);
			parser.parse(in, handler, metadata, context);
			for (String name : metadata.names()) {
				System.out.println(name+":"+metadata.get(name));
			}
			return handler.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void search(String key,Analyzer analyzer){
		try {
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			Query query = new TermQuery(new Term("content",key));
			TopDocs topDocs = searcher.search(query, 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("content:"+doc.get("content"));
//				System.out.println("id:"+doc.get("id")+"|score:"+sd.score+"|doc:"+sd.doc+"|FileName:"+doc.get("filename")+"|size:"+doc.get("size")+"|Myscore:"+doc.get("score")+"|"+sdf.format(new Date(Long.parseLong(doc.get("date")))));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public  void index(String filepath,boolean isNew,Analyzer analyzer){
		IndexWriter writer = null;
		Document doc = null;
		if(isNew){
			deleteAll();
		}
		try {
			File f = new File(filepath);
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			doc = new Document();
			doc.add(new Field("content",fileToTxt(f),Field.Store.YES,Field.Index.ANALYZED));
			writer.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
