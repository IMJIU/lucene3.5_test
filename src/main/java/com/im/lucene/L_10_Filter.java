package com.im.lucene;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
/**
 * 各种filter的用法
 * @author root
 *
 */
public class L_10_Filter extends Constans{
	public static void main(String[] args) {
		L_05_Paging_FileUtil page = new L_05_Paging_FileUtil();
		//创建索引
		page.index_File(true);
		L_10_Filter filter = new L_10_Filter();
		Filter f = new TermRangeFilter("filename", "a", "t", true, true);
		f = NumericRangeFilter.newIntRange("size", 100, 200, true, true);
		f = new QueryWrapperFilter(new WildcardQuery(new Term("filename","*.java")));
		filter.searchByFilter("java", f);
		
	}
	/**
	 * 排序
	 * @param queryStr
	 * @param sort
	 */
	public void searchByFilter(String queryStr,Filter filter){
		DateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IndexSearcher searcher = getSearcher();
		
		QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
		Query query;
		try {
			query = parser.parse(queryStr);
			TopDocs topdocs = null;
			if(filter!=null){
				topdocs = searcher.search(query, filter,500);
			}else{
				topdocs = searcher.search(query, 500);
			}
			ScoreDoc[] scoreDocs = topdocs.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(sd.score+"|"+sd.doc+"|"+doc.get("filename")+"|"+doc.get("size")+"|"+formate.format(new Date(Long.parseLong(doc.get("date")))));
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
}
