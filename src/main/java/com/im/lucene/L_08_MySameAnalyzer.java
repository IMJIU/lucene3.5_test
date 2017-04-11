package com.im.lucene;

import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
/**
 * 简单的同义词分词器实现
 * @author root
 *
 */
public class L_08_MySameAnalyzer extends Analyzer{
	
	public static void main(String[] args) {
		L_08_MySameAnalyzer l8 = new L_08_MySameAnalyzer();
		String s = "how are you thank you";
		s = "我来自中国湖南省永州市江永县潇浦街005号";
		//显示分词信息
//		L_06_Attribute.displayToken(s, l8);
		//显示所有的分词信息
//		L_06_Attribute.displayAllToken(s, l8);
		//同义词查询 都可以搜索出来
		l8.search("中国");
		l8.search("咱");
		l8.search("大陆");
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		Dictionary dic =Dictionary.getInstance(new File("E:\\lib\\mmseg4j-1.8.5\\data"));
		MySameTokenFilter myFilter = new MySameTokenFilter(new MMSegTokenizer(new MaxWordSeg(dic), reader),new SimpleSamewordContext());
		return myFilter;
	}
	
	public void search(String key){
		try {
			String s = "how are you thank you";
			s = "我来自中国湖南省永州市江永县潇浦街005号";
			Directory dir = new RAMDirectory();
			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, this));
			Document doc = new Document();
			doc.add(new Field("content",s,Field.Store.YES,Field.Index.ANALYZED));
			writer.addDocument(doc);
			writer.close();
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			TopDocs t = searcher.search(new TermQuery(new Term("content",key)), 500);
			for (ScoreDoc sd : t.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				System.out.println(d.get("content"));
			}
			searcher.close();
			L_06_Attribute.displayAllToken(s, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class MySameTokenFilter extends TokenFilter{
	private CharTermAttribute cattr = null;
	private PositionIncrementAttribute pattr = null;
	private Stack<String> sameWords;
	private SamewordContext samewordContext;
	private State current = null;
	public MySameTokenFilter(TokenStream input,SamewordContext samewordContext) {
		super(input);
		cattr = this.addAttribute(CharTermAttribute.class);
		pattr = this.addAttribute(PositionIncrementAttribute.class);
		sameWords = new Stack<String>();
		this.samewordContext = samewordContext;
	}

	public boolean incrementToken() throws IOException {
//		if(!input.incrementToken()){
//			return false;
//		}
//		if(cattr.toString().equals("中国")){
//			cattr.setEmpty();
//			cattr.append("大陆");
//		}
//		System.out.println("\nattr:"+cattr);
		if(sameWords.size()>0){
			//将元素车站，并且获取同义词
			String popSameWord = sameWords.pop();
			//还原状态
			restoreState(current);
			//清空 现在 这个cattr,前一个已经存储所以清空不影响前一个
			//并设置为同义词，位置为0，因为正常单词位置为1
			cattr.setEmpty();
			cattr.append(popSameWord);
			pattr.setPositionIncrement(0);
			return true;
		}
		if(!input.incrementToken()){
			return false;
		}
		if(hasSameWordAndAddToStack(cattr.toString())){
			//如果有同义词 就将当前状态保存
			current = captureState();
		}
		return true;
	}
	private boolean hasSameWordAndAddToStack(String name){
//		Map<String,String[]>maps = new HashMap<String,String[]>();
//		maps.put("中国", new String[]{"天朝","大陆"});
//		maps.put("我", new String[]{"咱","俺"});
//		String[] words = maps.get(name);
		String[] words = samewordContext.getSamewords(name);
		if( words != null){
			for (String word : words) {
				sameWords.push(word);
			}
			return true;
		}
		return false;
	}
}
/** 
 * 加入设计
 * @author root
 *
 */
	interface SamewordContext{
		public String[] getSamewords(String name);
	}
	class SimpleSamewordContext implements SamewordContext{
		Map<String,String[]>maps = new HashMap<String,String[]>();
		public SimpleSamewordContext() {
			maps.put("中国", new String[]{"天朝","大陆"});
			maps.put("我", new String[]{"咱","俺"});
		}
		public String[] getSamewords(String name) {
			String[] words = maps.get(name);
			return words;
		}
	}
