package com.im.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;

public class L_04_IndexSearchUtil extends Constans{
	private IndexReader reader;
	public L_04_IndexSearchUtil() {}
	public static void main(String[] args) {
		L_04_IndexSearchUtil searchUtil = new L_04_IndexSearchUtil();
		//searchUtil.search01_Term("name","aa");
		//跟数字字符串那样对比，如：20比1999大，az比aqqqqq大，这就是范围query
		//searchUtil.search02_TermRange("name","a","lm",true,true);
		//searchUtil.search02_TermRange("id","1","5",true,true);
		//数字range
		//searchUtil.search03_NumRange("attachs",1,9,true,true);
		//前缀查询
		//searchUtil.search04_Prefix("content", "yo");
		//通配符查询：?匹配一个字符,*匹配多个字符
		//searchUtil.search05_WildCardQuery("content", "lo??");
		//逻辑匹配  Occur.SHOULD Occur.MUST Occur.MUST_NOT
		//searchUtil.search06_Boolean();
		//短语查询 slop 代表跳
		//searchUtil.search07_phrase("content", 2, "hello","you");
		//相似度查询  70%相似
		//searchUtil.search08_fuzzy("content", "beeutifll",0.7f);
		//parser搜索
		searchUtil.search09_parser();
	}
	public void search09_parser(){
		try {
			IndexSearcher searcher = getSearcher();
			//创建QueryParser,搜索域设置为content
			QueryParser parser = new QueryParser(Version.LUCENE_35,"content",new StandardAnalyzer(Version.LUCENE_35));
			//设置空格代表的 默认操作符，默认为OR
			parser.setDefaultOperator(Operator.OR);
			//开启第一个字符的通配符匹配，默认关闭 因为效率不高
			//parser.setAllowLeadingWildcard(true);
			Query query = parser.parse("love");
			//有love 或  boy
//			query = parser.parse("love girls");
			//搜索域为name，而且可以加通配符
//			query = parser.parse("name:z*");
			//通配符不能放在首位
//			query = parser.parse("email:*@qq.com");
			//陪陪name中没有zlf,但content中必须有you
			//+和- 要放置到域说明前面
//			query = parser.parse("- name:zlf + you");
			//匹配一个区间，注意:TO必须是大写
//			query = parser.parse("id:[2 TO 4]");
			//匹配闭区间
//			query = parser.parse("id:{1 TO 3}");
			//匹配一个完整字符串
//			query = parser.parse("content:\"hello i\"");
			//匹配 hello 与 i 之间 有3个单词距离
//			query = parser.parse("content:\"hello i\"~3");
			//模糊查询
//			query = parser.parse("name:zll~");
			//无法做到匹配数字
//			query = parser.parse("attach:{2 TO 4}");
			TopDocs topDocs = searcher.search(query, 10);
			
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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
	
	
	public void search01_Term(String field,String key){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = new TermQuery(new Term(field,key));
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search02_TermRange(String field,String start,String end,boolean includeStart,boolean includeEnd){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = new TermRangeQuery(field,start,end,includeStart,includeEnd);
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search03_NumRange(String field,int start,int end,boolean includeStart,boolean includeEnd){
		try {
			IndexSearcher searcher = getSearcher();
			//Query query =  NumericRangeQuery.newFloatRange(field,start,end,includeStart,includeEnd);
			Query query =  NumericRangeQuery.newIntRange(field,start,end,includeStart,includeEnd);
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search04_Prefix(String field,String key){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = new PrefixQuery(new Term(field,key));
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search05_WildCardQuery(String field,String wildcard){
		try {
			IndexSearcher searcher = getSearcher();
			Query query = new WildcardQuery(new Term(field,wildcard));
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search06_Boolean(){
		try {
			IndexSearcher searcher = getSearcher();
			BooleanQuery query = new BooleanQuery();
			query.add(new TermQuery(new Term("content","love")), Occur.MUST);
			query.add(new TermQuery(new Term("content","so")), Occur.MUST_NOT);
			query.add(new TermQuery(new Term("content","i")), Occur.SHOULD);
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search07_phrase(String field,int slop,String ... keys){
		try {
			IndexSearcher searcher = getSearcher();
			PhraseQuery query = new PhraseQuery();
			query.setSlop(slop);
			for (String key : keys) {
				query.add(new Term(field,key));
			}
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search08_fuzzy(String field,String key,float similar){
		try {
			IndexSearcher searcher = getSearcher();
			FuzzyQuery query = new FuzzyQuery(new Term(field,key),similar);
			TopDocs topDocs = searcher.search(query, 10);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				printDoc("2",scoreDoc, doc);
				System.out.println("=================");
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printDoc(String type,ScoreDoc scoreDoc, Document doc) {
		if(type.equals("1")){
			System.out.println("docNo:"+scoreDoc.doc);
			//内部算分
			System.out.println("score:"+scoreDoc.score);
			System.out.println("id:"+doc.get("id"));
			System.out.println("name:"+doc.get("name"));
			System.out.println("email:"+doc.get("email"));
			System.out.println("attachs:"+doc.get("attachs"));
			//这里是得不到boost信息的
			System.out.println("boost:"+doc.getBoost());
			System.out.println("date:"+doc.get("date"));
			System.out.println("content:"+doc.get("content"));
		}else if(type.equals("2")){
			System.out.print("docNo:"+scoreDoc.doc);
			//内部算分
			System.out.print("||score:"+scoreDoc.score);
			System.out.print("||id:"+doc.get("id"));
			System.out.print("||name:"+doc.get("name"));
			System.out.print("||email:"+doc.get("email"));
			System.out.print("||attachs:"+doc.get("attachs"));
			//这里是得不到boost信息的
			System.out.print("||boost:"+doc.getBoost());
			System.out.print("||date:"+doc.get("date"));
			System.out.println("||content:"+doc.get("content"));
		}
		
	}
}
