package edu.dlut.software.cagetian.storagenode;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Created by CageTian on 2017/7/7.
 * 每隔一段时间将服务器的文件、存储等信息发送给服务器，并通知服务器节点正常运行
 */
public class notifyServerService implements Runnable {
    private StorageNode storageNode;
    private int serverPort;
    private String message;

    notifyServerService(StorageNode storageNode, int serverPort) {
        this.serverPort=serverPort;
        this.storageNode=storageNode;
        this.message=storageNode.getNodeName()+'#'+storageNode.getNodeIP()
                +'#'+storageNode.getNodePort()+'#'+storageNode.getRestVolume()+'#'
                +storageNode.getFile_num();
    }

    @Override
    public void run() {
        try {
            DatagramSocket ds = new DatagramSocket();
            while (true){
                message=storageNode.getNodeName()+'#'+storageNode.getNodeIP()
                        +'#'+storageNode.getNodePort()+'#'+storageNode.getRestVolume()+'#'+
                storageNode.getFile_num();
                DatagramPacket dp = new DatagramPacket(message.getBytes(),
                        message.getBytes().length,
                        InetAddress.getByName(storageNode.getFileServerIP()), serverPort);
                ds.send(dp);
                Thread.sleep(10000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
