package edu.dlut.software.cagetian.server;

import edu.dlut.software.cagetian.storagenode.StorageNode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类为FileServer定时处理无消息节点的一个服务线程
 * 定时更新存储节点的状态
 * 将长时间无响应的节点从FileServer内存中删除
 * Created by CageTian on 2017/7/7.
 */
public class CheckNodeService implements Runnable {
    private FileServer fileServer;
    public CheckNodeService(FileServer fileServer) {
        this.fileServer=fileServer;
    }

    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(10000);
                ConcurrentHashMap<String, Integer> map = fileServer.getNode_statue();
                for (String k : map.keySet()) {
                    if(map.get(k)==1){
                        map.put(k,0);
                    } else if (map.get(k) == 0) {
                        boolean b = fileServer.getNode_info().remove(new StorageNode(k));
                        if (b)
                            System.out.println(k + " down");
                    }
                }//statue delete items

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
