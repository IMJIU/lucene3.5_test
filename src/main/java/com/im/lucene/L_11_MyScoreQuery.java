package com.im.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
/**
 * 自定义评分规则
 * @author root
 *
 */
public class L_11_MyScoreQuery extends Constans{
	public static void main(String[] args) {
		L_11_MyScoreQuery score = new L_11_MyScoreQuery();
		//创建索引
//		score.index_File(true);
		score.searchByScore("java",true);
	}
	/**
	 * class MyCustomScoreQuery
	 */
	class MyCustomScoreQuery extends CustomScoreQuery{
		public MyCustomScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery) {
			super(subQuery, valSrcQuery);
		}
		protected CustomScoreProvider getCustomScoreProvider(IndexReader reader)
				throws IOException {
			//默认情况实现的评分是通过原有的评分*传入进来的评分域所获取的评分来确定最终评分
			//为了根据不同的需求进行评分，需要自己进行评分的设定
			/**
			 * 自定义的评分步骤：
			 * 1.创建一个类继承CustomScoreProvider
			 * 2.覆盖customScore方法
			 */
//			return super.getCustomScoreProvider(reader);
			return new MyCustomScoreProvider(reader);
		}
	}
	/**
	 * class MyCustomScoreProvider
	 */
	class MyCustomScoreProvider extends CustomScoreProvider{

		public MyCustomScoreProvider(IndexReader reader) {
			super(reader);
		}
		/**
		 * subQueryScore表示默认文档的打分
		 * valSrcScore表示评分域的打分
		 */
		public float customScore(int doc, float subQueryScore, float valSrcScore)
				throws IOException {
//			return super.customScore(doc, subQueryScore, valSrcScore);
			return subQueryScore/valSrcScore;
		}
		
	}
	
	public void searchByScore(String queryStr,boolean score){
		DateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		IndexSearcher searcher = getSearcher();
		Query query;
		try {
			if(score){
				Query q = new TermQuery(new Term("content","java"));
				//创建一个评分域
				FieldScoreQuery fsq = new FieldScoreQuery("score",Type.INT);
				//根据评分域和原有的Query创建自定义的Query对象
				query = new MyCustomScoreQuery(q, fsq);
				
			}else{
				query = new TermQuery(new Term("content",queryStr));
			}
			TopDocs topdocs = null;
			topdocs = searcher.search(query, 500);
			ScoreDoc[] scoreDocs = topdocs.scoreDocs;
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(sd.score+"|"+sd.doc+"|"+doc.get("filename")+"|"+doc.get("size")+"|"+formate.format(new Date(Long.parseLong(doc.get("date"))))+"|"+doc.get("score"));
			}
			searcher.close();
		}  catch (IOException e) {
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
		Random random = new Random(47);
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			for (File file : parent.listFiles()) {
				doc = new Document();
				doc.add(new Field("content",new FileReader(file)));
				doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(file.lastModified()));
				doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(file.length())));
				doc.add(new NumericField("score",Field.Store.YES,true).setIntValue(random.nextInt(1000)));
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
}
