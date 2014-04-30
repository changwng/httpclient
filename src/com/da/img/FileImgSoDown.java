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
 
public class FileImgSoDown {
	public static void main(String[] args) throws Exception{ 
        HttpClient httpclient = new DefaultHttpClient(); 
         
        String id = "bimohani"; 
        String pw = "cw8904"; 
         
        // p_userid, p_passwd
        List<NameValuePair> qparams = new ArrayList<NameValuePair>(); 
       // qparams.add(new BasicNameValuePair("URL", "http://www.soraspace.info/common/include/login.php")); 
        qparams.add(new BasicNameValuePair("p_userid", id)); 
        qparams.add(new BasicNameValuePair("p_passwd", pw)); 
        //qparams.add(new BasicNameValuePair("SERVERIP", "203.236.20.129")); 
        qparams.add(new BasicNameValuePair("x", "0")); 
        qparams.add(new BasicNameValuePair("y", "0")); 
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(qparams, "UTF-8"); 
        HttpPost httpPost = new HttpPost("http://www.soraspace.info/common/include/login.php"); 
        httpPost.setEntity(entity); 
         
        ResponseHandler<String> responseHandler = new BasicResponseHandler(); 
        String responseBody = ""; 
        HttpResponse response = httpclient.execute(httpPost); 
        Header[] headers  = response.getAllHeaders(); 
        httpclient = new DefaultHttpClient(); 
        HttpGet httpGet = new HttpGet(); 
        /*
        if (headers.length > 1){ 
            String url = headers[3].getValue(); 
            System.out.println("url = " + url); 
            httpGet.setURI(new URI(url)); 
            responseBody = httpclient.execute(httpGet, responseHandler); 
            System.out.println(responseBody); 
        } */
        httpGet.setURI(new URI("http://story.soraspace.info/bank/story_mn.php?p_userid=bluesman&p_snum=201&p_num=35788")); 
        responseBody = httpclient.execute(httpGet, responseHandler); 
        System.out.println("result = " + responseBody); 
    }  
}
