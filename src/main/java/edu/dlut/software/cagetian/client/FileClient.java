package edu.dlut.software.cagetian.client;

import edu.dlut.software.cagetian.FileInfo;
import edu.dlut.software.cagetian.storagenode.StorageNode;
import edu.dlut.software.cagetian.util.CompressFileGZIP;
import edu.dlut.software.cagetian.util.Tool;
import edu.dlut.software.cagetian.util.UncompressFileGZIP;

import java.io.*;
import java.net.Socket;
import java.util.Properties;



/**
 * Created by CageTian on 2017/7/6.
 */
public class FileClient  {
    private String serverIP;
    private int server_port;
    private String client_name;

    private FileClient(File f) throws IOException {
        getProperties(f);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3)
            System.out.println("usage:\nproperties_file -d file_uuid --------- download file in server" +
                    "\t\nproperties_file -r file_uuid -------- delete file in server" +
                    "\t\nproperties_file -u file_path -------- upload file to server");
        else {
            FileClient fileClient = new FileClient(new File(
                    args[0]));
            switch (args[1]) {
                case "-d":
                    fileClient.download(args[2], args[3]);
                    break;
                case "-r":
                    fileClient.remove(args[2]);
                    break;
                case "-u":
                    fileClient.upload(args[2]);
                    break;
                default:
                    System.err.println("no param like " + args[1]);
                    break;
            }
        }

    }

    private void upload(String file_path) {
        File file = new File(file_path);
        File z_file=CompressFileGZIP.doCompressFile(file_path);
        FileInfo fileInfo;
        Socket socket;
        try {
            socket = new Socket(serverIP, server_port);
            fileInfo = getServerMes(socket, 'u', file.getName() + "#" +
                    String.valueOf(z_file.length()) + "#" + client_name);
            fileInfo.setFile_size(z_file.length());
            System.out.println(fileInfo);
        } catch (Exception e) {
            System.err.println("fail to connect server to get update information.please retry");
            return;
        }
        try {
            send(fileInfo, z_file, fileInfo.getMain_node());
            if (z_file.delete()) {
                System.out.println("deleted the temp file");
            }
        }catch (Exception e){
            System.err.println("upload to node erro please check your internet");
        }
    }

    private void send(FileInfo fileInfo, File file, StorageNode node) throws Exception {
        Socket socket = new Socket(node.getNodeIP(), node.getNodePort());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        oos.writeChar('u');
        oos.flush();
        oos.writeObject(fileInfo);
        oos.flush();

        System.out.println("======== 开始传输文件 ========");
        byte[] bytes = new byte[1024 * 128];
        int length;
        long progress = 0;
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {

            bytes = Tool.encrypt(bytes);
            length = bytes.length;
            oos.writeInt(length);
            oos.flush();
            oos.write(bytes, 0, length);
            oos.flush();
            progress += length;
            System.out.print("| " + (100 * progress / file.length()) + "% |");
        }
        oos.writeInt(-1);
        oos.flush();
        System.out.println();
        System.out.println("======== 文件传输成功 ======== file size:" + file.length());
        System.out.println("UUID: " + fileInfo.getFile_id());

        oos.close();
        socket.close();

    }

    private void remove(String uuid) throws Exception {
        Socket socket=new Socket(serverIP,server_port);
        FileInfo fileInfo = new FileInfo();
        try {
            fileInfo = getServerMes(socket, 'r', uuid + "#" + client_name);
        } catch (Exception e) {
            if (e instanceof NullPointerException)
                System.err.println(uuid + " not found please check file uuid");
            else
                System.err.println("connection erro,please try again");
            System.exit(1);
        }

        StorageNode firstNode=fileInfo.getMain_node();
        socket=new Socket(firstNode.getNodeIP(),firstNode.getNodePort());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeChar('r');
        oos.flush();
        oos.writeUTF(client_name);
        oos.flush();
        oos.writeObject(fileInfo);
        oos.flush();
        oos.close();
        socket.close();
        System.out.println("success remove file: " + fileInfo.getFile_id());
    }

    private void download(String uuid, String file_path) {
        File directory;
        Socket socket;
        FileInfo fileInfo;
        try {

            directory = new File(file_path);
            socket = new Socket(serverIP, server_port);
            fileInfo = getServerMes(socket, 'd', uuid + "#" +
                    client_name);
        } catch (Exception e) {
            if (e instanceof NullPointerException)
                System.err.println(uuid + " not found please check file uuid");
            else
                System.err.println("connection erro,please try again");
            return;
        }
        try {
            receive(fileInfo, uuid, directory, fileInfo.getMain_node());
        } catch (Exception e) {
            try {
                System.err.println("fail to get file from main node,now using second node...");
                receive(fileInfo, uuid, directory, fileInfo.getSec_node());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

    }

    private void receive(FileInfo fileInfo, String uuid, File directory,
                         StorageNode node) throws Exception {
        Socket socket = new Socket(node.getNodeIP(), node.getNodePort());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeChar('d');
        oos.flush();
        oos.writeUTF(uuid);
        oos.flush();
        oos.writeUTF(client_name);
        oos.flush();
        ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());

        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory.getAbsolutePath()
                + File.separatorChar + fileInfo.getFile_name()+ ".gz");
        FileOutputStream fos = new FileOutputStream(file);

        byte[] bytes;
        int length;
        while ((length=ois.readInt())!= -1) {
            bytes=new byte[length];
            ois.readFully(bytes,0,length);
            bytes=Tool.decrypt(bytes);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
        }
        oos.close();
        fos.close();
        ois.close();
        File row_file= UncompressFileGZIP.doUncompressFile(file.toString());
        if(file.delete())
            System.out.println("delete zip file");
        System.out.println("======== 文件下载成功 [File Name：" + fileInfo.getFile_name()
                + "] [zip size：" + file.length() + "] ====[row size: "+row_file.length()+"]====");
        socket.close();
    }

    private FileInfo getServerMes(Socket socket, char ch, String uName) throws Exception {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        dos.writeChar(ch);
        dos.flush();
        dos.writeUTF(uName);
        dos.flush();
        FileInfo fileInfo = (FileInfo) ois.readObject();
        fileInfo.setClient_name(client_name);
        dos.close();
        ois.close();
        socket.close();
        return fileInfo;

    }

    private void getProperties(File prop_file) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(prop_file));
        pps.load(in);
        serverIP=pps.getProperty("FileServerIP");
        server_port=Integer.parseInt(pps.getProperty("FileServerPort"));
        client_name=pps.getProperty("ClientName");
    }
}
