package com.im.lucene;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public abstract class Constans {
	protected String[] ids = {"1","2","3","4","5"};
	protected String[] emails = {"aa@qq.com","bb@sina.com","cc@gmail.com","dd@163.com","ee@qq.com"};
	protected String[] contents = {
			 "1 hello~ boy ! Why do i love you~!"
			,"2 hello girls  ! i love you so much!"
			,"3 are you really?"
			,"4 yes,you're beautiful"
			,"5 good jobs!"
	};
	protected static IndexReader reader = null;
	protected int[] attachs = {2,3,1,4,5};
	protected String[] names = {"aa","kyo","zlf","linye","mama"};
	protected Directory dir = null;
	protected String INDEX_PATH = "E:\\workspace\\Lucene3.5\\index01";
	protected static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
	protected Date[] ds = new Date[5];
	protected SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
	protected static Version v35 = Version.LUCENE_35;
	public Constans() {
		try {
			dir = FSDirectory.open(new File(INDEX_PATH));
			ds[0] = convert("20140601");
			ds[1] = convert("20140701");
			ds[2] = convert("20140801");
			ds[3] = convert("20140901");
			ds[4] = convert("20141001");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Date convert(String date){
		Date d = null;
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	public String conver(Date d){
		return format.format(d);
	}
	
	public Directory getDirectory(){
		return dir;
	}
	public void deleteAll(){
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_35,analyzer));
			writer.deleteAll();
		}catch (Exception e) {
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
