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
public class DaumCafeOneLineParser {
	public static String BOARD_TYPE = "_memo";
	Pattern pattern = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)</a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	Pattern pattern_atag = Pattern
			.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*onclick=[\"']?([^>]+)[\"']?[^>]*>(.*?)<\\/a>",
					Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
							| Pattern.DOTALL);
	Pattern pattern_comment_atag = Pattern
			.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)<\\/a>",
					Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
							| Pattern.DOTALL);
	Pattern pattern_li = Pattern.compile("<li class=[^>]*>(.*?)<\\/li>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Pattern pattern_li_noclass = Pattern.compile("<li.*?>(.*?)<\\/li>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Pattern pattern_span = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
																	// tag
	Pattern pattern_owner = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
	Pattern pattern_spannum = Pattern.compile(
			"<span(.*?)class=\"num\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html
																	// tag
	Pattern pattern_article = Pattern.compile(
			"<strong(.*?)class=\"article_sbj\"*>(.*?)<\\/strong>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all

	Pattern pattern_article_body = Pattern.compile(
			"<div(.*?)class=\"article_body\"*>(.*?)<\\/div>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all

	Pattern pattern_sbj = Pattern.compile("<font(.*?)>(.*?)<\\/font>",
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

	// <font color="#57048c">2009년 3월 정기모임 후기 ~~ 늦어서 미안 ^-^</font>
	public static void main(String[] args) throws IOException {
		DaumCafeOneLineParser parser = new DaumCafeOneLineParser();
		String listBody = "(황금자)미소^*~.txt";
		listBody = listBody.replaceAll("\\*", "").replaceAll("\\*", "");
		System.out.println(listBody);
		// FileUtils.writeStringToFile(new File(file), listBody, "utf-8");
		// File file = new File("C:\\TEMP\\daum\\user\\Lak_view_찌니.txt");
		File file = new File("C:\\TEMP\\daum\\user\\Lak_list_1.txt");
		listBody = FileUtils.readFileToString(file, "utf-8");
		Map pageMap = new HashMap();
		parser.setDaumListVoList(listBody, pageMap);
		// parser.setDaumView(listBody);

	}

	public List<DaumListVo> setDaumListVoList(String listBody, Map pPageMap) {
		List<DaumListVo> lst = new ArrayList<DaumListVo>();
		DaumListVo vo = null;
		
		Matcher match_li = pattern_li_noclass.matcher(listBody);
		while (match_li.find()) {
			String licomment = match_li.group(1);
		 	//System.out.println("setDaumListVoList:"+licomment);
			if (!licomment.startsWith("<a")) { 
				Matcher match = pattern_atag.matcher(licomment);
				// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				vo = setCommentForJavascript(lst, vo, licomment, match);
			}
			  if(vo ==null)
			  {
				Matcher match = pattern_comment_atag.matcher(licomment);
				while (match.find()) {
					String subject ="";
					// System.out.println(match);
					String listurl = match.group(0);
					//System.out.println("===================listurl:"+listurl+"===================="); 
					String listurl1 = match.group(1);
					String listurl2 = match.group(2);
					
					
					 /*System.out.println("================listurl1:==="+listurl1+"====================");
					 System.out.println("================listurl2:==="+listurl2+"====================");*/
					 
					if (!listurl1.startsWith("#")) {
						if (listurl1.startsWith("/CHILIL/_memo")
								&& !listurl1.startsWith("/CHILIL/_memo/edit")
								&& !listurl1.startsWith("/CHILIL/_memo/new")
								&& !listurl1.startsWith("/CHILIL/_memo/57780")
								&& !listurl1.startsWith("/CHILIL/_memo/57718")) { 
							listurl2 = match.group(2); 
							
							String rnum = listurl1;
							rnum = rnum.replace("/CHILIL/_memo/", "");
							rnum = rnum.substring(0, rnum.indexOf("/comments"));
							/*System.out.println("listurl1:"+listurl1);
							System.out.println("rnum:"+rnum);*/
							if( "57780".equals(rnum)
								|| "57718".equals(rnum)
									)
							{
								continue;
							}
							vo = new DaumListVo();
							String viewUrl = listurl1;
							//viewUrl = viewUrl.substring(0,viewUrl.indexOf("', '"));
							
							 subject = match.group(2);
							 subject= StringUtils.trim(subject);
							 Matcher match_num = pattern_spannum.matcher(subject);
							 subject= subject.replace("<span class=\"point\">[공지]</span> 					","");
							 
							 if( subject.indexOf("<span") >0 )
							 {
								 subject= subject.substring(0,subject.indexOf("<span")-1); //<span class="num">(10)</span> 삭제
							 }
							//  System.out.println("subject:"+subject);
							 
							
							//
							subject = subject.trim();
							subject = subject.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
							subject= subject.replaceAll("&nbsp;", "");
							subject= subject.replaceAll("\n", "\r\n");
							// System.out.println("===================subject:"+subject+"====================");
							//
							vo.setRnum(rnum);
							vo.setViewUrl(viewUrl);
							
							
							//vo.setContent(licomment);
							
							vo.setSubject(subject);
							if (match_num.find()) {
								// System.out.println("=====================2==================");
								String num = match_num.group(2);
								//System.out.println(owner);
								subject= subject+num;
								vo.setSubject(subject);	
								//System.out.println("subject:"+subject);
							}
							
							 

							Matcher match_owner = pattern_owner.matcher(licomment);
							vo.setContent(licomment);
							if (match_owner.find()) {
								// System.out.println("=====================2==================");
								String owner = match_owner.group(2);
								//System.out.println(owner);
								vo.setIdAlais(owner);
							}

							/*Matcher match_article = pattern_article_body
									.matcher(licomment);
							if (match_article.find()) {
								// System.out.println("=====================3==================");
								String article = match_article.group(2);
								//System.out.println(article);
								vo.setSubject(article);
							}*/
							Matcher match_dt = pattern_dt.matcher(licomment);
							if (match_dt.find()) {
								// System.out.println("=====================4==================");
								String dt = match_dt.group(2);
								//System.out.println(dt);
								vo.setCreatYmd(dt);
							}
							lst.add(vo);
						}
					}
				}
			}
		}
		return lst;
	}

	private DaumListVo setCommentForJavascript(List<DaumListVo> lst,
			DaumListVo vo, String licomment, Matcher match) {
		vo =null;
		while (match.find()) {
			String subject ="";
			// System.out.println(match);
			String listurl = match.group(0);
			// System.out.println("===================listurl:"+listurl+"===================="); 
			String listurl1 = match.group(1);
			String listurl2 = match.group(2);
			
			
			  //System.out.println("================listurl1:==="+listurl1+"====================");
			  //System.out.println("================listurl2:==="+listurl2+"====================");
			 
			if (listurl1.startsWith("#")) {
				if (listurl2.startsWith("loadComments(")
						&& !listurl1.startsWith("/CHILIL/_memo/edit")
						&& !listurl1.startsWith("/CHILIL/_memo/new")
						&& !listurl1.startsWith("/CHILIL/_memo/57780")
						&& !listurl1.startsWith("/CHILIL/_memo/57718")) { 
					listurl2 = match.group(2); 
					//System.out.println("listurl1:"+listurl1);
					String rnum = listurl2.replace("loadComments('", "");
					rnum = rnum.replace("/CHILIL/_memo/", "");
					rnum = rnum.substring(0, rnum.indexOf("/comments"));
					if( "57780".equals(rnum)
						|| "57718".equals(rnum)
							)
					{
						continue;
					}
					vo = new DaumListVo();
					String viewUrl = listurl2.replace("loadComments('","");
					viewUrl = viewUrl.substring(0,viewUrl.indexOf("', '"));
					
					 subject = match.group(3);
					 subject= StringUtils.trim(subject);
					 Matcher match_num = pattern_spannum.matcher(subject);
					 subject= subject.replace("<span class=\"point\">[공지]</span> 					","");
					 
					 subject= subject.substring(0,subject.indexOf("<span")-1); //<span class="num">(10)</span> 삭제
					// System.out.println("subject:"+subject);
					 
					
					//
					subject = subject.trim();
					subject = subject.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
					subject= subject.replaceAll("&nbsp;", "");
					subject= subject.replaceAll("\n", "\r\n");
					// System.out.println("===================subject:"+subject+"====================");
					//
					vo.setRnum(rnum);
					vo.setViewUrl(viewUrl);
					
					
					//vo.setContent(licomment);
					
					vo.setSubject(subject);
					if (match_num.find()) {
						// System.out.println("=====================2==================");
						String num = match_num.group(2);
						//System.out.println(owner);
						subject= subject+num;
						vo.setSubject(subject);	
						//System.out.println("subject:"+subject);
					}
					
					

					Matcher match_owner = pattern_owner.matcher(licomment);
					vo.setContent(licomment);
					if (match_owner.find()) {
						// System.out.println("=====================2==================");
						String owner = match_owner.group(2);
						//System.out.println(owner);
						vo.setIdAlais(owner);
					}

					/*Matcher match_article = pattern_article_body
							.matcher(licomment);
					if (match_article.find()) {
						// System.out.println("=====================3==================");
						String article = match_article.group(2);
						//System.out.println(article);
						vo.setSubject(article);
					}*/
					Matcher match_dt = pattern_dt.matcher(licomment);
					if (match_dt.find()) {
						// System.out.println("=====================4==================");
						String dt = match_dt.group(2);
						//System.out.println(dt);
						vo.setCreatYmd(dt);
					}
					lst.add(vo);
				}
			}
		}
		return vo;
	}

	public List<DaumListVo> setDaumListVoList_back(String listBody, Map pPageMap) {
		DaumListVo vo = new DaumListVo();
		Matcher match = pattern_atag.matcher(listBody);
		List<DaumListVo> lst = new ArrayList<DaumListVo>();
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		while (match.find()) {
			// System.out.println(match);
			String listurl = match.group(0);
			//System.out.println("===================listurl:" + listurl+ "====================");

			String listurl1 = match.group(1);
			String listurl2 = match.group(2);
			/*
			 * System.out.println("================listurl1:==="+listurl1+
			 * "====================");
			 * System.out.println("================listurl2:==="
			 * +listurl2+"====================");
			 */
			if (listurl1.startsWith("#")) {
				if (listurl2.startsWith("loadComments(")
						&& !listurl1.startsWith("/CHILIL/_memo/edit")
						&& !listurl1.startsWith("/CHILIL/_memo/new")) {
					vo = new DaumListVo();

					listurl2 = match.group(2);
					String rnum = listurl2.replace("loadComments('", "");
					rnum = rnum.replace("/CHILIL/_memo/", "");
					rnum = rnum.substring(0, rnum.indexOf("/comments"));

					String viewUrl = listurl2.replace("loadComments('", "");
					viewUrl = viewUrl.substring(0, viewUrl.indexOf("', '"));
					vo.setRnum(rnum);
					vo.setViewUrl(viewUrl);
					lst.add(vo);

				}
			}
		}
		return lst;
	}

	public String setDaumView(String listBody) {
		DaumListVo vo = new DaumListVo();
		String listurl1 = "";
		Matcher match = pattern_div_article.matcher(listBody);
		if (match.find()) {
			// System.out.println(match);
			String listurl = match.group(1);
			// System.out.println("===================0====================");
			// System.out.println(listurl);
			listurl1 = match.group(2);
			// System.out.println(listurl1);

		}
		return listurl1;
	}

	/**
	 * 코멘트의 리스트 처리
	 * 
	 * @param listBody
	 * @param pPageMap
	 * @return
	 */
	public List<DaumListVo> setDaumListVoCommentList(String listBody,
			String rRum, Map pCommentPageMap) {
		DaumListVo vo = null;
		Matcher match = pattern_comment_atag.matcher(listBody);
		List<DaumListVo> lst = new ArrayList<DaumListVo>();
		String strConsts = "/CHILIL/" + BOARD_TYPE + "/" + rRum + "/comments";
		//"/CHILIL/_memo/44537/comments?prev_page=
		//System.out.println(">>>>>>>>>>>>>>>>>>Comment>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		while (match.find()) {
			// System.out.println(match);
			String listurl = match.group(0);
			// System.out.println("===================0====================");

			String listurl1 = match.group(1);
			
			if (listurl1.startsWith(strConsts + "?prev_page=")) {
				String pageUrl = listurl1.replaceAll("&amp;", "&");
				String[] aPage = pageUrl.split("&");
				String nPage = aPage[aPage.length - 1].split("=")[1];
				pCommentPageMap.put(nPage, pageUrl);
			}
		}
		Matcher match_li = pattern_li.matcher(listBody);
		while (match_li.find()) {
			String licomment = match_li.group(1);
			 // System.out.println("licomment:"+licomment);
			if (!licomment.startsWith("<a")) {
				vo = new DaumListVo();

				Matcher match_owner = pattern_owner.matcher(licomment);
				// vo.setViewUrl(licomment);
				vo.setContent(licomment);
				if (match_owner.find()) {
					// System.out.println("=====================2==================");
					String owner = match_owner.group(2);
					// System.out.println(owner);
					vo.setIdAlais(owner);
				}
				Matcher match_article = pattern_article_body.matcher(licomment);
				if (match_article.find()) {
					// System.out.println("=====================3==================");
					String article = match_article.group(2);
					 
					String r="<em class=\"new\">new</em>";
					article = article.replace(r, "");
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
