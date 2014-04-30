package com.da.img;
import java.net.URI; 
import java.util.ArrayList; 
import java.util.List; 
 
import org.apache.http.Header; 
import org.apache.http.HttpResponse; 
import org.apache.http.NameValuePair; 
import org.apache.http.client.HttpClient; 
import org.apache.http.client.ResponseHandler; 
import org.apache.http.client.entity.UrlEncodedFormEntity; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.impl.client.BasicResponseHandler; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.message.BasicNameValuePair; 
 
public class FileImgDown {
	public static void main(String[] args) throws Exception{ 
        HttpClient httpclient = new DefaultHttpClient(); 
         
        String id = "changwng"; 
        String pw = "qncjdjssl"; 
         
        // p_userid, p_pas
        List<NameValuePair> qparams = new ArrayList<NameValuePair>(); 
        qparams.add(new BasicNameValuePair("URL", "http://www.tworld.co.kr/loginservlet.do?returnURL=http%3A%2F%2Fwww.tworld.co.kr&kind=&popup=&cmd=&reload=&ID=" + id)); 
        qparams.add(new BasicNameValuePair("ID", id)); 
        qparams.add(new BasicNameValuePair("PASSWORD", pw)); 
        qparams.add(new BasicNameValuePair("SERVERIP", "203.236.20.129")); 
        qparams.add(new BasicNameValuePair("X", "0")); 
        qparams.add(new BasicNameValuePair("Y", "0")); 
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(qparams, "UTF-8"); 
        HttpPost httpPost = new HttpPost("http://nicasams.sktelecom.com:2040/icas/fc/LogOnSV"); 
        httpPost.setEntity(entity); 
         
        ResponseHandler<String> responseHandler = new BasicResponseHandler(); 
        String responseBody = ""; 
        HttpResponse response = httpclient.execute(httpPost); 
        Header[] headers  = response.getAllHeaders(); 
        httpclient = new DefaultHttpClient(); 
        HttpGet httpGet = new HttpGet(); 
        
        if (headers.length > 1){ 
            String url = headers[3].getValue(); 
            System.out.println("url = " + url); 
            httpGet.setURI(new URI(url)); 
            responseBody = httpclient.execute(httpGet, responseHandler); 
            System.out.println(responseBody); 
        } 
        httpGet.setURI(new URI("http://www.tworld.co.kr/normal.do?serviceId=S_BILL0070&viewId=V_CENT0261")); 
        responseBody = httpclient.execute(httpGet, responseHandler); 
         
        System.out.println("result = " + responseBody); 
    }  
}
