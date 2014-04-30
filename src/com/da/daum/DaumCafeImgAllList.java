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
 * --리스트
 * http://m.cafe.daum.net/CHILIL/QQN?boardType=5&prev_page=16&firstbbsdepth=0004e&lastbbsdepth=0004V&noticeYn=&page=20
 * http://m.cafe.daum.net/CHILIL/QQN?boardType=5&prev_page=1&firstbbsdepth=0007s&lastbbsdepth=0007b&noticeYn=&page=2
 * ---뷰
 * http://m.cafe.daum.net/CHILIL/LPN/333?listURI=%2FCHILIL%2FLPN%3Fprev_page%3D1%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bfirstbbsdepth%3D00062%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Blastbbsdepth%3D0005o%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3BnoticeYn%3D%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bpage%3D3
 * --코멘트
 * http://m.cafe.daum.net/CHILIL/LPN/333/comments?prev_page=3&listURI=%2FCHILIL%2FLPN%3Fprev_page%3D1%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bfirstbbsdepth%3D00062%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Blastbbsdepth%3D0005o%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3BnoticeYn%3D%26amp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bamp%3Bpage%3D3&mode=regular&cdepth=0000f00000&page=1&
 * http://m.cafe.daum.net/CHILIL/LPN/318/comments?prev_page=1&listURI=%2FCHILIL%2FLPN%3Fprev_page%3D3%26amp%3Bamp%3Bamp%3Bfirstbbsdepth%3D0005V%26amp%3Bamp%3Bamp%3Blastbbsdepth%3D0005I%26amp%3Bamp%3Bamp%3BnoticeYn%3D%26amp%3Bamp%3Bamp%3Bpage%3D4&mode=regular&cdepth=0000F00000&page=2&
prev_page	1
listURI	/CHILIL/LPN?prev_page=3&amp;amp;amp;firstbbsdepth=0005V&amp;amp;amp;lastbbsdepth=0005I&amp;amp;amp;noticeYn=&amp;amp;amp;page=4
mode	regular
cdepth	0000F00000
page	2
------------------------------
prev_page	2
listURI	/CHILIL/LPN?prev_page=3&amp;amp;amp;amp;firstbbsdepth=0005V&amp;amp;amp;amp;lastbbsdepth=0005I&amp;amp;amp;amp;noticeYn=&amp;amp;amp;amp;page=4
mode	regular
cdepth	0000S0000W
page	3
 * @author changwng
 * 
 */
public class DaumCafeImgAllList {
	private static String SO_URL = "daum.net";
	private static String host_url = "http://m.cafe.daum.net";
	private static String photo_url = "http://photo." + SO_URL
			+ "/album/theme/";
	private static String story_url = "http://story." + SO_URL + "/honor/";

	private static String SAVE_DIR = "c:/temp/daum/moim/";
	private static String STORY_DIR = "c:/temp";
	private Map pageMap = new HashMap();
	DaumCafeBungImgParser parser =null;
	static Pattern pattern = Pattern
			.compile("<a[^>]*href=[\"']?/_c21_/album_read([^>\"']+)[\"']?[^>]*>");
	//static Pattern pattern_img = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"'](.*?)class=\"txc-image\"?(.*?)[^>]*>");
	//static Pattern pattern_img = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"'](.*?)class=\"tx-daum-image\"?(.*?)[^>]*>");
	//static Pattern pattern_img = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"'](.*?)class=\"c\" alt=\"이미지를 클릭하면 원본을 보실 수 있습니다.\"?(.*?)[^>]*>");
	
	static Pattern pattern_img = Pattern.compile("<IMG[^>]*class=c alt=\"이미지를 클릭하면 원본을 보실 수 있습니다.\"(.*?)src=[\"']?([^>\"']+)[\"'](.*?)?(.*?)[^>]*>");
	static Pattern pattern_author = Pattern.compile(
			"<span(.*?)style=\"cursor:hand\">(.*?)<\\/span>", Pattern.MULTILINE
					+ Pattern.CASE_INSENSITIVE); // all html tag

	static Log log = LogFactory.getLog(Class.class);

	public static void main(String[] args) {
		DaumCafeImgAllList cfl = new DaumCafeImgAllList();

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
			 executeLogin(httpclient);
			 this.parser = new DaumCafeBungImgParser();
			int max_page = 10000;
			int init_page = 188;
			 p_page="188"; 
			if (!"".equals(StringUtils.stripToEmpty(p_page))) {
				init_page = Integer.parseInt(p_page);
			}
			String output = "";
			String next_page="";
			InputStream istream;
			int idx =0; 
			for (int i = init_page; i < max_page; i++) {
				if(i==init_page)
				{
					//strUrl = "http://cafe308.daum.net/_c21_/image_bbs_read?grpid=i6V&fldid=_album&page=&prev_page=&firstbbsdepth=&lastbbsdepth=&contentval=000Hzzzzzzzzzzzzzzzzzzzzzzzzzz&datanum=1115&edge=&listnum=";
					//15
					//strUrl = "http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=_album&listnum=&datanum=1073&contentval=000HJzzzzzzzzzzzzzzzzzzzzzzzzz";
					//75
					strUrl = "http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=_album&listnum=&datanum=847&contentval=000Dfzzzzzzzzzzzzzzzzzzzzzzzzz";
					//207
					strUrl = "http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=_album&listnum=&datanum=373&contentval=00061zzzzzzzzzzzzzzzzzzzzzzzzz";
					// 모임사진
					strUrl ="http://cafe308.daum.net/_c21_/album_read?grpid=i6V&fldid=QQN&page=1&prev_page=0&firstbbsdepth=&lastbbsdepth=zzzzzzzzzzzzzzzzzzzzzzzzzzzzzz&contentval=0007szzzzzzzzzzzzzzzzzzzzzzzzz&datanum=488&edge=F&listnum=15";
					//4
					strUrl ="http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=QQN&listnum=&datanum=478&contentval=0007izzzzzzzzzzzzzzzzzzzzzzzzz";
					//63
					strUrl ="http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=QQN&listnum=&datanum=313&contentval=00053zzzzzzzzzzzzzzzzzzzzzzzzz";
					//166
					strUrl ="http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=QQN&listnum=&datanum=55&contentval=0000tzzzzzzzzzzzzzzzzzzzzzzzzz";
					//188
					strUrl ="http://cafe308.daum.net/_c21_/album_read?grpid=i6V&mgrpid=&fldid=QQN&listnum=&datanum=2&contentval=00002zzzzzzzzzzzzzzzzzzzzzzzzz";
				}else
				{strUrl = next_page;}
				Thread.sleep(1500);
				//System.out.println("strUrl:"+strUrl);
				responseBody =  execGetUrl(httpclient,strUrl); 
				Matcher match_next = pattern.matcher(responseBody);
				while(match_next.find())
				{
					next_page =  match_next.group(1);
					next_page =  "http://cafe308.daum.net/_c21_/album_read"+next_page.replaceAll("&amp;", "&");
				}
				System.out.println("strUrl "+String.valueOf(i)+":"+strUrl);
				//이미지 분석처리
				Matcher match = pattern_img.matcher(responseBody); 
				
				while (match.find()) {
					  String imgUrl = match.group(2);
					 
						  idx++;
						  System.out.println("imgUrl:"+imgUrl);
						  istream = getDownloadUrlInputStream(httpclient,imgUrl);
						  output = SAVE_DIR+"/img/chiLil_"+i+"_"+StringUtils.leftPad(String.valueOf(idx), 10, "0")+".jpg"; 
						  	fileDownCopy(output, istream);
							if (istream != null) {
								istream.close();
							}
				}
				//saveViewFile(responseBody, file); 
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



	private void saveViewFile(String responseBody, String file)
			throws IOException {
		String viewBody;
		viewBody = parser.setDaumView(responseBody);
		//viewBody= this.htmlRemove(viewBody);
		//viewBody= StringEscapeUtils.escapeHtml(viewBody);
		viewBody = viewBody.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
		viewBody= viewBody.replaceAll("&nbsp;", "");
		viewBody= viewBody.replaceAll("\n", "\r\n");

		FileHelper.createFile(file);
		FileUtils.writeStringToFile(new  File(file), viewBody, "utf-8");
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
			if (FileHelper.createFile(output)) {
				System.out.println("save File:" + output);
				FileOutputStream os = new FileOutputStream(output);
				IOUtils.copy(istream, os);
				if (os != null) {
					os.close();
				}
			}
			/*if (!(new File(output)).exists()) {
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
			}*/
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
		
		String strUrl =   host_url+"/CHILIL/QQN?prev_page="+String.valueOf(prev_page)+"&firstbbsdepth=0007s&lastbbsdepth=0007b&noticeYn=&page=" + n_page;
		// http://cafe308.daum.net/_c21_/image_bbs_read?grpid=i6V&fldid=_album&page=&prev_page=&firstbbsdepth=&lastbbsdepth=&contentval=000Hzzzzzzzzzzzzzzzzzzzzzzzzzz&datanum=1115&edge=&listnum=
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
