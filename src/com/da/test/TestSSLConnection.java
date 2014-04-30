package com.da.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TestSSLConnection {

	public static void main(String[] args) throws Exception {
		TestSSLConnection manager = new TestSSLConnection();
		manager.send();
	}

	protected void send() {
		try {
			trustAllHttpsCertificates();
		//	String urlStr = "http://m.cafe.daum.net/CHILIL/LAK/790?listURI=%2FCHILIL%2FLAK%3FboardType%3D";
			String urlStr = "https://logins.daum.net/accounts/mobile.do?url=http%3A%2F%2Fm.daum.net%2F&relative=&mobilefull=1&weblogin=1&id=changwng&pw=cw89040310&stln=on&saved_id=on";
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());
			URL url = new URL(urlStr);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/xml");

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeChars("test");
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
			StringBuffer buffer = new StringBuffer();

			int read = 0;

			char[] cbuff = new char[1024];
			while ((read = reader.read(cbuff)) > 0)
				buffer.append(cbuff, 0, read);

			reader.close();
			System.out.println("DATA : " + buffer.toString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		trustAllCerts[0] = new SSLTrustManager();
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}
}