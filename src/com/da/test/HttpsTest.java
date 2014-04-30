package com.da.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpsTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        TrustManager easyTrustManager = new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                // no-op
                return null;
            }

            public void checkServerTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            public void checkClientTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
        };

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);

            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

//            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext,
//                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            
            Scheme sch = new Scheme("https", 443, socketFactory);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);

       //     HttpGet httpget = new HttpGet( "https://msp.f-secure.com/web-test/common/test.html");
            HttpGet httpget = new HttpGet( "https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=cw89040310&stln=on&saved_id=on");

            System.out.println("executing request" + httpget.getRequestLine());

            HttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            String responseBody = EntityUtils.toString(response.getEntity(),"UTF-8");

            System.out.println(responseBody);

            System.out.println("————————————————————");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: "
                        + entity.getContentLength());
            }
            EntityUtils.consume(entity);

        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }
}