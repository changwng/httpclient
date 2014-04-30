package com.da.img;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class ParseAllAuthor {
	public static void main(String[] args) {
		try {
			parseAlllAuthor02();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void parseAlllAuthor02() throws IOException {
		 Pattern pattern = Pattern.compile("<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>");
		 //Pattern pattern_span = Pattern .compile("<span[^>]*onClick=[\"']?([^>\"']+)[\"']?[^>]*>");
		//  Pattern pattern_span = Pattern .compile("<span[^>]*>(?<text>.*)</span>"); // all html tag
		  Pattern pattern_span = Pattern .compile("<span(.*?)style=\"cursor:hand\">(.*?)<\\/span>",Pattern.MULTILINE+Pattern.CASE_INSENSITIVE); // all html tag
		//  Pattern pattern_span = Pattern .compile("<(?<tag>.*).*>(?<text>.*)</\\k<tag>>");
		//  Pattern pattern_span = Pattern .compile("(?s)/\\*.*\\*/"); // all comment
		// Pattern pattern_span = Pattern .compile( "(?i:on(blur|c(hange|lick)|dblclick|focus|keypress|(key|mouse)(down|up)|(un)?load|mouse(move|o(ut|ver))|reset|s(elect|ubmit)))");
		String listBody ="";
		//FileUtils.writeStringToFile(new File(file), listBody, "utf-8");
		File file = new File("c:/temp/story/honor_list_02.txt");
		listBody = FileUtils.readFileToString(file, "utf-8");
		listBody = listBody.substring(listBody.indexOf("<!-- START 작가 목록 -->"),listBody.indexOf("<!-- END 작가 목록 -->"));
	//	System.out.println(listBody);
		// 
		// 
		Matcher match = pattern_span.matcher(listBody);
		while (match.find()) {
			 //System.out.println(match);
			 String listurl = match.group(0);
			System.out.println(listurl);
			 String listurl1 = match.group(1);
			 listurl1 = listurl1.replace("onClick=\"soraShowUserLayer('", "").replace(" ", "");
			 listurl1 = listurl1.substring(0,listurl1.indexOf("'"));
			 System.out.println(listurl1);
			System.out.println(listurl1.length());
			String listurl2 = match.group(2);
			System.out.println(listurl2);
		}
	}
}
