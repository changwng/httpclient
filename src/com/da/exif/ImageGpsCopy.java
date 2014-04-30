package com.da.exif;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
 
 

/**
* MultiThreadedCopy accepts a list of files and
* a destination directory.  It then starts one
* thread to copy each file into the destination
* directory.
*   - synchronization and mutual exclusion features 
  - timed sleep
  - interruptible I/O operations (at least most of them)
  - thread priorities
  - thread-local caching

*/
public class ImageGpsCopy {
//	static String  start_sourceDir ="C:\\Documentum\\data\\dhddmsnet"; 
 	//static String  start_sourceDir ="C:\\Documentum\\data\\dhddmsnet\\content_storage_03";
	//static String  start_sourceDir ="C:\\Documentum\\data\\dhddmsnet\\content_storage_03\\0076adf1\\80\\34";
	static String  start_sourceDir ="C:\\TEMP\\gps_"; 
	//static String  start_sourceDir ="C:\\TEMP\\so\\544";
//	static String  start_sourceDir ="D:\\__backup_20120321\\__20120318\\dh_workspace_20120321.zip"; 
	static String  start_destDir ="c:\\TEMP\\gps";  
	private static final Logger logger = Logger.getLogger(ImageGpsCopy.class);
   public static void main(String args[]) { 
       File destDir ;
       int i;  
        File fileDir = new File(start_sourceDir);
        
		try {
				getDirectoryCopy(fileDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }

private static int getDirectoryCopy( File src_directory) throws IOException {
	File dest;
	File destDir;
	File src; 
	Thread tc;
	int i=0;
	 String src_dir_path 	= src_directory.getAbsolutePath(); 
	 String compute_destDir  = start_destDir;
	 destDir = new File(compute_destDir);
	
	  if(!destDir.exists())
	   {
		   System.out.println("create destDir:"+destDir.getAbsolutePath());
		   destDir.mkdir();
	   }
	
	  if(src_directory.isFile())
	  {
		  FileHelper.copyRec( src_directory, destDir);
	  }
	  else
	  {
		 File[] lstfiles = src_directory.listFiles();
		logger.info("Dest Dir : "+compute_destDir+" File Count: "+lstfiles.length);
		//System.out.println(" File Count : "+lstfiles.length);
		for(  i=0 ; i < lstfiles.length; i++) {
	           src = lstfiles[i];
	          // System.out.println("src file:"+src.getAbsolutePath());
	           if(src.isDirectory())
	           {    
	        	   getDirectoryCopy(src);
	           }else
	           {
	        	   try
	        	   {
	        	   if(JPEGExifExtraction.isGPSInfo(src.getAbsolutePath()))
	        	   {
		        	   compute_destDir = start_destDir+File.separatorChar+src.getName();
		        	   System.out.println("compute_destDir:"+compute_destDir);
		        	   FileHelper.copyRec( src, new File(compute_destDir));
	        	   } }
	        	   catch(Exception ex){
	        		   System.out.println("ERROR IMAGE :"+src.getAbsolutePath());
	        	   }
	           }
       } //end for
	  }
	return i;
}
 
}

