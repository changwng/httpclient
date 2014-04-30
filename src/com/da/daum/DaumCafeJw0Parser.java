/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.da.daum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * postMethod.setRequestHeader("Content-type",
 * "application/x-www-form-urlencoded; charset=UTF-8");
 * https://obank.kbstar.com/quics?page=C025255&cc=b028364:b028702
 * 
 * @author changwng
 * 
 */
public class DaumCafeJw0Parser {
	public  static String BOARD_TYPE ="Jw0";
	Pattern pattern = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)</a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	Pattern pattern_atag = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)<\\/a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	Pattern pattern_li = Pattern.compile(
			"<li class=[^>]*>(.*?)<\\/li>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	Pattern pattern_span = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
																	// tag
	Pattern pattern_owner = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
																	// tag
	Pattern pattern_article = Pattern.compile(
			"<strong(.*?)class=\"article_sbj\"*>(.*?)<\\/strong>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all
	
	Pattern pattern_article_body = Pattern.compile(
			"<div(.*?)class=\"article_body\"*>(.*?)<\\/div>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all
	
	Pattern pattern_sbj = Pattern.compile(
			"<font(.*?)>(.*?)<\\/font>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all
				
	// html
																			// tag
	Pattern pattern_dt = Pattern.compile(
			"<span(.*?)class=\"dt\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
																	// tag

	Pattern pattern_div_articleTit = Pattern.compile(
			"<div(.*?)id=\"articleTit\">(.*?)<\\/div>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
	Pattern pattern_div_article = Pattern.compile(
			"<div(.*?)id=\"article\">(.*?)<\\/div>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html	
	static Log log = LogFactory.getLog(Class.class);

	// <font color="#57048c">2009년 3월 정기모임 후기 ~~ 늦어서 미안  ^-^</font>
	public static void main(String[] args) throws IOException {
		DaumCafeJw0Parser parser = new DaumCafeJw0Parser();
		String listBody = "(황금자)미소^*~.txt";
		listBody = listBody.replaceAll("\\*", "").replaceAll("\\*", "");
		System.out.println(listBody);
		// FileUtils.writeStringToFile(new File(file), listBody, "utf-8");
		//File file = new File("C:\\TEMP\\daum\\user\\Lak_view_찌니.txt");
		File file = new File("C:\\TEMP\\daum\\user\\Lak_list_1.txt");
		listBody = FileUtils.readFileToString(file, "utf-8");
		 Map pageMap = new HashMap(); 
		 parser.setDaumListVoList( listBody, pageMap);
		//parser.setDaumView(listBody);
	 
	}

	public List<DaumListVo> setDaumListVoList(String listBody, Map pPageMap) {
		DaumListVo vo = new DaumListVo();
		Matcher match = pattern_atag.matcher(listBody); 
		List<DaumListVo> lst = new ArrayList<DaumListVo>();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		while (match.find()) {
			// System.out.println(match);
		   String listurl = match.group(0);
			// System.out.println("===================0====================");
			
			String listurl1 = match.group(1);
			if (listurl1.startsWith("/CHILIL/"+BOARD_TYPE+"?prev_page=")) {
				String pageUrl = listurl1.replaceAll("&amp;", "&");
				String[] aPage = pageUrl.split("&");
				String nPage = aPage[aPage.length-1].split("=")[1];
				pPageMap.put(nPage, pageUrl);
			}
			if (listurl1.startsWith("/CHILIL/"+BOARD_TYPE+"/")) {
				if (!listurl1.startsWith("/CHILIL/"+BOARD_TYPE+"/448")
						&& !listurl1.startsWith("/CHILIL/"+BOARD_TYPE+"/696")
						&& !listurl1.startsWith("/CHILIL/"+BOARD_TYPE+"/new")) {
					 vo = new DaumListVo();
					/* System.out.println(listurl); */
					/*
					 * System.out.println("====================1==================="
					 * ); System.out.println(listurl1);
					 */
					String listurl2 = match.group(2);
					String rnum=listurl1.replace("/CHILIL/"+BOARD_TYPE+"/", "");
					//System.out.println(rnum);
					rnum= rnum.substring(0,rnum.indexOf("?"));
					vo.setRnum(rnum);
					// System.out.println(listurl2);
					Matcher match_owner = pattern_owner.matcher(listurl2);
					vo.setViewUrl(listurl1);
					if (match_owner.find()) {
						//  System.out.println("=====================2==================");
						String owner = match_owner.group(2);
						//  System.out.println(owner);
						vo.setIdAlais(owner);
					}
					Matcher match_article = pattern_article.matcher(listurl2);
					if (match_article.find()) {
						//  System.out.println("=====================3==================");
						  String article =  match_article.group(2);
						  //StringUtils.replace(article, "									", "");
						   article =  htmlRemove(article);
						//  System.out.println(article.length());
						  article =  StringUtils.replace(article, "									", "");
						  article =  StringUtils.replace(article, "						", "");
						  article =  StringUtils.replace(article, "\r\n", "");		
						  article =  StringUtils.replace(article, "			[이미지]", "");
						  article =  StringUtils.replace(article, "	", "");
						  article =  StringUtils.replace(article, ",", "");
						  article =  StringUtils.replace(article, "?", "");
						  //article =  StringUtils.replace(article, "*", ""); 
						  article =  StringUtils.replace(article, ">", "("); 
						  article =  StringUtils.replace(article, "<", ")");
						  article =  StringUtils.replace(article, "!", "_");
						  article =  StringUtils.replace(article, ":", "_");
						  article = article.replaceAll("\\*", "").replaceAll("\\/", "_");
						  	
						 // System.out.println(article.length());
						 // System.out.println(article);
						  vo.setSubject(article);
					}
					Matcher match_dt = pattern_dt.matcher(listurl2);
					if (match_dt.find()) {
						// System.out.println("=====================4==================");
						String dt = match_dt.group(2);
						// System.out.println(dt);
						vo.setCreatYmd(dt);
					}
					lst.add(vo);

				}
			}
		}
		return lst;
	}

	public String setDaumView(String listBody) {
		DaumListVo vo = new DaumListVo();
		String listurl1 ="";
		Matcher match = pattern_div_article.matcher(listBody);
		if(match.find()) {
			// System.out.println(match);
			String listurl = match.group(1);
			// System.out.println("===================0====================");
			 // System.out.println(listurl);
			 listurl1 = match.group(2);
			//  System.out.println(listurl1);
 
		}
		return listurl1;
	}
	/**
	 * 코멘트의 리스트 처리
	 * @param listBody
	 * @param pPageMap
	 * @return
	 */
	public List<DaumListVo> setDaumListVoCommentList(String listBody,String rRum, Map pCommentPageMap) {
		DaumListVo vo = new DaumListVo();
		Matcher match = pattern_atag.matcher(listBody); 
		List<DaumListVo> lst = new ArrayList<DaumListVo>();
		String strConsts ="/CHILIL/"+BOARD_TYPE+"/"+rRum+"/comments";
		System.out.println(">>>>>>>>>>>>>>>>>>Comment>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		while (match.find()) {
			// System.out.println(match);
		   String listurl = match.group(0);
			// System.out.println("===================0====================");
			
			String listurl1 = match.group(1);
			if (listurl1.startsWith(strConsts+"?prev_page=")) {
				String pageUrl = listurl1.replaceAll("&amp;", "&");
				String[] aPage = pageUrl.split("&");
				String nPage = aPage[aPage.length-1].split("=")[1];
				pCommentPageMap.put(nPage, pageUrl);
			}
		}
		Matcher match_li = pattern_li.matcher(listBody); 
		while (match_li.find()) {
			String licomment= match_li.group(1);
			//System.out.println("licomment:"+licomment); 
			if (!licomment.startsWith("<a")) {
					 vo = new DaumListVo();
					  
					Matcher match_owner = pattern_owner.matcher(licomment);
					//vo.setViewUrl(licomment);
					vo.setContent(licomment);
					if (match_owner.find()) {
						//  System.out.println("=====================2==================");
						String owner = match_owner.group(2);
						//  System.out.println(owner);
						vo.setIdAlais(owner);
					}
					Matcher match_article = pattern_article_body.matcher(licomment);
					if (match_article.find()) {
						//  System.out.println("=====================3==================");
						  String article =  match_article.group(2);
						  //StringUtils.replace(article, "									", "");
						 /*  article =  htmlRemove(article);
						//  System.out.println(article.length());
						  article =  StringUtils.replace(article, "									", "");
						  article =  StringUtils.replace(article, "						", "");
						  article =  StringUtils.replace(article, "\r\n", "");		
						  article =  StringUtils.replace(article, "			[이미지]", "");
						  article =  StringUtils.replace(article, "	", "");
						  article =  StringUtils.replace(article, ",", "");
						  article =  StringUtils.replace(article, "?", "");
						  //article =  StringUtils.replace(article, "*", ""); 
						  article =  StringUtils.replace(article, ">", "("); 
						  article =  StringUtils.replace(article, "<", ")");
						  article =  StringUtils.replace(article, "!", "_");
						  article =  StringUtils.replace(article, ":", "_");
						  article = article.replaceAll("\\*", "").replaceAll("\\/", "_");*/
						  	
						 // System.out.println(article.length());
						 // System.out.println(article);
						  vo.setSubject(article);
					}
					Matcher match_dt = pattern_dt.matcher(licomment);
					if (match_dt.find()) {
						// System.out.println("=====================4==================");
						String dt = match_dt.group(2);
						// System.out.println(dt);
						vo.setCreatYmd(dt);
					}
					lst.add(vo);
			}
		}
		return lst;
	}
	public String htmlRemove(String str) {
		StringBuffer t = new StringBuffer();
		StringBuffer t2 = new StringBuffer();

		char[] c = str.toCharArray();
		char ch;
		int d = 0;
		boolean check = false;
		boolean scriptChkeck = false;
		boolean styleCheck = false;
		for (int i = 0, len = c.length; i < len; i++) {
			ch = c[i];
			if (ch == '<') {
				check = true;
			}

			if (!check & !scriptChkeck && !styleCheck) {

				t.append(ch);
			}

			d++;
			t2.append(ch);
			if (d > 9) {
				t2.delete(0, 1);

			}

			if (!scriptChkeck) {
				if (t2.toString().toLowerCase().indexOf("<script") == 0) {
					scriptChkeck = true;
				}

			}
			if (scriptChkeck) {
				if (t2.toString().toLowerCase().indexOf("</script>") == 0) {

					scriptChkeck = false;
				}

			}

			if (!styleCheck) {
				if (t2.toString().toLowerCase().indexOf("<style") == 0) {
					styleCheck = true;
				}

			}
			if (styleCheck) {

				if (t2.toString().toLowerCase().indexOf("</style>") == 0) {
					styleCheck = false;
				}

			}

			if (ch == '>') {
				check = false;
			}
		}

		return t.toString().replace("&nbsp;", "");
	}
}
