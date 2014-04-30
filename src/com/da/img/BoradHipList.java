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

package com.da.img;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class BoradHipList {
	private static String SO_URL = "soraven.info";
	private static String host_url = "http://www."+SO_URL;
	private static String photo_url = "http://photo."+SO_URL+"/album/theme/";
	static Pattern pattern = Pattern
			.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
	static Pattern pattern_img = Pattern
			.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");

	public static void main(String[] args) {
		BoradHipList cfl = new BoradHipList();
		try {
			String nPage = "1";
			if (args.length > 0) {
				nPage = args[0];
			}
			cfl.executeURL(nPage);
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

	protected void executeURL(String p_page) throws IOException,
			ClientProtocolException, URISyntaxException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = executeLogin(httpclient);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = "";
			// /bank/story_mn.php?p_userid=bluesman&p_snum=201&p_num=35788
			// String strUrl =
			// "http://story.soraspace.info/bank/story_mn.php?p_userid=bluesman&p_snum=201&p_num=35821";
			// 몸승부 :
			// http://photo.soraspace.info/album/theme/pic_list.php?p_anum=173&p_ix=3&p_gnum=351
			// http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=
			// http://photo.soraspace.info/album/theme/pic_list.php?p_page=2&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=
			// http://photo.soraspace.info/album/theme/pic_list.php?p_page=3&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=
			// http://photo.soraspace.info/album/theme/pic_list.php?p_page=3&p_sort=D&p_anum=351&p_gnum=351&p_soption=&p_stxt=

			String imgUrl;
			// p_anum=244&p_ix=1 인물 셀프
			// p_anum=281&p_ix=2 패티시
			// p_anum=173&p_ix=3 몸짱이다
			// p_gnum= 351 : mom , 481: lip , 352: hip,
			// 354:ga,353:leg,442:pussy,
			String SaveFilePath = "c:/temp/173";
			String p_gnum = "352";
			List<ImageVo> lst = null;
			int max_page = 10000;
			String output = "";
			InputStream istream;
			int init_page = 1;
			if (!"".equals(StringUtils.stripToEmpty(p_page))) {
				init_page = Integer.parseInt(p_page);
			}
			for (int i = init_page; i < max_page; i++) {
				lst = getBoardList(httpclient, httpget, responseHandler,
						p_gnum, String.valueOf(i));
				for (ImageVo vo : lst) {
					// continue;
					imgUrl = vo.getImgUrl(); // "http://photo2.soraspace.info/thumbnail.php?p_imgwidth=1104&p_imgheight=1104&p_width=1104&p_height=1104&p_imgfile=%2F%2F201205%2F22%2Fsk650%2F%2F1501694.jpg&verify=%2F%90%F9%B7%D6%D0%12%0EF%17%B8M%DA%A4L%C4";
					istream = getDownloadUrlInputStream(httpclient, httpget,
							imgUrl);
					// System.out.println("result = " + istream.toString());
					output = SaveFilePath
							+ "/"
							+ p_gnum
							+ "/"
							+ org.apache.commons.lang.StringUtils.leftPad(
									String.valueOf(i), 4, "0") + "/"
							+ vo.getFileName();
					fileDownCopy(output, istream);
					if (istream != null) {
						istream.close();
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("ERROR: " + ex.getLocalizedMessage());
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
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

	private List<ImageVo> getBoardList(DefaultHttpClient httpclient,
			HttpGet httpget, ResponseHandler<String> responseHandler,
			String p_gnum, String n_page) throws URISyntaxException,
			IOException, ClientProtocolException {
		String listBody = "", viewBody = "";

		// String strUrl =
		// "http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=";
		String strUrl = "http://photo.soraspace.info/album/theme/pic_list.php?p_page="
				+ n_page
				+ "&p_sort=D&p_anum=173&p_gnum="
				+ p_gnum
				+ "&p_soption=&p_stxt=";
		System.out.println("=========================================");
		System.out.println(strUrl);
		System.out.println("=========================================");
		// httpget.setURI(new URI(strUrl));
		// lip
		// http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=481&p_soption=&p_stxt=

		listBody = getDownloadUrl(httpclient, httpget, responseHandler, strUrl);
		String file = "c:/temp/list/list_" + p_gnum + "_" + n_page;
		// FileUtils.writeStringToFile(new File(file+".txt"), listBody,
		// "utf-8");
		// boardlist에서 a tag 분석
		// pic_view.php?p_num=1308916&p_ix=3&&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=&p_page=1
		/*
		 * String file ="c:/temp/list2.txt"; FileUtils.writeStringToFile(new
		 * File(file), responseBody, "utf-8"); System.out.println("result = " +
		 * responseBody);
		 */
		Matcher match = pattern.matcher(listBody);
		String listurl = "";
		String viewurl = "";
		String p_num = "";
		List<ImageVo> lst = new ArrayList<ImageVo>();
		ImageVo vo = null;
		while (match.find()) {
			vo = new ImageVo();
			listurl = match.group(1);
			if (listurl.indexOf("pic_view.php") == 0) // href가 바로 시작된다
			{
				p_num = listurl.substring(listurl.indexOf("?") + 1, listurl
						.indexOf("&"));
				p_num = p_num.replaceAll("p_num=", "");
				System.out.println("p_num :" + p_num);
				vo.setFileName(p_num + ".jpg");
				viewBody = getDownloadUrl(httpclient, httpget, responseHandler,
						photo_url + listurl);
				// FileUtils.writeStringToFile(new
				// File(file+"_view_"+p_num+".txt"), viewBody, "utf-8");

				Matcher m_img = pattern_img.matcher(viewBody);
				while (m_img.find()) {
					viewurl = m_img.group(1);
					if (viewurl.indexOf("thumbnail.php") > 10) // 이것을 포함 하고 있는
																// 것들
					{
						// ail.php?p_imgwidth=681&p_imgheight=906&p_width=530&p_height=705
						// 다운이미지 width및 height 변경처리
						viewurl = getViewImageUrlSwitch(viewurl);
						// System.out.println("img viewurl src :"+viewurl);
						vo.setImgUrl(viewurl);
						lst.add(vo);
					}
				}

			}
		}
		return lst;
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

	private String getDownloadUrl(DefaultHttpClient httpclient,
			HttpGet httpget, ResponseHandler<String> responseHandler,
			String p_url) throws URISyntaxException, IOException,
			ClientProtocolException {
		// String responseBody;
		// p_url = URLEncoder.encode(p_url, "utf-8");
		// System.out.println("p_url:"+p_url);
		// httpget.setURI(new
		// URI("http://story.soraspace.info/bank/story_mn.php?p_userid=bluesman&p_snum=201&p_num=35821"));
		String ret = "";
		try {
			httpget.setURI(new URI(p_url));
			ret = httpclient.execute(httpget, responseHandler);
		} catch (Exception ex) {
		}
		return ret;
		// return responseBody;
	}

	private InputStream getDownloadUrlInputStream(DefaultHttpClient httpclient,
			HttpGet httpget, String p_url) throws URISyntaxException,
			IOException, ClientProtocolException {
		// p_url = URLEncoder.encode(p_url, "utf-8");
		// System.out.println("p_url2:"+p_url);
		InputStream is = null;
		try {
			httpget.setURI(new URI(p_url));
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			is = resEntity.getContent();
			;
		} catch (Exception ex) {
		}
		return is;
	}

	private HttpGet executeLogin(DefaultHttpClient httpclient)
			throws IOException, ClientProtocolException {
		HttpGet httpget = new HttpGet(host_url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		System.out.println("Login form get: " + response.getStatusLine());
		EntityUtils.consume(entity);
		System.out.println("Initial set of cookies:");
		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("- " + cookies.get(i).toString());
			}
		}
		HttpPost httpost = new HttpPost(host_url + "/common/include/login.php");
		  Header header1 = new BasicHeader("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg,application/msword, */*");
        Header header2 = new BasicHeader ("Referer",host_url+"/index.php");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("p_userid", "bimohani"));
		nvps.add(new BasicNameValuePair("p_passwd", "cw8904"));
	   httpost.setHeader(header1);
     httpost.setHeader(header2);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
 
		response = httpclient.execute(httpost);
		entity = response.getEntity();

		System.out.println("Login form get: " + response.getStatusLine());
		EntityUtils.consume(entity);

		System.out.println("Post logon cookies:");
		cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("- " + cookies.get(i).toString());
			}
		}
		return httpget;
	}
}
