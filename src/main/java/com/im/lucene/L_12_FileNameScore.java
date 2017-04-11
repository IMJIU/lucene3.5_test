package com.im.lucene;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.search.function.ValueSourceQuery;
import org.apache.lucene.search.function.FieldScoreQuery.Type;

import com.im.lucene.L_11_MyScoreQuery.MyCustomScoreQuery;
/**
 * 根据文件后缀 判断评分
 * @author root
 *
 */
public class L_12_FileNameScore extends Constans{
	public static void main(String[] args) {
		L_12_FileNameScore fileScore = new L_12_FileNameScore();
		fileScore.search("java",true);
	}
	/**
	 * class FilenameScoreQuery
	 */
	class FilenameScoreQuery extends CustomScoreQuery{
		public FilenameScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery) {
			super(subQuery, valSrcQuery);
		}
		protected CustomScoreProvider getCustomScoreProvider(IndexReader reader)
				throws IOException {
			return new FilenameScoreProvider(reader);
		}
		
	}
	/**
	 * class FilenameScoreProvider
	 */
	class FilenameScoreProvider extends CustomScoreProvider{
		private String[] filenames = null;
		public FilenameScoreProvider(IndexReader reader) {
			super(reader);
			try {
				filenames = FieldCache.DEFAULT.getStrings(reader, "filename");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public float customScore(int doc, float subQueryScore, float valSrcScore)
				throws IOException {
			/** subQueryScore 	默认文档的打分
			 *  valSrcScore		评分域的打分
			 */
			if(filenames!=null){
				String filename = filenames[doc];
				if(filename.endsWith(".txt")||filename.endsWith(".java")){
					return subQueryScore * 1.5f;
				}
			}
			return subQueryScore/1.5f;
			//如何根据doc获取相应的field的值
			//在reader没有关闭之前，所有的数据会存储在一个域缓存中，可以通过域混成获取很多有用的信息
//			return super.customScore(doc, subQueryScore, valSrcScore);
		}
		
	}
	/**
	 * class DateScoreProvider
	 */
	class DateScoreProvider extends CustomScoreProvider{
		private long[] dates = null;
		public DateScoreProvider(IndexReader reader) {
			super(reader);
			try {
				dates = FieldCache.DEFAULT.getLongs(reader, "date");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public float customScore(int doc, float subQueryScore, float valSrcScore)
				throws IOException {
			/** subQueryScore 	默认文档的打分
			 *  valSrcScore		评分域的打分
			 */
			if(dates!=null){
				long date = dates[doc];
				long today = new Date().getTime();
				long year = 1000*60*20*24*365;
				if(today - date <= year){
					//加分
					// xxxxx
					// xx
					// ..
				}
			}
			return subQueryScore/1.5f;
		}
		
	}
	public void search(String key,boolean score){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = null;
			if(score){
				Query q = new TermQuery(new Term("content","java"));
				//创建一个评分域
				FieldScoreQuery fsq = new FieldScoreQuery("score",Type.INT);
				//根据评分域和原有的Query创建自定义的Query对象
				query = new FilenameScoreQuery(q, fsq);
				
			}else{
				query = new TermQuery(new Term("content",key));
			}
			TopDocs topDocs = searcher.search(query, 10);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(sd.score+"|"+sd.doc+"|"+doc.get("filename")+"|"+doc.get("size")+"|"+doc.get("score"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
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
