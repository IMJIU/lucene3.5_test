package com.im.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.Version;

import com.im.lucene.L_13_ParserImpl.ParserImpl;

public class L_14_MyFilter extends Constans {
	public static void main(String[] args) {
		L_14_MyFilter l14 = new L_14_MyFilter();
//		l14.index(true);
		//自定义filter
//		l14.search("java",new MyFilter());
		//优化设计
		l14.search("java",new MyFilter2(new FilterAccesser() {
			public String[] values() {
				return new String[]{"6","9"};
			}
			public boolean set() {
				return true;
			}
			public String getField() {
				return "id";
			}
		}));
	}
	public void search(String key,Filter filter){
		try {
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			Query query = new TermQuery(new Term("content",key));
			TopDocs topDocs = searcher.search(query,filter, 100);
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
	public  void index(boolean isNew){
		IndexWriter writer = null;
		Document doc = null;
		File parent = new File("E:\\workspace\\Lucene3.5\\doc");
		if(isNew){
			deleteAll();
		}
		Random random = new Random(47);int i = 0;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			for (File file : parent.listFiles()) {
				doc = new Document();
				doc.add(new Field("id",""+i++,Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("content",new FileReader(file)));
				doc.add(new Field("content",new FileReader(file)));
				doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(file.lastModified()));
				doc.add(new NumericField("size",Field.Store.YES,true).setIntValue((int)(file.length())));
				doc.add(new NumericField("score",Field.Store.YES,true).setIntValue(random.nextInt(1000)));
				writer.addDocument(doc);
			}
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
class MyFilter extends Filter{
	private static final long serialVersionUID = 1L;
	private String[] delIds = {"6","9"};
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		OpenBitSet set = new OpenBitSet(reader.maxDoc());
		//把所有元素设置为0，可以查询出来
		set.set(0,reader.maxDoc()-1);
		int[]docs = new int[1];
		//查询词出现的次数
		int[] freqs = new int[1];
		for (String delId : delIds) {
			TermDocs tds = reader.termDocs(new Term("id",delId));
			//会将查询出来的对象的位置存储到docs中，出现的频率存储到freqs
			//并返回查询出来的记录数
			int count = tds.read(docs,freqs);
			if(count == 1){
				set.clear(docs[0]);
			}
		}
		return set;
	}
}
/**
 * 优化设计
 */
interface FilterAccesser{
	public String[] values();
	public String getField();
	public boolean set();
}
/**
 * 优化设计
 */
class MyFilter2 extends Filter{
	private FilterAccesser accesser;
	private static final long serialVersionUID = 1L;
	MyFilter2(FilterAccesser accesser){
		this.accesser = accesser;
	}
	private String[] delIds = {"6","9"};
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		OpenBitSet set = new OpenBitSet(reader.maxDoc());
		try {
			if(accesser.set()){
				set(reader,set);
			}else{
				clear(reader, set);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}
	private void set(IndexReader reader,OpenBitSet set) throws IOException{
		int[]docs = new int[1];
		//查询词出现的次数
		int[] freqs = new int[1];
		for (String delId : accesser.values()) {
			TermDocs tds = reader.termDocs(new Term(accesser.getField(),delId));
			//会将查询出来的对象的位置存储到docs中，出现的频率存储到freqs
			//并返回查询出来的记录数
			int count = tds.read(docs,freqs);
			if(count == 1){
				set.set(docs[0]);
			}
		}
	}
	private void clear(IndexReader reader,OpenBitSet set) throws Exception{
		//把所有元素设置为0，可以查询出来
		set.set(0,reader.maxDoc()-1);
		int[]docs = new int[1];
		//查询词出现的次数
		int[] freqs = new int[1];
		for (String delId : accesser.values()) {
			TermDocs tds = reader.termDocs(new Term(accesser.getField(),delId));
			//会将查询出来的对象的位置存储到docs中，出现的频率存储到freqs
			//并返回查询出来的记录数
			int count = tds.read(docs,freqs);
			if(count == 1){
				set.clear(docs[0]);
			}
		}
	}
	
}
