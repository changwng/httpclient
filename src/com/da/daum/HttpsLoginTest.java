package com.da.daum;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.junit.Test;

public class HttpsLoginTest {
	@Test  
	public void testHttpsLogin() throws  Exception {  
		HttpClient httpclient =null;
		  		httpclient = getPoolHttpClient(); 
			String responseBody = "";
			 
			//httpclient.setRedirectHandler(new spaceRedirectHandler());
			 
			 HttpGet httpget = new HttpGet();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			
			responseBody =  executeLogin(httpclient); 
			System.out.println(responseBody);
			String strUrl ="http://m.cafe.daum.net//CHILIL/LAK?prev_page=1&amp;firstbbsdepth=000CW&amp;lastbbsdepth=000CJ&amp;noticeYn=&amp;page=3";
			
			 responseBody = getDownloadUrl(httpclient, httpget,
					 responseHandler, strUrl);
					//  responseBody = CHttpUtil.DownloadHtml(strUrl);
					System.out.println(responseBody); 
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
	private String executeLogin(HttpClient httpclient)
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
		String retStr =execPostWidthParam(httpclient, login_url,nvps);
		
		return retStr;

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

}
