package edu.dlut.software.cagetian;
import edu.dlut.software.cagetian.storagenode.StorageNode;

import java.io.File;
import java.io.Serializable;


/**
 * Created by CageTian on 2017/7/6.
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

    public FileInfo(String file_id, String file_name, long file_size, StorageNode main_node, StorageNode sec_node) {
        this.file_id = file_id;
        this.file_name = file_name;
        this.file_size = file_size;
        this.main_node = main_node;
        this.sec_node = sec_node;
    }

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

    public void setFile_name(String file_name) {
        this.file_name = file_name;
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

    public void setMain_node(StorageNode main_node) {
        this.main_node = main_node;
    }

    public StorageNode getSec_node() {
        return sec_node;
    }

    public void setSec_node(StorageNode sec_node) {
        this.sec_node = sec_node;
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
