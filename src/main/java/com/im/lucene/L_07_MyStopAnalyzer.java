package com.im.lucene;

import java.io.File;
import java.io.Reader;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
/**
 * 自定义stop分词器  自己增加stop单词
 * @author root
 *
 */
public class L_07_MyStopAnalyzer extends Analyzer{
	private Set stops;
	protected static Version v35 = Version.LUCENE_35;
	
	public L_07_MyStopAnalyzer(String[] ss) {
		stops = StopFilter.makeStopSet(v35,ss,true);//第三个参数 是否大小写区分
		stops.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);//把stopAnalyzer默认的stop词加入
	}
	public L_07_MyStopAnalyzer() {
		stops.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);//把stopAnalyzer默认的stop词加入
	}
	public static void main(String[] args) {
		String[] filterKeys = new String[]{"I","you"};
		L_07_MyStopAnalyzer my = new L_07_MyStopAnalyzer(filterKeys);
		Analyzer mm = new MMSegAnalyzer(new File("E:\\lib\\mmseg4j-1.8.5\\data"));
		IKAnalyzer ik = new IKAnalyzer();
		StopAnalyzer stop = new StopAnalyzer(v35);
		
		String s = "how are you thank you";
		s = "我来自中国湖南省永州市江永县潇浦街005号";
//		L_6_Attribute.displayAllToken(s, stop);
//		L_6_Attribute.displayAllToken(s, my);
		L_06_Attribute.displayAllToken(s, mm);
		L_06_Attribute.displayAllToken(s, ik);
	}
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new StopFilter(	v35, 
								new LowerCaseFilter(v35,new LetterTokenizer(v35,reader)), 
								stops);
	}
}
