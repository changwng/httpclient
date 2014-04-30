package com.da.img;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class FileListParse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url="http://photo.soraspace.info/album/theme/pic_view.php?p_num=1307118&p_ix=2&p_anum=281";
		String p_num =url.substring(url.indexOf("?")+1,url.indexOf("&"));
		System.out.println ( p_num.replaceAll("p_num=", ""));
			
		 try {
			readParse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	protected static void readParse() throws IOException {
		 Pattern pattern  =  Pattern.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
		 Pattern pattern_h= Pattern.compile("href=([\"']?([^>\"']+)[\"'])");
		// Pattern pattern_h= Pattern.compile("href=('|\")?([0-9]{1,5})('|\")?");
		 Pattern pattern_w= Pattern.compile("width=('|\")?([0-9]{1,5})('|\")?");
		 Pattern pattern_img  =  Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
		// Pattern pattern_writer  =  Pattern.compile("<span[\s\S]*?/span>");
		 // <h4\sclass=\"heading\"><a\shref=\"(.*)\">(.*)<\/a><\/h4> 
		// <span\s>(.*)<\/span> 
		 // span onClick=
		  Pattern pattern_writer  =  Pattern.compile("<span>(.*)</span> ");
		  String  imgtag  =  null;                              

		  String listfile ="c:/temp/list.txt";
		// String listfile ="c:/temp/pic_view.txt";
		 String content = FileUtils.readFileToString(new File(listfile),"utf-8");
		 
		 Matcher  match  =  pattern.matcher( content );
		  int imgIndex=0;
		  while(match.find())  {
		     imgtag  =  match.group(1);
		    
		     imgIndex = imgtag.indexOf("pic_view.php");
		    // System.out.println(imgIndex+"pic href :"+imgtag);
		     if(imgIndex==0)
		     {
		    	 System.out.println("a href :"+imgtag);
		     }
		  } 
		  	 listfile ="c:/temp/pic_view.txt";
			 content = FileUtils.readFileToString(new File(listfile),"utf-8");
			// System.out.println(content);
			 Matcher  m_img  =  pattern_img.matcher( content ); 
			  while(m_img.find())  {
				     imgtag  =  m_img.group(1);
				     if (imgtag.indexOf("thumbnail.php") > 10)
				     { 
				    	 // http://photo1.soraspace.info/thumbnail.php?p_imgwidth=530&p_imgheight=189&p_width=330&p_height=189&p_imgfile=
				    	 System.out.println("img src1 :"+imgtag);
				    	 String p_imgwidth = imgtag.substring(imgtag.indexOf("p_imgwidth"),imgtag.indexOf("&p_imgheight"));
				    	 p_imgwidth = StringUtils.replace(p_imgwidth, "p_imgwidth=", "");
				    	 System.out.println("p_imgwidth src :"+p_imgwidth);
				    	 String p_imgheight = imgtag.substring(imgtag.indexOf("p_imgheight"),imgtag.indexOf("&p_width"));
				    	 p_imgheight = StringUtils.replace(p_imgheight, "p_imgheight=", "");
				    	 System.out.println("p_imgheight src :"+p_imgheight);
				    	 String[] aUrl = imgtag.split("&");
				    	 if(aUrl.length==6)
				    	 {
				    		 aUrl[2]="p_width="+p_imgwidth;
				    		 aUrl[3]="p_height="+p_imgheight; 
				    		 imgtag = StringUtils.join(aUrl,"&");
				    	 } 
				    	 System.out.println("img src2 :"+imgtag);
				     }
			  } 
			  Matcher  m_writer  =  pattern_writer.matcher( content ); 
			  while(m_writer.find())  {
				     imgtag  =  m_img.group(0);
				     System.out.println("m_writer src :"+imgtag);
				     if (imgtag.indexOf("thumbnail.php") > 10)
				     {
				    	 //p_imgwidth=530&p_imgheight=189&p_width=330&p_height=189&p_imgfile=
				    	 
				    	 System.out.println("img src :"+imgtag);
				     }
			  } 
			  
	}

}
