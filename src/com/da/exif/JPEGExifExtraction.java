package com.da.exif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class JPEGExifExtraction {
	 /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        /*if ( args.length != 1 ) {
            System.err.println("Usage:java JPEGExifExtraction filepath");
            System.exit(0);
        }*/
        
       // String filePath = "C:\\TEMP\\173\\351\\1312729.jpg";
    	 String filePath = "C:\\TEMP\\173\\gps.jpg";
    	 
    	 if (isGPSInfo(filePath))
    	 {
    		 System.err.println( filePath ); 
    	 }
    }

	public static boolean isGPSInfo(String filePath)
			throws FileNotFoundException, IOException, MetadataException {
		boolean bret=false;
	//	System.out.println("filePath:"+filePath);
		JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(new File(filePath)));
        BufferedImage image = jpegDecoder.decodeAsBufferedImage();
         // now you can use the image
        JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
        Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
        Iterator directories = metadata.getDirectoryIterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            
            // iterate through tags and print to System.out
            Iterator tags = directory.getTagIterator(); 
            while (tags.hasNext()) {
                Tag tag = (Tag)tags.next();
                 if("GPS".equals(tag.getDirectoryName()))	
                { 
                	/* if(tag.getTagType() > 4)
                	 {*/
                	 if("GPS Altitude".equals(tag.getTagName()))
                	 {
                		 if(!"0 metres".equals(tag.getDescription()))
                		 {
	                	System.out.println( "============================"); 
	                	 bret=true;
		                System.out.println( tag.getDirectoryName()); 
		                //System.out.println( tag.getTagType()); 
		                System.out.println( tag.getTagName()); 
		                System.out.println( tag.getDescription()); 
		               // System.out.println( tag.getTagTypeHex());
		                System.out.println( "============================");
		                // use Tag.toString()
		                /*
		                [Exif] Make - Apple
		                [Exif] Model - iPhone 4
		                [GPS] GPS Version ID - 2 2 0 0
		                [GPS] GPS Latitude Ref - N
		                [GPS] GPS Latitude - 37"34'30.0
		                [GPS] GPS Longitude Ref - E
		                [GPS] GPS Longitude - 126"59'46.799927
		                [GPS] GPS Altitude Ref - Sea level
		                [GPS] GPS Altitude - 16407/2110 metres
		                [GPS] GPS Time-Stamp - 15:47:4 UTC
		                [GPS] GPS Img Direction - True direction
		                [GPS] Unknown tag (0x0011) - 38945/121 degrees
		                */
		                System.out.println(tag);
		                break;
                	  }
                	 }
               }
            }
        }
        return bret;
	}
	public static boolean printGPSInfo(String filePath)
	throws FileNotFoundException, IOException, MetadataException {
boolean bret=false;
//	System.out.println("filePath:"+filePath);
JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(new File(filePath)));
BufferedImage image = jpegDecoder.decodeAsBufferedImage();
 // now you can use the image
JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
Iterator directories = metadata.getDirectoryIterator();
while (directories.hasNext()) {
    Directory directory = (Directory)directories.next();
    
    // iterate through tags and print to System.out
    Iterator tags = directory.getTagIterator(); 
    while (tags.hasNext()) {
        Tag tag = (Tag)tags.next();
         
        	System.out.println( "============================"); 
            System.out.println( tag.getDirectoryName()); 
            System.out.println( tag.getTagType()); 
            System.out.println( tag.getDescription()); 
            System.out.println( tag.getTagName()); 
            System.out.println( tag.getTagTypeHex());
            
            System.out.println( "============================");
            System.out.println(tag);
            System.err.println( "-----------------------------");
            // use Tag.toString()
            /*
            [Exif] Make - Apple
            [Exif] Model - iPhone 4
            [GPS] GPS Version ID - 2 2 0 0
            [GPS] GPS Latitude Ref - N
            [GPS] GPS Latitude - 37"34'30.0
            [GPS] GPS Longitude Ref - E
            [GPS] GPS Longitude - 126"59'46.799927
            [GPS] GPS Altitude Ref - Sea level
            [GPS] GPS Altitude - 16407/2110 metres
            [GPS] GPS Time-Stamp - 15:47:4 UTC
            [GPS] GPS Img Direction - True direction
            [GPS] Unknown tag (0x0011) - 38945/121 degrees
            */
            
         
    }
}
return bret;
}

}