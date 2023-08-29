package com.github.wjlong1128.fileupload.config.minio.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc minio配置类
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String accessKey;

    private String secretKey;

    private String endpointUrl;


}
