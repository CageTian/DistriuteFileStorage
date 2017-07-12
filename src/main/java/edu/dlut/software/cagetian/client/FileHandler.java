package edu.dlut.software.cagetian.client;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by CageTian on 2017/7/6.
 */
public interface FileHandler {
    void upload(String file) throws IOException;
    void download(UUID uuid) throws IOException;
    void remove(UUID uuid);
}
