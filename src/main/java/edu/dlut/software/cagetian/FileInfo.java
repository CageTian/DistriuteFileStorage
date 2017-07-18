package edu.dlut.software.cagetian;
import edu.dlut.software.cagetian.storagenode.StorageNode;

import java.io.File;
import java.io.Serializable;


/**
 * Created by CageTian on 2017/7/6.
 * 该类用于存储服务器端所需的文件信息：uuid，文件原名称，主存节点名称，备份节点名称，所属用户名，所指向的文件（可选）
 */
public class FileInfo implements Serializable{
    private String file_id;
    private String file_name;
    private long file_size;
    private StorageNode main_node;
    private StorageNode sec_node;
    private File file;
    private String client_name;

    public FileInfo() {
    }

    public static FileInfo getServerInitInstance(String file_id, String file_name, String client_name, long file_size, StorageNode main_node, StorageNode sec_node) {
        FileInfo f = new FileInfo();
        f.file_id = file_id;
        f.file_name = file_name;
        f.file_size = file_size;
        f.main_node = main_node;
        f.sec_node = sec_node;
        f.client_name = client_name;
        return f;
    }

    /**
     * 节点初始化准备的静态工厂
     * 返回节点初始化的FileInfo对象
     *
     * @param file_id
     * @param client_name
     * @param file_size
     * @param file
     * @return
     */
    public static FileInfo getNodeInitInstance(String file_id, String client_name, long file_size, File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFile_id(file_id);
        fileInfo.setClient_name(client_name);
        fileInfo.setFile_size(file_size);
        fileInfo.setFile(file);
        return fileInfo;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public StorageNode getMain_node() {
        return main_node;
    }

    public StorageNode getSec_node() {
        return sec_node;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "file_id='" + file_id + '\'' +
                ", file_name='" + file_name + '\'' +
                ", file_size=" + file_size +
                ", main_node=" + main_node +
                ", sec_node=" + sec_node +
                ", file=" + file +
                ", client_name='" + client_name + '\'' +
                '}';
    }
}
