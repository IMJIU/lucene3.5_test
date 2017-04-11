package com.im.lucene;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
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
import org.wltea.analyzer.lucene.IKAnalyzer;

public class L_18_SearcherManager extends Constans{
	private SearcherManager mgr = null;
	
	public static void main(String[] args) throws IOException {
		L_16_Tika l16 = new L_16_Tika();
		final L_18_SearcherManager nrt = new L_18_SearcherManager();
		
//		nrt.deleteAll();
//		l16.index_File(true);
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					//若未执行mgr.maybeReopen(); 则不会重新打开search
					nrt.search_highlight("content", "content");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0,3000);
//		nrt.deleteAll();
//		nrt.search_highlight("title", "one");
	}
	public L_18_SearcherManager() {
		try {
			mgr = new SearcherManager(dir, new SearcherWarmer() {
				//重新打开search会触发   可以来释放资源
				public void warm(IndexSearcher s) throws IOException {
					System.out.println("changed!!!");
				}
			}, Executors.newCachedThreadPool());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void search_highlight(String field,String key) throws IOException{
		IndexSearcher searcher =null;
		searcher = mgr.acquire();
		try {
			/**用处：检测是否需要重新打开，如果不调用这个方法，则操作过的看不到.
			 * 缺点：性能差
			 * 综合：每隔一段时间 调用一次 */
//			mgr.maybeReopen();
			Analyzer a = new IKAnalyzer();
			QueryParser parser = new QueryParser(v35,field,a);
			Query query = parser.parse(key);
			TopDocs tds = searcher.search(query,500);
			System.out.println("hit:"+tds.totalHits);
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				//System.out.println("id:"+doc.get("id")+"|score:"+sd.score+"|doc:"+sd.doc+"|FileName:"+doc.get("filename")+"|size:"+doc.get("size")+"|Myscore:"+doc.get("score"));
				String content = doc.get(field);
				if(content!=null){
					content = lighter01(a, query, content);
//					System.out.println(content);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}finally{
			 mgr.release(searcher);
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
