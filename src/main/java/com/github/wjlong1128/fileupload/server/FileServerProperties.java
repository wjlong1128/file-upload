package com.github.wjlong1128.fileupload.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
@ConfigurationProperties(prefix = "file-server.bucket")
@Component
@Data
public class FileServerProperties {

    private List<Bucket> defaultTypeBuckets;

    private Boolean autoCreateBuckets = false;

    private String unknownBucket;

    @Data
    public static class Bucket {

        /**
         * bucketName
         */
        private String name;

        /**
         * 存放的文件类型，可以是 文件后缀，也可以是文件的content-type
         */
        private List<String> storeTypes;
    }
}
