package com.da.img;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

 
public class HttpMultiPartFileUpload {

private static String url =  "http://localhost:8080/HttpServerSideApp/ProcessFileUpload.jsp";



  public static void main(String[] args) throws IOException {

      HttpClient client = new DefaultHttpClient();

      HttpPost mPost = new HttpPost(url);

//      client.
//      client.setConnectionTimeout(8000);



      // Send any XML file as the body of the POST request

      File f1 = new File("students.xml");

      File f2 = new File("academy.xml");

      File f3 = new File("academyRules.xml");



      System.out.println("File1 Length = " + f1.length());

      System.out.println("File2 Length = " + f2.length());

      System.out.println("File3 Length = " + f3.length());



     /* mPost.addParameter(f1.getName(), f1);

      mPost.addParameter(f2.getName(), f2);

      mPost.addParameter(f3.getName(), f3);



      int statusCode1 = client.executeMethod(mPost);



      System.out.println("statusLine>>>" + mPost.getStatusLine());*/

      mPost.releaseConnection();

  }

}
