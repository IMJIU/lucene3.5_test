package com.im.lucene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

/**
 * 自定义扩展QueryParser
 * 1.提高性能
 * 2.扩展需要的功能（lucene没有提供）
 *
 */
public class L_13_ParserImpl extends Constans{
	public static void main(String[] args) {
		L_13_ParserImpl impl = new L_13_ParserImpl();
		//通配符查询
//		impl.search("*java");
//		impl.search("ja?va");
		//模糊查询
//		impl.search("java~");
		//字符范围
//		impl.search("filename:{a TO j}");
		//数字范围
//		impl.search("size:{100 TO 200}");
		impl.search("date:[2010-01-01 TO 2015-01-01]");
		
	}
	class ParserImpl  extends QueryParser{
		public ParserImpl(Version matchVersion, String f, Analyzer a) {
			super(matchVersion, f, a);
		}
		/**
		 * 通配符查询影响性能，不能用
		 */
		protected org.apache.lucene.search.Query getWildcardQuery(String field,
				String termStr) throws ParseException {
			throw new ParseException("性能原因，禁用通配符查询！");
		}
		/**
		 * 模糊查询影响性能，不能用
		 */
		protected org.apache.lucene.search.Query getFuzzyQuery(String field,
				String termStr, float minSimilarity) throws ParseException {
			throw new ParseException("性能原因，禁用模糊查询！");
		}
		protected org.apache.lucene.search.Query getRangeQuery(String field,
				String part1, String part2, boolean inclusive)
				throws ParseException {
			if(field.equals("size")){
				return NumericRangeQuery.newIntRange(field, Integer.parseInt(part1), Integer.parseInt(part2), inclusive, inclusive);
			}else if(field.equals("date")){
				String dateType = "yyyy-MM-dd";
				String regex = "\\d{4}-\\d{2}-\\d{2}";
				if(part1.matches(regex) && part2.matches(regex)){
					SimpleDateFormat sdf = new SimpleDateFormat(dateType);
					try {
						long start = sdf.parse(part1).getTime();
						long end = sdf.parse(part2).getTime();
						return NumericRangeQuery.newLongRange(field, start, end, inclusive, inclusive);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ParseException("时间格式错误，请使用格式："+dateType);
					}
				}
			}
			return super.getRangeQuery(field, part1, part2, inclusive);
		}
	}
	public void search(String key){
		try {
			IndexSearcher searcher = new IndexSearcher(IndexReader.open(dir));
			ParserImpl parser = new ParserImpl(v35, "content", new StandardAnalyzer(v35));
			TopDocs topDocs = searcher.search(parser.parse(key), 100);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (ScoreDoc sd : scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println(sd.score+"|"+sd.doc+"|"+doc.get("filename")+"|size:"+doc.get("size")+"|score:"+doc.get("score")+"|"+sdf.format(new Date(Long.parseLong(doc.get("date")))));
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

}
