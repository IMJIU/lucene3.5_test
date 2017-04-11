package com.im.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class HelloLucene {
	public static void main(String[] args) {
		HelloLucene lucene = new HelloLucene();
		// 增量索引
		// lucene.index();
		lucene.searcher();
	}

	/**
	 * 建立索引
	 */
	private void index() {
		IndexWriter writer = null;
		try {
			// //1、 创建Directory(内存中）
			// Directory directory = new RAMDirectory();
			// 1、 创建Directory(磁盘中）
			Directory directory = FSDirectory.open(new File("g:\\workspace\\Lucene3.5\\index01"));
			// 2、创建IndexWriter
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
			writer = new IndexWriter(directory, config);
			// 3、创建Document对象
			Document doc = new Document();
			// 4、为Document添加Field
			File file = new File("g:\\workspace\\Lucene3.5\\doc");
			for (File f : file.listFiles()) {
				doc = new Document();
				doc.add(new Field("content", new FileReader(f)));
				// Field.Store.Yes是否保存到硬盘
				// Filed.Index.NOT_ANALYZED 是否进行分词
				doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("path", f.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				// 5、通过IndexWriter添加文本
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
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

	public void searcher() {
		try {
			// 1、 创建Directory(磁盘中）
			Directory directory = FSDirectory.open(new File("g:\\workspace\\Lucene3.5\\index01"));
			// 2、创建IndexReader
			IndexReader reader = IndexReader.open(directory);
			// 3、根据IndexReader创建IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			// 4、创建搜索的Query(搜索内容包含java的文档）
			QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			Query query = parser.parse("java");
			// 5、根据searcher搜索并且返回Topcs
			TopDocs tds = searcher.search(query, 10);
			// 6、根据Topcs获取ScoreDoc对象
			for (ScoreDoc sd : tds.scoreDocs) {
				// 7、根据search和ScoreDoc对象获取具体的Document
				Document d = searcher.doc(sd.doc);
				// 8、根据Document对象获取需要的值
				System.out.println(d.get("filename") + "[" + d.get("path") + "]");
				System.out.println(d.get("content"));
			}
			// 9、关闭reader
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
