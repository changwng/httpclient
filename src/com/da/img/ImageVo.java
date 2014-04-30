package com.da.img;

import java.util.ArrayList;

public class ImageVo {
 private String fileName;
 private String dirName;
 private String authorId;
 private String imgUrl;
 private String subject;
 private ArrayList imglist;
 
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public String getImgUrl() {
	return imgUrl;
}
public void setImgUrl(String imgUrl) {
	this.imgUrl = imgUrl;
}
public String getDirName() {
	return dirName;
}
public void setDirName(String dirName) {
	this.dirName = dirName;
}
public String getAuthorId() {
	return authorId;
}
public void setAuthorId(String authorId) {
	this.authorId = authorId;
}
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public ArrayList getImglist() {
	return imglist;
}
public void setImglist(ArrayList imglist) {
	this.imglist = imglist;
}

}
