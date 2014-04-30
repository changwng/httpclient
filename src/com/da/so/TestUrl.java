package com.da.so;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.io.FilenameUtils;

public class TestUrl {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, MalformedURLException {
		// TODO Auto-generated method stub
		//String filename ="http://toro911.diskn.com/핑크퀸/다혜원본.jpg";
		String viewurl ="/common";
	//	 viewurl ="http://cafe.soramoo2.info";
		 viewurl ="http://www.daum.net";
		System.out.println("viewurl index:"+viewurl.indexOf("/"));
		if ( !(viewurl.indexOf("soramoon.info") > -1 ) // 이것을 포함 하고 있는 
				&& !(viewurl.indexOf("/") < 0 ) )
		{
			System.out.println("viewurl index2:"+viewurl.indexOf("/"));
			System.out.println("viewurl index2:"+viewurl);
		}
		//System.out.println("sb: indexof:"+viewurl.indexOf("dommon"));
		String filename ="http://121.190.13.23/a/switch/kirara100/16TWLa48KI/1373569965/a438e9b5d9c0cf8082e004738b98f26e6?srv=4hl27&type=jpg&backup=20&size=1011";
		filename ="http://kdbuni.hubweb.net/연지 사본.jpg";
		System.out.println("filename:"+filename);
		URL url =TestUrl.convertToURLEscapingIllegalCharacters(filename);
		System.out.println("url:"+url.toString());
		
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
				System.out.println("lastUrl[0]:"+lastUrl[0]);
				System.out.println("lastUrl[0]:"+URLEncoder.encode(" " , "EUC-KR"));
				sb.append(lastUrl[0]);
				if(lastUrl.length>1){
					sb.append("?");
					lastParam = lastUrl[1].split("&");
					for( int j=0; j< lastParam.length; j++)
					{
						System.out.println("lastParam[j]:"+lastParam[j]);
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
		System.out.println("sb: tot:"+sb.toString());
		String ret =FilenameUtils.getFullPath(filename);
		System.out.println("ret:"+ret);
		ret =FilenameUtils.getPath(filename);
		System.out.println("ret:"+ret);
		ret =FilenameUtils.normalize(filename);
		System.out.println("ret:"+ret);
		ret =FilenameUtils.getName(filename);
		System.out.println("ret:"+ret);
		System.out.println("ret:"+FilenameUtils.getExtension(filename));
		System.out.println("ret:"+FilenameUtils.getPrefix(filename));

	}
	public static URL convertToURLEscapingIllegalCharacters(String string){
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

}
