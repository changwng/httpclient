package com.da.img;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *http://www.joyting.com/agit_joyting/agit_board_view.asp?num=51&cate=243&userid=qwer86&page=1
 http://www.joyting.com/agit_joyting/agit_board_list.asp?userid=qwer86
	khj00707
		lmy147

	likejhwr
http://www.joyting.com/agit_joyting/agit_board_list.asp?userid=likejhwr
 */
public class SCityList {
	private static String host_url = "http://sxcity.info/index_girl.html?page=2";
	private static String photo_url = "http://photo.soraspace.info/album/theme/";
	private static String story_url 	= "http://story.soraspace.info/honor/";
	private static String SO_URL = "soraspace.info";

	private static String SAVE_DIR = "c:/temp/scity";
	static Pattern pattern = Pattern
			.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
	static Pattern pattern_img = Pattern
			.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");

	public static void main(String[] args) {
		SCityList cfl = new SCityList();
		try {
			// 호출 방법 BoardAllList 1 174, 354, c:/temp
			// ba 1 174 354 c:\tmp
			/**
			 * // p_anum=244&p_ix=1 인물 셀프 // p_anum=281&p_ix=2 패티시 //
			 * p_anum=173&p_ix=3 몸짱이다 // p_gnum= 351 : mom , 481: lip , 352:
			 * hip, 354:ga,353:leg,442:pussy,
			 */
			String nPage = "1";
			String p_author_id = "bluesman";
			String p_gnum = ""; // ga

			if (args.length > 0) {
				nPage = args[0];
			}
			if (args.length > 1) {
				p_author_id = args[1];
			}
			if (args.length > 2) {
				p_gnum = args[2];
			}
			if (args.length > 3) {
				SAVE_DIR = args[3]+"/story/"+p_author_id;
			}
			cfl.executeURL(nPage, p_author_id, p_gnum);
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
			// p_anum=244&p_ix=1 인물 셀프
			// p_anum=281&p_ix=2 패티시
			// p_anum=173&p_ix=3 몸짱이다
			// p_gnum= 351 : mom , 481: lip , 352: hip,
			// 354:ga,353:leg,442:pussy,
			// /honor/author_board_list.php?p_userid=bluesman&p_soption=storyname&p_stxt=수취인 불명
			// String p_gnum ="481";
			List<ImageVo> lst = null;
			int max_page = 10000;
			int init_page = 1;
			if (!"".equals(StringUtils.stripToEmpty(p_page))) {
				init_page = Integer.parseInt(p_page);
			}
			// init_page = 17;
			// p_anum ="bluesman";
			for (int i = init_page; i < max_page; i++) {
				lst = getBoardList(httpclient, httpget, responseHandler,
						p_gnum, String.valueOf(i), p_author_id);
				System.out.println("Story Size: "+lst.size());
				if(lst.size()< 1)
				{
					break;
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
			String p_gnum, String n_page, String p_author_id)
			throws URISyntaxException, IOException, ClientProtocolException {
		String listBody = "", viewBody = "";

		// String strUrl =
		// "http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=";
		// thskrsns67
		// bluesman
	/*	String strUrl = "http://photo." + SO_URL
				+ "/album/theme/pic_list.php?p_page=" + n_page
				+ "&p_sort=D&p_anum=" + p_anum + "&p_gnum=" + p_gnum
				+ "&p_soption=&p_stxt=";*/
		if("".equals(StringUtils.stripToEmpty(p_author_id))){p_author_id="bluesman";}
		String	strUrl = "http://sxcity.info/index_girl.html?page="+n_page+"&p_userid="+p_author_id;
		// http://sxcity.info/index_girl.html?page=6
		System.out.println("=========================================");
		System.out.println(strUrl);
		System.out.println("=========================================");
		// httpget.setURI(new URI(strUrl));
		// lip 689576
		// http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=481&p_soption=&p_stxt=

		listBody = getDownloadUrl(httpclient, httpget, responseHandler, strUrl);
		listBody  = htmlRemove(listBody);
		 String file = SAVE_DIR+"/list_"+p_author_id+"_"+n_page;
		 System.out.println(file);
		 FileHelper.createFile(file+".txt");
		 FileUtils.writeStringToFile(new File(file+".txt"), listBody, "utf-8");
		 
		 int nlastIdx = listBody.lastIndexOf("<img src=\"http://image.soraspace.info/common/btn_brd_next.gif\" align=absmiddle></a>");
		 	System.out.println("nlastIdx:" + nlastIdx);
		Matcher match = pattern.matcher(listBody);
		String listurl = "";
		String viewurl = "";
		String p_num = "";
		List<ImageVo> lst = new ArrayList<ImageVo>();
		ImageVo vo = null;
		lst.add(new ImageVo());
		/*while (match.find()) {
			
			listurl = match.group(1);
			if (listurl.indexOf("author_board_view.php") == 0) // href가 바로 시작된다
			{
				vo = new ImageVo();
			 	System.out.println(" ba listurl :" + listurl);
				String p_storyname = listurl.substring(listurl.indexOf("p_storyname=") + 12,
						listurl.indexOf("&p_userid="));
				System.out.println("p_storyname: "+p_storyname);
				
				p_num = listurl.substring(listurl.indexOf("?") + 1,
						listurl.indexOf("&"));
				p_num = p_num.replaceAll("p_num=", "");
				System.out.println(" ba p_num :" + p_num);
				
				vo.setFileName(SAVE_DIR+"/"+p_author_id+"_"+p_storyname+"_"+p_num + ".txt"); 
				p_storyname = URLEncoder.encode(p_storyname, "euc-kr");
				listurl = listurl.substring(0,listurl.indexOf("&p_grade="));
				listurl = listurl+"&p_storyname="+p_storyname+"&p_userid="+p_author_id;
				 //URLEncoder.encode(p_url, "utf-8");
				
				//System.out.println("story_url+listurl: "+story_url+listurl);
				viewBody = getDownloadUrl(httpclient, httpget, responseHandler, story_url+listurl); 
				viewBody = viewBody.substring(viewBody.indexOf("<!-- START 타이틀 -->"),viewBody.indexOf("<!-- END 내용 -->")); 
				viewBody = htmlRemove(viewBody);
				viewBody = viewBody.replaceAll("&quot;", "");
				FileUtils.writeStringToFile(new  File(vo.getFileName()), viewBody, "utf-8");
				// lastindex가 없으면 제거 
				if( nlastIdx > -1)
				{
				lst.add(vo);
				}

			}
		}*/
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
				System.out.println("getDownloadUrl ERROR:"+ex.getLocalizedMessage());
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
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("p_userid", "bimohani"));
		nvps.add(new BasicNameValuePair("p_passwd", "cw8904"));

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
	public  String htmlRemove(String str) {
		  StringBuffer t = new StringBuffer();
		  StringBuffer t2 = new StringBuffer();
		  
		 
		  char[] c = str.toCharArray();
		  char ch;
		  int d = 0;
		  boolean check = false;
		  boolean scriptChkeck = false;
		  boolean styleCheck = false;
		  for(int i=0,len = c.length;i<len;i++) {
		   ch = c[i];
		   if(ch=='<') {
		    check = true;
		   }
		   
		   if(!check&!scriptChkeck&&!styleCheck){
		    
		    t.append(ch);
		   }

		    d++;
		    t2.append(ch);
		    if(d>9){
		     t2.delete(0,1);

		    }
		    
		    
		    if(!scriptChkeck) {
		     if(t2.toString().toLowerCase().indexOf("<script")==0){
		      scriptChkeck = true; 
		     }
		     
		    }
		    if(scriptChkeck) {
		     if(t2.toString().toLowerCase().indexOf("</script>")==0){
		     
		      scriptChkeck = false; 
		     }

		    }
		    
		    
		    if(!styleCheck) {
		     if(t2.toString().toLowerCase().indexOf("<style")==0){
		      styleCheck = true; 
		     }
		     
		    }
		    if(styleCheck) {
		     
		     if(t2.toString().toLowerCase().indexOf("</style>")==0){
		      styleCheck = false; 
		     }

		    }
		    
		    if(ch=='>') {
		     check = false;
		    }
		   }
		  
		    
		  return  t.toString();  
		 }
	
}
