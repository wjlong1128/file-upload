package com.github.wjlong1128.fileupload.service;

import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.server.impl.MinIOUploadFileServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
@SpringBootTest
public class TestMinioFileService {

    @Resource
    MinIOUploadFileServer minIOUploadFileServer;

    @Test
    void getObject() throws FileServerException {
        byte[] bytes = this.minIOUploadFileServer.getObject("videos", "5/d/b/5db5b427587e2bf4f0a3d190c1c13e67.mkv");
        System.out.println(DigestUtils.md5DigestAsHex(bytes));
    }

}
