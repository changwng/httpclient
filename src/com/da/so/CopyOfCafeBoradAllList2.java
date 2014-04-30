package com.da.so;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

import com.da.img.FileHelper;
import com.da.img.ImageVo;

/**
 * http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=44 //강남
 * http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=02 //역삼
 * http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=97 //선릉
 * http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=62 // 피부짱ㄴ
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class CopyOfCafeBoradAllList2 {
	private static String SO_URL = "soraven.info";
	private static String host_url = "http://www."+SO_URL;
	private static String photo_url 	= "http://photo."+SO_URL+"/album/theme/"; 
	private static String cafe_url 	= "http://cafe."+SO_URL+"/cafe/main/";
	private static String SAVE_DIR = "c:/temp/so/cafe";
	private static String p_cafeid = "sexclub"; //sexclub , loveyuna7
	private static String p_bid = "40"; //강남 ,97:선릉 ,02 
	 
	static Pattern pattern = Pattern.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
	static Pattern pattern_img = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
	static  Pattern pattern_span = Pattern .compile("<span(.*?)style=\"cursor:hand\">(.*?)<\\/span>",Pattern.MULTILINE+Pattern.CASE_INSENSITIVE); // all html tag
	
	public static void main(String[] args) {
		CopyOfCafeBoradAllList2 cfl = new CopyOfCafeBoradAllList2();
		try {
			// 호출 방법 BoardAllList 1 174, 354, c:/temp
			// ba 1 174 354 c:\tmp
			/**
			 * // p_anum=244&p_ix=1 인물 셀프 // p_anum=281&p_ix=2 패티시 //
			 * p_anum=173&p_ix=3 몸짱이다 // p_gnum= 351 : mom , 481: lip , 352:
			 * hip, 354:ga,353:leg,442:pussy,
			 */
			String nPage = "1";
			String p_anum = "173";
			String p_gnum = "354"; // ga

			if (args.length > 0) {
				nPage = args[0];
			}
			if (args.length > 1) {
				p_anum = args[1];
			}
			if (args.length > 2) {
				p_gnum = args[2];
			}
			if (args.length > 3) {
				SAVE_DIR = args[3];
			}
			cfl.executeURL(nPage, p_anum, p_gnum);
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

	protected void executeURL(String p_page, String p_anum, String p_gnum)
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
			String imgUrl;
			// 351 : mom , 481: lip
			String SaveFilePath = SAVE_DIR + "/" + p_anum;
			// String p_gnum ="481";
			List<ImageVo> lst = null;
			int max_page = 10000;
			String output = "";
			InputStream istream;
			int init_page = 1;
			if (!"".equals(StringUtils.stripToEmpty(p_page))) {
				init_page = Integer.parseInt(p_page);
			}
			for (int i = init_page; i < max_page; i++) {
				// httpget = executeLogin(httpclient);
				lst = getBoardList(httpclient, httpget, responseHandler,
						p_gnum, String.valueOf(i), p_anum);
				for (ImageVo vo : lst) {
					// continue;
					imgUrl = vo.getImgUrl(); // "http://photo2.soraspace.info/thumbnail.php?p_imgwidth=1104&p_imgheight=1104&p_width=1104&p_height=1104&p_imgfile=%2F%2F201205%2F22%2Fsk650%2F%2F1501694.jpg&verify=%2F%90%F9%B7%D6%D0%12%0EF%17%B8M%DA%A4L%C4";
					istream = getDownloadUrlInputStream(httpclient, httpget,
							imgUrl);
					output = SaveFilePath
							+ "/"
							+ p_cafeid
							+ "/"
							+ p_bid +"_"+vo.getFileName();
					// System.out.println("result = " + istream.toString());
					/*output = SaveFilePath
							+ "/"
							+ p_gnum
							+ "/"
							+ org.apache.commons.lang.StringUtils.leftPad(
									String.valueOf(i), 4, "0") + "/"
							+ vo.getFileName();*/
					fileDownCopy(output, istream);
					if (istream != null) {
						istream.close();
					}
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
			String p_gnum, String n_page, String p_anum)
			throws URISyntaxException, IOException, ClientProtocolException {
		String listBody = "", viewBody = "";

		// String strUrl =
		// http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=97
		// http://cafe.soramoon.info/cafe/main/board_list.php?p_page=2&p_cafeid=loveyuna7&p_bid=44&p_soption=&p_stxt=
		String strUrl = "http://cafe." + SO_URL
				+ "/cafe/main/board_list.php?p_cafeid="+p_cafeid+"&p_bid="+p_bid+"&p_page=" + n_page
				+ "&p_sort=D&p_anum=" + p_anum + "&p_gnum=" + p_gnum
				+ "&p_soption=title&p_stxt=MODEL";
		System.out.println("=========================================");
		System.out.println(strUrl);
		System.out.println("=========================================");
		// httpget.setURI(new URI(strUrl));
		// lip 689576
		// http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=481&p_soption=&p_stxt=

		listBody = getDownloadUrl(httpclient, httpget, responseHandler, strUrl);
		/* String file = this.SAVE_DIR+"/list_"+p_gnum+"_"+n_page;
		 FileUtils.writeStringToFile(new File(file+".txt"), listBody,
		"utf-8");*/
		// boardlist에서 a tag 분석
		// pic_view.php?p_num=1308916&p_ix=3&&p_sort=D&p_anum=173&p_gnum=351&p_soption=&p_stxt=&p_page=1
		/*  
		 * String file =SAVE_DIR+/list2.txt"; FileUtils.writeStringToFile(new
		 * File(file), responseBody, "utf-8"); System.out.println("result = " +
		 * responseBody);
		 * <img src="http://cjimg.inames.co.kr/SH20121015000006/ann/혜미.jpg">
		 * <img src="http://poongpa80.diskn.com/1R3lDyVMkm">
		 */
		
		
		Matcher match = pattern.matcher(listBody);
		String listurl = "";
		String viewurl = "";
		String p_num = "";
		ImageVo vo =  null;
		List<ImageVo> lst = new ArrayList<ImageVo>();
		List<ImageVo> imglist = new ArrayList<ImageVo>();
		while (match.find()) {
			//vo = new ImageVo();
			listurl = match.group(1);
			if (listurl.indexOf("board_view.php") == 0) // href가 바로 시작된다
			{
				p_num = listurl.substring(listurl.indexOf("?") + 1,
						listurl.indexOf("&"));
				p_num = p_num.replaceAll("p_num=", "");
				 System.out.println(" ba p_num :" + p_num);
				
				viewBody = getDownloadUrl(httpclient, httpget, responseHandler,
						cafe_url + listurl);
				
				vo =   getAuthor(viewBody); //작성자 리턴
				
				// System.out.println(viewBody);
				  //String file =SAVE_DIR+"/"+p_cafeid+"/"+p_bid+"_board_view_"+p_num+"_"+vo.getDirName()+".htm";
				String file =SAVE_DIR+"/"+p_cafeid+"/"+p_bid+"_board_view_"+vo.getDirName()+"_"+p_num+".htm";
				  FileHelper.createFile(file);
				//  FileUtils.writeStringToFile(new File(file), viewBody, "utf-8");
				  FileUtils.writeStringToFile(new File(file), viewBody, "euc-kr");
				  viewBody = viewBody.toLowerCase();
				Matcher m_img = pattern_img.matcher(viewBody);
				int i =0;
				
				ImageVo imgVo =  null;
				String file_name =  null;
				while (m_img.find()) {
					
					//viewurl = m_img.group(0);
					//System.out.println("others viewurl 0 :"+viewurl);
					viewurl = m_img.group(1);
					//System.out.println("others viewurl 1 :"+viewurl);
					if ( (viewurl.indexOf(SO_URL) < 0 ) // 이것을 포함 하고 있는
							|| (viewurl.indexOf("/") < 0 ) )
					{
						// ail.php?p_imgwidth=681&p_imgheight=906&p_width=530&p_height=705
						// 다운이미지 width및 height 변경처리
						//viewurl = getViewImageUrlSwitch(viewurl);
						 System.out.println("img viewurl src :"+viewurl);
						 //
						 i++;
						 imgVo =  new ImageVo();
						 imgVo.setAuthorId(vo.getAuthorId());
						 imgVo.setDirName(vo.getDirName());
						 if("".equals(StringUtils.stripToEmpty(FilenameUtils.getExtension(viewurl)))){
							 file_name = vo.getDirName()+"_"+p_num +"_"+String.valueOf(i)+"_"+FilenameUtils.getName(viewurl)+".jpg";
						 }else {
							 file_name = vo.getDirName()+"_"+p_num +"_"+String.valueOf(i)+"_"+FilenameUtils.getName(viewurl);
						 }
						 imgVo.setFileName(file_name);
						 imgVo.setImgUrl(viewurl);
						 imglist.add(imgVo);
						// 최초에만 넣는다.
						 
					}
				}
				 
				//lst.add(vo);

			}
		}
		return imglist;
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
		//viewurl= viewurl.replace("thumbnail.php", "thumbnail_watermark.php");
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
		  System.out.println("p_url2:"+p_url);
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
	private  ImageVo getAuthor( String listBody) {
		String dirName ="";
		Matcher match = pattern_span.matcher(listBody);
		ImageVo vo = new ImageVo();
		if (match.find()) {
			 //System.out.println(match);
			 String listurl = match.group(0);
			 //System.out.println(listurl);
			 String listurl1 = match.group(1);
			 listurl1 = listurl1.replace("onClick=\"soraShowUserLayer('", "").replace(" ", "");
			 listurl1 = listurl1.substring(0,listurl1.indexOf("'"));
			/*System.out.println(listurl1);
			System.out.println(listurl1.length());*/
			String listurl2 = match.group(2);
			listurl2 = this.getMatchFileName(listurl2);
			 
			//listurl2 = StringUtils.escape(listurl2);
			
			//System.out.println(listurl2);
			dirName = listurl2+"_"+listurl1+"_";
			vo.setAuthorId(listurl1);
			vo.setDirName(dirName);
		}
		return vo;
	}
	private String getMatchFileName(String name)
	{
		//String name = "name.é+!@#$%^&*(){}][/=?+-_\\|;:`~!'\",<>";
		//String name = "name.é+!@#$%^&*(){}][/=?+-\\|;:`~!'\",<>";
		StringBuilder filename = new StringBuilder(); 
		for (char c : name.toCharArray()) {
		  if (c=='.' || Character.isJavaIdentifierPart(c)) {
		    filename.append(c);
		  }
		}
		return filename.toString();
		
	}
}
