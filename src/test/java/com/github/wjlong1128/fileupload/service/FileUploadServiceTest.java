package com.github.wjlong1128.fileupload.service;

import com.github.wjlong1128.fileupload.domain.bo.DownloadBO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.server.FileServer;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
@SpringBootTest
public class FileUploadServiceTest {

    @Resource
    private FileUploadService fileUploadService;

    @Resource
    private FileServer fileServer;

    @Test
    void testUpload() throws Exception {
        try (FileInputStream stream = new FileInputStream("D:\\file\\images\\200f28c125e8f54fb9b9893fc52d114273d93e16.jpg")) {
            this.fileUploadService
                    .upload("1.jpg", MimeTypeUtils.getMimeWithSuffix("1.jpg"), IOUtils.toByteArray(stream));
        }
    }

    @Test
    void testGet() throws FileServerException, IOException {
        String file = "9/0/8/90819dde4a421d8d7179dc20f1c23100.mp4";
        byte[] videos = this.fileServer
                .getObject("videos", file);
//        System.out.println(Arrays.toString(videos));
        System.out.println(DigestUtils.md5DigestAsHex(videos).length());
    }

//    @Test
/*    void download() {
        this.fileUploadService.downLoad("5db5b427587e2bf4f0a3d190c1c13e67.mkv");
    }*/

    @Test
    void test3FileMd5() throws IOException {
        String file1 = "D:/download/f953142d_0b47_48a4_bbe7_0f249dbd064f.png";
        String file2 = "D:/file/images/wallhaven-n6d81l.png";
        FileInputStream s1 = new FileInputStream(file1);
        FileInputStream s2 = new FileInputStream(file2);
        DownloadBO downLoad = this.fileUploadService.downLoad("98a9e39ad69e42cc2b30f051761af87c.png");
        ByteArrayInputStream s3 = new ByteArrayInputStream(downLoad.getContent());

        String h1 = DigestUtils.md5DigestAsHex(s1);
        String h2 = DigestUtils.md5DigestAsHex(s2);
        String h3 = DigestUtils.md5DigestAsHex(s3);
        System.out.println(h1);
        System.out.println(h2);
        System.out.println(h3);
        System.out.println(h1.equals(h2) && h1.equals(h3));
        System.out.println(new Tika().detect(downLoad.getContent()));
    }
}
