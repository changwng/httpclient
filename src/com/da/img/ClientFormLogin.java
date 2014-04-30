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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 * HttpPost httpost = new HttpPost(host_url + "/common/include/login.php");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("p_userid", "bimohani"));
		nvps.add(new BasicNameValuePair("p_passwd", "cw8904"));
		
		
		POST /common/include/login.php HTTP/1.1
Host: www.soraven.info
Connection: keep-alive
Content-Length: 44
Cache-Control: max-age=0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9, *;q=0.8
Origin: http://www.soraven.info
User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17
Content-Type: application/x-www-form-urlencoded
Referer: http://www.soraven.info/index.php
Accept-Encoding: gzip,deflate,sdch
Accept-Language: ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4
Accept-Charset: windows-949,utf-8;q=0.7,*;q=0.3
Cookie: PHPSESSID=5srfd33u67aimi046sumcrv870; __utma=199692460.833480633.1359280175.1360135862.1360140523.3; __utmb=199692460.4.10.1360140523; __utmc=199692460; __utmz=199692460.1359280175.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)

 */
public class ClientFormLogin {

    public static void main(String[] args) throws Exception {

        DefaultHttpClient httpclient = new DefaultHttpClient();
      
        try {
            HttpGet httpget = new HttpGet("http://www.soraven.info/index.php");

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            //System.out.println( EntityUtils.toString(entity));
            System.out.println("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);

            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("-1 " + cookies.get(i).toString());
                }
            }

            HttpPost httpost = new HttpPost("http://www.soraven.info/common/include/login.php"); 
            Header header1 = new BasicHeader("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg,application/msword, */*");
            Header header2 = new BasicHeader ("Referer","http://www.soraven.info/index.php");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("p_userid", "bimohani"));
            nvps.add(new BasicNameValuePair("p_passwd", "cw8904"));
            nvps.add(new BasicNameValuePair("x", "12"));
            nvps.add(new BasicNameValuePair("y", "20"));
            httpost.setHeader(header1);
            httpost.setHeader(header2);
            httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            	
            //Thread.sleep(2000);
            response = httpclient.execute(httpost);
            entity = response.getEntity();

            System.out.println("Login form get: " + response.getStatusLine());
          
            System.out.println( EntityUtils.toString(entity));
            EntityUtils.consume(entity);

            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("-2 " + cookies.get(i).toString());
                }
            }

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
