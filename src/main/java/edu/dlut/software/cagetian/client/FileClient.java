package edu.dlut.software.cagetian.client;

import edu.dlut.software.cagetian.FileInfo;
import edu.dlut.software.cagetian.storagenode.StorageNode;
import edu.dlut.software.cagetian.util.CompressFileGZIP;
import edu.dlut.software.cagetian.util.Tool;
import edu.dlut.software.cagetian.util.UncompressFileGZIP;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.zip.GZIPInputStream;


/**
 * Created by CageTian on 2017/7/6.
 */
public class FileClient  {
    private String serverIP;
    private int server_port;
    private String client_name;
    FileClient(File f) throws IOException {
        getProperties(f);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            System.out.println("usage:\n-d file_uuid --------- download file in server" +
                    "\t\n-r file_uuid -------- delete file in server" +
                    "\t\n-u file_path -------- upload file to server");
        else {
            FileClient fileClient = new FileClient(new File(
                    "D:\\Projects\\Projects_JavaWeb\\DistriuteFileStorage\\src\\main\\resources\\client1.properties"));
            switch (args[0]) {
                case "-d":
                    fileClient.download(args[1], args[2]);
                    break;
                case "-r":
                    fileClient.remove(args[1]);
                    break;
                case "-u":
                    fileClient.upload(args[1]);
                    break;
                default:
                    System.out.println("no param like " + args[0]);
                    break;
            }
        }

    }

    public void upload(String file_path) {
        File file = new File(file_path);
        File z_file=CompressFileGZIP.doCompressFile(file_path);
        FileInfo fileInfo;
        Socket socket;
        try {
            socket = new Socket(serverIP, server_port);
            fileInfo = getServerMes(socket, 'u', file.getName() + "#" +
                    String.valueOf(z_file.length()));
            fileInfo.setFile_size(z_file.length());
            System.out.println(fileInfo);
        } catch (Exception e) {
            System.out.println("fail to connect server to get update information.please retry");
            return;
        }
        try {
            send(fileInfo, z_file, fileInfo.getMain_node());
        }catch (Exception e){
            try {
                send(fileInfo, z_file, fileInfo.getSec_node());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

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
        byte[] bytes = new byte[2048];
        int length;
        long progress = 0;
        byte[]byte_tmp;
        int encrypt_len;
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {

            byte_tmp=Tool.encrypt(bytes);
            encrypt_len=byte_tmp.length;
            oos.writeInt(encrypt_len);
            oos.flush();
            oos.write(byte_tmp, 0, encrypt_len);
            oos.flush();
            progress += length;
            System.out.print("| " + (100 * progress / file.length()) + "% |");
        }
        oos.writeInt(-1);
        oos.flush();
        System.out.println();
        System.out.println("======== 文件传输成功 ========"+file.length());
        oos.close();
        socket.close();

    }

    private void remove(String uuid) throws Exception {
        Socket socket=new Socket(serverIP,server_port);
        FileInfo fileInfo = getServerMes(socket, 'r', uuid);

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
    }

    public void download(String uuid, String file_path) {
        File directory;
        Socket socket;
        FileInfo fileInfo;
        try {

            directory = new File(file_path);
            socket = new Socket(serverIP, server_port);
            fileInfo = getServerMes(socket, 'd', uuid);
        } catch (Exception e) {
            System.out.println("fail to connect server to get update information.please retry");
            return;
        }
        try {
            receive(fileInfo, uuid, directory, fileInfo.getMain_node());
        } catch (Exception e) {
            try {
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
