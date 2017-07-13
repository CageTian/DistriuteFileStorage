package edu.dlut.software.cagetian.util;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * -----------------------------------------------------------------------------
 * Used to provide an example of uncompressing a file in the GZIP Format.
 *
 * @version 1.0
 * @author  Jeffrey M. Hunter  (jhunter@idevelopment.info)
 * @author  http://www.idevelopment.info
 * -----------------------------------------------------------------------------
 */

public class UncompressFileGZIP {

    /**
     * Uncompress the incoming file.
     * @param inFileName Name of the file to be uncompressed
     */
    public static File doUncompressFile(String inFileName) {

        try {

            if (!getExtension(inFileName).equalsIgnoreCase("gz")) {
                System.err.println("File name must have extension of \".gz\"");
                System.exit(1);
            }

            System.out.println("Opening the compressed file.");
            GZIPInputStream in = null;
            try {
                in = new GZIPInputStream(new FileInputStream(inFileName));
            } catch(FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                System.exit(1);
            }

            System.out.println("Open the output file.");
            String outFileName = getFileName(inFileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(outFileName);
            } catch (FileNotFoundException e) {
                System.err.println("Could not write to file. " + outFileName);
                System.exit(1);
            }

            System.out.println("Transfering bytes from compressed file to the output file.");
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            System.out.println("Closing the file and stream");
            in.close();
            out.close();
            return new File(outFileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Used to extract and return the extension of a given file.
     * @param f Incoming file to get the extension of
     * @return <code>String</code> representing the extension of the incoming
     *         file.
     */
    public static String getExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');

        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i+1);
        }
        return ext;
    }

    /**
     * Used to extract the filename without its extension.
     * @param f Incoming file to get the filename
     * @return <code>String</code> representing the filename without its
     *         extension.
     */
    public static String getFileName(String f) {
        String fname = "";
        int i = f.lastIndexOf('.');

        if (i > 0 &&  i < f.length() - 1) {
            fname = f.substring(0,i);
        }
        return fname;
    }

    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {

            doUncompressFile("D:\\Dowloads\\pycharm-professional-2017.1.4.exe.gz");

    }

}
