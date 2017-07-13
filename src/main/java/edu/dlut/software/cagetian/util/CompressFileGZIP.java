package edu.dlut.software.cagetian.util;
import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * -----------------------------------------------------------------------------
 * Used to provide an example of compressing a file using the GZIP Format.
 *
 * @version 1.0
 * @author  Jeffrey M. Hunter  (jhunter@idevelopment.info)
 * @author  http://www.idevelopment.info
 * -----------------------------------------------------------------------------
 */

public class CompressFileGZIP {

    /**
     * Perform file compression.
     * @param inFileName Name of the file to be compressed
     */
    public static File doCompressFile(String inFileName) {

        try {

            System.out.println("Creating the GZIP output stream.");
            String outFileName = inFileName + ".gz";
            GZIPOutputStream out = null;
            try {
                out = new GZIPOutputStream(new FileOutputStream(outFileName));
            } catch(FileNotFoundException e) {
                System.err.println("Could not create file: " + outFileName);
                System.exit(1);
            }


            System.out.println("Opening the input file.");
            FileInputStream in = null;
            try {
                in = new FileInputStream(inFileName);
            } catch (FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                System.exit(1);
            }

            System.out.println("Transfering bytes from input file to GZIP Format.");
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            System.out.println("Completing the GZIP file");
            out.finish();
            out.close();
            return new File(outFileName);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java CompressFileGZIP filename");
        } else {
            doCompressFile(args[0]);
        }


    }

}
