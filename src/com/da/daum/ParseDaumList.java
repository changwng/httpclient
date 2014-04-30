package com.da.daum;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class ParseDaumList {
	Pattern pattern = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>]*>(.*?)</a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	Pattern pattern_atag = Pattern.compile(
			"<a[^>]*href=[\"']?([^>\"']+)[\"']?[^>](.*?)</a>",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	Pattern pattern_span = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html tag
	Pattern pattern_owner = Pattern.compile(
			"<span(.*?)class=\"owner\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html tag
	Pattern pattern_article = Pattern.compile(
			"<strong(.*?)class=\"article_sbj\">(.*?)<\\/strong>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html tag
	Pattern pattern_dt = Pattern.compile(
			"<span(.*?)class=\"dt\">(.*?)<\\/span>", Pattern.MULTILINE
					| Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // all html tag
	public static void main(String[] args) {
		try {
			ParseDaumList pd = new ParseDaumList();
			pd.parseAlllAuthor02();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseAlllAuthor02() throws IOException {
		
		// Pattern pattern_span = Pattern
		// .compile("<span[^>]*onClick=[\"']?([^>\"']+)[\"']?[^>]*>");
		// Pattern pattern_span = Pattern
		// .compile("<span[^>]*>(?<text>.*)</span>"); // all html tag
		// Pattern pattern_li = Pattern
		// .compile("<li(.*?)*>(.*?)<\\/li(.*?)>",Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
		// // all html tag
		// Pattern pattern_span = Pattern
		// .compile("<(?<tag>.*).*>(?<text>.*)</\\k<tag>>");
		// Pattern pattern_span = Pattern .compile("(?s)/\\*.*\\*/"); // all
		// comment
		// Pattern pattern_span = Pattern .compile(
		// "(?i:on(blur|c(hange|lick)|dblclick|focus|keypress|(key|mouse)(down|up)|(un)?load|mouse(move|o(ut|ver))|reset|s(elect|ubmit)))");
		String listBody = "";
		// FileUtils.writeStringToFile(new File(file), listBody, "utf-8");
		File file = new File("C:\\TEMP\\daum\\user\\Lak_list_1.txt");
		listBody = FileUtils.readFileToString(file, "utf-8");
		// Matcher match = pattern_span.matcher(listBody);
		setAuthor(pattern_atag, listBody);
	}

	private void setAuthor(Pattern pattern_span, String listBody) {
		Matcher match = pattern_span.matcher(listBody);
		while (match.find()) {
			// System.out.println(match);
			String listurl = match.group(0);
			// System.out.println("===================0====================");
			// System.out.println(listurl);
			String listurl1 = match.group(1);

			if (listurl1.startsWith("/CHILIL/LAK/")) {
				if (!listurl1.startsWith("/CHILIL/LAK/697")
						&& !listurl1.startsWith("/CHILIL/LAK/696")
						&& !listurl1.startsWith("/CHILIL/LAK/new")
						) {
					/*System.out.println(listurl);*/
					System.out.println("====================1===================");
					System.out.println(listurl1);
					String listurl2 = match.group(2);
					Matcher match_owner = pattern_owner.matcher(listurl2);
					if (match_owner.find()) {
						System.out.println("=====================2==================");
						String owner = match_owner.group(2);
						System.out.println(owner);
					}
					Matcher match_article = pattern_article.matcher(listurl2);
					if (match_article.find()) {
						System.out.println("=====================3==================");
						String article = match_article.group(2);
						System.out.println(article);
					}
					Matcher match_dt = pattern_dt.matcher(listurl2);
					if (match_dt.find()) {
						System.out.println("=====================4==================");
						String dt = match_dt.group(2);
						System.out.println(dt);
					}
					/*
					 * listurl1 =
					 * listurl1.replace("onClick=\"soraShowUserLayer('",
					 * "").replace(" ", ""); listurl1 =
					 * listurl1.substring(0,listurl1.indexOf("'"));
					 */

					// System.out.println("=====================end==================");
				}
			}
		}
	}
}
