package edu.dlut.software.cagetian.storagenode;

import edu.dlut.software.cagetian.FileInfo;

import java.io.*;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 *  服务器节点主类，主要响应客户端请求
 *  并隔一段时间向FileServer发送节点信息
 * Created by CageTian on 2017/7/6.
 */
public class StorageNode implements Serializable{
    private static final long DEFAULT_VOLUME = 10737418240L;
    private static DecimalFormat df = null;
    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    private String nodeName;
    private String nodeIP;
    private int nodePort;
    private String rootFolder;
    private long volume;
    private String fileServerIP;
    private int fileServerPort;
    private long restVolume;
    private int file_num;

    private HashMap<String,FileInfo> file_info_map;

    public StorageNode(String nodeName, String nodeIP, int nodePort, long restVolume,int file_num) {
        this.nodeName = nodeName;
        this.nodeIP = nodeIP;
        this.nodePort = nodePort;
        this.restVolume = restVolume;
        this.file_info_map = new HashMap<>();
        this.file_num=file_num;
        this.volume=DEFAULT_VOLUME;
    }

    public StorageNode(File f) throws IOException {
        getProperties(f);
        this.file_info_map = getAllFile(this.rootFolder, new HashMap<String,FileInfo>());
        this.file_num=file_info_map.size();
    }
    public StorageNode(String nodeName){
        this.nodeName=nodeName;
        this.file_info_map = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        StorageNode storageNode=new StorageNode(new File(args[0]));
        ServerSocket serverSocket=new ServerSocket(storageNode.nodePort);
        storageNode.notifyServer();
        while(true){
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress().toString()+socket.getPort()+" access in:");
            storageNode.clientService(socket);
        }
    }

    private HashMap<String, FileInfo> getAllFile(String rootFolder, HashMap<String, FileInfo> map) {
        File directory = new File(rootFolder);
        if (directory.isDirectory()) {
            for (File file : directory.listFiles())
                getAllFile(file.getAbsolutePath(), map);
        } else if (directory.isFile()) {
            this.restVolume-=directory.length();
            map.put(directory.getName(), FileInfo.getNodeInitInstance(directory.getName()
                    , directory.getParentFile().getName(), directory.length(), directory));
        }
        return map;
    }

    /**
     * 隔一段时间发送节点信息给FileServer
     */
    public void notifyServer() {
        new Thread(new notifyServerService(this, 3000)).start();
    }

    /**
     * 响应客户端的上传、下载、删除请求
     * 及其他节点的备份请求
     *
     * @param socket
     */
    public void clientService(Socket socket) {
        new Thread(new StorageClientService(this, socket)).start();
    }

    /**
     * 获取配置文件信息，主要用于节点的初始化
     * @param prop_file
     * @throws IOException
     */
    private void getProperties(File prop_file) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(prop_file));
        pps.load(in);
        nodeName=pps.getProperty("NodeName");
        nodeIP=pps.getProperty("NodeIP");
        nodePort=Integer.parseInt(pps.getProperty("NodePort"));
        rootFolder=pps.getProperty("RootFolder");
        volume=Long.parseLong(pps.getProperty("Volume"));
        restVolume = Long.parseLong(pps.getProperty("RestVolume"));
        fileServerIP=pps.getProperty("FileServerIP");
        fileServerPort=Integer.parseInt(pps.getProperty("FileServerPort"));
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof StorageNode){
            StorageNode s=(StorageNode)o;
            return s.getNodeName().equals(this.getNodeName()) ;
        }
        return false;
    }
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIP() {
        return nodeIP;
    }


    public int getNodePort() {
        return nodePort;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public int getFile_num() {
        return file_num;
    }

    public void setFile_num(int file_num) {
        this.file_num = file_num;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getFileServerIP() {
        return fileServerIP;
    }

    public long getRestVolume() {
        return restVolume;
    }

    public void setRestVolume(long restVolume) {
        this.restVolume = restVolume;
    }

    public HashMap<String, FileInfo> getFile_info_map() {
        return file_info_map;
    }

}
