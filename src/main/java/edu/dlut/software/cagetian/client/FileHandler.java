package edu.dlut.software.cagetian.client;

/**
 * Created by CageTian on 2017/7/6.
 */
public interface FileHandler {
    void upload(String file_path) throws Exception;

    void download(String uuid, String file_path) throws Exception;

    void remove(String uuid) throws Exception;
}
