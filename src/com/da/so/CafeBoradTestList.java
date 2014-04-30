package com.da.so;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
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
public class CafeBoradTestList {
	private static String SO_URL = "soraven.info";
	private static String host_url = "http://www."+SO_URL;
	private static String photo_url 	= "http://photo."+SO_URL+"/album/theme/"; 
	private static String cafe_url 	= "http://cafe."+SO_URL+"/cafe/main/";
	private static String SAVE_DIR = "c:/temp/so/cafe2";
	//private static String p_cafeid = "sexclub"; //sexclub , loveyuna7
	private static String p_cafeid = "sexclub"; //sexclub , loveyuna7
	private static String p_bid = "87"; //40: 강남 ,26:역삼,45:선릉, 29:분당 , 47
	 
	static Pattern pattern = Pattern.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)<\\/a>");
	static Pattern pattern_img = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
	static  Pattern pattern_span = Pattern .compile("<span(.*?)style=\"cursor:hand\">(.*?)<\\/span>",Pattern.MULTILINE+Pattern.CASE_INSENSITIVE); // all html tag
	
	public static void main(String[] args) {
		CafeBoradTestList cfl = new CafeBoradTestList();
		try {
			// 호출 방법 BoardAllList 1 174, 354, c:/temp
			// ba 1 174 354 c:\tmp
			 
			String nPage = "1";
			String p_anum = "173";
			String p_gnum = "354"; // ga

			if (args.length > 0) {
				nPage = args[0];
			}
			if (args.length > 1) {
				p_cafeid = args[1];
			}
			if (args.length > 2) {
				p_bid = args[2];
			}
			if (args.length > 3) {
				SAVE_DIR = args[3];
			}
			cfl.executeURL(nPage, p_cafeid, p_cafeid);
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
	public static final String LAST_REDIRECT_URL = "last_redirect_url";
	private String getUrlAfterRedirects(HttpContext context) {
	    String lastRedirectUrl = (String) context.getAttribute(LAST_REDIRECT_URL);
	    if (lastRedirectUrl != null)
	        return lastRedirectUrl;
	    else {
	        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
	        HttpHost currentHost = (HttpHost)  context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
	        String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
	        return currentUrl;
	    }
	}
	protected void executeURL(String p_page, String p_anum, String p_gnum)
			throws IOException, ClientProtocolException, URISyntaxException {
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
		HttpHost localhost = new HttpHost("localhost", 80);
		cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		 
		//HttpClient httpClient = new DefaultHttpClient(cm);
		DefaultHttpClient httpclient = new DefaultHttpClient(cm);
		try {
			httpclient.setRedirectStrategy(new DefaultRedirectStrategy(){                  
		        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {  
		            boolean isRedirect=false;  
		            try {  
		                isRedirect = super.isRedirected(request, response, context);  
		            } catch (ProtocolException e) {  
		                // TODO Auto-generated catch block  
		                e.printStackTrace();  
		            }  
		            if (!isRedirect) {  
		                int responseCode = response.getStatusLine().getStatusCode();  
		                if (responseCode == 301 || responseCode == 302) {  
		                    return true;  
		                }  
		            }  
		            return false;  
		        }  
		    });   
			HttpGet httpget = executeLogin(httpclient);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = ""; 
			String imgUrl;
			// 351 : mom , 481: lip
			String SaveFilePath = SAVE_DIR + "/" + p_cafeid+ "/" + p_bid;
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
				if(lst.size() < 10) {
					System.out.println("=========Exit to the Program :"+p_bid);
					break;
				}
				/*for (ImageVo vo : lst) {
					// continue;
					imgUrl = vo.getImgUrl(); // "http://photo2.soraspace.info/thumbnail.php?p_imgwidth=1104&p_imgheight=1104&p_width=1104&p_height=1104&p_imgfile=%2F%2F201205%2F22%2Fsk650%2F%2F1501694.jpg&verify=%2F%90%F9%B7%D6%D0%12%0EF%17%B8M%DA%A4L%C4";
					istream = getDownloadUrlInputStream(httpclient, httpget,
							imgUrl);
					output = SAVE_DIR
							+ "/"
							+ p_cafeid
							+ "/"
							+ p_bid +"_"+vo.getFileName(); 
					fileDownCopy(output, istream);
					if (istream != null) {
						istream.close();
					}
				}*/
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
		String output = "";
		InputStream istream;
		// String strUrl =
		// http://cafe.soramoon.info/cafe/main/board_list.php?p_cafeid=loveyuna7&p_bid=97
		// http://cafe.soramoon.info/cafe/main/board_list.php?p_page=2&p_cafeid=loveyuna7&p_bid=44&p_soption=&p_stxt=
		String strUrl = "http://cafe." + SO_URL
				+ "/cafe/main/board_list.php?p_cafeid="+p_cafeid+"&p_bid="+p_bid+"&p_page=" + n_page
				+ "&p_sort=D&p_anum=" + p_anum + "&p_gnum=" + p_gnum
				+ "&p_soption=&p_stxt=";
				//+ "&p_soption=title&p_stxt=MODEL";
		System.out.println("=========================================");
		System.out.println(strUrl);
		System.out.println("=========================================");
		// httpget.setURI(new URI(strUrl));
		// lip 689576
		// http://photo.soraspace.info/album/theme/pic_list.php?p_page=1&p_sort=D&p_anum=173&p_gnum=481&p_soption=&p_stxt=

		listBody = getDownloadUrl(httpclient, httpget, responseHandler, strUrl);
		 
		
		Matcher match = pattern.matcher(listBody);
		String listurl = "";
		String viewurl = "";
		String p_num = "";
		String subject ="";
		ImageVo vo =  null;
		List<ImageVo> lst = new ArrayList<ImageVo>();
		List<ImageVo> imglist = new ArrayList<ImageVo>(); 
		while (match.find()) {
			//vo = new ImageVo();
			listurl = match.group(1);
			if (listurl.indexOf("board_view.php") == 0) // href가 바로 시작된다
			{ 
				subject = match.group(2);
				p_num = listurl.substring(listurl.indexOf("?") + 1,
						listurl.indexOf("&"));
				p_num = p_num.replaceAll("p_num=", "");
				 System.out.println(" ba p_num :" + p_num);
				listurl = "/board_view.php?p_num=25913746&p_cafeid=sexclub&p_bid=87&p_soption=title&p_stxt=%C7%D2%C0%CE&p_page=1";
				viewBody = getDownloadUrl(httpclient, httpget, responseHandler,
						cafe_url + listurl);
				
				subject = this.getMatchFileName(subject);
				vo =   getAuthor(viewBody); //작성자 리턴
				vo.setSubject(subject);
				lst.add(vo);
				//  System.out.println("subject:"+subject);
				  //String file =SAVE_DIR+"/"+p_cafeid+"/"+p_bid+"_board_view_"+p_num+"_"+vo.getDirName()+".htm";
				//if(!"".equals(StringUtils.stripToEmpty(vo.getDirName()))) {
					String file =SAVE_DIR+"/"+p_cafeid+"/"+p_bid+"/"+p_bid+"_board_view_"+vo.getDirName()+"_"+p_num+"_"+subject+".htm";
					System.out.println("Board list file:"+ file);
					try {
						FileHelper.createFile(file);
						FileUtils.writeStringToFile(new File(file), viewBody, "euc-kr");
					} catch(Exception ex)
					{
						System.out.println("Board file Ex:"+ ex.getMessage() +", file:"+file); 
					}
				//}
				  viewBody = viewBody.replace("<IMG", "<img");
				Matcher m_img = pattern_img.matcher(viewBody);
				int i =0;
				
				ImageVo imgVo =  null;
				String file_name =  null;
				while (m_img.find()) {
					
					//viewurl = m_img.group(0);
					//System.out.println("others viewurl 0 :"+viewurl);
					viewurl = m_img.group(1);
					//System.out.println("others viewurl 1 :"+viewurl);
					if ( !(viewurl.indexOf("soramoon.info") > -1 ) // 이것을 포함 하고 있는 
							&& !(viewurl.indexOf("//") < 0 ) )
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
							 file_name = vo.getDirName()+"_"+p_num +"_"+String.valueOf(i)+"_"+subject+"_"+FilenameUtils.getName(viewurl)+".jpg";
						 }else {
							 file_name = vo.getDirName()+"_"+p_num +"_"+String.valueOf(i)+"_"+subject+"_"+FilenameUtils.getName(viewurl);
						 }
						 imgVo.setFileName(file_name);
						 imgVo.setImgUrl(viewurl);
						 imglist.add(imgVo);
						// 최초에만 넣는다.
						 
						 istream = getDownloadUrlInputStream(httpclient, httpget,
								 viewurl);
							output = SAVE_DIR
									+ "/"
									+ p_cafeid
									+"/"+p_bid
									+ "/"
									+ p_bid +"_"+imgVo.getFileName(); 
							fileDownCopy(output, istream);
							if (istream != null) {
								istream.close();
							}
					}
				}
				 
				//lst.add(vo);

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
		//viewurl= viewurl.replace("thumbnail.php", "thumbnail_watermark.php");
		return viewurl;
	}

	private String getDownloadUrl(DefaultHttpClient httpclient,
			HttpGet httpget, ResponseHandler<String> responseHandler,
			String p_url) throws URISyntaxException, IOException,
			ClientProtocolException {
		System.out.println("getDownloadUrl:"+p_url);
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
	
    private InputStream getDownloadUrlInputStream_Post(DefaultHttpClient httpclient,
			HttpGet httpget, String p_url) throws URISyntaxException,
			IOException, ClientProtocolException {
		// p_url = URLEncoder.encode(p_url, "utf-8");
		  System.out.println("post p_url2:"+p_url);
		  p_url = getEncodingUri(p_url);
		InputStream is = null;
		try {
			HttpPost httpost = new HttpPost(p_url);

		    //List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		   /* nvps.add(new BasicNameValuePair("IDToken1", "username"));
		    nvps.add(new BasicNameValuePair("IDToken2", "password"));*/

		    //httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			  HttpResponse response = httpclient.execute(httpost);
			  // POST 메소드는 동작 하지 않는다.
			if (response.getStatusLine().getStatusCode() == 302) { 
				 String redirectURL = response.getFirstHeader("Location").getValue(); 
				  // no auto-redirecting at client side, need manual send the request.
				  HttpGet request2 = new HttpGet(redirectURL);
				  HttpResponse response2 = httpclient.execute(request2);
				    HttpEntity resEntity = response2.getEntity();
				    is = resEntity.getContent();
			}else
			{ 
			    HttpEntity resEntity = response.getEntity();
			    is = resEntity.getContent();
			}
			  
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return is;
	}
	private InputStream getDownloadUrlInputStream(DefaultHttpClient httpclient,
			HttpGet httpget, String p_url) throws URISyntaxException,
			IOException, ClientProtocolException {
		 // p_url = URLEncoder.encode(p_url, "utf-8");
		  System.out.println("p_url2:"+p_url);
		  p_url = getEncodingUri(p_url);
		  String redirectLocation ="";
		InputStream is = null;
		HttpResponse response = null;
		HttpResponse response2 = null;
		HttpEntity resEntity = null;
		HttpContext context =  new BasicHttpContext();;
		try {
			httpget.setURI(new URI(p_url));
			 response2 = httpclient.execute(httpget, context);
			// response 후의 값 매핑
			 resEntity = getGetLocationUrl(httpclient, redirectLocation, response2); 
			is = resEntity.getContent(); 
		} catch (Exception ex) {
			/*Header locationHeader = response2.getFirstHeader("Location");
			String redirectLocation ="";
			if (locationHeader != null) {
			    redirectLocation = locationHeader.getValue();
			  System.out.println("loaction: " + redirectLocation);
			} else {
			  // The response is invalid and did not provide the new location for
			  // the resource.  Report an error or possibly handle the response
			  // like a 404 Not Found error.
			}*/
			System.out.println("file Error:"+ex);
		}
		return is;
	}
	private HttpEntity getGetLocationUrl(DefaultHttpClient httpclient,
			String redirectLocation, HttpResponse response2)
			throws IOException, URISyntaxException, ClientProtocolException {
		HttpResponse response;
		HttpEntity resEntity=null;
		int responseCode = response2.getStatusLine().getStatusCode();  
		 if (responseCode == 301 || responseCode == 302) {  
			 Header locationHeader = response2.getFirstHeader("Location");
			 if (locationHeader != null) {
			    redirectLocation = locationHeader.getValue();
			    System.out.println("loaction: " + redirectLocation); 
			  //  redirectLocation = URLEncoder.encode(redirectLocation, "utf-8");
			    redirectLocation = getEncodingUri(redirectLocation);
			    System.out.println("encoding redirectLocation: " + redirectLocation); 
			}  
			// EntityUtils.consumeQuietly(response2.getEntity());
			 HttpEntity enty = response2.getEntity();
		     if (enty != null)
		         enty.consumeContent();
			 
			 HttpGet httpget2 = new HttpGet();
			 httpget2.setURI(new URI(redirectLocation));  
			 response 		= httpclient.execute(httpget2);
			 responseCode   = response.getStatusLine().getStatusCode(); 
			 if (responseCode == 301 || responseCode == 302) {
				 resEntity =  getGetLocationUrl(httpclient,redirectLocation, response );
			 }else
			 {
				 resEntity 		= response.getEntity();
			 }
		 } else
		 {
			 resEntity 		= response2.getEntity();  
		 }
		return resEntity;
	}
	private String getEncodingUri_back(String filename) throws UnsupportedEncodingException
	{
		
		filename = filename.replace("http://","");
		String[] file1 = filename.split("/");
		StringBuilder sb = new StringBuilder();
		sb.append("http://"+file1[0]).append("/");
		String[] lastUrl = null;
		String[] lastParam = null;
		for( int i=1; i< file1.length; i++)
		{
			if (i==file1.length-1) {
				lastUrl = file1[i].split("\\?"); 
				sb.append(URLEncoder.encode(lastUrl[0],"utf-8"));
				if(lastUrl.length>1){
					sb.append("?");
					lastParam = lastUrl[1].split("&");
					for( int j=0; j< lastParam.length; j++)
					{
						if (j==lastParam.length-1) {
							sb.append(URLEncoder.encode(lastParam[j],"utf-8"));
						}else {
							sb.append(URLEncoder.encode(lastParam[j],"utf-8")).append("&");
						}
					}
				}
			}else {
				sb.append(URLEncoder.encode(file1[i],"utf-8")).append("/");
			}
			
		}
		return sb.toString();
	}
	public  String getEncodingUri(String filename){
	    try {
	         
	        URL url = convertToURLEscapingIllegalCharacters(filename);
	        return url.toString();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
	public  URL convertToURLEscapingIllegalCharacters(String string){
	    try {
	        String decodedURL = URLDecoder.decode(string, "UTF-8");
	        URL url = new URL(decodedURL);
	        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()); 
	        return uri.toURL(); 
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
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
		name = name.replaceAll("",""); 
		for (char c : name.toCharArray()) {
		  if (c=='.' || Character.isJavaIdentifierPart(c)) {
		    filename.append(c);
		  }
		}
		return filename.toString();
		
	}
}
