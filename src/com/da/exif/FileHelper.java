package com.da.exif;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Helper methods for file manipulation. 
 * All methods are <em>thread safe</em>.
 * 
 * @version $Id: FileHelper.java 493628 2007-01-07 01:42:48Z joerg $
 */
public final class FileHelper {
	private static final Logger logger = Logger.getLogger(FileHelper.class);
   // private static int BUF_SIZE = 50000;
	private static int BUF_SIZE = 32*1024;
    private static byte[] BUF = new byte[BUF_SIZE];

    /**
     * Deletes a file specified by a path.
     *  
     * @param path path of file to be deleted
     * @return <code>true</code> if file has been deleted, <code>false</code> otherwise
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * Checks if a file specified by a path exits.
     *  
     * @param path path of file to be checked
     * @return <code>true</code> if file exists, <code>false</code> otherwise
     */
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Creates a file specified by a path. All necessary directories will be created.
     * 
     * @param path path of file to be created
     * @return <code>true</code> if file has been created, <code>false</code> if the file already exists
     * @throws  IOException
     *          If an I/O error occurred
     */
    public static boolean createFile(String path) throws IOException {
        File file = new File(path);
        if (file.isDirectory()) {
            return file.mkdirs();
        } else {
            File dir = file.getParentFile();
            // do not check if this worked
			// , as it may also return false, when all neccessary dirs are present
            dir.mkdirs();
            return file.createNewFile();
        }
    }

    /**
     * Removes a file. If the specified file is a directory all contained files will
     * be removed recursively as well. 
     * 
     * @param toRemove file to be removed
     */
    public static void removeRec(File toRemove) {
        if (toRemove.isDirectory()) {
            File fileList[] = toRemove.listFiles();
            for (int a = 0; a < fileList.length; a++) {
                removeRec(fileList[a]);
            }
        }
        toRemove.delete();
    }

    /**
     * Moves one directory or file to another. Existing files will be replaced.
     * 
     * @param source file to move from
     * @param target file to move to
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     */
    public static void moveRec(File source, File target) throws IOException {
        byte[] sharedBuffer = new byte[BUF_SIZE];
        moveRec(source, target, sharedBuffer);
    }

    static void moveRec(File source, File target, byte[] sharedBuffer) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }
            if (target.isDirectory()) {

                File[] files = source.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    File targetFile = new File(target, file.getName());
                    if (file.isFile()) {
                        if (targetFile.exists()) {
                            targetFile.delete();
                        }
                        if (!file.renameTo(targetFile)) {
                            copy(file, targetFile, sharedBuffer);
                            file.delete();
                        }
                    } else {
                        if (!targetFile.exists()) {
                            if (!targetFile.mkdirs()) {
                                throw new IOException("Could not create target directory: "
                                        + targetFile);
                            }
                        }
                        moveRec(file, targetFile);
                    }
                }
                source.delete();
            }
        } else {
            if (!target.isDirectory()) {
                copy(source, target, sharedBuffer);
                source.delete();
            }
        }
    }

    /**
     * Copies one directory or file to another. Existing files will be replaced.
     * 
     * @param source directory or file to copy from
     * @param target directory or file to copy to
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     */
    public static void copyRec(File source, File target) throws IOException {
        byte[] sharedBuffer = new byte[BUF_SIZE];
        copyRec(source, target, sharedBuffer);
    }

    static void copyRec(File source, File target, byte[] sharedBuffer) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }
            if (target.isDirectory()) {

                File[] files = source.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    File targetFile = new File(target, file.getName());
                    if (file.isFile()) {
                        if (targetFile.exists()) {
                        	logger.info("file exists delete targetFile: "+targetFile);
                            targetFile.delete();
                        }
                        // 파일 한개에 대해서만 에러 처리 로직 추가 
                         copy(file, targetFile, sharedBuffer);
                       
                    } else {
                        targetFile.mkdirs();
                        copyRec(file, targetFile);
                    }
                }
            }
        } else {
            if (!target.isDirectory()) {
                if (!target.exists()) {
                    File dir = target.getParentFile();
                    if(!dir.exists() && !dir.mkdirs()) {
                        throw new IOException("Could not create target directory: " + dir);
                    }
                    if (!target.createNewFile()) {
                        throw new IOException("Could not create target file: " + target);
                    }
                }
                copy(source, target, sharedBuffer);
            }
        }
    }

    /**
     * Copies one file to another using {@link #copy(InputStream, OutputStream)}.
     * 
     * @param input
     *            source file
     * @param output
     *            destination file
     * @return the number of bytes copied
     * @throws IOException
     *             if an I/O error occurs (may result in partially done work)
     * @see #copy(InputStream, OutputStream)
     */
    public static long copy(File input, File output) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(input);
            return copy(in, output);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Copies one file to another using the supplied buffer.
     * 
     * @param input source file
     * @param output destination file
     * @param copyBuffer buffer used for copying
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     * @see #copy(InputStream, OutputStream)
     */
    public static long copy(File input, File output, byte[] copyBuffer) throws IOException {
            long lngSize = 0;
        	long startTime, endTime; 
        	startTime = System.currentTimeMillis();
       //
        try 
        {
        	lngSize = copyFile(input, output, copyBuffer);
        	 endTime = System.currentTimeMillis();
 	        String msg =  "copy Finished :" + input + " to " + output +    ", time=" + (endTime - startTime) + "ms"    +", filesize="+input.length();
 	        logger.info(msg);
 	        System.err.println(msg);
 	       return lngSize;
        } catch (IOException ioe)
        { // 
        	String msg =  "copy Error : "+ioe.getLocalizedMessage()+", src: "+ input + " to :" + output;  
	        logger.warn( msg);
	        System.err.println(msg);
        }finally
        {  
        	return lngSize;
        }
    }

	private static long copyFile(File input, File output, byte[] copyBuffer)
			throws FileNotFoundException, IOException {
		FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
            return copy(in, out, copyBuffer);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
	}

    /**
     * Copies an <code>InputStream</code> to a file using {@link #copy(InputStream, OutputStream)}.
     * 
     * @param in stream to copy from 
     * @param outputFile file to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     * @see #copy(InputStream, OutputStream)
     */
    public static long copy(InputStream in, File outputFile) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            return copy(in, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</code> using a local internal buffer for performance.
     * Compared to {@link #globalBufferCopy(InputStream, OutputStream)} this method allows for better
     * concurrency, but each time it is called generates a buffer which will be garbage.
     * 
     * @param in stream to copy from 
     * @param out stream to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     * @see #globalBufferCopy(InputStream, OutputStream)
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        // we need a buffer of our own, so no one else interferes
        byte[] buf = new byte[BUF_SIZE];
        return copy(in, out, buf);
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</code> using a global internal buffer for performance.
     * Compared to {@link #copy(InputStream, OutputStream)} this method generated no garbage,
     * but decreases concurrency.
     * 
     * @param in stream to copy from 
     * @param out stream to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     * @see #copy(InputStream, OutputStream)
     */
    public static long globalBufferCopy(InputStream in, OutputStream out) throws IOException {
        synchronized (BUF) {
            return copy(in, out, BUF);
        }
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</code> using the specified buffer. 
     * 
     * @param in stream to copy from 
     * @param out stream to copy to
     * @param copyBuffer buffer used for copying
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done work)  
     * @see #globalBufferCopy(InputStream, OutputStream)
     * @see #copy(InputStream, OutputStream)
     */
    public static long copy(InputStream in, OutputStream out, byte[] copyBuffer) throws IOException {
        long bytesCopied = 0;
        int read = -1;
    	BufferedInputStream bis;
    	BufferedOutputStream bos;
    	
        bis = new BufferedInputStream(in);
 	    bos = new BufferedOutputStream(out);
 	   
	   
	   for(read = bis.read(copyBuffer); read > 0; read = bis.read(copyBuffer)) {
	       bos.write(copyBuffer, 0, read);
	       bytesCopied += read;
	   }
	   bis.close();
	   bos.close();
	   
        return bytesCopied;
    }
}
