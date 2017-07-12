package edu.dlut.software.cagetian.monitor;


import edu.dlut.software.cagetian.server.ClientService;
import edu.dlut.software.cagetian.server.FileServer;
import net.sf.json.JSONObject;

import java.net.ServerSocket;
import java.net.Socket;

import static edu.dlut.software.cagetian.server.FileServer.getServerInstance;
import static edu.dlut.software.cagetian.server.FileServer.saveMainClassInstance;

/**
 * Created by CageTian on 2017/7/12.
 */
public class MyThread extends Thread{
    private static FileServer fs = getServerInstance();
    static JSONObject getNodeFileList(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("nodes",fs.getNode_info());
        jsonObject.put("files",fs.getFile_info().values());
        return jsonObject;
    }
    public void run() {
        try {
            fs.runNodeACKListenner();
            ServerSocket serverSocket=new ServerSocket(fs.getPort());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        saveMainClassInstance("D:\\HomeWork\\server_data\\file_server.obj", fs);
                    }
                }
            }).start();

            while(true){
                Socket socket=serverSocket.accept();
                System.out.println(socket.getInetAddress().toString() + " " + socket.getPort() + " access in:");
                new Thread(new ClientService(socket,fs)).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
