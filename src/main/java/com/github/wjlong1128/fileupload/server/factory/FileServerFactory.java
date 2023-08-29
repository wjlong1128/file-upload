package com.github.wjlong1128.fileupload.server.factory;

import com.github.wjlong1128.fileupload.server.FileServer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
@Component
public class FileServerFactory {

    /**
     * key: serverType,
     * value: server
     */
    private final ConcurrentHashMap<String, FileServer> map = new ConcurrentHashMap<>();
    @Resource
    private List<FileServer> fileServers;


    @PostConstruct
    public void init() {
        if (!CollectionUtils.isEmpty(map)) {
            return;
        }
        for (FileServer fileServer : this.fileServers) {
            map.put(fileServer.getServerType().toUpperCase(), fileServer);
        }
    }

    public FileServer getFileServer(String serverType) {
        FileServer server = this.map.get(serverType.toUpperCase());
        if (server != null) {
            return server;
        }
        throw new NullPointerException("serverType:" + serverType + " 对应的fileServer不存在");
    }

}
