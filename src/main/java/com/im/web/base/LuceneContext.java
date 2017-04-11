package com.im.web.base;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManagerReopenThread;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.SearcherWarmer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneContext {
	private static LuceneContext instance;
	private static IndexWriter writer;
	private static Analyzer analyzer = null;
	private static NRTManager nrtMgr = null;
	private static SearcherManager smgr = null;
	private static Directory dir = null;
	private static final String INDEX_PATH = "E:/workspace/Lucene3.5/index02";
	private LuceneContext(){init();}
	public static LuceneContext getInstance(){
		if(instance == null){
			instance = new LuceneContext();
		}
		return instance;
	}
	private static void init(){
		try {
			dir = FSDirectory.open(new File(INDEX_PATH));
//			String dicURL = LuceneContext.class.getClassLoader().getResource("data").getPath();
			analyzer = new IKAnalyzer();
			writer = new IndexWriter(dir,new IndexWriterConfig(Conts.v35,analyzer));
			nrtMgr = new NRTManager(writer,new SearcherWarmer() {
				public void warm(IndexSearcher s) throws IOException {
					System.out.println("reopen!");
				}
			});
			smgr = nrtMgr.getSearcherManager(true);
			NRTManagerReopenThread reopenthread = new NRTManagerReopenThread(nrtMgr, 5.0, 0.025);
			reopenthread.setName("NRTManager reopen thread");
			reopenthread.setDaemon(true);
			reopenthread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public IndexSearcher getSearch(){
		return smgr.acquire();
	}
	public void release(IndexSearcher searcher){
		try {
			smgr.release(searcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void commitIndex(){
		try {
			writer.commit();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public NRTManager getNRTManager(){
		return nrtMgr;
	}
	public Analyzer getAnalyzer(){
		return analyzer;
	}
}
