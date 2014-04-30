package com.da.test;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class NtlmHttpsTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	DefaultHttpClient  httpClient = new DefaultHttpClient();       

    	httpClient.getCredentialsProvider().setCredentials(
    	    AuthScope.ANY,
    	    new NTCredentials("user", "pass", "tamtam", "tamtam"));         

    	URI uri = new URI("http://www.example.com/WebService/Service.svc/subscriptions/tiganeus");
    	HttpGet httpget = new HttpGet(uri);
    	httpget.setHeader("Content-type", "application/json; charset=utf-8"); 

    	HttpResponse response = httpClient.execute(httpget);

    	HttpEntity responseEntity = response.getEntity();
    	String result = EntityUtils.toString(responseEntity);
    }
}