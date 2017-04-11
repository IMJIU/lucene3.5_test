package com.im.lucene;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/**
 * 显示分词的属性
 * @author root
 *
 */
public class L_06_Attribute extends Constans{
	public static void main(String[] args) {
		L_06_Attribute a = new L_06_Attribute();
		Analyzer a1 = new StandardAnalyzer(v35);
		Analyzer a2 = new StopAnalyzer(v35);
		Analyzer a3 = new SimpleAnalyzer(v35);
		Analyzer a4 = new WhitespaceAnalyzer(v35);
		String s = "this is my house,I am come from JiangYong"
			+" my email is 303780813@qq.com and zlfkyo@sina.com";
//		s = "我来自中国湖南省永州市江永县潇浦街005号";
//		a.displayToken(s, a1);
//		a.displayToken(s, a2);
//		a.displayToken(s, a3);
//		a.displayToken(s, a4);
		a.displayAllToken(s, a1);
		a.displayAllToken(s, a2);
		a.displayAllToken(s, a3);
		a.displayAllToken(s, a4);
	}
	public static void displayToken(String str,Analyzer a){
		try {
			TokenStream stream = a.tokenStream("content", new StringReader(str));
			//创建一个属性，这个属性会添加流中，随着TokenStream流动
			CharTermAttribute charTermAtt = stream.addAttribute(CharTermAttribute.class);
			System.out.print(a.getClass().getSimpleName()+" | ");
			while (stream.incrementToken()) {
				System.out.print("["+charTermAtt+"]");
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
StandardAnalyzer:
|3:my[8-10]<ALPHANUM>
|1:house[11-16]<ALPHANUM>
|1:i[17-18]<ALPHANUM>
|1:am[19-21]<ALPHANUM>
|1:come[22-26]<ALPHANUM>
|1:from[27-31]<ALPHANUM>
|1:jiangyong[32-41]<ALPHANUM>
|1:my[42-44]<ALPHANUM>
|1:email[45-50]<ALPHANUM>
|2:303780813[54-63]<NUM>
|1:qq.com[64-70]<ALPHANUM>
|2:zlfkyo[75-81]<ALPHANUM>
|1:sina.com[82-90]<ALPHANUM>
===========================
StopAnalyzer:
|3:my[8-10]word
|1:house[11-16]word
|1:i[17-18]word
|1:am[19-21]word
|1:come[22-26]word
|1:from[27-31]word
|1:jiangyong[32-41]word
|1:my[42-44]word
|1:email[45-50]word
|2:qq[64-66]word
|1:com[67-70]word
|2:zlfkyo[75-81]word
|1:sina[82-86]word
|1:com[87-90]word
*/
	public static void displayAllToken(String str,Analyzer a){
		try {
			TokenStream stream = a.tokenStream("content", new StringReader(str));
			PositionIncrementAttribute patt = stream.addAttribute(PositionIncrementAttribute.class);
			OffsetAttribute offatt = stream.addAttribute(OffsetAttribute.class);
			CharTermAttribute catt = stream.addAttribute(CharTermAttribute.class);
			TypeAttribute tatt  = stream.addAttribute(TypeAttribute.class);
			System.out.print(a.getClass().getSimpleName()+":\n");
			while (stream.incrementToken()) {
				System.out.print("|"+patt.getPositionIncrement()+":");
				System.out.println(catt+"["+offatt.startOffset()+"-"+offatt.endOffset()+"]"+tatt.type());
			}
			System.out.println("===========================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * 3.5主要四大类
 *  SimpleAnalyzer 
 *  StopAnalyzer 
 *  WhitespaceAnalyzer
 *  StandardAnalyzer
 * 
 * Reader 
 *  Tokenier(负责将相应的一组数据转换为一个个语汇单元)
 *    TokenFilter (对已经分好词的数据进行过滤操作）
 *       TokenStream
 * 
 * Tokenizer
 * +StandardTokenizer 	
 * +KeywordTokenier
 * +CharTokenizer  		
 * 		+WhitespaceAnalyzer
 * 		+LetterTokenizer
 *   		+LowerCaseTokenizer
 *   
 *  TokenFilter
 *  +CachingTokenFilter
 *  +LengthFilter
 *  +ASCIIFoldingFilter
 *  +PorterStemFilter
 *  +StopFilter
 *  +LowerCaseFilter
 *  +StandardFilter
 *  +TeeSinkTokenFilter
 *  
 *  CharacterAttribute 保存相应词汇
 *  OffsetAttribute  保存各个词汇之间的偏移量
 *  PositionIncrementAttribute 保存词语词之间位置增量
 */
}
