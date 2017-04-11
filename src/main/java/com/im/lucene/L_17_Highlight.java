package com.im.lucene;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class L_17_Highlight extends Constans{
	String txt = "我爱北京天安门，天安门上彩旗飞，伟大领袖毛主席！";
	public static void main(String[] args) {
		L_17_Highlight h = new L_17_Highlight();
//		h.search("伟大 毛主席");
//		h.searchFormat("伟大 毛主席");
		h.search_highlight("title","one");
	}
	public void search(String key){
		try {
//			Query query = new TermQuery(new Term("content",key));
			Query query = new QueryParser(v35,"content",new IKAnalyzer()).parse(key);
			QueryScorer score=new QueryScorer(query);
			Highlighter highlighter=new Highlighter(score);
			Fragmenter fragmenter=new SimpleSpanFragmenter(score);
			highlighter.setTextFragmenter(fragmenter);
//			String str = highlighter.getBestFragment(new MMSegAnalyzer(),"f", txt);
			String str = highlighter.getBestFragment(new IKAnalyzer(),"content", txt);
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void searchFormat(String key){
		try {
			Query query = new QueryParser(v35,"content",new IKAnalyzer()).parse(key);
			QueryScorer score=new QueryScorer(query);
			Fragmenter fragmenter=new SimpleSpanFragmenter(score);
			Formatter formatter = new SimpleHTMLFormatter("<span>","</span>");
			Highlighter highlighter=new Highlighter(formatter,score);
			highlighter.setTextFragmenter(fragmenter);
			String str = highlighter.getBestFragment(new IKAnalyzer(),"content", txt);
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void search_highlight(String field,String key){
		try {
			Analyzer a = new IKAnalyzer();
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			QueryParser parser = new QueryParser(v35,field,a);
			Query query = parser.parse(key);
			TopDocs tds = searcher.search(query,500);
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("id:"+doc.get("id")+"|score:"+sd.score+"|doc:"+sd.doc+"|FileName:"+doc.get("filename")+"|size:"+doc.get("size")+"|Myscore:"+doc.get("score"));
				String title = doc.get(field);
				if(title!=null){
					title = lighter01(a, query, title);
					System.out.println(title);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
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
