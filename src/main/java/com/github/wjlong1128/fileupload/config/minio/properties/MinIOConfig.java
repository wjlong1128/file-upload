package com.github.wjlong1128.fileupload.config.minio.properties;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc minio 配置类
 */
@EnableConfigurationProperties(MinioProperties.class)
@Configuration
public class MinIOConfig {

    private static Logger log = LoggerFactory.getLogger(MinIOConfig.class);

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        log.info("properties:{}", properties);
        return MinioClient
                .builder()
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .endpoint(properties.getEndpointUrl())
                .build();
    }


}
