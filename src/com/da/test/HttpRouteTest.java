package com.da.test;



	import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.routing.RouteInfo.LayerType;
import org.apache.http.conn.routing.RouteInfo.TunnelType;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

	public class HttpRouteTest { 

	  public static void main(String[] args) throws Exception { 
	    HttpParams httpParams = new BasicHttpParams(); 

	    SchemeRegistry schemeRegistry = new SchemeRegistry(); 
	    schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
	    schemeRegistry.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443)); 

	    ClientConnectionManager connectionManager = new 
	ThreadSafeClientConnManager(httpParams, schemeRegistry); 

	    DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, httpParams); 

	    HttpRoutePlanner routePlanner = new HttpRoutePlanner() { 

	      public HttpRoute determineRoute(HttpHost aTarget, HttpRequest 
	aRequest, HttpContext aContext) throws HttpException { 
	    	  //http://logins.daum.net:443
	    	  //https://logins.daum.net/accounts/loginform.do?mobilefull=1&t__nil_footer=login&url=http%3a%2f%2fm%2ecafe%2edaum%2enet%2fCHILIL%2f%5fmemo%3fboardType%3dC%26noticeYn%3d%26page%3d3

	        HttpHost[] proxies = new HttpHost[1]; 
	        //proxies[0] = new HttpHost("m.daum.net", 80, "http"); 
	        proxies[0] = new HttpHost("logins.daum.net", 443, "https");
	        boolean isSecure = true; 

	        //boolean tunneledAndLayered = false; 
	       /* TunnelType tunnelType = tunneledAndLayered ? TunnelType.TUNNELLED : TunnelType.PLAIN; 
	        LayerType layerType = tunneledAndLayered ? LayerType.LAYERED :LayerType.PLAIN; 
*/
	        boolean tunneledAndLayered = false;
	        TunnelType tunnelType = tunneledAndLayered ? TunnelType.TUNNELLED : TunnelType.PLAIN; 
	        LayerType layerType = tunneledAndLayered ? LayerType.LAYERED :LayerType.PLAIN;
	        
	        return new HttpRoute(aTarget, null, proxies, isSecure, tunnelType, layerType); 

	      } 

	    }; 

	    httpClient.setRoutePlanner(routePlanner); 

	    //https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=cw89040310&stln=on&saved_id=on
	  //  HttpUriRequest request = new HttpGet("https://logins.daum.net"); 
	   // HttpUriRequest request = new HttpGet("https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=cw89040310&stln=on&saved_id=on");
	    HttpUriRequest request = new HttpGet("http://m.cafe.daum.net/CHILIL/LAK/790?listURI=%2FCHILIL%2FLAK%3FboardType%3D");
	   // httpClient.execute(request); 
	    HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        System.out.println("Response content length: " + entity.getContentLength());
        System.out.println(EntityUtils.toString(entity));

	  } 
	} 
	