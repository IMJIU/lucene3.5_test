package com.im.lucene;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManagerReopenThread;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.SearcherWarmer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class L_19_NRTManager extends Constans{
	private NRTManager nrtMgr = null;
	private SearcherManager smgr = null;
	private IndexWriter writer = null;
	
	public static void main(String[] args) throws IOException {
		final L_19_NRTManager nrt = new L_19_NRTManager();
//		l2.index_Boost();
		nrt.query();
//		
		nrt.timer();
//		nrt.deleteAll();
	}
	public L_19_NRTManager() {
		try {
			analyzer = new IKAnalyzer();
			writer = new IndexWriter(dir,new IndexWriterConfig(v35, analyzer));
			//线程安全
			nrtMgr = new NRTManager(writer, new SearcherWarmer() {
				public void warm(IndexSearcher s) throws IOException {
					System.out.println("reopen");
				}
			});
			//启动NRTManager的reopen线程
			NRTManagerReopenThread thread = new NRTManagerReopenThread(nrtMgr, 5.0	, 0.025);
			thread.setDaemon(true);
			thread.setName("NRTManager reopen Thread");
			thread.start();
			//允许所有的删除
			smgr = nrtMgr.getSearcherManager(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 数据存在内存中
	 */
	public void timer(){
		new Timer().schedule(new TimerTask() {
			int n = 0;
			public void run() {
				try {
					if(n++%5 == 0){
						index();
					}
					if(n%9 == 0){
//						commit();//提交到硬盘
					}
					search_highlight("content", "love");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 2000);
	}
	/**
	 * 把内存中的数据写入磁盘
	 */
	public void commit() throws CorruptIndexException, IOException{
		writer.commit();
	}
	
	
	public void deleteAll(){
		try {
			nrtMgr.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void index() throws IOException{
		Document doc = null;
		for (int i = 0; i < ids.length; i++) {
			doc = new Document();
			doc.add(new Field("id",ids[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email",emails[i],Field.Store.YES,Field.Index.NOT_ANALYZED));
			doc.add(new Field("content",contents[i],Field.Store.NO,Field.Index.ANALYZED));
			doc.add(new Field("name",names[i],Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
			nrtMgr.addDocument(doc);
		}
	}
	public void query() throws CorruptIndexException, IOException{
		IndexReader reader = null;
		reader = IndexReader.open(dir);
		System.out.println("num:"+reader.numDocs());
		System.out.println("max:"+reader.maxDoc());
		System.out.println("deletes:"+reader.numDeletedDocs());
	}
	public void delete() throws IOException{
//		nrtMgr.deleteAll();
		nrtMgr.deleteDocuments(new Term("id","1"));
	}
	public void update() throws IOException{
		Document doc = new Document();
		doc.add(new Field("id","9999",Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field("email","null@sina.com",Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new Field("content","this is update!",Field.Store.NO,Field.Index.ANALYZED));
		doc.add(new Field("name","null",Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
		nrtMgr.updateDocument(new Term("id","1"),doc);
	}
	public void search_highlight(String field,String key) throws IOException{
		IndexSearcher searcher =null;
		searcher = smgr.acquire();
		try {
			/**用处：检测是否需要重新打开，如果不调用这个方法，则操作过的看不到.
			 * 缺点：性能差
			 * 综合：每隔一段时间 调用一次 */
//			mgr.maybeReopen();
			Analyzer a = new IKAnalyzer();
			QueryParser parser = new QueryParser(v35,field,a);
			Query query = parser.parse(key);
			TopDocs tds = searcher.search(query,500);
			System.out.println("===========\nhit:"+tds.totalHits);
			for (ScoreDoc scoreDoc : tds.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
//				System.out.println("id:"+doc.get("id")+"|score:"+sd.score+"|doc:"+sd.doc+"|FileName:"+doc.get("filename")+"|size:"+doc.get("size")+"|Myscore:"+doc.get("score"));
				System.out.print(" docNo:"+scoreDoc.doc);
				//内部算分
				System.out.print(" score:"+scoreDoc.score);
				System.out.print(" id:"+doc.get("id"));
				System.out.print(" name:"+doc.get("name"));
				System.out.print(" email:"+doc.get("email"));
				System.out.print(" attachs:"+doc.get("attachs"));
				//这里是得不到boost信息的
				System.out.print(" boost:"+doc.getBoost());
				System.out.print(" date:"+doc.get("date"));
				System.out.println(" content:"+doc.get("content"));
				String content = doc.get(field);
				if(content!=null){
					content = lighter01(a, query, content);
					System.out.println(content);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			smgr.release(searcher);
		}
	}
	public String lighter01(Analyzer a,Query query,String txt){
		try {
			QueryScorer score=new QueryScorer(query);
			Fragmenter fragmenter=new SimpleSpanFragmenter(score);
			Formatter formatter = new SimpleHTMLFormatter("<span>","</span>");
			Highlighter highlighter=new Highlighter(formatter,score);
			highlighter.setTextFragmenter(fragmenter);
			String str = highlighter.getBestFragment(a,"content", txt);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
