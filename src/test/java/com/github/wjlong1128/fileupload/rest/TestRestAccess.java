package com.github.wjlong1128.fileupload.rest;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/24
 * @desc
 */
public class TestRestAccess {

    @Test
    void get() throws IOException {
        RestTemplate template = new RestTemplate();
        ResponseEntity<byte[]> entity = template.getForEntity(
                "http://localhost:8888/file/simple/minio/6294b4834c214bdbfa2f397ee869fbf1.png",
                byte[].class
        );

        System.out.println(entity);
        IOUtils.copy(new ByteArrayInputStream(entity.getBody()), new FileOutputStream("1.txt"));
    }

    @Test
    void upload() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8888/file/simple/upload?serverType=MINIO";
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        String filePath = System.getProperty("user.dir") + File.separator + "1.txt";
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        params.add("file", fileSystemResource);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        System.out.println(result);
    }
}
