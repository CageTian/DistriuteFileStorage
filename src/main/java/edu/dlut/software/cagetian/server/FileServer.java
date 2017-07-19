package edu.dlut.software.cagetian.server;

import edu.dlut.software.cagetian.FileInfo;
import edu.dlut.software.cagetian.storagenode.StorageNode;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileServer主类
 * 负责响应客户端请求
 * 接收节点信息报文
 * 定时更新节点的状态和信息列表
 * Created by CageTian on 2017/7/6.
 */
public class FileServer implements Serializable{
    private List<StorageNode> node_info;
    private ConcurrentHashMap<String, FileInfo> file_info;
    private ConcurrentHashMap<String, Integer> node_statue;
    private String ip;
    private int port;
    FileServer (int port){
        this.port=port;
        node_statue = new ConcurrentHashMap<>();
        file_info = new ConcurrentHashMap<>();
        node_info =
                Collections.synchronizedList(new ArrayList<StorageNode>());
    }

    /**
     * 将FileServer对象从内存序列化保存进文件
     *
     * @param path
     * @param fileServer
     */
    public static void saveMainClassInstance(String path, FileServer fileServer) {
        try {
            ObjectOutputStream out = new ObjectOutputStream
                    (new FileOutputStream(path));
            out.writeObject(fileServer);
            out.close();
        } catch (Exception e) {
            System.out.println("fail to save main class instance");
        }

    }

    /**
     * 将FileServer对象从文中反序列化进内存
     * @return
     */
    public static FileServer getServerInstance() {
        try {
            ObjectInputStream in = new ObjectInputStream
                    (new FileInputStream("D:\\HomeWork\\server_data\\file_server.obj"));
            return (FileServer)in.readObject();
        } catch (Exception e) {
            System.out.println("no object found,will use new");
            return new FileServer(6666);
        }
    }

    public static void main(String[] args) throws Exception {
        final Scanner sc = new Scanner(System.in);
//        FileServer fs=new FileServer(6666);
        final FileServer fs = getServerInstance();
        fs.runNodeACKListenner();
        ServerSocket serverSocket=new ServerSocket(fs.port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ("0".equals(sc.nextLine())) {
                    saveMainClassInstance("D:\\HomeWork\\server_data\\file_server.obj", fs);
                    System.exit(0);//关闭当前进程。
                }
            }
        }).start();

        while(true){
            Socket socket=serverSocket.accept();
            System.out.println(socket.getInetAddress().toString() + " " + socket.getPort() + " access in:");
            new Thread(new ClientService(socket,fs)).start();
        }

    }

    /**
     * 监听节点发送的报文信息，定时更新节点的状态和节点信息列表
     * @throws Exception
     */
    public void runNodeACKListenner() throws Exception {
        new Thread(new NodeService(this)).start();
        new Thread(new CheckNodeService(this)).start();
    }

    public List<StorageNode> getNode_info() {
        return node_info;
    }
    public int getPort() {
        return port;
    }

    public ConcurrentHashMap<String, FileInfo> getFile_info() {
        return file_info;
    }

    public ConcurrentHashMap<String, Integer> getNode_statue() {
        return node_statue;
    }

}
