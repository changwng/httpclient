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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.da.img.FileHelper;

  /**
   * Jw0: 벙게신청 페이지
   * 
   * @author changwng
   *
   */
public class DaumCafeJw0List {
	private static String SO_URL = "daum.net";
	private static String host_url = "http://m.cafe.daum.net";
	private static String photo_url = "http://photo." + SO_URL
			+ "/album/theme/";
	private static String story_url = "http://story." + SO_URL + "/honor/";

	private static String SAVE_DIR = "c:/temp/daum";
	private static String STORY_DIR = "c:/temp";
	public  static String BOARD_TYPE ="Jw0";
	private Map pageMap = new HashMap();
	private Map commentPageMap = new HashMap();
	private DaumCafeJw0Parser parser =null;
	static Pattern pattern = Pattern
			.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
	static Pattern pattern_img = Pattern
			.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
	static Pattern pattern_author = Pattern.compile(
			"<span(.*?)style=\"cursor:hand\">(.*?)<\\/span>", Pattern.MULTILINE
					+ Pattern.CASE_INSENSITIVE); // all html tag

	static Log log = LogFactory.getLog(Class.class);

	public static void main(String[] args) {
		DaumCafeJw0List cfl = new DaumCafeJw0List();

		log.warn("Logging Works");
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");

		System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
				"true");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.httpclient.wire",
				"debug");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
				"debug");

		try {
			// https://msp.f-secure.com/web-test/common/test.html
			// String body =
			// CHttpUtil.DownloadHtml("https://logins.daum.net/accounts/loginform.do?mobilefull=1&t__nil_footer=login&url=http%3a%2f%2fm%2edaum%2enet%2f");
			/*
			 * String body = CHttpUtil.DownloadHtml(
			 * "https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=cw89040310&stln=on&saved_id=on"
			 * ); System.out.println(body);
			 */
			String nPage = "1";
			String p_author_id = "bluesman";
			String p_gnum = ""; // ga
			String p_host_url = ""; // ga
			if (args.length > 0) {
				nPage = args[0];
			}
			if (args.length > 1) {
				p_author_id = args[1];
			}
			if (args.length > 2) {
				STORY_DIR = args[2];
			}

			if (args.length > 3) {
				p_host_url = args[3];
				SO_URL = p_host_url;
				/*
				 * host_url = "http://www."+SO_URL; photo_url =
				 * "http://photo."+SO_URL+"/album/theme/"; story_url =
				 * "http://story."+SO_URL+"/honor/";
				 */
			}
			cfl.executeURL(nPage, p_author_id, p_gnum);
			// cfl.executeAuthorList(nPage, p_author_id, p_gnum);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 

	protected void executeURL(String p_page, String p_author_id, String p_gnum)
			throws IOException, ClientProtocolException, URISyntaxException {
		HttpClient httpclient =null;
		try {
			httpclient = getPoolHttpClient(); 
			String responseBody = "";
			String strUrl = "";
			String file ="";
			//httpclient.setRedirectHandler(new spaceRedirectHandler());
			List<DaumListVo> lst = null;
			 executeLogin(httpclient);
			 this.parser = new DaumCafeJw0Parser();
			int max_page = 10000;
			int init_page = 1;
			 p_page="1";
			String viewBody="";
			if (!"".equals(StringUtils.stripToEmpty(p_page))) {
				init_page = Integer.parseInt(p_page);
			}
			String output = "",bodyAndComment="";
			InputStream istream;
			for (int i = init_page; i < max_page; i++) {
				lst = getBoardList(httpclient,  String.valueOf(i));
				if(lst.size()<1)
				{
					break;
				}
				for (DaumListVo vo : lst) {
					  
					 strUrl =   host_url+vo.getViewUrl();
					System.out.println("=========================================");
					System.out.println(strUrl);
					System.out.println("=========================================");
					file = SAVE_DIR+"/"+BOARD_TYPE+"/"+vo.getRnum()+"."+vo.getSubject().replaceAll("\\*", "").replaceAll("\\/", "_").replaceAll("\"", "_")+"_"
					+vo.getIdAlais().replaceAll("\\*", "").replaceAll("\\/", "_").replaceAll("\"", "_")
					+"_"+vo.getCreatYmd()+""
					+".txt";
					System.out.println("save file:"+file);
					//코멘트 분석처리
					responseBody =  execGetUrl(httpclient,strUrl); 
				 
					//이미지 분석처리
					saveImageFile(httpclient, responseBody, vo);
					//내용 저장
					String comment = getSaveComment(httpclient, vo);
					
					bodyAndComment= saveViewFile(responseBody, file)+"\r\n\r\n"+comment;
					
					FileHelper.createFile(file);
					FileUtils.writeStringToFile(new  File(file), bodyAndComment, "utf-8");
					//코멘트 분석
					//strUrl+/comments?page=1
					// 여기서 페이지 돌면서 코멘트를 푼다. /316
					//strUrl =   host_url+"/CHILIL/LPN/"+vo.getRnum()+"/comments?page=1";
				
					// List<DaumListVo> lstComment = parser.setDaumListVoCommentList(responseBody, "316", commentPageMap);
				}
			} 

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			System.out.println("ERROR: " + ex.getLocalizedMessage());
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}



	private String getSaveComment(HttpClient httpclient, DaumListVo vo)
			throws IOException, ClientProtocolException {
		String responseBody;
		String strUrl;
		List<DaumListVo> lstComment = null;
		StringBuilder sb = new StringBuilder();
		commentPageMap = new HashMap();
		for (int x = 1; x < 50; x++) {
			if(x==1)
			{
				strUrl =  host_url+"/CHILIL/"+BOARD_TYPE+"/"+vo.getRnum()+"/comments?page=1";
				// host_url+"/CHILIL/LPN/316/comments?page=1";
			}else
			{
				strUrl = host_url+(String) commentPageMap.get(String.valueOf(x));
			}
			if(strUrl.equals("http://m.cafe.daum.netnull"))
			{
				 lstComment= new ArrayList<DaumListVo>();
			}else
			{
				 System.out.println("commnet strUrl:"+strUrl);
				 responseBody =  execGetUrl(httpclient,strUrl); 
				 lstComment = parser.setDaumListVoCommentList(responseBody, vo.getRnum(), commentPageMap);
				 //lstComment = parser.setDaumListVoCommentList(responseBody, "316", commentPageMap);
			}
			 if(lstComment.size()<1)
			{
				break;
			}
			for (DaumListVo commentvo : lstComment) {
				//System.out.println(commentvo.getSubject());
				sb.append(commentvo.getIdAlais()+"|"+commentvo.getCreatYmd()+"\r\n\t"+commentvo.getSubject()+"\r\n");
			}
		}
		return sb.toString();
	}



	private void saveImageFile(HttpClient httpclient, String responseBody,
			DaumListVo vo) throws URISyntaxException, IOException,
			ClientProtocolException {
		String output;
		InputStream istream;
		Matcher match = pattern_img.matcher(responseBody); 
		int idx =0; 
		while (match.find()) {
			  String imgUrl = match.group(1);
			  if(!"http://m1.daumcdn.net/cafeimg/mobile/320/v02/common/2010/blue/daumlogo.gif".equals(imgUrl))
			  {
				  idx++;
				 // System.out.println("imgUrl:"+imgUrl);
				  istream = getDownloadUrlInputStream(httpclient,imgUrl);
				  output = SAVE_DIR+"/"+BOARD_TYPE+"/"+vo.getRnum()+"_"+String.valueOf(idx)+".jpg"; 
				  	fileDownCopy(output, istream);
					if (istream != null) {
						istream.close();
					}
			  }
		}
	}
	private String saveViewFile(String responseBody, String file)
			throws IOException {
		String viewBody;
		viewBody = parser.setDaumView(responseBody);
		//viewBody= this.htmlRemove(viewBody);
		//viewBody= StringEscapeUtils.escapeHtml(viewBody);
		viewBody = viewBody.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
		viewBody= viewBody.replaceAll("&nbsp;", "");
		viewBody= viewBody.replaceAll("\n", "\r\n");

		return viewBody;
	}

	private HttpClient getPoolHttpClient() {
		HttpClient httpclient;
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
		new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(
		new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		// Increase max connections for localhost:80 to 50
		HttpHost localhost = new HttpHost("locahost", 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		//DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient = new DefaultHttpClient(cm);
		return httpclient;
	}

	private void fileDownCopy(String output, InputStream istream) {
		try {
			if (!(new File(output)).exists()) {
				if (FileHelper.createFile(output)) {
					System.out.println("save File:" + output);
					FileOutputStream os = new FileOutputStream(output);
					IOUtils.copy(istream, os);
					if (os != null) {
						os.close();
					}
				}
			} else {
				System.out.println("Image File Exist :" + output);
			}
		} catch (Exception ex) {
		}
	}

	private List<DaumListVo> getBoardList(HttpClient httpclient,   String n_page ) throws ClientProtocolException, IOException
			 {
		String listBody = "", viewBody = "";
		//String strUrl =   "http://m.cafe.daum.net//CHILIL/LAK?prev_page=1&amp;firstbbsdepth=000CW&amp;lastbbsdepth=000CJ&amp;noticeYn=&amp;page=" + n_page;
		int prev_page  = Integer.parseInt(n_page)-1;
		if(prev_page<1){prev_page=1;}
		// prev_page=1&firstbbsdepth=000CW&lastbbsdepth=000CJ&noticeYn=&page=1
		
		String strUrl =   host_url+"/CHILIL/"+BOARD_TYPE+"?prev_page="+String.valueOf(prev_page)+"&firstbbsdepth=0005n&lastbbsdepth=0005W&noticeYn=&page=" + n_page;
		if(!"1".equals(n_page))
		{
			strUrl = host_url+(String) pageMap.get(n_page);
		}
		
		//String strUrl =   "http://m.cafe.daum.net/CHILIL/LAK?prev_page=350firstbbsdepth=000Bi&lastbbsdepth=000BZ&noticeYn=&page=" + n_page;
		System.out.println("=========================================");
		System.out.println(strUrl);
		System.out.println("=========================================");
		if(strUrl.equals("http://m.cafe.daum.netnull"))
		{
			return new ArrayList<DaumListVo>();
		}
		listBody = execGetUrl(httpclient,strUrl); 
	   
		return parser.setDaumListVoList(listBody,this.pageMap);
	}

	private String getViewImageUrlSwitch(String viewurl) {

		String p_imgwidth = viewurl.substring(viewurl.indexOf("p_imgwidth"),
				viewurl.indexOf("&p_imgheight"));
		p_imgwidth = StringUtils.replace(p_imgwidth, "p_imgwidth=", "");
		// System.out.println("p_imgwidth src :"+p_imgwidth);
		String p_imgheight = viewurl.substring(viewurl.indexOf("p_imgheight"),
				viewurl.indexOf("&p_width"));
		p_imgheight = StringUtils.replace(p_imgheight, "p_imgheight=", "");
		// System.out.println("p_imgheight src :"+p_imgheight);
		String[] aUrl = viewurl.split("&");
		if (aUrl.length == 6) {
			aUrl[2] = "p_width=" + p_imgwidth;
			aUrl[3] = "p_height=" + p_imgheight;
			viewurl = StringUtils.join(aUrl, "&");
		}

		return viewurl;
	}

	private String getDownloadUrl(HttpClient httpclient,
			HttpGet httpget, ResponseHandler<String> responseHandler,
			String p_url) throws URISyntaxException, IOException,
			ClientProtocolException { 
		String ret = "";
		try {
			httpget.setURI(new URI(p_url));
			ret = httpclient.execute(httpget, responseHandler);
		} catch (Exception ex) {
			System.out.println("getDownloadUrl ERROR:"
					+ ex.getLocalizedMessage());
		}
		return ret;
		// return responseBody;
	}

	private InputStream getDownloadUrlInputStream(HttpClient httpclient,
			 String p_url) throws URISyntaxException,
			IOException, ClientProtocolException {
		// p_url = URLEncoder.encode(p_url, "utf-8");
		// System.out.println("p_url2:"+p_url);
		InputStream is = null;
		try {
			//HttpGet httpget = new HttpGet();
			HttpGet httpget = new HttpGet(p_url);
			//httpget.setURI(new URI(p_url));
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			is = resEntity.getContent();
			;
		} catch (Exception ex) {
		}
		return is;
	}

	private HttpGet executeLogin(HttpClient httpclient)
			throws IOException, ClientProtocolException {
		// HttpGet httpget = new
		// HttpGet("https://logins.daum.net/accounts/loginform.do?mobilefull=1&t__nil_footer=login&url=http%3a%2f%2fm%2edaum%2enet%2f");
		//HttpGet httpget = new HttpGet("http://m.daum.net/");
		/*
		 * HttpResponse response = httpclient.execute(httpget);
		 * System.out.println("----------------------------------------");
		 * Header[] headers = response.getAllHeaders(); for (int i = 0;
		 * i<headers.length; i++) { System.out.println(headers[i]); } HttpEntity
		 * entity = response.getEntity(); System.out.println("Login form get: "
		 * + response.getStatusLine()); EntityUtils.consume(entity);
		 * System.out.println("Initial set of cookies:");
		 */
		/*
		 * List<Cookie> cookies = httpclient.getCookieStore().getCookies(); if
		 * (cookies.isEmpty()) { System.out.println("None"); } else { for (int i
		 * = 0; i < cookies.size(); i++) { System.out.println("- " +
		 * cookies.get(i).toString()); } }
		 */
		// https://logins.daum.net/accounts/loginform.do?mobilefull=1&t__nil_footer=login&url=http%3a%2f%2fm%2edaum%2enet%2f
		// Protocol.registerProtocol("https", new Protocol("https",new
		// EasySSLProtocolSocketFactory(), 443));
		// https://logins.daum.net/accounts/login.do
		// http://login.daum.net/accounts/loginform.do?url=http%3A%2F%2Fmail.daum.net%2F
		// http://login.daum.net/accounts/presrp.do?id=changwng&srpla=11064368d4975eabadfeb0348f516975bc10c0c9d7b2b30f79c2e69e9182feb88
		//String login_url = "https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=qncjdjssl&stln=on&saved_id=on";
		String login_url = "https://logins.daum.net/accounts/mobile.do";
		List<NameValuePair> nvps  = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("url", "http://m.daum.net/")); 
		nvps.add(new BasicNameValuePair("relative", "")); 
		nvps.add(new BasicNameValuePair("mobilefull", "1")); 
		nvps.add(new BasicNameValuePair("weblogin", "1")); 
		nvps.add(new BasicNameValuePair("id", "changwng")); 
		nvps.add(new BasicNameValuePair("pw", "qncjdjssl"));
		nvps.add(new BasicNameValuePair("stln", "on")); 
		nvps.add(new BasicNameValuePair("saved_id", "on")); 
		execPostWidthParam(httpclient, login_url,nvps);
		
		return null;

	}

	private String execPostWidthParam(HttpClient httpclient, String p_url,List<NameValuePair> nvps)
			throws IOException, ClientProtocolException {
		String retStr = "";
		HttpResponse response;
		HttpEntity entity;
		List<Cookie> cookies;
		HttpPost httpost = new HttpPost(p_url);
		//List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		/*
		 * nvps.add(new BasicNameValuePair("id", "changwng")); nvps.add(new
		 * BasicNameValuePair("pw", "cw89040310"));
		 */

		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		response = httpclient.execute(httpost);
		entity = response.getEntity();

	//	System.out.println("Login form get: " + response.getStatusLine());

		/*
		 * System.out.println("----------------------------------------");
		 * Header[] headers = response.getAllHeaders(); for (int i = 0;
		 * i<headers.length; i++) { System.out.println(headers[i]); }
		 * System.out.println("----------------------------------------");
		 * System.out.println("Post logon cookies:"); cookies =
		 * httpclient.getCookieStore().getCookies(); if (cookies.isEmpty()) {
		 * System.out.println("None"); } else { for (int i = 0; i <
		 * cookies.size(); i++) { System.out.println("- " +
		 * cookies.get(i).toString()); } }
		 */
		retStr = EntityUtils.toString(entity);
		//System.out.println("retStr:" + retStr);
		
		  EntityUtils.consume(entity);
		return retStr;
	}
	private String execGetUrl(HttpClient httpclient, String p_url)
			throws IOException, ClientProtocolException {
		String ret = "";  
		HttpGet httpget = new HttpGet(p_url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		try {
			httpget.setURI(new URI(p_url));
			ret = httpclient.execute(httpget, responseHandler);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			
		}
		 
		 
 
		return ret;
	}
	private String execPostUrl(HttpClient httpclient, String p_url)
			throws IOException, ClientProtocolException {
		String retStr = "";
		HttpResponse response;
		HttpEntity entity;
		List<Cookie> cookies;
		HttpPost httpost = new HttpPost(p_url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		/*
		 * nvps.add(new BasicNameValuePair("id", "changwng")); 
		 * nvps.add(new BasicNameValuePair("pw", "cw89040310"));
		 */

		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		response = httpclient.execute(httpost);
		entity = response.getEntity();

		//System.out.println("Login form get: " + response.getStatusLine());

		/*
		 * System.out.println("----------------------------------------");
		 * Header[] headers = response.getAllHeaders(); for (int i = 0;
		 * i<headers.length; i++) { System.out.println(headers[i]); }
		 * System.out.println("----------------------------------------");
		 * System.out.println("Post logon cookies:"); cookies =
		 * httpclient.getCookieStore().getCookies(); if (cookies.isEmpty()) {
		 * System.out.println("None"); } else { for (int i = 0; i <
		 * cookies.size(); i++) { System.out.println("- " +
		 * cookies.get(i).toString()); } }
		 */
		retStr = EntityUtils.toString(entity);
		// System.out.println("retStr:" + retStr);
		  EntityUtils.consume(entity);
		return retStr;
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
