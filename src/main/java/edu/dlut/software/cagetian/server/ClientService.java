package edu.dlut.software.cagetian.server;

import edu.dlut.software.cagetian.FileInfo;
import edu.dlut.software.cagetian.storagenode.StorageNode;

import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by CageTian on 2017/7/7.
 */
public class ClientService implements Runnable {
    private Socket socket;
    private FileServer fileServer;

    public ClientService(Socket socket, FileServer fileServer) {
        this.socket = socket;
        this.fileServer = fileServer;
    }

    @Override
    public void run() {
        try{
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            char request=dis.readChar();
            FileInfo fileInfo=null;
            String uuid_str;
            ArrayList<StorageNode> list = fileServer.getNode_info();
            switch (request){
                case 'u'://upload
                    String[] info = dis.readUTF().split("#");
                    long fileSize = Long.parseLong(info[1]);//get file size;
                    System.out.println(fileSize);

                    Collections.sort(list, new Comparator<StorageNode>() {
                        @Override
                        public int compare(StorageNode o1, StorageNode o2) {
                            return (int)(o2.getRestVolume()-o1.getRestVolume());
                        }
                    });
                    //负载均衡
                    try {
                        long nrest1 = list.get(0).getRestVolume() - fileSize;
                        long nrest2 = list.get(1).getRestVolume() - fileSize;
                        if (nrest1 > 0 && nrest2 > 0) {
                            list.get(0).setRestVolume(nrest1);
                            list.get(1).setRestVolume(nrest2);
                            fileInfo = new FileInfo(UUID.randomUUID().toString(), info[0], fileSize, list.get(0), list.get(1));
                            fileServer.getFile_info().put(fileInfo.getFile_id(), fileInfo);
                            System.out.println(fileInfo.getFile_id());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("System not ready or have less than two nodes remain");
                    }
                    //exception node not enough
                    break;
                case 'd'://download
                    System.out.println("download");
                    uuid_str = dis.readUTF();
                    fileInfo = fileServer.getFile_info().get(uuid_str);
                    break;
                case 'r'://remove
                    System.out.println("remove");
                    uuid_str = dis.readUTF();
                    fileInfo = fileServer.getFile_info().get(uuid_str);
                    fileServer.getFile_info().remove(uuid_str);
                    //recover the rest volume
                    StorageNode n1 = fileInfo.getMain_node();
                    StorageNode n2 = fileInfo.getSec_node();
                    n1.setRestVolume(n1.getRestVolume() + fileInfo.getFile_size());
                    n2.setRestVolume(n2.getRestVolume() + fileInfo.getFile_size());
                    list.set(list.indexOf(n1), n1);
                    list.set(list.indexOf(n2), n2);
                    break;
                default:
                    break;

            }

            oos.writeObject(fileInfo);
            oos.flush();
            dis.close();
            oos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
