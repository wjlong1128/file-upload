package com.github.wjlong1128.fileupload.config.minio.properties;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MinIOConfig {


    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient
                .builder()
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .endpoint(properties.getEndpointUrl())
                .build();
    }


}
