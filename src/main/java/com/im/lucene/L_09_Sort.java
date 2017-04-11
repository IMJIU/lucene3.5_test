package com.im.lucene;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
/**
 * sort用法
 * @author root
 *
 */
public class L_09_Sort extends Constans{
	public static void main(String[] args) {
		L_05_Paging_FileUtil page = new L_05_Paging_FileUtil();
		//创建索引
		page.index_File(true);
		L_09_Sort sort = new L_09_Sort();
		//不使用排序
//		sort.searchBySort("java", null);
		//Sort.INDEXORDER通过doc的id进行排序
//		sort.searchBySort("java", Sort.INDEXORDER);
		//使用默认评分排序
//		sort.searchBySort("java", Sort.RELEVANCE);
		//通过 int字段排序
//		sort.searchBySort("java", new Sort(new SortField("size", SortField.INT)));
//		sort.searchBySort("java", new Sort(new SortField("date", SortField.LONG)));
		// 第三个参数 反向排序
//		sort.searchBySort("java", new Sort(new SortField("size", SortField.INT,true)));
		//通过 String字段排序
//		sort.searchBySort("java", new Sort(new SortField("filename", SortField.STRING)));
		//通过 score字段排序
//		sort.searchBySort("java", new Sort(new SortField("filename", SortField.SCORE)));
		//使用多个字段排序
		sort.searchBySort("java", new Sort(new SortField("size", SortField.INT),new SortField("filename", SortField.STRING)));
		
	}
	/**
	 * 排序
	 * @param queryStr
	 * @param sort
	 */
	public void searchBySort(String queryStr,Sort sort){
		DateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IndexSearcher searcher = getSearcher();
		
		QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
		Query query;
		try {
			query = parser.parse(queryStr);
			TopDocs topdocs = null;
			if(sort!=null){
				topdocs = searcher.search(query, 500,sort);
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
