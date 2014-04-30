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
public class DaumCafeParser {
	Pattern pattern = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)</a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	Pattern pattern_atag = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>](.*?)</a>",
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
			"<strong(.*?)class=\"article_sbj\">(.*?)<\\/strong>",
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

	public static void main(String[] args) throws IOException {
		DaumCafeParser parser = new DaumCafeParser();
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
			if (listurl1.startsWith("/CHILIL/LAK?prev_page=")) {
				String pageUrl = listurl1.replaceAll("&amp;", "&");
				String[] aPage = pageUrl.split("&");
				String nPage = aPage[aPage.length-1].split("=")[1];
				pPageMap.put(nPage, pageUrl);
			}
			if (listurl1.startsWith("/CHILIL/LAK/")) {
				if (!listurl1.startsWith("/CHILIL/LAK/697")
						&& !listurl1.startsWith("/CHILIL/LAK/696")
						&& !listurl1.startsWith("/CHILIL/LAK/new")) {
					 vo = new DaumListVo();
					/* System.out.println(listurl); */
					/*
					 * System.out.println("====================1==================="
					 * ); System.out.println(listurl1);
					 */
					String listurl2 = match.group(2);
					Matcher match_owner = pattern_owner.matcher(listurl2);
					vo.setViewUrl(listurl1);
					if (match_owner.find()) {
						// System.out.println("=====================2==================");
						String owner = match_owner.group(2);
						// System.out.println(owner);
						vo.setIdAlais(owner);
					}
					Matcher match_article = pattern_article.matcher(listurl2);
					if (match_article.find()) {
						// System.out.println("=====================3==================");
						String article = match_article.group(2);
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
}
