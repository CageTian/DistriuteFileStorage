package edu.dlut.software.cagetian.storagenode;

import edu.dlut.software.cagetian.FileInfo;

import java.io.*;
import java.net.Socket;

/**
 * Created by CageTian on 2017/7/8.
 * StorageNode的客户服务线程类
 * 响应客户发来的上传、下载、删除请求
 */
public class StorageClientService implements Runnable {
    private StorageNode storageNode;
    private Socket socket;

    /**
     * 构造函数
     *
     * @param storageNode 节点类的对象
     * @param socket      节点响应客户端请求的套接字
     */
    public StorageClientService(StorageNode storageNode, Socket socket) {
        this.storageNode = storageNode;
        this.socket = socket;
    }

    /**
     * 响应客户端的下载请求<br>
     * 从客户端用户文件夹下寻找文件并传输给客户端
     * @param ois 传入的对象输入流
     * @throws Exception
     */
    public void clientDownload(ObjectInputStream ois) throws Exception {
        String file_uuid = ois.readUTF();
        String client_name = ois.readUTF();
        String file_path=storageNode.getRootFolder()+
                File.separatorChar+client_name+ File.separatorChar+file_uuid;
        File file=new File(file_path);
        if(file.isFile())
            send(file, new ObjectOutputStream(socket.getOutputStream()));
    }

    /**
     * clientUpload的内部函数<br>
     * 负责将文件传输至备份节点
     * @param fileInfo
     * @throws Exception
     */
    private void backUpToBNode(FileInfo fileInfo) throws Exception {
        StorageNode bNode=fileInfo.getSec_node();
        if (!bNode.equals(storageNode)) {
            Socket node_socket = new Socket(bNode.getNodeIP(), bNode.getNodePort());
            ObjectOutputStream oos = new ObjectOutputStream(node_socket.getOutputStream());
            oos.writeChar('b');
            oos.writeObject(fileInfo);
            send(fileInfo.getFile(), oos);
        } else {
            System.out.println("received file but last node have some problems");
        }

    }

    /**
     * 响应节点服务器的备份请求<br>
     * 接收备份文件并更新文件信息
     * @param ois
     * @throws Exception
     */
    private void receiveBackUp(ObjectInputStream ois) throws Exception {
        FileInfo fileInfo = receive(ois);
        System.out.println("======== 节点成功接收备份文件 [File Name：" +
                fileInfo.getFile_id() + "] [Size：" + fileInfo.getFile_size() + "] ========");
        storageNode.getFile_info_map().put(fileInfo.getFile_id(),fileInfo);
        storageNode.setFile_num(storageNode.getFile_info_map().size());
        storageNode.setRestVolume(storageNode.getRestVolume()-fileInfo.getFile_size());

    }

    /**
     * 响应客户端的上传请求<br>
     * 将客户端上传的文件接收到相应的客户端文件夹下
     * 并更新文件列表信息
     * @param ois
     * @throws Exception
     */
    public void clientUpload(ObjectInputStream ois) throws Exception {
        FileInfo fileInfo = receive(ois);
        System.out.println("======== 节点成功接收上传文件 [File Name：" +
                fileInfo.getFile_id() + "] [Size：" + fileInfo.getFile_size() + "] ========");
        socket.close();
        storageNode.getFile_info_map().put(fileInfo.getFile_id(),fileInfo);
        storageNode.setFile_num(storageNode.getFile_info_map().size());
        storageNode.setRestVolume(storageNode.getRestVolume()-fileInfo.getFile_size());
        backUpToBNode(fileInfo);
    }

    /**
     * 响应客户端的删除请求<br>
     * 删除客户端文件夹下的文件
     * 更新文件信息并通知备份节点删除备份文件
     * @param ois
     * @throws Exception
     */
    public void clientRemove(ObjectInputStream ois) throws Exception {
        String client_name = ois.readUTF();
        FileInfo fileInfo = (FileInfo) ois.readObject();
        String uuid = fileInfo.getFile_id();
        try {
            File local_file = storageNode.getFile_info_map().get(uuid).getFile();
            long size=local_file.length();

            if (storageNode.getFile_info_map().keySet().contains(uuid) &&
                    fileInfo.getClient_name().equals(client_name) && local_file.delete()) {
                System.out.println(client_name + " deleted " + uuid);
                storageNode.getFile_info_map().remove(uuid);
                storageNode.setFile_num(storageNode.getFile_info_map().size());
                storageNode.setRestVolume(storageNode.getRestVolume()+size);
                if (storageNode.equals(fileInfo.getMain_node())) {
                    Socket bSocket = new Socket(fileInfo.getSec_node()
                            .getNodeIP(), fileInfo.getSec_node().getNodePort());
                    ObjectOutputStream oos = new ObjectOutputStream(bSocket.getOutputStream());
                    oos.writeChar('r');
                    oos.flush();
                    oos.writeUTF(client_name);
                    oos.flush();
                    oos.writeObject(fileInfo);
                    oos.flush();
                    oos.close();
                    bSocket.close();
                }
            }
        }catch (NullPointerException e){
            System.out.println("no file found to remove");
        }
    }


    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            char ch = ois.readChar();
            switch (ch){
                case 'd':
                    clientDownload(ois);
                    break;
                case 'u':
                    clientUpload(ois);
                    break;
                case 'r':
                    clientRemove(ois);
                    break;
                case 'b':
                    receiveBackUp(ois);
                default:
                    break;//do somethings
            }
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * clientUpload和receiveBackup的内部函数
     * 接收文件
     * @param ois
     * @return
     * @throws Exception
     */
    private FileInfo receive(ObjectInputStream ois) throws Exception {
        FileInfo fileInfo=(FileInfo)ois.readObject();
        String file_uuid = fileInfo.getFile_id();
        String client_name=fileInfo.getClient_name();
        File directory=new File(storageNode.getRootFolder()
                + File.separatorChar +client_name);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory.getAbsolutePath()+File.separatorChar + file_uuid);
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos=new DataOutputStream(fos);
        DataInputStream dis=new DataInputStream(ois);

        byte[] bytes;
        int length=ois.readInt();
        while (length != -1) {
            bytes=new byte[length];
            ois.readFully(bytes,0,length);
            dos.writeInt(length);
            dos.flush();
            dos.write(bytes, 0, length);
            dos.flush();
            length=ois.readInt();
        }
        dos.writeInt(-1);
        dos.flush();
        dos.close();
        ois.close();
        fos.close();
        fileInfo.setFile(file);
        return fileInfo;
    }

    /**
     * clientDownload和backoBnode的内部函数
     * 发送文件
     * @param file
     * @param oos
     * @throws Exception
     */
    private void send(File file, ObjectOutputStream oos) throws Exception {
        FileInputStream fis=new FileInputStream(file);
        DataInputStream dis=new DataInputStream(fis);

        System.out.println("======== 开始传输文件 ========");
        byte[] bytes;
        int length;
        long progress = 0;
        while((length = dis.readInt()) != -1) {
            oos.writeInt(length);
            oos.flush();
            bytes=new byte[length];
            dis.readFully(bytes,0,length);
            oos.write(bytes, 0, length);
            oos.flush();
            progress += length;
            System.out.print("| " + (100*progress/file.length()) + "% |");
        }
        oos.writeInt(-1);
        System.out.println();
        System.out.println("======== 文件传输成功 ========");
        fis.close();
        fis.close();
        oos.close();


    }
}
